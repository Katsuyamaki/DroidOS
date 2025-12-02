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
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Display
import android.view.GestureDetector
import android.view.Gravity
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.abs
import kotlin.math.max
import java.util.ArrayList
import com.example.coverscreentester.BuildConfig

class OverlayService : AccessibilityService(), DisplayManager.DisplayListener {

    private var windowManager: WindowManager? = null
    private var displayManager: DisplayManager? = null
    private var trackpadLayout: FrameLayout? = null
    private lateinit var trackpadParams: WindowManager.LayoutParams
    private var cursorLayout: FrameLayout? = null
    private var cursorView: ImageView? = null
    private lateinit var cursorParams: WindowManager.LayoutParams
    
    private var debugTextView: TextView? = null
    
    private var remoteWindowManager: WindowManager? = null
    private var remoteCursorLayout: FrameLayout? = null
    private var remoteCursorView: ImageView? = null
    private lateinit var remoteCursorParams: WindowManager.LayoutParams
    
    private var shellService: IShellService? = null
    private var isBound = false
    
    private var keyboardOverlay: KeyboardOverlay? = null
    private var isCustomKeyboardVisible = false
    
    private var isTrackpadVisible = true
    private var isPreviewMode = false
    
    private var currentOverlayDisplayId = 0
    private var currentDisplayId = -1 
    private var inputTargetDisplayId = -1
    private var lastLoadedProfileKey = ""

    private var uiScreenWidth = 1080
    private var uiScreenHeight = 2640
    private var targetScreenWidth = 1920
    private var targetScreenHeight = 1080

    private var cursorX = 300f
    private var cursorY = 300f
    private var virtualScrollX = 0f
    private var virtualScrollY = 0f
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
    
    private var cursorSpeed = 2.5f
    private var scrollSpeed = 3.0f 
    private var scrollZoneThickness = 60 
    private var prefVibrate = true
    private var prefReverseScroll = true
    private var prefAlpha = 200
    private var prefHandleSize = 60 
    private var prefVPosLeft = false
    private var prefHPosTop = false
    private var prefLocked = false
    private var prefHandleTouchSize = 60
    private var prefScrollTouchSize = 60
    private var prefScrollVisualSize = 4
    private var prefCursorSize = 50 
    
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
    
    private val handler = Handler(Looper.getMainLooper())
    private val longPressRunnable = Runnable { startTouchDrag() }
    private var isResizing = false
    private val resizeLongPressRunnable = Runnable { startResize() }
    private var isMoving = false
    private val moveLongPressRunnable = Runnable { startMove() }
    private val voiceRunnable = Runnable { toggleKeyboardMode() }
    
    private var keyboardHandleDownTime = 0L
    private val keyboardLongPressRunnable = Runnable { toggleKeyboardMode() }
    
    private val clearHighlightsRunnable = Runnable {
        highlightAlpha = false; highlightHandles = false; highlightScrolls = false
        updateBorderColor(currentBorderColor); updateLayoutSizes() 
    }
    
