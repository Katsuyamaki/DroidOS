package com.example.coverscreentester

/*
 * ======================================================================================
 * CRITICAL REGRESSION CHECKLIST
 * ======================================================================================
 * 1. SCROLL: Edge Zones (15%) + Tap/Hold logic preserved.
 * 2. DRAG: Long Press (400ms) + SOURCE_TOUCHSCREEN injection preserved.
 * 3. CURSOR: SOURCE_MOUSE for hover. No offsets.
 * 4. KEYS: Vol Up/Down preserved.
 * 5. SAFETY: Shell checks + Vibrate preserved.
 * 6. PREFS: Safe parsing preserved.
 * 7. REMOTE: cycleInputTarget creates remote cursor on secondary context.
 * 8. AUTOMATION: Keyboard Open = Screen ON. Keyboard Close = Screen OFF (Alt/Std).
 * 9. MENU ACTIONS: hideApp() minimizes, forceExit() kills process.
 * 10. ENTRY POINT: "OPEN_MENU" action triggers menuManager.show().
 * 11. FULL SCREEN: NO_LIMITS + SHORT_EDGES flags required for cursor to reach edges.
 * 12. PRESETS: applyLayoutPreset(1|2|0) supported. 0 is Freeform.
 * 13. LAUNCH: FORCE_MOVE extra forces UI refresh on current display.
 * 14. DISPLAY CONTEXT: WindowManager MUST be obtained from display-specific context.
 * 15. FOREGROUND: Safe startForeground with try-catch fallback.
 * 16. MULTI-DISPLAY: Teardown old views before switching displays.
 * 17. BUBBLE: resetBubblePosition() added.
 * 18. HANDLE: Size scaled 2x, Touch size synced.
 * 19. NO VIBRATE: Removed debug vibrations from menu/toggles.
 * 20. SCROLL FIX: performSwipe restored. ACTION_DOWN checks prefTapScroll.
 * 21. MANUAL ADJUST: manualAdjust() uses separate Move vs Resize logic via Toggle.
 * ======================================================================================
 */

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
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import java.util.ArrayList
import com.example.coverscreentester.BuildConfig

class OverlayService : AccessibilityService(), DisplayManager.DisplayListener {

    var windowManager: WindowManager? = null
    var displayManager: DisplayManager? = null
    var shellService: IShellService? = null
    private var isBound = false
    private val handler = Handler(Looper.getMainLooper())

    private var bubbleView: View? = null
    private var trackpadLayout: FrameLayout? = null
    private var cursorLayout: FrameLayout? = null
    private var cursorView: ImageView? = null
    
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var trackpadParams: WindowManager.LayoutParams
    private lateinit var cursorParams: WindowManager.LayoutParams

    private var menuManager: TrackpadMenuManager? = null
    private var keyboardOverlay: KeyboardOverlay? = null

    var currentDisplayId = 0
    var inputTargetDisplayId = 0
    var isTrackpadVisible = true
    var isCustomKeyboardVisible = false
    var isScreenOff = false
    private var isPreviewMode = false
    
    // =========================
    // PREFS CLASS - Stores all user preferences
    // Contains anchor mode to lock trackpad/keyboard position
    // Contains hardkey binding mappings for Vol Up/Down and Power
    // =========================
    class Prefs {
        var cursorSpeed = 2.5f
        var scrollSpeed = 3.0f 
        var prefTapScroll = true 
        var prefVibrate = false
        var prefReverseScroll = true
        var prefAlpha = 200
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
        var prefAnchored = false  // Anchor mode to disable handle drag/resize
        var prefBubbleSize = 100        // 50-200 range (percentage, 100 = standard)
        var prefBubbleIconIndex = 0     // Index into icon array
        var prefBubbleAlpha = 255       // 0-255 opacity
        
        // =========================
        // HARDKEY BINDING PREFS - Configurable hardware key actions
        // Each key combo maps to an action ID string
        // =========================
        var hardkeyVolUpTap = "left_click"
        var hardkeyVolUpDouble = "none"
        var hardkeyVolUpHold = "left_drag"
        var hardkeyVolDownTap = "right_click"
        var hardkeyVolDownDouble = "display_toggle"
        var hardkeyVolDownHold = "alt_position"
        var hardkeyPowerDouble = "none"
        var doubleTapMs = 300           // Max time between taps for double-tap (150-500)
        var holdDurationMs = 400        // Time to trigger hold action (200-800)
        var displayOffMode = "alternate" // "standard" or "alternate"
        // =========================
        // END HARDKEY BINDING PREFS
        // =========================
    }
    // =========================
    // END PREFS CLASS
    // =========================
  val prefs = Prefs()

    private var uiScreenWidth = 1080
    private var uiScreenHeight = 2640
    private var targetScreenWidth = 1920
    private var targetScreenHeight = 1080
    private var cursorX = 300f
    private var cursorY = 300f
    private var virtualScrollX = 0f
    private var virtualScrollY = 0f
    private var scrollAccumulatorX = 0f
    private var scrollAccumulatorY = 0f
    private var rotationAngle = 0
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var initialWindowX = 0
    private var initialWindowY = 0
    private var initialWindowWidth = 0
    private var initialWindowHeight = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isTouchDragging = false
    private var isLeftKeyHeld = false
    private var isRightKeyHeld = false
    private var isRightDragPending = false
    private var isVScrolling = false
    private var isHScrolling = false
    private var dragDownTime: Long = 0L
    private var isClicking = false
    private var hasSentTouchDown = false
    private var hasSentMouseDown = false
    private var hasSentScrollDown = false
    private var activeFingerDeviceId = -1
    private var pendingDragDownTime: Long = 0L
    private var ignoreTouchSequence = false
    private var isDebugMode = false
    private var isKeyboardMode = false
    private var savedWindowX = 0
    private var savedWindowY = 0
    private var currentBorderColor = 0xFFFFFFFF.toInt()
    private var highlightAlpha = false
    private var highlightHandles = false
    private var highlightScrolls = false
    // =========================
    // TOUCH TIMING VARIABLES - For click detection and release debounce
    // =========================
    private var touchDownTime: Long = 0L          // When finger touched down
    private var touchDownX: Float = 0f            // Where finger touched down (X)
    private var touchDownY: Float = 0f            // Where finger touched down (Y)
    private var isReleaseDebouncing = false       // True during post-release cooldown
    private val releaseDebounceRunnable = Runnable { isReleaseDebouncing = false }
    
    // === FINE TUNING: Adjust these for click sensitivity ===
    private val TAP_TIMEOUT_MS = 300L             // Max time for tap (ms) - longer = click
    private val TAP_SLOP_PX = 15f                 // Max movement for tap (px) - more = drag
    private val RELEASE_DEBOUNCE_MS = 50L         // Cooldown after release (ms)
    // === END FINE TUNING ===
    // =========================
    // END TOUCH TIMING VARIABLES
    // =========================

    // Scroll zone touch detection thickness (synced with prefScrollTouchSize)
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
    private var currentOverlayDisplayId = 0
    private var lastLoadedProfileKey = ""

    private val longPressRunnable = Runnable { startTouchDrag() }
    private var isResizing = false
    private val resizeLongPressRunnable = Runnable { startResize() }
    private var isMoving = false
    private val moveLongPressRunnable = Runnable { startMove() }
    private val voiceRunnable = Runnable { toggleKeyboardMode() }
    private var keyboardHandleDownTime = 0L
    private val keyboardLongPressRunnable = Runnable { toggleKeyboardMode() }
    private val clearHighlightsRunnable = Runnable { highlightAlpha = false; highlightHandles = false; highlightScrolls = false; updateBorderColor(currentBorderColor); updateLayoutSizes() }
    
    // =========================
    // HARDKEY DETECTION STATE - Variables for tap/double-tap/hold detection
    // Used by onKeyEvent() to distinguish between different gesture types
    // =========================
    private var lastVolUpTime: Long = 0L
    private var lastVolDownTime: Long = 0L
    private var lastPowerTime: Long = 0L
    private var volUpTapCount = 0
    private var volDownTapCount = 0
    private var powerTapCount = 0
    private var volUpHoldTriggered = false
    private var volDownHoldTriggered = false
    private var volUpDragActive = false   // Track if we're currently dragging
    private var volDownDragActive = false // Track if right-drag is active
    
