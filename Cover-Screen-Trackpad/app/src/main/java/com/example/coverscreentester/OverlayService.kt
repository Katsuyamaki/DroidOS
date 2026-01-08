package com.example.coverscreentester

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.ServiceInfo
import androidx.core.content.ContextCompat
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Process
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Display
import android.view.GestureDetector
import android.view.Gravity
import android.view.InputDevice
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import android.os.PowerManager
import java.util.ArrayList
import com.example.coverscreentester.BuildConfig

class OverlayService : AccessibilityService(), DisplayManager.DisplayListener {

    // === RECEIVER & ACTIONS - START ===
    private val commandReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            
            // Helper to match both OLD and NEW package names
            fun matches(cmd: String): Boolean {
                return action.endsWith(cmd)
            }

            if (matches("SOFT_RESTART")) {
                Log.d("OverlayService", "Received SOFT_RESTART")
                performSoftRestart()
            } else if (matches("ENFORCE_ZORDER")) {
                Log.d("OverlayService", "Received ENFORCE_ZORDER")
                enforceZOrder()
            } else if (matches("MOVE_TO_DISPLAY")) {
                val targetId = intent.getIntExtra("displayId", 0)
                Log.d("OverlayService", "Moving to Display: $targetId")
                handler.post {
                    removeOldViews()
                    setupUI(targetId)
                    enforceZOrder()
                }
            } else if (matches("TOGGLE_MIRROR") || matches("TOGGLE_VIRTUAL_MIRROR")) {
                Log.d("OverlayService", "Toggling Mirror Mode")
                handler.post { toggleVirtualMirrorMode() }
            } else if (matches("OPEN_DRAWER")) {
                Log.d("OverlayService", "Opening Drawer")
                handler.post { toggleDrawer() }
            } else if (matches("STOP_SERVICE")) {
                Log.d("OverlayService", "Stopping Service")
                forceExit()
            }
        }
    }

    private fun performSoftRestart() {
        // HARD RESTART: Kill the process to force a full WindowManager reset.
        // This is the only way to fix persistent Z-order issues against the Launcher.
        handler.post {
            Toast.makeText(this, "Restarting Trackpad...", Toast.LENGTH_SHORT).show()
            handler.postDelayed({
                // This will kill the process. The system should restart it (START_STICKY).
                forceExit()
            }, 300)
        }
    }


    fun enforceZOrder() {
        try {
            if (windowManager != null) {
                // LAYER 1: TRACKPAD (Lowest)
                // We update layout but do NOT remove/add, keeping it at the bottom of the stack
                if (trackpadLayout != null) {
                    try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch(e: Exception) {}
                }

                // LAYER 2: KEYBOARD
                // Managed independently. If visible, it sits above Trackpad.
                // We don't touch it here; we just stack everything else ON TOP of it.

                // LAYER 3: MENU
                // Bring Menu to front (puts it above Keyboard)
                if (menuManager != null) {
                    try { menuManager?.bringToFront() } catch(e: Exception) {}
                }

                // LAYER 4: BUBBLE
                // Explicitly Remove and Add to force it ABOVE Keyboard and Menu
                if (bubbleView != null && bubbleView?.isAttachedToWindow == true) {
                    try {
                        windowManager?.removeView(bubbleView)
                        windowManager?.addView(bubbleView, bubbleParams)
                    } catch (e: Exception) {
                        // Ignore errors if view state is weird
                    }
                } else if (bubbleView != null) {
                    try { windowManager?.addView(bubbleView, bubbleParams) } catch(e: Exception) {}
                }

                // LAYER 5: CURSOR (Highest)
                // Explicitly Remove and Add to force it ABOVE everything
                if (cursorLayout != null && cursorLayout?.isAttachedToWindow == true) {
                    try {
                        windowManager?.removeView(cursorLayout)
                        windowManager?.addView(cursorLayout, cursorParams)
                    } catch (e: Exception) {
                        try { windowManager?.addView(cursorLayout, cursorParams) } catch(z: Exception) {}
                    }
                } else if (cursorLayout != null) {
                    try { windowManager?.addView(cursorLayout, cursorParams) } catch(e: Exception) {}
                }
                
                Log.d("OverlayService", "Z-Order Enforced (T < K < M < B < C)")
            }
        } catch (e: Exception) {
            Log.e("OverlayService", "Z-Order failed", e)
        }
    }

    // === RECEIVER & ACTIONS - END ===

    private val TAG = "OverlayService"

    var windowManager: WindowManager? = null
    var displayManager: DisplayManager? = null
    var shellService: IShellService? = null
    private var appWindowManager: WindowManager? = null
    private var isBound = false
    private val handler = Handler(Looper.getMainLooper())

    // Create a single worker queue for all input events to prevent race conditions
    private val inputExecutor = java.util.concurrent.Executors.newSingleThreadExecutor()

    private var lastBlockTime: Long = 0

    // =================================================================================
    // VIRTUAL DISPLAY KEEP-ALIVE SYSTEM
    // SUMMARY: Prevents system from timing out the display when using trackpad on a
    //          remote/virtual display. The overlay consumes touch events so the system
    //          doesn't see "real" user activity. We solve this by:
    //          1. Holding a SCREEN_BRIGHT_WAKE_LOCK when targeting remote display
    //          2. Periodically calling userActivity() via shell during active touch
    // =================================================================================
    private var powerManager: PowerManager? = null
    private var displayWakeLock: PowerManager.WakeLock? = null
    private var lastUserActivityPing: Long = 0
    private val USER_ACTIVITY_PING_INTERVAL_MS = 30_000L // Ping every 30 seconds during active use
    // =================================================================================
    // END BLOCK: VIRTUAL DISPLAY KEEP-ALIVE SYSTEM VARIABLES
    // =================================================================================

    private var bubbleView: View? = null
    private var trackpadLayout: FrameLayout? = null
    private var cursorLayout: FrameLayout? = null
    private var cursorView: ImageView? = null
    
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var trackpadParams: WindowManager.LayoutParams
    private lateinit var cursorParams: WindowManager.LayoutParams

    private var menuManager: TrackpadMenuManager? = null
    private var savedKbX = 0
    private var savedKbY = 0
    private var savedKbW = 0
    private var savedKbH = 0
    private var keyboardOverlay: KeyboardOverlay? = null

    var currentDisplayId = 0
    var inputTargetDisplayId = 0
    var isTrackpadVisible = true
    var isCustomKeyboardVisible = false
    var isScreenOff = false
    private var isPreviewMode = false
    private var previousImeId: String? = null
    
    // --- SMART RESTORE STATE ---
    private var pendingRestoreTrackpad = false
    private var pendingRestoreKeyboard = false
    private var hasPendingRestore = false

    // =================================================================================
    // VIRTUAL MIRROR MODE STATE
    // SUMMARY: Tracks component visibility before entering mirror mode so we can
    //          restore to previous state when exiting. Also stores the display ID
    //          we were targeting before mirror mode was enabled.
    // =================================================================================
    private var preMirrorTrackpadVisible = false
    private var preMirrorKeyboardVisible = false
    private var preMirrorTargetDisplayId = 0
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE STATE
    // =================================================================================

    private var isVoiceActive = false
    
    
    
    // Heartbeat to keep hardware state alive AND enforce settings
    private val blockingHeartbeat = object : Runnable {
        override fun run() {
            // No-op: Null Keyboard handles blocking natively
        }
    }
    
    class Prefs {
        var cursorSpeed = 2.5f
        var scrollSpeed = 1.0f 
        var prefTapScroll = true 
        var prefVibrate = false
        var prefReverseScroll = true
        var prefAlpha = 200
        var prefBgAlpha = 0
        var prefKeyboardAlpha = 200
        var prefHandleSize = 60 
        var prefVPosLeft = false
        var prefHPosTop = false
        var prefLocked = false
        var prefHandleTouchSize = 80
        var prefScrollTouchSize = 80 
        var prefScrollVisualSize = 4
        var prefCursorSize = 50 
        var prefKeyScale = 69 // Default to 69% to match Reset Position (0.55 ratio)
        var prefUseAltScreenOff = true
        var prefAutomationEnabled = true
        var prefBubbleX = 50
        var prefBubbleY = 300
        var prefAnchored = false 
        var prefBubbleSize = 100        
        var prefBubbleIconIndex = 0     
        var prefBubbleAlpha = 255       
        var prefPersistentService = false 
        var prefBlockSoftKeyboard = false
        
        // Defaults set to "none" (System Default)
        var hardkeyVolUpTap = "none"
        var hardkeyVolUpDouble = "none"
        var hardkeyVolUpHold = "none"
        var hardkeyVolDownTap = "none"
        var hardkeyVolDownDouble = "none"
        var hardkeyVolDownHold = "none"
        var hardkeyPowerDouble = "none"
        var doubleTapMs = 300
        var holdDurationMs = 400
        var displayOffMode = "alternate"

        // =================================================================================
        // VIRTUAL MIRROR MODE PREFERENCES
        // SUMMARY: Settings for displaying a mirror keyboard on remote/AR display.
        //          When enabled, touching the physical keyboard shows an orange orientation
        //          trail on both displays. After finger stops for orientDelayMs, normal
        //          keyboard input resumes.
        // =================================================================================
        var prefVirtualMirrorMode = false
        var prefMirrorOrientDelayMs = 1000L  // Default 1 second orientation delay
        
        // Mirror Keyboard Prefs
        var prefMirrorAlpha = 200
        var prefMirrorX = -1      // -1 = auto center
        var prefMirrorY = 0
        var prefMirrorWidth = -1  // -1 = auto
        var prefMirrorHeight = -1 // -1 = auto
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR MODE PREFERENCES
        // =================================================================================
    }
    val prefs = Prefs()

    // =========================
    // KEY INJECTION
    // =========================
    private fun injectKey(keyCode: Int, action: Int = KeyEvent.ACTION_DOWN, metaState: Int = 0) {
        // Dynamic Device ID:
        // Blocking ON: Use 1 (Physical) to maintain "Hardware Keyboard" state.
        // Blocking OFF: Use -1 (Virtual). ID 0 is often ignored by Gboard. -1 is standard software injection.
        val deviceId = if (prefs.prefBlockSoftKeyboard) 1 else -1
        shellService?.injectKey(keyCode, action, metaState, inputTargetDisplayId, deviceId)
    }

    // =========================
    // BLOCKING TRIGGER (Global)
    // =========================

// FILE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
// LOC: Around line 1550 (Search for 'fun triggerAggressiveBlocking')

// =================================================================================
// FUNCTION: triggerAggressiveBlocking
// SUMMARY: Enforces Soft Keyboard blocking.
// CHANGES: Commented out 'enforceZOrder()' to stop the UI Lag Loop. 
//          We now rely purely on 'softKeyboardController.showMode' which is faster.
// =================================================================================
    private fun triggerAggressiveBlocking() {
        // [PERFORMANCE FIX] 
        // Previously, this called 'enforceZOrder()' which removed/re-added views.
        // This caused massive lag/stutter on the Cover Screen.
        // Since we are already setting SHOW_MODE_HIDDEN below, the manual Z-order 
        // shuffle is unnecessary and harmful.
        
        // enforceZOrder() // <--- DISABLED TO FIX LAG

        // Rely on standard Android API to suppress keyboard
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                if (softKeyboardController.showMode != AccessibilityService.SHOW_MODE_HIDDEN) {
                    softKeyboardController.showMode = AccessibilityService.SHOW_MODE_HIDDEN
                }
            } catch (e: Exception) {
                // Controller might not be connected yet
            }
        }
    }
// =================================================================================
// END FUNCTION: triggerAggressiveBlocking
// =================================================================================


    private fun setSoftKeyboardBlocking(enabled: Boolean) {
        if (shellService == null) {
            showToast("Shizuku required for Keyboard Blocking")
            return
        }

        Thread {
            try {
                // 1. Find correct ID dynamically
                val listOutput = shellService?.runCommand("ime list -a -s") ?: ""
                val myImeId = listOutput.lines().firstOrNull { 
                    it.contains(packageName) && it.contains("NullInputMethodService") 
                }?.trim()

                if (myImeId.isNullOrEmpty()) {
                    handler.post { showToast("Error: Null Keyboard not found. Please re-install.") }
                    return@Thread
                }

                if (enabled) {
                    // --- BLOCKING (Switch to Null) ---
                    
                    // Save current (if not us)
                    val current = shellService?.runCommand("settings get secure default_input_method")?.trim() ?: ""
                    if (current.isNotEmpty() && !current.contains("NullInputMethodService")) {
                        previousImeId = current
                    }

                    // Attempt Shell Switch
                    shellService?.runCommand("ime enable $myImeId")
                    shellService?.runCommand("ime set $myImeId")
                    shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 0")

                    // Verify & Fallback
                    Thread.sleep(500)
                    val newDefault = shellService?.runCommand("settings get secure default_input_method")?.trim() ?: ""
                    
                    if (newDefault.contains("NullInputMethodService")) {
                        handler.post { showToast("Blocked: Null Keyboard Active") }
                    } else {
                        // FALLBACK: Open Picker
                        handler.post { 
                            showToast("Select 'DroidOS Null Keyboard'")
                            try {
                                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                                imm.showInputMethodPicker()
                            } catch(e: Exception){}
                        }
                    }
                    
                } else {
                    // --- UNBLOCKING (Restore Gboard) ---
                    
                    shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1")
                    
                    // Determine Target
                    var targetIme = previousImeId
                    if (targetIme.isNullOrEmpty()) {
                        val list = shellService?.runCommand("ime list -s") ?: ""
                        targetIme = list.lines().firstOrNull { it.contains("com.google.android.inputmethod.latin") } 
                            ?: list.lines().firstOrNull { it.contains("com.sec.android.inputmethod") }
                            ?: list.lines().firstOrNull { it.contains("honeyboard") }
                    }

                    if (!targetIme.isNullOrEmpty()) {
                        // Try Broadcast first (Clean Handoff)
                        val intent = Intent("com.example.coverscreentester.RESTORE_IME")
                        intent.setPackage(packageName)
                        intent.putExtra("target_ime", targetIme)
                        sendBroadcast(intent)
                        
                        // Blast Shell too just in case
                        shellService?.runCommand("ime enable $targetIme")
                        shellService?.runCommand("ime set $targetIme")
                        
                        handler.post { showToast("Restored: $targetIme") }
                    } else {
                        handler.post { 
                            showToast("Select your normal keyboard")
                            try {
                                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                                imm.showInputMethodPicker()
                            } catch(e: Exception){}
                        }
                    }
                }
            } catch (e: Exception) {
                handler.post { showToast("Error: ${e.message}") }
            }
        }.start()
    }