    private val switchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                "RESET_CURSOR" -> resetCursorCenter()
                "TOGGLE_DEBUG" -> toggleDebugMode()
                "FORCE_KEYBOARD" -> toggleCustomKeyboard()
                "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
                "SET_TRACKPAD_VISIBILITY" -> {
                    val visible = intent.getBooleanExtra("VISIBLE", true)
                    val menuDisplayId = intent.getIntExtra("MENU_DISPLAY_ID", -1)
                    
                    if (visible) {
                        setTrackpadVisibility(true)
                    } else {
                        // Only hide if menu is on the SAME display as this overlay
                        if (menuDisplayId == -1 || menuDisplayId == currentDisplayId) {
                            setTrackpadVisibility(false)
                        }
                    }
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
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (isPreviewMode || !isTrackpadVisible) return super.onKeyEvent(event)
        
        val action = event.action
        val keyCode = event.keyCode
        
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (action == KeyEvent.ACTION_DOWN) { 
                if (!isLeftKeyHeld) { 
                    isLeftKeyHeld = true
                    startKeyDrag(MotionEvent.BUTTON_PRIMARY) 
                } 
            } else if (action == KeyEvent.ACTION_UP) { 
                isLeftKeyHeld = false
                stopKeyDrag(MotionEvent.BUTTON_PRIMARY) 
            }
            return true 
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (action == KeyEvent.ACTION_DOWN) { 
                if (!isRightDragPending) { 
                    isRightDragPending = true
                    handler.postDelayed(voiceRunnable, 1000) 
                } 
            } else if (action == KeyEvent.ACTION_UP) { 
                handler.removeCallbacks(voiceRunnable)
                if (isRightDragPending) { 
                    performClick(true)
                    isRightDragPending = false 
                } 
            }
            return true 
        }
        return super.onKeyEvent(event)
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) { 
            shellService = IShellService.Stub.asInterface(binder)
            isBound = true 
            Thread { shellService?.runCommand("settings put secure show_ime_with_hard_keyboard 1") }.start()
            initCustomKeyboard()
        }
        override fun onServiceDisconnected(name: ComponentName?) { 
            shellService = null
            isBound = false 
        }
    }

    override fun onCreate() {
        super.onCreate()
        try { 
            displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            displayManager?.registerDisplayListener(this, handler) 
        } catch (e: Exception) { 
            Log.e(TAG, "Failed to init DisplayManager", e) 
        }
        val filter = IntentFilter().apply {
            addAction("CYCLE_INPUT_TARGET")
            addAction("RESET_CURSOR")
            addAction("TOGGLE_DEBUG")
            addAction("FORCE_KEYBOARD")
            addAction("TOGGLE_CUSTOM_KEYBOARD")
            addAction("SET_TRACKPAD_VISIBILITY")
            addAction("SET_PREVIEW_MODE")
        }
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(switchReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(switchReceiver, filter)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        try { 
            createNotification()
            bindShizuku()
            loadPrefs()
            if (displayManager == null) { 
                displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                displayManager?.registerDisplayListener(this, handler) 
            }
            setupWindows(Display.DEFAULT_DISPLAY) 
        } catch (e: Exception) { 
            Log.e(TAG, "Crash in onServiceConnected", e) 
        }
    }

    override fun onDisplayAdded(displayId: Int) {}
    override fun onDisplayRemoved(displayId: Int) {}
    override fun onDisplayChanged(displayId: Int) { 
        if (displayId == currentDisplayId) { 
            updateUiMetrics(displayId)
            val newKey = getProfileKey()
            if (newKey != lastLoadedProfileKey) loadLayout() 
        }
    }

    private fun updateUiMetrics(displayId: Int) {
        try {
            if (displayManager == null) displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val display = displayManager?.getDisplay(displayId) ?: return
            val metrics = android.util.DisplayMetrics()
            display.getRealMetrics(metrics)
            if (metrics.widthPixels > 0) { 
                uiScreenWidth = metrics.widthPixels
                uiScreenHeight = metrics.heightPixels
                if (inputTargetDisplayId == -1 || inputTargetDisplayId == displayId) { 
                    targetScreenWidth = uiScreenWidth
                    targetScreenHeight = uiScreenHeight 
                }
                keyboardOverlay?.setScreenDimensions(uiScreenWidth, uiScreenHeight, currentOverlayDisplayId)
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateUiMetrics failed", e)
        }
    }

    private fun updateTargetMetrics(displayId: Int) {
        try {
            if (displayManager == null) displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val display = displayManager?.getDisplay(displayId)
            if (display == null) { 
                targetScreenWidth = 1920
                targetScreenHeight = 1080
                return 
            }
            val metrics = android.util.DisplayMetrics()
            display.getRealMetrics(metrics)
            if (metrics.widthPixels > 0) { 
                targetScreenWidth = metrics.widthPixels
                targetScreenHeight = metrics.heightPixels 
            } else { 
                targetScreenWidth = 1920
                targetScreenHeight = 1080 
            }
        } catch (e: Exception) { 
            targetScreenWidth = 1920
            targetScreenHeight = 1080 
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
                }
                "PREVIEW_UPDATE" -> handlePreview(intent)
                "CYCLE_INPUT_TARGET" -> cycleInputTarget()
                "RESET_CURSOR" -> resetCursorCenter()
                "TOGGLE_DEBUG" -> toggleDebugMode()
                "FORCE_KEYBOARD" -> toggleCustomKeyboard()
                "TOGGLE_CUSTOM_KEYBOARD" -> toggleCustomKeyboard()
            }
            if (intent?.hasExtra("DISPLAY_ID") == true) {
                val targetDisplayId = intent.getIntExtra("DISPLAY_ID", Display.DEFAULT_DISPLAY)
                // --- FIX IMPLEMENTED HERE ---
                // If force move is TRUE (user clicked button) OR if trackpad is null (fresh start)
                val forceMove = intent.getBooleanExtra("FORCE_MOVE", false)
                
                if (trackpadLayout == null || forceMove) {
                    removeOverlays()
                    setupWindows(targetDisplayId) 
                } else {
                    Log.d(TAG, "Trackpad already active. Ignoring automatic move to Display $targetDisplayId")
                }
                // ----------------------------
            }
        } catch (e: Exception) { 
            Log.e(TAG, "Crash in onStartCommand", e) 
        }
        return START_STICKY
    }
    
    private fun setTrackpadVisibility(visible: Boolean) {
        if (!visible && isTrackpadVisible) {
            resetAllTouchStates()
        }
        
        isTrackpadVisible = visible
        trackpadLayout?.visibility = if (visible) View.VISIBLE else View.GONE
        cursorLayout?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setPreviewMode(preview: Boolean) {
        if (preview && !isPreviewMode) {
            resetAllTouchStates()
        }
        
        isPreviewMode = preview
        
        if (preview) {
            trackpadLayout?.visibility = View.VISIBLE
            cursorLayout?.visibility = View.VISIBLE
            trackpadParams.flags = trackpadParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            try {
                windowManager?.updateViewLayout(trackpadLayout, trackpadParams)
            } catch (e: Exception) {}
            trackpadLayout?.alpha = 0.7f
        } else {
            trackpadParams.flags = trackpadParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
            try {
                windowManager?.updateViewLayout(trackpadLayout, trackpadParams)
            } catch (e: Exception) {}
            trackpadLayout?.alpha = 1.0f
        }
    }
    
    private fun resetAllTouchStates() {
        handler.removeCallbacks(longPressRunnable)
        handler.removeCallbacks(resizeLongPressRunnable)
        handler.removeCallbacks(moveLongPressRunnable)
        handler.removeCallbacks(voiceRunnable)
        
        if (isTouchDragging && hasSentTouchDown) {
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, dragDownTime)
        }
        isTouchDragging = false
        hasSentTouchDown = false
        
        if (isLeftKeyHeld && hasSentMouseDown) {
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, dragDownTime)
        }
        isLeftKeyHeld = false
        
        if (isRightKeyHeld && hasSentMouseDown) {
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_SECONDARY, dragDownTime)
        }
        isRightKeyHeld = false
        hasSentMouseDown = false
        
        if ((isVScrolling || isHScrolling) && hasSentScrollDown) {
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY)
        }
        isVScrolling = false
        isHScrolling = false
        hasSentScrollDown = false
        
        isRightDragPending = false
        isMoving = false
        isResizing = false
        isClicking = false
        activeFingerDeviceId = -1
        
        if (!isDebugMode) {
            if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
            else updateBorderColor(0x55FFFFFF.toInt())
        }
    }
    
    private fun initCustomKeyboard() {
        if (windowManager == null || shellService == null) return
        keyboardOverlay = KeyboardOverlay(this, windowManager!!, shellService, inputTargetDisplayId)
        keyboardOverlay?.setScreenDimensions(uiScreenWidth, uiScreenHeight, currentOverlayDisplayId)
    }
    
    private fun toggleCustomKeyboard() {
        if (keyboardOverlay == null && shellService != null) initCustomKeyboard()
        
        if (keyboardOverlay != null) {
            val wasVisible = keyboardOverlay?.isShowing() == true
            if (wasVisible) keyboardOverlay?.hide()
            keyboardOverlay = KeyboardOverlay(this, windowManager!!, shellService, inputTargetDisplayId)
            keyboardOverlay?.setScreenDimensions(uiScreenWidth, uiScreenHeight, currentOverlayDisplayId)
            if (!wasVisible) keyboardOverlay?.show()
        } else {
            keyboardOverlay?.toggle()
        }
        
        isCustomKeyboardVisible = keyboardOverlay?.isShowing() ?: false
        if (isCustomKeyboardVisible) { 
            updateBorderColor(0xFF9C27B0.toInt()) 
        } else { 
            if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
            else updateBorderColor(0x55FFFFFF.toInt()) 
        }
        vibrate()
    }

    private fun toggleDebugMode() { 
        isDebugMode = !isDebugMode
        if (isDebugMode) { 
            showToast("Debug ON")
            updateBorderColor(0xFFFFFF00.toInt()) 
            debugTextView?.visibility = View.VISIBLE
        } else { 
            showToast("Debug OFF")
            if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
            else updateBorderColor(0x55FFFFFF.toInt()) 
            debugTextView?.visibility = View.GONE
        } 
    }
    
    private fun resetCursorCenter() { 
        cursorX = (targetScreenWidth / 2).toFloat()
        cursorY = (targetScreenHeight / 2).toFloat()
        // Don't inject hover on reset - just update visual
        if (inputTargetDisplayId == currentDisplayId) { 
            cursorParams.x = cursorX.toInt()
            cursorParams.y = cursorY.toInt()
            try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {} 
        } else { 
            remoteCursorParams.x = cursorX.toInt()
            remoteCursorParams.y = cursorY.toInt()
            try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {} 
        }
        showToast("Reset to ${cursorX.toInt()}x${cursorY.toInt()}")
        vibrate() 
    }
    
    private fun handleManualAdjust(intent: Intent) { 
        if (windowManager == null || trackpadLayout == null) return
        val dx = intent.getIntExtra("DX", 0)
        val dy = intent.getIntExtra("DY", 0)
        val dw = intent.getIntExtra("DW", 0)
        val dh = intent.getIntExtra("DH", 0)
        trackpadParams.x += dx
        trackpadParams.y += dy
        trackpadParams.width = max(200, trackpadParams.width + dw)
        trackpadParams.height = max(200, trackpadParams.height + dh)
        try { 
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams)
            saveLayout() 
        } catch (e: Exception) {} 
    }
    
    private fun removeOverlays() { 
        try { 
            keyboardOverlay?.hide()
            if (trackpadLayout != null) { 
                windowManager?.removeView(trackpadLayout)
                trackpadLayout = null 
            }
            if (cursorLayout != null) { 
                windowManager?.removeView(cursorLayout)
                cursorLayout = null 
            }
            removeRemoteCursor()
        } catch (e: Exception) {} 
    }

    private fun setupWindows(displayId: Int) {
        if (trackpadLayout != null && displayId == currentDisplayId) return
        try {
            updateUiMetrics(displayId)
            if (uiScreenWidth == 0) updateUiMetrics(displayId)
            val displayContext = createDisplayContext(displayManager!!.getDisplay(displayId))
            windowManager = displayContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            currentDisplayId = displayId
            inputTargetDisplayId = displayId
            currentOverlayDisplayId = displayId
            targetScreenWidth = uiScreenWidth
            targetScreenHeight = uiScreenHeight
            
            // CURSOR WINDOW
            cursorLayout = FrameLayout(displayContext)
            cursorView = ImageView(displayContext)
            cursorView?.setImageResource(R.drawable.ic_cursor)
            val size = if (prefCursorSize > 0) prefCursorSize else 50
            cursorLayout?.addView(cursorView, FrameLayout.LayoutParams(size, size))
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
            cursorParams.gravity = Gravity.TOP or Gravity.LEFT
            cursorX = uiScreenWidth / 2f
            cursorY = uiScreenHeight / 2f
            cursorParams.x = cursorX.toInt()
            cursorParams.y = cursorY.toInt()
            windowManager?.addView(cursorLayout, cursorParams)

            // TRACKPAD WINDOW
            trackpadLayout = FrameLayout(displayContext)
            val bg = GradientDrawable()
            bg.cornerRadius = 30f
            trackpadLayout?.background = bg
            updateBorderColor(0x55FFFFFF.toInt())
            trackpadLayout?.isFocusable = true
            trackpadLayout?.isFocusableInTouchMode = true
            
            handleContainers.clear()
            handleVisuals.clear()
            val handleColor = 0x15FFFFFF.toInt()
            addHandle(displayContext, Gravity.TOP or Gravity.RIGHT, handleColor) { _, e -> moveWindow(e) }
            addHandle(displayContext, Gravity.BOTTOM or Gravity.RIGHT, handleColor) { _, e -> resizeWindow(e) }
            addHandle(displayContext, Gravity.BOTTOM or Gravity.LEFT, handleColor) { _, e -> openMenuHandle(e) }
            addHandle(displayContext, Gravity.TOP or Gravity.LEFT, handleColor) { _, e -> keyboardHandle(e) }
            addScrollBars(displayContext)
            
            // DEBUG TEXT
            debugTextView = TextView(displayContext)
            debugTextView?.text = "DEBUG"
            debugTextView?.setTextColor(Color.YELLOW)
            debugTextView?.setBackgroundColor(0xAA000000.toInt())
            debugTextView?.textSize = 9f
            debugTextView?.visibility = View.GONE
            val debugParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, 
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            debugParams.gravity = Gravity.CENTER
            trackpadLayout?.addView(debugTextView, debugParams)

            // TRACKPAD PARAMS
            trackpadParams = WindowManager.LayoutParams(
                400, 300,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or 
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
            trackpadParams.gravity = Gravity.TOP or Gravity.LEFT
            trackpadParams.title = "TrackpadInput"
            
            loadOverlayPositionForDisplay(displayId)
            
            // GESTURE DETECTOR for single tap
            val gestureDetector = GestureDetector(displayContext, object : GestureDetector.SimpleOnGestureListener() { 
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean { 
                    if (!isTouchDragging && !isLeftKeyHeld && !isRightKeyHeld && 
                        !isVScrolling && !isHScrolling && !isPreviewMode && 
                        isTrackpadVisible && !isClicking) {
                        performClick(false)
                    }
                    return true 
                } 
            })
            
            // MAIN TOUCH LISTENER
            trackpadLayout?.setOnTouchListener { _, event -> 
                val devId = event.deviceId
                val tool = event.getToolType(0)
                val action = event.actionMasked
                
                // Debug logging
                if (isDebugMode) {
                    val toolName = when(tool) {
                        MotionEvent.TOOL_TYPE_FINGER -> "FNGR"
                        MotionEvent.TOOL_TYPE_MOUSE -> "MOUS"
                        MotionEvent.TOOL_TYPE_STYLUS -> "STYL"
                        else -> "?$tool"
                    }
                    val actionName = when(action) {
                        MotionEvent.ACTION_DOWN -> "DOWN"
                        MotionEvent.ACTION_MOVE -> "MOVE"
                        MotionEvent.ACTION_UP -> "UP"
                        MotionEvent.ACTION_CANCEL -> "CNCL"
                        else -> "?$action"
                    }
                    val state = "D:$isTouchDragging L:$isLeftKeyHeld R:$isRightKeyHeld sent:$hasSentTouchDown"
                    debugTextView?.text = "$actionName $toolName id:$devId\n$state\nCUR:${cursorX.toInt()},${cursorY.toInt()}"
                }
                
                // FILTER 1: Only accept FINGER tool type
                if (tool != MotionEvent.TOOL_TYPE_FINGER) {
                    return@setOnTouchListener false
                }
                
                // FILTER 2: Reject device ID <= 0 (synthetic/system events)
                if (devId <= 0) {
                    // If we get a cancel with ID -1, clean up our state
                    if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                        if (activeFingerDeviceId > 0) {
                            Log.w(TAG, "Synthetic cancel received (id=$devId), cleaning up state")
                            cleanupAfterCancel()
                        }
                    }
                    return@setOnTouchListener false
                }
                
                // FILTER 3: Track active finger - only process events from the finger that started the gesture
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        activeFingerDeviceId = devId
                    }
                    MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        if (activeFingerDeviceId > 0 && devId != activeFingerDeviceId) {
                            // Different finger/device - ignore
                            return@setOnTouchListener false
                        }
                    }
                }
                
                // Skip if clicking animation in progress
                if (isClicking) return@setOnTouchListener true
                
                // Process input if trackpad is active
                if (!isPreviewMode && isTrackpadVisible) {
                    gestureDetector.onTouchEvent(event)
                    handleTrackpadTouch(event)
                }
                true 
            }
            
            windowManager?.addView(trackpadLayout, trackpadParams)
            loadLayout()
            
            if (shellService != null) initCustomKeyboard()
        } catch (e: Exception) { 
            Log.e(TAG, "Setup Windows Crash", e) 
        }
    }
    
    /**
     * Called when we receive a synthetic cancel event (device ID -1 or 0)
     * This cleans up state without sending injection commands that could cause more issues
     */
    private fun cleanupAfterCancel() {
        handler.removeCallbacks(longPressRunnable)
        handler.removeCallbacks(resizeLongPressRunnable)
        handler.removeCallbacks(moveLongPressRunnable)
        handler.removeCallbacks(voiceRunnable)
        
        // Send UP events for any active drags to clean up remote state
        if (isTouchDragging && hasSentTouchDown) {
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, dragDownTime)
        }
        if ((isVScrolling || isHScrolling) && hasSentScrollDown) {
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY)
        }
        
        // Reset all state
        isTouchDragging = false
        hasSentTouchDown = false
        isVScrolling = false
        isHScrolling = false
        hasSentScrollDown = false
        isMoving = false
        isResizing = false
        activeFingerDeviceId = -1
        
        // Update border color
        if (!isDebugMode) {
            if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
            else updateBorderColor(0x55FFFFFF.toInt())
        }
    }
    
    private fun loadOverlayPositionForDisplay(displayId: Int) {
        currentOverlayDisplayId = displayId
        val prefs = getSharedPreferences("TrackpadPrefs", MODE_PRIVATE)
        
        val defaultX = (uiScreenWidth - 400) / 2
        val defaultY = (uiScreenHeight - 300) / 2
        
        if (prefs.contains("overlay_x_d$displayId")) {
            trackpadParams.x = prefs.getInt("overlay_x_d$displayId", defaultX)
            trackpadParams.y = prefs.getInt("overlay_y_d$displayId", defaultY)
            trackpadParams.width = prefs.getInt("overlay_width_d$displayId", 400)
            trackpadParams.height = prefs.getInt("overlay_height_d$displayId", 300)
        } else {
            trackpadParams.x = defaultX
            trackpadParams.y = defaultY
            trackpadParams.width = 400
            trackpadParams.height = 300
        }
    }
    
    private fun saveOverlayPosition() {
        val prefs = getSharedPreferences("TrackpadPrefs", MODE_PRIVATE)
        prefs.edit()
            .putInt("overlay_x_d$currentOverlayDisplayId", trackpadParams.x)
            .putInt("overlay_y_d$currentOverlayDisplayId", trackpadParams.y)
            .apply()
    }
    
    private fun saveOverlaySize() {
        val prefs = getSharedPreferences("TrackpadPrefs", MODE_PRIVATE)
        prefs.edit()
            .putInt("overlay_width_d$currentOverlayDisplayId", trackpadParams.width)
            .putInt("overlay_height_d$currentOverlayDisplayId", trackpadParams.height)
            .apply()
    }
    
    private fun createRemoteCursor(displayId: Int) { 
        try { 
            removeRemoteCursor()
            val display = displayManager?.getDisplay(displayId) ?: return
            val remoteContext = createDisplayContext(display)
            remoteWindowManager = remoteContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            remoteCursorLayout = FrameLayout(remoteContext)
            remoteCursorView = ImageView(remoteContext)
            remoteCursorView?.setImageResource(R.drawable.ic_cursor)
            val size = if (prefCursorSize > 0) prefCursorSize else 50
            remoteCursorLayout?.addView(remoteCursorView, FrameLayout.LayoutParams(size, size))
            remoteCursorParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or 
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )
            remoteCursorParams.gravity = Gravity.TOP or Gravity.LEFT
            val metrics = android.util.DisplayMetrics()
            display.getRealMetrics(metrics)
            remoteCursorParams.x = metrics.widthPixels / 2
            remoteCursorParams.y = metrics.heightPixels / 2
            remoteWindowManager?.addView(remoteCursorLayout, remoteCursorParams) 
        } catch (e: Exception) { 
            Log.e(TAG, "Failed to create remote cursor", e)
            showToast("Failed to draw on Display $displayId") 
        } 
    }
    
    private fun removeRemoteCursor() { 
        try { 
            if (remoteCursorLayout != null && remoteWindowManager != null) { 
                remoteWindowManager?.removeView(remoteCursorLayout) 
            } 
        } catch(e: Exception) {}
        remoteCursorLayout = null
        remoteCursorView = null
        remoteWindowManager = null 
    }
    
    private fun updateCursorSize() { 
        val size = if (prefCursorSize > 0) prefCursorSize else 50
        cursorView?.layoutParams?.let { lp ->
            lp.width = size
            lp.height = size
            cursorView?.layoutParams = lp
        }
        remoteCursorView?.layoutParams?.let { lp ->
            lp.width = size
            lp.height = size
            remoteCursorView?.layoutParams = lp
        }
    }
    
    private fun getProfileKey(): String { 
        if (uiScreenHeight == 0) return "profile_1.0"
        val ratio = uiScreenWidth.toFloat() / uiScreenHeight.toFloat()
        return "profile_" + String.format("%.1f", ratio) 
    }
    
    private fun saveLayout() { 
        if (trackpadLayout == null || uiScreenWidth == 0 || uiScreenHeight == 0) return
        val key = getProfileKey()
        lastLoadedProfileKey = key
        val xPct = trackpadParams.x.toFloat() / uiScreenWidth.toFloat()
        val yPct = trackpadParams.y.toFloat() / uiScreenHeight.toFloat()
        val wPct = trackpadParams.width.toFloat() / uiScreenWidth.toFloat()
        val hPct = trackpadParams.height.toFloat() / uiScreenHeight.toFloat()
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        p.edit()
            .putFloat("${key}_xp", xPct)
            .putFloat("${key}_yp", yPct)
            .putFloat("${key}_wp", wPct)
            .putFloat("${key}_hp", hPct)
            .putBoolean("${key}_saved", true)
            .apply()
        saveOverlayPosition()
        saveOverlaySize()
        vibrate() 
    }
    
    private fun loadLayout() { 
        if (trackpadLayout == null || windowManager == null) return
        val key = getProfileKey()
        lastLoadedProfileKey = key
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        if (p.getBoolean("${key}_saved", false)) { 
            val xPct = p.getFloat("${key}_xp", 0.1f)
            val yPct = p.getFloat("${key}_yp", 0.1f)
            val wPct = p.getFloat("${key}_wp", 0.5f)
            val hPct = p.getFloat("${key}_hp", 0.4f)
            val calcW = (wPct * uiScreenWidth).toInt()
            val calcH = (hPct * uiScreenHeight).toInt()
            trackpadParams.width = calcW.coerceAtLeast(300)
            trackpadParams.height = calcH.coerceAtLeast(300)
            trackpadParams.x = (xPct * uiScreenWidth).toInt()
            trackpadParams.y = (yPct * uiScreenHeight).toInt() 
        } else { 
            trackpadParams.width = 400
            trackpadParams.height = 300
            trackpadParams.x = (uiScreenWidth / 2) - 200
            trackpadParams.y = (uiScreenHeight / 2) - 150 
        }
        try { 
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams) 
        } catch (e: Exception) {} 
    }
    
    private fun deleteCurrentProfile() { 
        val key = getProfileKey()
        getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
            .remove("${key}_saved")
            .remove("${key}_xp")
            .remove("${key}_yp")
            .remove("${key}_wp")
            .remove("${key}_hp")
            .apply()
        resetTrackpadPosition() 
    }
    
    private fun resetTrackpadPosition() { 
        if (windowManager == null || trackpadLayout == null) return
        trackpadParams.width = 400
        trackpadParams.height = 300
        val centerX = (uiScreenWidth / 2) - 200
        val centerY = (uiScreenHeight / 2) - 150
        trackpadParams.x = if (centerX > 0) centerX else 100
        trackpadParams.y = if (centerY > 0) centerY else 100
        try { 
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams)
            vibrate() 
        } catch (e: Exception) {} 
    }
    
    private fun moveWindow(event: MotionEvent): Boolean { 
        if (prefLocked) return true
        when (event.action) { 
            MotionEvent.ACTION_DOWN -> { 
                handler.postDelayed(moveLongPressRunnable, 1000)
                initialWindowX = trackpadParams.x
                initialWindowY = trackpadParams.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                return true 
            }
            MotionEvent.ACTION_MOVE -> { 
                if (isMoving) { 
                    trackpadParams.x = initialWindowX + (event.rawX - initialTouchX).toInt()
                    trackpadParams.y = initialWindowY + (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(trackpadLayout, trackpadParams) 
                } else if (abs(event.rawX - initialTouchX) > 20) {
                    handler.removeCallbacks(moveLongPressRunnable)
                }
                return true 
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { 
                handler.removeCallbacks(moveLongPressRunnable)
                if (isMoving) stopMove()
                return true 
            } 
        }
        return false 
    }
    
    private fun startMove() { 
        isMoving = true
        vibrate()
        updateBorderColor(0xFF0000FF.toInt()) 
    }
    
    private fun stopMove() { 
        isMoving = false
        saveOverlayPosition()
        if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
        else updateBorderColor(0x55FFFFFF.toInt()) 
    }
    
    private fun resizeWindow(event: MotionEvent): Boolean { 
        if (prefLocked) return true
        when (event.action) { 
            MotionEvent.ACTION_DOWN -> { 
                handler.postDelayed(resizeLongPressRunnable, 1000)
                initialWindowWidth = trackpadParams.width
                initialWindowHeight = trackpadParams.height
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                return true 
            }
            MotionEvent.ACTION_MOVE -> { 
                if (isResizing) { 
                    val deltaX = event.rawX - initialTouchX
                    val deltaY = event.rawY - initialTouchY
                    val newWidth = (initialWindowWidth + deltaX).toInt()
                    val newHeight = (initialWindowHeight + deltaY).toInt()
                    trackpadParams.width = max(300, newWidth)
                    trackpadParams.height = max(300, newHeight)
                    windowManager?.updateViewLayout(trackpadLayout, trackpadParams) 
                } else if (abs(event.rawX - initialTouchX) > 20) {
                    handler.removeCallbacks(resizeLongPressRunnable)
                }
                return true 
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { 
                handler.removeCallbacks(resizeLongPressRunnable)
                if (isResizing) stopResize()
                return true 
            } 
        }
        return false 
    }
    
    private fun startResize() { 
        isResizing = true
        vibrate()
        updateBorderColor(0xFF0000FF.toInt()) 
    }
    
    private fun stopResize() { 
        isResizing = false
        saveOverlaySize()
        if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
        else updateBorderColor(0x55FFFFFF.toInt()) 
    }
    
    /**
     * Check if cursor is currently inside the trackpad window bounds (on same display)
     */
    private fun isCursorOverTrackpad(): Boolean {
        if (inputTargetDisplayId != currentDisplayId) return false
        if (trackpadLayout == null) return false
        
        val tX = trackpadParams.x.toFloat()
        val tY = trackpadParams.y.toFloat()
        val tW = trackpadParams.width.toFloat()
        val tH = trackpadParams.height.toFloat()
        
        return cursorX >= tX && cursorX <= (tX + tW) && 
               cursorY >= tY && cursorY <= (tY + tH)
    }
    
    private fun handleTrackpadTouch(event: MotionEvent) {
        val viewWidth = trackpadLayout?.width ?: 0
        val viewHeight = trackpadLayout?.height ?: 0
        
        if (viewWidth == 0 || viewHeight == 0) return

        when (event.actionMasked) { 
            MotionEvent.ACTION_DOWN -> { 
                isVScrolling = false
                isHScrolling = false
                isTouchDragging = false
                handler.removeCallbacks(longPressRunnable)
                
                lastTouchX = event.x
                lastTouchY = event.y
                
                val inVZone = if (prefVPosLeft) event.x < scrollZoneThickness else event.x > (viewWidth - scrollZoneThickness)
                val inHZone = if (prefHPosTop) event.y < scrollZoneThickness else event.y > (viewHeight - scrollZoneThickness)
                
                if (inVZone) { 
                    isVScrolling = true
                    vibrate()
                    updateBorderColor(0xFF00FFFF.toInt())
                    virtualScrollX = cursorX
                    virtualScrollY = cursorY
                    dragDownTime = SystemClock.uptimeMillis()
                    
                    // Start simulated scroll (Touch Down)
                    if (!isCursorOverTrackpad()) {
                        injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY)
                        hasSentScrollDown = true
                    }
                } else if (inHZone) { 
                    isHScrolling = true
                    vibrate()
                    updateBorderColor(0xFF00FFFF.toInt())
                    virtualScrollX = cursorX
                    virtualScrollY = cursorY
                    dragDownTime = SystemClock.uptimeMillis()
                    
                    // Start simulated scroll (Touch Down)
                    if (!isCursorOverTrackpad()) {
                        injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY)
                        hasSentScrollDown = true
                    }
                } else {
                    handler.postDelayed(longPressRunnable, 400) 
                }
            }
            MotionEvent.ACTION_MOVE -> { 
                val rawDx = (event.x - lastTouchX) * cursorSpeed
                val rawDy = (event.y - lastTouchY) * cursorSpeed
                
                if (isVScrolling) { 
                    // Simulate Touch Scroll (Drag)
                    val dist = (event.y - lastTouchY) * scrollSpeed
                    if (abs(dist) > 0) { 
                        if (prefReverseScroll) virtualScrollY += dist else virtualScrollY -= dist
                        if (hasSentScrollDown) {
                            injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY)
                        }
                    } 
                } else if (isHScrolling) { 
                     // Simulate Touch Scroll (Drag)
                    val dist = (event.x - lastTouchX) * scrollSpeed
                    if (abs(dist) > 0) { 
                        if (prefReverseScroll) virtualScrollX += dist else virtualScrollX -= dist
                        if (hasSentScrollDown) {
                            injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY)
                        }
                    } 
                } else { 
                    var finalDx = rawDx
                    var finalDy = rawDy
                    when (rotationAngle) { 
                        90 -> { finalDx = -rawDy; finalDy = rawDx }
                        180 -> { finalDx = -rawDx; finalDy = -rawDy } 
                        270 -> { finalDx = rawDy; finalDy = -rawDx } 
                    }
                    
                    // Cancel long press if moving significantly
                    if (!isTouchDragging && (abs(rawDx) > 5 || abs(rawDy) > 5)) { 
                        handler.removeCallbacks(longPressRunnable)
                        if (isRightDragPending) { 
                            isRightDragPending = false
                            handler.removeCallbacks(voiceRunnable)
                            isRightKeyHeld = true
                            startKeyDrag(MotionEvent.BUTTON_SECONDARY) 
                        } 
                    }
                    
                    // Store old cursor position to check if we crossed trackpad boundary
                    val wasOverTrackpad = isCursorOverTrackpad()
                    
                    // Update cursor position
                    val safeW = if (inputTargetDisplayId != currentDisplayId) targetScreenWidth.toFloat() else uiScreenWidth.toFloat()
                    val safeH = if (inputTargetDisplayId != currentDisplayId) targetScreenHeight.toFloat() else uiScreenHeight.toFloat()
                    
                    val margin = 2f
                    cursorX = (cursorX + finalDx).coerceIn(margin, safeW - margin)
                    cursorY = (cursorY + finalDy).coerceIn(margin, safeH - margin)
                    
                    // Update visual cursor
                    if (inputTargetDisplayId == currentDisplayId) { 
                        cursorParams.x = cursorX.toInt()
                        cursorParams.y = cursorY.toInt()
                        try { windowManager?.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {} 
                    } else { 
                        remoteCursorParams.x = cursorX.toInt()
                        remoteCursorParams.y = cursorY.toInt()
                        try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, remoteCursorParams) } catch(e: Exception) {} 
                    }
                    
                    // Check if cursor is now over trackpad
                    val nowOverTrackpad = isCursorOverTrackpad()
                    
                    // DEFERRED INJECTION: If we're dragging and cursor just exited trackpad, send DOWN now
                    if (isTouchDragging && !hasSentTouchDown && wasOverTrackpad && !nowOverTrackpad) {
                        injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, pendingDragDownTime)
                        hasSentTouchDown = true
                    }
                    
                    // Same for key drags
                    if (isLeftKeyHeld && !hasSentMouseDown && wasOverTrackpad && !nowOverTrackpad) {
                        injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, dragDownTime)
                        hasSentMouseDown = true
                    }
                    if (isRightKeyHeld && !hasSentMouseDown && wasOverTrackpad && !nowOverTrackpad) {
                        injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_SECONDARY, dragDownTime)
                        hasSentMouseDown = true
                    }
                    
                    // Only inject MOVE if cursor is outside trackpad AND we've sent DOWN
                    if (!nowOverTrackpad) {
                        if (isTouchDragging && hasSentTouchDown) {
                            injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, pendingDragDownTime)
                        } else if (isLeftKeyHeld && hasSentMouseDown) {
                            injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, dragDownTime)
                        } else if (isRightKeyHeld && hasSentMouseDown) {
                            injectAction(MotionEvent.ACTION_MOVE, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_SECONDARY, dragDownTime)
                        } else if (!isTouchDragging && !isLeftKeyHeld && !isRightKeyHeld) {
                            // ALWAYS INJECT HOVER MOVE
                            injectAction(MotionEvent.ACTION_HOVER_MOVE, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
                        }
                    }
                }
                lastTouchX = event.x
                lastTouchY = event.y 
            }
            MotionEvent.ACTION_UP -> { 
                handler.removeCallbacks(longPressRunnable)
                if (isTouchDragging) stopTouchDrag()
                
                if ((isVScrolling || isHScrolling) && hasSentScrollDown) { 
                    injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, 0, dragDownTime, virtualScrollX, virtualScrollY)
                }
                
                isVScrolling = false
                isHScrolling = false
                hasSentScrollDown = false
                activeFingerDeviceId = -1
                
                if (isDebugMode) { 
                    showToast("Disp:$inputTargetDisplayId | X:${cursorX.toInt()} Y:${cursorY.toInt()}")
                    updateBorderColor(0xFFFFFF00.toInt()) 
                } else { 
                    if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
                    else updateBorderColor(0x55FFFFFF.toInt()) 
                } 
            }
            MotionEvent.ACTION_CANCEL -> {
                // Cancel received - clean up but don't send injections (might cause loop)
                handler.removeCallbacks(longPressRunnable)
                isTouchDragging = false
                hasSentTouchDown = false
                isVScrolling = false
                isHScrolling = false
                hasSentScrollDown = false
                activeFingerDeviceId = -1
                
                if (!isDebugMode) {
                    if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
                    else updateBorderColor(0x55FFFFFF.toInt())
                }
            }
        } 
    }
    
    private fun performRotation() { 
        rotationAngle = (rotationAngle + 90) % 360
        cursorView?.rotation = rotationAngle.toFloat()
        vibrate()
        updateBorderColor(0xFFFFFF00.toInt())
    }
    
    private fun startKeyDrag(button: Int) { 
        vibrate()
        updateBorderColor(0xFF00FF00.toInt())
        dragDownTime = SystemClock.uptimeMillis()
        // Only inject immediately if cursor is outside trackpad
        if (!isCursorOverTrackpad()) {
            injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime)
            hasSentMouseDown = true
        }
        // Otherwise, hasSentMouseDown stays false and we'll send DOWN when cursor exits
    }
    
    private fun stopKeyDrag(button: Int) { 
        if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
        else updateBorderColor(0x55FFFFFF.toInt())
        if (hasSentMouseDown) { 
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, button, dragDownTime) 
        }
        hasSentMouseDown = false
    }
    
    private fun startTouchDrag() { 
        isTouchDragging = true
        vibrate()
        updateBorderColor(0xFF00FF00.toInt())
        pendingDragDownTime = SystemClock.uptimeMillis()
        
        // Only inject immediately if cursor is outside trackpad
        if (!isCursorOverTrackpad()) {
            injectAction(MotionEvent.ACTION_DOWN, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, pendingDragDownTime)
            hasSentTouchDown = true
        }
        // Otherwise, hasSentTouchDown stays false and we'll send DOWN when cursor exits trackpad
    }
    
    private fun stopTouchDrag() { 
        isTouchDragging = false
        if (inputTargetDisplayId != currentDisplayId) updateBorderColor(0xFFFF00FF.toInt()) 
        else updateBorderColor(0x55FFFFFF.toInt())
        if (hasSentTouchDown) { 
            injectAction(MotionEvent.ACTION_UP, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY, pendingDragDownTime) 
        }
        hasSentTouchDown = false
    }
    
    private fun injectAction(action: Int, source: Int, buttonState: Int, downTime: Long, x: Float = cursorX, y: Float = cursorY) { 
        if (shellService == null) return
        
        val dId = if (inputTargetDisplayId != -1) inputTargetDisplayId 
                  else (cursorLayout?.display?.displayId ?: Display.DEFAULT_DISPLAY)
        
        // Fire and forget on background thread - no blocking
        Thread {
            try {
                // Use CURRENT event time for the action, but preserve original downTime
                shellService?.injectMouse(action, x, y, dId, source, buttonState, downTime)
            } catch (e: Exception) {
                Log.e(TAG, "Injection failed", e)
            }
        }.start()
    }
    
    private fun injectScroll(vDist: Float, hDist: Float) { 
        if (shellService == null) return
        val dId = if (inputTargetDisplayId != -1) inputTargetDisplayId 
                  else (cursorLayout?.display?.displayId ?: Display.DEFAULT_DISPLAY)
        Thread {
            try { 
                shellService?.injectScroll(cursorX, cursorY, vDist, hDist, dId) 
            } catch (e: Exception) {
                Log.e(TAG, "Scroll injection failed", e)
            }
        }.start()
    }
    
    private fun performClick(isRightClick: Boolean) { 
        if (shellService == null) { 
            bindShizuku()
            return 
        }
        // CRITICAL: Only click if cursor is outside trackpad (on same display)
        if (isCursorOverTrackpad()) {
            if (isDebugMode) showToast("Click blocked - cursor over trackpad")
            return
        }
        Thread {
            try { 
                if (isRightClick) {
                    shellService?.execRightClick(cursorX, cursorY, inputTargetDisplayId) 
                } else {
                    shellService?.execClick(cursorX, cursorY, inputTargetDisplayId) 
                }
            } catch (e: Exception) {
                Log.e(TAG, "Click injection failed", e)
            }
        }.start()
    }
    
    private fun cycleInputTarget() { 
        if (displayManager == null) return
        try { 
            val displays = displayManager!!.displays
            var nextId = -1
            for (d in displays) { 
                if (d.displayId != currentDisplayId) { 
                    if (inputTargetDisplayId == currentDisplayId) { 
                        nextId = d.displayId
                        break 
                    } else if (inputTargetDisplayId == d.displayId) { 
                        continue 
                    } else { 
                        nextId = d.displayId 
                    } 
                } 
            }
            if (nextId == -1) { 
                inputTargetDisplayId = currentDisplayId
                targetScreenWidth = uiScreenWidth
                targetScreenHeight = uiScreenHeight
                removeRemoteCursor()
                cursorX = (uiScreenWidth / 2).toFloat()
                cursorY = (uiScreenHeight / 2).toFloat()
                cursorParams.x = cursorX.toInt()
                cursorParams.y = cursorY.toInt()
                windowManager?.updateViewLayout(cursorLayout, cursorParams)
                currentBorderColor = 0x55FFFFFF.toInt()
                updateBorderColor(currentBorderColor)
                cursorView?.visibility = View.VISIBLE
                showToast("Target: Local (Display $currentDisplayId)")
                vibrate()
            } else { 
                inputTargetDisplayId = nextId
                updateTargetMetrics(nextId)
                createRemoteCursor(nextId)
                cursorX = (targetScreenWidth / 2).toFloat()
                cursorY = (targetScreenHeight / 2).toFloat()
                currentBorderColor = 0xFFFF00FF.toInt()
                updateBorderColor(currentBorderColor)
                cursorView?.visibility = View.GONE
                showToast("Target: Display $nextId (${targetScreenWidth}x${targetScreenHeight})")
                vibrate()
                vibrate()
            }
            if (isCustomKeyboardVisible) { 
                keyboardOverlay?.hide()
                keyboardOverlay = KeyboardOverlay(this, windowManager!!, shellService, inputTargetDisplayId)
                keyboardOverlay?.setScreenDimensions(uiScreenWidth, uiScreenHeight, currentOverlayDisplayId)
                keyboardOverlay?.show() 
            }
        } catch (e: Exception) { 
            Log.e(TAG, "Cycle Error", e) 
        } 
    }
    
    private fun showToast(msg: String) { 
        handler.post { 
            android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show() 
        } 
    }
    
    private fun vibrate() { 
        if (!prefVibrate) return
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)) 
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(50) 
        }
    }
    
    private fun bindShizuku() { 
        try { 
            val c = ComponentName(packageName, ShellUserService::class.java.name)
            ShizukuBinder.bind(c, userServiceConnection, BuildConfig.DEBUG, BuildConfig.VERSION_CODE) 
        } catch (e: Exception) {
            Log.e(TAG, "Shizuku bind failed", e)
        } 
    }
    
    private fun createNotification() { 
        val channel = NotificationChannel("overlay_service", "Trackpad Active", NotificationManager.IMPORTANCE_LOW)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        val notification = Notification.Builder(this, "overlay_service")
            .setContentTitle("Trackpad Active")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) 
        } else {
            startForeground(1, notification) 
        }
    }
    
    override fun onDestroy() { 
        super.onDestroy()
        try { unregisterReceiver(switchReceiver) } catch(e: Exception) {}
        displayManager?.unregisterDisplayListener(this)
        if (trackpadLayout != null) windowManager?.removeView(trackpadLayout)
        if (cursorLayout != null) windowManager?.removeView(cursorLayout)
        removeRemoteCursor()
        if (isBound) {
            ShizukuBinder.unbind(
                ComponentName(packageName, ShellUserService::class.java.name), 
                userServiceConnection
            )
        }
    }
    
    private fun toggleKeyboardMode() { 
        vibrate()
        isRightDragPending = false
        if (!isKeyboardMode) { 
            isKeyboardMode = true
            savedWindowX = trackpadParams.x
            savedWindowY = trackpadParams.y
            trackpadParams.x = uiScreenWidth - trackpadParams.width
            trackpadParams.y = 0
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams)
            updateBorderColor(0xFFFF0000.toInt()) 
        } else { 
            isKeyboardMode = false
            trackpadParams.x = savedWindowX
            trackpadParams.y = savedWindowY
            windowManager?.updateViewLayout(trackpadLayout, trackpadParams)
            updateBorderColor(currentBorderColor) 
        } 
    }
    
    private fun openMenuHandle(event: MotionEvent): Boolean { 
        if (event.action == MotionEvent.ACTION_DOWN) { 
            vibrate()
            val intent = Intent(this, MainActivity::class.java).apply { 
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) 
            }
            startActivity(intent)
            return true 
        }
        return false 
    }
    
    private fun keyboardHandle(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { 
                keyboardHandleDownTime = SystemClock.uptimeMillis()
                handler.postDelayed(keyboardLongPressRunnable, 800)
                return true 
            }
            MotionEvent.ACTION_UP -> { 
                handler.removeCallbacks(keyboardLongPressRunnable)
                if (SystemClock.uptimeMillis() - keyboardHandleDownTime < 800) { 
                    toggleCustomKeyboard() 
                }
                return true 
            }
            MotionEvent.ACTION_CANCEL -> { 
                handler.removeCallbacks(keyboardLongPressRunnable)
                return true 
            }
        }
        return false
    }
    
    private fun loadPrefs() { 
        val p = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        cursorSpeed = p.getFloat("cursor_speed", 2.5f)
        scrollSpeed = p.getFloat("scroll_speed", 3.0f)
        prefVibrate = p.getBoolean("vibrate", true)
        prefReverseScroll = p.getBoolean("reverse_scroll", true)
        prefAlpha = p.getInt("alpha", 200)
        prefLocked = p.getBoolean("lock_position", false)
        prefVPosLeft = p.getBoolean("v_pos_left", false)
        prefHPosTop = p.getBoolean("h_pos_top", false)
        prefHandleTouchSize = p.getInt("handle_touch_size", 60)
        prefScrollTouchSize = p.getInt("scroll_touch_size", 60)
        prefHandleSize = p.getInt("handle_size", 60)
        prefScrollVisualSize = p.getInt("scroll_visual_size", 4)
        scrollZoneThickness = prefScrollTouchSize
        prefCursorSize = p.getInt("cursor_size", 50) 
    }
    
    private fun handlePreview(intent: Intent) { 
        val target = intent.getStringExtra("TARGET")
        val value = intent.getIntExtra("VALUE", 0)
        handler.removeCallbacks(clearHighlightsRunnable)
        when (target) { 
            "alpha" -> { 
                prefAlpha = value
                highlightAlpha = true
                updateBorderColor(currentBorderColor) 
            }
            "handle_touch" -> { 
                prefHandleTouchSize = value
                highlightHandles = true
                updateLayoutSizes() 
            }
            "scroll_touch" -> { 
                prefScrollTouchSize = value
                scrollZoneThickness = value
                highlightScrolls = true
                updateLayoutSizes()
                updateScrollPosition() 
            }
            "handle_size" -> { 
                prefHandleSize = value
                highlightHandles = true
                updateHandleSize() 
            }
            "scroll_visual" -> { 
                prefScrollVisualSize = value
                highlightScrolls = true
                updateLayoutSizes() 
            }
            "cursor_size" -> { 
                prefCursorSize = value
                updateCursorSize() 
            } 
        }
        handler.postDelayed(clearHighlightsRunnable, 1500) 
    }
    
    private fun addHandle(context: Context, gravity: Int, color: Int, onTouch: (View, MotionEvent) -> Boolean) { 
        val container = FrameLayout(context)
        val containerParams = FrameLayout.LayoutParams(prefHandleTouchSize, prefHandleTouchSize)
        containerParams.gravity = gravity
        
        val visual = View(context)
        val bg = GradientDrawable()
        bg.setColor(color)
        bg.cornerRadii = floatArrayOf(15f, 15f, 15f, 15f, 15f, 15f, 15f, 15f)
        visual.background = bg
        
        val visualParams = FrameLayout.LayoutParams(prefHandleSize, prefHandleSize)
        visualParams.gravity = Gravity.CENTER
        container.addView(visual, visualParams)
        
        handleVisuals.add(visual)
        handleContainers.add(container)
        trackpadLayout?.addView(container, containerParams)
        container.setOnTouchListener { view, e -> onTouch(view, e) } 
    }
    
    private fun updateHandleSize() { 
        for (v in handleVisuals) { 
            val p = v.layoutParams
            p.width = prefHandleSize
            p.height = prefHandleSize
            v.layoutParams = p 
        } 
    }
    
    private fun updateLayoutSizes() { 
        for (c in handleContainers) { 
            val p = c.layoutParams
            p.width = prefHandleTouchSize
            p.height = prefHandleTouchSize
            c.layoutParams = p 
        }
        updateScrollPosition() 
    }
    
    private fun addScrollBars(context: Context) { 
        val margin = prefHandleTouchSize + 10
        
        vScrollContainer = FrameLayout(context)
        val vParams = FrameLayout.LayoutParams(prefScrollTouchSize, FrameLayout.LayoutParams.MATCH_PARENT)
        vParams.gravity = if (prefVPosLeft) Gravity.LEFT else Gravity.RIGHT
        vParams.setMargins(0, margin, 0, margin)
        trackpadLayout?.addView(vScrollContainer, vParams)
        
        vScrollVisual = View(context)
        vScrollVisual!!.setBackgroundColor(0x30FFFFFF.toInt())
        val vvParams = FrameLayout.LayoutParams(prefScrollVisualSize, FrameLayout.LayoutParams.MATCH_PARENT)
        vvParams.gravity = Gravity.CENTER_HORIZONTAL
        vScrollContainer?.addView(vScrollVisual, vvParams)
        
        hScrollContainer = FrameLayout(context)
        val hParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefScrollTouchSize)
        hParams.gravity = if (prefHPosTop) Gravity.TOP else Gravity.BOTTOM
        hParams.setMargins(margin, 0, margin, 0)
        trackpadLayout?.addView(hScrollContainer, hParams)
        
        hScrollVisual = View(context)
        hScrollVisual!!.setBackgroundColor(0x30FFFFFF.toInt())
        val hvParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, prefScrollVisualSize)
        hvParams.gravity = Gravity.CENTER_VERTICAL
        hScrollContainer?.addView(hScrollVisual, hvParams) 
    }
    
    private fun updateScrollPosition() { 
        val margin = prefHandleTouchSize + 10
        vScrollContainer?.let { container ->
            val vp = container.layoutParams as FrameLayout.LayoutParams
            vp.gravity = if (prefVPosLeft) Gravity.LEFT else Gravity.RIGHT
            vp.setMargins(0, margin, 0, margin)
            container.layoutParams = vp 
        }
        hScrollContainer?.let { container ->
            val hp = container.layoutParams as FrameLayout.LayoutParams
            hp.gravity = if (prefHPosTop) Gravity.TOP else Gravity.BOTTOM
            hp.setMargins(margin, 0, margin, 0)
            container.layoutParams = hp 
        }
    }
    
    private fun updateBorderColor(strokeColor: Int) { 
        currentBorderColor = strokeColor
        val bg = trackpadLayout?.background as? GradientDrawable ?: return
        bg.setColor(Color.TRANSPARENT)
        val colorWithAlpha = (strokeColor and 0x00FFFFFF) or (prefAlpha shl 24)
        bg.setStroke(4, if (highlightAlpha) 0xFF00FF00.toInt() else colorWithAlpha)
        trackpadLayout?.invalidate() 
    }
}