    // Hold detection runnables - trigger hold action after delay
    private val volUpHoldRunnable = Runnable {
        volUpHoldTriggered = true
        executeHardkeyAction(prefs.hardkeyVolUpHold)
    }
    private val volDownHoldRunnable = Runnable {
        volDownHoldTriggered = true
        executeHardkeyAction(prefs.hardkeyVolDownHold)
    }
    
    // Double-tap detection runnables - execute single tap if no second tap arrives
    private val volUpDoubleTapRunnable = Runnable {
        if (volUpTapCount == 1) {
            executeHardkeyAction(prefs.hardkeyVolUpTap)
        }
        volUpTapCount = 0
    }
    private val volDownDoubleTapRunnable = Runnable {
        if (volDownTapCount == 1) {
            executeHardkeyAction(prefs.hardkeyVolDownTap)
        }
        volDownTapCount = 0
    }
    private val powerDoubleTapRunnable = Runnable {
        // Power single-tap is handled by system, we only care about double-tap
        powerTapCount = 0
    }
    // =========================
    // END HARDKEY DETECTION STATE
    // =========================

    
    private val switchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                "RESET_CURSOR" -> resetCursorCenter()
                "TOGGLE_DEBUG" -> toggleDebugMode()
                "FORCE_KEYBOARD" -> toggleCustomKeyboard()
                "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
                "OPEN_MENU" -> {
                    menuManager?.show()
                }
                "SET_TRACKPAD_VISIBILITY" -> {
                    val visible = intent.getBooleanExtra("VISIBLE", true)
                    val menuDisplayId = intent.getIntExtra("MENU_DISPLAY_ID", -1)
                    if (visible) setTrackpadVisibility(true) 
                    else { if (menuDisplayId == -1 || menuDisplayId == currentDisplayId) setTrackpadVisibility(false) }
                }
                "SET_PREVIEW_MODE" -> {
                    val preview = intent.getBooleanExtra("PREVIEW_MODE", false)
                    setPreviewMode(preview)
                }
            }
        }
    }

    companion object {
        private const val TAG = "OverlayService"
        private const val BASE_SWIPE_DISTANCE = 200f
        private const val SCROLL_THRESHOLD = 5f
        private const val DRAG_MOVEMENT_THRESHOLD = 20
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
            Thread { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") }.start()
            initCustomKeyboard()
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
        }
        ContextCompat.registerReceiver(this, switchReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        bindShizuku()
        setupUI(Display.DEFAULT_DISPLAY)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try { createNotification() } catch(e: Exception){ e.printStackTrace() }
        
        try {
            when (intent?.action) {
                "RESET_POSITION" -> resetTrackpadPosition()
                "ROTATE" -> performRotation()
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
                "OPEN_MENU" -> {
                    menuManager?.show()
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

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
    // =========================
    // ON KEY EVENT - Hardware key handler with configurable bindings
    // Supports tap, double-tap, and hold gestures for Vol Up, Vol Down, and Power
    // Uses prefs.doubleTapMs for double-tap window and prefs.holdDurationMs for hold threshold
    // =========================
    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (isPreviewMode || !isTrackpadVisible) return super.onKeyEvent(event)
        
        val action = event.action
        val keyCode = event.keyCode
        
        // Helper to check if an action is a direct mouse operation
        fun isDirectMouseAction(actionId: String): Boolean {
            return actionId == "left_click" || actionId == "right_click"
        }

        // =========================
        // VOLUME UP
        // =========================
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // If configured as a simple click/drag button, execute immediately
            // We use the "Hold" preference slot as the primary binding for single-button usage
            if (isDirectMouseAction(prefs.hardkeyVolUpHold)) {
                executeHardkeyAction(prefs.hardkeyVolUpHold, action)
                return true
            }
            
            // Standard Timer Logic for non-mouse actions (menus, toggles)
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
                    if (!volUpHoldTriggered) {
                        // Handle tap/double-tap logic here...
                        // (Use existing logic for taps)
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

        // =========================
        // VOLUME DOWN
        // =========================
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
             if (isDirectMouseAction(prefs.hardkeyVolDownHold)) {
                executeHardkeyAction(prefs.hardkeyVolDownHold, action)
                return true
            }
            
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
                    if (!volDownHoldTriggered) {
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
    // =========================
    // END ON KEY EVENT
    // =========================

    private fun setupUI(displayId: Int) {
        try {
            if (windowManager != null) {
                if (bubbleView != null) windowManager?.removeView(bubbleView)
                if (trackpadLayout != null) windowManager?.removeView(trackpadLayout)
                if (cursorLayout != null) windowManager?.removeView(cursorLayout)
            }
            menuManager?.hide()
        } catch (e: Exception) {}

        val display = displayManager?.getDisplay(displayId)
        if (display == null) {
            showToast("Error: Display $displayId not found")
            return
        }
        
        try {
            val displayContext = createDisplayContext(display)
            val windowContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 displayContext.createWindowContext(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null)
            } else {
                 displayContext
            }
            
            windowManager = windowContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            
            currentDisplayId = displayId
            inputTargetDisplayId = displayId
            
            updateUiMetrics()
            setupBubble(windowContext)
            setupTrackpad(windowContext)
            setupCursor(windowContext)
            
            menuManager = TrackpadMenuManager(windowContext, windowManager!!, this)
            if (shellService != null) initCustomKeyboard()
            
            showToast("Trackpad active on Display $displayId")
            
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
        bubbleView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> { initialX = bubbleParams.x; initialY = bubbleParams.y; initialTouchX = event.rawX; initialTouchY = event.rawY; isDrag = false; handler.postDelayed({ if (!isDrag) { vibrate(); menuManager?.toggle() } }, 600); true }
                MotionEvent.ACTION_MOVE -> { 
                    if (abs(event.rawX - initialTouchX) > 10 || abs(event.rawY - initialTouchY) > 10) { 
                        isDrag = true
                        bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(bubbleView, bubbleParams) 
                    }
                    true 
                }
                MotionEvent.ACTION_UP -> { 
                    handler.removeCallbacksAndMessages(null)
                    if (!isDrag) toggleTrackpad() 
                    else {
                        prefs.prefBubbleX = bubbleParams.x
                        prefs.prefBubbleY = bubbleParams.y
                        savePrefs()
                    }
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
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                // Let handleTrackpadTouch handle clicks
                return false
            }
 
        })
        
        trackpadLayout?.setOnTouchListener { _, event -> val devId = event.deviceId; val tool = event.getToolType(0); if (tool != MotionEvent.TOOL_TYPE_FINGER) return@setOnTouchListener false; when (event.actionMasked) { MotionEvent.ACTION_DOWN -> activeFingerDeviceId = devId; MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> { if (activeFingerDeviceId > 0 && devId != activeFingerDeviceId) return@setOnTouchListener false } }; gestureDetector.onTouchEvent(event); handleTrackpadTouch(event); true }
        trackpadLayout?.visibility = View.GONE; windowManager?.addView(trackpadLayout, trackpadParams); updateBorderColor(currentBorderColor)
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

    fun toggleTrackpad() { isTrackpadVisible = !isTrackpadVisible; trackpadLayout?.visibility = if (isTrackpadVisible) View.VISIBLE else View.GONE; if (isTrackpadVisible) updateBorderColor(currentBorderColor) }
    
    // =========================
    // EXECUTE HARDKEY ACTION - Central dispatcher for all hardkey-triggered actions
    // Called by onKeyEvent() when a tap/double-tap/hold gesture is detected
    // Action IDs map to specific behaviors defined in HARDKEY_ACTIONS
    // =========================
    private fun executeHardkeyAction(actionId: String, keyEventAction: Int = KeyEvent.ACTION_UP) {
        when (actionId) {
            "none" -> { /* Do nothing */ }
            
            "left_click" -> {
                if (keyEventAction == KeyEvent.ACTION_DOWN) {
                    if (!volUpDragActive) { // Re-using state var to track physical press state
                        volUpDragActive = true
                        startKeyDrag(MotionEvent.BUTTON_PRIMARY)
                    }
                } else {
                    volUpDragActive = false
                    stopKeyDrag(MotionEvent.BUTTON_PRIMARY)
                }
            }
            
            "right_click" -> {
                 if (keyEventAction == KeyEvent.ACTION_DOWN) {
                    if (!volDownDragActive) {
                        volDownDragActive = true
                        startKeyDrag(MotionEvent.BUTTON_SECONDARY)
                    }
                } else {
                    volDownDragActive = false
                    stopKeyDrag(MotionEvent.BUTTON_SECONDARY)
                }
            }
            
            // ... (rest of the actions remain the same, just wrapped in 'if (keyEventAction == ACTION_UP)')
            "scroll_up" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed
                    performSwipe(0f, -dist)
                }
            }
            "scroll_down" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed
                    performSwipe(0f, dist)
                }
            }
            
            "display_toggle" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    // Use preferred display off mode
                    when (prefs.displayOffMode) {
                        "standard" -> {
                            isScreenOff = !isScreenOff
                            Thread {
                                try {
                                    if (isScreenOff) shellService?.setScreenOff(0, true)
                                    else shellService?.setScreenOff(0, false)
                                } catch (e: Exception) {}
                            }.start()
                            showToast("Display ${if(isScreenOff) "Off" else "On"} (Std)")
                        }
                        else -> {
                            isScreenOff = !isScreenOff
                            Thread {
                                try {
                                    if (isScreenOff) shellService?.setBrightness(-1)
                                    else shellService?.setBrightness(128)
                                } catch (e: Exception) {}
                            }.start()
                            showToast("Display ${if(isScreenOff) "Off" else "On"} (Alt)")
                        }
                    }
                }
            }
            
            "display_toggle_alt" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    isScreenOff = !isScreenOff
                    Thread {
                        try {
                            if (isScreenOff) shellService?.setBrightness(-1)
                            else shellService?.setBrightness(128)
                        } catch (e: Exception) {}
                    }.start()
                    showToast("Display ${if(isScreenOff) "Off" else "On"} (Alt)")
                }
            }
            
            "display_toggle_std" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    isScreenOff = !isScreenOff
                    Thread {
                        try {
                            if (isScreenOff) shellService?.setScreenOff(0, true)
                            else shellService?.setScreenOff(0, false)
                        } catch (e: Exception) {}
                    }.start()
                            showToast("Display ${if(isScreenOff) "Off" else "On"} (Std)")
                }
            }
            
            "alt_position" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    toggleKeyboardMode()
                }
            }
            
            "toggle_keyboard" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    toggleCustomKeyboard()
                }
            }
            
            "toggle_trackpad" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    toggleTrackpad()
                }
            }
            
            "open_menu" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    menuManager?.toggle()
                }
            }
            
            "reset_cursor" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    resetCursorCenter()
                }
            }
            
            "display_wake" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    // Wake display if off
                    if (isScreenOff) {
                        isScreenOff = false
                        Thread {
                            try {
                                shellService?.setBrightness(128)
                                shellService?.setScreenOff(0, false)
                            } catch (e: Exception) {}
                        }.start()
                        showToast("Display Woken")
                    }
                }
            }
        }
    }
    // =========================
    // END EXECUTE HARDKEY ACTION
    // =========================

    fun toggleKeyboardMode() { vibrate(); isRightDragPending = false; if (!isKeyboardMode) { isKeyboardMode = true; savedWindowX = trackpadParams.x; savedWindowY = trackpadParams.y; trackpadParams.x = uiScreenWidth - trackpadParams.width; trackpadParams.y = 0; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(0xFFFF0000.toInt()) } else { isKeyboardMode = false; trackpadParams.x = savedWindowX; trackpadParams.y = savedWindowY; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(currentBorderColor) } }
    
    private fun toggleDebugMode() { isDebugMode = !isDebugMode; if (isDebugMode) { showToast("Debug ON"); updateBorderColor(0xFFFFFF00.toInt()); debugTextView?.visibility = View.VISIBLE } else { showToast("Debug OFF"); if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt()); debugTextView?.visibility = View.GONE } }

    fun updateBubbleStatus() { val dot = bubbleView?.findViewById<ImageView>(R.id.status_dot); if (shellService != null) dot?.visibility = View.GONE else dot?.visibility = View.VISIBLE }

    // =========================
    // BUBBLE CUSTOMIZATION - Icon, Size, and Opacity
    // =========================
    
    // Available bubble icons (add more as needed)
    private val bubbleIcons = arrayOf(
        R.mipmap.ic_trackpad_adaptive,     // Default trackpad icon
        R.drawable.ic_cursor,               // Cursor icon
        R.drawable.ic_tab_main,             // Main tab icon
        R.drawable.ic_tab_keyboard,         // Keyboard icon
        android.R.drawable.ic_menu_compass, // System compass
        android.R.drawable.ic_menu_myplaces // System location
    )
    
    fun getBubbleIconCount(): Int = bubbleIcons.size
    
    fun updateBubbleSize(sizePercent: Int) {
        prefs.prefBubbleSize = sizePercent.coerceIn(50, 200)
        applyBubbleAppearance()
        savePrefs()
    }
    
    fun updateBubbleIcon(index: Int) {
        prefs.prefBubbleIconIndex = index.coerceIn(0, bubbleIcons.size - 1)
        applyBubbleAppearance()
        savePrefs()
    }
    
    fun cycleBubbleIcon() {
        val nextIndex = (prefs.prefBubbleIconIndex + 1) % bubbleIcons.size
        updateBubbleIcon(nextIndex)
    }
    
    fun updateBubbleAlpha(alpha: Int) {
        prefs.prefBubbleAlpha = alpha.coerceIn(50, 255)
        applyBubbleAppearance()
        savePrefs()
    }
    
    private fun applyBubbleAppearance() {
        if (bubbleView == null) return
        
        val scale = prefs.prefBubbleSize / 100f
        
        // Base sizes (standard = Launcher size: 60dp container, 40dp icon)
        val baseContainerDp = 60
        val baseIconDp = 40
        
        val density = resources.displayMetrics.density
        val containerSize = (baseContainerDp * scale * density).toInt()
        val iconSize = (baseIconDp * scale * density).toInt()
        
        // Update container size via LayoutParams
        bubbleParams.width = containerSize
        bubbleParams.height = containerSize
        try {
            windowManager?.updateViewLayout(bubbleView, bubbleParams)
        } catch (e: Exception) {}
        
        // Update icon
        val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon)
        iconView?.let {
            val iconParams = it.layoutParams as? FrameLayout.LayoutParams
            iconParams?.width = iconSize
            iconParams?.height = iconSize
            it.layoutParams = iconParams
            
            // Set icon drawable
            val iconRes = bubbleIcons.getOrElse(prefs.prefBubbleIconIndex) { bubbleIcons[0] }
            it.setImageResource(iconRes)
            
            // Set alpha (0.0-1.0)
            it.alpha = prefs.prefBubbleAlpha / 255f
        }
        
        // Also apply alpha to background
        bubbleView?.alpha = prefs.prefBubbleAlpha / 255f
    }
    // =========================
    // END BUBBLE CUSTOMIZATION
    // =========================


    fun forceMoveToCurrentDisplay() { setupUI(currentDisplayId) }
    fun forceMoveToDisplay(displayId: Int) { 
        if (displayId == currentDisplayId) return // Optimization: Don't teardown if already there
        setupUI(displayId) 
    }
    
    // --- UPDATED HIDE APP LOGIC ---
    fun hideApp() { 
        menuManager?.hide()
        if (isTrackpadVisible) toggleTrackpad()
    }
    
    // --- NEW FORCE EXIT LOGIC ---
    fun forceExit() {
        try {
            stopSelf()
            Process.killProcess(Process.myPid())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // --- FIXED MANUAL ADJUST ---
    fun manualAdjust(isKeyboard: Boolean, isResize: Boolean, dx: Int, dy: Int) { 
        if (isKeyboard) { 
            if (isResize) keyboardOverlay?.resizeWindow(dx, dy) 
            else keyboardOverlay?.moveWindow(dx, dy) 
        } else { 
            if (trackpadLayout == null) return
            
            if (isResize) {
                // Resize: Only change width/height
                trackpadParams.width = max(200, trackpadParams.width + dx)
                trackpadParams.height = max(200, trackpadParams.height + dy)
            } else {
                // Move: Only change x/y
                trackpadParams.x += dx
                trackpadParams.y += dy
            }
            
            try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch (e: Exception) {}
            saveLayout() 
        } 
    }
    
    private fun parseBoolean(value: Any): Boolean { return when(value) { is Boolean -> value; is Int -> value == 1; is String -> value == "1" || value.equals("true", ignoreCase = true); else -> false } }
    
    // =========================
    // UPDATE PREF - Handles preference changes from menu
    // Includes anchor mode toggle and updates keyboard anchor state
    // =========================
    fun updatePref(key: String, value: Any) { 
        when(key) { 
            "cursor_speed" -> prefs.cursorSpeed = (value.toString().toFloatOrNull() ?: 2.5f)
            "scroll_speed" -> prefs.scrollSpeed = (value.toString().toFloatOrNull() ?: 3.0f)
            "tap_scroll" -> prefs.prefTapScroll = parseBoolean(value)
            "vibrate" -> prefs.prefVibrate = parseBoolean(value)
            "reverse_scroll" -> prefs.prefReverseScroll = parseBoolean(value)
            "alpha" -> { prefs.prefAlpha = (value.toString().toIntOrNull() ?: 200); updateBorderColor(currentBorderColor) }
            "keyboard_alpha" -> { prefs.prefKeyboardAlpha = (value.toString().toIntOrNull() ?: 200); keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha) }
            "handle_size" -> { 
                val raw = (value.toString().toIntOrNull() ?: 60)
                val scaled = raw * 2 // Scale 2x
                prefs.prefHandleSize = scaled
                prefs.prefHandleTouchSize = scaled + 20 // Auto-sync touch size
                updateHandleSize()
                updateLayoutSizes()
            }
            "scroll_size" -> {
                // === FINE TUNING: Adjust multipliers here ===
                val touchSize = (value.toString().toIntOrNull() ?: 80)
                val visualSize = (touchSize * 8 / 80).coerceIn(4, 20) // Proportional scaling
                // === END FINE TUNING ===
                
                prefs.prefScrollTouchSize = touchSize
                prefs.prefScrollVisualSize = visualSize
                scrollZoneThickness = touchSize  // CRITICAL: Sync the touch detection zone!
                updateScrollSize()
            }
           "cursor_size" -> { prefs.prefCursorSize = (value.toString().toIntOrNull() ?: 50); updateCursorSize() }
            "keyboard_key_scale" -> { prefs.prefKeyScale = (value.toString().toIntOrNull() ?: 100); keyboardOverlay?.updateScale(prefs.prefKeyScale / 100f) }
            "use_alt_screen_off" -> prefs.prefUseAltScreenOff = parseBoolean(value) 
            "automation_enabled" -> prefs.prefAutomationEnabled = parseBoolean(value)
            "anchored" -> { 
                prefs.prefAnchored = parseBoolean(value)
                keyboardOverlay?.setAnchored(prefs.prefAnchored)  // Sync to keyboard overlay
            }
             "bubble_size" -> { prefs.prefBubbleSize = (value.toString().toIntOrNull() ?: 100); applyBubbleAppearance() }
            "bubble_icon" -> { cycleBubbleIcon() }
            "bubble_alpha" -> { prefs.prefBubbleAlpha = (value.toString().toIntOrNull() ?: 255); applyBubbleAppearance() }
            
            // =========================
            // HARDKEY TIMING PREFS - Double-tap speed and hold duration
            // =========================
            "double_tap_ms" -> { prefs.doubleTapMs = (value.toString().toIntOrNull() ?: 300).coerceIn(150, 500) }
            "hold_duration_ms" -> { prefs.holdDurationMs = (value.toString().toIntOrNull() ?: 400).coerceIn(200, 800) }
            "display_off_mode" -> { prefs.displayOffMode = value.toString() }
            "hardkey_vol_up_tap" -> { prefs.hardkeyVolUpTap = value.toString() }
            "hardkey_vol_up_double" -> { prefs.hardkeyVolUpDouble = value.toString() }
            "hardkey_vol_up_hold" -> { prefs.hardkeyVolUpHold = value.toString() }
            "hardkey_vol_down_tap" -> { prefs.hardkeyVolDownTap = value.toString() }
            "hardkey_vol_down_double" -> { prefs.hardkeyVolDownDouble = value.toString() }
            "hardkey_vol_down_hold" -> { prefs.hardkeyVolDownHold = value.toString() }
            "hardkey_power_double" -> { prefs.hardkeyPowerDouble = value.toString() }
            // =========================
            // END HARDKEY TIMING PREFS
            // =========================

        }
        savePrefs() 
    }
    // =========================
    // END UPDATE PREF
    // =========================
    
    // =========================
    // PRESETS LOGIC - Apply split screen layouts
    // Type 0 = Freeform (loads saved profile)
    // Type 1 = KB Top / TP Bottom
    // Type 2 = TP Top / KB Bottom
    // =========================
    fun applyLayoutPreset(type: Int) {
        if (type == 0) { // Freeform
            loadLayout()
            showToast("Freeform Profile Loaded")
            return
        }

        val h = uiScreenHeight
        val w = uiScreenWidth
        
        when(type) {
            1 -> {
                // Preset 1: KB Top 50%, TP Bottom 50%
                trackpadParams.x = 0
                trackpadParams.y = h / 2
                trackpadParams.width = w
                trackpadParams.height = h / 2
                
                // KB: Width 90%, centered horizontal, bottom aligned in top half
                val kbW = (w * 0.9f).toInt()
                val kbH = (h * 0.4f).toInt()
                val kbX = (w - kbW) / 2
                val kbY = (h / 2) - kbH
                
                keyboardOverlay?.setWindowBounds(kbX, kbY, kbW, kbH)
            }
            2 -> {
                // Preset 2: TP Top 50%, KB Bottom 50%
                trackpadParams.x = 0
                trackpadParams.y = 0
                trackpadParams.width = w
                trackpadParams.height = h / 2
                
                // KB: Width 90%, centered horizontal, at screen bottom
                val kbW = (w * 0.9f).toInt()
                val kbH = (h * 0.4f).toInt()
                val kbX = (w - kbW) / 2
                val kbY = h - kbH
                
                keyboardOverlay?.setWindowBounds(kbX, kbY, kbW, kbH)
            }
        }
                // === OPTIONAL: Use wider scroll bars for split presets ===
        // When trackpad is smaller (split view), make scroll bars easier to hit
        if (type == 1 || type == 2) {
            prefs.prefScrollTouchSize = prefs.prefScrollTouchSize.coerceAtLeast(200) // At least 100px
            prefs.prefScrollVisualSize = prefs.prefScrollVisualSize.coerceAtLeast(10) // At least 10px visible
        }
        // === END OPTIONAL ===
        // Update trackpad window
        try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch(e: Exception){}
        
        // === FIX: Refresh scroll bars after preset resize ===
        updateScrollSize()
        updateScrollPosition()
        updateHandleSize()
        updateLayoutSizes()
        // === END FIX ===
        
        // Don't call saveLayout for presets - they're temporary
    }
    // =========================
    // END PRESETS LOGIC
    // =========================
    
    fun resetBubblePosition() {
        bubbleParams.x = 50
        bubbleParams.y = uiScreenHeight / 2
        try {
            windowManager?.updateViewLayout(bubbleView, bubbleParams)
            prefs.prefBubbleX = bubbleParams.x
            prefs.prefBubbleY = bubbleParams.y
            savePrefs()
            showToast("Bubble Reset")
        } catch(e: Exception){}
    }

    // =========================
    // LOAD PREFS - Loads all preferences from SharedPreferences
    // Includes hardkey binding mappings and timing settings
    // =========================
    private fun loadPrefs() { 
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        prefs.cursorSpeed = p.getFloat("cursor_speed", 2.5f)
        prefs.scrollSpeed = p.getFloat("scroll_speed", 3.0f)
        prefs.prefTapScroll = p.getBoolean("tap_scroll", true)
        prefs.prefVibrate = p.getBoolean("vibrate", true)
        prefs.prefReverseScroll = p.getBoolean("reverse_scroll", true)
        prefs.prefAlpha = p.getInt("alpha", 200)
        prefs.prefKeyboardAlpha = p.getInt("keyboard_alpha", 200)
        prefs.prefHandleSize = p.getInt("handle_size", 60)
        prefs.prefHandleTouchSize = p.getInt("handle_touch_size", 80)
        prefs.prefVPosLeft = p.getBoolean("v_pos_left", false)
        prefs.prefHPosTop = p.getBoolean("h_pos_top", false)
        prefs.prefLocked = p.getBoolean("lock_position", false)
        prefs.prefCursorSize = p.getInt("cursor_size", 50)
        prefs.prefKeyScale = p.getInt("keyboard_key_scale", 100)
        prefs.prefUseAltScreenOff = p.getBoolean("use_alt_screen_off", true)
        prefs.prefAutomationEnabled = p.getBoolean("automation_enabled", true)
        prefs.prefBubbleX = p.getInt("bubble_x", 50)
        prefs.prefBubbleY = p.getInt("bubble_y", 300)
        prefs.prefAnchored = p.getBoolean("anchored", false)
        prefs.prefBubbleSize = p.getInt("bubble_size", 100)
        prefs.prefBubbleIconIndex = p.getInt("bubble_icon_index", 0)
        prefs.prefBubbleAlpha = p.getInt("bubble_alpha", 255)
        prefs.prefScrollTouchSize = p.getInt("scroll_touch_size", 80)
        prefs.prefScrollVisualSize = p.getInt("scroll_visual_size", 8)
        
        // =========================
        // LOAD HARDKEY BINDINGS - Action mappings for hardware keys
        // =========================
        prefs.hardkeyVolUpTap = p.getString("hardkey_vol_up_tap", "left_click") ?: "left_click"
        prefs.hardkeyVolUpDouble = p.getString("hardkey_vol_up_double", "none") ?: "none"
        prefs.hardkeyVolUpHold = p.getString("hardkey_vol_up_hold", "left_drag") ?: "left_drag"
        prefs.hardkeyVolDownTap = p.getString("hardkey_vol_down_tap", "right_click") ?: "right_click"
        prefs.hardkeyVolDownDouble = p.getString("hardkey_vol_down_double", "display_toggle") ?: "display_toggle"
        prefs.hardkeyVolDownHold = p.getString("hardkey_vol_down_hold", "alt_position") ?: "alt_position"
        prefs.hardkeyPowerDouble = p.getString("hardkey_power_double", "none") ?: "none"
        prefs.doubleTapMs = p.getInt("double_tap_ms", 300)
        prefs.holdDurationMs = p.getInt("hold_duration_ms", 400)
        prefs.displayOffMode = p.getString("display_off_mode", "alternate") ?: "alternate"
        // =========================
        // END LOAD HARDKEY BINDINGS
        // =========================
        
        // === CRITICAL: Sync scrollZoneThickness with loaded value ===
        scrollZoneThickness = prefs.prefScrollTouchSize
        // === END SYNC ===
        
        // Reset handles if corrupted or first run
        if (prefs.prefHandleSize > 300) prefs.prefHandleSize = 60
        if (prefs.prefHandleTouchSize > 300) prefs.prefHandleTouchSize = 80
    }
    // =========================
    // END LOAD PREFS
    // =========================
    private fun savePrefs() { 
        val e = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        e.putFloat("cursor_speed", prefs.cursorSpeed)
        e.putInt("alpha", prefs.prefAlpha)
        e.putInt("keyboard_alpha", prefs.prefKeyboardAlpha)
        e.putBoolean("use_alt_screen_off", prefs.prefUseAltScreenOff)
        e.putBoolean("automation_enabled", prefs.prefAutomationEnabled)
        e.putInt("bubble_x", prefs.prefBubbleX)
        e.putInt("bubble_y", prefs.prefBubbleY)
        e.putBoolean("anchored", prefs.prefAnchored)
        e.putInt("bubble_size", prefs.prefBubbleSize)
        e.putInt("bubble_icon_index", prefs.prefBubbleIconIndex)
        e.putInt("bubble_alpha", prefs.prefBubbleAlpha)
        e.putInt("scroll_touch_size", prefs.prefScrollTouchSize)
        e.putInt("scroll_visual_size", prefs.prefScrollVisualSize)
        
        // =========================
        // SAVE HARDKEY BINDINGS - Action mappings for hardware keys
        // =========================
        e.putString("hardkey_vol_up_tap", prefs.hardkeyVolUpTap)
        e.putString("hardkey_vol_up_double", prefs.hardkeyVolUpDouble)
        e.putString("hardkey_vol_up_hold", prefs.hardkeyVolUpHold)
        e.putString("hardkey_vol_down_tap", prefs.hardkeyVolDownTap)
        e.putString("hardkey_vol_down_double", prefs.hardkeyVolDownDouble)
        e.putString("hardkey_vol_down_hold", prefs.hardkeyVolDownHold)
        e.putString("hardkey_power_double", prefs.hardkeyPowerDouble)
        e.putInt("double_tap_ms", prefs.doubleTapMs)
        e.putInt("hold_duration_ms", prefs.holdDurationMs)
        e.putString("display_off_mode", prefs.displayOffMode)
        // =========================
        // END SAVE HARDKEY BINDINGS
        // =========================
        
        e.apply() 
    }
    // =========================
    // END SAVE PREFS
    // =========================

    private fun bindShizuku() { try { val c = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(c, userServiceConnection, BuildConfig.DEBUG, BuildConfig.VERSION_CODE) } catch (e: Exception) { e.printStackTrace() } }
    
    // --- SAFE CREATE NOTIFICATION ---
    private fun createNotification() { 
        try {
            val channel = NotificationChannel("overlay_service", "Trackpad", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            val notif = Notification.Builder(this, "overlay_service")
                .setContentTitle("Trackpad Active")
                .setSmallIcon(R.drawable.ic_cursor)
                .build()
            
            // Try specific type first, fallback to generic if it crashes due to manifest mismatch
            try {
                if (Build.VERSION.SDK_INT >= 34) {
                    startForeground(1, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) 
                } else {
                    startForeground(1, notif)
                }
            } catch(e: Exception) {
                startForeground(1, notif)
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initCustomKeyboard() { if (windowManager == null || shellService == null) return; keyboardOverlay = KeyboardOverlay(this, windowManager!!, shellService, inputTargetDisplayId, { toggleScreen() }, { toggleScreenMode() }); keyboardOverlay?.setScreenDimensions(uiScreenWidth, uiScreenHeight, currentDisplayId) }

    // --- KEYBOARD AUTOMATION FIX ---
    fun toggleCustomKeyboard() {
        if (keyboardOverlay == null) initCustomKeyboard()
        
        val isNowVisible = if (keyboardOverlay?.isShowing() == true) {
            keyboardOverlay?.hide()
            false
        } else {
            keyboardOverlay?.show()
            true
        }
        
        isCustomKeyboardVisible = isNowVisible
        
        if (prefs.prefAutomationEnabled) {
            if (isNowVisible) turnScreenOn() else turnScreenOff()
        }
    }

    // --- SCREEN CONTROL HELPERS ---
    private fun turnScreenOn() {
        isScreenOff = false
        Thread {
            try {
                shellService?.setBrightness(128)
                shellService?.setScreenOff(0, false)
            } catch(e: Exception) {}
        }.start()
        showToast("Screen On")
    }

    private fun turnScreenOff() {
        isScreenOff = true
        Thread {
            try {
                if (prefs.prefUseAltScreenOff) shellService?.setBrightness(-1) 
                else shellService?.setScreenOff(0, true)
            } catch(e: Exception) {}
        }.start()
        showToast("Screen Off (${if(prefs.prefUseAltScreenOff) "Alt" else "Std"})")
    }
    
    private fun toggleScreenMode() {
        prefs.prefUseAltScreenOff = !prefs.prefUseAltScreenOff
        savePrefs()
        showToast("Mode: ${if(prefs.prefUseAltScreenOff) "Alternate" else "Standard"}")
    }

    private fun toggleScreen() {
        if (isScreenOff) turnScreenOn() else turnScreenOff()
    }
    
    private fun updateUiMetrics() { val display = displayManager?.getDisplay(currentDisplayId) ?: return; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); uiScreenWidth = metrics.widthPixels; uiScreenHeight = metrics.heightPixels }
    private fun createTrackpadDisplayContext(display: Display): Context { return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) createDisplayContext(display).createWindowContext(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null) else createDisplayContext(display) }
    private fun addHandle(context: Context, gravity: Int, color: Int, onTouch: (View, MotionEvent) -> Boolean) { val container = FrameLayout(context); val p = FrameLayout.LayoutParams(prefs.prefHandleTouchSize, prefs.prefHandleTouchSize); p.gravity = gravity; val visual = View(context); val bg = GradientDrawable(); bg.setColor(color); bg.cornerRadius = 15f; visual.background = bg; val vp = FrameLayout.LayoutParams(prefs.prefHandleSize, prefs.prefHandleSize); vp.gravity = Gravity.CENTER; container.addView(visual, vp); handleContainers.add(container); handleVisuals.add(visual); trackpadLayout?.addView(container, p); container.setOnTouchListener { v, e -> onTouch(v, e) } }
    // =========================
    // ADD SCROLL BARS - Creates visual scroll bar overlays with touch handlers
    // Touch handlers consume events to prevent trackpad from receiving them
    // =========================
    private fun addScrollBars(context: Context) {
        val margin = prefs.prefHandleTouchSize + 10
        
        // --- Vertical Scroll Bar ---
        vScrollContainer = FrameLayout(context)
        val vp = FrameLayout.LayoutParams(prefs.prefScrollTouchSize, FrameLayout.LayoutParams.MATCH_PARENT)
        vp.gravity = if (prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT
        vp.setMargins(0, margin, 0, margin)
        trackpadLayout?.addView(vScrollContainer, vp)
        
        vScrollVisual = View(context)
        vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())
        vScrollContainer?.addView(vScrollVisual, FrameLayout.LayoutParams(prefs.prefScrollVisualSize, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER))
        
        // Touch listener for vertical scroll - MUST return true to consume event
        vScrollContainer?.setOnTouchListener { _, event ->
            handleVScrollTouch(event)
            true  // Always consume to prevent trackpad from getting the touch
        }
        
        // --- Horizontal Scroll Bar ---
        hScrollContainer = FrameLayout(context)
        val hp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefs.prefScrollTouchSize)
        hp.gravity = if (prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM
        hp.setMargins(margin, 0, margin, 0)
        trackpadLayout?.addView(hScrollContainer, hp)
        
        hScrollVisual = View(context)
        hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())
        hScrollContainer?.addView(hScrollVisual, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefs.prefScrollVisualSize, Gravity.CENTER))
        
        // Touch listener for horizontal scroll - MUST return true to consume event
        hScrollContainer?.setOnTouchListener { _, event ->
            handleHScrollTouch(event)
            true  // Always consume to prevent trackpad from getting the touch
        }
    }
    // =========================
    // END ADD SCROLL BARS
    // =========================

    // =========================
    // HANDLE V SCROLL TOUCH - Dedicated vertical scroll bar touch handler
    // Consumes all touches in the scroll bar area
    // =========================
    private fun handleVScrollTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isVScrolling = true
                lastTouchY = event.y
                scrollAccumulatorY = 0f
                vScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt())  // Highlight active
                
                if (prefs.prefTapScroll) {
                    // Tap mode: instant scroll based on tap position
                    val viewHeight = vScrollContainer?.height ?: return
                    val isTopHalf = event.y < (viewHeight / 2)
                    val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed
                    val dy = if (isTopHalf) {
                        if (prefs.prefReverseScroll) -dist else dist
                    } else {
                        if (prefs.prefReverseScroll) dist else -dist
                    }
                    performSwipe(0f, dy)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isVScrolling && !prefs.prefTapScroll) {
                    // Drag mode: continuous scroll
                    val dy = event.y - lastTouchY
                    scrollAccumulatorY += dy * prefs.scrollSpeed
                    
                    val scrollThreshold = 30f
                    if (kotlin.math.abs(scrollAccumulatorY) > scrollThreshold) {
                        val scrollDist = scrollAccumulatorY * 2
                        val actualDy = if (prefs.prefReverseScroll) -scrollDist else scrollDist
                        performSwipe(0f, actualDy)
                        scrollAccumulatorY = 0f
                    }
                    lastTouchY = event.y
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isVScrolling = false
                scrollAccumulatorY = 0f
                vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())  // Reset color
            }
        }
    }
    // =========================
    // END HANDLE V SCROLL TOUCH
    // =========================
    
    // =========================
    // HANDLE H SCROLL TOUCH - Dedicated horizontal scroll bar touch handler
    // Consumes all touches in the scroll bar area
    // =========================
    private fun handleHScrollTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isHScrolling = true
                lastTouchX = event.x
                scrollAccumulatorX = 0f
                hScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt())  // Highlight active
                
                if (prefs.prefTapScroll) {
                    // Tap mode: instant scroll based on tap position
                    val viewWidth = hScrollContainer?.width ?: return
                    val isLeftHalf = event.x < (viewWidth / 2)
                    val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed
                    val dx = if (isLeftHalf) {
                        if (prefs.prefReverseScroll) -dist else dist
                    } else {
                        if (prefs.prefReverseScroll) dist else -dist
                    }
                    performSwipe(dx, 0f)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isHScrolling && !prefs.prefTapScroll) {
                    // Drag mode: continuous scroll
                    val dx = event.x - lastTouchX
                    scrollAccumulatorX += dx * prefs.scrollSpeed
                    
                    val scrollThreshold = 30f
                    if (kotlin.math.abs(scrollAccumulatorX) > scrollThreshold) {
                        val scrollDist = scrollAccumulatorX * 2
                        val actualDx = if (prefs.prefReverseScroll) -scrollDist else scrollDist
                        performSwipe(actualDx, 0f)
                        scrollAccumulatorX = 0f
                    }
                    lastTouchX = event.x
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isHScrolling = false
                scrollAccumulatorX = 0f
                hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())  // Reset color
            }
        }
    }
    // =========================
    // END HANDLE H SCROLL TOUCH
    // =========================
    private fun updateScrollPosition() { val margin = prefs.prefHandleTouchSize + 10; vScrollContainer?.let { container -> val vp = container.layoutParams as FrameLayout.LayoutParams; vp.gravity = if (prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; vp.setMargins(0, margin, 0, margin); container.layoutParams = vp }; hScrollContainer?.let { container -> val hp = container.layoutParams as FrameLayout.LayoutParams; hp.gravity = if (prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM; hp.setMargins(margin, 0, margin, 0); container.layoutParams = hp } }
    private fun updateHandleSize() { for (v in handleVisuals) { val p = v.layoutParams; p.width = prefs.prefHandleSize; p.height = prefs.prefHandleSize; v.layoutParams = p } }
     // =========================
    // UPDATE SCROLL SIZE - Updates scrollbar touch and visual widths
    // Called when user adjusts the Scroll Bar Width slider
    // MIN/MAX values can be adjusted here for fine-tuning
    // =========================
    private fun updateScrollSize() {
        val SCROLL_TOUCH_MIN = 40
        val SCROLL_TOUCH_MAX = 180
        val SCROLL_VISUAL_MIN = 4
        val SCROLL_VISUAL_MAX = 20
        
        prefs.prefScrollTouchSize = prefs.prefScrollTouchSize.coerceIn(SCROLL_TOUCH_MIN, SCROLL_TOUCH_MAX)
        prefs.prefScrollVisualSize = prefs.prefScrollVisualSize.coerceIn(SCROLL_VISUAL_MIN, SCROLL_VISUAL_MAX)
        
        // === CRITICAL: Sync the touch detection variable ===
        scrollZoneThickness = prefs.prefScrollTouchSize
        // === END SYNC ===
        
        // Update vertical scroll bar
        vScrollContainer?.let { container ->
            val params = container.layoutParams as? FrameLayout.LayoutParams
            params?.width = prefs.prefScrollTouchSize
            container.layoutParams = params
        }

        vScrollVisual?.let { visual ->
            val params = visual.layoutParams as? FrameLayout.LayoutParams
            params?.width = prefs.prefScrollVisualSize
            visual.layoutParams = params
        }
        
        // Update horizontal scroll bar
        hScrollContainer?.let { container ->
            val params = container.layoutParams as? FrameLayout.LayoutParams
            params?.height = prefs.prefScrollTouchSize
            container.layoutParams = params
        }
        hScrollVisual?.let { visual ->
            val params = visual.layoutParams as? FrameLayout.LayoutParams
            params?.height = prefs.prefScrollVisualSize
            visual.layoutParams = params
        }
    }
    // =========================
    // END UPDATE SCROLL SIZE
    // =========================
   private fun updateLayoutSizes() { for (c in handleContainers) { val p = c.layoutParams; p.width = prefs.prefHandleTouchSize; p.height = prefs.prefHandleTouchSize; c.layoutParams = p } }
    private fun updateCursorSize() { val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; cursorView?.layoutParams?.let { it.width = size; it.height = size; cursorView?.layoutParams = it } }
    private fun updateBorderColor(strokeColor: Int) { currentBorderColor = strokeColor; val bg = trackpadLayout?.background as? GradientDrawable ?: return; bg.setColor(Color.TRANSPARENT); val colorWithAlpha = (strokeColor and 0x00FFFFFF) or (prefs.prefAlpha shl 24); bg.setStroke(4, if (highlightAlpha) 0xFF00FF00.toInt() else colorWithAlpha); trackpadLayout?.invalidate() }
    
    private fun performSwipe(dx: Float, dy: Float) {
        Thread {
            val dId = if (inputTargetDisplayId != -1) inputTargetDisplayId else (cursorLayout?.display?.displayId ?: Display.DEFAULT_DISPLAY)
            val now = SystemClock.uptimeMillis()
            val startX = cursorX
            val startY = cursorY
            val endX = startX + dx
            val endY = startY + dy
            try { shellService?.injectMouse(MotionEvent.ACTION_DOWN, startX, startY, dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now) } catch(e: Exception) {}
            val steps = 5
            for (i in 1..steps) {
                val t = i.toFloat() / steps
                val cx = startX + (dx * t)
                val cy = startY + (dy * t)
                try { shellService?.injectMouse(MotionEvent.ACTION_MOVE, cx, cy, dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now + (i * 10)); Thread.sleep(10) } catch(e: Exception) {}
            }
            try { shellService?.injectMouse(MotionEvent.ACTION_UP, endX, endY, dId, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, now + 100) } catch(e: Exception) {}
        }.start()
    }

    
    // =========================
    // HANDLE TRACKPAD TOUCH - Main touch event handler
    // Click logic: Only click on short tap with minimal movement
    // Drag logic: Long press OR significant movement starts drag
    // Release debounce: Brief cooldown prevents phantom cursor movement
    // =========================
    private fun handleTrackpadTouch(event: MotionEvent) {
        val viewWidth = trackpadLayout?.width ?: 0
        val viewHeight = trackpadLayout?.height ?: 0
        if (viewWidth == 0 || viewHeight == 0) return
        
        // Skip input during release debounce period
        if (isReleaseDebouncing && event.actionMasked != MotionEvent.ACTION_DOWN) {
            return
        }
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // Cancel any active debounce
                handler.removeCallbacks(releaseDebounceRunnable)
                isReleaseDebouncing = false
                
                // Record touch start position and time
                touchDownTime = SystemClock.uptimeMillis()
                touchDownX = event.x
                touchDownY = event.y
                lastTouchX = event.x
                lastTouchY = event.y
                isTouchDragging = false
                
                // Check scroll zones first
                val actualZoneV = kotlin.math.min(scrollZoneThickness, (viewWidth * 0.15f).toInt())
                val actualZoneH = kotlin.math.min(scrollZoneThickness, (viewHeight * 0.15f).toInt())
                val inVZone = if (prefs.prefVPosLeft) event.x < actualZoneV else event.x > (viewWidth - actualZoneV)
                val inHZone = if (prefs.prefHPosTop) event.y < actualZoneH else event.y > (viewHeight - actualZoneH)
                
                if (inVZone || inHZone) {
                    // Touch is in scroll zone - let scroll handlers deal with it
                    ignoreTouchSequence = true
                    return
                }
                
                // Schedule long press for DRAG (not click)
                handler.postDelayed(longPressRunnable, 400)
            }
            
            MotionEvent.ACTION_MOVE -> {
                if (ignoreTouchSequence) return
                
                // Calculate movement from touch DOWN position (not last position)
                val totalDx = event.x - touchDownX
                val totalDy = event.y - touchDownY
                val totalDistance = kotlin.math.sqrt(totalDx * totalDx + totalDy * totalDy)
                
                // If moved beyond tap threshold, this is now a cursor move (not a tap)
                if (totalDistance > TAP_SLOP_PX) {
                    // Cancel long press - movement detected
                    handler.removeCallbacks(longPressRunnable)
                }
                
                // Calculate delta from LAST position for cursor movement
                val dx = (event.x - lastTouchX) * prefs.cursorSpeed
                val dy = (event.y - lastTouchY) * prefs.cursorSpeed
                
                val safeW = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth.toFloat() else uiScreenWidth.toFloat()
                val safeH = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight.toFloat() else uiScreenHeight.toFloat()
                
                var finalDx = dx
                var finalDy = dy
                when (rotationAngle) {
                    90 -> { finalDx = -dy; finalDy = dx }
                    180 -> { finalDx = -dx; finalDy = -dy }
                    270 -> { finalDx = dy; finalDy = -dx }
                }
                
                cursorX = (cursorX + finalDx).coerceIn(0f, safeW)
                cursorY = (cursorY + finalDy).coerceIn(0f, safeH)
                
                // Update cursor visual
                if (inputTargetDisplayId == currentDisplayId) {
                    cursorParams.x = cursorX.toInt()
                    cursorParams.y = cursorY.toInt()
                    try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {}
                } else {
                    remoteCursorParams.x = cursorX.toInt()
                    remoteCursorParams.y = cursorY.toInt()
                    try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {}
                }
                
                // Inject appropriate event
                if (isTouchDragging) {
                    injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, SystemClock.uptimeMillis())
                } else {
                    injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
                }
                
                lastTouchX = event.x
                lastTouchY = event.y
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable)
                
                if (!ignoreTouchSequence) {
                    val touchDuration = SystemClock.uptimeMillis() - touchDownTime
                    val totalDx = event.x - touchDownX
                    val totalDy = event.y - touchDownY
                    val totalDistance = kotlin.math.sqrt(totalDx * totalDx + totalDy * totalDy)
                    
                    if (isTouchDragging) {
                        // End drag
                        injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, SystemClock.uptimeMillis())
                        isTouchDragging = false
                        hasSentTouchDown = false
                    } else if (touchDuration < TAP_TIMEOUT_MS && totalDistance < TAP_SLOP_PX) {
                        // This was a TAP - perform click
                        performClick(false)
                    }
                    // Else: Long touch without drag, or moved too much - no click
                }
                
                // Start release debounce
                isReleaseDebouncing = true
                handler.postDelayed(releaseDebounceRunnable, RELEASE_DEBOUNCE_MS)
                
                // Reset state
                isTouchDragging = false
                isVScrolling = false
                isHScrolling = false
                ignoreTouchSequence = false
                updateBorderColor(currentBorderColor)
            }
        }
    }
    // =========================
    // END HANDLE TRACKPAD TOUCH
    // =========================

    // =========================
    // MOVE WINDOW - Handles trackpad overlay position drag
    // Returns early if anchored to prevent accidental movement
    // =========================
    private fun moveWindow(event: MotionEvent): Boolean { 
        if (prefs.prefAnchored) return true  // Anchored: block movement
        if (event.action == MotionEvent.ACTION_MOVE) { 
            trackpadParams.x += (event.rawX - lastTouchX).toInt()
            trackpadParams.y += (event.rawY - lastTouchY).toInt()
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams) 
        }
        lastTouchX = event.rawX
        lastTouchY = event.rawY
        return true 
    }
    // =========================
    // END MOVE WINDOW
    // =========================
    
    // =========================
    // RESIZE WINDOW - Handles trackpad overlay size drag
    // Returns early if anchored to prevent accidental resizing
    // =========================
    private fun resizeWindow(event: MotionEvent): Boolean { 
        if (prefs.prefAnchored) return true  // Anchored: block resize
        if (event.action == MotionEvent.ACTION_MOVE) { 
            trackpadParams.width += (event.rawX - lastTouchX).toInt()
            trackpadParams.height += (event.rawY - lastTouchY).toInt()
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams) 
        }
        lastTouchX = event.rawX
        lastTouchY = event.rawY
        return true 
    }
    // =========================
    // END RESIZE WINDOW
    // =========================
   private fun keyboardHandle(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_UP) toggleCustomKeyboard(); return true }
    private fun openMenuHandle(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_DOWN) menuManager?.show(); return true }
    private fun injectAction(action: Int, source: Int, button: Int, time: Long) { if (shellService == null) return; Thread { try { shellService?.injectMouse(action, cursorX, cursorY, inputTargetDisplayId, source, button, time) } catch(e: Exception){} }.start() }
    private fun injectScroll(hScroll: Float, vScroll: Float) { if (shellService == null) return; Thread { try { shellService?.injectScroll(cursorX, cursorY, vScroll / 10f, hScroll / 10f, inputTargetDisplayId) } catch(e: Exception){} }.start() }
    private fun performClick(right: Boolean) { if (shellService == null) return; Thread { try { if (right) shellService?.execRightClick(cursorX, cursorY, inputTargetDisplayId) else shellService?.execClick(cursorX, cursorY, inputTargetDisplayId) } catch(e: Exception){} }.start() }
    fun resetCursorCenter() { 
        cursorX = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth/2f else uiScreenWidth/2f
        cursorY = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight/2f else uiScreenHeight/2f
        if (inputTargetDisplayId == currentDisplayId) { cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); windowManager?.updateViewLayout(cursorLayout, cursorParams) } 
        else { remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception){} }
    }
    fun performRotation() { rotationAngle = (rotationAngle + 90) % 360; cursorView?.rotation = rotationAngle.toFloat() }
    fun getProfileKey(): String = "P_${uiScreenWidth}_${uiScreenHeight}"
    fun saveLayout() { val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit(); p.putInt("X_${getProfileKey()}", trackpadParams.x); p.putInt("Y_${getProfileKey()}", trackpadParams.y); p.putInt("W_${getProfileKey()}", trackpadParams.width); p.putInt("H_${getProfileKey()}", trackpadParams.height); p.apply(); showToast("Layout Saved") }
    private fun loadLayout() { val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE); trackpadParams.x = p.getInt("X_${getProfileKey()}", 100); trackpadParams.y = p.getInt("Y_${getProfileKey()}", 100); trackpadParams.width = p.getInt("W_${getProfileKey()}", 400); trackpadParams.height = p.getInt("H_${getProfileKey()}", 300); try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch(e: Exception){} }
    fun deleteCurrentProfile() { /* Stub */ }
    fun resetTrackpadPosition() { trackpadParams.x = 100; trackpadParams.y = 100; trackpadParams.width = 400; trackpadParams.height = 300; windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }
    fun cycleInputTarget() { 
        if (displayManager == null) return; val displays = displayManager!!.displays; var nextId = -1
        for (d in displays) { if (d.displayId != currentDisplayId) { if (inputTargetDisplayId == currentDisplayId) { nextId = d.displayId; break } else if (inputTargetDisplayId == d.displayId) { continue } else { nextId = d.displayId } } }
        if (nextId == -1) { inputTargetDisplayId = currentDisplayId; targetScreenWidth = uiScreenWidth; targetScreenHeight = uiScreenHeight; removeRemoteCursor(); cursorX = uiScreenWidth / 2f; cursorY = uiScreenHeight / 2f; cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception){}; cursorView?.visibility = View.VISIBLE; updateBorderColor(0x55FFFFFF.toInt()); showToast("Target: Local (Display $currentDisplayId)") } 
        else { inputTargetDisplayId = nextId; updateTargetMetrics(nextId); createRemoteCursor(nextId); cursorX = targetScreenWidth / 2f; cursorY = targetScreenHeight / 2f; remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception){}; cursorView?.visibility = View.GONE; updateBorderColor(0xFFFF00FF.toInt()); showToast("Target: Display $nextId") }
    }
    private fun createRemoteCursor(displayId: Int) { try { removeRemoteCursor(); val display = displayManager?.getDisplay(displayId) ?: return; val remoteContext = createTrackpadDisplayContext(display); remoteWindowManager = remoteContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager; remoteCursorLayout = FrameLayout(remoteContext); remoteCursorView = ImageView(remoteContext); remoteCursorView?.setImageResource(R.drawable.ic_cursor); val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; remoteCursorLayout?.addView(remoteCursorView, FrameLayout.LayoutParams(size, size)); remoteCursorParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT); remoteCursorParams.gravity = Gravity.TOP or Gravity.LEFT; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); remoteCursorParams.x = metrics.widthPixels / 2; remoteCursorParams.y = metrics.heightPixels / 2; remoteWindowManager?.addView(remoteCursorLayout, remoteCursorParams) } catch (e: Exception) { e.printStackTrace() } }
    private fun removeRemoteCursor() { try { if (remoteCursorLayout != null && remoteWindowManager != null) { remoteWindowManager?.removeView(remoteCursorLayout) } } catch (e: Exception) {}; remoteCursorLayout = null; remoteCursorView = null; remoteWindowManager = null }
    // =========================
    // START TOUCH DRAG - Called by longPressRunnable after 400ms hold
    // Initiates drag mode with touch injection
    // =========================
    private fun startTouchDrag() {
        if (ignoreTouchSequence || isTouchDragging) return
        
        isTouchDragging = true
        dragDownTime = SystemClock.uptimeMillis()
        
        // Inject touch down at current cursor position
        injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, dragDownTime)
        hasSentTouchDown = true
        
        // Visual feedback
        if (prefs.prefVibrate) vibrate()
        updateBorderColor(0xFFFF9900.toInt())  // Orange = drag mode
    }
    // =========================
    // END START TOUCH DRAG
    // =========================

    private fun startResize() {}
    private fun startMove() {}
    private fun startKeyDrag(button: Int) { vibrate(); updateBorderColor(0xFF00FF00.toInt()); dragDownTime = SystemClock.uptimeMillis(); injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime); hasSentMouseDown = true }
    private fun stopKeyDrag(button: Int) { if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt()); if (hasSentMouseDown) injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime); hasSentMouseDown = false }
    private fun handleManualAdjust(intent: Intent) {}
    private fun handlePreview(intent: Intent) {}
    private fun setTrackpadVisibility(visible: Boolean) { isTrackpadVisible = visible; if (trackpadLayout != null) trackpadLayout?.visibility = if (visible) View.VISIBLE else View.GONE }
    private fun setPreviewMode(preview: Boolean) { isPreviewMode = preview; trackpadLayout?.alpha = if (preview) 0.5f else 1.0f }
    override fun onDisplayAdded(displayId: Int) {}
    override fun onDisplayRemoved(displayId: Int) {}
    override fun onDisplayChanged(displayId: Int) {}
    override fun onDestroy() { super.onDestroy(); try { unregisterReceiver(switchReceiver) } catch(e: Exception){}; if (isBound) ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection) }

    private fun showToast(msg: String) {
        handler.post { android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show() }
    }

    private fun updateTargetMetrics(displayId: Int) {
        val display = displayManager?.getDisplay(displayId) ?: return
        val metrics = android.util.DisplayMetrics()
        display.getRealMetrics(metrics)
        targetScreenWidth = metrics.widthPixels
        targetScreenHeight = metrics.heightPixels
    }
}