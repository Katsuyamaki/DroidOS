package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.media.AudioManager
import android.media.AudioRecordingConfiguration
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.res.Configuration
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
import android.hardware.input.InputManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Process
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
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
import android.view.VelocityTracker
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import android.os.PowerManager
import java.util.ArrayList
import com.katsuyamaki.DroidOSTrackpadKeyboard.BuildConfig

class OverlayService : AccessibilityService(), DisplayManager.DisplayListener, InputManager.InputDeviceListener {


    private var isAccessibilityReady = false
    private var pendingDisplayId = -1
    // Track last time we injected text to distinguish our events from user touches
    private var lastInjectionTime = 0L


    // === RECEIVER & ACTIONS - START ===
    private val commandReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (::commandDispatcher.isInitialized) {
                commandDispatcher.handleCommand(intent)
            }
        }
    }

    internal fun performSoftRestart() {
        // [FIX] HARD RESET (Process Kill)
        // This is the ONLY method that fixes Z-order relative to the Launcher.
        // We rely on the updated scheduleRestart() (Activity Launch) to bring the app back alive.
        handler.post {
            Toast.makeText(this, "Restarting System...", Toast.LENGTH_SHORT).show()
            
            // 1. Schedule the wake-up alarm
            scheduleRestart()
            
            // 2. Kill the process after a short delay
            handler.postDelayed({
                // Always Restart for Soft Restart command (Ignore preference)
                removeOldViews()
                killProcessAndExit()
            }, 500)
        }
    }

    // =================================================================================
    // FUNCTION: requestKeybindsFromLauncher
    // SUMMARY: Sends a broadcast to the DroidOS Launcher requesting the current list
    //          of registered keybinds. The Launcher will respond with UPDATE_KEYBINDS.
    // =================================================================================
    private fun requestKeybindsFromLauncher() {
        val intent = Intent("com.katsuyamaki.DroidOSLauncher.REQUEST_KEYBINDS")
        intent.setPackage("com.katsuyamaki.DroidOSLauncher")
        sendBroadcast(intent)
    }
    // =================================================================================
    // END FUNCTION: requestKeybindsFromLauncher
    // =================================================================================

    fun enforceZOrder() {
        try {
            // Delegate trackpad/bubble/cursor z-order to layoutManager
            layoutManager?.enforceZOrder()

            // Handle keyboard and menu (not managed by layoutManager)
            if (keyboardOverlay != null && keyboardOverlay?.isShowing() == true) {
                 keyboardOverlay?.bringToFront()
            }
            if (menuManager != null) {
                try { menuManager?.bringToFront() } catch(e: Exception) {}
            }
            btMouseManager?.bringToFront()

        } catch (e: Exception) {
        }
    }


    // === RECEIVER & ACTIONS - END ===

    private val TAG = "OverlayService"

    @Suppress("UNUSED_PARAMETER")
    private fun logOverlayKbDiag(event: String, extra: String = "") {
    }

    // Command dispatcher for broadcast receiver logic
    internal lateinit var commandDispatcher: OverlayCommandDispatcher

    var windowManager: WindowManager? = null
    var displayManager: DisplayManager? = null
    var shellService: IShellService? = null
    internal lateinit var inputHandler: ShizukuInputHandler
    private var appWindowManager: WindowManager? = null
    private var isBound = false
    internal val handler = Handler(Looper.getMainLooper())

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
    internal lateinit var trackpadParams: WindowManager.LayoutParams
    private lateinit var cursorParams: WindowManager.LayoutParams

    internal var menuManager: TrackpadMenuManager? = null
    internal var mirrorManager: MirrorModeManager? = null
    internal var btMouseManager: BluetoothMouseManager? = null
    internal var imeManager: SystemImeManager? = null
    internal var layoutManager: OverlayLayoutManager? = null
    internal var savedKbX = 0
    internal var savedKbY = 0
    internal var savedKbW = 0
    internal var savedKbH = 0
    internal var keyboardOverlay: KeyboardOverlay? = null

    var currentDisplayId = 0
    var inputTargetDisplayId = 0
    var isTrackpadVisible = false // Changed: Default OFF
    private val FORCE_HIDE_DEBOUNCE_MS = 350L // Keep small guard against show/hide bounce without visible close lag
    private var lastForceShowTime = 0L // Debounce IME FORCE_SHOW/FORCE_HIDE flicker
    private var preserveKeyboardUntil = 0L // Ignore FORCE_HIDE until this timestamp (launcher drawer transition)
    private var pendingForceHideRunnable: Runnable? = null
    private var isDockIMEVisible = false
    private var dockNavBarHeight: Int = -1
    private var lastDockMarginPercent = -1 // Track whether DockIME toolbar is currently showing
    private var manualKeyScaleBeforeMargin = -1 // Save manual key scale before margin adjustment


    var isCustomKeyboardVisible = true // Changed: Default ON
    var isScreenOff = false
    private var isPreviewMode = false
    private var previousImeId: String? = null
    
    // --- SMART RESTORE STATE ---
    private var pendingRestoreTrackpad = false
    private var pendingRestoreKeyboard = false
    private var hasPendingRestore = false

    // NOTE: preMirror state variables moved to helper methods section

    private var isVoiceActive = false
    
    
    
    // Heartbeat to keep hardware state alive AND enforce settings
    private val blockingHeartbeat = object : Runnable {
        override fun run() {
            // No-op: Null Keyboard handles blocking natively
        }
    }
    
    val prefs = TrackpadPrefs()
    internal val layoutStateStore by lazy { OverlayLayoutStateStore(this) }

    // =================================================================================
    // LAUNCHER BLOCKED SHORTCUTS SET
    // SUMMARY: Contains the set of shortcuts registered in the DroidOS Launcher.
    //          Format: "modifier|keyCode" (e.g., "4096|47" for Meta+S)
    //          Only shortcuts in this set should be blocked when overrideSystemShortcuts is true.
    //          Received via UPDATE_KEYBINDS broadcast from Launcher.
    // =================================================================================
    internal val launcherBlockedShortcuts = mutableSetOf<String>()
    // =================================================================================
    // END BLOCK: LAUNCHER BLOCKED SHORTCUTS SET
    // =================================================================================

    // =========================
    // BLOCKING TRIGGER (Global)
    // =========================

