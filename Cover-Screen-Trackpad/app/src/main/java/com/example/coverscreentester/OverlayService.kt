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
        var prefKeyScale = 100 
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
        
        var hardkeyVolUpTap = "left_click"
        var hardkeyVolUpDouble = "none"
        var hardkeyVolUpHold = "left_click"
        var hardkeyVolDownTap = "right_click"
        var hardkeyVolDownDouble = "display_toggle"
        var hardkeyVolDownHold = "alt_position"
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
    private var lastOrientX = 0f
    private var lastOrientY = 0f
    private val MOVEMENT_THRESHOLD = 15f  // Pixels - ignore movement smaller than this
    private var orientationModeHandler = Handler(Looper.getMainLooper())
    private var lastOrientationTouchTime = 0L

    // =================================================================================
    // RUNNABLE: orientationModeTimeout
    // SUMMARY: Fires when finger has been still for delay period.
    //          Switches from orange trail to blue trail.
    //          Initializes swipe tracking so path collection starts NOW.
    // =================================================================================
    private val orientationModeTimeout = Runnable {
        Log.d(TAG, ">>> TIMEOUT - switching to BLUE trail <<<")

        isInOrientationMode = false

        // Clear orange trails
        mirrorTrailView?.clear()
        keyboardOverlay?.clearOrientationTrail()

        // Exit orientation mode
        keyboardOverlay?.setOrientationMode(false)

        // Set BLUE trail color for typing phase
        mirrorTrailView?.setTrailColor(0xFF4488FF.toInt())  // Blue

        // Start swipe tracking from current position
        keyboardOverlay?.startSwipeFromCurrentPosition(lastOrientX, lastOrientY)

        // Add first point to blue trail on mirror
        val scaleX = if (physicalKbWidth > 0) mirrorKbWidth / physicalKbWidth else 1f
        val scaleY = if (physicalKbHeight > 0) mirrorKbHeight / physicalKbHeight else 1f
        mirrorTrailView?.addPoint(lastOrientX * scaleX, lastOrientY * scaleY)

        // Keep mirror visible
        mirrorKeyboardView?.alpha = 0.7f
        mirrorKeyboardContainer?.setBackgroundColor(0x60000000)
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
    private val switchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "SWITCH_DISPLAY" -> switchDisplay() 
                "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                "RESET_CURSOR" -> resetCursorCenter()
                "TOGGLE_DEBUG" -> toggleDebugMode()
                "FORCE_KEYBOARD" -> toggleCustomKeyboard()
                "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
                "OPEN_MENU" -> { menuManager?.show(); enforceZOrder() }
                "SET_TRACKPAD_VISIBILITY" -> {
                    val visible = intent.getBooleanExtra("VISIBLE", true)
                    val menuDisplayId = intent.getIntExtra("MENU_DISPLAY_ID", -1)
                    if (visible) setTrackpadVisibility(true) 
                    else { if (menuDisplayId == -1 || menuDisplayId == currentDisplayId) setTrackpadVisibility(false) }
                }
                "SET_PREVIEW_MODE" -> setPreviewMode(intent.getBooleanExtra("PREVIEW_MODE", false))
                
                // ACTION: Voice Input Triggered
                "VOICE_TYPE_TRIGGERED" -> {
                    isVoiceActive = true
                    
                    // UPDATE UI: Turn Mic Green
                    keyboardOverlay?.setVoiceActive(true)
                    
                    // Force overlay to be NOT focusable (Termux Focus Fix)
                    setOverlayFocusable(false)

                    // Click the input field (Focus Wakeup)
                    handler.postDelayed({ attemptRefocusInput() }, 300)
                }
                
                Intent.ACTION_SCREEN_ON -> triggerAggressiveBlocking()
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
            addAction("CYCLE_INPUT_TARGET")
            addAction("RESET_CURSOR")
            addAction("TOGGLE_DEBUG")
            addAction("FORCE_KEYBOARD")
            addAction("TOGGLE_CUSTOM_KEYBOARD")
            addAction("SET_TRACKPAD_VISIBILITY")
            addAction("SET_PREVIEW_MODE") 
            addAction("OPEN_MENU")
            addAction("VOICE_TYPE_TRIGGERED") // <--- Add this
            addAction(Intent.ACTION_SCREEN_ON)
        }
        ContextCompat.registerReceiver(this, switchReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        
        if (Build.VERSION.SDK_INT >= 24) {
            try { softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO } catch(e: Exception){}
        }
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

            when (intent?.action) {
                "SWITCH_DISPLAY" -> switchDisplay()
                "RESET_POSITION" -> {
                    val target = intent.getStringExtra("TARGET") ?: "TRACKPAD"
                    if (target == "KEYBOARD") {
                        keyboardOverlay?.resetPosition()
                    } else {
                        resetTrackpadPosition()
                    }
                }
                "ROTATE" -> {
                    val target = intent.getStringExtra("TARGET") ?: "TRACKPAD"
                    if (target == "KEYBOARD") {
                        keyboardOverlay?.cycleRotation()
                    } else {
                        performRotation()
                    }
                }
                "SAVE_LAYOUT" -> saveLayout()
                "LOAD_LAYOUT" -> loadLayout()
                "DELETE_PROFILE" -> deleteCurrentProfile()
                "MANUAL_ADJUST" -> handleManualAdjust(intent)
                "RELOAD_PREFS" -> {
                    loadPrefs()
                    updateBorderColor(currentBorderColor)
                    updateLayoutSizes()
                    updateScrollPosition()
                    updateCursorSize()
                    keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha)
                    if (isCustomKeyboardVisible) { toggleCustomKeyboard(); toggleCustomKeyboard() }
                }
                "PREVIEW_UPDATE" -> handlePreview(intent)
                "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                "RESET_CURSOR" -> resetCursorCenter()
                "TOGGLE_DEBUG" -> toggleDebugMode()
                "FORCE_KEYBOARD" -> toggleCustomKeyboard()
                "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
                "OPEN_MENU" -> menuManager?.show()
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
    
    fun getSavedProfileList(): List<String> {
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val allKeys = p.all.keys
        val profiles = java.util.HashSet<String>()
        val regex = Regex("X_P_(\\d+)_(\\d+)")
        for (key in allKeys) { val match = regex.find(key); if (match != null) profiles.add("${match.groupValues[1]} x ${match.groupValues[2]}") }
        return profiles.sorted()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (!prefs.prefPersistentService) forceExit()
    }

    fun forceExit() { try { stopSelf(); Process.killProcess(Process.myPid()) } catch (e: Exception) { e.printStackTrace() } }
    
    fun manualAdjust(isKeyboard: Boolean, isResize: Boolean, dx: Int, dy: Int) { 
        if (isKeyboard) { if (isResize) keyboardOverlay?.resizeWindow(dx, dy) else keyboardOverlay?.moveWindow(dx, dy) } 
        else { 
            if (trackpadLayout == null) return
            if (isResize) { trackpadParams.width = max(200, trackpadParams.width + dx); trackpadParams.height = max(200, trackpadParams.height + dy) } 
            else { trackpadParams.x += dx; trackpadParams.y += dy }
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
            "anchored" -> prefs.prefAnchored = parseBoolean(value)
            "automation_enabled" -> prefs.prefAutomationEnabled = parseBoolean(value)
            "bubble_size" -> updateBubbleSize(value as Int)
            "bubble_icon" -> cycleBubbleIcon()
            "bubble_alpha" -> updateBubbleAlpha(value as Int)
            "persistent_service" -> prefs.prefPersistentService = parseBoolean(value)
            "block_soft_kb" -> { prefs.prefBlockSoftKeyboard = parseBoolean(value); setSoftKeyboardBlocking(prefs.prefBlockSoftKeyboard) }
            "keyboard_alpha" -> { prefs.prefKeyboardAlpha = value as Int; keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha) }
            "keyboard_key_scale" -> { prefs.prefKeyScale = value as Int; keyboardOverlay?.updateScale(prefs.prefKeyScale / 100f) }
            "hardkey_vol_up_tap" -> prefs.hardkeyVolUpTap = value as String
            "hardkey_vol_up_double" -> prefs.hardkeyVolUpDouble = value as String
            "hardkey_vol_up_hold" -> prefs.hardkeyVolUpHold = value as String
            "hardkey_vol_down_tap" -> prefs.hardkeyVolDownTap = value as String
            "hardkey_vol_down_double" -> prefs.hardkeyVolDownDouble = value as String
            "hardkey_vol_down_hold" -> prefs.hardkeyVolDownHold = value as String
            "double_tap_ms" -> prefs.doubleTapMs = value as Int
            "hold_duration_ms" -> prefs.holdDurationMs = value as Int
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
        val kbHeight = (275f * (prefs.prefKeyScale / 100f) * density).toInt().coerceAtMost((h * 0.6f).toInt())
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
        prefs.prefKeyScale = p.getInt("keyboard_key_scale", 135) // CHANGED: Default 135
        prefs.prefUseAltScreenOff = p.getBoolean("use_alt_screen_off", true)
        prefs.prefAutomationEnabled = p.getBoolean("automation_enabled", false) // CHANGED: Default false
        prefs.prefBubbleX = p.getInt("bubble_x", 50)
        prefs.prefBubbleY = p.getInt("bubble_y", 300)
        prefs.prefBubbleSize = p.getInt("bubble_size", 100)
        prefs.prefBubbleIconIndex = p.getInt("bubble_icon_index", 0)
        prefs.prefBubbleAlpha = p.getInt("bubble_alpha", 255)
        
        prefs.prefPersistentService = p.getBoolean("persistent_service", false)
        prefs.prefBlockSoftKeyboard = p.getBoolean("block_soft_kb", false)
        
        prefs.hardkeyVolUpTap = p.getString("hardkey_vol_up_tap", "left_click") ?: "left_click"
        prefs.hardkeyVolUpDouble = p.getString("hardkey_vol_up_double", "left_click") ?: "left_click" // CHANGED: left_click
        prefs.hardkeyVolUpHold = p.getString("hardkey_vol_up_hold", "left_click") ?: "left_click"
        prefs.hardkeyVolDownTap = p.getString("hardkey_vol_down_tap", "toggle_keyboard") ?: "toggle_keyboard" // CHANGED: toggle_keyboard
        prefs.hardkeyVolDownDouble = p.getString("hardkey_vol_down_double", "open_menu") ?: "open_menu" // CHANGED: open_menu
        prefs.hardkeyVolDownHold = p.getString("hardkey_vol_down_hold", "action_back") ?: "action_back" // CHANGED: action_back
        prefs.hardkeyPowerDouble = p.getString("hardkey_power_double", "none") ?: "none"
        
        prefs.doubleTapMs = p.getInt("double_tap_ms", 300)
        prefs.holdDurationMs = p.getInt("hold_duration_ms", 400)

        // =================================================================================
        // VIRTUAL MIRROR MODE LOAD
        // =================================================================================
        prefs.prefVirtualMirrorMode = p.getBoolean("virtual_mirror_mode", false)
        prefs.prefMirrorOrientDelayMs = p.getLong("mirror_orient_delay_ms", 1000L)
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR MODE LOAD
        // =================================================================================
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

        // FIX: Restore Saved Layout (fixes reset/aspect ratio issue)
        if (savedKbW > 0 && savedKbH > 0) {
            keyboardOverlay?.updatePosition(savedKbX, savedKbY)
            keyboardOverlay?.updateSize(savedKbW, savedKbH)
        } else {
            // Sane Defaults: Bottom 45% of screen
            val defaultH = (uiScreenHeight * 0.45f).toInt()
            keyboardOverlay?.updatePosition(0, uiScreenHeight - defaultH)
            keyboardOverlay?.updateSize(uiScreenWidth, defaultH)
        }
    }




    fun toggleCustomKeyboard(suppressAutomation: Boolean = false) {
        if (keyboardOverlay == null) initCustomKeyboard()
        val isNowVisible = if (keyboardOverlay?.isShowing() == true) { keyboardOverlay?.hide(); false } else { keyboardOverlay?.show(); true }
        isCustomKeyboardVisible = isNowVisible
        enforceZOrder()
        if (prefs.prefAutomationEnabled && !suppressAutomation) { if (isNowVisible) turnScreenOn() else turnScreenOff() }
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

        // Input Injection
        if (isDragging) {
             // TOUCH DRAG: SOURCE_TOUCHSCREEN + ACTION_MOVE
             injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, 0, SystemClock.uptimeMillis())
        } else {
             // MOUSE HOVER: SOURCE_MOUSE + ACTION_HOVER_MOVE
             injectAction(MotionEvent.ACTION_HOVER_MOVE, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
        }
    }

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
    fun getProfileKey(): String = "P_${uiScreenWidth}_${uiScreenHeight}"

    // =================================================================================
    // VIRTUAL MIRROR MODE PROFILE KEY
    // SUMMARY: Returns a unique profile key for mirror mode, separate from normal
    //          display-based profiles. This allows mirror mode to have its own
    //          trackpad/keyboard layout that persists independently.
    // =================================================================================
    fun getMirrorModeProfileKey(): String = "MIRROR_MODE_${uiScreenWidth}_${uiScreenHeight}"
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE PROFILE KEY
    // =================================================================================

    // =================================================================================
    // VIRTUAL MIRROR MODE LAYOUT SAVE
    // SUMMARY: Saves the current layout to the mirror mode profile. Called when
    //          exiting mirror mode or when explicitly saving while in mirror mode.
    // =================================================================================
    fun saveMirrorModeLayout() {
        val currentKbX = keyboardOverlay?.getViewX() ?: savedKbX
        val currentKbY = keyboardOverlay?.getViewY() ?: savedKbY
        val currentKbW = keyboardOverlay?.getViewWidth() ?: savedKbW
        val currentKbH = keyboardOverlay?.getViewHeight() ?: savedKbH

        savedKbX = currentKbX; savedKbY = currentKbY; savedKbW = currentKbW; savedKbH = currentKbH
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        val key = getMirrorModeProfileKey()

        p.putInt("X_$key", trackpadParams.x)
        p.putInt("Y_$key", trackpadParams.y)
        p.putInt("W_$key", trackpadParams.width)
        p.putInt("H_$key", trackpadParams.height)

        val kbX = keyboardOverlay?.getViewX() ?: 0
        val kbY = keyboardOverlay?.getViewY() ?: 0
        val kbW = keyboardOverlay?.getViewWidth() ?: 0
        val kbH = keyboardOverlay?.getViewHeight() ?: 0

        // Save settings specific to mirror mode
        p.putString("SETTINGS_$key", "${prefs.cursorSpeed};${prefs.scrollSpeed};${if(prefs.prefTapScroll) 1 else 0};${if(prefs.prefReverseScroll) 1 else 0};${prefs.prefAlpha};${prefs.prefBgAlpha};${prefs.prefKeyboardAlpha};${prefs.prefHandleSize};${prefs.prefHandleTouchSize};${prefs.prefScrollTouchSize};${prefs.prefScrollVisualSize};${prefs.prefCursorSize};${prefs.prefKeyScale};${if(prefs.prefAutomationEnabled) 1 else 0};${if(prefs.prefAnchored) 1 else 0};${prefs.prefBubbleSize};${prefs.prefBubbleAlpha};${prefs.prefBubbleIconIndex};${prefs.prefBubbleX};${prefs.prefBubbleY};${prefs.hardkeyVolUpTap};${prefs.hardkeyVolUpDouble};${prefs.hardkeyVolUpHold};${prefs.hardkeyVolDownTap};${prefs.hardkeyVolDownDouble};${prefs.hardkeyVolDownHold};${prefs.hardkeyPowerDouble};$kbX;$kbY;$kbW;$kbH")

        p.apply()
        Log.d(TAG, "Mirror mode layout saved: $key")
    }
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE LAYOUT SAVE
    // =================================================================================

    // =================================================================================
    // VIRTUAL MIRROR MODE LAYOUT LOAD
    // SUMMARY: Loads the mirror mode profile layout. Called when entering mirror mode.
    //          If no saved profile exists, uses sensible defaults.
    // =================================================================================
    fun loadMirrorModeLayout() {
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val key = getMirrorModeProfileKey()

        // Check if mirror mode profile exists
        val hasSavedLayout = p.contains("X_$key")

        if (hasSavedLayout) {
            trackpadParams.x = p.getInt("X_$key", 100)
            trackpadParams.y = p.getInt("Y_$key", 100)
            trackpadParams.width = p.getInt("W_$key", 400)
            trackpadParams.height = p.getInt("H_$key", 300)
            try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch(e: Exception){}

            val settings = p.getString("SETTINGS_$key", null)
            if (settings != null) {
                val parts = settings.split(";")
                if (parts.size >= 17) {
                    prefs.cursorSpeed = parts[0].toFloat(); prefs.scrollSpeed = parts[1].toFloat()
                    prefs.prefTapScroll = parseBoolean(parts[2]); prefs.prefReverseScroll = parseBoolean(parts[3])
                    prefs.prefAlpha = parts[4].toInt(); prefs.prefBgAlpha = parts[5].toInt()
                    prefs.prefKeyboardAlpha = parts[6].toInt(); prefs.prefHandleSize = parts[7].toInt()
                    prefs.prefHandleTouchSize = parts[8].toInt(); prefs.prefScrollTouchSize = parts[9].toInt()
                    prefs.prefScrollVisualSize = parts[10].toInt(); prefs.prefCursorSize = parts[11].toInt()
                    prefs.prefKeyScale = parts[12].toInt(); prefs.prefAutomationEnabled = parseBoolean(parts[13])
                    prefs.prefAnchored = parseBoolean(parts[14]); prefs.prefBubbleSize = parts[15].toInt()
                    prefs.prefBubbleAlpha = parts[16].toInt()

                    if (parts.size >= 31) {
                        savedKbX = parts[27].toInt()
                        savedKbY = parts[28].toInt()
                        savedKbW = parts[29].toInt()
                        savedKbH = parts[30].toInt()
                        keyboardOverlay?.setWindowBounds(savedKbX, savedKbY, savedKbW, savedKbH)
                    }

                    updateBorderColor(currentBorderColor)
                    updateLayoutSizes()
                    updateScrollSize()
                    updateHandleSize()
                    updateCursorSize()
                    keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha)
                    keyboardOverlay?.updateScale(prefs.prefKeyScale / 100f)
                    keyboardOverlay?.setAnchored(prefs.prefAnchored)
                }
            }
            Log.d(TAG, "Mirror mode layout loaded: $key")
        } else {
            Log.d(TAG, "No saved mirror mode layout, using current settings")
        }
    }
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE LAYOUT LOAD
    // =================================================================================

    fun saveLayout() {
        // =================================================================================
        // MIRROR MODE CHECK: If in mirror mode, save to mirror profile instead
        // =================================================================================
        if (prefs.prefVirtualMirrorMode && inputTargetDisplayId != currentDisplayId) {
            saveMirrorModeLayout()
            return
        }
        // =================================================================================
        // END BLOCK: MIRROR MODE CHECK
        // =================================================================================

        // Cache the current values
        val currentKbX = keyboardOverlay?.getViewX() ?: savedKbX
        val currentKbY = keyboardOverlay?.getViewY() ?: savedKbY
        val currentKbW = keyboardOverlay?.getViewWidth() ?: savedKbW
        val currentKbH = keyboardOverlay?.getViewHeight() ?: savedKbH

        // Update local memory
        savedKbX = currentKbX; savedKbY = currentKbY; savedKbW = currentKbW; savedKbH = currentKbH
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit(); val key = getProfileKey()
        p.putInt("X_$key", trackpadParams.x); p.putInt("Y_$key", trackpadParams.y); p.putInt("W_$key", trackpadParams.width); p.putInt("H_$key", trackpadParams.height)
        val kbX = keyboardOverlay?.getViewX() ?: 0; val kbY = keyboardOverlay?.getViewY() ?: 0; val kbW = keyboardOverlay?.getViewWidth() ?: 0; val kbH = keyboardOverlay?.getViewHeight() ?: 0
        p.putString("SETTINGS_$key", "${prefs.cursorSpeed};${prefs.scrollSpeed};${if(prefs.prefTapScroll) 1 else 0};${if(prefs.prefReverseScroll) 1 else 0};${prefs.prefAlpha};${prefs.prefBgAlpha};${prefs.prefKeyboardAlpha};${prefs.prefHandleSize};${prefs.prefHandleTouchSize};${prefs.prefScrollTouchSize};${prefs.prefScrollVisualSize};${prefs.prefCursorSize};${prefs.prefKeyScale};${if(prefs.prefAutomationEnabled) 1 else 0};${if(prefs.prefAnchored) 1 else 0};${prefs.prefBubbleSize};${prefs.prefBubbleAlpha};${prefs.prefBubbleIconIndex};${prefs.prefBubbleX};${prefs.prefBubbleY};${prefs.hardkeyVolUpTap};${prefs.hardkeyVolUpDouble};${prefs.hardkeyVolUpHold};${prefs.hardkeyVolDownTap};${prefs.hardkeyVolDownDouble};${prefs.hardkeyVolDownHold};${prefs.hardkeyPowerDouble};$kbX;$kbY;$kbW;$kbH")
        p.apply(); showToast("Layout Saved") 
    }

    fun loadLayout() { 
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE); val key = getProfileKey()
        trackpadParams.x = p.getInt("X_$key", 100); trackpadParams.y = p.getInt("Y_$key", 100); trackpadParams.width = p.getInt("W_$key", 400); trackpadParams.height = p.getInt("H_$key", 300)
        try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch(e: Exception){} 
        val settings = p.getString("SETTINGS_$key", null)
        if (settings != null) {
            val parts = settings.split(";")
            if (parts.size >= 17) {
                prefs.cursorSpeed = parts[0].toFloat(); prefs.scrollSpeed = parts[1].toFloat(); prefs.prefTapScroll = parseBoolean(parts[2]); prefs.prefReverseScroll = parseBoolean(parts[3])
                prefs.prefAlpha = parts[4].toInt(); prefs.prefBgAlpha = parts[5].toInt(); prefs.prefKeyboardAlpha = parts[6].toInt(); prefs.prefHandleSize = parts[7].toInt()
                prefs.prefHandleTouchSize = parts[8].toInt(); prefs.prefScrollTouchSize = parts[9].toInt(); prefs.prefScrollVisualSize = parts[10].toInt(); prefs.prefCursorSize = parts[11].toInt()
                prefs.prefKeyScale = parts[12].toInt(); prefs.prefAutomationEnabled = parseBoolean(parts[13]); prefs.prefAnchored = parseBoolean(parts[14]); prefs.prefBubbleSize = parts[15].toInt(); prefs.prefBubbleAlpha = parts[16].toInt()
                if (parts.size >= 27) { prefs.prefBubbleIconIndex = parts[17].toInt(); prefs.prefBubbleX = parts[18].toInt(); prefs.prefBubbleY = parts[19].toInt(); prefs.hardkeyVolUpTap = parts[20]; prefs.hardkeyVolUpDouble = parts[21]; prefs.hardkeyVolUpHold = parts[22]; prefs.hardkeyVolDownTap = parts[23]; prefs.hardkeyVolDownDouble = parts[24]; prefs.hardkeyVolDownHold = parts[25]; prefs.hardkeyPowerDouble = parts[26] }
                else if (parts.size >= 29) { keyboardOverlay?.updatePosition(parts[27].toInt(), parts[28].toInt()) }
                updateBorderColor(currentBorderColor); updateLayoutSizes(); updateScrollSize(); updateHandleSize(); updateCursorSize(); keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha); keyboardOverlay?.updateScale(prefs.prefKeyScale / 100f); keyboardOverlay?.setAnchored(prefs.prefAnchored)
                if (bubbleView != null) { bubbleParams.x = prefs.prefBubbleX; bubbleParams.y = prefs.prefBubbleY; windowManager?.updateViewLayout(bubbleView, bubbleParams); applyBubbleAppearance() }
                if (parts.size >= 31) { 
                    savedKbX = parts[27].toInt()
                    savedKbY = parts[28].toInt()
                    savedKbW = parts[29].toInt()
                    savedKbH = parts[30].toInt()
                } else {
                    // Default if no save exists yet
                    savedKbW = uiScreenWidth
                    savedKbH = (uiScreenHeight * 0.45f).toInt()
                    savedKbX = 0
                    savedKbY = uiScreenHeight - savedKbH
                } 

                // IMPORTANT: Save loaded values to disk so toggling components (Keyboard) recalls correct state
                savePrefs()
                
                showToast("Layout Loaded")
            }
        }
    }
    fun deleteCurrentProfile() { /* Stub */ }
    fun resetKeyboardPosition() {
        keyboardOverlay?.resetPosition()
    }

    fun rotateKeyboard() {
        keyboardOverlay?.cycleRotation()
    }

    fun resetTrackpadPosition() { trackpadParams.x = 100; trackpadParams.y = 100; trackpadParams.width = 400; trackpadParams.height = 300; windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }    fun cycleInputTarget() {
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
    // =================================================================================
    private fun createMirrorKeyboard(displayId: Int) {
        try {
            removeMirrorKeyboard()

            val display = displayManager?.getDisplay(displayId) ?: return
            val mirrorContext = createTrackpadDisplayContext(display)

            mirrorWindowManager = mirrorContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            mirrorKeyboardContainer = FrameLayout(mirrorContext)
            mirrorKeyboardContainer?.setBackgroundColor(0x40000000) // Semi-transparent bg
            mirrorKeyboardContainer?.alpha = 0f // Start fully invisible

            // Create KeyboardView for the mirror
            mirrorKeyboardView = KeyboardView(mirrorContext, null, 0)
            mirrorKeyboardView?.alpha = 0f // Start fully invisible

            // Apply same scale
            val scale = prefs.prefKeyScale / 100f
            mirrorKeyboardView?.setScale(scale)

            // Create SwipeTrailView for orientation trail - ORANGE
            mirrorTrailView = SwipeTrailView(mirrorContext)
            mirrorTrailView?.setTrailColor(0xFFFF9900.toInt())

            // Layout params for views
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

            // Calculate mirror keyboard size
            val mirrorWidth = (metrics.widthPixels * 0.95f).toInt()

            // Store dimensions for coordinate scaling
            mirrorKbWidth = mirrorWidth.toFloat()
            mirrorKbHeight = 400f // Approximate, will be adjusted

            // Get physical keyboard dimensions
            physicalKbWidth = keyboardOverlay?.getViewWidth()?.toFloat() ?: 600f
            physicalKbHeight = keyboardOverlay?.getViewHeight()?.toFloat() ?: 400f

            Log.d(TAG, "Mirror KB: ${mirrorKbWidth}x${mirrorKbHeight}, Physical KB: ${physicalKbWidth}x${physicalKbHeight}")

            // Window params
            mirrorKeyboardParams = WindowManager.LayoutParams(
                mirrorWidth,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )
            mirrorKeyboardParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            mirrorKeyboardParams?.y = 0

            mirrorWindowManager?.addView(mirrorKeyboardContainer, mirrorKeyboardParams)

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
    // SUMMARY: Handles touch events forwarded from physical keyboard.
    //          Scales coordinates to match mirror keyboard dimensions.
    //          Shows orange trail on both displays during orientation mode.
    // =================================================================================
    // =================================================================================
    // FUNCTION: onMirrorKeyboardTouch
    // SUMMARY: Virtual Mirror Mode touch handling.
    //          - Every new touch: Show mirror + orange trail
    //          - After timeout: Switch to blue trail, allow typing
    //          - Single taps (quick touch) should also type after orientation
    // @return true to block input, false to allow input
    // =================================================================================
    fun onMirrorKeyboardTouch(x: Float, y: Float, action: Int): Boolean {
        if (!isVirtualMirrorModeActive()) {
            return false
        }

        // Scale coordinates for mirror display
        val scaleX = if (physicalKbWidth > 0) mirrorKbWidth / physicalKbWidth else 1f
        val scaleY = if (physicalKbHeight > 0) mirrorKbHeight / physicalKbHeight else 1f
        val mirrorX = x * scaleX
        val mirrorY = y * scaleY

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "Mirror touch DOWN - starting ORANGE trail")

                // Cancel any pending fade
                mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)

                // Make mirror VISIBLE on touch
                mirrorKeyboardView?.alpha = 0.9f
                mirrorKeyboardContainer?.alpha = 1f
                mirrorKeyboardContainer?.setBackgroundColor(0x80000000.toInt())

                // Start orientation mode
                isInOrientationMode = true
                keyboardOverlay?.setOrientationMode(true)

                // Store position for movement threshold
                lastOrientX = x
                lastOrientY = y

                // Set ORANGE trail color
                mirrorTrailView?.setTrailColor(0xFFFF9900.toInt())
                keyboardOverlay?.setOrientationTrailColor(0xFFFF9900.toInt())

                // Clear old trails, start new orange trail on BOTH displays
                mirrorTrailView?.clear()
                keyboardOverlay?.clearOrientationTrail()
                keyboardOverlay?.startOrientationTrail(x, y)
                mirrorTrailView?.addPoint(mirrorX, mirrorY)

                // Start timeout
                orientationModeHandler.removeCallbacks(orientationModeTimeout)
                orientationModeHandler.postDelayed(
                    orientationModeTimeout,
                    prefs.prefMirrorOrientDelayMs
                )

                return true  // Block input during orange
            }

            MotionEvent.ACTION_MOVE -> {
                if (isInOrientationMode) {
                    // ORANGE phase
                    val dx = x - lastOrientX
                    val dy = y - lastOrientY
                    val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                    // Draw orange trail
                    keyboardOverlay?.addOrientationTrailPoint(x, y)
                    mirrorTrailView?.addPoint(mirrorX, mirrorY)

                    // Reset timeout only on significant movement
                    if (distance > MOVEMENT_THRESHOLD) {
                        lastOrientX = x
                        lastOrientY = y
                        orientationModeHandler.removeCallbacks(orientationModeTimeout)
                        orientationModeHandler.postDelayed(
                            orientationModeTimeout,
                            prefs.prefMirrorOrientDelayMs
                        )
                    }

                    return true  // Block input
                } else {
                    // BLUE phase - allow input, draw blue trail on mirror
                    mirrorTrailView?.addPoint(mirrorX, mirrorY)
                    return false
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "Mirror touch UP")

                orientationModeHandler.removeCallbacks(orientationModeTimeout)

                val wasInOrientation = isInOrientationMode

                if (isInOrientationMode) {
                    // Quick tap - lifted during orange phase
                    Log.d(TAG, "Quick tap detected - triggering deferred key press")
                    isInOrientationMode = false
                    keyboardOverlay?.setOrientationMode(false)
                    mirrorTrailView?.clear()
                    keyboardOverlay?.clearOrientationTrail()

                    // CRITICAL: Trigger the key press that was deferred
                    keyboardOverlay?.handleDeferredTap(x, y)
                } else {
                    // Normal lift after blue phase - clear mirror trail
                    mirrorTrailView?.clear()
                }

                // Fade mirror
                mirrorKeyboardView?.alpha = 0.3f
                mirrorKeyboardContainer?.setBackgroundColor(0x40000000)

                // Schedule fade out
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
        val wasEnabled = prefs.prefVirtualMirrorMode
        prefs.prefVirtualMirrorMode = !wasEnabled

        if (prefs.prefVirtualMirrorMode) {
            // === ENTERING MIRROR MODE ===
            Log.d(TAG, "Entering Virtual Mirror Mode")

            // 1. Save current state before changes
            preMirrorTrackpadVisible = isTrackpadVisible
            preMirrorKeyboardVisible = isCustomKeyboardVisible
            preMirrorTargetDisplayId = inputTargetDisplayId

            // 2. Save current layout to normal profile before switching
            saveLayout()

            // 3. Find and switch to a virtual/remote display
            val displays = displayManager?.displays ?: emptyArray()
            var targetDisplay: Display? = null
            for (d in displays) {
                if (d.displayId != currentDisplayId && d.displayId >= 2) {
                    // Prefer virtual displays (ID >= 2)
                    targetDisplay = d
                    break
                }
            }
            // Fallback to any non-current display
            if (targetDisplay == null) {
                for (d in displays) {
                    if (d.displayId != currentDisplayId) {
                        targetDisplay = d
                        break
                    }
                }
            }

            if (targetDisplay != null) {
                // Switch cursor target to remote display
                inputTargetDisplayId = targetDisplay.displayId
                updateTargetMetrics(inputTargetDisplayId)
                createRemoteCursor(inputTargetDisplayId)
                cursorX = targetScreenWidth / 2f
                cursorY = targetScreenHeight / 2f
                remoteCursorParams.x = cursorX.toInt()
                remoteCursorParams.y = cursorY.toInt()
                try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {}
                cursorView?.visibility = View.GONE
                updateBorderColor(0xFFFF00FF.toInt())

                // 4. Load mirror mode profile
                loadMirrorModeLayout()

                // 5. Ensure keyboard and trackpad are visible
                if (!isTrackpadVisible) toggleTrackpad()
                if (!isCustomKeyboardVisible) toggleCustomKeyboard(suppressAutomation = true)

                // 6. Create mirror keyboard
                updateVirtualMirrorMode()

                showToast("Mirror Mode ON → Display ${inputTargetDisplayId}")
            } else {
                // No remote display available
                prefs.prefVirtualMirrorMode = false
                showToast("No virtual display found. Enable Virtual Display first.")
            }

        } else {
            // === EXITING MIRROR MODE ===
            Log.d(TAG, "Exiting Virtual Mirror Mode")

            // 1. Save mirror mode layout
            saveMirrorModeLayout()

            // 2. Remove mirror keyboard
            removeMirrorKeyboard()

            // 3. Switch back to local display
            inputTargetDisplayId = currentDisplayId
            targetScreenWidth = uiScreenWidth
            targetScreenHeight = uiScreenHeight
            removeRemoteCursor()
            cursorX = uiScreenWidth / 2f
            cursorY = uiScreenHeight / 2f
            cursorParams.x = cursorX.toInt()
            cursorParams.y = cursorY.toInt()
            try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {}
            cursorView?.visibility = View.VISIBLE
            updateBorderColor(0x55FFFFFF.toInt())

            // 4. Load normal profile
            loadLayout()

            // 5. Restore previous visibility state
            if (isTrackpadVisible != preMirrorTrackpadVisible) toggleTrackpad()
            if (isCustomKeyboardVisible != preMirrorKeyboardVisible) toggleCustomKeyboard(suppressAutomation = true)

            showToast("Mirror Mode OFF → Local Display")
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
                                
                                setupUI(1)
                                // We don't reset bubble pos here to avoid it jumping if you just locked the screen
                                // But we do ensure menu is hidden if it was open
                                menuManager?.hide() 
                                // showToast("Phone Closed: Moved to Cover Screen") // Removed debug toast
                            }
                        } catch(e: Exception) {}
                    }, 500)
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // =================================================================================
        // VIRTUAL DISPLAY KEEP-ALIVE: Release wake lock on destroy
        // =================================================================================
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
    
    fun enforceZOrder() {
        if (windowManager == null) return
        menuManager?.bringToFront()
        if (bubbleView != null && bubbleView!!.isAttachedToWindow) { try { windowManager?.removeView(bubbleView); windowManager?.addView(bubbleView, bubbleParams) } catch (e: Exception) {} }
        if (cursorLayout != null && cursorLayout!!.isAttachedToWindow) { try { windowManager?.removeView(cursorLayout); windowManager?.addView(cursorLayout, cursorParams) } catch (e: Exception) {} }
    }

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
        val targetId = if (currentDisplayId == 0) 1 else 0
        val display = displayManager?.getDisplay(targetId)
        if (display != null) {
            setupUI(targetId)
            // Save as preference so it persists restarts
            prefs.prefBubbleX = 50 // Reset position slightly to ensure visibility
            prefs.prefBubbleY = 300
            savePrefs()
            showToast("Switched to Display $targetId")
        } else {
            showToast("Display $targetId unavailable")
        }
    }
}
