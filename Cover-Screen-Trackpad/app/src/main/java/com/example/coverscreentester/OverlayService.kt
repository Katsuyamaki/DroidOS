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
    private var appWindowManager: WindowManager? = null
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
    
    // Heartbeat to keep hardware state alive
    private val blockingHeartbeat = object : Runnable {
        override fun run() {
            if (prefs.prefBlockSoftKeyboard && shellService != null) {
                // Silent heartbeat (no toasts)
                try { shellService?.injectDummyHardwareKey(0) } catch(e: Exception){}
                handler.postDelayed(this, 2000) 
            }
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
    }
    val prefs = Prefs()

    // =========================
    // KEY INJECTION
    // =========================
    private fun injectKey(keyCode: Int, action: Int = KeyEvent.ACTION_DOWN, metaState: Int = 0) {
        // Use Virtual ID (-1) for text to prevent buffering
        shellService?.injectKey(keyCode, action, metaState, inputTargetDisplayId, -1)
    }

    // =========================
    // BLOCKING TRIGGER (Global)
    // =========================
    private fun triggerAggressiveBlocking() {
        if (!prefs.prefBlockSoftKeyboard || shellService == null) return
        
        Thread {
            try {
                shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 0")
                shellService?.injectDummyHardwareKey(0) 
                if (currentDisplayId != 0) shellService?.injectDummyHardwareKey(currentDisplayId)
                
                // Extra ID 1 Pulse for safety
                shellService?.injectKey(KeyEvent.KEYCODE_UNKNOWN, KeyEvent.ACTION_DOWN, 0, 0, 1)
                shellService?.injectKey(KeyEvent.KEYCODE_UNKNOWN, KeyEvent.ACTION_UP, 0, 0, 1)
                
            } catch(e: Exception){}
        }.start()
    }

    private fun setSoftKeyboardBlocking(enabled: Boolean) {
        // Accessibility: Force AUTO on Main Screen
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                if (currentDisplayId != 0 && enabled) {
                    softKeyboardController.showMode = AccessibilityService.SHOW_MODE_HIDDEN
                } else {
                    softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO
                }
            } catch (e: Exception) {}
        }

        if (enabled) {
            triggerAggressiveBlocking()
            handler.post(blockingHeartbeat)
        } else {
            handler.removeCallbacks(blockingHeartbeat)
            Thread { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") }.start()
        }
        
        val statusMsg = if (enabled) "BLOCKED" else "ALLOWED"
        showToast("Soft Keyboard: $statusMsg")
    }

    // =========================
    // ACCESSIBILITY EVENT (DEBUG FOCUS WATCHDOG)
    // =========================
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        // Catch Focus, Window State, and Window Content changes
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || 
            event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            
            if (prefs.prefBlockSoftKeyboard) {
                // DEBUG: Announce Focus Change
                val pkgName = event.packageName?.toString() ?: "System/Unknown"
                // Only toast if it's a significant change to avoid pure spam
                if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    showToast("Focus: $pkgName")
                }
                
                if (shellService != null) {
                    Thread {
                        try {
                            // Re-assert settings
                            shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 0")
                            
                            // Fire Hardware Signal (ID 0 & ID 1)
                            shellService?.injectDummyHardwareKey(0)
                            shellService?.injectKey(KeyEvent.KEYCODE_UNKNOWN, KeyEvent.ACTION_DOWN, 0, 0, 1)
                            shellService?.injectKey(KeyEvent.KEYCODE_UNKNOWN, KeyEvent.ACTION_UP, 0, 0, 1)
                            
                            if (currentDisplayId != 0) shellService?.injectDummyHardwareKey(currentDisplayId)
                            
                            // DEBUG: Confirm Signal Fired
                            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                                handler.post { showToast("HW Signal: Success") }
                            }
                        } catch(e: Exception) {
                            handler.post { showToast("HW Signal: Failed") }
                        }
                    }.start()
                }
            }
            
            // Anti-Buffering Safety Check
            if (currentDisplayId == 0 && Build.VERSION.SDK_INT >= 24) {
                try {
                    if (softKeyboardController.showMode == AccessibilityService.SHOW_MODE_HIDDEN) {
                        softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO
                    }
                } catch(e: Exception){}
            }
        }
    }

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

    private val longPressRunnable = Runnable { startTouchDrag() }
    private val clearHighlightsRunnable = Runnable { updateBorderColor(currentBorderColor); updateLayoutSizes() }
    
    private var isKeyDragging = false
    private var activeDragButton = MotionEvent.BUTTON_PRIMARY
    
    private var lastVolUpTime: Long = 0L
    private var lastVolDownTime: Long = 0L
    private var volUpTapCount = 0
    private var volDownTapCount = 0
    private var volUpHoldTriggered = false
    private var volDownHoldTriggered = false
    private var volUpDragActive = false
    private var volDownDragActive = false
    
    private val volUpHoldRunnable = Runnable {
        volUpHoldTriggered = true
        executeHardkeyAction(prefs.hardkeyVolUpHold, KeyEvent.ACTION_DOWN)
    }
    private val volDownHoldRunnable = Runnable {
        volDownHoldTriggered = true
        executeHardkeyAction(prefs.hardkeyVolDownHold, KeyEvent.ACTION_DOWN)
    }
    
    private val volUpDoubleTapRunnable = Runnable {
        if (volUpTapCount == 1) executeHardkeyAction(prefs.hardkeyVolUpTap)
        volUpTapCount = 0
    }
    private val volDownDoubleTapRunnable = Runnable {
        if (volDownTapCount == 1) executeHardkeyAction(prefs.hardkeyVolDownTap)
        volDownTapCount = 0
    }

    private val switchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
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
                Intent.ACTION_SCREEN_ON -> triggerAggressiveBlocking()
            }
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
            addAction(Intent.ACTION_SCREEN_ON)
        }
        ContextCompat.registerReceiver(this, switchReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        
        if (Build.VERSION.SDK_INT >= 24) {
            try { softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO } catch(e: Exception){}
        }
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

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (isPreviewMode || !isTrackpadVisible) return super.onKeyEvent(event)
        
        val action = event.action
        val keyCode = event.keyCode
        
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
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

    private fun setupUI(displayId: Int) {
        try {
            if (windowManager != null) {
                if (bubbleView != null) windowManager?.removeView(bubbleView)
                if (cursorLayout != null) windowManager?.removeView(cursorLayout)
            }
            if (trackpadLayout != null && windowManager != null) {
                 try { windowManager?.removeView(trackpadLayout) } catch(e: Exception){}
            }
            keyboardOverlay?.hide()
            keyboardOverlay = null
            menuManager?.hide()
            menuManager = null
        } catch (e: Exception) {}

        val display = displayManager?.getDisplay(displayId)
        if (display == null) {
            showToast("Error: Display $displayId not found")
            return
        }

        try {
            val displayContext = createDisplayContext(display)
            val accessContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 displayContext.createWindowContext(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null)
            } else displayContext
            
            windowManager = accessContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            appWindowManager = windowManager

            currentDisplayId = displayId
            inputTargetDisplayId = displayId

            updateUiMetrics()
            
            setupTrackpad(accessContext)
            if (shellService != null) initCustomKeyboard()
            menuManager = TrackpadMenuManager(displayContext, windowManager!!, this)
            setupBubble(accessContext)
            setupCursor(accessContext)

            enforceZOrder()
            showToast("Trackpad active on Display $displayId")
            
            if (prefs.prefBlockSoftKeyboard) triggerAggressiveBlocking()

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
        trackpadLayout?.visibility = if (isTrackpadVisible) View.VISIBLE else View.GONE
        if (isTrackpadVisible) updateBorderColor(currentBorderColor) 
        else if (isCustomKeyboardVisible) toggleCustomKeyboard(suppressAutomation = true)
    }
    
    private fun handleBubbleTap() {
        var didHideSomething = false
        if (isCustomKeyboardVisible) {
            toggleCustomKeyboard(suppressAutomation = true)
            didHideSomething = true
        }
        toggleTrackpad() 
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
        prefs.prefPersistentService = p.getBoolean("persistent_service", false)
        prefs.prefBlockSoftKeyboard = p.getBoolean("block_soft_kb", false)
    }
    
    private fun savePrefs() { 
        val e = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        e.putBoolean("persistent_service", prefs.prefPersistentService)
        e.putBoolean("block_soft_kb", prefs.prefBlockSoftKeyboard)
        e.apply() 
    }

    private fun bindShizuku() { try { val c = ComponentName(packageName, ShellUserService::class.java.name); ShizukuBinder.bind(c, userServiceConnection, BuildConfig.DEBUG, BuildConfig.VERSION_CODE) } catch (e: Exception) { e.printStackTrace() } }
    
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
        keyboardOverlay = KeyboardOverlay(this, appWindowManager!!, shellService, inputTargetDisplayId, { toggleScreen() }, { toggleScreenMode() }, { toggleCustomKeyboard() })
        keyboardOverlay?.setScreenDimensions(uiScreenWidth, uiScreenHeight, currentDisplayId) 
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
                handler.removeCallbacks(releaseDebounceRunnable); isReleaseDebouncing = false
                touchDownTime = SystemClock.uptimeMillis(); touchDownX = event.x; touchDownY = event.y; lastTouchX = event.x; lastTouchY = event.y; isTouchDragging = false
                val actualZoneV = min(scrollZoneThickness, (viewWidth * 0.15f).toInt()); val actualZoneH = min(scrollZoneThickness, (viewHeight * 0.15f).toInt())
                if ((prefs.prefVPosLeft && event.x < actualZoneV) || (!prefs.prefVPosLeft && event.x > viewWidth - actualZoneV) || (prefs.prefHPosTop && event.y < actualZoneH) || (!prefs.prefHPosTop && event.y > viewHeight - actualZoneH)) { ignoreTouchSequence = true; return }
                handler.postDelayed(longPressRunnable, 400)
            }
            MotionEvent.ACTION_MOVE -> {
                if (ignoreTouchSequence) return
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

    private fun moveWindow(event: MotionEvent): Boolean { if (prefs.prefAnchored) return true; if (event.action == MotionEvent.ACTION_MOVE) { trackpadParams.x += (event.rawX - lastTouchX).toInt(); trackpadParams.y += (event.rawY - lastTouchY).toInt(); windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }; lastTouchX = event.rawX; lastTouchY = event.rawY; return true }
    private fun resizeWindow(event: MotionEvent): Boolean { if (prefs.prefAnchored) return true; if (event.action == MotionEvent.ACTION_MOVE) { trackpadParams.width += (event.rawX - lastTouchX).toInt(); trackpadParams.height += (event.rawY - lastTouchY).toInt(); windowManager?.updateViewLayout(trackpadLayout, trackpadParams) }; lastTouchX = event.rawX; lastTouchY = event.rawY; return true }
    private fun keyboardHandle(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_UP) toggleCustomKeyboard(); return true }
    private fun openMenuHandle(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_DOWN) menuManager?.toggle(); return true }
    private fun injectAction(action: Int, source: Int, button: Int, time: Long) { if (shellService == null) return; Thread { try { shellService?.injectMouse(action, cursorX, cursorY, inputTargetDisplayId, source, button, time) } catch(e: Exception){} }.start() }
    private fun injectScroll(hScroll: Float, vScroll: Float) { if (shellService == null) return; Thread { try { shellService?.injectScroll(cursorX, cursorY, vScroll / 10f, hScroll / 10f, inputTargetDisplayId) } catch(e: Exception){} }.start() }
    private fun performClick(right: Boolean) { if (shellService == null) return; Thread { try { if (right) shellService?.execRightClick(cursorX, cursorY, inputTargetDisplayId) else shellService?.execClick(cursorX, cursorY, inputTargetDisplayId) } catch(e: Exception){} }.start() }
    fun resetCursorCenter() { cursorX = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth/2f else uiScreenWidth/2f; cursorY = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight/2f else uiScreenHeight/2f; if (inputTargetDisplayId == currentDisplayId) { cursorParams.x = cursorX.toInt(); cursorParams.y = cursorY.toInt(); windowManager?.updateViewLayout(cursorLayout, cursorParams) } else { remoteCursorParams.x = cursorX.toInt(); remoteCursorParams.y = cursorY.toInt(); try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception){} } }
    fun performRotation() { rotationAngle = (rotationAngle + 90) % 360; cursorView?.rotation = rotationAngle.toFloat() }
    fun getProfileKey(): String = "P_${uiScreenWidth}_${uiScreenHeight}"
    
    fun saveLayout() { 
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
                if (parts.size >= 31) { keyboardOverlay?.updatePosition(parts[27].toInt(), parts[28].toInt()); keyboardOverlay?.updateSize(parts[29].toInt(), parts[30].toInt()) } 
                else if (parts.size >= 29) { keyboardOverlay?.updatePosition(parts[27].toInt(), parts[28].toInt()) }
                updateBorderColor(currentBorderColor); updateLayoutSizes(); updateScrollSize(); updateHandleSize(); updateCursorSize(); keyboardOverlay?.updateAlpha(prefs.prefKeyboardAlpha); keyboardOverlay?.updateScale(prefs.prefKeyScale / 100f); keyboardOverlay?.setAnchored(prefs.prefAnchored)
                if (bubbleView != null) { bubbleParams.x = prefs.prefBubbleX; bubbleParams.y = prefs.prefBubbleY; windowManager?.updateViewLayout(bubbleView, bubbleParams); applyBubbleAppearance() }
                showToast("Layout Loaded")
            }
        }
    }
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
    override fun onDisplayChanged(displayId: Int) {}
    
    override fun onDestroy() { 
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= 24) { try { softKeyboardController.showMode = AccessibilityService.SHOW_MODE_AUTO } catch (e: Exception) {} }
        Thread { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") }.start()
        try { unregisterReceiver(switchReceiver) } catch(e: Exception){}; 
        if (isBound) ShizukuBinder.unbind(ComponentName(packageName, ShellUserService::class.java.name), userServiceConnection) 
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
        Thread {
            try {
                injectKey(keyCode, KeyEvent.ACTION_DOWN, metaState)
                Thread.sleep(20)
                injectKey(keyCode, KeyEvent.ACTION_UP, metaState)
            } catch (e: Exception) { Log.e(TAG, "Key injection failed", e) }
        }.start()
    }
}