// FILE: Cover-Screen-Trackpad/app/src/main/java/com.katsuyamaki.DroidOSTrackpadKeyboard/OverlayService.kt
// LOC: Around line 1550 (Search for 'fun triggerAggressiveBlocking')

    // =================================================================================
    // Empty Block after removing IME logic
    // =================================================================================
    // =================================================================================
    // END BLOCK: ensureCoverKeyboardEnforced
    // =================================================================================

    // =================================================================================
    // FUNCTION: onAccessibilityEvent
    // SUMMARY: Monitors system events to manage Keyboard Blocking and Mode Switching.
    // UPDATED: Now explicitly UNBLOCKS the keyboard when Main Screen (0) is active.
    // =================================================================================
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        // [EFFICIENCY] IMMEDIATE FILTER
        // Block high-frequency noise immediately
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED || 
            event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            return
        }
        

        // [MODIFIED] MAIN SCREEN GUARD REMOVED
        // We now allow blocking logic to run on the Main Screen if configured.


        // [FIX 2] Standard Multi-Display Filter
        // Ignore events from displays we aren't managing (unless it was Main Screen handled above)
        if (event.displayId != currentDisplayId) {
            return
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
        
        // [FIX] External Cursor Movement Detection
        // If selection changes or user clicks text in the target app, reset swipe history.
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED || 
            event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            
            val timeSinceInjection = System.currentTimeMillis() - lastInjectionTime
            if (timeSinceInjection > 500) {
                keyboardOverlay?.resetSwipeHistory()
            }
        }

        // [FIX 5] Throttle & Execute Blocking (ONLY on Cover Screen - display 1)
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {



            // GUARD: Only run blocking logic on Cover Screen (display 1)
            // Skip during active typing to prevent overlay flashing with DroidOS IME
            val isActivelyTyping = System.currentTimeMillis() - lastInjectionTime < 300
            if (currentDisplayId == 1 && prefs.prefBlockSoftKeyboard && !isVoiceActive && !isActivelyTyping) {
                 // CASE A: Cover Screen + Blocking Enabled -> Force Null Keyboard
                 imeManager?.ensureKeyboardBlocked()
                 imeManager?.triggerAggressiveBlockingWithThrottle()
            } else if (currentDisplayId != 1) {
                 // CASE B: Main/Virtual Display -> Ensure keyboard is NOT blocked
                 if (Build.VERSION.SDK_INT >= 24) {
                     try {
                         if (softKeyboardController.showMode != AccessibilityService.SHOW_MODE_AUTO) {
                             softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO
                         }
                     } catch(e: Exception) {}
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
    internal var uiScreenWidth = 1080
    internal var uiScreenHeight = 2640
    private var lastKnownScreenW = 0
    private var lastKnownScreenH = 0
    internal var targetScreenWidth = 1920
    internal var targetScreenHeight = 1080
    internal var cursorX = 300f
    internal var cursorY = 300f
    private var cursorFadeRunnable: Runnable? = null
    private val CURSOR_FADE_TIMEOUT = 5000L // 5 seconds
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

    // Remote cursor variables moved to OverlayLayoutManager

    private val BT_TAG = "BT_MOUSE_CAPTURE"

    // NOTE: Virtual Mirror Mode variables moved to MirrorModeManager.kt

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
        // [FIX] Check count to determine action
        // If count > 2, user is spamming keys (Panic?), so don't fire normal actions
        if (volUpTapCount == 1) {
            executeHardkeyAction(prefs.hardkeyVolUpTap, KeyEvent.ACTION_UP)
        } else if (volUpTapCount == 2) {
            executeHardkeyAction(prefs.hardkeyVolUpDouble, KeyEvent.ACTION_UP)
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
            if (::commandDispatcher.isInitialized) {
                commandDispatcher.handleCommand(intent)
            }
        }
    }

    // Helper to dynamically update window flags
    internal fun setOverlayFocusable(focusable: Boolean) {
        try {
            keyboardOverlay?.setFocusable(focusable)
        } catch (e: Exception) {
            
        }
    }

    // === DISPATCHER HELPER METHODS ===
    internal fun handleKeyboardToggle(intent: Intent) {
        val forceShow = intent.getBooleanExtra("FORCE_SHOW", false)
        val forceHide = intent.getBooleanExtra("FORCE_HIDE", false)

        logOverlayKbDiag(
            "handleKeyboardToggle_enter",
            "forceShow=$forceShow forceHide=$forceHide currentlyVisible=$isCustomKeyboardVisible dockVisible=$isDockIMEVisible"
        )

        if (forceShow) isDockIMEVisible = true
        if (forceHide) isDockIMEVisible = false

        if (forceHide) {
            // Ignore FORCE_HIDE if within preserve window (launcher drawer transition)
            if (System.currentTimeMillis() < preserveKeyboardUntil) {
                logOverlayKbDiag("handleKeyboardToggle_forceHide_preserved", "ignoring FORCE_HIDE during preserve window")
                return
            }
            val inSpacebarMouse = keyboardOverlay?.getKeyboardView()?.isInSpacebarMouseMode() == true
            if (inSpacebarMouse) {
                val fixIntent = Intent("com.katsuyamaki.DroidOSLauncher.IME_VISIBILITY")
                fixIntent.setPackage("com.katsuyamaki.DroidOSLauncher")
                fixIntent.putExtra("VISIBLE", true)
                fixIntent.putExtra("MANUAL_TOGGLE", true)
                sendBroadcast(fixIntent)
                logOverlayKbDiag("handleKeyboardToggle_forceHide_spacebarMouse", "sentVisible=true")
            } else if (isCustomKeyboardVisible) {
                pendingForceHideRunnable?.let { handler.removeCallbacks(it) }
                val elapsed = System.currentTimeMillis() - lastForceShowTime
                val delayMs = (FORCE_HIDE_DEBOUNCE_MS - elapsed).coerceAtLeast(0L)
                logOverlayKbDiag("handleKeyboardToggle_forceHide_schedule", "elapsedMs=$elapsed delayMs=$delayMs")
                val hideRunnable = Runnable {
                    keyboardOverlay?.hide()
                    isCustomKeyboardVisible = false
                    pendingRestoreKeyboard = true
                    hasPendingRestore = true

                    // Guarantee launcher clears IME margin override on actual hide.
                    val imeIntent = Intent("com.katsuyamaki.DroidOSLauncher.IME_VISIBILITY")
                    imeIntent.setPackage("com.katsuyamaki.DroidOSLauncher")
                    imeIntent.putExtra("VISIBLE", false)
                    imeIntent.putExtra("IS_TILED", false)
                    imeIntent.putExtra("MANUAL_TOGGLE", true)
                    imeIntent.putExtra("FORCE_RETILE", true)
                    sendBroadcast(imeIntent)

                    logOverlayKbDiag("handleKeyboardToggle_forceHide_run", "sentVisible=false forceRetile=true")
                    pendingForceHideRunnable = null
                }
                pendingForceHideRunnable = hideRunnable
                if (delayMs <= 0L) {
                    hideRunnable.run()
                } else {
                    handler.postDelayed(hideRunnable, delayMs)
                }
            }
        } else if (forceShow) {
            pendingForceHideRunnable?.let { handler.removeCallbacks(it) }
            pendingForceHideRunnable = null
            lastForceShowTime = System.currentTimeMillis()
            isDockIMEVisible = true
            if (keyboardOverlay == null) initCustomKeyboard()
            keyboardOverlay?.hide()
            keyboardOverlay?.show()
            isCustomKeyboardVisible = true
            enforceZOrder()
            logOverlayKbDiag("handleKeyboardToggle_forceShow_apply", "visible=true")
        } else {
            toggleCustomKeyboard()
        }
    }

    internal fun handleDockPrefChanged(intent: Intent) {
        if (intent.hasExtra("dock_mode")) {
            val dockMode = intent.getBooleanExtra("dock_mode", false)
            if (dockMode) applyDockMode() else showToast("Dock mode disabled")
        }
        if (intent.hasExtra("show_kb_above_dock")) {
            val showAbove = intent.getBooleanExtra("show_kb_above_dock", true)
            prefs.prefShowKBAboveDock = showAbove
            prefs.save(this)
            if (isCustomKeyboardVisible) {
                val margin = intent.getIntExtra("resize_to_margin", -1)
                if (margin >= 0) applyDockModeWithMargin(margin) else applyDockMode()
            }
        }
        if (intent.hasExtra("resize_to_margin")) {
            val marginPercent = intent.getIntExtra("resize_to_margin", -1)
            if (marginPercent >= 0 && isCustomKeyboardVisible) applyDockModeWithMargin(marginPercent)
        }
        if (intent.hasExtra("auto_resize")) {
            val autoResize = intent.getBooleanExtra("auto_resize", false)
            if (!autoResize) {
                lastDockMarginPercent = -1
                if (isCustomKeyboardVisible) {
                    val restoreScale = if (manualKeyScaleBeforeMargin > 0) manualKeyScaleBeforeMargin else prefs.prefKeyScale
                    manualKeyScaleBeforeMargin = -1
                    prefs.prefKeyScale = restoreScale
                    val density = resources.displayMetrics.density
                    val navBarHeight = getNavBarHeight()
                    val kbHeight = (275f * (restoreScale / 100f) * density).toInt()
                    val dockToolbarHeight = if (prefs.prefShowKBAboveDock && isDockIMEVisible) (40 * density).toInt() else 0
                    val targetY = uiScreenHeight - kbHeight - dockToolbarHeight - navBarHeight
                    keyboardOverlay?.setWindowBoundsWithScale(0, targetY, uiScreenWidth, kbHeight)
                    saveKeyboardHeightForDock(kbHeight)
                } else {
                    manualKeyScaleBeforeMargin = -1
                }
            }
        }
    }

    internal fun handleApplyDockMode(intent: Intent) {
        dockNavBarHeight = intent.getIntExtra("nav_bar_height", -1)
        if (intent.getBooleanExtra("enabled", false)) {
            val marginPercent = intent.getIntExtra("resize_to_margin", -1)
            if (marginPercent >= 0) {
                lastDockMarginPercent = marginPercent
                applyDockModeWithMargin(marginPercent)
            } else {
                applyDockMode()
            }
        }
    }

    internal fun handleDockPopupVisibility(popupVisible: Boolean) {
        if (popupVisible) {
            if (isCustomKeyboardVisible) keyboardOverlay?.hide()
        } else {
            if (isCustomKeyboardVisible) {
                keyboardOverlay?.show()
                enforceZOrder()
            }
        }
    }

    internal fun handleVoiceTypeTriggered() {
        isVoiceActive = true
        keyboardOverlay?.setVoiceActive(true)
        setOverlayFocusable(false)
        handler.postDelayed({ attemptRefocusInput() }, 300)
    }
    // === END DISPATCHER HELPER METHODS ===
    
    // NEW FUNCTION: Finds the focused input field and performs a click
    internal fun attemptRefocusInput() {
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
            
            // Stop monitoring
            micCheckHandler.removeCallbacks(micCheckRunnable)
            
            // Standard Dismissal Logic (Back Button)
            Thread {
                try {
                    val success = performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                    if (!success) {
                        inputHandler.injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN, 0, prefs.prefBlockSoftKeyboard)
                        Thread.sleep(50)
                        inputHandler.injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP, 0, prefs.prefBlockSoftKeyboard)
                    }
                    // Only re-enable blocking on cover screen
                    if (currentDisplayId == 1 && prefs.prefBlockSoftKeyboard) {
                        imeManager?.triggerAggressiveBlocking()
                    }
                } catch (e: Exception) {
                    
                }
            }.start()
        }
    }

    // --- Voice Logic & Mic Check Loop ---


    private val micCheckHandler = Handler(Looper.getMainLooper())
    
    private val micCheckRunnable = object : Runnable {
        override fun run() {
            try {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                
                var isMicOn = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (audioManager.activeRecordingConfigurations.isNotEmpty()) {
                        isMicOn = true
                    }
                }
                
                if (isMicOn) {
                    // Still recording, check again in 1 second
                    micCheckHandler.postDelayed(this, 1000)
                } else {
                    // Mic stopped, turn off the green light
                    isVoiceActive = false
                    keyboardOverlay?.setVoiceActive(false)
                }
            } catch (e: Exception) {
                isVoiceActive = false
                keyboardOverlay?.setVoiceActive(false)
            }
        }
    }

    internal fun triggerVoiceTyping() {
        if (shellService == null) return

        // 1. UI: Turn Button Green Immediately
        isVoiceActive = true
        keyboardOverlay?.setVoiceActive(true)
        setOverlayFocusable(false) // Ensure focus is passable
        
        // 2. Start Monitoring Loop
        // Delay 3 seconds to allow the Voice IME to open and start recording
        micCheckHandler.removeCallbacks(micCheckRunnable)
        micCheckHandler.postDelayed(micCheckRunnable, 3000)

        // 3. Perform IME Switch via Shell
        Thread {
            try {
                // Fetch IME list and find Google Voice Typing
                val output = shellService?.runCommand("ime list -a -s") ?: ""
                val voiceIme = output.lines().find { it.contains("google", true) && it.contains("voice", true) } 
                    ?: output.lines().find { it.contains("voice", true) }
                
                if (voiceIme != null) {
                    shellService?.runCommand("ime set $voiceIme")
                    // Try to refocus input
                    handler.postDelayed({ attemptRefocusInput() }, 500)
                } else {
                    handler.post { 
                        isVoiceActive = false
                        keyboardOverlay?.setVoiceActive(false)
                        showToast("Voice IME not found")
                    }
                }
            } catch (e: Exception) {
                handler.post { 
                    isVoiceActive = false
                    keyboardOverlay?.setVoiceActive(false)
                }
            }
        }.start()
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
            inputHandler = ShizukuInputHandler(this@OverlayService, shellService, currentDisplayId)
            mirrorManager = MirrorModeManager(this@OverlayService, displayManager)
            imeManager = SystemImeManager(this@OverlayService, shellService)
            isBound = true
            updateBubbleStatus()
            showToast("Shizuku Connected")
            initCustomKeyboard()
            
            // CRITICAL FIX: Only apply blocking on Cover Screen (display 1) when enabled
            if (currentDisplayId == 1 && prefs.prefBlockSoftKeyboard) {
                imeManager?.triggerAggressiveBlocking()
                handler.post(blockingHeartbeat)
            }

// Request Custom Modifier Key from Launcher
            val syncIntent = Intent("com.katsuyamaki.DroidOSLauncher.REQUEST_CUSTOM_MOD_SYNC")
            syncIntent.setPackage("com.katsuyamaki.DroidOSLauncher")
            sendBroadcast(syncIntent)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            shellService = null
            isBound = false
            updateBubbleStatus()
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize command dispatcher FIRST (before registering receivers)
        commandDispatcher = OverlayCommandDispatcher(this)

        // Register BOTH new and old prefixes to support all scripts/buttons
        val commandFilter = IntentFilter().apply {
            val cmds = listOf("SOFT_RESTART", "ENFORCE_ZORDER", "MOVE_TO_DISPLAY", "TOGGLE_MIRROR", "TOGGLE_VIRTUAL_MIRROR", "OPEN_DRAWER", "STOP_SERVICE", "SET_INPUT_CAPTURE", "SET_CUSTOM_MOD", "UPDATE_KEYBINDS", "SET_NUM_LAYER")
            val prefixes = listOf("com.katsuyamaki.DroidOSTrackpadKeyboard.")

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

        // =================================================================================
        // REQUEST KEYBINDS FROM LAUNCHER ON STARTUP
        // SUMMARY: Ask the Launcher to broadcast its registered keybinds so we know
        //          which shortcuts to block. This ensures sync on Trackpad startup.
        // =================================================================================
        requestKeybindsFromLauncher()
        // =================================================================================
        // END BLOCK: REQUEST KEYBINDS FROM LAUNCHER ON STARTUP
        // =================================================================================

        try { displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager; displayManager?.registerDisplayListener(this, handler) } catch (e: Exception) {}
        try { val im = getSystemService(Context.INPUT_SERVICE) as InputManager; im.registerInputDeviceListener(this, handler) } catch (e: Exception) {}
        // =================================================================================
        // VIRTUAL DISPLAY KEEP-ALIVE: Initialize PowerManager
        // =================================================================================
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        // =================================================================================
        // END BLOCK: VIRTUAL DISPLAY KEEP-ALIVE PowerManager Init
        // =================================================================================


        imeManager?.startMonitoring()

        // =================================================================================
        // END BLOCK: CONTENT OBSERVER for Default Keyboard Changes
        // =================================================================================
        
        prefs.load(this)
        val filter = IntentFilter().apply { 
            // Internal short commands
            addAction("SWITCH_DISPLAY")

                        addAction("CYCLE_INPUT_TARGET")
                        addAction("RESET_CURSOR")
                        addAction("SET_CURSOR_POS")
                        addAction("TOGGLE_DEBUG")
                        addAction("FORCE_KEYBOARD")
                        addAction("TOGGLE_CUSTOM_KEYBOARD")
                        addAction("PRESERVE_KEYBOARD")
                        addAction("com.katsuyamaki.DroidOSTrackpadKeyboard.SWITCH_KEYBOARD")
                        addAction("com.katsuyamaki.DroidOSTrackpadKeyboard.LIST_KEYBOARDS")
                        addAction("DOCK_PREF_CHANGED")
                        addAction("APPLY_DOCK_MODE")
                        addAction("DOCK_POPUP_VISIBLE")
                        addAction("SET_TRACKPAD_VISIBILITY")
                        addAction("SET_PREVIEW_MODE") 
                        addAction("OPEN_MENU")
                        addAction("VOICE_TYPE_TRIGGERED")
                        addAction("REQUEST_VOICE_INPUT")
                        addAction(Intent.ACTION_SCREEN_ON)

                        // External commands (Old and New Prefixes)
                        val actions = listOf(
                            "SOFT_RESTART", "MOVE_TO_VIRTUAL", "RETURN_TO_PHYSICAL",
                            "ENFORCE_ZORDER", "TOGGLE_VIRTUAL_MIRROR", "GET_STATUS"
                        )
                        val prefixes = listOf(
                            "com.katsuyamaki.DroidOSTrackpadKeyboard.",
                            "com.katsuyamaki.DroidOSTrackpadKeyboard."
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



    // This is the Accessibility Service entry point
// =================================================================================
    // FUNCTION: onServiceConnected
    // SUMMARY: AccessibilityService entry point - called when user enables service.
    //          Initializes managers, loads dictionary, registers receivers, and builds UI.
    // =================================================================================
    // =================================================================================
    // FUNCTION: handleGlobalTouch
    // SUMMARY: Called on any touch event that passes through. Checks if spacebar mouse
    //          extended mode is active and if touch is outside keyboard bounds.
    //          If so, exits extended mode.
    // NOTE: This is called from the root trackpad touch handler or accessibility event.
    // =================================================================================
    internal fun handleSpacebarExtendedModeCheck(x: Float, y: Float, action: Int) {
        if (!prefs.prefSpacebarMouseExtended) return
        if (action != MotionEvent.ACTION_DOWN) return
        
        val kbView = keyboardOverlay?.getKeyboardView() ?: return
        if (!kbView.isInSpacebarMouseMode()) return
        
        // Check if touch is within keyboard bounds
        val kbBounds = keyboardOverlay?.getKeyboardBounds()
        if (kbBounds == null) {
            // Can't determine bounds, don't exit
            return
        }
        
        val isInsideKeyboard = x >= kbBounds.left && x <= kbBounds.right &&
                               y >= kbBounds.top && y <= kbBounds.bottom
        
        if (!isInsideKeyboard) {
            kbView.exitSpacebarMouseMode()
        }
    }
    // =================================================================================
    // END BLOCK: handleSpacebarExtendedModeCheck
    // =================================================================================

    override fun onServiceConnected() {
        super.onServiceConnected()
        isAccessibilityReady = true

        // =================================================================================
        // CRITICAL: Initialize PredictionEngine Dictionary
        // SUMMARY: Load the dictionary at service start. Without this, swipe typing fails
        //          because decodeSwipe() has no words to match against.
        // =================================================================================
        PredictionEngine.instance.loadDictionary(this)
        // =================================================================================
        // END BLOCK: Initialize PredictionEngine Dictionary
        // =================================================================================

        // Initialize Managers
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        
        // [FIX] Correct variable names for OverlayService:
        // Use 'this' (because OverlayService implements DisplayListener)
        // Use 'handler' (not uiHandler)
        displayManager?.registerDisplayListener(this, handler)

        // Register receivers
        val filter = IntentFilter().apply {
            addAction("com.katsuyamaki.DroidOSLauncher.OPEN_DRAWER")
            addAction("com.katsuyamaki.DroidOSLauncher.UPDATE_ICON")
            addAction("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(commandReceiver, filter, Context.RECEIVER_EXPORTED) else registerReceiver(commandReceiver, filter)

        // [FIX] Removed incompatible Shizuku listeners that belong to Launcher
        
        // [FIX] USE RETRY LOGIC HERE
        checkAndBindShizuku()

        // Load preferences
        prefs.load(this) 
        
        // [FIX] READ TARGET DISPLAY (Fixes "Wrong Display" on Race Condition)
        val globalTarget = try {
            android.provider.Settings.Global.getInt(contentResolver, "droidos_target_display", -1)
        } catch (e: Exception) { -1 }

        val finalTarget = when {
            pendingDisplayId >= 0 -> pendingDisplayId
            globalTarget >= 0 -> globalTarget
            else -> currentDisplayId
        }
        
        // =================================================================================
        // ONE-TIME FIX: Clear stale Samsung preference if Gboard is available
        // SUMMARY: Previous bug caused Samsung to be saved as preferred IME.
        //          This checks on startup and fixes if Gboard is available.
        // =================================================================================
        try {
            val prefs = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            val savedIme = prefs.getString("user_preferred_ime", null)
            if (savedIme != null && (savedIme.contains("honeyboard") || savedIme.contains("com.sec"))) {
                // Check if Gboard is enabled
                Thread {
                    try {
                        val enabledImes = shellService?.runCommand("ime list -s") ?: ""
                        val gboard = enabledImes.lines().find { 
                            it.contains("com.google.android.inputmethod.latin") 
                        }
                        if (gboard != null) {
                            prefs.edit().putString("user_preferred_ime", gboard.trim()).apply()
                        }
                    } catch (e: Exception) {}
                }.start()
            }
        } catch (e: Exception) {}
        // =================================================================================
        // END BLOCK: ONE-TIME FIX for stale Samsung preference
        // =================================================================================
        
        // Build UI
        setupUI(finalTarget)
        
        // [FIX] Ensure bubble icon status is updated
        updateBubbleStatus()

        // Clear pending states
        pendingDisplayId = -1

        imeManager?.startMonitoring()

        showToast("Trackpad Ready")
    }




   override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try { createNotification() } catch(e: Exception){  }
        
        if (intent != null) {
            val dId = intent.getIntExtra("displayId", -999)
            if (dId != -999) {
                handler.post { Toast.makeText(this, "Service Started on D:$dId", Toast.LENGTH_SHORT).show() }
            }
        }

        try {
            checkAndBindShizuku()

            // [FIX] Combined Startup Logic
            if (intent != null) {
                val dId = intent.getIntExtra("displayId", -1)
                
                if (dId != -1) {
                    if (isAccessibilityReady) {
                        // Safe to launch immediately
                        setupUI(dId)
                    } else {
                        // Too early! Queue it for onServiceConnected
                        pendingDisplayId = dId
                    }
                    return START_STICKY
                }
            }

            // --- Original Logic for other intents/conditions ---
            // If no explicit displayId was passed, or if the AccessibilityService is not yet ready
            // for the initial setup, we fall back to the existing logic.
            val action = intent?.action
            val isForceStart = intent?.getBooleanExtra("force_start", false) == true
            val startupTarget = if (isForceStart) {
                try {
                    android.provider.Settings.Global.getInt(contentResolver, "droidos_target_display", Display.DEFAULT_DISPLAY)
                } catch (e: Exception) {
                    Display.DEFAULT_DISPLAY
                }
            } else {
                Display.DEFAULT_DISPLAY
            }
            fun matches(suffix: String): Boolean = action?.endsWith(suffix) == true
            
            // Handle commands robustly (keeping original intent handling)
            if (action != null) {
                when {
                    // Standard Actions
                    action == "SWITCH_DISPLAY" -> switchDisplay()
                    action == "RESET_POSITION" -> {
                        val target = intent.getStringExtra("TARGET") ?: "TRACKPAD"
                        if (target == "KEYBOARD") keyboardOverlay?.resetPosition(preserveScale = true) else resetTrackpadPosition()
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
                        prefs.load(this)
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
                    action == "PRESERVE_KEYBOARD" -> { preserveKeyboardUntil = System.currentTimeMillis() + 1000L }
                    action == "OPEN_MENU" -> menuManager?.show()
                    
                    // ADB / Launcher Commands (Suffix Matching)
                    matches("SOFT_RESTART") -> {
                        performSoftRestart()
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
                setupUI(startupTarget)
            }
            
            // [CRITICAL] Ensure UI is created if missing
            if (windowManager == null || bubbleView == null) {
                setupUI(startupTarget)
            }

        } catch (e: Exception) {
            // Retry setup safely
            try { setupUI(Display.DEFAULT_DISPLAY) } catch(e2: Exception) {}
        }
        return START_STICKY
    }

    override fun onInterrupt() {}

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val display = displayManager?.getDisplay(currentDisplayId)
        if (display != null) {
            val metrics = android.util.DisplayMetrics()
            display.getRealMetrics(metrics)
            val w = metrics.widthPixels; val h = metrics.heightPixels
            if (lastKnownScreenW != 0 && lastKnownScreenH != 0 &&
                (w != lastKnownScreenW || h != lastKnownScreenH)) {
                // =================================================================================
                // FIX: Don't reload full profile on orientation/config change.
                // Profile should only load on: (1) app restart, (2) manual reload request.
                // Just save current state and rebuild UI - setupUI loads per-display prefs.
                // This prevents scale being overwritten by profile defaults on every fold/unfold.
                // =================================================================================
                saveCurrentState()
                lastKnownScreenW = w; lastKnownScreenH = h
                handler.post { setupUI(currentDisplayId) }
            } else {
                lastKnownScreenW = w; lastKnownScreenH = h
            }
        }
    }

    // Use dispatchKeyEvent to catch BOTH Down and Up events in one place
    override fun onKeyEvent(event: KeyEvent): Boolean {
        val isVolKey = event.keyCode == KeyEvent.KEYCODE_VOLUME_UP || event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
        
        if (isPreviewMode || (!isTrackpadVisible && !isVolKey)) {
            return super.onKeyEvent(event)
        }
        
        val action = event.action
        val keyCode = event.keyCode
        
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // [FIX] Allow panic detection even if "none" is selected
            // We check logic inside to allow pass-through if count < 5 and action is none
            val isConfigured = (prefs.hardkeyVolUpTap != "none" || prefs.hardkeyVolUpHold != "none")

            when (action) {
                KeyEvent.ACTION_DOWN -> {
                    if (!isLeftKeyHeld) {
                        isLeftKeyHeld = true
                        volUpHoldTriggered = false
                        if (isConfigured) handler.postDelayed(volUpHoldRunnable, prefs.holdDurationMs.toLong())
                    }
                }
                KeyEvent.ACTION_UP -> {
                    isLeftKeyHeld = false
                    handler.removeCallbacks(volUpHoldRunnable)

                    if (volUpHoldTriggered) {
                        // RELEASE the hold action
                        if (isConfigured) executeHardkeyAction(prefs.hardkeyVolUpHold, KeyEvent.ACTION_UP)
                        volUpTapCount = 0
                    } else {
                        val timeSinceLastTap = System.currentTimeMillis() - lastVolUpTime
                        lastVolUpTime = System.currentTimeMillis()

                        // [SAFETY] PANIC LOGIC: 5 Rapid Taps (< 400ms gap)
                        if (timeSinceLastTap < 400) {
                            volUpTapCount++
                        } else {
                            volUpTapCount = 1
                        }

                        // Remove pending double-tap action while we count up
                        handler.removeCallbacks(volUpDoubleTapRunnable)

                        if (volUpTapCount >= 5) {
                            // !!! PANIC TRIGGERED !!!
                            volUpTapCount = 0
                            performEmergencyReset()
                            return true // Always consume panic
                        } else if (volUpTapCount == 2) {
                            // Schedule Double Tap (cancellable if user keeps tapping for panic)
                            if (isConfigured) handler.postDelayed(volUpDoubleTapRunnable, prefs.doubleTapMs.toLong())
                        } else if (volUpTapCount == 1) {
                            // Schedule Single Tap
                            if (isConfigured) handler.postDelayed(volUpDoubleTapRunnable, prefs.doubleTapMs.toLong())
                        }
                    }
                }
            }

            // If configured, consume event. If not, only consume if we are mid-panic-sequence (count > 0)
            // This allows normal Volume Up to work if "none" is selected, until you tap fast enough to trigger panic logic
            return isConfigured || volUpTapCount > 0
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

    internal fun removeOldViews() {
        val viewsToRemove = listOf(trackpadLayout, bubbleView, cursorLayout)
        for (view in viewsToRemove) {
            if (view != null && view.parent != null && windowManager != null) {
                try {
                    windowManager?.removeViewImmediate(view)
                } catch (e: Exception) {
                }
            }
        }
        // Clean up keyboard and menu — each independently so one failure doesn't block others
        try { keyboardOverlay?.hide() } catch (e: Exception) {
        }
        keyboardOverlay = null
        try { menuManager?.hide() } catch (e: Exception) {
        }
        menuManager = null
        // Clean up BT mouse capture overlay
        btMouseManager?.removeBtMouseCaptureOverlay()
        // Nullify references to ensure setup functions create fresh instances
        trackpadLayout = null
        bubbleView = null
        cursorLayout = null
        cursorView = null
    }

    internal fun setupUI(displayId: Int) {
        logOverlayKbDiag("setupUI_start", "targetDisplay=$displayId")

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
            if (::inputHandler.isInitialized) inputHandler.updateDisplay(displayId)

            updateUiMetrics()

            // 4. Rebuild the UI components
            layoutManager = OverlayLayoutManager(this, accessContext, windowManager!!)
            layoutManager!!.setupAll()
            trackpadLayout = layoutManager!!.trackpadLayout
            trackpadParams = layoutManager!!.trackpadParams
            bubbleView = layoutManager!!.bubbleView
            bubbleParams = layoutManager!!.bubbleParams
            cursorLayout = layoutManager!!.cursorLayout
            cursorView = layoutManager!!.cursorView
            cursorParams = layoutManager!!.cursorParams
            if (shellService != null) initCustomKeyboard()
            menuManager = TrackpadMenuManager(displayContext, windowManager!!, this)
            btMouseManager = BluetoothMouseManager(this, windowManager!!)


            enforceZOrder()
            showToast("Trackpad active on Display $displayId")

            // =================================================================================
            // KEYBOARD BLOCKING/RESTORATION LOGIC FOR DISPLAY SWITCH
            // SUMMARY: On Main Screen (0), ensure showMode is AUTO so keyboards can appear.
            //          On Cover Screen (1+), apply blocking if enabled.
            //          Samsung takeover is handled by the Content Observer, not here.
            // =================================================================================
            


            // =================================================================================
            // KEYBOARD BLOCKING: ONLY on Cover Screen (display 1)
            // Main screen (0) and virtual displays (2+) should NEVER block keyboard
            // =================================================================================
            if (displayId == 1) {
                // COVER SCREEN - Apply blocking if enabled
                if (prefs.prefBlockSoftKeyboard) {
                    imeManager?.triggerAggressiveBlocking()
                }
            } else {
                // MAIN SCREEN (0) or VIRTUAL DISPLAY (2+) - Never block, ensure AUTO mode
                if (Build.VERSION.SDK_INT >= 24) {
                    try { softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO } catch (e: Exception) {}
                }
            }
            // =================================================================================
            // END BLOCK: KEYBOARD BLOCKING LOGIC
            // =================================================================================
            
            // =================================================================================
            // END BLOCK: KEYBOARD BLOCKING/RESTORATION LOGIC FOR DISPLAY SWITCH
            // =================================================================================

            if (prefs.prefBubbleX == -1 || prefs.prefBubbleY == -1) {
                resetBubblePosition()
            }

            logOverlayKbDiag("setupUI_end", "targetDisplay=$displayId")


        } catch (e: Exception) {
            showToast("Failed to launch on display $displayId")
        }
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
    internal fun toggleDrawer() {
        menuManager?.toggle()
        enforceZOrder() // Ensure drawer is on top
    }
    
    private fun handleBubbleTap() {
        // Check if any bubble-included component is currently visible
        val anythingBubbleVisible = (prefs.prefBubbleIncludeTrackpad && isTrackpadVisible) || (prefs.prefBubbleIncludeKeyboard && isCustomKeyboardVisible)
        if (anythingBubbleVisible) {
            performSmartHide()
        } else if (prefs.prefBubbleIncludeTrackpad || prefs.prefBubbleIncludeKeyboard) {
            // Only restore if at least one component is bubble-included
            performSmartRestore()
        }
        // If nothing is bubble-included, bubble tap does nothing
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
                    else inputHandler.performClick(cursorX, cursorY, inputTargetDisplayId, false)
                }
            }
            "right_click" -> {
                 if (keyEventAction == KeyEvent.ACTION_DOWN) {
                    if (!volDownDragActive) { volDownDragActive = true; startKeyDrag(MotionEvent.BUTTON_SECONDARY) }
                } else {
                    if (volDownDragActive) { volDownDragActive = false; stopKeyDrag(MotionEvent.BUTTON_SECONDARY) } 
                    else inputHandler.performClick(cursorX, cursorY, inputTargetDisplayId, true)
                }
            }
            "action_back" -> if (isUp) Thread { try { inputHandler.injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_DOWN, 0, prefs.prefBlockSoftKeyboard); Thread.sleep(20); inputHandler.injectKey(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP, 0, prefs.prefBlockSoftKeyboard) } catch(e: Exception){} }.start()
            "action_home" -> if (isUp) Thread { try { inputHandler.injectKey(KeyEvent.KEYCODE_HOME, KeyEvent.ACTION_DOWN, 0, prefs.prefBlockSoftKeyboard); Thread.sleep(20); inputHandler.injectKey(KeyEvent.KEYCODE_HOME, KeyEvent.ACTION_UP, 0, prefs.prefBlockSoftKeyboard) } catch(e: Exception){} }.start()
            "action_forward" -> if (isUp) Thread { try { inputHandler.injectKey(KeyEvent.KEYCODE_FORWARD, KeyEvent.ACTION_DOWN, 0, prefs.prefBlockSoftKeyboard); Thread.sleep(20); inputHandler.injectKey(KeyEvent.KEYCODE_FORWARD, KeyEvent.ACTION_UP, 0, prefs.prefBlockSoftKeyboard) } catch(e: Exception){} }.start()
            "action_vol_up" -> if (isUp) Thread { try { inputHandler.injectKey(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.ACTION_DOWN, 0, prefs.prefBlockSoftKeyboard); Thread.sleep(20); inputHandler.injectKey(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.ACTION_UP, 0, prefs.prefBlockSoftKeyboard) } catch(e: Exception){} }.start()
            "action_vol_down" -> if (isUp) Thread { try { inputHandler.injectKey(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.ACTION_DOWN, 0, prefs.prefBlockSoftKeyboard); Thread.sleep(20); inputHandler.injectKey(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.ACTION_UP, 0, prefs.prefBlockSoftKeyboard) } catch(e: Exception){} }.start()
            "scroll_up" -> if (isUp) inputHandler.performSwipe(cursorX, cursorY, 0f, -(BASE_SWIPE_DISTANCE * prefs.scrollSpeed), inputTargetDisplayId)
            "scroll_down" -> if (isUp) inputHandler.performSwipe(cursorX, cursorY, 0f, BASE_SWIPE_DISTANCE * prefs.scrollSpeed, inputTargetDisplayId)
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
            
            // "Launcher Bubble" keybind should open Launcher drawer, not switch trackpad display.
            "toggle_bubble", "launcher_bubble" -> if (isUp) {
                try {
                    val launcherIntent = Intent("com.katsuyamaki.DroidOSLauncher.OPEN_DRAWER")
                    launcherIntent.setPackage("com.katsuyamaki.DroidOSLauncher")
                    sendBroadcast(launcherIntent)
                } catch (e: Exception) {
                    showToast("Launcher Bubble failed")
                }
            }
        }
    }

    fun toggleKeyboardMode() { vibrate(); if (!isKeyboardMode) { isKeyboardMode = true; savedWindowX = trackpadParams.x; savedWindowY = trackpadParams.y; trackpadParams.x = uiScreenWidth - trackpadParams.width; trackpadParams.y = 0; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(0xFFFF0000.toInt()) } else { isKeyboardMode = false; trackpadParams.x = savedWindowX; trackpadParams.y = savedWindowY; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(currentBorderColor) } }
    
    // [SAFETY] Emergency Reset (Panic Button)
    // Triggered by tapping Volume Up 5 times rapidly
    private fun performEmergencyReset() {
        // Long Vibration Pattern (SOS-ish)
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 100, 100, 100, 500), -1))
        } else {
            @Suppress("DEPRECATION") v.vibrate(500)
        }

        showToast("!!! EMERGENCY RESET TRIGGERED !!!")
        // 1. Force Screen ON
        isScreenOff = false
        Thread {
            try { shellService?.setBrightness(128) } catch (e: Exception) {}
            try { shellService?.setScreenOff(0, false) } catch (e: Exception) {}
        }.start()

        // 2. Unblock Keyboard (Force Gboard/System default)
        imeManager?.setSoftKeyboardBlocking(false)
        if (Build.VERSION.SDK_INT >= 24) {
            try { softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO } catch(e: Exception){}
        }

        // 3. Reset Overlays
        resetTrackpadPosition()
        keyboardOverlay?.resetPosition()
        resetBubblePosition()

        // 4. Disable Virtual Modes
        if (prefs.prefVirtualMirrorMode) {
            toggleVirtualMirrorMode() // This turns it OFF
        }

        // 5. Release Locks (handled by system on exit)
    }

    // [FIX] Public so MenuManager can call it
    fun toggleDebugMode() {
        isDebugMode = !isDebugMode
        if (isDebugMode) { 
            showToast("Debug ON")
            updateBorderColor(0xFFFFFF00.toInt())
            debugTextView?.visibility = View.VISIBLE 
        } else { 
            showToast("Debug OFF")
            if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt())
            debugTextView?.visibility = View.GONE 
        } 
    }

    fun updateBubbleStatus() { val dot = bubbleView?.findViewById<ImageView>(R.id.status_dot); if (shellService != null) dot?.visibility = View.GONE else dot?.visibility = View.VISIBLE }

    internal val bubbleIcons = arrayOf(R.mipmap.ic_trackpad_adaptive, R.drawable.ic_cursor, R.drawable.ic_tab_main, R.drawable.ic_tab_keyboard, android.R.drawable.ic_menu_compass, android.R.drawable.ic_menu_myplaces)
    
    fun updateBubbleSize(sizePercent: Int) { prefs.prefBubbleSize = sizePercent.coerceIn(50, 200); applyBubbleAppearance(); prefs.save(this) }
    fun updateBubbleIcon(index: Int) { prefs.prefBubbleIconIndex = index.coerceIn(0, bubbleIcons.size - 1); applyBubbleAppearance(); prefs.save(this) }
    fun cycleBubbleIcon() { updateBubbleIcon((prefs.prefBubbleIconIndex + 1) % bubbleIcons.size) }
    fun updateBubbleAlpha(alpha: Int) { prefs.prefBubbleAlpha = alpha.coerceIn(50, 255); applyBubbleAppearance(); prefs.save(this) }
    
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

    // =================================================================================
    // FUNCTION: captureLiveBubbleState
    // SUMMARY: Captures current floating bubble position and size into prefs fields
    //          so saveCurrentState/saveLayout persist the live bubble geometry.
    // =================================================================================
    internal fun captureLiveBubbleState() {
        try {
            layoutManager?.let { lm ->
                prefs.prefBubbleX = lm.bubbleParams.x
                prefs.prefBubbleY = lm.bubbleParams.y
            }
            // Keep user-selected size stable; never infer from pixel width.
            prefs.prefBubbleSize = prefs.prefBubbleSize.coerceIn(50, 200)
        } catch (_: Exception) {
            // Bubble params may be unavailable during early startup; skip safely.
        }
    }

    fun forceMoveToCurrentDisplay() { setupUI(currentDisplayId) }
    fun forceMoveToDisplay(displayId: Int) { if (displayId == currentDisplayId) return; saveCurrentState(); setupUI(displayId) }
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
    internal fun moveToVirtualDisplayAndEnableMirror(virtualDisplayId: Int) {
        try {
            // Store current state for potential return
            preMirrorTrackpadVisible = isTrackpadVisible
            preMirrorKeyboardVisible = isCustomKeyboardVisible
            preMirrorTargetDisplayId = inputTargetDisplayId
            
            // Move UI to virtual display
            setupUI(virtualDisplayId)
            
            // Enable Virtual Mirror Mode
            if (!prefs.prefVirtualMirrorMode) {
                prefs.prefVirtualMirrorMode = true
                prefs.save(this)
            }
            
            // Set input target to virtual display
            inputTargetDisplayId = virtualDisplayId
            updateTargetMetrics(virtualDisplayId)
            
            // Show trackpad and keyboard
            if (!isTrackpadVisible) toggleTrackpad()
            if (!isCustomKeyboardVisible) toggleCustomKeyboard()
            
            // Create mirror keyboard on virtual display
            mirrorManager?.update()
            createRemoteCursor(virtualDisplayId)
            
            // Update visual indicators
            updateBorderColor(0xFFFF00FF.toInt()) // Purple for remote mode
            updateWakeLockState()
            
            showToast("Virtual Display Mode Active")
            
        } catch (e: Exception) {
            showToast("Error: ${e.message}")
        }
    }
    
    /**
     * Returns overlay to physical display and disables Virtual Mirror Mode.
     * Called by RETURN_TO_PHYSICAL broadcast from Launcher or ADB.
     */
    internal fun returnToPhysicalDisplay(physicalDisplayId: Int) {
        try {
            // Disable Virtual Mirror Mode
            if (prefs.prefVirtualMirrorMode) {
                prefs.prefVirtualMirrorMode = false
                prefs.save(this)
            }
            
            // Remove remote cursor and mirror keyboard
            removeRemoteCursor()
            mirrorManager?.removeMirrorKeyboard()
            
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
            showToast("Error: ${e.message}")
        }
    }
    // =================================================================================
    // END BLOCK: INTER-APP COMMUNICATION HELPER FUNCTIONS
    // =================================================================================

    


    // =================================================================================
    // PROFILE KEY / STATE STORE DELEGATION
    // SUMMARY: Persistence logic lives in OverlayLayoutStateStore to keep this
    //          service focused on runtime orchestration.
    // =================================================================================
    private fun getLegacyProfileKey(): String = layoutStateStore.getLegacyProfileKey()
    fun getProfileKey(): String = layoutStateStore.getProfileKey()
    internal fun restoreTrackpadBoundsOnly() = layoutStateStore.restoreTrackpadBoundsOnly()
    fun getSavedProfileList(): List<String> = layoutStateStore.getSavedProfileList()








    private fun scheduleRestart() {
        try {
            // Keep the Smart Display Logic (it's safe to keep)
            var restartDisplayId = currentDisplayId
            try {
                val d0 = displayManager?.getDisplay(0)
                if (d0 != null && d0.state == Display.STATE_OFF) {
                     val d1 = displayManager?.getDisplay(1)
                     if (d1 != null && d1.state == Display.STATE_ON) {
                         restartDisplayId = 1
                     }
                }
            } catch(e: Exception) {}

            // [FIX] Target the SERVICE directly, not the Activity.
            // This bypasses the "Background Activity Start" restriction that was blocking the restart.
            val restartIntent = Intent(applicationContext, OverlayService::class.java)
            restartIntent.putExtra("displayId", restartDisplayId)
            restartIntent.putExtra("force_start", true)
            restartIntent.putExtra("IS_RESTART", true)
            
            val flags = if (Build.VERSION.SDK_INT >= 23) 
                android.app.PendingIntent.FLAG_ONE_SHOT or android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
            else 
                android.app.PendingIntent.FLAG_ONE_SHOT or android.app.PendingIntent.FLAG_UPDATE_CURRENT

            // [FIX] Use getForegroundService (or getService) instead of getActivity
            val pendingIntent = if (Build.VERSION.SDK_INT >= 26) {
                android.app.PendingIntent.getForegroundService(applicationContext, 1, restartIntent, flags)
            } else {
                android.app.PendingIntent.getService(applicationContext, 1, restartIntent, flags)
            }
            
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val triggerTime = System.currentTimeMillis() + 800

            try {
                if (Build.VERSION.SDK_INT >= 23) {
                     alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                     alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } catch (e: SecurityException) {
                alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: Exception) {
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
        try {
            removeOldViews()
            
            if (prefs.prefPersistentService) {
                // KEEP ALIVE ON: Schedule restart and kill process (Auto-Restart)
                scheduleRestart()
                killProcessAndExit()
            } else {
                // KEEP ALIVE OFF: Disable service completely (Terminate)
                // This stops the service and prevents auto-restart by the system
                if (Build.VERSION.SDK_INT >= 24) {
                    disableSelf()
                } else {
                    stopSelf()
                }
                // Optional: Kill process to ensure clean slate
                handler.postDelayed({ 
                    Process.killProcess(Process.myPid()) 
                    System.exit(0)
                }, 100)
            }
        } catch (e: Exception) {
            
        }
    }

    // Helper for Soft Restart and Keep-Alive kills
    private fun killProcessAndExit() {
        stopSelf()
        try { Thread.sleep(200) } catch(e: Exception){}
        Process.killProcess(Process.myPid())
        System.exit(0)
    }
    // =================================================================================
    // FUNCTION: syncMirrorWithPhysicalKeyboard
    // SUMMARY: Updates mirror keyboard dimensions to match physical keyboard.
    // =================================================================================
    fun syncMirrorWithPhysicalKeyboard() {
        mirrorManager?.syncWithPhysicalKeyboard()
    }
    // =================================================================================
    // END BLOCK: syncMirrorWithPhysicalKeyboard
    // =================================================================================
    
    fun manualAdjust(isKeyboard: Boolean, isResize: Boolean, dx: Int, dy: Int) { 
        if (isKeyboard) { 
            if (isResize) keyboardOverlay?.resizeWindow(dx, dy) else keyboardOverlay?.moveWindow(dx, dy)
            // Sync mirror keyboard with physical keyboard changes
            mirrorManager?.syncWithPhysicalKeyboard()
        } 
        else { 
            if (trackpadLayout == null) return
            if (isResize) { trackpadParams.width = max(200, trackpadParams.width + dx); trackpadParams.height = max(200, trackpadParams.height + dy) } 
            { trackpadParams.x += dx; trackpadParams.y += dy }
            try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch (e: Exception) {}; saveCurrentState() 
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
            "bubble_include_trackpad" -> prefs.prefBubbleIncludeTrackpad = parseBoolean(value)
            "bubble_include_keyboard" -> prefs.prefBubbleIncludeKeyboard = parseBoolean(value)
            "block_soft_kb" -> {
 
                prefs.prefBlockSoftKeyboard = parseBoolean(value)
                // Only activate blocking if on cover screen (display 1)
                if (currentDisplayId == 1) {
                    imeManager?.setSoftKeyboardBlocking(prefs.prefBlockSoftKeyboard)
                } else if (prefs.prefBlockSoftKeyboard) {
                    showToast("KB Blocker saved - will activate on cover screen")
                }
            }
            "prediction_aggression" -> { 
                prefs.prefPredictionAggression = (value as? Float) ?: 1.1f


                // Apply immediately to Engine
                PredictionEngine.instance.speedThreshold = prefs.prefPredictionAggression
            }
            // =================================================================================
            // SPACEBAR MOUSE EXTENDED MODE UPDATE
            // =================================================================================
            "spacebar_mouse_extended" -> { 
                prefs.prefSpacebarMouseExtended = parseBoolean(value)
                keyboardOverlay?.getKeyboardView()?.setSpacebarExtendedMode(prefs.prefSpacebarMouseExtended)
                prefs.save(this)
            }
            // =================================================================================
            // END BLOCK: SPACEBAR MOUSE EXTENDED MODE UPDATE
            // =================================================================================
            "keyboard_alpha" -> { prefs.prefKeyboardAlpha = value as Int; keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha) }
            "dock_to_bottom" -> {
                val newDockMode = parseBoolean(value)
                val dockPrefs = getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE)
                val dockOs = if (uiScreenWidth > uiScreenHeight) "_L" else "_P"
                if (newDockMode) {
                    if (!dockPrefs.contains("show_kb_above_dock")) {
                        prefs.prefShowKBAboveDock = true
                        prefs.save(this)
                    }
                    dockPrefs.edit()
                        .putBoolean("dock_mode_d${currentDisplayId}$dockOs", true)
                        .putBoolean("dock_mode_d$currentDisplayId", true)
                        .putBoolean("dock_mode", true)
                        .apply()
                    if (isCustomKeyboardVisible) {
                        if (lastDockMarginPercent >= 0) applyDockModeWithMargin(lastDockMarginPercent)
                        else applyDockMode()
                    }
                } else {
                    dockPrefs.edit()
                        .putBoolean("dock_mode_d${currentDisplayId}$dockOs", false)
                        .putBoolean("dock_mode_d$currentDisplayId", false)
                        .putBoolean("dock_mode", false)
                        .putBoolean("auto_resize$dockOs", false)
                        .putBoolean("auto_resize", false)
                        .apply()
                    lastDockMarginPercent = -1
                    showToast("Dock mode disabled")
                }
            }

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
                val alpha = v / 255f
                mirrorManager?.setAlpha(alpha)
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
            "override_system_shortcuts" -> {
                prefs.prefOverrideSystemShortcuts = parseBoolean(value)
                keyboardOverlay?.setOverrideSystemShortcuts(prefs.prefOverrideSystemShortcuts)
                // Sync launcher blocked shortcuts to keyboard
                keyboardOverlay?.setLauncherBlockedShortcuts(launcherBlockedShortcuts)
            }
        }
        prefs.save(this) 
    }
    
// =================================================================================
    // FUNCTION: applyDockMode
    // SUMMARY: Snaps the overlay keyboard to the bottom of the screen, full width.
    //          Used when "Dock KB to Bottom" is enabled in Dock IME popup.
    // =================================================================================
    internal fun applyDockMode() {
        if (keyboardOverlay == null) initCustomKeyboard()
        if (!isCustomKeyboardVisible) {
            keyboardOverlay?.show()
            isCustomKeyboardVisible = true
        }
        
        val density = resources.displayMetrics.density
        val screenWidth = uiScreenWidth
        val screenHeight = uiScreenHeight
        
        // Get current keyboard height or use default
        val kbHeight = keyboardOverlay?.getViewHeight() ?: ((275f * (prefs.prefKeyScale / 100f) * density).toInt())
        
        // Nav bar height — use runtime value from DockIME broadcast, fall back to system resource
        val navBarHeight = getNavBarHeight()
        
        // Position at bottom, full width (100%)
        val targetW = screenWidth
        var targetY = screenHeight - kbHeight
        
        // If "Show KB Above Dock" is enabled AND DockIME is actually visible,
        // position above the DockIME toolbar (40dp) and nav bar. Otherwise position at true bottom.
        if (prefs.prefShowKBAboveDock && isDockIMEVisible) {
            val dockToolbarHeight = (40 * density).toInt()
            targetY = screenHeight - kbHeight - dockToolbarHeight - navBarHeight
        }
        
        keyboardOverlay?.setWindowBounds(0, targetY, targetW, kbHeight)
        
        // Save keyboard height for Dock IME auto-resize feature
        saveKeyboardHeightForDock(kbHeight)
        
        showToast("Keyboard docked to bottom")
    }
    
    // =================================================================================
    // FUNCTION: applyDockModeWithMargin
    // SUMMARY: Docks keyboard to bottom and resizes it to fit the specified margin %.
    //          Uses setWindowBoundsWithScale() to atomically set window size AND key scale
    //          so they stay perfectly in sync.
    // =================================================================================
    internal fun applyDockModeWithMargin(marginPercent: Int) {
        logOverlayKbDiag("applyDockModeWithMargin_start", "marginPercent=$marginPercent")
        if (keyboardOverlay == null) initCustomKeyboard()
        if (!isCustomKeyboardVisible) {
            keyboardOverlay?.show()
            isCustomKeyboardVisible = true
        }
        
        // Save manual key scale before overwriting (for restore when auto resize is turned off)
        if (manualKeyScaleBeforeMargin < 0) {
            manualKeyScaleBeforeMargin = prefs.prefKeyScale
        }
        
        val density = resources.displayMetrics.density
        val screenWidth = uiScreenWidth
        val screenHeight = uiScreenHeight
        
        // Nav bar height — use runtime value from DockIME broadcast, fall back to system resource
        val navBarHeight = getNavBarHeight()
        
        // Calculate keyboard height from margin percentage
        // Only account for dock toolbar if it's enabled AND DockIME is actually visible
        val dockToolbarHeight = if (prefs.prefShowKBAboveDock && isDockIMEVisible) (40 * density).toInt() else 0
        val marginHeight = (screenHeight * (marginPercent / 100f)).toInt()
        val kbHeight = (marginHeight - dockToolbarHeight - navBarHeight).coerceAtLeast((90 * density).toInt()) // Min 90dp
        
        // Position at bottom (above dock toolbar and nav bar only if dock is visible)
        val targetW = screenWidth
        val targetY = screenHeight - kbHeight - dockToolbarHeight - navBarHeight
        
        // Use atomic method that sets BOTH window bounds AND key scale together
        // This prevents desync between window size and key scale
        keyboardOverlay?.setWindowBoundsWithScale(0, targetY, targetW, kbHeight)
        
        // Update local prefs to match (base height is 300dp)
        val baseKbHeight = 300f * density
        prefs.prefKeyScale = ((kbHeight.toFloat() / baseKbHeight) * 100).toInt().coerceIn(30, 150)
        
        // Save keyboard height for Dock IME auto-resize feature
        saveKeyboardHeightForDock(kbHeight)
        
        logOverlayKbDiag("applyDockModeWithMargin_end", "marginPercent=$marginPercent targetW=$targetW kbHeight=$kbHeight targetY=$targetY")
    }
    // =================================================================================
    // END BLOCK: applyDockModeWithMargin
    // =================================================================================
    
    // =================================================================================
    // FUNCTION: saveKeyboardHeightForDock
    // SUMMARY: Saves the current keyboard height so Dock IME can use it for auto-resize.
    // =================================================================================
    internal fun getNavBarHeight(): Int {
        if (dockNavBarHeight >= 0) return dockNavBarHeight
        val navResId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (navResId > 0) resources.getDimensionPixelSize(navResId) else 0
    }

    private fun saveKeyboardHeightForDock(height: Int) {
        val os = if (uiScreenWidth > uiScreenHeight) "_L" else "_P"
        val key = "keyboard_height_d${currentDisplayId}$os"
        getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
            .putInt(key, height)
            .apply()
    }
    // =================================================================================
    // END BLOCK: applyDockMode
    // =================================================================================

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
        updateScrollSize(); updateScrollPosition(); updateHandleSize(); updateLayoutSizes(); prefs.save(this); showToast("Preset Applied")
    }
    
    fun resetBubblePosition() { bubbleParams.x = (uiScreenWidth / 2) + 80; bubbleParams.y = uiScreenHeight / 2; try { windowManager?.updateViewLayout(bubbleView, bubbleParams) } catch(e: Exception){}; prefs.prefBubbleX = bubbleParams.x; prefs.prefBubbleY = bubbleParams.y; prefs.save(this); showToast("Bubble Reset") }




    


    // =================================================================================
    // PROFILE STATE SAVE DELEGATION
    // =================================================================================
    internal fun saveCurrentState() = layoutStateStore.saveCurrentState()
    fun saveLayout() = layoutStateStore.saveLayout()




    private fun bindShizuku() { try { val c = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(c, userServiceConnection, BuildConfig.DEBUG, BuildConfig.VERSION_CODE) } catch (e: Exception) {  } }

    // Helper to retry binding if connection is dead/null




    private fun checkAndBindShizuku() {
        // 1. If already bound and alive, do nothing
        if (shellService != null && shellService!!.asBinder().isBinderAlive) {
            return
        }

        // 2. If dead but not null, clear it
        if (shellService != null && !shellService!!.asBinder().isBinderAlive) {
            isBound = false
            shellService = null
        }

        // Use existing safe bind method
        bindShizuku() 
        
        // 3. Retry after 2.5 seconds (The Critical Fix)
        // We use 'handler' here (not uiHandler)
        handler.postDelayed({
            if (shellService == null) {
                bindShizuku()
            }
        }, 2500)
    }





    private fun createNotification() { 
        try {
            val channel = NotificationChannel("overlay_service", "Trackpad", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            val notif = Notification.Builder(this, "overlay_service").setContentTitle("Trackpad Active").setSmallIcon(R.drawable.ic_cursor).build()
            try { if (Build.VERSION.SDK_INT >= 34) startForeground(1, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(1, notif) } catch(e: Exception) { startForeground(1, notif) }
        } catch(e: Exception) {  }
    }



    internal fun initCustomKeyboard() { 
        if (appWindowManager == null || shellService == null) return
        
        // Defensive cleanup — prevent orphaned windows
        try { keyboardOverlay?.hide() } catch (e: Exception) {}
        keyboardOverlay = null
        
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
        // ARROW KEYS SWIPE CALLBACK - SPACEBAR MOUSE MODE
        // SUMMARY: When arrow keys pressed in spacebar mouse mode, perform swipe.
        //          dx/dy are -1, 0, or 1. Uses BASE_SWIPE_DISTANCE * scrollSpeed.
        // =================================================================================
        keyboardOverlay?.onArrowSwipe = { dx, dy ->
            val distance = BASE_SWIPE_DISTANCE * prefs.scrollSpeed
            inputHandler.performSwipe(cursorX, cursorY, dx * distance, dy * distance, inputTargetDisplayId)
        }

        // =================================================================================
        // MOUSE SCROLL CALLBACK
        // SUMMARY: Forward mouse wheel events from keyboard overlay to target display
        // =================================================================================
        keyboardOverlay?.onMouseScroll = { h, v ->
            // Scale scroll speed (e.g. 10x for reasonable scroll amount)
            injectScroll(h * 10f, v * 10f)
        }
        // =================================================================================
        // END BLOCK: ARROW KEYS SWIPE CALLBACK
        // =================================================================================

        // =================================================================================
        // VIRTUAL MIRROR TOUCH CALLBACK
        // SUMMARY: Wire up the mirror touch callback to forward touch events from the
        //          physical keyboard to the mirror keyboard on the remote display.
        //          Returns true if the touch should be consumed (orientation mode active).
        // =================================================================================
        keyboardOverlay?.onMirrorTouch = { x, y, action ->
            mirrorManager?.onTouch(x, y, action) ?: false
        }
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR TOUCH CALLBACK
        // =================================================================================

        // Wire up layer change callback for mirror keyboard sync
        keyboardOverlay?.onLayerChanged = { state ->
            mirrorManager?.syncKeyboardLayer(state)
        }

        // =================================================================================
        // MIRROR SUGGESTIONS SYNC CALLBACK
        // SUMMARY: When suggestions change on physical keyboard, sync to mirror keyboard.
        //          This keeps the prediction bar in sync on both displays.
        // =================================================================================
        keyboardOverlay?.onSuggestionsChanged = { suggestions ->
            mirrorManager?.setSuggestions(suggestions as List<KeyboardView.Candidate>)
        }
        // =================================================================================
        // END BLOCK: MIRROR SUGGESTIONS SYNC CALLBACK
        // =================================================================================

        // =================================================================================
        // PHYSICAL KEYBOARD SIZE/POSITION CHANGE CALLBACK
        // SUMMARY: When physical keyboard is moved/resized, sync mirror keyboard.
        // =================================================================================
        keyboardOverlay?.onSizeChanged = {
            mirrorManager?.syncWithPhysicalKeyboard()
        }
        // =================================================================================
        // END BLOCK: PHYSICAL KEYBOARD SIZE/POSITION CHANGE CALLBACK
        // =================================================================================

        // Position and size are handled by KeyboardOverlay.createKeyboardWindow() on show(),
        // which loads per-display prefs (keyboard_x_d$displayId, etc.).
        // Profile restore writes correct values via setWindowBounds() directly.
        // Do NOT override position here — it corrupts per-display prefs when display changes
        // (savedKb* from old display gets written to new display's prefs).

        // [REMOVED] Do not force scale here. 
        // KeyboardOverlay loads the fresh "keyboard_key_scale" from disk automatically.
        // Forcing 'prefs.prefKeyScale' here causes bugs because 'prefs' might be stale.
        // =================================================================================
        // SPACEBAR MOUSE EXTENDED MODE INITIALIZATION
        // =================================================================================
        keyboardOverlay?.getKeyboardView()?.setSpacebarExtendedMode(prefs.prefSpacebarMouseExtended)
        // =================================================================================
        // END BLOCK: SPACEBAR MOUSE EXTENDED MODE INITIALIZATION
        // =================================================================================

        // Set override system shortcuts preference
        keyboardOverlay?.setOverrideSystemShortcuts(prefs.prefOverrideSystemShortcuts)

        // Sync launcher blocked shortcuts to keyboard
        keyboardOverlay?.setLauncherBlockedShortcuts(launcherBlockedShortcuts)

        // Re-apply saved custom mod key
        keyboardOverlay?.setCustomModKey(prefs.customModKey)

        // Force initial visibility based on flag
        if (isCustomKeyboardVisible) {
            keyboardOverlay?.show()
        }
    }




    fun toggleCustomKeyboard(suppressAutomation: Boolean = false) {
        logOverlayKbDiag("toggleCustomKeyboard_start", "suppressAutomation=$suppressAutomation currentlyVisible=${keyboardOverlay?.isShowing() == true}")
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
        
        // [FIX] When manually showing, reapply dock mode positioning
        // Only dock if DockIME is the current default input method AND dock_mode is enabled.
        // This prevents: (1) Gboard triggering dock snapping via stale state,
        // (2) Bubble icon docking when dock mode is toggled off in overlay menu.
        val currentIme = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.DEFAULT_INPUT_METHOD)
        val isDockIMEConfigured = currentIme?.contains("DroidOSTrackpadKeyboard") == true
        val dockModePrefs = getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE)
        val isDockModeEnabled = dockModePrefs.getBoolean("dock_mode_d$currentDisplayId", dockModePrefs.getBoolean("dock_mode", false))
        if (isNowVisible && prefs.prefShowKBAboveDock && isDockIMEConfigured && isDockModeEnabled) {
            if (lastDockMarginPercent >= 0) {
                logOverlayKbDiag("toggleCustomKeyboard_dockDecision", "mode=margin margin=$lastDockMarginPercent")
                applyDockModeWithMargin(lastDockMarginPercent)
            } else {
                logOverlayKbDiag("toggleCustomKeyboard_dockDecision", "mode=defaultDock")
                applyDockMode()
            }
        } else {
            logOverlayKbDiag(
                "toggleCustomKeyboard_dockDecision",
                "skipped isNowVisible=$isNowVisible showAboveDock=${prefs.prefShowKBAboveDock} dockImeConfigured=$isDockIMEConfigured dockModeEnabled=$isDockModeEnabled"
            )
        }
        
        enforceZOrder()
        logOverlayKbDiag("toggleCustomKeyboard_end", "isNowVisible=$isNowVisible")

        // Notify launcher so auto-adjust margin retiles apps when KB is toggled
        // IS_TILED=false triggers 100ms delay for fullscreen apps to let Android handle insets first
        // FORCE_RETILE ensures retile happens on manual hide even if margin was already 0
        val imeIntent = android.content.Intent("com.katsuyamaki.DroidOSLauncher.IME_VISIBILITY")
        imeIntent.setPackage("com.katsuyamaki.DroidOSLauncher")
        imeIntent.putExtra("VISIBLE", isNowVisible)
        imeIntent.putExtra("IS_TILED", false)
        imeIntent.putExtra("MANUAL_TOGGLE", true)
        if (!isNowVisible) {
            imeIntent.putExtra("FORCE_RETILE", true)
        }
        sendBroadcast(imeIntent)
        
        // [FIX] For fullscreen apps: sync DockIME visibility with overlay
        if (!isNowVisible) {
            // Hide: also hide DockIME so Android recalculates insets
            isDockIMEVisible = false
            val hideIntent = android.content.Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.HIDE_DOCK_IME")
            hideIntent.setPackage(packageName)
            sendBroadcast(hideIntent)
        }
        
        if (prefs.prefAutomationEnabled && !suppressAutomation) { 
            if (isNowVisible) turnScreenOn() else turnScreenOff() 
        }
    }

    private fun turnScreenOn() { isScreenOff = false; Thread { try { shellService?.setBrightness(128); shellService?.setScreenOff(0, false) } catch(e: Exception) {} }.start(); showToast("Screen On") }
    private fun turnScreenOff() { isScreenOff = true; Thread { try { if (prefs.prefUseAltScreenOff) shellService?.setBrightness(-1) else shellService?.setScreenOff(0, true) } catch(e: Exception) {} }.start(); showToast("Screen Off (${if(prefs.prefUseAltScreenOff) "Alt" else "Std"})") }
    private fun toggleScreenMode() { prefs.prefUseAltScreenOff = !prefs.prefUseAltScreenOff; prefs.save(this); showToast("Mode: ${if(prefs.prefUseAltScreenOff) "Alternate" else "Standard"}") }
    private fun toggleScreen() { if (isScreenOff) turnScreenOn() else turnScreenOff() }
    
    private fun updateUiMetrics() { val display = displayManager?.getDisplay(currentDisplayId) ?: return; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); uiScreenWidth = metrics.widthPixels; uiScreenHeight = metrics.heightPixels }
    // NOTE: createTrackpadDisplayContext moved to helper methods section as internal
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
            MotionEvent.ACTION_DOWN -> { isVScrolling = true; lastTouchY = event.y; scrollAccumulatorY = 0f; vScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt()); if (prefs.prefTapScroll) { val h = vScrollContainer?.height ?: return; val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed; inputHandler.performSwipe(cursorX, cursorY, 0f, if (event.y < h/2) (if (prefs.prefReverseScroll) -dist else dist) else (if (prefs.prefReverseScroll) dist else -dist), inputTargetDisplayId) } }
            MotionEvent.ACTION_MOVE -> { if (isVScrolling && !prefs.prefTapScroll) { val dy = event.y - lastTouchY; scrollAccumulatorY += dy * prefs.scrollSpeed; if (abs(scrollAccumulatorY) > 30f) { inputHandler.performSwipe(cursorX, cursorY, 0f, if (prefs.prefReverseScroll) -scrollAccumulatorY * 2 else scrollAccumulatorY * 2, inputTargetDisplayId); scrollAccumulatorY = 0f }; lastTouchY = event.y } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isVScrolling = false; scrollAccumulatorY = 0f; vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt()) }
        }
    }
    
    private fun handleHScrollTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { isHScrolling = true; lastTouchX = event.x; scrollAccumulatorX = 0f; hScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt()); if (prefs.prefTapScroll) { val w = hScrollContainer?.width ?: return; val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed; inputHandler.performSwipe(cursorX, cursorY, if (event.x < w/2) (if (prefs.prefReverseScroll) -dist else dist) else (if (prefs.prefReverseScroll) dist else -dist), 0f, inputTargetDisplayId) } }
            MotionEvent.ACTION_MOVE -> { if (isHScrolling && !prefs.prefTapScroll) { val dx = event.x - lastTouchX; scrollAccumulatorX += dx * prefs.scrollSpeed; if (abs(scrollAccumulatorX) > 30f) { inputHandler.performSwipe(cursorX, cursorY, if (prefs.prefReverseScroll) -scrollAccumulatorX * 2 else scrollAccumulatorX * 2, 0f, inputTargetDisplayId); scrollAccumulatorX = 0f }; lastTouchX = event.x } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isHScrolling = false; scrollAccumulatorX = 0f; hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt()) }
        }
    }

    private fun updateScrollPosition() { layoutManager?.updateScrollPosition() }
    private fun updateHandleSize() { layoutManager?.updateHandleSize() }
    private fun updateScrollSize() {
        prefs.prefScrollTouchSize = prefs.prefScrollTouchSize.coerceIn(40, 180); prefs.prefScrollVisualSize = prefs.prefScrollVisualSize.coerceIn(4, 20); scrollZoneThickness = prefs.prefScrollTouchSize
        vScrollContainer?.let { it.layoutParams.width = prefs.prefScrollTouchSize; it.requestLayout() }; vScrollVisual?.let { it.layoutParams.width = prefs.prefScrollVisualSize; it.requestLayout() }
        hScrollContainer?.let { it.layoutParams.height = prefs.prefScrollTouchSize; it.requestLayout() }; hScrollVisual?.let { it.layoutParams.height = prefs.prefScrollVisualSize; it.requestLayout() }
    }
    private fun updateLayoutSizes() { for (c in handleContainers) { val p = c.layoutParams; p.width = prefs.prefHandleTouchSize; p.height = prefs.prefHandleTouchSize; c.layoutParams = p } }
    private fun updateCursorSize() { layoutManager?.updateCursorSize() }
    private fun updateBorderColor(strokeColor: Int) { layoutManager?.updateBorderColor(strokeColor) }
    


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
                else { layoutManager?.updateRemoteCursorPosition(cursorX.toInt(), cursorY.toInt()) }
                showCursorAndResetFade()
                if (isTouchDragging || isKeyDragging) inputHandler.injectMouse(MotionEvent.ACTION_MOVE, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, SystemClock.uptimeMillis()) else inputHandler.injectMouse(MotionEvent.ACTION_MOVE, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
                lastTouchX = event.x; lastTouchY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable)
                if (!ignoreTouchSequence) {
                    if (isTouchDragging) { inputHandler.injectMouse(MotionEvent.ACTION_UP, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, SystemClock.uptimeMillis()); isTouchDragging = false }
                    else if (!isKeyDragging && SystemClock.uptimeMillis() - touchDownTime < TAP_TIMEOUT_MS && kotlin.math.sqrt((event.x - touchDownX) * (event.x - touchDownX) + (event.y - touchDownY) * (event.y - touchDownY)) < TAP_SLOP_PX) inputHandler.performClick(cursorX, cursorY, inputTargetDisplayId, false)
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
        
        // Hide only bubble-included components
        // [FIX] toggleCustomKeyboard() already sends IME_VISIBILITY broadcast, no need for duplicate
        if (prefs.prefBubbleIncludeKeyboard && isCustomKeyboardVisible) toggleCustomKeyboard()
        if (prefs.prefBubbleIncludeTrackpad && isTrackpadVisible) toggleTrackpad()
        
        handler.post { Toast.makeText(this, "Hidden (Tap Bubble to Restore)", Toast.LENGTH_SHORT).show() }
    }


    fun performSmartRestore() {
        // Always show all bubble-included components
        if (prefs.prefBubbleIncludeTrackpad && !isTrackpadVisible) toggleTrackpad()
        if (prefs.prefBubbleIncludeKeyboard && !isCustomKeyboardVisible) toggleCustomKeyboard()
        hasPendingRestore = false

        // Notify launcher that overlay KB is back (for auto-adjust margin)
        if (isCustomKeyboardVisible) {
            val intent = android.content.Intent("com.katsuyamaki.DroidOSLauncher.IME_VISIBILITY")
            intent.setPackage("com.katsuyamaki.DroidOSLauncher")
            intent.putExtra("VISIBLE", true)
            intent.putExtra("MANUAL_TOGGLE", true)
            sendBroadcast(intent)
        }
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

    fun injectScroll(hScroll: Float, vScroll: Float) {
        inputHandler.injectScroll(cursorX, cursorY, hScroll, vScroll, inputTargetDisplayId)
    }

    // Helper to allow external components (like Keyboard) to control the cursor
    // Added 'isDragging' to switch between Hover (Mouse) and Drag (Touch)
// =================================================================================
    // SPACEBAR MOUSE CURSOR MOVEMENT HANDLER (with BT Mouse Display Sync)
    // SUMMARY: Handles cursor movement from the spacebar trackpad feature AND
    //          Bluetooth mouse input. Updates cursor position and visual, then
    //          injects hover/drag events.
    //
    //          BLUETOOTH MOUSE DISPLAY SYNC: When targeting a virtual display,
    //          uses 'input -d <displayId> mouse move' command to physically move
    //          the system cursor to the target display. This syncs the Android
    //          system cursor with DroidOS's software cursor.
    //
    //          CRITICAL: Skips hover injection when cursor is over keyboard bounds
    //          to prevent feedback loop that causes lag/freezing.
    // =================================================================================

    private var lastMouseMoveTime = 0L

    fun handleExternalMouseMove(dx: Float, dy: Float, isDragging: Boolean) {
        // [EFFICIENCY] Throttle input to ~120Hz (8ms)
        // This prevents flooding the binder transaction buffer
        val now = SystemClock.uptimeMillis()
        if (now - lastMouseMoveTime < 8) return
        lastMouseMoveTime = now


        // Calculate safe bounds based on target display
        val safeW = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth.toFloat() else uiScreenWidth.toFloat()
        val safeH = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight.toFloat() else uiScreenHeight.toFloat()

        // Update software cursor position
        cursorX = (cursorX + dx).coerceIn(0f, safeW)
        cursorY = (cursorY + dy).coerceIn(0f, safeH)

        // Update Visuals (Redraw the cursor icon overlay)
        if (inputTargetDisplayId == currentDisplayId) {
            cursorParams.x = cursorX.toInt()
            cursorParams.y = cursorY.toInt()
            try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch (e: Exception) {}
        } else {
            layoutManager?.updateRemoteCursorPosition(cursorX.toInt(), cursorY.toInt())
        }
        showCursorAndResetFade()

        // =======================================================================
        // BLUETOOTH MOUSE DISPLAY SYNC
        // When targeting a virtual/remote display, move the system mouse cursor
        // to the target display using shell command. This ensures the BT mouse
        // cursor follows our software cursor to the virtual display.
        // =======================================================================
        if (inputTargetDisplayId != currentDisplayId && shellService != null) {
            val dxInt = dx.toInt()
            val dyInt = dy.toInt()
            if (dxInt != 0 || dyInt != 0) {
                Thread {
                    try {
                        // Move system cursor to target display using relative movement
                        shellService?.runCommand("input -d $inputTargetDisplayId mouse move $dxInt $dyInt")
                    } catch (e: Exception) {
                    }
                }.start()
            }
        }
        // =======================================================================
        // END BLOCK: BLUETOOTH MOUSE DISPLAY SYNC
        // =======================================================================

        // [ANTI-FEEDBACK-LOOP FIX]
        // Check if cursor is over the keyboard - if so, skip hover injection
        // This prevents the injected mouse events from creating a feedback loop
        // where the keyboard receives the hover, processes it, and triggers more events
        val isOverKeyboard = isCursorOverKeyboard()

        // Input Injection (for apps that listen to motion events)
        if (isDragging) {
            // TOUCH DRAG: SOURCE_TOUCHSCREEN + ACTION_MOVE
            // Always inject drag events - user explicitly initiated drag
            inputHandler.injectMouse(MotionEvent.ACTION_MOVE, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, 0, SystemClock.uptimeMillis())
        } else {
            // MOUSE HOVER: SOURCE_MOUSE + ACTION_HOVER_MOVE
            // Skip hover injection when over keyboard to prevent feedback loop
            if (!isOverKeyboard) {
                inputHandler.injectMouse(MotionEvent.ACTION_HOVER_MOVE, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
            }
        }
    }
    // =================================================================================
    // END BLOCK: SPACEBAR MOUSE CURSOR MOVEMENT HANDLER (with BT Mouse Display Sync)
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
        inputHandler.injectMouse(MotionEvent.ACTION_DOWN, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, 0, SystemClock.uptimeMillis())
    }

    // Explicit Touch Up (End Drag/Hold)
    fun handleExternalTouchUp() {
        inputHandler.injectMouse(MotionEvent.ACTION_UP, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, 0, SystemClock.uptimeMillis())
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
            } catch (e: Exception) {
            }
        }.start()
    }

    // Keep Right Click for the predictive bar if needed
    fun handleExternalMouseClick(isRight: Boolean) {
        inputHandler.performClick(cursorX, cursorY, inputTargetDisplayId, isRight)
    }

    fun performClick(right: Boolean) { inputHandler.performClick(cursorX, cursorY, inputTargetDisplayId, right) }
    fun resetCursorCenter() { cursorX = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth/2f else uiScreenWidth/2f; cursorY = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight/2f else uiScreenHeight/2f; if (inputTargetDisplayId == currentDisplayId) { cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); windowManager?.updateViewLayout(cursorLayout, cursorParams) } else { layoutManager?.updateRemoteCursorPosition(cursorX.toInt(), cursorY.toInt()) }; showCursorAndResetFade() }
    
    internal fun setCursorPosition(x: Float, y: Float) {
        cursorX = x; cursorY = y
        if (inputTargetDisplayId == currentDisplayId) {
            cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt()
            try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {}
        } else {
            layoutManager?.updateRemoteCursorPosition(cursorX.toInt(), cursorY.toInt())
        }
        showCursorAndResetFade()
    }

    private fun showCursorAndResetFade() {
        // Make cursor visible
        val target = if (inputTargetDisplayId == currentDisplayId) cursorView else layoutManager?.remoteCursorView
        target?.alpha = 1f
        target?.visibility = View.VISIBLE
        // Cancel existing fade timer and start new one
        cursorFadeRunnable?.let { handler.removeCallbacks(it) }
        cursorFadeRunnable = Runnable {
            target?.animate()?.alpha(0f)?.setDuration(500)?.start()
        }
        handler.postDelayed(cursorFadeRunnable!!, CURSOR_FADE_TIMEOUT)
    }

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

    // NOTE: applyMirrorKeyboardSettings moved to MirrorModeManager.kt
    private fun applyMirrorKeyboardSettings() {
        mirrorManager?.applySettings()
    }



    // =================================================================================
    // FUNCTION: loadLayout
    // SUMMARY: Loads a display-specific profile containing trackpad window position/size,
    //          keyboard settings (scale, alpha, position), and visual preferences.
    //          The profile key is based on screen resolution + mirror mode state.
    //          CRITICAL: After loading profile scale, syncs it to global SharedPreferences
    //          so KeyboardOverlay.show() picks up the correct value.
    // =================================================================================
    fun loadLayout() {
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val key = getProfileKey()
        val legacyKey = getLegacyProfileKey()
        val effectiveKey = when {
            p.contains("SETTINGS_$key") || p.contains("X_$key") -> key
            p.contains("SETTINGS_$legacyKey") || p.contains("X_$legacyKey") -> legacyKey
            else -> key
        }
        logOverlayKbDiag("loadLayout_start", "profileKey=$key effectiveKey=$effectiveKey")
        
        // 1. Load Trackpad Window
        trackpadParams.x = p.getInt("X_$effectiveKey", 100)
        trackpadParams.y = p.getInt("Y_$effectiveKey", 100)
        trackpadParams.width = p.getInt("W_$effectiveKey", 400)
        trackpadParams.height = p.getInt("H_$effectiveKey", 300)
        try {
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams)
        } catch(e: Exception){}
        
        // 2. Load Settings String
        val settings = p.getString("SETTINGS_$effectiveKey", null)
        var keyboardUpdated = false

        // If no saved profile for this orientation, apply landscape defaults
        if (settings == null && uiScreenWidth > uiScreenHeight) {
            val dockPrefs = getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE)
            dockPrefs.edit()
                .putBoolean("dock_mode_d${currentDisplayId}_L", true)
                .putBoolean("dock_mode_d$currentDisplayId", true)
                .putBoolean("dock_mode", true)
                .putBoolean("auto_resize_L", true)
                .putBoolean("auto_resize", true)
                .apply()
        }

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
                
                // =================================================================================
                // FIX: SYNC PROFILE SCALE TO SHAREDPREFS
                // SUMMARY: Keep scale strictly display+orientation scoped to avoid
                //          cross-display profile bleed for floating keyboard.
                // =================================================================================
                val orientKey = if (uiScreenWidth > uiScreenHeight) "_L" else "_P"
                p.edit()
                    .putInt("keyboard_key_scale_d${currentDisplayId}$orientKey", prefs.prefKeyScale)
                    .apply()
                logOverlayKbDiag("loadLayout_scaleSync", "prefKeyScale=${prefs.prefKeyScale} orientKey=$orientKey")
                // =================================================================================
                // END BLOCK: SYNC PROFILE SCALE TO SHAREDPREFS
                // =================================================================================
                
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
                

            // [FIX] Load Prediction Aggression (Only if profile is new enough, i.e. has 35+ parts)
            // We check >= 35 because: 30(base) + 1(aggression) + 4(bounds) = 35
            // This prevents reading "X-coordinate" as "Aggression" on legacy profiles.
            if (parts.size >= 35) {
                try {
                    prefs.prefPredictionAggression = parts[30].toFloat()
                    PredictionEngine.instance.speedThreshold = prefs.prefPredictionAggression
                } catch (e: Exception) {
                }
            }

            // Load Physical Keyboard Bounds
            // Logic:
            // Size 35+ -> Newest (Aggression at 30, Bounds at 31)
            // Size 34  -> Previous (Bounds at 30)
            // Size 31  -> Oldest (Bounds at 27)
            var kbIndex = 30
            if (parts.size >= 35) {
                kbIndex = 31
            } else if (parts.size < 34 && parts.size >= 31) {
                kbIndex = 27 
            }
            

            // Ensure we have enough parts for X, Y, W, H
            if (parts.size > kbIndex + 3) {
                 try {
                     savedKbX = parts[kbIndex].toInt()
                     savedKbY = parts[kbIndex+1].toInt()

                     savedKbW = parts[kbIndex+2].toInt(); savedKbH = parts[kbIndex+3].toInt()
                     
                     // Apply bounds + scale atomically from the saved geometry.
                     // This avoids stale profile-scale overrides that can shrink rows in floating mode.
                     keyboardOverlay?.setWindowBoundsWithScale(savedKbX, savedKbY, savedKbW, savedKbH)
                     keyboardUpdated = true
                } catch (e: Exception) { }

                // Load Dock Mode + all dock prefs from profile
                if (parts.size > kbIndex + 4) {
                    try {
                        val profileDockMode = parts[kbIndex + 4] == "1"
                        val dockOs = if (uiScreenWidth > uiScreenHeight) "_L" else "_P"
                        val dockEdit = getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE).edit()
                            .putBoolean("dock_mode_d${currentDisplayId}$dockOs", profileDockMode)
                            .putBoolean("dock_mode_d$currentDisplayId", profileDockMode)
                            .putBoolean("dock_mode", profileDockMode)
                        // Restore extended dock prefs if present (new profile format)
                        if (parts.size > kbIndex + 9) {
                            val pAutoShow = parts[kbIndex + 5] == "1"
                            val pAutoResize = parts[kbIndex + 6] == "1"
                            val pResizeScale = parts[kbIndex + 7].toIntOrNull() ?: 0
                            val pSyncMargin = parts[kbIndex + 8] == "1"
                            val pKBAboveDock = parts[kbIndex + 9] == "1"
                            dockEdit.putBoolean("auto_show_overlay$dockOs", pAutoShow)
                                .putBoolean("auto_show_overlay", pAutoShow)
                                .putBoolean("auto_resize$dockOs", pAutoResize)
                                .putBoolean("auto_resize", pAutoResize)
                                .putInt("auto_resize_scale$dockOs", pResizeScale)
                                .putInt("auto_resize_scale", pResizeScale)
                                .putBoolean("sync_margin$dockOs", pSyncMargin)
                                .putBoolean("sync_margin", pSyncMargin)
                                .putBoolean("show_kb_above_dock$dockOs", pKBAboveDock)
                                .putBoolean("show_kb_above_dock", pKBAboveDock)
                            prefs.prefShowKBAboveDock = pKBAboveDock
                        }
                        dockEdit.apply()
                        if (profileDockMode && isCustomKeyboardVisible) {
                            if (lastDockMarginPercent >= 0) applyDockModeWithMargin(lastDockMarginPercent)
                            else applyDockMode()
                        }
                    } catch (e: Exception) {}
                }

                // Apply Visuals
                updateBorderColor(currentBorderColor); updateScrollSize(); updateHandleSize()
                updateCursorSize(); updateScrollPosition()
                keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha)
                keyboardOverlay?.setAnchored(prefs.prefAnchored)
                keyboardOverlay?.setVibrationEnabled(prefs.prefVibrate)

                layoutManager?.let { lm ->
                    lm.bubbleParams.x = prefs.prefBubbleX.coerceIn(0, (uiScreenWidth - 60).coerceAtLeast(0))
                    lm.bubbleParams.y = prefs.prefBubbleY.coerceIn(0, (uiScreenHeight - 60).coerceAtLeast(0))
                    try { windowManager?.updateViewLayout(bubbleView, lm.bubbleParams) } catch (_: Exception) {}
                    lm.applyBubbleAppearance()
                } ?: applyBubbleAppearance()
            }
        }
    }


        // [FIX] LOAD MIRROR KEYBOARD PARAMS (If in Mirror Mode)
        if (prefs.prefVirtualMirrorMode) {
            if (p.contains("MIRROR_X_$effectiveKey")) {
                prefs.prefMirrorX = p.getInt("MIRROR_X_$effectiveKey", -1)
                prefs.prefMirrorY = p.getInt("MIRROR_Y_$effectiveKey", 0)
                prefs.prefMirrorWidth = p.getInt("MIRROR_W_$effectiveKey", -1)
                prefs.prefMirrorHeight = p.getInt("MIRROR_H_$effectiveKey", -1)
                prefs.prefMirrorAlpha = p.getInt("MIRROR_ALPHA_$effectiveKey", 200)
                
                // Update live window if it exists
                mirrorManager?.applySettings()
            }
        }

        // [FIX] GHOSTING PREVENTION
        // [FIX] Pass preserveScale=true to avoid overwriting user's saved scale when
        // no profile exists for this display. The scale is loaded independently via
        // display-specific keys (keyboard_key_scale_d${displayId}_L/P).
        if (!keyboardUpdated) {
            keyboardOverlay?.resetPosition(preserveScale = true)
            showToast("Defaults Loaded")
        } else {
            showToast("Profile Loaded: ${if(prefs.prefVirtualMirrorMode) "Mirror" else "Std"}")
        }
    }
    // =================================================================================
    // END BLOCK: loadLayout
    // =================================================================================




    fun deleteCurrentProfile() { /* Stub */ }
    fun resetKeyboardPosition() {
        keyboardOverlay?.resetPosition(preserveScale = true)
    }

    fun rotateKeyboard() {
        keyboardOverlay?.cycleRotation()
    }

    fun resetTrackpadPosition() { trackpadParams.x = 100; trackpadParams.y = 100; trackpadParams.width = 400; trackpadParams.height = 300; windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }    // =================================================================================
    // NOTE: showMirrorTemporarily moved to MirrorModeManager.kt
    // =================================================================================