// FUNCTION: onAccessibilityEvent
// SUMMARY: Monitors system events.
// CHANGES: Added logic to FORCE UNBLOCK the keyboard when an event is detected on
//          the Main Screen (Display 0), provided the Trackpad is not currently running there.
// =================================================================================
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // [FIX 1] Keyboard Restoration Logic
        // If we detect activity on the Main Screen (Display 0), and our Trackpad is
        // NOT currently set to the Main Screen, we must ensure the keyboard is UNBLOCKED.
        if (Build.VERSION.SDK_INT >= 30) {
            val isMainScreenEvent = (event.displayId == Display.DEFAULT_DISPLAY)
            // 'currentDisplayId' tracks where our Trackpad UI is currently placed
            val isTrackpadOnMain = (currentDisplayId == Display.DEFAULT_DISPLAY)

            if (isMainScreenEvent && !isTrackpadOnMain) {
                // We are on Main Screen, but Trackpad is on Cover (or elsewhere).
                // Ensure we don't accidentally leave the keyboard blocked.
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        if (softKeyboardController.showMode == AccessibilityService.SHOW_MODE_HIDDEN) {
                            softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO
                        }
                    } catch (e: Exception) {
                        // Ignore controller errors
                    }
                }
                // Stop processing to prevent Lag Loop on Main Screen
                return
            }

            // [FIX 2] Standard Multi-Display Filter
            // Ignore events from displays we aren't managing
            if (event.displayId != currentDisplayId) {
                return
            }
        }

        val eventPkg = event.packageName?.toString() ?: ""

        // [FIX 3] Anti-Loop (Ignore Self)
        if (eventPkg == packageName) return

        // [FIX 4] Allow Voice Input
        if (eventPkg.contains("google.android.googlequicksearchbox") ||
            eventPkg.contains("com.google.android.voicesearch") ||
            eventPkg.contains("com.google.android.tts")) {

            if (prefs.prefBlockSoftKeyboard && shellService != null) {
                 Thread { try { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") } catch(e: Exception){} }.start()
            }
            return
        }

        // [FIX 5] Throttle & Execute Blocking
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {

            if (prefs.prefBlockSoftKeyboard && !isVoiceActive) {
                 val currentTime = System.currentTimeMillis()

                 // Throttle: Only run max once every 500ms
                 if (currentTime - lastBlockTime > 500) {
                     lastBlockTime = currentTime

                     triggerAggressiveBlocking()

                     // Enforce Hidden Mode
                     if (Build.VERSION.SDK_INT >= 24) {
                         try {
                             if (softKeyboardController.showMode != AccessibilityService.SHOW_MODE_HIDDEN) {
                                 softKeyboardController.showMode = AccessibilityService.SHOW_MODE_HIDDEN
                             }
                         } catch(e: Exception) {}
                         }
                 }
            }
        }
    }
// =================================================================================
// END FUNCTION: onAccessibilityEvent
// =================================================================================


    // =========================
    // STANDARD OVERRIDES
    // =========================
    private var uiScreenWidth = 1080
    private var uiScreenHeight = 2640
    private var targetScreenWidth = 1920
    private var targetScreenHeight = 1080
    private var cursorX = 300f
    private var cursorY = 300f
    private var rotationAngle = 0
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isTouchDragging = false
    private var isLeftKeyHeld = false
    private var isRightKeyHeld = false
    private var isVScrolling = false
    private var isHScrolling = false
    private var dragDownTime: Long = 0L
    private var hasSentTouchDown = false
    private var hasSentMouseDown = false
    private var activeFingerDeviceId = -1
    private var ignoreTouchSequence = false
    private var isDebugMode = false
    private var isKeyboardMode = false
    private var savedWindowX = 0
    private var savedWindowY = 0
    private var currentBorderColor = 0xFFFFFFFF.toInt()
    private var scrollAccumulatorX = 0f
    private var scrollAccumulatorY = 0f

    private var touchDownTime: Long = 0L
    private var touchDownX: Float = 0f
    private var touchDownY: Float = 0f
    private var isReleaseDebouncing = false
    private val releaseDebounceRunnable = Runnable { isReleaseDebouncing = false }
    
    private val TAP_TIMEOUT_MS = 300L
    private val TAP_SLOP_PX = 15f
    private val RELEASE_DEBOUNCE_MS = 50L

    private var scrollZoneThickness = 80
    private val handleContainers = ArrayList<FrameLayout>()
    private val handleVisuals = ArrayList<View>()
    private var vScrollContainer: FrameLayout? = null
    private var hScrollContainer: FrameLayout? = null
    private var vScrollVisual: View? = null
    private var hScrollVisual: View? = null
    private var debugTextView: TextView? = null

    private var remoteWindowManager: WindowManager? = null
    private var remoteCursorLayout: FrameLayout? = null
    private var remoteCursorView: ImageView? = null
    private lateinit var remoteCursorParams: WindowManager.LayoutParams

    // =================================================================================
    // VIRTUAL MIRROR MODE VARIABLES
    // =================================================================================
    private var mirrorWindowManager: WindowManager? = null
    private var mirrorKeyboardContainer: FrameLayout? = null
    private var mirrorKeyboardView: KeyboardView? = null
    private var mirrorTrailView: SwipeTrailView? = null
    private var mirrorKeyboardParams: WindowManager.LayoutParams? = null

    // Dimensions for coordinate scaling between physical and mirror keyboards
    private var physicalKbWidth = 0f
    private var physicalKbHeight = 0f
    private var mirrorKbWidth = 0f
    private var mirrorKbHeight = 0f

    private var isInOrientationMode = false
    private var isMirrorDragActive = false
    private var isHoveringBackspace = false // [FIX] Add this variable
    private var draggedPredictionIndex = -1 // [FIX] Add this missing variable
    private var lastOrientX = 0f
    private var lastOrientY = 0f
    private val MOVEMENT_THRESHOLD = 15f  // Pixels - ignore movement smaller than this
    private var orientationModeHandler = Handler(Looper.getMainLooper())
    private var lastOrientationTouchTime = 0L

    // =================================================================================
    // MIRROR MODE KEY REPEAT VARIABLES
    // SUMMARY: Variables for repeating backspace/arrow keys when held during orange
    //          trail orientation mode. Only active in mirror mode, doesn't affect
    //          normal blue trail swipe typing.
    // =================================================================================
    private val mirrorRepeatHandler = Handler(Looper.getMainLooper())
    private var mirrorRepeatKey: String? = null
    private var isMirrorRepeating = false
    private val MIRROR_REPEAT_INITIAL_DELAY = 400L
    private val MIRROR_REPEAT_INTERVAL = 50L
    
    // Keys that can repeat in mirror orientation mode (backspace + arrows)
    private val mirrorRepeatableKeys = setOf("BKSP", "⌫", "←", "→", "↑", "↓", "◄", "▲", "▼", "►")
    

    private val mirrorRepeatRunnable = object : Runnable {
        override fun run() {
            mirrorRepeatKey?.let { key ->
                // FIXED: Removed '&& isInOrientationMode' check.
                // This allows repeat to work in Blue Mode (where isInOrientationMode is false).
                if (isMirrorRepeating) {
                    Log.d(TAG, "Mirror repeat: $key")
                    keyboardOverlay?.triggerKeyPress(key)
                    mirrorRepeatHandler.postDelayed(this, MIRROR_REPEAT_INTERVAL)
                }
            }
        }
    }

    // =================================================================================
    // END BLOCK: MIRROR MODE KEY REPEAT VARIABLES
    // =================================================================================    // =================================================================================
    // RUNNABLE: orientationModeTimeout
    // SUMMARY: Fires when finger has been still for delay period.
    //          Switches from orange trail to blue trail.
    //          Initializes swipe tracking so path collection starts NOW.
    // =================================================================================




    private val orientationModeTimeout = Runnable {
        // DYNAMIC HEIGHT CALCULATION
        val currentHeight = if (physicalKbHeight > 0) physicalKbHeight else 400f
        
        // ADJUSTMENT: Set to 19% (0.19f)
        // 0.23f blocked the top row. 0.17f caused ghost words. 0.19f is the sweet spot.
        val stripHeight = currentHeight * 0.12f

        // Check if finger is currently holding on the Prediction Bar
        if (lastOrientY < stripHeight) {

             // START DRAG MODE (Blue Trail)
             isInOrientationMode = false
             keyboardOverlay?.setOrientationMode(false)
             mirrorTrailView?.clear()
             keyboardOverlay?.clearOrientationTrail()
             
             isMirrorDragActive = true
             
             // Calculate index
             val width = if (physicalKbWidth > 0) physicalKbWidth else 1080f
             val slotWidth = width / 3f
             draggedPredictionIndex = (lastOrientX / slotWidth).toInt().coerceIn(0, 2)

             // START BLUE TRAIL IMMEDIATELY
             mirrorTrailView?.setTrailColor(0xFF4488FF.toInt())
             mirrorTrailView?.addPoint(lastOrientX, lastOrientY)
             
             val now = SystemClock.uptimeMillis()
             val scaleX = if (physicalKbWidth > 0) mirrorKbWidth / physicalKbWidth else 1f
             val scaleY = if (physicalKbHeight > 0) mirrorKbHeight / physicalKbHeight else 1f
             val mx = lastOrientX * scaleX
             val my = lastOrientY * scaleY
             
             val event = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, mx, my, 0)
             mirrorKeyboardView?.dispatchTouchEvent(event)
             event.recycle()
             
             return@Runnable
        }

        // CASE B: Holding on Keys -> START BLUE TRAIL
        isInOrientationMode = false
        mirrorTrailView?.clear()
        keyboardOverlay?.clearOrientationTrail()
        keyboardOverlay?.setOrientationMode(false)
        mirrorTrailView?.setTrailColor(0xFF4488FF.toInt()) 
        keyboardOverlay?.startSwipeFromCurrentPosition(lastOrientX, lastOrientY)

        val scaleX = if (physicalKbWidth > 0) mirrorKbWidth / physicalKbWidth else 1f
        val scaleY = if (physicalKbHeight > 0) mirrorKbHeight / physicalKbHeight else 1f
        mirrorTrailView?.addPoint(lastOrientX * scaleX, lastOrientY * scaleY)
        mirrorKeyboardView?.alpha = 0.7f

        // Immediate Repeat Check
        val currentKey = keyboardOverlay?.getKeyAtPosition(lastOrientX, lastOrientY)
        if (currentKey != null && mirrorRepeatableKeys.contains(currentKey)) {
            mirrorRepeatKey = currentKey
            isMirrorRepeating = true
            keyboardOverlay?.triggerKeyPress(currentKey)
            mirrorRepeatHandler.postDelayed(mirrorRepeatRunnable, MIRROR_REPEAT_INTERVAL)
        }
    }



    // =================================================================================
    // END BLOCK: orientationModeTimeout
    // =================================================================================

    // =================================================================================
    // MIRROR FADE OUT HANDLER
    // SUMMARY: Fades the mirror keyboard to fully transparent after inactivity.
    // =================================================================================
    private val mirrorFadeHandler = Handler(Looper.getMainLooper())
    private val mirrorFadeRunnable = Runnable {
        mirrorKeyboardView?.animate()?.alpha(0f)?.setDuration(300)?.start()
        mirrorKeyboardContainer?.animate()?.alpha(0f)?.setDuration(300)?.start()
    }
    // =================================================================================
    // END BLOCK: MIRROR FADE OUT HANDLER
    // =================================================================================
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE VARIABLES
    // =================================================================================

    private val longPressRunnable = Runnable { startTouchDrag() }
    private val clearHighlightsRunnable = Runnable { updateBorderColor(currentBorderColor); updateLayoutSizes() }
    
    private var isKeyDragging = false
    private var activeDragButton = MotionEvent.BUTTON_PRIMARY
    
    private var lastVolUpTime: Long = 0L
    private var lastVolDownTime: Long = 0L
    // HARDKEY STATE TRACKING
    private var volUpTapCount = 0
    private var volDownTapCount = 0
    private var volUpHoldTriggered = false
    private var volDownHoldTriggered = false
    private var volUpDragActive = false
    private var volDownDragActive = false
    private var lastManualSwitchTime: Long = 0L

    private val volUpHoldRunnable = Runnable {
        volUpHoldTriggered = true
        executeHardkeyAction(prefs.hardkeyVolUpHold, KeyEvent.ACTION_DOWN)
    }

    private val volDownHoldRunnable = Runnable {
        volDownHoldTriggered = true
        executeHardkeyAction(prefs.hardkeyVolDownHold, KeyEvent.ACTION_DOWN)
    }

    private val volUpDoubleTapRunnable = Runnable {
        if (volUpTapCount == 1) {
            executeHardkeyAction(prefs.hardkeyVolUpTap, KeyEvent.ACTION_UP)
        }
        volUpTapCount = 0
    }

    private val volDownDoubleTapRunnable = Runnable {
        if (volDownTapCount == 1) {
            executeHardkeyAction(prefs.hardkeyVolDownTap, KeyEvent.ACTION_UP)
        }
        volDownTapCount = 0
    }

    // =================================================================================
    // SECTION: BroadcastReceiver & Window Focus Logic
    // SUMMARY: Updates VOICE_TYPE_TRIGGERED.
    //          REMOVED the logic that restored 'setOverlayFocusable(true)'.
    //          The overlay must remain NOT_FOCUSABLE so Termux retains input focus.
    // =================================================================================
    // =================================================================================
    // SECTION: BroadcastReceiver (Voice Trigger)
    // SUMMARY: Turns the indicator GREEN when Voice is triggered.
    //          Sets isVoiceActive = true immediately.
    // =================================================================================
    // =================================================================================
    // INTER-APP COMMAND RECEIVER
    // SUMMARY: BroadcastReceiver for ADB commands and inter-app communication.
    //          Allows DroidOS Launcher to control Trackpad without killing permissions.
    //          Commands can be sent via ADB: adb shell am broadcast -a <ACTION>
    // =================================================================================
    private val switchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            
            // Helper to match both old (example) and new (katsuyamaki) package actions
            fun matches(suffix: String): Boolean = action.endsWith(suffix)

            when {
                // Internal Actions (Exact Match)
                action == "SWITCH_DISPLAY" -> switchDisplay()
                action == "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                action == "RESET_CURSOR" -> resetCursorCenter()
                action == "TOGGLE_DEBUG" -> toggleDebugMode()
                action == "FORCE_KEYBOARD" || action == "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
                action == "OPEN_MENU" -> { menuManager?.show(); enforceZOrder() }
                action == "SET_TRACKPAD_VISIBILITY" -> {
                    val visible = intent.getBooleanExtra("VISIBLE", true)
                    val menuDisplayId = intent.getIntExtra("MENU_DISPLAY_ID", -1)
                    if (visible) setTrackpadVisibility(true) 
                    else { if (menuDisplayId == -1 || menuDisplayId == currentDisplayId) setTrackpadVisibility(false) }
                }
                action == "SET_PREVIEW_MODE" -> setPreviewMode(intent.getBooleanExtra("PREVIEW_MODE", false))
                action == "VOICE_TYPE_TRIGGERED" -> {
                    isVoiceActive = true
                    keyboardOverlay?.setVoiceActive(true)
                    setOverlayFocusable(false)
                    handler.postDelayed({ attemptRefocusInput() }, 300)
                }
                action == Intent.ACTION_SCREEN_ON -> {
                    // [FIX] Only trigger blocking if explicitly enabled in prefs.
                    // Previously, this ran unconditionally, causing the keyboard to vanish/block
                    // on the Beam Pro every time the screen turned on.
                    if (prefs.prefBlockSoftKeyboard) {
                        triggerAggressiveBlocking()
                    }
                }

                // Universal ADB/External Commands (Suffix Match)
                matches("SOFT_RESTART") -> {
                    Log.d(TAG, "Received SOFT_RESTART command")
                    val targetDisplayId = intent.getIntExtra("DISPLAY_ID", currentDisplayId)
                    handler.post {
                        removeOldViews()
                        handler.postDelayed({
                            setupUI(targetDisplayId)
                            enforceZOrder()
                            showToast("Trackpad Soft Restarted")
                        }, 200)
                    }
                }
                matches("MOVE_TO_VIRTUAL") -> {
                    Log.d(TAG, "Received MOVE_TO_VIRTUAL command")
                    val virtualDisplayId = intent.getIntExtra("DISPLAY_ID", 2)
                    handler.post { moveToVirtualDisplayAndEnableMirror(virtualDisplayId) }
                }
                matches("RETURN_TO_PHYSICAL") -> {
                    Log.d(TAG, "Received RETURN_TO_PHYSICAL command")
                    val physicalDisplayId = intent.getIntExtra("DISPLAY_ID", 0)
                    handler.post { returnToPhysicalDisplay(physicalDisplayId) }
                }
                matches("ENFORCE_ZORDER") -> {
                    Log.d(TAG, "Received ENFORCE_ZORDER command")
                    handler.post { enforceZOrder() }
                }
                matches("TOGGLE_VIRTUAL_MIRROR") -> {
                    Log.d(TAG, "Received TOGGLE_VIRTUAL_MIRROR command")
                    handler.post { toggleVirtualMirrorMode() }
                }
                matches("GET_STATUS") -> {
                    Log.d(TAG, "Received GET_STATUS command")
                    showToast("D:$currentDisplayId T:$inputTargetDisplayId M:${if(prefs.prefVirtualMirrorMode) "ON" else "OFF"}")
                }
            }
        }
    }

    // Helper to dynamically update window flags
    private fun setOverlayFocusable(focusable: Boolean) {
        try {
            keyboardOverlay?.setFocusable(focusable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // NEW FUNCTION: Finds the focused input field and performs a click
    private fun attemptRefocusInput() {
        try {
            // Requires canRetrieveWindowContent="true" in accessibility xml
            val root = rootInActiveWindow ?: return
            
            // Find the node that currently has input focus
            val focus = root.findFocus(android.view.accessibility.AccessibilityNodeInfo.FOCUS_INPUT)
            
            if (focus != null) {
                // Simulate a tap on the text box to refresh the InputConnection
                focus.performAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK)
                focus.recycle()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // =================================================================================
    // END BLOCK: BroadcastReceiver & Window Focus Logic
    // =================================================================================

    // =================================================================================
    // FUNCTION: checkAndDismissVoice
    // SUMMARY:  Dismisses Voice Input.
    //           FIX 1: Uses performGlobalAction(GLOBAL_ACTION_BACK) for reliable closing.
    //           FIX 2: Adds a small delay to ensure the action registers.
    // =================================================================================
    // =================================================================================
    // FUNCTION: checkAndDismissVoice
    // SUMMARY:  Called when user touches Trackpad/Keyboard.
    //           Turns the indicator OFF (Red) and Resets Flag.
    //           Injects BACK key to close Google Voice.
    // =================================================================================
    private fun checkAndDismissVoice() {
        if (isVoiceActive) {
            isVoiceActive = false 
            
            // IMMEDIATE UI UPDATE: Turn Mic Off
            keyboardOverlay?.setVoiceActive(false)
            
            // Standard Dismissal Logic (Back Button)
            Thread {
                try {
                    val success = performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                    if (!success) {
                        injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN)
                        Thread.sleep(50)
                        injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP)
                    }
                    if (prefs.prefBlockSoftKeyboard) {
                        triggerAggressiveBlocking()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    companion object {
        private const val TAG = "OverlayService"
        private const val BASE_SWIPE_DISTANCE = 200f
    }
    
    fun vibrate() { 
        if (!prefs.prefVibrate) return
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)) 
        else @Suppress("DEPRECATION") v.vibrate(50) 
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            shellService = IShellService.Stub.asInterface(binder)
            isBound = true
            updateBubbleStatus()
            showToast("Shizuku Connected") 
            initCustomKeyboard()
            
            // CRITICAL FIX: Only apply blocking if EXPLICITLY enabled. 
            // Do NOT reset to "1" here, as that unblocks the keyboard every time the app opens.
            if (prefs.prefBlockSoftKeyboard) {
                triggerAggressiveBlocking()
                handler.post(blockingHeartbeat)
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            shellService = null
            isBound = false
            updateBubbleStatus()
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Register BOTH new and old prefixes to support all scripts/buttons
        val commandFilter = IntentFilter().apply {
            val cmds = listOf("SOFT_RESTART", "ENFORCE_ZORDER", "MOVE_TO_DISPLAY", "TOGGLE_MIRROR", "TOGGLE_VIRTUAL_MIRROR", "OPEN_DRAWER", "STOP_SERVICE")
            val prefixes = listOf("com.katsuyamaki.DroidOSTrackpadKeyboard.", "com.example.coverscreentester.")
            
            for (p in prefixes) {
                for (c in cmds) {
                    addAction("$p$c")
                }
            }
        }
        // Export receiver so ADB and other apps can send these commands
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(commandReceiver, commandFilter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(commandReceiver, commandFilter)
        }
        try { displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; displayManager?.registerDisplayListener(this, handler) } catch (e: Exception) {}
        // =================================================================================
        // VIRTUAL DISPLAY KEEP-ALIVE: Initialize PowerManager
        // =================================================================================
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        // =================================================================================
        // END BLOCK: VIRTUAL DISPLAY KEEP-ALIVE PowerManager Init
        // =================================================================================

                    loadPrefs()
                    val filter = IntentFilter().apply { 
                        // Internal short commands
                        addAction("SWITCH_DISPLAY")
                        addAction("CYCLE_INPUT_TARGET")
                        addAction("RESET_CURSOR")
                        addAction("TOGGLE_DEBUG")
                        addAction("FORCE_KEYBOARD")
                        addAction("TOGGLE_CUSTOM_KEYBOARD")
                        addAction("SET_TRACKPAD_VISIBILITY")
                        addAction("SET_PREVIEW_MODE") 
                        addAction("OPEN_MENU")
                        addAction("VOICE_TYPE_TRIGGERED")
                        addAction(Intent.ACTION_SCREEN_ON)

                        // External commands (Old and New Prefixes)
                        val actions = listOf(
                            "SOFT_RESTART", "MOVE_TO_VIRTUAL", "RETURN_TO_PHYSICAL",
                            "ENFORCE_ZORDER", "TOGGLE_VIRTUAL_MIRROR", "GET_STATUS"
                        )
                        val prefixes = listOf(
                            "com.katsuyamaki.DroidOSTrackpadKeyboard.",
                            "com.example.coverscreentester."
                        )
                        for (prefix in prefixes) {
                            for (act in actions) {
                                addAction("$prefix$act")
                            }
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // For Android 13 (TIRAMISU) and above, receivers must explicitly specify exported state
                        ContextCompat.registerReceiver(this, switchReceiver, filter, Context.RECEIVER_EXPORTED)
                    } else {
                        ContextCompat.registerReceiver(this, switchReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
                    }
                    
                    if (Build.VERSION.SDK_INT >= 24) {
                        try { softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO } catch(e: Exception){}        }
    }


    // AccessibilityService entry point - called when user enables service in Settings
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility Service Connected")

        // === NEW CODE START: Initialize Dictionary ===
        PredictionEngine.instance.loadDictionary(this)
        // === NEW CODE END ===

        // Initialize WindowManager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try { createNotification() } catch(e: Exception){ e.printStackTrace() }

        // Re-check Shizuku binding on every start command to ensure connection is alive
        checkAndBindShizuku()

        try {
            // Handle display recall from MainActivity
            if (intent != null) {
                val targetDisplayId = intent.getIntExtra("displayId", currentDisplayId)
                val isRecall = intent.getBooleanExtra("isRecall", false)

                android.util.Log.d("OverlayService", "onStartCommand: target=$targetDisplayId, current=$currentDisplayId, recall=$isRecall")

                // Only setup UI if display changed OR if user explicitly tapped the icon (Recall)
                if (targetDisplayId != currentDisplayId || isRecall || bubbleView == null) {
                    setupUI(targetDisplayId)
                    return START_STICKY
                }
            }

            val action = intent?.action
            fun matches(suffix: String): Boolean = action?.endsWith(suffix) == true

            // Handle commands robustly
            if (action != null) {
                when {
                    // Standard Actions
                    action == "SWITCH_DISPLAY" -> switchDisplay()
                    action == "RESET_POSITION" -> {
                        val target = intent.getStringExtra("TARGET") ?: "TRACKPAD"
                        if (target == "KEYBOARD") keyboardOverlay?.resetPosition() else resetTrackpadPosition()
                    }
                    action == "ROTATE" -> {
                        val target = intent.getStringExtra("TARGET") ?: "TRACKPAD"
                        if (target == "KEYBOARD") keyboardOverlay?.cycleRotation() else performRotation()
                    }
                    action == "SAVE_LAYOUT" -> saveLayout()
                    action == "LOAD_LAYOUT" -> loadLayout()
                    action == "DELETE_PROFILE" -> deleteCurrentProfile()
                    action == "MANUAL_ADJUST" -> handleManualAdjust(intent)
                    action == "RELOAD_PREFS" -> {
                        loadPrefs()
                        updateBorderColor(currentBorderColor)
                        updateLayoutSizes()
                        updateScrollPosition()
                        updateCursorSize()
                        keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha)
                        if (isCustomKeyboardVisible) { toggleCustomKeyboard(); toggleCustomKeyboard() }
                    }
                    action == "PREVIEW_UPDATE" -> handlePreview(intent)
                    action == "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                    action == "RESET_CURSOR" -> resetCursorCenter()
                    action == "TOGGLE_DEBUG" -> toggleDebugMode()
                    action == "FORCE_KEYBOARD" || action == "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
                    action == "OPEN_MENU" -> menuManager?.show()

                    // ADB / Launcher Commands (Suffix Matching)
                    matches("SOFT_RESTART") -> {
                        Log.d(TAG, "onStartCommand: SOFT_RESTART")
                        val targetDisplayId = intent.getIntExtra("DISPLAY_ID", currentDisplayId)
                        handler.post {
                            removeOldViews()
                            handler.postDelayed({ setupUI(targetDisplayId); enforceZOrder() }, 200)
                        }
                    }
                    matches("MOVE_TO_VIRTUAL") -> {
                        val vid = intent.getIntExtra("DISPLAY_ID", 2)
                        handler.post { moveToVirtualDisplayAndEnableMirror(vid) }
                    }
                    matches("RETURN_TO_PHYSICAL") -> {
                        val pid = intent.getIntExtra("DISPLAY_ID", 0)
                        handler.post { returnToPhysicalDisplay(pid) }
                    }
                    matches("ENFORCE_ZORDER") -> handler.post { enforceZOrder() }
                    matches("TOGGLE_VIRTUAL_MIRROR") -> handler.post { toggleVirtualMirrorMode() }
                    matches("GET_STATUS") -> showToast("Status: D=$currentDisplayId M=${prefs.prefVirtualMirrorMode}")
                }
            }
            if (intent?.hasExtra("DISPLAY_ID") == true) {
                val targetId = intent.getIntExtra("DISPLAY_ID", Display.DEFAULT_DISPLAY)
                val force = intent.getBooleanExtra("FORCE_MOVE", false)
                if (targetId >= 0 && (targetId != currentDisplayId || force)) {
                    forceMoveToDisplay(targetId)
                }
            } else if (windowManager == null) {
                setupUI(Display.DEFAULT_DISPLAY)
            }
        } catch (e: Exception) { e.printStackTrace() }
        return START_STICKY
    }

    override fun onInterrupt() {}

    // Use dispatchKeyEvent to catch BOTH Down and Up events in one place
    override fun onKeyEvent(event: KeyEvent): Boolean {
        val isVolKey = event.keyCode == KeyEvent.KEYCODE_VOLUME_UP || event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
        
        if (isPreviewMode || (!isTrackpadVisible && !isVolKey)) {
            return super.onKeyEvent(event)
        }
        
        val action = event.action
        val keyCode = event.keyCode
        
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (prefs.hardkeyVolUpTap == "none" && prefs.hardkeyVolUpHold == "none") return super.onKeyEvent(event)
            
            when (action) {
                KeyEvent.ACTION_DOWN -> {
                    if (!isLeftKeyHeld) {
                        isLeftKeyHeld = true
                        volUpHoldTriggered = false
                        handler.postDelayed(volUpHoldRunnable, prefs.holdDurationMs.toLong())
                    }
                }
                KeyEvent.ACTION_UP -> {
                    isLeftKeyHeld = false
                    handler.removeCallbacks(volUpHoldRunnable)
                    if (volUpHoldTriggered) {
                        // RELEASE the hold action (Crucial for drag/click release)
                        executeHardkeyAction(prefs.hardkeyVolUpHold, KeyEvent.ACTION_UP)
                    } else {
                        val timeSinceLastTap = System.currentTimeMillis() - lastVolUpTime
                        lastVolUpTime = System.currentTimeMillis()
                        if (timeSinceLastTap < prefs.doubleTapMs && volUpTapCount == 1) {
                            handler.removeCallbacks(volUpDoubleTapRunnable)
                            volUpTapCount = 0
                            executeHardkeyAction(prefs.hardkeyVolUpDouble, KeyEvent.ACTION_UP)
                        } else {
                            volUpTapCount = 1
                            handler.removeCallbacks(volUpDoubleTapRunnable)
                            handler.postDelayed(volUpDoubleTapRunnable, prefs.doubleTapMs.toLong())
                        }
                    }
                }
            }
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (prefs.hardkeyVolDownTap == "none" && prefs.hardkeyVolDownHold == "none") return super.onKeyEvent(event)
            
            when (action) {
                KeyEvent.ACTION_DOWN -> {
                    if (!isRightKeyHeld) {
                        isRightKeyHeld = true
                        volDownHoldTriggered = false
                        handler.postDelayed(volDownHoldRunnable, prefs.holdDurationMs.toLong())
                    }
                }
                KeyEvent.ACTION_UP -> {
                    isRightKeyHeld = false
                    handler.removeCallbacks(volDownHoldRunnable)
                    if (volDownHoldTriggered) {
                        // RELEASE the hold action
                        executeHardkeyAction(prefs.hardkeyVolDownHold, KeyEvent.ACTION_UP)
                    } else {
                        val timeSinceLastTap = System.currentTimeMillis() - lastVolDownTime
                        lastVolDownTime = System.currentTimeMillis()
                        if (timeSinceLastTap < prefs.doubleTapMs && volDownTapCount == 1) {
                            handler.removeCallbacks(volDownDoubleTapRunnable)
                            volDownTapCount = 0
                            executeHardkeyAction(prefs.hardkeyVolDownDouble, KeyEvent.ACTION_UP)
                        } else {
                            volDownTapCount = 1
                            handler.removeCallbacks(volDownDoubleTapRunnable)
                            handler.postDelayed(volDownDoubleTapRunnable, prefs.doubleTapMs.toLong())
                        }
                    }
                }
            }
            return true
        }
        return super.onKeyEvent(event)
    }

    private fun removeOldViews() {
        val viewsToRemove = listOf(trackpadLayout, bubbleView, cursorLayout)
        for (view in viewsToRemove) {
            if (view != null && view.parent != null && windowManager != null) {
                try {
                    windowManager?.removeViewImmediate(view)
                    android.util.Log.d("OverlayService", "Successfully removed view: ${view.javaClass.simpleName}")
                } catch (e: Exception) {
                    android.util.Log.e("OverlayService", "Failed to remove view immediate", e)
                }
            }
        }
        // Clean up keyboard and menu
        try {
            keyboardOverlay?.hide()
            keyboardOverlay = null
            menuManager?.hide()
            menuManager = null
        } catch (e: Exception) {
            android.util.Log.e("OverlayService", "Failed to cleanup keyboard/menu", e)
        }
        // Nullify references to ensure setup functions create fresh instances
        trackpadLayout = null
        bubbleView = null
        cursorLayout = null
        cursorView = null
    }

    private fun setupUI(displayId: Int) {
        android.util.Log.d("OverlayService", "setupUI starting for Display $displayId")

        // 1. Force complete removal of all views using the current WindowManager
        removeOldViews()

        val display = displayManager?.getDisplay(displayId)
        if (display == null) {
            showToast("Error: Display $displayId not found")
            return
        }

        try {
            // 2. Create a new context strictly for the target physical display
            val displayContext = createDisplayContext(display)
            val accessContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 displayContext.createWindowContext(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null)
            } else displayContext

            // 3. RE-BIND WindowManager and Inflater to the new display context
            windowManager = accessContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            appWindowManager = windowManager

            currentDisplayId = displayId
            inputTargetDisplayId = displayId

            updateUiMetrics()

            // 4. Rebuild the UI components
            setupTrackpad(accessContext)
            if (shellService != null) initCustomKeyboard()
            menuManager = TrackpadMenuManager(displayContext, windowManager!!, this)
            setupBubble(accessContext)
            setupCursor(accessContext)

            enforceZOrder()
            showToast("Trackpad active on Display $displayId")

            if (prefs.prefBlockSoftKeyboard) triggerAggressiveBlocking()

            android.util.Log.d("OverlayService", "setupUI completed successfully on Display $displayId")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup UI on display $displayId", e)
            showToast("Failed to launch on display $displayId")
        }
    }

    private fun setupBubble(context: Context) {
        bubbleView = LayoutInflater.from(context).inflate(R.layout.layout_trackpad_bubble, null)
        bubbleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or 
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
             bubbleParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        bubbleParams.gravity = Gravity.TOP or Gravity.START
        bubbleParams.x = prefs.prefBubbleX
        bubbleParams.y = prefs.prefBubbleY
        
        var initialX = 0; var initialY = 0; var initialTouchX = 0f; var initialTouchY = 0f; var isDrag = false
        var isLongPressHandled = false
        val bubbleLongPressRunnable = Runnable {
            if (!isDrag) {
                vibrate()
                menuManager?.toggle()
                isLongPressHandled = true
                handler.post { enforceZOrder() }
            }
        }

        bubbleView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> { 
                    initialX = bubbleParams.x; initialY = bubbleParams.y
                    initialTouchX = event.rawX; initialTouchY = event.rawY
                    isDrag = false
                    isLongPressHandled = false
                    handler.postDelayed(bubbleLongPressRunnable, 600)
                    true 
                }
                MotionEvent.ACTION_MOVE -> { 
                    if (abs(event.rawX - initialTouchX) > 10 || abs(event.rawY - initialTouchY) > 10) { 
                        isDrag = true
                        handler.removeCallbacks(bubbleLongPressRunnable)
                        bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(bubbleView, bubbleParams) 
                    }
                    true 
                }
                MotionEvent.ACTION_UP -> { 
                    handler.removeCallbacks(bubbleLongPressRunnable)
                    if (!isDrag && !isLongPressHandled) {
                        handleBubbleTap()
                    } else if (isDrag) {
                        prefs.prefBubbleX = bubbleParams.x
                        prefs.prefBubbleY = bubbleParams.y
                        savePrefs()
                    }
                    handler.post { enforceZOrder() }
                    true 
                }
                else -> false
            }
        }
        windowManager?.addView(bubbleView, bubbleParams); updateBubbleStatus()
        applyBubbleAppearance()
    }
    
    private fun setupTrackpad(context: Context) {
        trackpadLayout = FrameLayout(context)
        val bg = GradientDrawable(); bg.cornerRadius = 30f; bg.setColor(Color.TRANSPARENT); trackpadLayout?.background = bg
        val handleColor = 0x15FFFFFF.toInt(); handleContainers.clear(); handleVisuals.clear()
        addHandle(context, Gravity.TOP or Gravity.RIGHT, handleColor) { _, e -> moveWindow(e) }
        addHandle(context, Gravity.BOTTOM or Gravity.RIGHT, handleColor) { _, e -> resizeWindow(e) }
        addHandle(context, Gravity.TOP or Gravity.LEFT, handleColor) { _, e -> keyboardHandle(e) }
        addHandle(context, Gravity.BOTTOM or Gravity.LEFT, handleColor) { _, e -> openMenuHandle(e) }
        addScrollBars(context)
        debugTextView = TextView(context); debugTextView?.text = "DEBUG"; debugTextView?.setTextColor(Color.YELLOW); debugTextView?.setBackgroundColor(0xAA000000.toInt()); debugTextView?.textSize = 9f; debugTextView?.visibility = View.GONE; val debugParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT); debugParams.gravity = Gravity.CENTER; trackpadLayout?.addView(debugTextView, debugParams)
        
        trackpadParams = WindowManager.LayoutParams(
            400, 300, 
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, 
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or 
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or 
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, 
            PixelFormat.TRANSLUCENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
             trackpadParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        trackpadParams.gravity = Gravity.TOP or Gravity.LEFT; loadLayout()
        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() { 
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean { return false }
        })
        
        trackpadLayout?.setOnTouchListener { _, event -> val devId = event.deviceId; val tool = event.getToolType(0); if (tool != MotionEvent.TOOL_TYPE_FINGER) return@setOnTouchListener false; when (event.actionMasked) { MotionEvent.ACTION_DOWN -> activeFingerDeviceId = devId; MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> { if (activeFingerDeviceId > 0 && devId != activeFingerDeviceId) return@setOnTouchListener false } }; gestureDetector.onTouchEvent(event); handleTrackpadTouch(event); true }
        trackpadLayout?.visibility = View.GONE
        windowManager?.addView(trackpadLayout, trackpadParams)
        updateBorderColor(currentBorderColor)
    }
    
    private fun setupCursor(context: Context) {
        cursorLayout = FrameLayout(context); cursorView = ImageView(context); cursorView?.setImageResource(R.drawable.ic_cursor); val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; cursorLayout?.addView(cursorView, FrameLayout.LayoutParams(size, size))
        cursorParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, 
            WindowManager.LayoutParams.WRAP_CONTENT, 
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, 
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or 
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or 
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
             cursorParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        cursorParams.gravity = Gravity.TOP or Gravity.LEFT; cursorParams.x = uiScreenWidth / 2; cursorParams.y = uiScreenHeight / 2; windowManager?.addView(cursorLayout, cursorParams)
    }

    fun toggleTrackpad() { 
        isTrackpadVisible = !isTrackpadVisible
        
        // Toggle Visibility
        trackpadLayout?.visibility = if (isTrackpadVisible) View.VISIBLE else View.GONE
        
        // Update Border if showing
        if (isTrackpadVisible) {
            updateBorderColor(currentBorderColor) 
        }
        
        // PREVIOUSLY: else if (isCustomKeyboardVisible) toggleCustomKeyboard(...)
        // We removed that line so the keyboard stays open.
    }
    
    // NEW FUNCTION: Toggles the visibility of the trackpad menu drawer
    private fun toggleDrawer() {
        menuManager?.toggle()
        enforceZOrder() // Ensure drawer is on top
    }
    
    private fun handleBubbleTap() {
        val anythingVisible = isTrackpadVisible || isCustomKeyboardVisible
        if (anythingVisible) {
            performSmartHide()
        } else {
            performSmartRestore()
        }
    }
    
    private fun executeHardkeyAction(actionId: String, keyEventAction: Int = KeyEvent.ACTION_UP) {
        val isUp = (keyEventAction == KeyEvent.ACTION_UP)
        when (actionId) {
            "none" -> { }
            "left_click" -> {
                if (keyEventAction == KeyEvent.ACTION_DOWN) {
                    if (!volUpDragActive) { volUpDragActive = true; startKeyDrag(MotionEvent.BUTTON_PRIMARY) }
                } else {
                    if (volUpDragActive) { volUpDragActive = false; stopKeyDrag(MotionEvent.BUTTON_PRIMARY) } 
                    else performClick(false)
                }
            }
            "right_click" -> {
                 if (keyEventAction == KeyEvent.ACTION_DOWN) {
                    if (!volDownDragActive) { volDownDragActive = true; startKeyDrag(MotionEvent.BUTTON_SECONDARY) }
                } else {
                    if (volDownDragActive) { volDownDragActive = false; stopKeyDrag(MotionEvent.BUTTON_SECONDARY) } 
                    else performClick(true)
                }
            }
            "action_back" -> if (isUp) Thread { try { injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN); Thread.sleep(20); injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP) } catch(e: Exception){} }.start()
            "action_home" -> if (isUp) Thread { try { injectKey(KeyEvent.KEYCODE_HOME, KeyEvent.ACTION_DOWN); Thread.sleep(20); injectKey(KeyEvent.KEYCODE_HOME, KeyEvent.ACTION_UP) } catch(e: Exception){} }.start()
            "action_forward" -> if (isUp) Thread { try { injectKey(KeyEvent.KEYCODE_FORWARD, KeyEvent.ACTION_DOWN); Thread.sleep(20); injectKey(KeyEvent.KEYCODE_FORWARD, KeyEvent.ACTION_UP) } catch(e: Exception){} }.start()
            "action_vol_up" -> if (isUp) Thread { try { injectKey(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.ACTION_DOWN); Thread.sleep(20); injectKey(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.ACTION_UP) } catch(e: Exception){} }.start()
            "action_vol_down" -> if (isUp) Thread { try { injectKey(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.ACTION_DOWN); Thread.sleep(20); injectKey(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.ACTION_UP) } catch(e: Exception){} }.start()
            "scroll_up" -> if (isUp) performSwipe(0f, -(BASE_SWIPE_DISTANCE * prefs.scrollSpeed))
            "scroll_down" -> if (isUp) performSwipe(0f, BASE_SWIPE_DISTANCE * prefs.scrollSpeed)
            "display_toggle" -> if (isUp) {
                if (prefs.displayOffMode == "standard") {
                    isScreenOff = !isScreenOff
                    Thread { try { if (isScreenOff) shellService?.setScreenOff(0, true) else shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start()
                    showToast("Display ${if(isScreenOff) "Off" else "On"} (Std)")
                } else {
                    isScreenOff = !isScreenOff
                    Thread { try { if (isScreenOff) shellService?.setBrightness(-1) else shellService?.setBrightness(128) } catch (e: Exception) {} }.start()
                    showToast("Display ${if(isScreenOff) "Off" else "On"} (Alt)")
                }
            }
            "display_toggle_alt" -> if (isUp) {
                isScreenOff = !isScreenOff
                Thread { try { if (isScreenOff) shellService?.setBrightness(-1) else shellService?.setBrightness(128) } catch (e: Exception) {} }.start()
                showToast("Display ${if(isScreenOff) "Off" else "On"} (Alt)")
            }
            "display_toggle_std" -> if (isUp) {
                isScreenOff = !isScreenOff
                Thread { try { if (isScreenOff) shellService?.setScreenOff(0, true) else shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start()
                showToast("Display ${if(isScreenOff) "Off" else "On"} (Std)")
            }
            "alt_position" -> if (isUp) toggleKeyboardMode()
            "toggle_keyboard" -> if (isUp) toggleCustomKeyboard()
            "toggle_trackpad" -> if (isUp) toggleTrackpad()
            "open_menu" -> if (isUp) menuManager?.toggle()
            "reset_cursor" -> if (isUp) resetCursorCenter()
            "display_wake" -> if (isUp && isScreenOff) { isScreenOff = false; Thread { try { shellService?.setBrightness(128); shellService?.setScreenOff(0, false) } catch (e: Exception) {} }.start(); showToast("Display Woken") }
            
            // "Launcher Bubble" Keybind Action - Force Toggle/Swap
            "toggle_bubble" -> if (isUp) {
                // Simply toggle between 0 and 1. 
                // If we are on 1, go to 0. If on 0, go to 1.
                // This guarantees movement if the user presses it.
                val targetId = if (currentDisplayId == 0) 1 else 0
                
                try {
                    showToast("Force Switch to $targetId")
                    setupUI(targetId)
                    resetBubblePosition()
                    menuManager?.show()
                    enforceZOrder()
                } catch (e: Exception) {
                    showToast("Error: ${e.message}")
                }
            }
        }
    }

    fun toggleKeyboardMode() { vibrate(); if (!isKeyboardMode) { isKeyboardMode = true; savedWindowX = trackpadParams.x; savedWindowY = trackpadParams.y; trackpadParams.x = uiScreenWidth - trackpadParams.width; trackpadParams.y = 0; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(0xFFFF0000.toInt()) } else { isKeyboardMode = false; trackpadParams.x = savedWindowX; trackpadParams.y = savedWindowY; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(currentBorderColor) } }
    
    private fun toggleDebugMode() { isDebugMode = !isDebugMode; if (isDebugMode) { showToast("Debug ON"); updateBorderColor(0xFFFFFF00.toInt()); debugTextView?.visibility = View.VISIBLE } else { showToast("Debug OFF"); if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt()); debugTextView?.visibility = View.GONE } }

    fun updateBubbleStatus() { val dot = bubbleView?.findViewById<ImageView>(R.id.status_dot); if (shellService != null) dot?.visibility = View.GONE else dot?.visibility = View.VISIBLE }

    private val bubbleIcons = arrayOf(R.mipmap.ic_trackpad_adaptive, R.drawable.ic_cursor, R.drawable.ic_tab_main, R.drawable.ic_tab_keyboard, android.R.drawable.ic_menu_compass, android.R.drawable.ic_menu_myplaces)
    
    fun updateBubbleSize(sizePercent: Int) { prefs.prefBubbleSize = sizePercent.coerceIn(50, 200); applyBubbleAppearance(); savePrefs() }
    fun updateBubbleIcon(index: Int) { prefs.prefBubbleIconIndex = index.coerceIn(0, bubbleIcons.size - 1); applyBubbleAppearance(); savePrefs() }
    fun cycleBubbleIcon() { updateBubbleIcon((prefs.prefBubbleIconIndex + 1) % bubbleIcons.size) }
    fun updateBubbleAlpha(alpha: Int) { prefs.prefBubbleAlpha = alpha.coerceIn(50, 255); applyBubbleAppearance(); savePrefs() }
    
    private fun applyBubbleAppearance() {
        if (bubbleView == null) return
        val scale = prefs.prefBubbleSize / 100f
        val density = resources.displayMetrics.density
        bubbleParams.width = (60 * scale * density).toInt()
        bubbleParams.height = (60 * scale * density).toInt()
        try { windowManager?.updateViewLayout(bubbleView, bubbleParams) } catch (e: Exception) {}
        val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon)
        iconView?.let {
            val iconParams = it.layoutParams as? FrameLayout.LayoutParams
            iconParams?.gravity = Gravity.CENTER
            iconParams?.width = (40 * scale * density).toInt()
            iconParams?.height = (40 * scale * density).toInt()
            it.layoutParams = iconParams
            it.setImageResource(bubbleIcons.getOrElse(prefs.prefBubbleIconIndex) { bubbleIcons[0] })
            it.alpha = prefs.prefBubbleAlpha / 255f
        }
        bubbleView?.alpha = prefs.prefBubbleAlpha / 255f
    }

    fun forceMoveToCurrentDisplay() { setupUI(currentDisplayId) }
    fun forceMoveToDisplay(displayId: Int) { if (displayId == currentDisplayId) return; setupUI(displayId) }
    fun hideApp() { menuManager?.hide(); if (isTrackpadVisible) toggleTrackpad() }


    // =================================================================================
    // INTER-APP COMMUNICATION HELPER FUNCTIONS
    // SUMMARY: Functions to support commands from DroidOS Launcher or ADB.
    //          These enable coordinated display switching and z-order management.
    // =================================================================================
    
    /**
     * Moves overlay to virtual display and enables Virtual Mirror Mode.
     * Called by MOVE_TO_VIRTUAL broadcast from Launcher or ADB.
     */
    private fun moveToVirtualDisplayAndEnableMirror(virtualDisplayId: Int) {
        try {
            Log.d(TAG, "Moving to virtual display $virtualDisplayId and enabling mirror mode")
            
            // Store current state for potential return
            preMirrorTrackpadVisible = isTrackpadVisible
            preMirrorKeyboardVisible = isCustomKeyboardVisible
            preMirrorTargetDisplayId = inputTargetDisplayId
            
            // Move UI to virtual display
            setupUI(virtualDisplayId)
            
            // Enable Virtual Mirror Mode
            if (!prefs.prefVirtualMirrorMode) {
                prefs.prefVirtualMirrorMode = true
                savePrefs()
            }
            
            // Set input target to virtual display
            inputTargetDisplayId = virtualDisplayId
            updateTargetMetrics(virtualDisplayId)
            
            // Show trackpad and keyboard
            if (!isTrackpadVisible) toggleTrackpad()
            if (!isCustomKeyboardVisible) toggleCustomKeyboard()
            
            // Create mirror keyboard on virtual display
            createMirrorKeyboard(virtualDisplayId)
            createRemoteCursor(virtualDisplayId)
            
            // Update visual indicators
            updateBorderColor(0xFFFF00FF.toInt()) // Purple for remote mode
            updateWakeLockState()
            
            showToast("Virtual Display Mode Active")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to move to virtual display", e)
            showToast("Error: ${e.message}")
        }
    }
    
    /**
     * Returns overlay to physical display and disables Virtual Mirror Mode.
     * Called by RETURN_TO_PHYSICAL broadcast from Launcher or ADB.
     */
    private fun returnToPhysicalDisplay(physicalDisplayId: Int) {
        try {
            Log.d(TAG, "Returning to physical display $physicalDisplayId")
            
            // Disable Virtual Mirror Mode
            if (prefs.prefVirtualMirrorMode) {
                prefs.prefVirtualMirrorMode = false
                savePrefs()
            }
            
            // Remove remote cursor and mirror keyboard
            removeRemoteCursor()
            removeMirrorKeyboard()
            
            // Move UI to physical display
            setupUI(physicalDisplayId)
            
            // Reset input target to local
            inputTargetDisplayId = physicalDisplayId
            cursorX = uiScreenWidth / 2f
            cursorY = uiScreenHeight / 2f
            cursorParams.x = cursorX.toInt()
            cursorParams.y = cursorY.toInt()
            try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception){}
            cursorView?.visibility = View.VISIBLE
            
            // Update visual indicators
            updateBorderColor(0x55FFFFFF.toInt()) // Default for local mode
            releaseDisplayWakeLock()
            
            showToast("Returned to Physical Display")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to return to physical display", e)
            showToast("Error: ${e.message}")
        }
    }
    // =================================================================================
    // END BLOCK: INTER-APP COMMUNICATION HELPER FUNCTIONS
    // =================================================================================

    


    // =================================================================================
    // PROFILE KEY GENERATION
    // SUMMARY: Generates a unique key based on Resolution + Mirror Mode State.
    //          Allows separate profiles for "Standard" and "Mirror" modes.
    // =================================================================================
    fun getProfileKey(): String {
        val mode = if (prefs.prefVirtualMirrorMode) "MIRROR" else "STD"
        return "P_${uiScreenWidth}_${uiScreenHeight}_$mode"
    }

    fun getSavedProfileList(): List<String> {

        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val allKeys = p.all.keys
        val profiles = java.util.HashSet<String>()
        
        // Regex matches: X_P_{width}_{height}_{suffix}
        // Group 1: Width
        // Group 2: Height
        // Group 3: Optional Suffix (MIRROR or STD)
        val regex = Regex("X_P_(\\d+)_(\\d+)(?:_([A-Z]+))?")
        
        for (key in allKeys) { 
            // We only care about X position keys to identify a profile exists
            if (!key.startsWith("X_P_")) continue

            val match = regex.matchEntire(key)
            if (match != null) {
                val w = match.groupValues[1]
                val h = match.groupValues[2]
                val suffix = match.groupValues.getOrNull(3) // Can be null, STD, or MIRROR
                
                var displayLabel = "$w x $h"
                
                // If it is a Mirror Profile, append VM
                if (suffix == "MIRROR") {
                    displayLabel += " VM"
                }
                
                profiles.add(displayLabel)
            }
        }
        return profiles.sorted()
    }


    // [Fixed] Helper to schedule the restart alarm
    private fun scheduleRestart() {
        try {
            Log.i(TAG, "Scheduling Safety Restart (1s)...")
            
            // 1. Explicit Intent with Action
            val restartIntent = Intent(applicationContext, OverlayService::class.java)
            restartIntent.action = "$packageName.SOFT_RESTART"
            restartIntent.setPackage(packageName)
            
            // 2. Use getForegroundService for Android 8+ (Stronger wake-up capability)
            val flags = if (Build.VERSION.SDK_INT >= 23) 
                android.app.PendingIntent.FLAG_ONE_SHOT or android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_CANCEL_CURRENT
            else 
                android.app.PendingIntent.FLAG_ONE_SHOT or android.app.PendingIntent.FLAG_CANCEL_CURRENT

            val pendingIntent = if (Build.VERSION.SDK_INT >= 26) {
                android.app.PendingIntent.getForegroundService(applicationContext, 1, restartIntent, flags)
            } else {
                android.app.PendingIntent.getService(applicationContext, 1, restartIntent, flags)
            }
            
            // 3. Robust Alarm Scheduling (Fallback for Permission/Device issues)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val triggerTime = System.currentTimeMillis() + 1000

            if (Build.VERSION.SDK_INT >= 31) {
                // Try setExact, fallback if permission missing
                try {
                    alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } catch (e: SecurityException) {
                    Log.w(TAG, "Missing SCHEDULE_EXACT_ALARM, using standard set()")
                    alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                // For older versions, use setExactAndAllowWhileIdle for Doze penetration
                if (Build.VERSION.SDK_INT >= 23) {
                     alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                     alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule restart", e)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // [Fixed] If Keep Alive is ON, schedule restart BEFORE system kills us.
        // Many ROMs force-kill services on swipe, so we must register the alarm now.
        if (prefs.prefPersistentService) {
            scheduleRestart()
            // We do NOT call forceExit() here; we let the system decide if it wants to kill us.
            // If it does kill us, the alarm brings us back.
        } else {
            forceExit()
        }
    }

    fun forceExit() {
        Log.i(TAG, "forceExit called")
        try {
            removeOldViews()
            
            // If triggered manually (Button/Broadcast), schedule restart if enabled
            if (prefs.prefPersistentService) {
                scheduleRestart()
                handler.post { Toast.makeText(applicationContext, "Restarting...", Toast.LENGTH_SHORT).show() }
            }

            stopSelf()
            // Allow small window for Alarm registration IPC
            Thread.sleep(200) 
            Process.killProcess(Process.myPid())
            System.exit(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // =================================================================================
    // FUNCTION: syncMirrorWithPhysicalKeyboard
    // SUMMARY: Updates mirror keyboard dimensions to match physical keyboard.
    //          Called when physical keyboard is resized/moved.
    // =================================================================================
    fun syncMirrorWithPhysicalKeyboard() {
        if (!prefs.prefVirtualMirrorMode || mirrorKeyboardView == null) return
        
        // Update coordinate scaling variables
        physicalKbWidth = keyboardOverlay?.getViewWidth()?.toFloat() ?: 600f
        physicalKbHeight = keyboardOverlay?.getViewHeight()?.toFloat() ?: 400f
        
        Log.d(TAG, "Mirror sync: Physical KB now ${physicalKbWidth}x${physicalKbHeight}")
        
        // If mirror has custom size, keep it. Otherwise, could auto-scale here.
        // For now, just update the scaling ratios used for touch coordinate mapping.
    }
    // =================================================================================
    // END BLOCK: syncMirrorWithPhysicalKeyboard
    // =================================================================================
    
    fun manualAdjust(isKeyboard: Boolean, isResize: Boolean, dx: Int, dy: Int) { 
        if (isKeyboard) { 
            if (isResize) keyboardOverlay?.resizeWindow(dx, dy) else keyboardOverlay?.moveWindow(dx, dy)
            // Sync mirror keyboard with physical keyboard changes
            syncMirrorWithPhysicalKeyboard()
        } 
        else { 
            if (trackpadLayout == null) return
            if (isResize) { trackpadParams.width = max(200, trackpadParams.width + dx); trackpadParams.height = max(200, trackpadParams.height + dy) } 
            { trackpadParams.x += dx; trackpadParams.y += dy }
            try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch (e: Exception) {}; saveLayout() 
        } 
    }
    
    private fun parseBoolean(value: Any): Boolean { return when(value) { is Boolean -> value; is Int -> value == 1; is String -> value == "1" || value.equals("true", ignoreCase = true); else -> false } }
    
    fun updatePref(key: String, value: Any) { 
        when(key) { 
            "cursor_speed" -> prefs.cursorSpeed = (value as? Float) ?: 2.5f
            "scroll_speed" -> prefs.scrollSpeed = (value as? Float) ?: 1.0f
            "tap_scroll" -> prefs.prefTapScroll = parseBoolean(value)
            "vibrate" -> prefs.prefVibrate = parseBoolean(value)
            "reverse_scroll" -> prefs.prefReverseScroll = parseBoolean(value)
            "alpha" -> { prefs.prefAlpha = value as Int; updateBorderColor(currentBorderColor) }
            "bg_alpha" -> { prefs.prefBgAlpha = value as Int; updateBorderColor(currentBorderColor) }
            "handle_size" -> { prefs.prefHandleSize = value as Int; updateHandleSize() }
            "scroll_size" -> { prefs.prefScrollTouchSize = value as Int; updateScrollSize() }
            "scroll_visual" -> { prefs.prefScrollVisualSize = value as Int; updateScrollSize() }
            "cursor_size" -> { prefs.prefCursorSize = value as Int; updateCursorSize() }
"anchored" -> { prefs.prefAnchored = parseBoolean(value); keyboardOverlay?.setAnchored(prefs.prefAnchored) }            "automation_enabled" -> prefs.prefAutomationEnabled = parseBoolean(value)
            "bubble_size" -> updateBubbleSize(value as Int)
            "bubble_icon" -> cycleBubbleIcon()
            "bubble_alpha" -> updateBubbleAlpha(value as Int)
            "persistent_service" -> prefs.prefPersistentService = parseBoolean(value)
            "block_soft_kb" -> { prefs.prefBlockSoftKeyboard = parseBoolean(value); setSoftKeyboardBlocking(prefs.prefBlockSoftKeyboard) }
            "keyboard_alpha" -> { prefs.prefKeyboardAlpha = value as Int; keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha) }

            "hardkey_vol_up_tap" -> prefs.hardkeyVolUpTap = value as String
            "hardkey_vol_up_double" -> prefs.hardkeyVolUpDouble = value as String
            "hardkey_vol_up_hold" -> prefs.hardkeyVolUpHold = value as String
            "hardkey_vol_down_tap" -> prefs.hardkeyVolDownTap = value as String
            "hardkey_vol_down_double" -> prefs.hardkeyVolDownDouble = value as String
            "hardkey_vol_down_hold" -> prefs.hardkeyVolDownHold = value as String
            "double_tap_ms" -> prefs.doubleTapMs = value as Int
            "hold_duration_ms" -> prefs.holdDurationMs = value as Int
            "mirror_alpha" -> {
                val v = value as Int
                prefs.prefMirrorAlpha = v
                // Apply to BOTH container and keyboard view
                val alpha = v / 255f
                mirrorKeyboardContainer?.alpha = alpha
                mirrorKeyboardView?.alpha = alpha
            }
            "mirror_orient_delay" -> {
                prefs.prefMirrorOrientDelayMs = value as Long
            }
            // =================================================================================
            // VIRTUAL MIRROR MODE UPDATE HANDLERS
            // =================================================================================
            "virtual_mirror_mode" -> {
                prefs.prefVirtualMirrorMode = parseBoolean(value)
                updateVirtualMirrorMode()
            }
            "mirror_orient_delay_ms" -> prefs.prefMirrorOrientDelayMs = (value as? Long) ?: 1000L
            // =================================================================================
            // END BLOCK: VIRTUAL MIRROR MODE UPDATE HANDLERS
            // =================================================================================
        }
        savePrefs() 
    }
    
    fun applyLayoutPreset(type: Int) {
        if (type == 0) { loadLayout(); showToast("Freeform Profile Loaded"); return }
        val h = uiScreenHeight; val w = uiScreenWidth; val density = resources.displayMetrics.density
        val targetW = (w * 0.96f).toInt(); val marginX = (w - targetW) / 2
        // Added 20dp buffer to preset height calculation
        val kbHeight = ((275f * (prefs.prefKeyScale / 100f) * density) + (20 * density)).toInt().coerceAtMost((h * 0.6f).toInt())
        val tpHeight = h - kbHeight
        if (keyboardOverlay == null) initCustomKeyboard()
        if (!isCustomKeyboardVisible) toggleCustomKeyboard(suppressAutomation = true)
        if (!isTrackpadVisible) toggleTrackpad()
        prefs.prefScrollTouchSize = 80; prefs.prefScrollVisualSize = 8
        when(type) {
            1 -> { keyboardOverlay?.setWindowBounds(marginX, 0, targetW, kbHeight); trackpadParams.width = targetW; trackpadParams.height = tpHeight; trackpadParams.x = marginX; trackpadParams.y = kbHeight }
            2 -> { trackpadParams.width = targetW; trackpadParams.height = tpHeight; trackpadParams.x = marginX; trackpadParams.y = 0; keyboardOverlay?.setWindowBounds(marginX, tpHeight, targetW, kbHeight) }
        }
        try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch(e: Exception){}
        updateScrollSize(); updateScrollPosition(); updateHandleSize(); updateLayoutSizes(); savePrefs(); showToast("Preset Applied")
    }
    
    fun resetBubblePosition() { bubbleParams.x = 50; bubbleParams.y = uiScreenHeight / 2; try { windowManager?.updateViewLayout(bubbleView, bubbleParams) } catch(e: Exception){}; prefs.prefBubbleX = bubbleParams.x; prefs.prefBubbleY = bubbleParams.y; savePrefs(); showToast("Bubble Reset") }

    private fun loadPrefs() { 
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        prefs.cursorSpeed = p.getFloat("cursor_speed", 2.5f)
        prefs.scrollSpeed = p.getFloat("scroll_speed", 0.6f) // CHANGED: Default 0.6f (Slider 6)
        prefs.prefTapScroll = p.getBoolean("tap_scroll", true)
        prefs.prefVibrate = p.getBoolean("vibrate", true)
        prefs.prefReverseScroll = p.getBoolean("reverse_scroll", false) // CHANGED: Default false
        prefs.prefAlpha = p.getInt("alpha", 50) // CHANGED: Default 50
        prefs.prefBgAlpha = p.getInt("bg_alpha", 220) // CHANGED: Default 220
        prefs.prefKeyboardAlpha = p.getInt("keyboard_alpha", 255) // CHANGED: Default 255
        prefs.prefHandleSize = p.getInt("handle_size", 14) // CHANGED: Default 14
        prefs.prefVPosLeft = p.getBoolean("v_pos_left", false)
        prefs.prefHPosTop = p.getBoolean("h_pos_top", false)
        prefs.prefAnchored = p.getBoolean("anchored", false)
        prefs.prefHandleTouchSize = p.getInt("handle_touch_size", 80)
        prefs.prefScrollTouchSize = p.getInt("scroll_touch_size", 80)
        prefs.prefScrollVisualSize = p.getInt("scroll_visual_size", 4)
                prefs.prefCursorSize = p.getInt("cursor_size", 50)
                prefs.prefKeyScale = p.getInt("keyboard_key_scale", 69) // Default 69 to match resetPosition
                prefs.prefUseAltScreenOff = p.getBoolean("use_alt_screen_off", true)
                prefs.prefAutomationEnabled = p.getBoolean("automation_enabled", false) 
                prefs.prefBubbleX = p.getInt("bubble_x", 50)
                prefs.prefBubbleY = p.getInt("bubble_y", 300)
                prefs.prefBubbleSize = p.getInt("bubble_size", 100)
                prefs.prefBubbleIconIndex = p.getInt("bubble_icon_index", 0)
                prefs.prefBubbleAlpha = p.getInt("bubble_alpha", 255)
                prefs.prefPersistentService = p.getBoolean("persistent_service", false)
                prefs.prefBlockSoftKeyboard = p.getBoolean("block_soft_kb", false)
                // Hardkey Defaults: Set to "none"
                prefs.hardkeyVolUpTap = p.getString("hardkey_vol_up_tap", "none") ?: "none"
                prefs.hardkeyVolUpDouble = p.getString("hardkey_vol_up_double", "none") ?: "none"
                prefs.hardkeyVolUpHold = p.getString("hardkey_vol_up_hold", "none") ?: "none"
                prefs.hardkeyVolDownTap = p.getString("hardkey_vol_down_tap", "none") ?: "none"
                prefs.hardkeyVolDownDouble = p.getString("hardkey_vol_down_double", "none") ?: "none"
                prefs.hardkeyVolDownHold = p.getString("hardkey_vol_down_hold", "none") ?: "none"
        prefs.hardkeyPowerDouble = p.getString("hardkey_power_double", "none") ?: "none"
        
        prefs.doubleTapMs = p.getInt("double_tap_ms", 300)
        prefs.holdDurationMs = p.getInt("hold_duration_ms", 400)

        // =================================================================================
        // VIRTUAL MIRROR MODE LOAD
        // =================================================================================
        // [Fixed] Always FORCE OFF on app restart/reload. Do not load saved state.
        prefs.prefVirtualMirrorMode = false 
        prefs.prefMirrorOrientDelayMs = p.getLong("mirror_orient_delay_ms", 1000L)

        // Load Mirror Keyboard Prefs
        prefs.prefMirrorAlpha = p.getInt("mirror_alpha", 200)
        prefs.prefMirrorX = p.getInt("mirror_x", -1)
        prefs.prefMirrorY = p.getInt("mirror_y", 0)
        prefs.prefMirrorWidth = p.getInt("mirror_width", -1)
        prefs.prefMirrorHeight = p.getInt("mirror_height", -1)
        // Note: No height pref, it's wrap_content
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR MODE LOAD
        // =================================================================================
    }
    


    fun saveLayout() {
        // 1. FETCH LIVE VALUES FROM PHYSICAL KEYBOARD
        val currentKbX = keyboardOverlay?.getViewX() ?: savedKbX
        val currentKbY = keyboardOverlay?.getViewY() ?: savedKbY
        val currentKbW = keyboardOverlay?.getViewWidth() ?: savedKbW
        val currentKbH = keyboardOverlay?.getViewHeight() ?: savedKbH
        
        // Fetch live scale
        val liveScale = keyboardOverlay?.getScale() ?: (prefs.prefKeyScale / 100f)
        prefs.prefKeyScale = (liveScale * 100).toInt()

        savedKbX = currentKbX; savedKbY = currentKbY; savedKbW = currentKbW; savedKbH = currentKbH

        // 2. SAVE TO SHARED PREFS (Using Mode-Specific Key)
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        val key = getProfileKey() // Returns ..._STD or ..._MIRROR
        
        // Save Trackpad Window
        p.putInt("X_$key", trackpadParams.x)
        p.putInt("Y_$key", trackpadParams.y)
        p.putInt("W_$key", trackpadParams.width)
        p.putInt("H_$key", trackpadParams.height)

        // Save Settings String (Standard Settings)
        val settingsStr = StringBuilder()
        settingsStr.append("${prefs.cursorSpeed};${prefs.scrollSpeed};${if(prefs.prefTapScroll) 1 else 0};${if(prefs.prefReverseScroll) 1 else 0};${prefs.prefAlpha};${prefs.prefBgAlpha};${prefs.prefKeyboardAlpha};${prefs.prefHandleSize};${prefs.prefHandleTouchSize};${prefs.prefScrollTouchSize};${prefs.prefScrollVisualSize};${prefs.prefCursorSize};${prefs.prefKeyScale};${if(prefs.prefAutomationEnabled) 1 else 0};${if(prefs.prefAnchored) 1 else 0};${prefs.prefBubbleSize};${prefs.prefBubbleAlpha};${prefs.prefBubbleIconIndex};${prefs.prefBubbleX};${prefs.prefBubbleY};${prefs.hardkeyVolUpTap};${prefs.hardkeyVolUpDouble};${prefs.hardkeyVolUpHold};${prefs.hardkeyVolDownTap};${prefs.hardkeyVolDownDouble};${prefs.hardkeyVolDownHold};${prefs.hardkeyPowerDouble};")
        
        // New Settings (Vibrate, Position)
        settingsStr.append("${if(prefs.prefVibrate) 1 else 0};${if(prefs.prefVPosLeft) 1 else 0};${if(prefs.prefHPosTop) 1 else 0};")

        // Physical Keyboard Bounds
        settingsStr.append("$currentKbX;$currentKbY;$currentKbW;$currentKbH")

        p.putString("SETTINGS_$key", settingsStr.toString())

        // [FIX] SAVE MIRROR KEYBOARD PARAMS (If in Mirror Mode)
        if (prefs.prefVirtualMirrorMode) {
            // Get live values from window params if available, otherwise use prefs
            val mX = mirrorKeyboardParams?.x ?: prefs.prefMirrorX
            val mY = mirrorKeyboardParams?.y ?: prefs.prefMirrorY
            val mW = mirrorKeyboardParams?.width ?: prefs.prefMirrorWidth
            val mH = mirrorKeyboardParams?.height ?: prefs.prefMirrorHeight
            val mAlpha = prefs.prefMirrorAlpha

            p.putInt("MIRROR_X_$key", mX)
            p.putInt("MIRROR_Y_$key", mY)
            p.putInt("MIRROR_W_$key", mW)
            p.putInt("MIRROR_H_$key", mH)
            p.putInt("MIRROR_ALPHA_$key", mAlpha)
        }

        p.apply()
        showToast("Layout Saved (${if(prefs.prefVirtualMirrorMode) "Mirror" else "Std"})")
    }


    private fun savePrefs() { 
        val e = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        
        e.putFloat("cursor_speed", prefs.cursorSpeed)
        e.putFloat("scroll_speed", prefs.scrollSpeed)
        e.putBoolean("tap_scroll", prefs.prefTapScroll)
        e.putBoolean("vibrate", prefs.prefVibrate)
        e.putBoolean("reverse_scroll", prefs.prefReverseScroll)
        e.putInt("alpha", prefs.prefAlpha)
        e.putInt("bg_alpha", prefs.prefBgAlpha)
        e.putInt("keyboard_alpha", prefs.prefKeyboardAlpha)
        e.putInt("handle_size", prefs.prefHandleSize)
        e.putBoolean("v_pos_left", prefs.prefVPosLeft)
        e.putBoolean("h_pos_top", prefs.prefHPosTop)
        e.putBoolean("anchored", prefs.prefAnchored)
        e.putInt("handle_touch_size", prefs.prefHandleTouchSize)
        e.putInt("scroll_touch_size", prefs.prefScrollTouchSize)
        e.putInt("scroll_visual_size", prefs.prefScrollVisualSize)
        e.putInt("cursor_size", prefs.prefCursorSize)
        e.putInt("keyboard_key_scale", prefs.prefKeyScale)
        e.putBoolean("use_alt_screen_off", prefs.prefUseAltScreenOff)
        e.putBoolean("automation_enabled", prefs.prefAutomationEnabled)
        e.putInt("bubble_x", prefs.prefBubbleX)
        e.putInt("bubble_y", prefs.prefBubbleY)
        e.putInt("bubble_size", prefs.prefBubbleSize)
        e.putInt("bubble_icon_index", prefs.prefBubbleIconIndex)
        e.putInt("bubble_alpha", prefs.prefBubbleAlpha)
        
        e.putBoolean("persistent_service", prefs.prefPersistentService)
        e.putBoolean("block_soft_kb", prefs.prefBlockSoftKeyboard)
        
        e.putString("hardkey_vol_up_tap", prefs.hardkeyVolUpTap)
        e.putString("hardkey_vol_up_double", prefs.hardkeyVolUpDouble)
        e.putString("hardkey_vol_up_hold", prefs.hardkeyVolUpHold)
        e.putString("hardkey_vol_down_tap", prefs.hardkeyVolDownTap)
        e.putString("hardkey_vol_down_double", prefs.hardkeyVolDownDouble)
        e.putString("hardkey_vol_down_hold", prefs.hardkeyVolDownHold)
        e.putString("hardkey_power_double", prefs.hardkeyPowerDouble)
        
        e.putInt("double_tap_ms", prefs.doubleTapMs)
        e.putInt("hold_duration_ms", prefs.holdDurationMs)

        // =================================================================================
        // VIRTUAL MIRROR MODE SAVE
        // =================================================================================
        e.putBoolean("virtual_mirror_mode", prefs.prefVirtualMirrorMode)
        e.putLong("mirror_orient_delay_ms", prefs.prefMirrorOrientDelayMs)

        // Save Mirror Keyboard Prefs
        e.putInt("mirror_alpha", prefs.prefMirrorAlpha)
        e.putInt("mirror_x", prefs.prefMirrorX)
        e.putInt("mirror_y", prefs.prefMirrorY)
        e.putInt("mirror_width", prefs.prefMirrorWidth)
        e.putInt("mirror_height", prefs.prefMirrorHeight)
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR MODE SAVE
        // =================================================================================

        e.apply() 
    }

    private fun bindShizuku() { try { val c = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(c, userServiceConnection, BuildConfig.DEBUG, BuildConfig.VERSION_CODE) } catch (e: Exception) { e.printStackTrace() } }

    // Helper to retry binding if connection is dead/null
    private fun checkAndBindShizuku() {
        // If we think we are bound, but the binder is dead, reset flag
        if (shellService != null && !shellService!!.asBinder().isBinderAlive) {
            isBound = false
            shellService = null
        }

        // If actually connected, do nothing
        if (shellService != null) return

        // Otherwise, try to bind
        bindShizuku()
    }

    private fun createNotification() { 
        try {
            val channel = NotificationChannel("overlay_service", "Trackpad", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            val notif = Notification.Builder(this, "overlay_service").setContentTitle("Trackpad Active").setSmallIcon(R.drawable.ic_cursor).build()
            try { if (Build.VERSION.SDK_INT >= 34) startForeground(1, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(1, notif) } catch(e: Exception) { startForeground(1, notif) }
        } catch(e: Exception) { e.printStackTrace() }
    }



    private fun initCustomKeyboard() { 
        if (appWindowManager == null || shellService == null) return
        
        keyboardOverlay = KeyboardOverlay(
            this, 
            appWindowManager!!, 
            shellService, 
            inputTargetDisplayId, 
            { toggleScreen() }, 
            { toggleScreenMode() }, 
            { toggleCustomKeyboard() }
        )
        
        // Wire up Trackpad Callbacks
        keyboardOverlay?.onCursorMove = { dx, dy, isDragging -> handleExternalMouseMove(dx, dy, isDragging) }
        keyboardOverlay?.onCursorClick = { isRight -> handleExternalMouseClick(isRight) }

        // Wire Touch Primitives
        keyboardOverlay?.onTouchDown = { handleExternalTouchDown() }
        keyboardOverlay?.onTouchUp = { handleExternalTouchUp() }
        keyboardOverlay?.onTouchTap = { handleExternalTouchTap() }

        // =================================================================================
        // VIRTUAL MIRROR TOUCH CALLBACK
        // SUMMARY: Wire up the mirror touch callback to forward touch events from the
        //          physical keyboard to the mirror keyboard on the remote display.
        //          Returns true if the touch should be consumed (orientation mode active).
        // =================================================================================
        keyboardOverlay?.onMirrorTouch = { x, y, action ->
            onMirrorKeyboardTouch(x, y, action)
        }
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR TOUCH CALLBACK
        // =================================================================================

        // Wire up layer change callback for mirror keyboard sync
        keyboardOverlay?.onLayerChanged = { state ->
            syncMirrorKeyboardLayer(state)
        }

        // =================================================================================
        // MIRROR SUGGESTIONS SYNC CALLBACK
        // SUMMARY: When suggestions change on physical keyboard, sync to mirror keyboard.
        //          This keeps the prediction bar in sync on both displays.
        // =================================================================================
        keyboardOverlay?.onSuggestionsChanged = { suggestions ->
            mirrorKeyboardView?.setSuggestions(suggestions)
        }
        // =================================================================================
        // END BLOCK: MIRROR SUGGESTIONS SYNC CALLBACK
        // =================================================================================

        // =================================================================================
        // PHYSICAL KEYBOARD SIZE/POSITION CHANGE CALLBACK
        // SUMMARY: When physical keyboard is moved/resized, sync mirror keyboard.
        // =================================================================================
        keyboardOverlay?.onSizeChanged = {
            syncMirrorWithPhysicalKeyboard()
        }
        // =================================================================================
        // END BLOCK: PHYSICAL KEYBOARD SIZE/POSITION CHANGE CALLBACK
        // =================================================================================

        // FIX: Restore Saved Layout (fixes reset/aspect ratio issue)
        if (savedKbW > 0 && savedKbH > 0) {
            keyboardOverlay?.updatePosition(savedKbX, savedKbY)
            keyboardOverlay?.updateSize(savedKbW, savedKbH)
        } else {
            // [Fixed] Default Size: Calculate height based on scale + 20dp buffer
            val density = resources.displayMetrics.density
            // Add 20dp padding so the scale fits comfortably without clipping
            val buffer = 20 * density
            val defaultH = ((275f * (prefs.prefKeyScale / 100f) * density) + buffer).toInt()
            
            keyboardOverlay?.updatePosition(0, uiScreenHeight - defaultH)
            keyboardOverlay?.updateSize(uiScreenWidth, defaultH)
        }

        // [REMOVED] Do not force scale here. 
        // KeyboardOverlay loads the fresh "keyboard_key_scale" from disk automatically.
        // Forcing 'prefs.prefKeyScale' here causes bugs because 'prefs' might be stale.
    }




    fun toggleCustomKeyboard(suppressAutomation: Boolean = false) {
        if (keyboardOverlay == null) initCustomKeyboard()
        
        val isNowVisible = if (keyboardOverlay?.isShowing() == true) { 
            keyboardOverlay?.hide()
            false 
        } else { 
            keyboardOverlay?.show()
            // [REMOVED] Stale scale enforcement. 
            // The show() method already loads the correct saved scale from disk.
            true 
        }
        
        isCustomKeyboardVisible = isNowVisible
        enforceZOrder()
        
        if (prefs.prefAutomationEnabled && !suppressAutomation) { 
            if (isNowVisible) turnScreenOn() else turnScreenOff() 
        }
    }

    private fun turnScreenOn() { isScreenOff = false; Thread { try { shellService?.setBrightness(128); shellService?.setScreenOff(0, false) } catch(e: Exception) {} }.start(); showToast("Screen On") }
    private fun turnScreenOff() { isScreenOff = true; Thread { try { if (prefs.prefUseAltScreenOff) shellService?.setBrightness(-1) else shellService?.setScreenOff(0, true) } catch(e: Exception) {} }.start(); showToast("Screen Off (${if(prefs.prefUseAltScreenOff) "Alt" else "Std"})") }
    private fun toggleScreenMode() { prefs.prefUseAltScreenOff = !prefs.prefUseAltScreenOff; savePrefs(); showToast("Mode: ${if(prefs.prefUseAltScreenOff) "Alternate" else "Standard"}") }
    private fun toggleScreen() { if (isScreenOff) turnScreenOn() else turnScreenOff() }
    
    private fun updateUiMetrics() { val display = displayManager?.getDisplay(currentDisplayId) ?: return; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); uiScreenWidth = metrics.widthPixels; uiScreenHeight = metrics.heightPixels }
    private fun createTrackpadDisplayContext(display: Display): Context { return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) createDisplayContext(display).createWindowContext(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null) else createDisplayContext(display) }
    private fun addHandle(context: Context, gravity: Int, color: Int, onTouch: (View, MotionEvent) -> Boolean) { val container = FrameLayout(context); val p = FrameLayout.LayoutParams(prefs.prefHandleTouchSize, prefs.prefHandleTouchSize); p.gravity = gravity; val visual = View(context); val bg = GradientDrawable(); bg.setColor(color); bg.cornerRadius = 15f; visual.background = bg; val vp = FrameLayout.LayoutParams(prefs.prefHandleSize, prefs.prefHandleSize); vp.gravity = Gravity.CENTER; container.addView(visual, vp); handleContainers.add(container); handleVisuals.add(visual); trackpadLayout?.addView(container, p); container.setOnTouchListener { v, e -> onTouch(v, e) } }
    
    private fun addScrollBars(context: Context) {
        val margin = prefs.prefHandleTouchSize + 10
        vScrollContainer = FrameLayout(context)
        val vp = FrameLayout.LayoutParams(prefs.prefScrollTouchSize, FrameLayout.LayoutParams.MATCH_PARENT); vp.gravity = if (prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; vp.setMargins(0, margin, 0, margin)
        trackpadLayout?.addView(vScrollContainer, vp)
        vScrollVisual = View(context); vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())
        vScrollContainer?.addView(vScrollVisual, FrameLayout.LayoutParams(prefs.prefScrollVisualSize, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER))
        vScrollContainer?.setOnTouchListener { _, event -> handleVScrollTouch(event); true }
        
        hScrollContainer = FrameLayout(context)
        val hp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefs.prefScrollTouchSize); hp.gravity = if (prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM; hp.setMargins(margin, 0, margin, 0)
        trackpadLayout?.addView(hScrollContainer, hp)
        hScrollVisual = View(context); hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())
        hScrollContainer?.addView(hScrollVisual, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefs.prefScrollVisualSize, Gravity.CENTER))
        hScrollContainer?.setOnTouchListener { _, event -> handleHScrollTouch(event); true }
    }

    private fun handleVScrollTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { isVScrolling = true; lastTouchY = event.y; scrollAccumulatorY = 0f; vScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt()); if (prefs.prefTapScroll) { val h = vScrollContainer?.height ?: return; val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed; performSwipe(0f, if (event.y < h/2) (if (prefs.prefReverseScroll) -dist else dist) else (if (prefs.prefReverseScroll) dist else -dist)) } }
            MotionEvent.ACTION_MOVE -> { if (isVScrolling && !prefs.prefTapScroll) { val dy = event.y - lastTouchY; scrollAccumulatorY += dy * prefs.scrollSpeed; if (abs(scrollAccumulatorY) > 30f) { performSwipe(0f, if (prefs.prefReverseScroll) -scrollAccumulatorY * 2 else scrollAccumulatorY * 2); scrollAccumulatorY = 0f }; lastTouchY = event.y } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isVScrolling = false; scrollAccumulatorY = 0f; vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt()) }
        }
    }
    
    private fun handleHScrollTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { isHScrolling = true; lastTouchX = event.x; scrollAccumulatorX = 0f; hScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt()); if (prefs.prefTapScroll) { val w = hScrollContainer?.width ?: return; val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed; performSwipe(if (event.x < w/2) (if (prefs.prefReverseScroll) -dist else dist) else (if (prefs.prefReverseScroll) dist else -dist), 0f) } }
            MotionEvent.ACTION_MOVE -> { if (isHScrolling && !prefs.prefTapScroll) { val dx = event.x - lastTouchX; scrollAccumulatorX += dx * prefs.scrollSpeed; if (abs(scrollAccumulatorX) > 30f) { performSwipe(if (prefs.prefReverseScroll) -scrollAccumulatorX * 2 else scrollAccumulatorX * 2, 0f); scrollAccumulatorX = 0f }; lastTouchX = event.x } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isHScrolling = false; scrollAccumulatorX = 0f; hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt()) }
        }
    }

    private fun updateScrollPosition() { val margin = prefs.prefHandleTouchSize + 10; vScrollContainer?.let { c -> val p = c.layoutParams as FrameLayout.LayoutParams; p.gravity = if (prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; p.setMargins(0, margin, 0, margin); c.layoutParams = p }; hScrollContainer?.let { c -> val p = c.layoutParams as FrameLayout.LayoutParams; p.gravity = if (prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM; p.setMargins(margin, 0, margin, 0); c.layoutParams = p } }
    private fun updateHandleSize() { for (v in handleVisuals) { val p = v.layoutParams; p.width = prefs.prefHandleSize; p.height = prefs.prefHandleSize; v.layoutParams = p } }
    private fun updateScrollSize() {
        prefs.prefScrollTouchSize = prefs.prefScrollTouchSize.coerceIn(40, 180); prefs.prefScrollVisualSize = prefs.prefScrollVisualSize.coerceIn(4, 20); scrollZoneThickness = prefs.prefScrollTouchSize
        vScrollContainer?.let { it.layoutParams.width = prefs.prefScrollTouchSize; it.requestLayout() }; vScrollVisual?.let { it.layoutParams.width = prefs.prefScrollVisualSize; it.requestLayout() }
        hScrollContainer?.let { it.layoutParams.height = prefs.prefScrollTouchSize; it.requestLayout() }; hScrollVisual?.let { it.layoutParams.height = prefs.prefScrollVisualSize; it.requestLayout() }
    }
    private fun updateLayoutSizes() { for (c in handleContainers) { val p = c.layoutParams; p.width = prefs.prefHandleTouchSize; p.height = prefs.prefHandleTouchSize; c.layoutParams = p } }
    private fun updateCursorSize() { val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; cursorView?.layoutParams?.let { it.width = size; it.height = size; cursorView?.layoutParams = it } }
    private fun updateBorderColor(strokeColor: Int) { currentBorderColor = strokeColor; val bg = trackpadLayout?.background as? GradientDrawable ?: return; bg.setColor((prefs.prefBgAlpha shl 24) or 0x000000); bg.setStroke(4, (prefs.prefAlpha shl 24) or 0xFFFFFF); trackpadLayout?.invalidate() }
    
    private fun performSwipe(dx: Float, dy: Float) {
        Thread {
            val dId = if (inputTargetDisplayId != -1) inputTargetDisplayId else (cursorLayout?.display?.displayId ?: Display.DEFAULT_DISPLAY)
            val now = SystemClock.uptimeMillis(); val startX = cursorX; val startY = cursorY; val endX = startX + dx; val endY = startY + dy
            try { shellService?.injectMouse(MotionEvent.ACTION_DOWN, startX, startY, dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now) } catch(e: Exception) {}
            for (i in 1..5) { val t = i / 5f; try { shellService?.injectMouse(MotionEvent.ACTION_MOVE, startX + (dx*t), startY + (dy*t), dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now + (i*10)); Thread.sleep(10) } catch(e: Exception) {} }
            try { shellService?.injectMouse(MotionEvent.ACTION_UP, endX, endY, dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now + 100) } catch(e: Exception) {}
        }.start()
    }

    private fun handleTrackpadTouch(event: MotionEvent) {
        val viewWidth = trackpadLayout?.width ?: 0; val viewHeight = trackpadLayout?.height ?: 0; if (viewWidth == 0 || viewHeight == 0) return
        if (isReleaseDebouncing && event.actionMasked != MotionEvent.ACTION_DOWN) return
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // NEW: dismiss Voice if active
                checkAndDismissVoice()

                handler.removeCallbacks(releaseDebounceRunnable); isReleaseDebouncing = false
                touchDownTime = SystemClock.uptimeMillis(); touchDownX = event.x; touchDownY = event.y; lastTouchX = event.x; lastTouchY = event.y; isTouchDragging = false
                val actualZoneV = min(scrollZoneThickness, (viewWidth * 0.15f).toInt()); val actualZoneH = min(scrollZoneThickness, (viewHeight * 0.15f).toInt())
                if ((prefs.prefVPosLeft && event.x < actualZoneV) || (!prefs.prefVPosLeft && event.x > viewWidth - actualZoneV) || (prefs.prefHPosTop && event.y < actualZoneH) || (!prefs.prefHPosTop && event.y > viewHeight - actualZoneH)) { ignoreTouchSequence = true; return }
                handler.postDelayed(longPressRunnable, 400)
            }
            MotionEvent.ACTION_MOVE -> {
                if (ignoreTouchSequence) return
                // VIRTUAL DISPLAY KEEP-ALIVE: Ping user activity during active touch on remote display
                if (inputTargetDisplayId != currentDisplayId) pingUserActivity()
                if (kotlin.math.sqrt((event.x - touchDownX) * (event.x - touchDownX) + (event.y - touchDownY) * (event.y - touchDownY)) > TAP_SLOP_PX) handler.removeCallbacks(longPressRunnable)
                val dx = (event.x - lastTouchX) * prefs.cursorSpeed; val dy = (event.y - lastTouchY) * prefs.cursorSpeed
                val safeW = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth.toFloat() else uiScreenWidth.toFloat()
                val safeH = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight.toFloat() else uiScreenHeight.toFloat()
                var fDx = dx; var fDy = dy
                when (rotationAngle) { 90 -> { fDx = -dy; fDy = dx }; 180 -> { fDx = -dx; fDy = -dy }; 270 -> { fDx = dy; fDy = -dx } }
                cursorX = (cursorX + fDx).coerceIn(0f, safeW); cursorY = (cursorY + fDy).coerceIn(0f, safeH)
                if (inputTargetDisplayId == currentDisplayId) { cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {} } 
                else { remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {} }
                if (isTouchDragging || isKeyDragging) injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, SystemClock.uptimeMillis()) else injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
                lastTouchX = event.x; lastTouchY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable)
                if (!ignoreTouchSequence) {
                    if (isTouchDragging) { injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, SystemClock.uptimeMillis()); isTouchDragging = false }
                    else if (!isKeyDragging && SystemClock.uptimeMillis() - touchDownTime < TAP_TIMEOUT_MS && kotlin.math.sqrt((event.x - touchDownX) * (event.x - touchDownX) + (event.y - touchDownY) * (event.y - touchDownY)) < TAP_SLOP_PX) performClick(false)
                }
                isReleaseDebouncing = true; handler.postDelayed(releaseDebounceRunnable, RELEASE_DEBOUNCE_MS)
                if (!isKeyDragging) { isVScrolling = false; isHScrolling = false; updateBorderColor(currentBorderColor) }
                ignoreTouchSequence = false
            }
        }
    }

    // --- SMART VISIBILITY LOGIC ---
    fun performSmartHide() {
        pendingRestoreTrackpad = isTrackpadVisible
        pendingRestoreKeyboard = isCustomKeyboardVisible
        hasPendingRestore = true
        
        // Hide components (Automation logic inside toggleCustomKeyboard will handle screen off if enabled)
        if (isCustomKeyboardVisible) toggleCustomKeyboard()
        if (isTrackpadVisible) toggleTrackpad()
        
        handler.post { Toast.makeText(this, "Hidden (Tap Bubble to Restore)", Toast.LENGTH_SHORT).show() }
    }

    fun performSmartRestore() {
        if (!hasPendingRestore) {
            // Fallback: Just show Trackpad if no state saved
            if (!isTrackpadVisible) toggleTrackpad()
            return
        }
        
        if (pendingRestoreTrackpad && !isTrackpadVisible) toggleTrackpad()
        if (pendingRestoreKeyboard && !isCustomKeyboardVisible) toggleCustomKeyboard()
        
        hasPendingRestore = false
    }

    private fun moveWindow(event: MotionEvent): Boolean { if (prefs.prefAnchored) return true; if (event.action == MotionEvent.ACTION_MOVE) { trackpadParams.x += (event.rawX - lastTouchX).toInt(); trackpadParams.y += (event.rawY - lastTouchY).toInt(); windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }; lastTouchX = event.rawX; lastTouchY = event.rawY; return true }
    private fun resizeWindow(event: MotionEvent): Boolean { if (prefs.prefAnchored) return true; if (event.action == MotionEvent.ACTION_MOVE) { trackpadParams.width += (event.rawX - lastTouchX).toInt(); trackpadParams.height += (event.rawY - lastTouchY).toInt(); windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }; lastTouchX = event.rawX; lastTouchY = event.rawY; return true }
    private fun keyboardHandle(event: MotionEvent): Boolean { 
        if (event.action == MotionEvent.ACTION_UP) {
            // Reverted: Just toggle keyboard visibility
            toggleCustomKeyboard()
        } 
        return true 
    }
    private fun openMenuHandle(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_DOWN) menuManager?.toggle(); return true }
    private fun injectAction(action: Int, source: Int, button: Int, time: Long) { if (shellService == null) return; Thread { try { shellService?.injectMouse(action, cursorX, cursorY, inputTargetDisplayId, source, button, time) } catch(e: Exception){} }.start() }
    private fun injectScroll(hScroll: Float, vScroll: Float) { if (shellService == null) return; Thread { try { shellService?.injectScroll(cursorX, cursorY, vScroll / 10f, hScroll / 10f, inputTargetDisplayId) } catch(e: Exception){} }.start() }

    // Helper to allow external components (like Keyboard) to control the cursor
    // Added 'isDragging' to switch between Hover (Mouse) and Drag (Touch)
