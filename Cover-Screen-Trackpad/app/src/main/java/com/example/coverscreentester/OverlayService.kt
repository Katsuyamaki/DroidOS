package com.example.coverscreentester


/*
 * ======================================================================================
 * CRITICAL REGRESSION CHECKLIST - OVERLAY SERVICE
 * ======================================================================================
 * METHODOLOGY ENFORCEMENT:
 * * 1. SCROLL LOGIC: 
 * - Detection: defined by `scrollZoneThickness` (approx 15% of width/height).
 * - Execution: `injectScroll` MUST be used. Do not simulate Drag for scrolling.
 * - State: `isVScrolling` / `isHScrolling` flags must block cursor movement logic.
 *
 * 2. DRAG & HOLD LOGIC:
 * - Trigger: `longPressRunnable` (400ms delay) sets `isTouchDragging = true`.
 * - Sensitivity: `DRAG_MOVEMENT_THRESHOLD` (20px) prevents accidental cancels.
 * - Input Source: While `isTouchDragging` is true, injected events MUST use 
 * `InputDevice.SOURCE_TOUCHSCREEN`. Do NOT use `SOURCE_MOUSE` for dragging.
 * - Visuals: Border color must change to Green (0xFF00FF00) during drag.
 *
 * 3. CURSOR MOVEMENT:
 * - Input Source: Standard movement MUST use `InputDevice.SOURCE_MOUSE` (Hover).
 * - Coordinate System: Uses raw screen coordinates (0,0 = top-left).
 * - OFFSETS FORBIDDEN: Do not add manual X/Y offsets (e.g., +100f). 
 * Alignment issues must be fixed via WindowManager flags (Item 11).
 *
 * 4. HARDWARE KEYS:
 * - Vol Up: Acts as Left Click (Tap) or Drag Toggle (Hold).
 * - Vol Down: Acts as Right Click (Back).
 *
 * 5. SAFETY & STABILITY:
 * - Null Checks: All `shellService` calls must be wrapped in `try-catch`.
 * - Crash Prevention: `updatePref` must handle type casting (String/Int/Bool) safely.
 *
 * 6. REMOTE DISPLAY TARGETING:
 * - Logic: `cycleInputTarget` iterates through `displayManager.getDisplays()`.
 * - UI: If targeting Remote, Local cursor View is HIDDEN, Remote cursor View is CREATED.
 * - Context: Remote cursor requires its own `createDisplayContext` + `WindowManager`.
 *
 * 7. AUTOMATION (SMART SCREEN OFF):
 * - Trigger: `toggleCustomKeyboard` calls `turnScreenOn` / `turnScreenOff`.
 * - Targeting: `turnScreenOff` MUST use `currentDisplayId` variable. 
 * NEVER hardcode `0` or `Display.DEFAULT_DISPLAY`.
 * - Modes: Support `setBrightness(-1)` (Alternate) and `setScreenOff(id, true)` (Standard).
 *
 * 8. MENU & LIFECYCLE:
 * - Entry: `onStartCommand` with action `OPEN_MENU` calls `menuManager.show()`.
 * - Hide: `hideApp()` minimizes overlay (visibility GONE) but keeps Service alive.
 * - Exit: `forceExit()` calls `stopSelf()` AND `Process.killProcess()` for clean restart.
 *
 * 9. WINDOW CONFIGURATION (CRITICAL FOR COVER SCREENS):
 * - Flags: `FLAG_LAYOUT_NO_LIMITS` is MANDATORY to reach screen edges.
 * - Cutout: `LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES` is MANDATORY to bypass notches.
 * - Type: `TYPE_ACCESSIBILITY_OVERLAY` is the only window type used.
 *
 * 10. PRESETS & LAYOUT:
 * - `applyLayoutPreset(type)`: 0=Freeform (Load from Prefs), 1=Split Top, 2=Split Bottom.
 * - Keyboard: `setWindowBounds` must be used for absolute positioning in presets.
 *
 * 11. STARTUP & DISPLAY CONTEXT:
 * - Intent: `FORCE_MOVE` extra forces a UI teardown and rebuild.
 * - Context Creation: `WindowManager` MUST be obtained via:
 * `createDisplayContext(display).createWindowContext(TYPE_ACCESSIBILITY_OVERLAY, null)`.
 * Using `applicationContext` will fail on secondary displays (Cover Screen).
 *
 * 12. FOREGROUND SERVICE:
 * - Requirement: `createNotification()` (startForeground) MUST be called immediately 
 * at the top of `onStartCommand` to prevent Android 14+ crashes.
 *
 * 13. UI PERSISTENCE:
 * - Bubble: `resetBubblePosition()` centers bubble. Dragging bubble saves X/Y to prefs.
 * - Handles: Size preference scales 2x visually (`value * 2`) but keeps touch target synced.
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
    
    class Prefs {
        var cursorSpeed = 2.5f
        var scrollSpeed = 3.0f 
        var prefTapScroll = true 
        var prefVibrate = true
        var prefReverseScroll = true
        var prefAlpha = 200
        var prefKeyboardAlpha = 200
        var prefHandleSize = 60 
        var prefVPosLeft = false
        var prefHPosTop = false
        var prefLocked = false
        var prefHandleTouchSize = 80
        var prefScrollTouchSize = 60 
        var prefScrollVisualSize = 4
        var prefCursorSize = 50 
        var prefKeyScale = 100 
        var prefUseAltScreenOff = true
        var prefAutomationEnabled = true
        var prefBubbleX = 50
        var prefBubbleY = 300
    }
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
    private var scrollZoneThickness = 60

    private val longPressRunnable = Runnable { startTouchDrag() }
    private var isResizing = false
    private val resizeLongPressRunnable = Runnable { startResize() }
    private var isMoving = false
    private val moveLongPressRunnable = Runnable { startMove() }
    private val voiceRunnable = Runnable { toggleKeyboardMode() }
    private var keyboardHandleDownTime = 0L
    private val keyboardLongPressRunnable = Runnable { toggleKeyboardMode() }
    private val clearHighlightsRunnable = Runnable { highlightAlpha = false; highlightHandles = false; highlightScrolls = false; updateBorderColor(currentBorderColor); updateLayoutSizes() }
    
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
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(switchReceiver, filter, Context.RECEIVER_EXPORTED) else registerReceiver(switchReceiver, filter)
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
    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (isPreviewMode || !isTrackpadVisible) return super.onKeyEvent(event)
        val action = event.action; val keyCode = event.keyCode
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (action == KeyEvent.ACTION_DOWN) { if (!isLeftKeyHeld) { isLeftKeyHeld = true; startKeyDrag(MotionEvent.BUTTON_PRIMARY) } } else if (action == KeyEvent.ACTION_UP) { isLeftKeyHeld = false; stopKeyDrag(MotionEvent.BUTTON_PRIMARY) }
            return true 
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (action == KeyEvent.ACTION_DOWN) { if (!isRightDragPending) { isRightDragPending = true; handler.postDelayed(voiceRunnable, 1000) } } else if (action == KeyEvent.ACTION_UP) { handler.removeCallbacks(voiceRunnable); if (isRightDragPending) { performClick(true); isRightDragPending = false } }
            return true 
        }
        return super.onKeyEvent(event)
    }

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
                if (!isTouchDragging && !isLeftKeyHeld && !isRightKeyHeld && !isVScrolling && !isHScrolling) {
                    performClick(false)
                }
                return true 
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
    
    fun toggleKeyboardMode() { vibrate(); isRightDragPending = false; if (!isKeyboardMode) { isKeyboardMode = true; savedWindowX = trackpadParams.x; savedWindowY = trackpadParams.y; trackpadParams.x = uiScreenWidth - trackpadParams.width; trackpadParams.y = 0; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(0xFFFF0000.toInt()) } else { isKeyboardMode = false; trackpadParams.x = savedWindowX; trackpadParams.y = savedWindowY; windowManager?.updateViewLayout(trackpadLayout, trackpadParams); updateBorderColor(currentBorderColor) } }
    
    private fun toggleDebugMode() { isDebugMode = !isDebugMode; if (isDebugMode) { showToast("Debug ON"); updateBorderColor(0xFFFFFF00.toInt()); debugTextView?.visibility = View.VISIBLE } else { showToast("Debug OFF"); if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) else updateBorderColor(0x55FFFFFF.toInt()); debugTextView?.visibility = View.GONE } }

    fun updateBubbleStatus() { val dot = bubbleView?.findViewById<ImageView>(R.id.status_dot); if (shellService != null) dot?.visibility = View.GONE else dot?.visibility = View.VISIBLE }
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
    
    fun manualAdjust(isKeyboard: Boolean, isResize: Boolean, dx: Int, dy: Int) { if (isKeyboard) { if (isResize) keyboardOverlay?.resizeWindow(dx, dy) else keyboardOverlay?.moveWindow(dx, dy) } else { if (trackpadLayout == null) return; trackpadParams.x += dx; trackpadParams.y += dy; if (isResize) { trackpadParams.width = max(200, trackpadParams.width + dx); trackpadParams.height = max(200, trackpadParams.height + dy) }; try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch (e: Exception) {}; saveLayout() } }
    
    private fun parseBoolean(value: Any): Boolean { return when(value) { is Boolean -> value; is Int -> value == 1; is String -> value == "1" || value.equals("true", ignoreCase = true); else -> false } }
    
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
            "cursor_size" -> { prefs.prefCursorSize = (value.toString().toIntOrNull() ?: 50); updateCursorSize() }
            "keyboard_key_scale" -> { prefs.prefKeyScale = (value.toString().toIntOrNull() ?: 100); keyboardOverlay?.updateScale(prefs.prefKeyScale / 100f) }
            "use_alt_screen_off" -> prefs.prefUseAltScreenOff = parseBoolean(value) 
            "automation_enabled" -> prefs.prefAutomationEnabled = parseBoolean(value)
        }
        savePrefs() 
    }
    
    // --- PRESETS LOGIC ---
    fun applyLayoutPreset(type: Int) {
        if (type == 0) { // Freeform
            loadLayout()
            // keyboardOverlay?.restoreProfile() // Logic would go here
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
                val kbH = (h * 0.4f).toInt() // Slightly less than 50% to fit UI
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
                
                // KB: Width 90%, centered horizontal, bottom aligned in bottom half (screen bottom)
                val kbW = (w * 0.9f).toInt()
                val kbH = (h * 0.4f).toInt()
                val kbX = (w - kbW) / 2
                val kbY = h - kbH // Bottom of screen
                
                keyboardOverlay?.setWindowBounds(kbX, kbY, kbW, kbH)
            }
        }
        try { windowManager?.updateViewLayout(trackpadLayout, trackpadParams) } catch(e: Exception){} // Don't call saveLayout for presets!
        showToast("Preset $type Applied")
    }
    
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

    private fun loadPrefs() { 
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        prefs.cursorSpeed = p.getFloat("cursor_speed", 2.5f)
        prefs.scrollSpeed = p.getFloat("scroll_speed", 3.0f)
        prefs.prefAlpha = p.getInt("alpha", 200)
        prefs.prefKeyboardAlpha = p.getInt("keyboard_alpha", 200)
        prefs.prefCursorSize = p.getInt("cursor_size", 50)
        prefs.prefUseAltScreenOff = p.getBoolean("use_alt_screen_off", true)
        prefs.prefAutomationEnabled = p.getBoolean("automation_enabled", true)
        prefs.prefBubbleX = p.getInt("bubble_x", 50)
        prefs.prefBubbleY = p.getInt("bubble_y", 300)
        // Reset handles if corrupted or first run
        if (prefs.prefHandleSize > 300) prefs.prefHandleSize = 60
        if (prefs.prefHandleTouchSize > 300) prefs.prefHandleTouchSize = 80
    }
    
    private fun savePrefs() { 
        val e = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        e.putFloat("cursor_speed", prefs.cursorSpeed)
        e.putInt("alpha", prefs.prefAlpha)
        e.putInt("keyboard_alpha", prefs.prefKeyboardAlpha)
        e.putBoolean("use_alt_screen_off", prefs.prefUseAltScreenOff)
        e.putBoolean("automation_enabled", prefs.prefAutomationEnabled)
        e.putInt("bubble_x", prefs.prefBubbleX)
        e.putInt("bubble_y", prefs.prefBubbleY)
        e.apply() 
    }

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
                shellService?.setScreenOff(currentDisplayId, false) // Use specific ID
            } catch(e: Exception) {}
        }.start()
        showToast("Screen On")
    }

    private fun turnScreenOff() {
        isScreenOff = true
        Thread {
            try {
                if (prefs.prefUseAltScreenOff) {
                    shellService?.setBrightness(-1) // Global hack still required for Alt
                } else {
                    shellService?.setScreenOff(currentDisplayId, true) // Use specific ID
                }
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
    private fun addScrollBars(context: Context) { val margin = prefs.prefHandleTouchSize + 10; vScrollContainer = FrameLayout(context); val vp = FrameLayout.LayoutParams(prefs.prefScrollTouchSize, FrameLayout.LayoutParams.MATCH_PARENT); vp.gravity = if (prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; vp.setMargins(0, margin, 0, margin); trackpadLayout?.addView(vScrollContainer, vp); vScrollVisual = View(context); vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt()); vScrollContainer?.addView(vScrollVisual, FrameLayout.LayoutParams(prefs.prefScrollVisualSize, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER)); hScrollContainer = FrameLayout(context); val hp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefs.prefScrollTouchSize); hp.gravity = if (prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM; hp.setMargins(margin, 0, margin, 0); trackpadLayout?.addView(hScrollContainer, hp); hScrollVisual = View(context); hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt()); hScrollContainer?.addView(hScrollVisual, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefs.prefScrollVisualSize, Gravity.CENTER)) }
    private fun updateScrollPosition() { val margin = prefs.prefHandleTouchSize + 10; vScrollContainer?.let { container -> val vp = container.layoutParams as FrameLayout.LayoutParams; vp.gravity = if (prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; vp.setMargins(0, margin, 0, margin); container.layoutParams = vp }; hScrollContainer?.let { container -> val hp = container.layoutParams as FrameLayout.LayoutParams; hp.gravity = if (prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM; hp.setMargins(margin, 0, margin, 0); container.layoutParams = hp } }
    private fun updateHandleSize() { for (v in handleVisuals) { val p = v.layoutParams; p.width = prefs.prefHandleSize; p.height = prefs.prefHandleSize; v.layoutParams = p } }
    private fun updateLayoutSizes() { for (c in handleContainers) { val p = c.layoutParams; p.width = prefs.prefHandleTouchSize; p.height = prefs.prefHandleTouchSize; c.layoutParams = p } }
    private fun updateCursorSize() { val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; cursorView?.layoutParams?.let { it.width = size; it.height = size; cursorView?.layoutParams = it } }
    private fun updateBorderColor(strokeColor: Int) { currentBorderColor = strokeColor; val bg = trackpadLayout?.background as? GradientDrawable ?: return; bg.setColor(Color.TRANSPARENT); val colorWithAlpha = (strokeColor and 0x00FFFFFF) or (prefs.prefAlpha shl 24); bg.setStroke(4, if (highlightAlpha) 0xFF00FF00.toInt() else colorWithAlpha); trackpadLayout?.invalidate() }
    
    private fun handleTrackpadTouch(event: MotionEvent) {
         val viewWidth = trackpadLayout?.width ?: 0; val viewHeight = trackpadLayout?.height ?: 0; if (viewWidth == 0 || viewHeight == 0) return
         when (event.actionMasked) {
             MotionEvent.ACTION_DOWN -> {
                 lastTouchX = event.x; lastTouchY = event.y; isTouchDragging = false; handler.postDelayed(longPressRunnable, 400)
                 val actualZoneV = min(scrollZoneThickness, (viewWidth * 0.15f).toInt()); val actualZoneH = min(scrollZoneThickness, (viewHeight * 0.15f).toInt())
                 val inVZone = if (prefs.prefVPosLeft) event.x < actualZoneV else event.x > (viewWidth - actualZoneV); val inHZone = if (prefs.prefHPosTop) event.y < actualZoneH else event.y > (viewHeight - actualZoneH)
                 if (inVZone || inHZone) { 
                     handler.removeCallbacks(longPressRunnable); if (inVZone) { isVScrolling = true; vibrate() } else { isHScrolling = true; vibrate() }; updateBorderColor(0xFF00FFFF.toInt()) 
                 }
             }
             MotionEvent.ACTION_MOVE -> {
                 val rawDx = (event.x - lastTouchX); val rawDy = (event.y - lastTouchY); val dx = rawDx * prefs.cursorSpeed; val dy = rawDy * prefs.cursorSpeed
                 if (!isTouchDragging && (abs(rawDx) > 20 || abs(rawDy) > 20)) handler.removeCallbacks(longPressRunnable)
                 
                 if (isVScrolling) {
                     scrollAccumulatorY += (event.y - lastTouchY) * prefs.scrollSpeed
                     if (abs(scrollAccumulatorY) > 5) { injectScroll(0f, if (prefs.prefReverseScroll) scrollAccumulatorY else -scrollAccumulatorY); scrollAccumulatorY = 0f }
                 } else if (isHScrolling) {
                     scrollAccumulatorX += (event.x - lastTouchX) * prefs.scrollSpeed
                     if (abs(scrollAccumulatorX) > 5) { injectScroll(if (prefs.prefReverseScroll) scrollAccumulatorX else -scrollAccumulatorX, 0f); scrollAccumulatorX = 0f }
                 } else {
                    val safeW = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth.toFloat() else uiScreenWidth.toFloat(); val safeH = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight.toFloat() else uiScreenHeight.toFloat()
                    var finalDx = dx; var finalDy = dy
                    when (rotationAngle) { 90 -> { finalDx = -dy; finalDy = dx }; 180 -> { finalDx = -dx; finalDy = -dy } }
                    cursorX = (cursorX + finalDx).coerceIn(0f, safeW); cursorY = (cursorY + finalDy).coerceIn(0f, safeH)
                    if (inputTargetDisplayId == currentDisplayId) { cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception){} } 
                    else { remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception){} }
                    if (isTouchDragging) injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, SystemClock.uptimeMillis())
                    else injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
                 }
                 lastTouchX = event.x; lastTouchY = event.y
             }
             MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                 handler.removeCallbacks(longPressRunnable); if (isTouchDragging) injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, SystemClock.uptimeMillis()); isTouchDragging = false; isVScrolling = false; isHScrolling = false; updateBorderColor(currentBorderColor)
             }
         }
    }
    
    private fun moveWindow(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_MOVE) { trackpadParams.x += (event.rawX - lastTouchX).toInt(); trackpadParams.y += (event.rawY - lastTouchY).toInt(); windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }; lastTouchX = event.rawX; lastTouchY = event.rawY; return true }
    private fun resizeWindow(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_MOVE) { trackpadParams.width += (event.rawX - lastTouchX).toInt(); trackpadParams.height += (event.rawY - lastTouchY).toInt(); windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }; lastTouchX = event.rawX; lastTouchY = event.rawY; return true }
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
        if (nextId == -1) { inputTargetDisplayId = currentDisplayId; targetScreenWidth = uiScreenWidth; targetScreenHeight = uiScreenHeight; removeRemoteCursor(); cursorX = uiScreenWidth / 2f; cursorY = uiScreenHeight / 2f; cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception){}; cursorView?.visibility = View.VISIBLE; updateBorderColor(0x55FFFFFF.toInt()); showToast("Target: Local (Display $currentDisplayId)"); vibrate() } 
        else { inputTargetDisplayId = nextId; updateTargetMetrics(nextId); createRemoteCursor(nextId); cursorX = targetScreenWidth / 2f; cursorY = targetScreenHeight / 2f; remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception){}; cursorView?.visibility = View.GONE; updateBorderColor(0xFFFF00FF.toInt()); showToast("Target: Display $nextId"); vibrate() }
    }
    private fun createRemoteCursor(displayId: Int) { try { removeRemoteCursor(); val display = displayManager?.getDisplay(displayId) ?: return; val remoteContext = createTrackpadDisplayContext(display); remoteWindowManager = remoteContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager; remoteCursorLayout = FrameLayout(remoteContext); remoteCursorView = ImageView(remoteContext); remoteCursorView?.setImageResource(R.drawable.ic_cursor); val size = if (prefs.prefCursorSize > 0) prefs.prefCursorSize else 50; remoteCursorLayout?.addView(remoteCursorView, FrameLayout.LayoutParams(size, size)); remoteCursorParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT); remoteCursorParams.gravity = Gravity.TOP or Gravity.LEFT; val metrics = android.util.DisplayMetrics(); display.getRealMetrics(metrics); remoteCursorParams.x = metrics.widthPixels / 2; remoteCursorParams.y = metrics.heightPixels / 2; remoteWindowManager?.addView(remoteCursorLayout, remoteCursorParams) } catch (e: Exception) { e.printStackTrace() } }
    private fun removeRemoteCursor() { try { if (remoteCursorLayout != null && remoteWindowManager != null) { remoteWindowManager?.removeView(remoteCursorLayout) } } catch (e: Exception) {}; remoteCursorLayout = null; remoteCursorView = null; remoteWindowManager = null }
    private fun startTouchDrag() { isTouchDragging = true; vibrate(); updateBorderColor(0xFF00FF00.toInt()); injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, SystemClock.uptimeMillis()) }
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
