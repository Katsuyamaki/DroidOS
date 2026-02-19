package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.SystemClock
import android.view.GestureDetector
import android.view.Gravity
import android.view.InputDevice
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList
import kotlin.math.abs

class OverlayLayoutManager(
    private val service: OverlayService,
    private val context: Context,
    private val windowManager: WindowManager
) {
    // Views
    var trackpadLayout: FrameLayout? = null
    var bubbleView: View? = null
    var cursorLayout: FrameLayout? = null
    var cursorView: ImageView? = null
    var remoteCursorLayout: FrameLayout? = null
    var remoteCursorView: ImageView? = null
    internal var debugTextView: TextView? = null

    // Layout Params
    lateinit var trackpadParams: WindowManager.LayoutParams
    lateinit var bubbleParams: WindowManager.LayoutParams
    lateinit var cursorParams: WindowManager.LayoutParams
    var remoteCursorParams: WindowManager.LayoutParams? = null
    internal var remoteWindowManager: WindowManager? = null

    fun createRemoteCursor(displayId: Int) {
        try {
            removeRemoteCursor()
            val display = service.displayManager?.getDisplay(displayId) ?: return
            val remoteContext = service.createTrackpadDisplayContext(display)
            remoteWindowManager = remoteContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            remoteCursorLayout = FrameLayout(remoteContext)
            remoteCursorView = ImageView(remoteContext)
            remoteCursorView?.setImageResource(R.drawable.ic_cursor)
            val size = if (service.prefs.prefCursorSize > 0) service.prefs.prefCursorSize else 50
            remoteCursorLayout?.addView(remoteCursorView, FrameLayout.LayoutParams(size, size))
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )
            params.gravity = Gravity.TOP or Gravity.LEFT
            val metrics = android.util.DisplayMetrics()
            display.getRealMetrics(metrics)
            params.x = metrics.widthPixels / 2
            params.y = metrics.heightPixels / 2
            remoteCursorParams = params
            remoteWindowManager?.addView(remoteCursorLayout, params)
        } catch (e: Exception) {

        }
    }

    fun removeRemoteCursor() {
        try {
            if (remoteCursorLayout != null && remoteWindowManager != null) {
                remoteWindowManager?.removeView(remoteCursorLayout)
            }
        } catch (e: Exception) {}
        remoteCursorLayout = null
        remoteCursorView = null
        remoteWindowManager = null
        remoteCursorParams = null
    }

    fun updateRemoteCursorPosition(x: Int, y: Int) {
        remoteCursorParams?.let {
            it.x = x
            it.y = y
            try { remoteWindowManager?.updateViewLayout(remoteCursorLayout, it) } catch(e: Exception) {}
        }
    }

    // Touch & UI State
    internal var rotationAngle = 0
    internal var lastTouchX = 0f
    internal var lastTouchY = 0f
    internal var isTouchDragging = false
    private var ignoreTouchSequence = false
    private var isKeyDragging = false
    private var activeDragButton = MotionEvent.BUTTON_PRIMARY
    private var dragDownTime: Long = 0L
    private var hasSentTouchDown = false
    private var hasSentMouseDown = false
    private var activeFingerDeviceId = -1
    private var touchDownTime: Long = 0L
    internal var touchDownX: Float = 0f
    internal var touchDownY: Float = 0f
    internal var initialTouchX: Float = 0f
    internal var initialTouchY: Float = 0f
    private var isReleaseDebouncing = false
    private val releaseDebounceRunnable = Runnable { isReleaseDebouncing = false }
    internal var currentBorderColor = 0xFFFFFFFF.toInt()
    internal var cursorFadeRunnable: Runnable? = null
    internal val CURSOR_FADE_TIMEOUT = 5000L

    // Handles & Scrollbars
    internal var scrollZoneThickness = 80
    internal val handleContainers = ArrayList<FrameLayout>()
    internal val handleVisuals = ArrayList<View>()
    internal var vScrollContainer: FrameLayout? = null
    internal var hScrollContainer: FrameLayout? = null
    internal var vScrollVisual: View? = null
    internal var hScrollVisual: View? = null
    private var isVScrolling = false
    private var isHScrolling = false
    private var scrollAccumulatorX = 0f
    private var scrollAccumulatorY = 0f

    private val longPressRunnable = Runnable { startTouchDrag() }

    private val TAP_TIMEOUT_MS = 300L
    private val TAP_SLOP_PX = 15f
    private val RELEASE_DEBOUNCE_MS = 50L

    fun setupAll() {
        try { setupTrackpad(context) } catch (e: Exception) {}
        try { setupBubble(context) } catch (e: Exception) {}
        try { setupCursor(context) } catch (e: Exception) {}
    }

    fun removeOldViews() {
        val viewsToRemove = listOf(trackpadLayout, bubbleView, cursorLayout)
        for (view in viewsToRemove) {
            if (view != null && view.isAttachedToWindow) {
                try {
                    windowManager.removeViewImmediate(view)
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
        trackpadLayout = null
        bubbleView = null
        cursorLayout = null
        cursorView = null
        removeRemoteCursor()
    }

    fun enforceZOrder() {
        try {
            if (trackpadLayout != null && trackpadLayout?.isAttachedToWindow == true) {
                windowManager.removeView(trackpadLayout)
                windowManager.addView(trackpadLayout, trackpadParams)
            }

            service.keyboardOverlay?.bringToFront()
            service.menuManager?.bringToFront()

            if (bubbleView != null && bubbleView?.isAttachedToWindow == true) {
                windowManager.removeView(bubbleView)
                windowManager.addView(bubbleView, bubbleParams)
            }

            if (cursorLayout != null && cursorLayout?.isAttachedToWindow == true) {
                windowManager.removeView(cursorLayout)
                windowManager.addView(cursorLayout, cursorParams)
            }
            
            service.btMouseManager?.bringToFront()
        } catch (e: Exception) {
            // ignore
        }
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

        trackpadParams.gravity = Gravity.TOP or Gravity.LEFT; service.loadLayout()
        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean { return false }
        })

        trackpadLayout?.setOnTouchListener { _, event ->
            val devId = event.deviceId
            val tool = event.getToolType(0)
            
            // Only accept finger touches (not mouse, stylus, etc.)
            if (tool != MotionEvent.TOOL_TYPE_FINGER) return@setOnTouchListener false
            
            // Track active finger to prevent ghost touches
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> activeFingerDeviceId = devId
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                    if (activeFingerDeviceId > 0 && devId != activeFingerDeviceId) {
                        return@setOnTouchListener false
                    }
                }
            }
            
            // Check for spacebar mouse extended mode exit (on tap outside keyboard)
            service.handleSpacebarExtendedModeCheck(event.rawX, event.rawY, event.action)
            
            // Pass to gesture detector for tap detection
            gestureDetector.onTouchEvent(event)
            
            // CRITICAL: Handle trackpad touch for cursor movement
            try {
                handleTrackpadTouch(event)
            } catch (e: Exception) {
                android.util.Log.e("TrackpadDebug", "handleTrackpadTouch CRASHED", e)
            }
            
            true
        }
        trackpadLayout?.visibility = if (service.isTrackpadVisible) View.VISIBLE else View.GONE
        windowManager.addView(trackpadLayout, trackpadParams)
        updateBorderColor(currentBorderColor)
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
        if (service.prefs.prefBubbleX == -1 || service.prefs.prefBubbleY == -1) {
            service.prefs.prefBubbleX = (service.uiScreenWidth / 2) + 80
            service.prefs.prefBubbleY = service.uiScreenHeight / 2
        }
        bubbleParams.x = service.prefs.prefBubbleX.coerceIn(0, (service.uiScreenWidth - 60).coerceAtLeast(0))
        bubbleParams.y = service.prefs.prefBubbleY.coerceIn(0, (service.uiScreenHeight - 60).coerceAtLeast(0))

        var initialX = 0; var initialY = 0; var initialTouchX = 0f; var initialTouchY = 0f; var isDrag = false
        var isLongPressHandled = false
        var velocityTracker: VelocityTracker? = null
        val bubbleLongPressRunnable = Runnable {
            if (!isDrag) {
                service.vibrate()
                service.menuManager?.toggle()
                isLongPressHandled = true
                service.handler.post { enforceZOrder() }
            }
        }

        bubbleView?.setOnTouchListener { _, event ->
            if (velocityTracker == null) velocityTracker = VelocityTracker.obtain()
            velocityTracker?.addMovement(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = bubbleParams.x; initialY = bubbleParams.y
                    initialTouchX = event.rawX; initialTouchY = event.rawY
                    isDrag = false
                    isLongPressHandled = false
                    service.handler.postDelayed(bubbleLongPressRunnable, 600)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (abs(event.rawX - initialTouchX) > 10 || abs(event.rawY - initialTouchY) > 10) {
                        isDrag = true
                        service.handler.removeCallbacks(bubbleLongPressRunnable)
                        bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(bubbleView, bubbleParams)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    service.handler.removeCallbacks(bubbleLongPressRunnable)
                    velocityTracker?.computeCurrentVelocity(1000)
                    val vX = velocityTracker?.xVelocity ?: 0f
                    val vY = velocityTracker?.yVelocity ?: 0f
                    val totalVel = kotlin.math.hypot(vX.toDouble(), vY.toDouble())

                    // [SAFETY] FLING RESET: Harder threshold (4000) to prevent accidental flings
                    if (isDrag && totalVel > 4000) {

                        service.showToast("Force Closing Trackpad...")

                        // 1. Force Screen ON & Unblock Keyboard
                        service.isScreenOff = false
                        Thread {
                            try { service.shellService?.setBrightness(128) } catch (e: Exception) {}
                            try { service.shellService?.setScreenOff(0, false) } catch (e: Exception) {}
                        }.start()
                        service.imeManager?.setSoftKeyboardBlocking(false)
                        if (Build.VERSION.SDK_INT >= 24) {
                            try { service.softKeyboardController.showMode = android.accessibilityservice.AccessibilityService.SHOW_MODE_AUTO } catch(e: Exception){}
                        }

                        // 2. Kill Process
                        service.forceExit() // This handles cleanup and kill

                        velocityTracker?.recycle()
                        velocityTracker = null
                        return@setOnTouchListener true
                    }

                    if (!isDrag && !isLongPressHandled) {
                        handleBubbleTap()
                    } else if (isDrag) {
                        service.prefs.prefBubbleX = bubbleParams.x
                        service.prefs.prefBubbleY = bubbleParams.y
                        service.prefs.save(service)
                    }
                    velocityTracker?.recycle()
                    velocityTracker = null
                    service.handler.post { enforceZOrder() }
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    service.handler.removeCallbacks(bubbleLongPressRunnable)
                    velocityTracker?.recycle()
                    velocityTracker = null
                    false
                }
                else -> false
            }
        }
        windowManager.addView(bubbleView, bubbleParams); service.updateBubbleStatus()
        applyBubbleAppearance()
    }
    
    private fun setupCursor(context: Context) {
        cursorLayout = FrameLayout(context); cursorView = ImageView(context); cursorView?.setImageResource(R.drawable.ic_cursor); val size = if (service.prefs.prefCursorSize > 0) service.prefs.prefCursorSize else 50; cursorLayout?.addView(cursorView, FrameLayout.LayoutParams(size, size))
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
        cursorParams.gravity = Gravity.TOP or Gravity.LEFT; cursorParams.x = service.uiScreenWidth / 2; cursorParams.y = service.uiScreenHeight / 2; windowManager.addView(cursorLayout, cursorParams)
    }

    fun setTrackpadVisibility(visible: Boolean) {
        trackpadLayout?.visibility = if (visible) View.VISIBLE else View.GONE
        if (visible) updateBorderColor(currentBorderColor)
    }

    fun updateBubbleStatus() {
        val dot = bubbleView?.findViewById<ImageView>(R.id.status_dot)
        if (service.shellService != null) dot?.visibility = View.GONE else dot?.visibility = View.VISIBLE
    }

    fun handleBubbleTap() {
        val anythingBubbleVisible = (service.prefs.prefBubbleIncludeTrackpad && service.isTrackpadVisible) || (service.prefs.prefBubbleIncludeKeyboard && service.isCustomKeyboardVisible)
        if (anythingBubbleVisible) {
            service.performSmartHide()
        } else if (service.prefs.prefBubbleIncludeTrackpad || service.prefs.prefBubbleIncludeKeyboard) {
            service.performSmartRestore()
        }
    }

    private fun handleTrackpadTouch(event: MotionEvent) {

        val viewWidth = trackpadLayout?.width ?: 0; val viewHeight = trackpadLayout?.height ?: 0; if (viewWidth == 0 || viewHeight == 0) return
        if (isReleaseDebouncing && event.actionMasked != MotionEvent.ACTION_DOWN) return
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // NEW: dismiss Voice if active
                // service.checkAndDismissVoice() // This is private

                service.handler.removeCallbacks(releaseDebounceRunnable); isReleaseDebouncing = false
                touchDownTime = SystemClock.uptimeMillis(); touchDownX = event.x; touchDownY = event.y; lastTouchX = event.x; lastTouchY = event.y; isTouchDragging = false
                val actualZoneV = minOf(scrollZoneThickness, (viewWidth * 0.15f).toInt()); val actualZoneH = minOf(scrollZoneThickness, (viewHeight * 0.15f).toInt())
                if ((service.prefs.prefVPosLeft && event.x < actualZoneV) || (!service.prefs.prefVPosLeft && event.x > viewWidth - actualZoneV) || (service.prefs.prefHPosTop && event.y < actualZoneH) || (!service.prefs.prefHPosTop && event.y > viewHeight - actualZoneH)) { ignoreTouchSequence = true; return }
                service.handler.postDelayed(longPressRunnable, 400)
            }
            MotionEvent.ACTION_MOVE -> {
                if (ignoreTouchSequence) return
                // VIRTUAL DISPLAY KEEP-ALIVE: Ping user activity during active touch on remote display
                if (service.inputTargetDisplayId != service.currentDisplayId) service.pingUserActivity()
                if (kotlin.math.hypot((event.x - touchDownX).toDouble(), (event.y - touchDownY).toDouble()) > TAP_SLOP_PX) service.handler.removeCallbacks(longPressRunnable)
                val dx = (event.x - lastTouchX) * service.prefs.cursorSpeed; val dy = (event.y - lastTouchY) * service.prefs.cursorSpeed
                val safeW = if (service.inputTargetDisplayId != service.currentDisplayId) service.targetScreenWidth.toFloat() else service.uiScreenWidth.toFloat()
                val safeH = if (service.inputTargetDisplayId != service.currentDisplayId) service.targetScreenHeight.toFloat() else service.uiScreenHeight.toFloat()
                var fDx = dx; var fDy = dy
                when (rotationAngle) { 90 -> { fDx = -dy; fDy = dx }; 180 -> { fDx = -dx; fDy = -dy }; 270 -> { fDx = dy; fDy = -dx } }
                service.cursorX = (service.cursorX + fDx).coerceIn(0f, safeW); service.cursorY = (service.cursorY + fDy).coerceIn(0f, safeH)
                if (service.inputTargetDisplayId == service.currentDisplayId) { cursorParams.x = service.cursorX.toInt(); cursorParams.y = service.cursorY.toInt(); try { windowManager.updateViewLayout(cursorLayout, cursorParams) } catch(e: Exception) {} } 
                else { updateRemoteCursorPosition(service.cursorX.toInt(), service.cursorY.toInt()) }
                showCursorAndResetFade()
                if (isTouchDragging || isKeyDragging) service.inputHandler.injectMouse(MotionEvent.ACTION_MOVE, service.cursorX, service.cursorY, service.inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, SystemClock.uptimeMillis()) else service.inputHandler.injectMouse(MotionEvent.ACTION_MOVE, service.cursorX, service.cursorY, service.inputTargetDisplayId, InputDevice.SOURCE_MOUSE, 0, SystemClock.uptimeMillis())
                lastTouchX = event.x; lastTouchY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                service.handler.removeCallbacks(longPressRunnable)
                if (!ignoreTouchSequence) {
                    if (isTouchDragging) { service.inputHandler.injectMouse(MotionEvent.ACTION_UP, service.cursorX, service.cursorY, service.inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, SystemClock.uptimeMillis()); isTouchDragging = false }
                    else if (!isKeyDragging && SystemClock.uptimeMillis() - touchDownTime < TAP_TIMEOUT_MS && kotlin.math.hypot((event.x - touchDownX).toDouble(), (event.y - touchDownY).toDouble()) < TAP_SLOP_PX) service.inputHandler.performClick(service.cursorX, service.cursorY, service.inputTargetDisplayId, false)
                }
                isReleaseDebouncing = true; service.handler.postDelayed(releaseDebounceRunnable, RELEASE_DEBOUNCE_MS)
                if (!isKeyDragging) { isVScrolling = false; isHScrolling = false; updateBorderColor(currentBorderColor) }
                ignoreTouchSequence = false
            }
        }
    }
    
    private fun startTouchDrag() { if (ignoreTouchSequence || isTouchDragging) return; isTouchDragging = true; activeDragButton = MotionEvent.BUTTON_PRIMARY; dragDownTime = SystemClock.uptimeMillis(); service.inputHandler.injectMouse(MotionEvent.ACTION_DOWN, service.cursorX, service.cursorY, service.inputTargetDisplayId, InputDevice.SOURCE_TOUCHSCREEN, activeDragButton, dragDownTime); hasSentTouchDown = true; if (service.prefs.prefVibrate) service.vibrate(); updateBorderColor(0xFFFF9900.toInt()) }

    private fun addHandle(context: Context, gravity: Int, color: Int, onTouch: (View, MotionEvent) -> Boolean) { val container = FrameLayout(context); val p = FrameLayout.LayoutParams(service.prefs.prefHandleTouchSize, service.prefs.prefHandleTouchSize); p.gravity = gravity; val visual = View(context); val bg = GradientDrawable(); bg.setColor(color); bg.cornerRadius = 15f; visual.background = bg; val vp = FrameLayout.LayoutParams(service.prefs.prefHandleSize, service.prefs.prefHandleSize); vp.gravity = Gravity.CENTER; container.addView(visual, vp); handleContainers.add(container); handleVisuals.add(visual); trackpadLayout?.addView(container, p); container.setOnTouchListener { v, e -> onTouch(v, e) } }

    private fun moveWindow(event: MotionEvent): Boolean { if (service.prefs.prefAnchored) return true; if (event.action == MotionEvent.ACTION_MOVE) { trackpadParams.x += (event.rawX - lastTouchX).toInt(); trackpadParams.y += (event.rawY - lastTouchY).toInt(); windowManager.updateViewLayout(trackpadLayout, trackpadParams) }; lastTouchX = event.rawX; lastTouchY = event.rawY; return true }
    
    private fun resizeWindow(event: MotionEvent): Boolean { if (service.prefs.prefAnchored) return true; if (event.action == MotionEvent.ACTION_MOVE) { trackpadParams.width += (event.rawX - lastTouchX).toInt(); trackpadParams.height += (event.rawY - lastTouchY).toInt(); windowManager.updateViewLayout(trackpadLayout, trackpadParams) }; lastTouchX = event.rawX; lastTouchY = event.rawY; return true }
    
    private fun keyboardHandle(event: MotionEvent): Boolean { 
        if (event.action == MotionEvent.ACTION_UP) {
            service.toggleCustomKeyboard()
        } 
        return true 
    }

    private fun openMenuHandle(event: MotionEvent): Boolean { if (event.action == MotionEvent.ACTION_DOWN) service.menuManager?.toggle(); return true }
    
    private fun addScrollBars(context: Context) {
        val margin = service.prefs.prefHandleTouchSize + 10
        vScrollContainer = FrameLayout(context)
        val vp = FrameLayout.LayoutParams(service.prefs.prefScrollTouchSize, FrameLayout.LayoutParams.MATCH_PARENT); vp.gravity = if (service.prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; vp.setMargins(0, margin, 0, margin)
        trackpadLayout?.addView(vScrollContainer, vp)
        vScrollVisual = View(context); vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())
        vScrollContainer?.addView(vScrollVisual, FrameLayout.LayoutParams(service.prefs.prefScrollVisualSize, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER))
        vScrollContainer?.setOnTouchListener { _, event -> handleVScrollTouch(event); true }
        
        hScrollContainer = FrameLayout(context)
        val hp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, service.prefs.prefScrollTouchSize); hp.gravity = if (service.prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM; hp.setMargins(margin, 0, margin, 0)
        trackpadLayout?.addView(hScrollContainer, hp)
        hScrollVisual = View(context); hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt())
        hScrollContainer?.addView(hScrollVisual, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, service.prefs.prefScrollVisualSize, Gravity.CENTER))
        hScrollContainer?.setOnTouchListener { _, event -> handleHScrollTouch(event); true }
    }
    
    private fun handleVScrollTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { isVScrolling = true; lastTouchY = event.y; scrollAccumulatorY = 0f; vScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt()); if (service.prefs.prefTapScroll) { val h = vScrollContainer?.height ?: return; val dist = 200f * service.prefs.scrollSpeed; service.inputHandler.performSwipe(service.cursorX, service.cursorY, 0f, if (event.y < h/2) (if (service.prefs.prefReverseScroll) -dist else dist) else (if (service.prefs.prefReverseScroll) dist else -dist), service.inputTargetDisplayId) } }
            MotionEvent.ACTION_MOVE -> { if (isVScrolling && !service.prefs.prefTapScroll) { val dy = event.y - lastTouchY; scrollAccumulatorY += dy * service.prefs.scrollSpeed; if (abs(scrollAccumulatorY) > 30f) { service.inputHandler.performSwipe(service.cursorX, service.cursorY, 0f, if (service.prefs.prefReverseScroll) -scrollAccumulatorY * 2 else scrollAccumulatorY * 2, service.inputTargetDisplayId); scrollAccumulatorY = 0f }; lastTouchY = event.y } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isVScrolling = false; scrollAccumulatorY = 0f; vScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt()) }
        }
    }
    
    private fun handleHScrollTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { isHScrolling = true; lastTouchX = event.x; scrollAccumulatorX = 0f; hScrollVisual?.setBackgroundColor(0x80FFFFFF.toInt()); if (service.prefs.prefTapScroll) { val w = hScrollContainer?.width ?: return; val dist = 200f * service.prefs.scrollSpeed; service.inputHandler.performSwipe(service.cursorX, service.cursorY, if (event.x < w/2) (if (service.prefs.prefReverseScroll) -dist else dist) else (if (service.prefs.prefReverseScroll) dist else -dist), 0f, service.inputTargetDisplayId) } }
            MotionEvent.ACTION_MOVE -> { if (isHScrolling && !service.prefs.prefTapScroll) { val dx = event.x - lastTouchX; scrollAccumulatorX += dx * service.prefs.scrollSpeed; if (abs(scrollAccumulatorX) > 30f) { service.inputHandler.performSwipe(service.cursorX, service.cursorY, if (service.prefs.prefReverseScroll) -scrollAccumulatorX * 2 else scrollAccumulatorX * 2, 0f, service.inputTargetDisplayId); scrollAccumulatorX = 0f }; lastTouchX = event.x } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isHScrolling = false; scrollAccumulatorX = 0f; hScrollVisual?.setBackgroundColor(0x30FFFFFF.toInt()) }
        }
    }

    fun updateBorderColor(strokeColor: Int) { currentBorderColor = strokeColor; val bg = trackpadLayout?.background as? GradientDrawable ?: return; bg.setColor((service.prefs.prefBgAlpha shl 24) or 0x000000); bg.setStroke(4, (service.prefs.prefAlpha shl 24) or 0xFFFFFF); trackpadLayout?.invalidate() }

    fun applyBubbleAppearance() {
        if (bubbleView == null) return
        val scale = service.prefs.prefBubbleSize / 100f
        val density = service.resources.displayMetrics.density
        bubbleParams.width = (60 * scale * density).toInt()
        bubbleParams.height = (60 * scale * density).toInt()
        try { windowManager.updateViewLayout(bubbleView, bubbleParams) } catch (e: Exception) {}
        val iconView = bubbleView?.findViewById<ImageView>(R.id.bubble_icon)
        iconView?.let {
            val iconParams = it.layoutParams as? FrameLayout.LayoutParams
            iconParams?.gravity = Gravity.CENTER
            iconParams?.width = (40 * scale * density).toInt()
            iconParams?.height = (40 * scale * density).toInt()
            it.layoutParams = iconParams
            it.setImageResource(service.bubbleIcons.getOrElse(service.prefs.prefBubbleIconIndex) { service.bubbleIcons[0] })
            it.alpha = service.prefs.prefBubbleAlpha / 255f
        }
        bubbleView?.alpha = service.prefs.prefBubbleAlpha / 255f
    }

    fun updateBubbleSize(sizePercent: Int) { service.prefs.prefBubbleSize = sizePercent.coerceIn(50, 200); applyBubbleAppearance(); service.prefs.save(service) }
    fun updateBubbleIcon(index: Int) { service.prefs.prefBubbleIconIndex = index.coerceIn(0, service.bubbleIcons.size - 1); applyBubbleAppearance(); service.prefs.save(service) }
    fun cycleBubbleIcon() { updateBubbleIcon((service.prefs.prefBubbleIconIndex + 1) % service.bubbleIcons.size) }
    fun updateBubbleAlpha(alpha: Int) { service.prefs.prefBubbleAlpha = alpha.coerceIn(50, 255); applyBubbleAppearance(); service.prefs.save(service) }

    fun updateCursorSize() { val size = if (service.prefs.prefCursorSize > 0) service.prefs.prefCursorSize else 50; cursorView?.layoutParams?.let { it.width = size; it.height = size; cursorView?.layoutParams = it } }
    
    internal fun updateScrollPosition() { val margin = service.prefs.prefHandleTouchSize + 10; vScrollContainer?.let { c -> val p = c.layoutParams as FrameLayout.LayoutParams; p.gravity = if (service.prefs.prefVPosLeft) Gravity.LEFT else Gravity.RIGHT; p.setMargins(0, margin, 0, margin); c.layoutParams = p }; hScrollContainer?.let { c -> val p = c.layoutParams as FrameLayout.LayoutParams; p.gravity = if (service.prefs.prefHPosTop) Gravity.TOP else Gravity.BOTTOM; p.setMargins(margin, 0, margin, 0); c.layoutParams = p } }
    internal fun updateHandleSize() { for (v in handleVisuals) { val p = v.layoutParams; p.width = service.prefs.prefHandleSize; p.height = service.prefs.prefHandleSize; v.layoutParams = p } }
    private fun updateScrollSize() {
        service.prefs.prefScrollTouchSize = service.prefs.prefScrollTouchSize.coerceIn(40, 180); service.prefs.prefScrollVisualSize = service.prefs.prefScrollVisualSize.coerceIn(4, 20); scrollZoneThickness = service.prefs.prefScrollTouchSize
        vScrollContainer?.let { it.layoutParams.width = service.prefs.prefScrollTouchSize; it.requestLayout() }; vScrollVisual?.let { it.layoutParams.width = service.prefs.prefScrollVisualSize; it.requestLayout() }
        hScrollContainer?.let { it.layoutParams.height = service.prefs.prefScrollTouchSize; it.requestLayout() }; hScrollVisual?.let { it.layoutParams.height = service.prefs.prefScrollVisualSize; it.requestLayout() }
    }
    private fun updateLayoutSizes() { for (c in handleContainers) { val p = c.layoutParams; p.width = service.prefs.prefHandleTouchSize; p.height = service.prefs.prefHandleTouchSize; c.layoutParams = p } }

    private fun showCursorAndResetFade() {
        // Make cursor visible
        val target = if (service.inputTargetDisplayId != service.currentDisplayId) remoteCursorView else cursorView
        target?.alpha = 1f
        target?.visibility = View.VISIBLE
        // Cancel existing fade timer and start new one
        cursorFadeRunnable?.let { service.handler.removeCallbacks(it) }
        cursorFadeRunnable = Runnable {
            target?.animate()?.alpha(0f)?.setDuration(500)?.start()
        }
        service.handler.postDelayed(cursorFadeRunnable!!, CURSOR_FADE_TIMEOUT)
    }
}