// =================================================================================
    // SPACEBAR MOUSE CURSOR MOVEMENT HANDLER
    // SUMMARY: Handles cursor movement from the spacebar trackpad feature.
    //          Updates cursor position and visual, then injects hover/drag events.
    //          CRITICAL: Skips hover injection when cursor is over keyboard bounds
    //          to prevent feedback loop that causes lag/freezing.
    // =================================================================================
    fun handleExternalMouseMove(dx: Float, dy: Float, isDragging: Boolean) {
        // Calculate safe bounds
        val safeW = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth.toFloat() else uiScreenWidth.toFloat()
        val safeH = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight.toFloat() else uiScreenHeight.toFloat()

        // Update position
        cursorX = (cursorX + dx).coerceIn(0f, safeW)
        cursorY = (cursorY + dy).coerceIn(0f, safeH)

        // Update Visuals (Redraw the cursor icon)
        if (inputTargetDisplayId == currentDisplayId) {
             cursorParams.x = cursorX.toInt()
             cursorParams.y = cursorY.toInt()
             try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {}
        } else {
             remoteCursorParams.x = cursorX.toInt()
             remoteCursorParams.y = cursorY.toInt()
             try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {}
        }

        // [ANTI-FEEDBACK-LOOP FIX]
        // Check if cursor is over the keyboard - if so, skip hover injection
        // This prevents the injected mouse events from creating a feedback loop
        // where the keyboard receives the hover, processes it, and triggers more events
        val isOverKeyboard = isCursorOverKeyboard()
        
        // Input Injection
        if (isDragging) {
             // TOUCH DRAG: SOURCE_TOUCHSCREEN + ACTION_MOVE
             // Always inject drag events - user explicitly initiated drag
             injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, 0, SystemClock.uptimeMillis())
        } else {
             // MOUSE HOVER: SOURCE_MOUSE + ACTION_HOVER_MOVE
             // Skip hover injection when over keyboard to prevent feedback loop
             if (!isOverKeyboard) {
                 injectAction(MotionEvent.ACTION_HOVER_MOVE, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
             }
        }
    }
    // =================================================================================
    // END BLOCK: SPACEBAR MOUSE CURSOR MOVEMENT HANDLER
    // =================================================================================

    // =================================================================================
    // KEYBOARD BOUNDS CHECK FOR CURSOR
    // SUMMARY: Returns true if the cursor is currently positioned over the keyboard
    //          overlay window. Used to prevent feedback loops when the spacebar
    //          mouse feature moves the cursor over the keyboard itself.
    // =================================================================================
    private fun isCursorOverKeyboard(): Boolean {
        // Only check if keyboard is visible and we're on the same display
        if (!isCustomKeyboardVisible) return false
        if (inputTargetDisplayId != currentDisplayId) return false
        
        // Get keyboard bounds from the overlay
        val kbX = keyboardOverlay?.getViewX() ?: return false
        val kbY = keyboardOverlay?.getViewY() ?: return false
        val kbW = keyboardOverlay?.getViewWidth() ?: return false
        val kbH = keyboardOverlay?.getViewHeight() ?: return false
        
        // Add a small padding to the bounds to ensure we catch edge cases
        val padding = 10
        
        return cursorX >= (kbX - padding) && 
               cursorX <= (kbX + kbW + padding) &&
               cursorY >= (kbY - padding) && 
               cursorY <= (kbY + kbH + padding)
    }
    // =================================================================================
    // END BLOCK: KEYBOARD BOUNDS CHECK FOR CURSOR
    // ==============================================

    // Explicit Touch Down (Start Drag/Hold)
    fun handleExternalTouchDown() {
        injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, 0, SystemClock.uptimeMillis())
        android.util.Log.d("TouchInjection", "Touch DOWN at ($cursorX, $cursorY)")
    }

    // Explicit Touch Up (End Drag/Hold)
    fun handleExternalTouchUp() {
        injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, 0, SystemClock.uptimeMillis())
        android.util.Log.d("TouchInjection", "Touch UP at ($cursorX, $cursorY)")
    }

    // Quick Tap (Down + Up)
    fun handleExternalTouchTap() {
        if (shellService == null) return
        Thread {
            val now = SystemClock.uptimeMillis()
            try {
                shellService?.injectMouse(MotionEvent.ACTION_DOWN, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, 0, now)
                Thread.sleep(50)
                shellService?.injectMouse(MotionEvent.ACTION_UP, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, 0, now + 50)
                android.util.Log.d("TouchInjection", "Touch TAP at ($cursorX, $cursorY)")
            } catch (e: Exception) {
                android.util.Log.e("TouchInjection", "TAP failed", e)
            }
        }.start()
    }

    // Keep Right Click for the predictive bar if needed
    fun handleExternalMouseClick(isRight: Boolean) {
        performClick(isRight)
    }

    fun performClick(right: Boolean) { if (shellService == null) return; Thread { try { if (right) shellService?.execRightClick(cursorX, cursorY, inputTargetDisplayId) else shellService?.execClick(cursorX, cursorY, inputTargetDisplayId) } catch(e: Exception){} }.start() }
    fun resetCursorCenter() { cursorX = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth/2f else uiScreenWidth/2f; cursorY = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight/2f else uiScreenHeight/2f; if (inputTargetDisplayId == currentDisplayId) { cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); windowManager?.updateViewLayout(cursorLayout, cursorParams) } else { remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception){} } }
    fun performRotation() { rotationAngle = (rotationAngle + 90) % 360; cursorView?.rotation = rotationAngle.toFloat() }

    // =================================================================================
    // PROFILE KEY GENERATION
    // SUMMARY: Generates a unique key based on Resolution + Mirror Mode State.
    //          Allows separate profiles for "Standard" and "Mirror" modes.
    // =================================================================================

    // =================================================================================
    // PROFILE KEY GENERATION
    // SUMMARY: Generates a unique key based on Resolution + Mirror Mode State.
    //          Allows separate profiles for "Standard" and "Mirror" modes.
    // =================================================================================




    // =================================================================================
    // VIRTUAL MIRROR MODE PROFILE KEY
    // SUMMARY: Returns a unique profile key for mirror mode, separate from normal
    //          display-based profiles. This allows mirror mode to have its own
    //          trackpad/keyboard layout that persists independently.
    // =================================================================================

    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE PROFILE KEY
    // =================================================================================

    // =================================================================================
    // VIRTUAL MIRROR MODE LAYOUT SAVE
    // SUMMARY: Saves the current layout to the mirror mode profile. Called when
    //          exiting mirror mode or when explicitly saving while in mirror mode.
    //          Includes both physical keyboard AND mirror keyboard positions/sizes.
    // =================================================================================

    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE LAYOUT SAVE
    // =================================================================================

    // =================================================================================
    // VIRTUAL MIRROR MODE LAYOUT LOAD
    // SUMMARY: Loads the mirror mode profile layout. Called when entering mirror mode.
    //          If no saved profile exists, uses sensible defaults.
    // =================================================================================

    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE LAYOUT LOAD
    // =================================================================================

    // =================================================================================
    // FUNCTION: applyMirrorKeyboardSettings
    // SUMMARY: Applies saved mirror keyboard position/size/alpha to the live mirror
    //          keyboard. Called after loading a mirror mode profile.
    // =================================================================================
    private fun applyMirrorKeyboardSettings() {
        if (mirrorKeyboardParams == null || mirrorKeyboardContainer == null) return

        // Apply saved position if valid
        if (prefs.prefMirrorX != -1) {
            mirrorKeyboardParams?.x = prefs.prefMirrorX
            mirrorKeyboardParams?.gravity = Gravity.TOP or Gravity.START
        }
        if (prefs.prefMirrorY != 0 || prefs.prefMirrorX != -1) {
            mirrorKeyboardParams?.y = prefs.prefMirrorY
        }
        if (prefs.prefMirrorWidth != -1 && prefs.prefMirrorWidth > 0) {
            mirrorKeyboardParams?.width = prefs.prefMirrorWidth
        }
        if (prefs.prefMirrorHeight != -1 && prefs.prefMirrorHeight > 0) {
            mirrorKeyboardParams?.height = prefs.prefMirrorHeight
        }

        // Apply alpha
        val alpha = prefs.prefMirrorAlpha / 255f
        mirrorKeyboardContainer?.alpha = alpha

        // Update the window
        try {
            mirrorWindowManager?.updateViewLayout(mirrorKeyboardContainer, mirrorKeyboardParams)

            // Update sync dimensions after layout change
            handler.postDelayed({
                updateMirrorSyncDimensions()
            }, 100)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply mirror keyboard settings", e)
        }

        Log.d(TAG, "Mirror keyboard settings applied")
    }
    // =================================================================================
    // END BLOCK: applyMirrorKeyboardSettings
    // =================================================================================


    fun loadLayout() {
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val key = getProfileKey()
        
        // 1. Load Trackpad Window
        trackpadParams.x = p.getInt("X_$key", 100)
        trackpadParams.y = p.getInt("Y_$key", 100)
        trackpadParams.width = p.getInt("W_$key", 400)
        trackpadParams.height = p.getInt("H_$key", 300)
        try {
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams)
        } catch(e: Exception){}
        
        // 2. Load Settings String
        val settings = p.getString("SETTINGS_$key", null)
        var keyboardUpdated = false

        if (settings != null) {
            val parts = settings.split(";")
            if (parts.size >= 15) {
                // ... (Parsing Basic Params) ...
                prefs.cursorSpeed = parts[0].toFloat(); prefs.scrollSpeed = parts[1].toFloat()
                prefs.prefTapScroll = parts[2] == "1"; prefs.prefReverseScroll = parts[3] == "1"
                prefs.prefAlpha = parts[4].toInt(); prefs.prefBgAlpha = parts[5].toInt()
                prefs.prefKeyboardAlpha = parts[6].toInt(); prefs.prefHandleSize = parts[7].toInt()
                prefs.prefHandleTouchSize = parts[8].toInt(); prefs.prefScrollTouchSize = parts[9].toInt()
                prefs.prefScrollVisualSize = parts[10].toInt(); prefs.prefCursorSize = parts[11].toInt()
                prefs.prefKeyScale = parts[12].toInt()
                prefs.prefAutomationEnabled = parts[13] == "1"; prefs.prefAnchored = parts[14] == "1"
                
                if (parts.size >= 27) {
                    prefs.prefBubbleSize = parts[15].toInt(); prefs.prefBubbleAlpha = parts[16].toInt()
                    prefs.prefBubbleIconIndex = parts[17].toInt(); prefs.prefBubbleX = parts[18].toInt()
                    prefs.prefBubbleY = parts[19].toInt(); prefs.hardkeyVolUpTap = parts[20]
                    prefs.hardkeyVolUpDouble = parts[21]; prefs.hardkeyVolUpHold = parts[22]
                    prefs.hardkeyVolDownTap = parts[23]; prefs.hardkeyVolDownDouble = parts[24]
                    prefs.hardkeyVolDownHold = parts[25]; prefs.hardkeyPowerDouble = parts[26]
                }
                
                if (parts.size >= 30) {
                    prefs.prefVibrate = parts[27] == "1"
                    prefs.prefVPosLeft = parts[28] == "1"
                    prefs.prefHPosTop = parts[29] == "1"
                }

                // Load Physical Keyboard Bounds
                var kbIndex = 30
                if (parts.size < 34 && parts.size >= 31) kbIndex = 27 

                if (parts.size > kbIndex) {
                     savedKbX = parts[kbIndex].toInt(); savedKbY = parts[kbIndex+1].toInt()
                     savedKbW = parts[kbIndex+2].toInt(); savedKbH = parts[kbIndex+3].toInt()
                     
                     keyboardOverlay?.setWindowBounds(savedKbX, savedKbY, savedKbW, savedKbH)
                     keyboardUpdated = true
                }

                // Apply Visuals
                updateBorderColor(currentBorderColor); updateScrollSize(); updateHandleSize()
                updateCursorSize(); updateScrollPosition()
                keyboardOverlay?.updateScale(prefs.prefKeyScale / 100f)
                keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha)
                keyboardOverlay?.setAnchored(prefs.prefAnchored)
                keyboardOverlay?.setVibrationEnabled(prefs.prefVibrate)
                applyBubbleAppearance()
            }
        }

        // [FIX] LOAD MIRROR KEYBOARD PARAMS (If in Mirror Mode)
        if (prefs.prefVirtualMirrorMode) {
            if (p.contains("MIRROR_X_$key")) {
                prefs.prefMirrorX = p.getInt("MIRROR_X_$key", -1)
                prefs.prefMirrorY = p.getInt("MIRROR_Y_$key", 0)
                prefs.prefMirrorWidth = p.getInt("MIRROR_W_$key", -1)
                prefs.prefMirrorHeight = p.getInt("MIRROR_H_$key", -1)
                prefs.prefMirrorAlpha = p.getInt("MIRROR_ALPHA_$key", 200)
                
                // Update live window if it exists
                applyMirrorKeyboardSettings()
            }
        }

        // [FIX] GHOSTING PREVENTION
        if (!keyboardUpdated) {
            keyboardOverlay?.resetPosition()
            showToast("Defaults Loaded")
        } else {
            showToast("Profile Loaded: ${if(prefs.prefVirtualMirrorMode) "Mirror" else "Std"}")
        }
    }



    fun deleteCurrentProfile() { /* Stub */ }
    fun resetKeyboardPosition() {
        keyboardOverlay?.resetPosition()
    }

    fun rotateKeyboard() {
        keyboardOverlay?.cycleRotation()
    }

    fun resetTrackpadPosition() { trackpadParams.x = 100; trackpadParams.y = 100; trackpadParams.width = 400; trackpadParams.height = 300; windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }    // =================================================================================