// =================================================================================
    // FUNCTION: adjustMirrorKeyboard
    // SUMMARY: Adjusts mirror keyboard position or size via D-pad controls.
    //          FIXED: Correct Y direction for both move and resize.
    //          FIXED: Handle WRAP_CONTENT properly for resize.
    // =================================================================================
    fun adjustMirrorKeyboard(isResize: Boolean, deltaX: Int, deltaY: Int) {
        mirrorManager?.adjust(isResize, deltaX, deltaY)
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
        mirrorManager?.resetPosition()
    }
    // =================================================================================
    // END BLOCK: resetMirrorKeyboardPosition
    // =================================================================================
    fun cycleInputTarget() {
        if (displayManager == null) return; val displays = displayManager!!.displays; var nextId = -1
        for (d in displays) { if (d.displayId != currentDisplayId) { if (inputTargetDisplayId == currentDisplayId) { nextId = d.displayId; break } else if (inputTargetDisplayId == d.displayId) { continue } else { nextId = d.displayId } } }
        
        if (nextId == -1) { 
            inputTargetDisplayId = currentDisplayId; targetScreenWidth = uiScreenWidth; targetScreenHeight = uiScreenHeight; removeRemoteCursor(); mirrorManager?.removeMirrorKeyboard(); btMouseManager?.removeBtMouseCaptureOverlay(); cursorX = uiScreenWidth / 2f; cursorY = uiScreenHeight / 2f; cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception){}; cursorView?.visibility = View.VISIBLE; updateBorderColor(0x55FFFFFF.toInt()); showToast("Target: Local (Display $currentDisplayId)"); updateWakeLockState() 
        } else { 
            inputTargetDisplayId = nextId; updateTargetMetrics(nextId); createRemoteCursor(nextId); updateVirtualMirrorMode(); btMouseManager?.createBtMouseCaptureOverlay(); cursorX = targetScreenWidth / 2f; cursorY = targetScreenHeight / 2f; layoutManager?.updateRemoteCursorPosition(cursorX.toInt(), cursorY.toInt()); cursorView?.visibility = View.GONE; updateBorderColor(0xFFFF00FF.toInt()); showToast("Target: Display $nextId"); updateWakeLockState() 
        }
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
        } catch (e: Exception) {
        }
    }

    private fun releaseDisplayWakeLock() {
        try {
            if (displayWakeLock?.isHeld == true) {
                displayWakeLock?.release()
            }
        } catch (e: Exception) {
        }
        displayWakeLock = null
    }

    internal fun pingUserActivity() {
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

    private fun createRemoteCursor(displayId: Int) { layoutManager?.createRemoteCursor(displayId) }
    private fun removeRemoteCursor() { layoutManager?.removeRemoteCursor() }

    // =================================================================================
    // HELPER: Forward Touch to Sibling Windows
    // Used by BT Mouse Capture Overlay to let finger touches reach Trackpad/Keyboard
    // =================================================================================
    internal fun forwardTouchToSiblings(event: MotionEvent): Boolean {
        var handled = false
        val rawX = event.rawX
        val rawY = event.rawY

        // 1. Try Menu (Highest Priority)
        if (!handled) {
            handled = menuManager?.dispatchTouchToView(event) == true
        }

        // 2. Try Bubble (High Priority - floats above KB/Trackpad)
        if (!handled && bubbleView != null && bubbleView?.isAttachedToWindow == true) {
             val bView = bubbleView!!
             val loc = IntArray(2)
             bView.getLocationOnScreen(loc)
             val x = loc[0].toFloat()
             val y = loc[1].toFloat()
             
             if (rawX >= x && rawX <= x + bView.width && rawY >= y && rawY <= y + bView.height) {
                 event.offsetLocation(-x, -y)
                 handled = bView.dispatchTouchEvent(event)
                 event.offsetLocation(x, y) // Restore
             }
        }

        // 3. Try Keyboard
        if (!handled && isCustomKeyboardVisible) {
            val kbContainer = keyboardOverlay?.getContainerView()
            if (kbContainer != null && kbContainer.isAttachedToWindow && keyboardOverlay?.isShowing() == true) {
                val loc = IntArray(2)
                kbContainer.getLocationOnScreen(loc)
                val x = loc[0].toFloat()
                val y = loc[1].toFloat()
                
                if (rawX >= x && rawX <= x + kbContainer.width && rawY >= y && rawY <= y + kbContainer.height) {
                    event.offsetLocation(-x, -y)
                    handled = kbContainer.dispatchTouchEvent(event)
                    event.offsetLocation(x, y) // Restore
                }
            }
        }

        // 3. Try Trackpad
        if (!handled && isTrackpadVisible && trackpadLayout != null) {
            val tpView = trackpadLayout!!
            if (tpView.isAttachedToWindow && tpView.visibility == View.VISIBLE) {
                val loc = IntArray(2)
                tpView.getLocationOnScreen(loc)
                val x = loc[0].toFloat()
                val y = loc[1].toFloat()
                
                if (rawX >= x && rawX <= x + tpView.width && rawY >= y && rawY <= y + tpView.height) {
                    event.offsetLocation(-x, -y)
                    handled = tpView.dispatchTouchEvent(event)
                    event.offsetLocation(x, y) // Restore
                }
            }
        }

        return handled
    }



    // =================================================================================
    // VIRTUAL MIRROR MODE FUNCTIONS
    // =================================================================================

    /**
     * Creates or removes the mirror keyboard based on Virtual Mirror Mode preference.
     * Called when the preference changes or when switching displays.
     */
    private fun updateVirtualMirrorMode() {
        mirrorManager?.update()
    }

    // NOTE: createMirrorKeyboard, removeMirrorKeyboard, updateMirrorSyncDimensions, 
    //       syncMirrorKeyboardLayer, onMirrorKeyboardTouch, stopMirrorKeyRepeat,
    //       clearMirrorTrail, isVirtualMirrorModeActive moved to MirrorModeManager.kt

    /**
     * Returns true if currently in orientation mode (showing orange trail, blocking input).
     */
    fun isCurrentlyInOrientationMode(): Boolean {
        return mirrorManager?.isInOrientationMode() ?: false
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
        mirrorManager?.toggle()
    }


    // =================================================================================
    // END BLOCK: FUNCTION toggleVirtualMirrorMode
    // =================================================================================

    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE FUNCTIONS
    // =================================================================================

    private fun startTouchDrag() { if (ignoreTouchSequence || isTouchDragging) return; isTouchDragging = true; activeDragButton = MotionEvent.BUTTON_PRIMARY; dragDownTime = SystemClock.uptimeMillis(); inputHandler.injectMouse(MotionEvent.ACTION_DOWN, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, dragDownTime); hasSentTouchDown = true; if (prefs.prefVibrate) vibrate(); updateBorderColor(0xFFFF9900.toInt()) }
    private fun startResize() {}
    private fun startMove() {}
    private fun startKeyDrag(button: Int) { vibrate(); updateBorderColor(0xFF00FF00.toInt()); isKeyDragging = true; activeDragButton = button; dragDownTime = SystemClock.uptimeMillis(); inputHandler.injectMouse(MotionEvent.ACTION_DOWN, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime); hasSentMouseDown = true }
    private fun stopKeyDrag(button: Int) { if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt()); isKeyDragging = false; if (hasSentMouseDown) { inputHandler.injectMouse(MotionEvent.ACTION_UP, cursorX, cursorY, inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime) }; hasSentMouseDown = false }
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
    internal fun setTrackpadVisibility(visible: Boolean) { isTrackpadVisible = visible; if (trackpadLayout != null) trackpadLayout?.visibility = if (visible) View.VISIBLE else View.GONE }
    internal fun setPreviewMode(preview: Boolean) { isPreviewMode = preview; trackpadLayout?.alpha = if (preview) 0.5f else 1.0f }

    // =================================================================================
    // MIRROR MODE HELPER METHODS
    // These methods expose internal state/functionality to MirrorModeManager
    // =================================================================================
    internal var preMirrorTrackpadVisible = false
    internal var preMirrorKeyboardVisible = false
    internal var preMirrorTargetDisplayId = 0

    internal fun storePreMirrorState() {
        preMirrorTrackpadVisible = isTrackpadVisible
        preMirrorKeyboardVisible = isCustomKeyboardVisible
        preMirrorTargetDisplayId = inputTargetDisplayId
    }

    internal fun restorePreMirrorVisibility() {
        if (isTrackpadVisible != preMirrorTrackpadVisible) toggleTrackpad()
        if (isCustomKeyboardVisible != preMirrorKeyboardVisible) toggleCustomKeyboard(suppressAutomation = true)
    }

    internal fun switchToRemoteDisplay(displayId: Int) {
        inputTargetDisplayId = displayId
        updateTargetMetrics(inputTargetDisplayId)
        createRemoteCursor(inputTargetDisplayId)
        cursorX = targetScreenWidth / 2f
        cursorY = targetScreenHeight / 2f
        layoutManager?.updateRemoteCursorPosition(cursorX.toInt(), cursorY.toInt())
        cursorView?.visibility = View.GONE
        updateBorderColor(0xFFFF00FF.toInt())
    }

    internal fun switchToLocalDisplay() {
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
    }

    internal fun createTrackpadDisplayContext(display: Display): Context {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            createDisplayContext(display).createWindowContext(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null)
        } else {
            createDisplayContext(display)
        }
    }
    // =================================================================================
    // END BLOCK: MIRROR MODE HELPER METHODS
    // =================================================================================
    override fun onDisplayAdded(displayId: Int) {}
    override fun onDisplayRemoved(displayId: Int) {}
    override fun onDisplayChanged(displayId: Int) {
        logOverlayKbDiag("onDisplayChanged_enter", "eventDisplay=$displayId")
        // =================================================================================
        // VIRTUAL DISPLAY PROTECTION
        // SUMMARY: Skip auto-switch logic when targeting a virtual display (ID >= 2).
        //          This prevents the overlay from "crashing" back to physical screens
        //          when display states flicker during virtual display use.
        // =================================================================================
        if (inputTargetDisplayId >= 2) {
            return
        }
        // =================================================================================
        // END BLOCK: VIRTUAL DISPLAY PROTECTION
        // =================================================================================

        // ORIENTATION CHANGE: Full UI rebuild when screen dimensions change
        if (displayId == currentDisplayId) {
            val display = displayManager?.getDisplay(currentDisplayId)
            if (display != null) {
                val metrics = android.util.DisplayMetrics()
                display.getRealMetrics(metrics)
                val w = metrics.widthPixels; val h = metrics.heightPixels
                if (lastKnownScreenW != 0 && lastKnownScreenH != 0 &&
                    (w != lastKnownScreenW || h != lastKnownScreenH)) {
                    // =================================================================================
                    // FIX: Don't reload full profile on orientation/display change.
                    // Profile should only load on: (1) app restart, (2) manual reload request.
                    // Just save current state and rebuild UI - setupUI loads per-display prefs.
                    // This prevents scale being overwritten by profile defaults on every fold/unfold.
                    // =================================================================================
                    logOverlayKbDiag("onDisplayChanged_orientationRebuild", "old=${lastKnownScreenW}x${lastKnownScreenH} new=${w}x${h}")
                    saveCurrentState()
                    lastKnownScreenW = w; lastKnownScreenH = h
                    handler.post { setupUI(currentDisplayId) }
                } else {
                    lastKnownScreenW = w; lastKnownScreenH = h
                }
            }
        }

        // We only monitor the Main Screen (0) state changes to determine "Open/Closed"
        if (displayId == 0) {
            val display = displayManager?.getDisplay(0)
            val isDebounced = (System.currentTimeMillis() - lastManualSwitchTime > 5000)
            
            if (display != null && isDebounced) {
                // =================================================================================
                // CASE A: Phone Opened (Display 0 turned ON) -> Move to Main (0)
                // SUMMARY: When user unfolds the phone, switch UI to main screen.
                //          ALSO: Fight Samsung's keyboard takeover with delayed restoration.
                // =================================================================================
                if (display.state == Display.STATE_ON && currentDisplayId != 0) {
                    handler.postDelayed({
                        try {
                            val timeSinceManual = System.currentTimeMillis() - lastManualSwitchTime
                            if (timeSinceManual > 5000) {
                                // Pre-restore showMode
                                if (prefs.prefBlockSoftKeyboard) {
                                    if (Build.VERSION.SDK_INT >= 24) {
                                        try {
                                            softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO
                                        } catch (e: Exception) {}
                                    }
                                }

                                saveCurrentState()
                                setupUI(0)
                            }
                        } catch(e: Exception) {
                        }

                    }, 500)
                }
                    
                // =================================================================================
                // END BLOCK: CASE A - Phone Opened
                // =================================================================================

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
                                    saveCurrentState()
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
        pendingForceHideRunnable?.let { handler.removeCallbacks(it) }
        pendingForceHideRunnable = null
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
        if (::inputHandler.isInitialized) inputHandler.shutdown() // Stop the input handler executor
        if (Build.VERSION.SDK_INT >= 24) { try { softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO } catch (e: Exception) {} }
        Thread { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") }.start()
        try { unregisterReceiver(switchReceiver) } catch(e: Exception){};
        try { val im = getSystemService(Context.INPUT_SERVICE) as InputManager; im.unregisterInputDeviceListener(this) } catch (e: Exception) {}
        if (isBound) ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection)
        imeManager?.stopMonitoring()
    }

    fun forceSystemKeyboardVisible() {
        Thread {
            try {
                // Force setting to 1 (Show)
                shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1")
            } catch(e: Exception) {}
        }.start()
    }

    internal fun showToast(msg: String) { handler.post { android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show() } }
    private fun updateTargetMetrics(displayId: Int) { val display = displayManager?.getDisplay(displayId) ?: return; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); targetScreenWidth = metrics.widthPixels; targetScreenHeight = metrics.heightPixels }
    


    // =================================================================================
    // FUNCTION: injectKeyFromKeyboard
    // Delegates to ShizukuInputHandler - dismiss voice first then inject
    // =================================================================================
    fun injectKeyFromKeyboard(keyCode: Int, metaState: Int) {
        lastInjectionTime = System.currentTimeMillis()
        checkAndDismissVoice()
        inputHandler.injectKeyFromKeyboard(keyCode, metaState)
    }

    fun injectBulkDelete(length: Int) {
        lastInjectionTime = System.currentTimeMillis()
        inputHandler.injectBulkDelete(length)
    }

    fun injectText(text: String) {
        lastInjectionTime = System.currentTimeMillis()
        inputHandler.injectText(text)
    }

    // =================================================================================
    // INPUT DEVICE LISTENER
    // SUMMARY: Detects mouse connection/disconnection. Re-applies cursor hiding
    //          because Android resets cursor visibility when a mouse reconnects.
    // =================================================================================
    override fun onInputDeviceAdded(deviceId: Int) { handleInputDeviceChange() }
    override fun onInputDeviceRemoved(deviceId: Int) { handleInputDeviceChange() }
    override fun onInputDeviceChanged(deviceId: Int) { handleInputDeviceChange() }

    private fun handleInputDeviceChange() {
        btMouseManager?.handleInputDeviceChange()
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