// =================================================================================
    // FUNCTION: showMirrorTemporarily
    // SUMMARY: Makes mirror keyboard visible temporarily, like when touched.
    //          Used during D-pad adjustments so user can see changes.
    // =================================================================================
    private fun showMirrorTemporarily() {
        if (mirrorKeyboardContainer == null || mirrorKeyboardView == null) return
        
        // Cancel any pending fade
        mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)
        
        // Show mirror - only adjust alpha, container is transparent
        val alpha = prefs.prefMirrorAlpha / 255f
        mirrorKeyboardContainer?.alpha = 1f
        mirrorKeyboardView?.alpha = alpha.coerceAtLeast(0.7f)
        
        // Schedule fade out after 2 seconds
        mirrorFadeHandler.postDelayed(mirrorFadeRunnable, 2000)
    }
    // =================================================================================
    // END BLOCK: showMirrorTemporarily
    // =================================================================================
    // =================================================================================
// =================================================================================
    // FUNCTION: adjustMirrorKeyboard
    // SUMMARY: Adjusts mirror keyboard position or size via D-pad controls.
    //          FIXED: Correct Y direction for both move and resize.
    //          FIXED: Handle WRAP_CONTENT properly for resize.
    // =================================================================================
    fun adjustMirrorKeyboard(isResize: Boolean, deltaX: Int, deltaY: Int) {
        // DEBUG: Show toast so we know function is called
        showToast("adjust: resize=$isResize dX=$deltaX dY=$deltaY")
        
        if (mirrorKeyboardParams == null || mirrorKeyboardContainer == null) {
            showToast("ERROR: params null")
            return
        }
        
        // Show mirror during adjustment
        showMirrorTemporarily()
        
if (isResize) {
            // =================================================================================
            // RESIZE MODE - adjust width/height AND scale the keyboard keys
            // SUMMARY: Changes container size and scales the inner KeyboardView to match.
            //          Also updates sync dimensions so touch coordinates map correctly.
            // =================================================================================
            
            // Get current dimensions from params
            var currentWidth = mirrorKeyboardParams?.width ?: 600
            var currentHeight = mirrorKeyboardParams?.height ?: 350
            
            // Handle WRAP_CONTENT (-2) - must get actual measured size from view
            if (currentWidth == WindowManager.LayoutParams.WRAP_CONTENT || currentWidth <= 0) {
                currentWidth = mirrorKeyboardContainer?.width ?: mirrorKeyboardView?.width ?: 600
                if (currentWidth <= 0) {
                    val display = displayManager?.getDisplay(inputTargetDisplayId)
                    if (display != null) {
                        val metrics = android.util.DisplayMetrics()
                        display.getRealMetrics(metrics)
                        currentWidth = (metrics.widthPixels * 0.9f).toInt()
                    } else {
                        currentWidth = 600
                    }
                }
                mirrorKeyboardParams?.width = currentWidth
            }
            
            if (currentHeight == WindowManager.LayoutParams.WRAP_CONTENT || currentHeight <= 0) {
                currentHeight = mirrorKeyboardContainer?.height ?: mirrorKeyboardView?.height ?: 350
                if (currentHeight <= 0) {
                    currentHeight = 350
                }
                mirrorKeyboardParams?.height = currentHeight
            }
            
            // Apply deltas: positive = grow, negative = shrink
            val newWidth = (currentWidth + deltaX).coerceIn(250, 1500)
            val newHeight = (currentHeight + deltaY).coerceIn(150, 1200)
            
            android.util.Log.d("MirrorResize", "Resize: ${currentWidth}x${currentHeight} -> ${newWidth}x${newHeight}")
            
            // Update container window params
            mirrorKeyboardParams?.width = newWidth
            mirrorKeyboardParams?.height = newHeight
            
            // Calculate new scale based on height ratio
            // Use physical keyboard as reference - mirror should scale proportionally
            val physicalHeight = keyboardOverlay?.getViewHeight()?.toFloat() ?: 350f
            val physicalScale = keyboardOverlay?.getScale() ?: 1.0f
            
            // Calculate what scale the mirror needs to fit in newHeight
            // Scale is proportional to height ratio
            val heightRatio = newHeight.toFloat() / physicalHeight
            val newScale = (physicalScale * heightRatio).coerceIn(0.5f, 2.0f)
            
            android.util.Log.d("MirrorResize", "Scale: physH=$physicalHeight, physScale=$physicalScale, ratio=$heightRatio, newScale=$newScale")
            
            // Apply scale to mirror keyboard - this resizes the actual keys
            mirrorKeyboardView?.setScale(newScale)
            
            // Update inner view layout params to match container
            mirrorKeyboardView?.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT  // Let it size naturally with new scale
            )
            
            // Save to prefs
            prefs.prefMirrorWidth = newWidth
            prefs.prefMirrorHeight = newHeight
            
            getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
                .putInt("mirror_width", newWidth)
                .putInt("mirror_height", newHeight)
                .apply()
        } else {
            // =================================================================================
            // MOVE MODE - adjust x/y position
            // For move: we want UP button to move keyboard UP on screen
            // UP sends deltaY = -20
            // With Gravity.BOTTOM: to move UP visually, Y offset must INCREASE
            // With Gravity.TOP: to move UP visually, Y offset must DECREASE
            // =================================================================================
            val currentX = mirrorKeyboardParams?.x ?: 0
            val currentY = mirrorKeyboardParams?.y ?: 0
            val gravity = mirrorKeyboardParams?.gravity ?: 0
            val isBottomGravity = (gravity and Gravity.BOTTOM) == Gravity.BOTTOM
            
            val newX = currentX + deltaX
            
            // CRITICAL FIX: Correct Y handling based on gravity
            val newY = if (isBottomGravity) {
                // Gravity.BOTTOM: positive Y = UP from bottom
                // UP button sends -20, we want to move UP, so negate: currentY - (-20) = currentY + 20
                currentY - deltaY
            } else {
                // Gravity.TOP: positive Y = DOWN from top  
                // UP button sends -20, we want to move UP, so add directly: currentY + (-20)
                currentY + deltaY
            }
            
            showToast("Move: ($currentX,$currentY)->($newX,$newY) btm=$isBottomGravity")
            
            mirrorKeyboardParams?.x = newX
            mirrorKeyboardParams?.y = newY
            
            // Switch to TOP gravity after first move for consistent behavior
            mirrorKeyboardParams?.gravity = Gravity.TOP or Gravity.START
            
            getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
                .putInt("mirror_x", newX)
                .putInt("mirror_y", newY)
                .apply()
        }
        
        try {
            mirrorWindowManager?.updateViewLayout(mirrorKeyboardContainer, mirrorKeyboardParams)
            
            if (isResize) {
                handler.postDelayed({ updateMirrorSyncDimensions() }, 100)
            }
        } catch (e: Exception) {
            showToast("Layout update failed: ${e.message}")
        }
    }
    // =================================================================================
    // END BLOCK: adjustMirrorKeyboard
    // =================================================================================
    // =================================================================================
    // END BLOCK: adjustMirrorKeyboard
    // =================================================================================
    // =================================================================================
// =================================================================================
    // FUNCTION: resetMirrorKeyboardPosition
    // SUMMARY: Resets mirror keyboard to default centered position at bottom.
    //          Uses WRAP_CONTENT height and triggers sync dimension update.
    // =================================================================================
    fun resetMirrorKeyboardPosition() {
        if (mirrorKeyboardParams == null || mirrorKeyboardContainer == null) return
        
        // Show during reset
        showMirrorTemporarily()
        
        // Get display metrics for auto-sizing
        val display = displayManager?.getDisplay(inputTargetDisplayId) ?: return
        val metrics = android.util.DisplayMetrics()
        display.getRealMetrics(metrics)
        
        val mirrorWidth = (metrics.widthPixels * 0.95f).toInt()
        
        // Reset to defaults
        mirrorKeyboardParams?.x = 0
        mirrorKeyboardParams?.y = 0
        mirrorKeyboardParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        mirrorKeyboardParams?.width = mirrorWidth
        mirrorKeyboardParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        
        // Clear saved prefs
        getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
            .remove("mirror_x")
            .remove("mirror_y")
            .remove("mirror_width")
            .remove("mirror_height")
            .apply()
        
        try {
            mirrorWindowManager?.updateViewLayout(mirrorKeyboardContainer, mirrorKeyboardParams)
            
            // FIX: Update sync dimensions after layout
            handler.postDelayed({
                updateMirrorSyncDimensions()
            }, 100)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reset mirror keyboard layout", e)
        }
        
        Log.d(TAG, "Mirror keyboard reset to defaults")
    }
    // =================================================================================
    // END BLOCK: resetMirrorKeyboardPosition
    // =================================================================================
    fun cycleInputTarget() {
        if (displayManager == null) return; val displays = displayManager!!.displays; var nextId = -1
        for (d in displays) { if (d.displayId != currentDisplayId) { if (inputTargetDisplayId == currentDisplayId) { nextId = d.displayId; break } else if (inputTargetDisplayId == d.displayId) { continue } else { nextId = d.displayId } } }
        if (nextId == -1) { inputTargetDisplayId = currentDisplayId; targetScreenWidth = uiScreenWidth; targetScreenHeight = uiScreenHeight; removeRemoteCursor(); removeMirrorKeyboard(); cursorX = uiScreenWidth / 2f; cursorY = uiScreenHeight / 2f; cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception){}; cursorView?.visibility = View.VISIBLE; updateBorderColor(0x55FFFFFF.toInt()); showToast("Target: Local (Display $currentDisplayId)"); updateWakeLockState() }
        else { inputTargetDisplayId = nextId; updateTargetMetrics(nextId); createRemoteCursor(nextId); updateVirtualMirrorMode(); cursorX = targetScreenWidth / 2f; cursorY = targetScreenHeight / 2f; remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception){}; cursorView?.visibility = View.GONE; updateBorderColor(0xFFFF00FF.toInt()); showToast("Target: Display $nextId"); updateWakeLockState() }
    }

    // =================================================================================
    // VIRTUAL DISPLAY KEEP-ALIVE: Wake Lock Management
    // SUMMARY: Acquires/releases a SCREEN_BRIGHT wake lock when targeting a remote display.
    //          This prevents the system from timing out the display during active use.
    //          Called when cycling target display or when inputTargetDisplayId changes.
    // =================================================================================
    private fun acquireDisplayWakeLock() {
        if (displayWakeLock?.isHeld == true) return // Already held
        try {
            displayWakeLock = powerManager?.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "DroidOS:VirtualDisplayKeepAlive"
            )
            displayWakeLock?.acquire(60 * 60 * 1000L) // 1 hour max, will release manually
            Log.d(TAG, "Display wake lock ACQUIRED for remote display")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to acquire display wake lock", e)
        }
    }

    private fun releaseDisplayWakeLock() {
        try {
            if (displayWakeLock?.isHeld == true) {
                displayWakeLock?.release()
                Log.d(TAG, "Display wake lock RELEASED")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to release display wake lock", e)
        }
        displayWakeLock = null
    }

    private fun pingUserActivity() {
        val now = SystemClock.uptimeMillis()
        if (now - lastUserActivityPing < USER_ACTIVITY_PING_INTERVAL_MS) return
        lastUserActivityPing = now

        // Ping the power manager via shell to reset screen timeout
        Thread {
            try {
                // This simulates user activity on display 0 (main display)
                // Even when using virtual display, we want to keep the physical display awake
                shellService?.runCommand("input keyevent --longpress 0") // KEYCODE_UNKNOWN - no visible effect
            } catch (e: Exception) {
                Log.e(TAG, "Failed to ping user activity", e)
            }
        }.start()
    }

    private fun updateWakeLockState() {
        if (inputTargetDisplayId != currentDisplayId && inputTargetDisplayId >= 0) {
            // Targeting remote/virtual display - acquire wake lock
            acquireDisplayWakeLock()
        } else {
            // Targeting local display - release wake lock
            releaseDisplayWakeLock()
        }
    }
    // =================================================================================
    // END BLOCK: VIRTUAL DISPLAY KEEP-ALIVE Wake Lock Management
    // =================================================================================

    private fun createRemoteCursor(displayId: Int) { try { removeRemoteCursor(); val display = displayManager?.getDisplay(displayId) ?: return; val remoteContext = createTrackpadDisplayContext(display); remoteWindowManager = remoteContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager; remoteCursorLayout = FrameLayout(remoteContext); remoteCursorView = ImageView(remoteContext); remoteCursorView?.setImageResource(R.drawable.ic_cursor); val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; remoteCursorLayout?.addView(remoteCursorView, FrameLayout.LayoutParams(size, size)); remoteCursorParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT); remoteCursorParams.gravity = Gravity.TOP or Gravity.LEFT; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); remoteCursorParams.x = metrics.widthPixels / 2; remoteCursorParams.y = metrics.heightPixels / 2; remoteWindowManager?.addView(remoteCursorLayout, remoteCursorParams) } catch (e: Exception) { e.printStackTrace() } }
    private fun removeRemoteCursor() { try { if (remoteCursorLayout != null && remoteWindowManager != null) { remoteWindowManager?.removeView(remoteCursorLayout) } } catch (e: Exception) {}; remoteCursorLayout = null; remoteCursorView = null; remoteWindowManager = null }

    // =================================================================================
    // VIRTUAL MIRROR MODE FUNCTIONS
    // =================================================================================

    /**
     * Creates or removes the mirror keyboard based on Virtual Mirror Mode preference.
     * Called when the preference changes or when switching displays.
     */
    private fun updateVirtualMirrorMode() {
        if (prefs.prefVirtualMirrorMode && inputTargetDisplayId != currentDisplayId) {
            createMirrorKeyboard(inputTargetDisplayId)
        } else {
            removeMirrorKeyboard()
        }
    }

// =================================================================================
    // FUNCTION: createMirrorKeyboard
    // SUMMARY: Creates a transparent keyboard mirror on the remote display.
    //          Stores dimensions for coordinate scaling between physical and mirror.
    //          FIX: Container has NO background - KeyboardView's own #1A1A1A background
    //          ensures tight wrapping. Uses OnLayoutChangeListener to track actual
    //          KeyboardView dimensions for accurate touch sync.
    // =================================================================================
    private fun createMirrorKeyboard(displayId: Int) {
        try {
            removeMirrorKeyboard()

            val display = displayManager?.getDisplay(displayId) ?: return
            val mirrorContext = createTrackpadDisplayContext(display)

            mirrorWindowManager = mirrorContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            mirrorKeyboardContainer = FrameLayout(mirrorContext)
            // FIX: NO background on container - let KeyboardView's own background show
            mirrorKeyboardContainer?.setBackgroundColor(Color.TRANSPARENT)
            mirrorKeyboardContainer?.alpha = 0f // Start fully invisible


            // Create KeyboardView for the mirror
            mirrorKeyboardView = KeyboardView(mirrorContext, null, 0)
            mirrorKeyboardView?.setMirrorMode(true) // Disable internal logic
            mirrorKeyboardView?.alpha = 0f // Start fully invisible


            // Apply same scale as physical keyboard
            val scale = prefs.prefKeyScale / 100f
            mirrorKeyboardView?.setScale(scale)

            // Create SwipeTrailView for orientation trail - ORANGE
            mirrorTrailView = SwipeTrailView(mirrorContext)
            mirrorTrailView?.setTrailColor(0xFFFF9900.toInt())

            // Layout params for views - KeyboardView uses WRAP_CONTENT to size naturally
            val kbParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            val trailParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

            mirrorKeyboardContainer?.addView(mirrorKeyboardView, kbParams)
            mirrorKeyboardContainer?.addView(mirrorTrailView, trailParams)

            // Get display metrics
            val metrics = android.util.DisplayMetrics()
            display.getRealMetrics(metrics)

            // Calculate mirror keyboard size - use saved or default width
            val savedWidth = prefs.prefMirrorWidth
            val mirrorWidth = if (savedWidth != -1 && savedWidth > 0) savedWidth else (metrics.widthPixels * 0.95f).toInt()

            // Initialize with placeholder dimensions (will be updated by OnLayoutChangeListener)
            mirrorKbWidth = mirrorWidth.toFloat()
            mirrorKbHeight = 400f

            // Get physical keyboard dimensions
            physicalKbWidth = keyboardOverlay?.getKeyboardView()?.width?.toFloat() ?: 600f
            physicalKbHeight = keyboardOverlay?.getKeyboardView()?.height?.toFloat() ?: 400f

            Log.d(TAG, "Mirror KB init: ${mirrorKbWidth}x${mirrorKbHeight}, Physical KB: ${physicalKbWidth}x${physicalKbHeight}")
// Window params - use saved height or WRAP_CONTENT
            val savedHeight = prefs.prefMirrorHeight
            val mirrorHeight = if (savedHeight != -1 && savedHeight > 0) savedHeight else WindowManager.LayoutParams.WRAP_CONTENT
            
            mirrorKeyboardParams = WindowManager.LayoutParams(
                mirrorWidth,
                mirrorHeight,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )
            mirrorKeyboardParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            mirrorKeyboardParams?.y = 0
            
            // Apply saved position if available
            val savedX = prefs.prefMirrorX
            val savedY = prefs.prefMirrorY
            
            if (savedX != -1) {
                mirrorKeyboardParams?.x = savedX
                mirrorKeyboardParams?.gravity = Gravity.TOP or Gravity.START
            }
            if (savedY != -1) {
                mirrorKeyboardParams?.y = savedY
            }
            
            // Apply saved alpha
            val savedAlpha = prefs.prefMirrorAlpha / 255f
            mirrorKeyboardContainer?.alpha = savedAlpha

            mirrorWindowManager?.addView(mirrorKeyboardContainer, mirrorKeyboardParams)

            // FIX: Track actual KeyboardView dimensions for accurate touch sync
            // This listener fires after layout, giving us the real measured dimensions
            mirrorKeyboardView?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateMirrorSyncDimensions()
            }

            Log.d(TAG, "Mirror keyboard created on display $displayId")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to create mirror keyboard", e)
        }
    }
    // =================================================================================
    // END BLOCK: createMirrorKeyboard
    // =================================================================================

    /**
     * Removes the mirror keyboard overlay.
     */
    private fun removeMirrorKeyboard() {
        try {
            if (mirrorKeyboardContainer != null && mirrorWindowManager != null) {
                mirrorWindowManager?.removeView(mirrorKeyboardContainer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mirrorKeyboardContainer = null
        mirrorKeyboardView = null
        mirrorTrailView = null
        mirrorKeyboardParams = null
        mirrorWindowManager = null

        // Cancel any pending orientation mode timeout
        orientationModeHandler.removeCallbacks(orientationModeTimeout)
        mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)
        isInOrientationMode = false
    }

    // =================================================================================
    // FUNCTION: updateMirrorSyncDimensions
    // SUMMARY: Updates the scaling dimensions used for touch coordinate sync.
    //          Gets actual measured dimensions from both KeyboardViews to ensure
    //          accurate mapping regardless of container sizes or aspect ratios.
    //          Should be called after any layout change on either keyboard.
    // =================================================================================
    private fun updateMirrorSyncDimensions() {
        // Get physical keyboard's actual KeyboardView dimensions
        // Note: These are the dimensions where touch events are reported
        val physicalView = keyboardOverlay?.getKeyboardView()
        if (physicalView != null && physicalView.width > 0 && physicalView.height > 0) {
            physicalKbWidth = physicalView.width.toFloat()
            physicalKbHeight = physicalView.height.toFloat()
        }
        
        // Get mirror keyboard's actual KeyboardView dimensions
        val mirrorView = mirrorKeyboardView
        if (mirrorView != null && mirrorView.width > 0 && mirrorView.height > 0) {
            mirrorKbWidth = mirrorView.width.toFloat()
            mirrorKbHeight = mirrorView.height.toFloat()
        }
        
        Log.d(TAG, "Mirror sync updated: Physical=${physicalKbWidth}x${physicalKbHeight}, Mirror=${mirrorKbWidth}x${mirrorKbHeight}")
    }
    // =================================================================================
    // END BLOCK: updateMirrorSyncDimensions
    // =================================================================================

    // =================================================================================
    // FUNCTION: syncMirrorKeyboardLayer
    // SUMMARY: Syncs keyboard layer (state) from physical to mirror keyboard.
    //          Called when layer changes (shift, symbols, etc.)
    // =================================================================================
    private fun syncMirrorKeyboardLayer(state: KeyboardView.KeyboardState) {
        mirrorKeyboardView?.setKeyboardState(state)

        // Also sync Ctrl/Alt state
        keyboardOverlay?.getCtrlAltState()?.let { (ctrl, alt) ->
            mirrorKeyboardView?.setCtrlAltState(ctrl, alt)
        }
    }
    // =================================================================================
    // END BLOCK: syncMirrorKeyboardLayer
    // =================================================================================

// =================================================================================
    // FUNCTION: onMirrorKeyboardTouch
    // SUMMARY: Virtual Mirror Mode touch handling.
    //          - Every new touch: Show mirror + orange trail
    //          - After timeout: Switch to blue trail, allow typing
    //          - Single taps (quick touch) should also type after orientation
    //          - HOLD REPEAT: If finger stays on backspace/arrow key during orange
    //            trail, the key will repeat. The orientation timeout is CANCELLED
    //            when on a repeatable key so it stays orange and keeps repeating.
    // @return true to block input, false to allow input
    // =================================================================================

    // =================================================================================
    // FUNCTION: onMirrorKeyboardTouch
    // SUMMARY: Virtual Mirror Mode touch handling.
    // OPTIMIZED: Detects Prediction Bar touches on DOWN to start drag.
    // separates Drag logic from Orientation logic to prevent lag.
    // =================================================================================

    // =================================================================================
    // FUNCTION: onMirrorKeyboardTouch
    // SUMMARY: Virtual Mirror Mode touch handling.
    // FIXED: Restored 'handleDeferredTap' so typing works.
    // ADDED: Safe 'Prediction Bar Drag' detection that bypasses Orange logic.
    // =================================================================================


    // =================================================================================
    // FUNCTION: onMirrorKeyboardTouch
    // SUMMARY: Virtual Mirror Mode touch handling.
    // UPDATED: Added 400ms delay to Blue Mode key repeats to prevent mis-swipes.
    // =================================================================================


    fun onMirrorKeyboardTouch(x: Float, y: Float, action: Int): Boolean {
        if (!isVirtualMirrorModeActive()) return false

        val scaleX = if (physicalKbWidth > 0) mirrorKbWidth / physicalKbWidth else 1f
        val scaleY = if (physicalKbHeight > 0) mirrorKbHeight / physicalKbHeight else 1f
        val mirrorX = x * scaleX
        val mirrorY = y * scaleY

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                // ALWAYS START ORANGE
                isMirrorDragActive = false
                isInOrientationMode = true
                lastOrientX = x; lastOrientY = y

                // FIX: STOP PREVIOUS ANIMATIONS IMMEDIATELY
                // Prevents transparency fighting when typing fast
                mirrorKeyboardView?.animate()?.cancel()
                mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)
                
                // Force full visibility
                mirrorKeyboardView?.alpha = 0.9f

                mirrorKeyboardContainer?.alpha = 1f

                keyboardOverlay?.setOrientationMode(true)
                mirrorTrailView?.setTrailColor(0xFFFF9900.toInt())
                keyboardOverlay?.setOrientationTrailColor(0xFFFF9900.toInt())

                mirrorTrailView?.clear()
                keyboardOverlay?.clearOrientationTrail()
                keyboardOverlay?.startOrientationTrail(x, y)
                mirrorTrailView?.addPoint(mirrorX, mirrorY)

                orientationModeHandler.removeCallbacks(orientationModeTimeout)
                orientationModeHandler.postDelayed(orientationModeTimeout, prefs.prefMirrorOrientDelayMs)

                stopMirrorKeyRepeat()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                // 1. DRAG MODE (Blue Trail + Red Backspace)
                if (isMirrorDragActive) {
                     mirrorTrailView?.setTrailColor(0xFF4488FF.toInt())
                     mirrorTrailView?.addPoint(mirrorX, mirrorY)

                     val currentKey = keyboardOverlay?.getKeyAtPosition(x, y)
                     val isBackspace = (currentKey == "BKSP" || currentKey == "⌫" || currentKey == "BACKSPACE")
                     
                     if (isBackspace) isHoveringBackspace = true // Latch

                     if (isBackspace) {
                         mirrorKeyboardView?.highlightKey("BKSP", true, Color.RED)
                     } else {
                         mirrorKeyboardView?.highlightKey("BKSP", false, 0)
                     }

                     val now = SystemClock.uptimeMillis()
                     val event = MotionEvent.obtain(now, now, MotionEvent.ACTION_MOVE, mirrorX, mirrorY, 0)
                     mirrorKeyboardView?.dispatchTouchEvent(event)
                     event.recycle()
                     return true
                }

                // 2. ORANGE MODE
                if (isInOrientationMode) {
                    val dx = x - lastOrientX; val dy = y - lastOrientY
                    if (kotlin.math.sqrt(dx*dx + dy*dy) > MOVEMENT_THRESHOLD) {
                        lastOrientX = x; lastOrientY = y
                        orientationModeHandler.removeCallbacks(orientationModeTimeout)
                        orientationModeHandler.postDelayed(orientationModeTimeout, prefs.prefMirrorOrientDelayMs)
                    }
                    keyboardOverlay?.addOrientationTrailPoint(x, y)
                    mirrorTrailView?.addPoint(mirrorX, mirrorY)
                    return true
                } 
                
                // 3. BLUE MODE
                else {
                    mirrorTrailView?.addPoint(mirrorX, mirrorY)
                    val currentKey = keyboardOverlay?.getKeyAtPosition(x, y)
                    
                    if (currentKey != null && mirrorRepeatableKeys.contains(currentKey)) {
                         if (mirrorRepeatKey == currentKey) return false
                         stopMirrorKeyRepeat()
                         mirrorRepeatKey = currentKey
                         
                         mirrorRepeatHandler.postDelayed({
                             if (mirrorRepeatKey == currentKey && !isInOrientationMode) {
                                 isMirrorRepeating = true
                                 keyboardOverlay?.triggerKeyPress(currentKey)
                                 mirrorRepeatHandler.postDelayed(mirrorRepeatRunnable, MIRROR_REPEAT_INTERVAL)
                             }
                         }, 150) 
                    } else {
                        stopMirrorKeyRepeat()
                    }
                    return false
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 1. END DRAG (Robust Delete)
                if (isMirrorDragActive) {
                     mirrorKeyboardView?.highlightKey("BKSP", false, 0)

                     val upKey = keyboardOverlay?.getKeyAtPosition(x, y)
                     val isDroppedOnBksp = (upKey == "BKSP" || upKey == "⌫" || upKey == "BACKSPACE")
                     

                     if (isHoveringBackspace || isDroppedOnBksp) {
                         // 1. Update Main Logic
                         keyboardOverlay?.blockPrediction(draggedPredictionIndex)
                         
                         // 2. Update Mirror UI Instantly (Make word disappear)
                         mirrorKeyboardView?.removeCandidateAtIndex(draggedPredictionIndex)
                         
                         showToast("Blocked Prediction")
                     } else {

                         val now = SystemClock.uptimeMillis()
                         val event = MotionEvent.obtain(now, now, action, mirrorX, mirrorY, 0)
                         mirrorKeyboardView?.dispatchTouchEvent(event)
                         event.recycle()
                     }
                     
                     mirrorTrailView?.clear()
                     isMirrorDragActive = false
                     isHoveringBackspace = false
                     return true
                }

                // 2. END TOUCH
                orientationModeHandler.removeCallbacks(orientationModeTimeout)
                val wasRepeating = isMirrorRepeating
                stopMirrorKeyRepeat()

                if (isInOrientationMode) {
                    isInOrientationMode = false
                    keyboardOverlay?.setOrientationMode(false)
                    mirrorTrailView?.clear()
                    keyboardOverlay?.clearOrientationTrail()

                    if (!wasRepeating) {
                        keyboardOverlay?.handleDeferredTap(x, y)
                    }
                } else {
                    mirrorTrailView?.clear()
                }

                // Smoothly fade to dim state
                mirrorKeyboardView?.animate()?.alpha(0.3f)?.setDuration(200)?.start()
                mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)
                mirrorFadeHandler.postDelayed(mirrorFadeRunnable, 2000)


                return false
            }
        }
        return isInOrientationMode
    }






    // =================================================================================
    // END BLOCK: onMirrorKeyboardTouch
    // =================================================================================

    // =================================================================================
    // FUNCTION: stopMirrorKeyRepeat
    // SUMMARY: Stops any active mirror mode key repeat. Called on touch up or when
    //          finger moves off the repeatable key.
    // =================================================================================
    private fun stopMirrorKeyRepeat() {
        mirrorRepeatHandler.removeCallbacks(mirrorRepeatRunnable)
        mirrorRepeatKey = null
        isMirrorRepeating = false
    }
    // =================================================================================
    // END BLOCK: stopMirrorKeyRepeat
    // =================================================================================    // =================================================================================
    // FUNCTION: clearMirrorTrail
    // SUMMARY: Clears the orange orientation trail from the mirror keyboard display.
    // =================================================================================
    private fun clearMirrorTrail() {
        mirrorTrailView?.clear()
    }
    // =================================================================================
    // END BLOCK: clearMirrorTrail
    // =================================================================================

    /**
     * Returns true if Virtual Mirror Mode is currently active.
     */
    private fun isVirtualMirrorModeActive(): Boolean {
        return prefs.prefVirtualMirrorMode &&
               inputTargetDisplayId != currentDisplayId &&
               mirrorKeyboardView != null
    }

    /**
     * Returns true if currently in orientation mode (showing orange trail, blocking input).
     */
    fun isCurrentlyInOrientationMode(): Boolean {
        return isInOrientationMode
    }

    // =================================================================================
    // FUNCTION: toggleVirtualMirrorMode
    // SUMMARY: Enhanced toggle that automatically:
    //          - When ON: Saves current state, switches to virtual display, shows
    //            keyboard and trackpad, loads mirror mode profile
    //          - When OFF: Saves mirror mode profile, restores previous visibility
    //            state, switches back to local display, loads normal profile
    // =================================================================================


    fun toggleVirtualMirrorMode() {
        // 1. Save CURRENT state (before switching)
        // This correctly saves to the OLD profile (VM or STD) before we flip the switch.
        saveLayout() 

        val wasEnabled = prefs.prefVirtualMirrorMode
        prefs.prefVirtualMirrorMode = !wasEnabled

        if (prefs.prefVirtualMirrorMode) {
            // === ENTERING MIRROR MODE ===
            android.util.Log.d(TAG, "Entering Virtual Mirror Mode")

            // Store state for smart-toggle
            preMirrorTrackpadVisible = isTrackpadVisible
            preMirrorKeyboardVisible = isCustomKeyboardVisible
            preMirrorTargetDisplayId = inputTargetDisplayId

            // Switch to virtual display
            val displays = displayManager?.displays ?: emptyArray()
            var targetDisplay: Display? = null
            for (d in displays) { if (d.displayId != currentDisplayId && d.displayId >= 2) { targetDisplay = d; break } }
            if (targetDisplay == null) { for (d in displays) { if (d.displayId != currentDisplayId) { targetDisplay = d; break } } }

            if (targetDisplay != null) {
                inputTargetDisplayId = targetDisplay.displayId
                updateTargetMetrics(inputTargetDisplayId)
                createRemoteCursor(inputTargetDisplayId)
                cursorX = targetScreenWidth / 2f; cursorY = targetScreenHeight / 2f
                remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt()
                try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {}
                cursorView?.visibility = View.GONE
                updateBorderColor(0xFFFF00FF.toInt())

                // Ensure visibility
                if (!isTrackpadVisible) toggleTrackpad()
                if (!isCustomKeyboardVisible) toggleCustomKeyboard(suppressAutomation = true)

                // 2. Load MIRROR Profile (Pref is now true, so this loads VM profile)
                loadLayout() 
                updateVirtualMirrorMode()

                showToast("Mirror Mode ON")
                
                val intentCycle = Intent("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
                intentCycle.setPackage("com.katsuyamaki.DroidOSLauncher")
                sendBroadcast(intentCycle)
            } else {
                prefs.prefVirtualMirrorMode = false
                showToast("No virtual display found.")
            }

        } else {
            // === EXITING MIRROR MODE ===
            android.util.Log.d(TAG, "Exiting Virtual Mirror Mode")

            // [FIX] REMOVED the redundant saveLayout() here.
            // It was overwriting the Standard Profile with the active VM layout 
            // because prefVirtualMirrorMode was already set to false above.

            removeMirrorKeyboard()
            inputTargetDisplayId = currentDisplayId
            targetScreenWidth = uiScreenWidth
            targetScreenHeight = uiScreenHeight
            removeRemoteCursor()
            cursorX = uiScreenWidth / 2f; cursorY = uiScreenHeight / 2f
            cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt()
            try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {}
            cursorView?.visibility = View.VISIBLE
            updateBorderColor(0x55FFFFFF.toInt())
            
            // 3. Load STANDARD Profile (Pref is now false, so this loads STD profile)
            loadLayout() 

            showToast("Mirror Mode OFF")
            
            val intentCycle = Intent("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
            intentCycle.setPackage("com.katsuyamaki.DroidOSLauncher")
            sendBroadcast(intentCycle)
        }
        
        savePrefs()
    }


    // =================================================================================
    // END BLOCK: FUNCTION toggleVirtualMirrorMode
    // =================================================================================

    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE FUNCTIONS
    // =================================================================================

    private fun startTouchDrag() { if (ignoreTouchSequence || isTouchDragging) return; isTouchDragging = true; activeDragButton = MotionEvent.BUTTON_PRIMARY; dragDownTime = SystemClock.uptimeMillis(); injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, dragDownTime); hasSentTouchDown = true; if (prefs.prefVibrate) vibrate(); updateBorderColor(0xFFFF9900.toInt()) }
    private fun startResize() {}
    private fun startMove() {}
    private fun startKeyDrag(button: Int) { vibrate(); updateBorderColor(0xFF00FF00.toInt()); isKeyDragging = true; activeDragButton = button; dragDownTime = SystemClock.uptimeMillis(); injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime); hasSentMouseDown = true }
    private fun stopKeyDrag(button: Int) { if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt()); isKeyDragging = false; if (hasSentMouseDown) { injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime) }; hasSentMouseDown = false }
    private fun handleManualAdjust(intent: Intent) {
        val target = intent.getStringExtra("TARGET") ?: "TRACKPAD"
        val isResize = intent.hasExtra("DW")
        val dx = intent.getIntExtra(if(isResize) "DW" else "DX", 0)
        val dy = intent.getIntExtra(if(isResize) "DH" else "DY", 0)
        manualAdjust(target == "KEYBOARD", isResize, dx, dy)
    }
    private fun handlePreview(intent: Intent) {
        val target = intent.getStringExtra("TARGET") ?: return
        val value = intent.getIntExtra("VALUE", 0)
        when(target) {
            "cursor_size" -> { prefs.prefCursorSize = value; updateCursorSize() }
            "alpha" -> { prefs.prefAlpha = value; updateBorderColor(currentBorderColor) }
            "handle_size" -> { prefs.prefHandleSize = value; updateHandleSize() }
            "scroll_visual" -> { prefs.prefScrollVisualSize = value; updateScrollSize() }
            "handle_touch" -> { prefs.prefHandleTouchSize = value; updateLayoutSizes() }
            "scroll_touch" -> { prefs.prefScrollTouchSize = value; updateScrollSize() }
            "keyboard_scale" -> { prefs.prefKeyScale = value; keyboardOverlay?.updateScale(value / 100f) }
        }
    }
    private fun setTrackpadVisibility(visible: Boolean) { isTrackpadVisible = visible; if (trackpadLayout != null) trackpadLayout?.visibility = if (visible) View.VISIBLE else View.GONE }
    private fun setPreviewMode(preview: Boolean) { isPreviewMode = preview; trackpadLayout?.alpha = if (preview) 0.5f else 1.0f }
    override fun onDisplayAdded(displayId: Int) {}
    override fun onDisplayRemoved(displayId: Int) {}
    override fun onDisplayChanged(displayId: Int) {
        // =================================================================================
        // VIRTUAL DISPLAY PROTECTION
        // SUMMARY: Skip auto-switch logic when targeting a virtual display (ID >= 2).
        //          This prevents the overlay from "crashing" back to physical screens
        //          when display states flicker during virtual display use.
        // =================================================================================
        if (inputTargetDisplayId >= 2) {
            Log.d(TAG, "onDisplayChanged: Ignoring - targeting virtual display $inputTargetDisplayId")
            return
        }
        // =================================================================================
        // END BLOCK: VIRTUAL DISPLAY PROTECTION
        // =================================================================================

        // We only monitor the Main Screen (0) state changes to determine "Open/Closed"
        if (displayId == 0) {
            val display = displayManager?.getDisplay(0)
            val isDebounced = (System.currentTimeMillis() - lastManualSwitchTime > 5000)
            
            if (display != null && isDebounced) {
                // CASE A: Phone Opened (Display 0 turned ON) -> Move to Main (0)
                if (display.state == Display.STATE_ON && currentDisplayId != 0) {
                    handler.postDelayed({
                        try {
                            if (System.currentTimeMillis() - lastManualSwitchTime > 5000) {
                                setupUI(0)
                                resetBubblePosition()
                                // showToast("Phone Opened: Moved to Main Screen") // Removed debug toast
                            }
                        } catch(e: Exception) {}
                    }, 500)
                }

                // CASE B: Phone Closed (Display 0 turned OFF/DOZE) -> Move to Cover (1)
                else if (display.state != Display.STATE_ON && currentDisplayId == 0) {
                    handler.postDelayed({
                        try {
                            // Double-check state (ensure it didn't just flicker)
                            val d0 = displayManager?.getDisplay(0)
                            if (d0?.state != Display.STATE_ON && 
                                System.currentTimeMillis() - lastManualSwitchTime > 5000) {
                                
                                // [FIX] Only switch if Display 1 actually exists!
                                // This prevents the UI from disappearing on single-screen devices (Beam Pro)
                                // where setupUI(1) would remove the views but fail to re-add them.
                                if (displayManager?.getDisplay(1) != null) {
                                    setupUI(1)
                                    // We don't reset bubble pos here to avoid it jumping if you just locked the screen
                                    // But we do ensure menu is hidden if it was open
                                    menuManager?.hide()
                                }
                            }
                        } catch(e: Exception) {}
                    }, 500)
                }

            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(commandReceiver)
        } catch (e: Exception) {
            // Ignore if not registered
        }

        // VIRTUAL DISPLAY KEEP-ALIVE: Release wake lock on destroy
        releaseDisplayWakeLock()
        // =================================================================================
        // END BLOCK: VIRTUAL DISPLAY KEEP-ALIVE onDestroy cleanup
        // =================================================================================
        inputExecutor.shutdownNow() // Stop the worker thread
        if (Build.VERSION.SDK_INT >= 24) { try { softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO } catch (e: Exception) {} }
        Thread { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") }.start()
        try { unregisterReceiver(switchReceiver) } catch(e: Exception){};
        if (isBound) ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection)
    }

    fun forceSystemKeyboardVisible() {
        Thread {
            try {
                // Force setting to 1 (Show)
                shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1")
            } catch(e: Exception) {}
        }.start()
    }

    private fun showToast(msg: String) { handler.post { android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show() } }
    private fun updateTargetMetrics(displayId: Int) { val display = displayManager?.getDisplay(displayId) ?: return; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); targetScreenWidth = metrics.widthPixels; targetScreenHeight = metrics.heightPixels }
    

    fun injectKeyFromKeyboard(keyCode: Int, metaState: Int) {
        // NEW: dismiss Voice if active
        checkAndDismissVoice()

        // Submit to the sequential queue instead of spinning a random thread
        inputExecutor.execute {
            try {
                // 1. CHECK ACTUAL SYSTEM STATE
                val currentIme = android.provider.Settings.Secure.getString(contentResolver, "default_input_method") ?: ""
                val isNullKeyboardActive = currentIme.contains(packageName) && currentIme.contains("NullInputMethodService")

                if (isNullKeyboardActive) {
                    // STRATEGY A: NATIVE (Cleanest)
                    val intent = Intent("com.example.coverscreentester.INJECT_KEY")
                    intent.setPackage(packageName)
                    intent.putExtra("keyCode", keyCode)
                    intent.putExtra("metaState", metaState)
                    sendBroadcast(intent)
                } else {
                    // STRATEGY B: SHELL INJECTION (Fallback)
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_DOWN, metaState, inputTargetDisplayId, 1)
                    // Small delay only needed for shell to ensure UP registers after DOWN
                    Thread.sleep(10)
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_UP, metaState, inputTargetDisplayId, 1)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Key injection failed", e)
            }
        }
    }

    fun injectText(text: String) {
        inputExecutor.execute {
            try {
                // 1. CHECK ACTUAL SYSTEM STATE
                val currentIme = android.provider.Settings.Secure.getString(contentResolver, "default_input_method") ?: ""
                val isNullKeyboardActive = currentIme.contains(packageName) && currentIme.contains("NullInputMethodService")

                if (isNullKeyboardActive) {
                    // STRATEGY A: NATIVE (Cleanest)
                    val intent = Intent("com.example.coverscreentester.INJECT_TEXT")
                    intent.setPackage(packageName)
                    intent.putExtra("text", text)
                    sendBroadcast(intent)
                } else {
                    // STRATEGY B: SHELL INJECTION (Fallback)
                    val escapedText = text.replace("\"", "\\\"")
                    val cmd = "input -d $inputTargetDisplayId text \"$escapedText\""
                    shellService?.runCommand(cmd)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Text injection failed", e)
            }
        }
    }

    fun switchDisplay() {
        // ACTION: Smart Toggle
        // If we are on Main (0) or Cover (1), we assume we want to go to Virtual.
        if (currentDisplayId == 0 || currentDisplayId == 1) {
            
            // 1. Broadcast: Create Virtual Display
            val intentToggle = Intent("com.katsuyamaki.DroidOSLauncher.TOGGLE_VIRTUAL_DISPLAY")
            intentToggle.setPackage("com.katsuyamaki.DroidOSLauncher")
            sendBroadcast(intentToggle)

            showToast("Initializing Virtual Display...")

            // 2. Wait for display creation, then Cycle
            handler.postDelayed({
                 val intentCycle = Intent("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
                 intentCycle.setPackage("com.katsuyamaki.DroidOSLauncher")
                 sendBroadcast(intentCycle)
            }, 1000) // Increased wait time to 1s to be safe
            
        } else {
            // If we are already on Virtual (or other), just cycle back to Main
            val intentCycle = Intent("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
            intentCycle.setPackage("com.katsuyamaki.DroidOSLauncher")
            sendBroadcast(intentCycle)
        }
    }
}
