package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout

/**
 * MirrorModeManager - Manages Virtual Mirror Mode functionality
 *
 * This class handles the projection of a secondary keyboard onto a remote display
 * and manages complex touch gestures (Orange/Blue trails) to map physical touches
 * to that remote display.
 */
class MirrorModeManager(
    private val service: OverlayService,
    private val displayManager: DisplayManager?
) {
    private val TAG = "MirrorModeManager"

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
    private var isHoveringBackspace = false
    private var draggedPredictionIndex = -1
    private var lastOrientX = 0f
    private var lastOrientY = 0f
    private val MOVEMENT_THRESHOLD = 15f

    private val orientationModeHandler = Handler(Looper.getMainLooper())
    private var lastOrientationTouchTime = 0L

    // =================================================================================
    // MIRROR MODE KEY REPEAT VARIABLES
    // =================================================================================
    private val mirrorRepeatHandler = Handler(Looper.getMainLooper())
    private var mirrorRepeatKey: String? = null
    private var isMirrorRepeating = false
    private val MIRROR_REPEAT_INITIAL_DELAY = 400L
    private val MIRROR_REPEAT_INTERVAL = 50L

    private val mirrorRepeatableKeys = setOf("BKSP", "⌫", "←", "→", "↑", "↓", "◄", "▲", "▼", "►")

    private val mirrorRepeatRunnable = object : Runnable {
        override fun run() {
            mirrorRepeatKey?.let { key ->
                if (isMirrorRepeating) {

                    service.keyboardOverlay?.triggerKeyPress(key)
                    mirrorRepeatHandler.postDelayed(this, MIRROR_REPEAT_INTERVAL)
                }
            }
        }
    }

    // =================================================================================
    // ORIENTATION MODE TIMEOUT RUNNABLE
    // =================================================================================
    private val orientationModeTimeout = Runnable {
        val stripHeight = service.keyboardOverlay?.getKeyboardView()?.getSuggestionStripHeight()?.toFloat() ?: 0f

        if (lastOrientY < stripHeight) {
            // START DRAG MODE (Blue Trail)
            isInOrientationMode = false
            service.keyboardOverlay?.setOrientationMode(false)
            mirrorTrailView?.clear()
            service.keyboardOverlay?.clearOrientationTrail()

            isMirrorDragActive = true

            val width = if (physicalKbWidth > 0) physicalKbWidth else 1080f
            val slotWidth = width / 3f
            draggedPredictionIndex = (lastOrientX / slotWidth).toInt().coerceIn(0, 2)

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
        service.keyboardOverlay?.clearOrientationTrail()
        service.keyboardOverlay?.setOrientationMode(false)
        mirrorTrailView?.setTrailColor(0xFF4488FF.toInt())
        service.keyboardOverlay?.startSwipeFromCurrentPosition(lastOrientX, lastOrientY)

        val scaleX = if (physicalKbWidth > 0) mirrorKbWidth / physicalKbWidth else 1f
        val scaleY = if (physicalKbHeight > 0) mirrorKbHeight / physicalKbHeight else 1f
        mirrorTrailView?.addPoint(lastOrientX * scaleX, lastOrientY * scaleY)
        mirrorKeyboardView?.alpha = 0.7f

        val currentKey = service.keyboardOverlay?.getKeyAtPosition(lastOrientX, lastOrientY)
        if (currentKey != null && mirrorRepeatableKeys.contains(currentKey)) {
            mirrorRepeatKey = currentKey
            isMirrorRepeating = true
            service.keyboardOverlay?.triggerKeyPress(currentKey)
            mirrorRepeatHandler.postDelayed(mirrorRepeatRunnable, MIRROR_REPEAT_INTERVAL)
        }
    }

    // =================================================================================
    // MIRROR FADE OUT HANDLER
    // =================================================================================
    private val mirrorFadeHandler = Handler(Looper.getMainLooper())
    private val mirrorFadeRunnable = Runnable {
        mirrorKeyboardView?.animate()?.alpha(0f)?.setDuration(300)?.start()
        mirrorKeyboardContainer?.animate()?.alpha(0f)?.setDuration(300)?.start()
    }

    // =================================================================================
    // PUBLIC API
    // =================================================================================

    /**
     * Toggles Virtual Mirror Mode on/off.
     */
    fun toggle() {
        service.saveCurrentState()

        val wasEnabled = service.prefs.prefVirtualMirrorMode
        service.prefs.prefVirtualMirrorMode = !wasEnabled

        if (service.prefs.prefVirtualMirrorMode) {
            enterMirrorMode()
        } else {
            exitMirrorMode()
        }

        service.prefs.save(service)
    }

    /**
     * Updates mirror mode state based on current preferences.
     */
    fun update() {
        if (service.prefs.prefVirtualMirrorMode && service.inputTargetDisplayId != service.currentDisplayId) {
            createMirrorKeyboard(service.inputTargetDisplayId)
        } else {
            removeMirrorKeyboard()
        }
    }

    /**
     * Adjusts mirror keyboard position or size.
     */
    fun adjust(isResize: Boolean, deltaX: Int, deltaY: Int) {
        service.showToast("adjust: resize=$isResize dX=$deltaX dY=$deltaY")

        if (mirrorKeyboardParams == null || mirrorKeyboardContainer == null) {
            service.showToast("ERROR: params null")
            return
        }

        showMirrorTemporarily()

        if (isResize) {
            handleResize(deltaX, deltaY)
        } else {
            handleMove(deltaX, deltaY)
        }

        try {
            mirrorWindowManager?.updateViewLayout(mirrorKeyboardContainer, mirrorKeyboardParams)

            if (isResize) {
                service.handler.postDelayed({ updateMirrorSyncDimensions() }, 100)
            }
        } catch (e: Exception) {
            service.showToast("Layout update failed: ${e.message}")
        }
    }

    /**
     * Resets mirror keyboard to default centered position.
     */
    fun resetPosition() {
        if (mirrorKeyboardParams == null || mirrorKeyboardContainer == null) return

        showMirrorTemporarily()

        val display = displayManager?.getDisplay(service.inputTargetDisplayId) ?: return
        val metrics = android.util.DisplayMetrics()
        display.getRealMetrics(metrics)

        val mirrorWidth = (metrics.widthPixels * 0.95f).toInt()

        mirrorKeyboardParams?.x = 0
        mirrorKeyboardParams?.y = 0
        mirrorKeyboardParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        mirrorKeyboardParams?.width = mirrorWidth
        mirrorKeyboardParams?.height = WindowManager.LayoutParams.WRAP_CONTENT

        service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
            .remove("mirror_x")
            .remove("mirror_y")
            .remove("mirror_width")
            .remove("mirror_height")
            .apply()

        try {
            mirrorWindowManager?.updateViewLayout(mirrorKeyboardContainer, mirrorKeyboardParams)
            service.handler.postDelayed({ updateMirrorSyncDimensions() }, 100)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reset mirror keyboard layout", e)
        }


    }

    /**
     * Handles touch events for mirror mode.
     * @return true to block input, false to allow input
     */
    fun onTouch(x: Float, y: Float, action: Int): Boolean {
        if (!isActive()) return false

        val scaleX = if (physicalKbWidth > 0) mirrorKbWidth / physicalKbWidth else 1f
        val scaleY = if (physicalKbHeight > 0) mirrorKbHeight / physicalKbHeight else 1f
        val mirrorX = x * scaleX
        val mirrorY = y * scaleY

        when (action) {
            MotionEvent.ACTION_DOWN -> return handleTouchDown(x, y, mirrorX, mirrorY)
            MotionEvent.ACTION_MOVE -> return handleTouchMove(x, y, mirrorX, mirrorY)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> return handleTouchUp(x, y, mirrorX, mirrorY, action)
        }
        return isInOrientationMode
    }

    /**
     * Syncs mirror keyboard with physical keyboard dimensions.
     */
    fun syncWithPhysicalKeyboard() {
        if (!service.prefs.prefVirtualMirrorMode || mirrorKeyboardView == null) return

        physicalKbWidth = service.keyboardOverlay?.getKeyboardView()?.width?.toFloat() ?: physicalKbWidth
        physicalKbHeight = service.keyboardOverlay?.getKeyboardView()?.height?.toFloat() ?: physicalKbHeight


    }

    /**
     * Syncs keyboard layer (state) from physical to mirror keyboard.
     */
    fun syncKeyboardLayer(state: KeyboardView.KeyboardState) {
        mirrorKeyboardView?.setKeyboardState(state)

        service.keyboardOverlay?.getCtrlAltState()?.let { (ctrl, alt) ->
            mirrorKeyboardView?.setCtrlAltState(ctrl, alt)
        }
    }

    /**
     * Sets suggestions on the mirror keyboard.
     */
    fun setSuggestions(suggestions: List<KeyboardView.Candidate>) {
        mirrorKeyboardView?.setSuggestions(suggestions)
    }

    /**
     * Returns true if Virtual Mirror Mode is currently active.
     */
    fun isActive(): Boolean {
        return service.prefs.prefVirtualMirrorMode &&
               service.inputTargetDisplayId != service.currentDisplayId &&
               mirrorKeyboardView != null
    }

    /**
     * Returns true if currently in orientation mode.
     */
    fun isInOrientationMode(): Boolean {
        return isInOrientationMode
    }

    /**
     * Applies saved mirror keyboard settings.
     */
    fun applySettings() {
        if (mirrorKeyboardParams == null || mirrorKeyboardContainer == null) return

        if (service.prefs.prefMirrorX != -1) {
            mirrorKeyboardParams?.x = service.prefs.prefMirrorX
            mirrorKeyboardParams?.gravity = Gravity.TOP or Gravity.START
        }
        if (service.prefs.prefMirrorY != 0 || service.prefs.prefMirrorX != -1) {
            mirrorKeyboardParams?.y = service.prefs.prefMirrorY
        }
        if (service.prefs.prefMirrorWidth != -1 && service.prefs.prefMirrorWidth > 0) {
            mirrorKeyboardParams?.width = service.prefs.prefMirrorWidth
        }
        if (service.prefs.prefMirrorHeight != -1 && service.prefs.prefMirrorHeight > 0) {
            mirrorKeyboardParams?.height = service.prefs.prefMirrorHeight
        }

        val alpha = service.prefs.prefMirrorAlpha / 255f
        mirrorKeyboardContainer?.alpha = alpha

        try {
            mirrorWindowManager?.updateViewLayout(mirrorKeyboardContainer, mirrorKeyboardParams)
            service.handler.post { updateMirrorSyncDimensions() }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply mirror keyboard settings", e)
        }


    }

    /**
     * Removes the mirror keyboard overlay.
     */
    fun removeMirrorKeyboard() {
        try {
            if (mirrorKeyboardContainer != null && mirrorWindowManager != null) {
                mirrorWindowManager?.removeView(mirrorKeyboardContainer)
            }
        } catch (e: Exception) {

        }

        mirrorKeyboardContainer = null
        mirrorKeyboardView = null
        mirrorTrailView = null
        mirrorKeyboardParams = null
        mirrorWindowManager = null

        orientationModeHandler.removeCallbacks(orientationModeTimeout)
        mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)
        isInOrientationMode = false
    }

    /**
     * Sets mirror keyboard alpha/container alpha.
     */
    fun setAlpha(alpha: Float) {
        mirrorKeyboardContainer?.alpha = alpha
        mirrorKeyboardView?.alpha = alpha
    }

    // =================================================================================
    // PRIVATE METHODS
    // =================================================================================

    private fun enterMirrorMode() {


        service.storePreMirrorState()

        val displays = displayManager?.displays ?: emptyArray()
        var targetDisplay: Display? = null
        for (d in displays) {
            if (d.displayId != service.currentDisplayId && d.displayId >= 2) {
                targetDisplay = d
                break
            }
        }
        if (targetDisplay == null) {
            for (d in displays) {
                if (d.displayId != service.currentDisplayId) {
                    targetDisplay = d
                    break
                }
            }
        }

        if (targetDisplay != null) {
            service.switchToRemoteDisplay(targetDisplay.displayId)

            if (!service.isTrackpadVisible) service.toggleTrackpad()
            if (!service.isCustomKeyboardVisible) service.toggleCustomKeyboard(suppressAutomation = true)

            service.loadLayout()
            update()

            service.showToast("Mirror Mode ON")

            service.btMouseManager?.createBtMouseCaptureOverlay()

            val intentCycle = Intent("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
            intentCycle.setPackage("com.katsuyamaki.DroidOSLauncher")
            service.sendBroadcast(intentCycle)
        } else {
            service.prefs.prefVirtualMirrorMode = false
            service.showToast("No virtual display found.")
        }
    }

    private fun exitMirrorMode() {


        removeMirrorKeyboard()
        service.btMouseManager?.removeBtMouseCaptureOverlay()
        service.switchToLocalDisplay()

        service.loadLayout()
        service.restorePreMirrorVisibility()

        service.showToast("Mirror Mode OFF")

        val intentCycle = Intent("com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY")
        intentCycle.setPackage("com.katsuyamaki.DroidOSLauncher")
        service.sendBroadcast(intentCycle)
    }

    private fun createMirrorKeyboard(displayId: Int) {
        try {
            removeMirrorKeyboard()

            val display = displayManager?.getDisplay(displayId) ?: return
            val mirrorContext = service.createTrackpadDisplayContext(display)

            mirrorWindowManager = mirrorContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            mirrorKeyboardContainer = FrameLayout(mirrorContext)
            mirrorKeyboardContainer?.setBackgroundColor(Color.TRANSPARENT)
            mirrorKeyboardContainer?.alpha = 0f

            mirrorKeyboardView = KeyboardView(mirrorContext, null, 0)
            mirrorKeyboardView?.setMirrorMode(true)
            mirrorKeyboardView?.alpha = 0f

            val scale = service.prefs.prefKeyScale / 100f
            mirrorKeyboardView?.setScale(scale)

            mirrorTrailView = SwipeTrailView(mirrorContext)
            mirrorTrailView?.setTrailColor(0xFFFF9900.toInt())

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

            val metrics = android.util.DisplayMetrics()
            display.getRealMetrics(metrics)

            val savedWidth = service.prefs.prefMirrorWidth
            val mirrorWidth = if (savedWidth != -1 && savedWidth > 0) savedWidth else (metrics.widthPixels * 0.95f).toInt()

            mirrorKbWidth = mirrorWidth.toFloat()
            mirrorKbHeight = 400f

            physicalKbWidth = service.keyboardOverlay?.getKeyboardView()?.width?.toFloat() ?: 600f
            physicalKbHeight = service.keyboardOverlay?.getKeyboardView()?.height?.toFloat() ?: 400f



            val savedHeight = service.prefs.prefMirrorHeight
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

            val savedX = service.prefs.prefMirrorX
            val savedY = service.prefs.prefMirrorY

            if (savedX != -1) {
                mirrorKeyboardParams?.x = savedX
                mirrorKeyboardParams?.gravity = Gravity.TOP or Gravity.START
            }
            if (savedY != -1) {
                mirrorKeyboardParams?.y = savedY
            }

            val savedAlpha = service.prefs.prefMirrorAlpha / 255f
            mirrorKeyboardContainer?.alpha = savedAlpha

            mirrorWindowManager?.addView(mirrorKeyboardContainer, mirrorKeyboardParams)

            mirrorKeyboardView?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateMirrorSyncDimensions()
            }



        } catch (e: Exception) {
            Log.e(TAG, "Failed to create mirror keyboard", e)
        }
    }

    private fun updateMirrorSyncDimensions() {
        val physicalView = service.keyboardOverlay?.getKeyboardView()
        if (physicalView != null && physicalView.width > 0 && physicalView.height > 0) {
            physicalKbWidth = physicalView.width.toFloat()
            physicalKbHeight = physicalView.height.toFloat()
        }

        val mirrorView = mirrorKeyboardView
        if (mirrorView != null && mirrorView.width > 0 && mirrorView.height > 0) {
            mirrorKbWidth = mirrorView.width.toFloat()
            mirrorKbHeight = mirrorView.height.toFloat()
        }


    }

    private fun showMirrorTemporarily() {
        if (mirrorKeyboardContainer == null || mirrorKeyboardView == null) return

        mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)

        val alpha = service.prefs.prefMirrorAlpha / 255f
        mirrorKeyboardContainer?.alpha = 1f
        mirrorKeyboardView?.alpha = alpha.coerceAtLeast(0.7f)

        mirrorFadeHandler.postDelayed(mirrorFadeRunnable, 2000)
    }

    private fun handleResize(deltaX: Int, deltaY: Int) {
        var currentWidth = mirrorKeyboardParams?.width ?: 600
        var currentHeight = mirrorKeyboardParams?.height ?: 350

        if (currentWidth == WindowManager.LayoutParams.WRAP_CONTENT || currentWidth <= 0) {
            currentWidth = mirrorKeyboardContainer?.width ?: mirrorKeyboardView?.width ?: 600
            if (currentWidth <= 0) {
                val display = displayManager?.getDisplay(service.inputTargetDisplayId)
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

        val newWidth = (currentWidth + deltaX).coerceIn(250, 1500)
        val newHeight = (currentHeight + deltaY).coerceIn(150, 1200)



        mirrorKeyboardParams?.width = newWidth
        mirrorKeyboardParams?.height = newHeight

        val physicalHeight = service.keyboardOverlay?.getViewHeight()?.toFloat() ?: 350f
        val physicalScale = service.keyboardOverlay?.getScale() ?: 1.0f

        val heightRatio = newHeight.toFloat() / physicalHeight
        val newScale = (physicalScale * heightRatio).coerceIn(0.5f, 2.0f)



        mirrorKeyboardView?.setScale(newScale)

        mirrorKeyboardView?.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        service.prefs.prefMirrorWidth = newWidth
        service.prefs.prefMirrorHeight = newHeight

        service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
            .putInt("mirror_width", newWidth)
            .putInt("mirror_height", newHeight)
            .apply()
    }

    private fun handleMove(deltaX: Int, deltaY: Int) {
        val currentX = mirrorKeyboardParams?.x ?: 0
        val currentY = mirrorKeyboardParams?.y ?: 0
        val gravity = mirrorKeyboardParams?.gravity ?: 0
        val isBottomGravity = (gravity and Gravity.BOTTOM) == Gravity.BOTTOM

        val newX = currentX + deltaX

        val newY = if (isBottomGravity) {
            currentY - deltaY
        } else {
            currentY + deltaY
        }

        service.showToast("Move: ($currentX,$currentY)->($newX,$newY) btm=$isBottomGravity")

        mirrorKeyboardParams?.x = newX
        mirrorKeyboardParams?.y = newY

        mirrorKeyboardParams?.gravity = Gravity.TOP or Gravity.START

        service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
            .putInt("mirror_x", newX)
            .putInt("mirror_y", newY)
            .apply()
    }

    private fun handleTouchDown(x: Float, y: Float, mirrorX: Float, mirrorY: Float): Boolean {
        isMirrorDragActive = false
        isInOrientationMode = true
        lastOrientX = x
        lastOrientY = y

        mirrorKeyboardView?.animate()?.cancel()
        mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)

        mirrorKeyboardView?.alpha = 0.9f
        mirrorKeyboardContainer?.alpha = 1f

        service.keyboardOverlay?.setOrientationMode(true)
        mirrorTrailView?.setTrailColor(0xFFFF9900.toInt())
        service.keyboardOverlay?.setOrientationTrailColor(0xFFFF9900.toInt())

        mirrorTrailView?.clear()
        service.keyboardOverlay?.clearOrientationTrail()
        service.keyboardOverlay?.startOrientationTrail(x, y)
        mirrorTrailView?.addPoint(mirrorX, mirrorY)

        orientationModeHandler.removeCallbacks(orientationModeTimeout)
        orientationModeHandler.postDelayed(orientationModeTimeout, service.prefs.prefMirrorOrientDelayMs)

        stopMirrorKeyRepeat()
        return true
    }

    private fun handleTouchMove(x: Float, y: Float, mirrorX: Float, mirrorY: Float): Boolean {
        if (isMirrorDragActive) {
            mirrorTrailView?.setTrailColor(0xFF4488FF.toInt())
            mirrorTrailView?.addPoint(mirrorX, mirrorY)

            val currentKey = service.keyboardOverlay?.getKeyAtPosition(x, y)
            val isBackspace = (currentKey == "BKSP" || currentKey == "⌫" || currentKey == "BACKSPACE")

            if (isBackspace) isHoveringBackspace = true

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

        if (isInOrientationMode) {
            val dx = x - lastOrientX
            val dy = y - lastOrientY
            if (kotlin.math.sqrt(dx * dx + dy * dy) > MOVEMENT_THRESHOLD) {
                lastOrientX = x
                lastOrientY = y
                orientationModeHandler.removeCallbacks(orientationModeTimeout)
                orientationModeHandler.postDelayed(orientationModeTimeout, service.prefs.prefMirrorOrientDelayMs)
            }
            service.keyboardOverlay?.addOrientationTrailPoint(x, y)
            mirrorTrailView?.addPoint(mirrorX, mirrorY)
            return true
        }

        // BLUE MODE
        mirrorTrailView?.addPoint(mirrorX, mirrorY)
        val currentKey = service.keyboardOverlay?.getKeyAtPosition(x, y)

        if (currentKey != null && mirrorRepeatableKeys.contains(currentKey)) {
            if (mirrorRepeatKey == currentKey) return false
            stopMirrorKeyRepeat()
            mirrorRepeatKey = currentKey

            mirrorRepeatHandler.postDelayed({
                if (mirrorRepeatKey == currentKey && !isInOrientationMode) {
                    isMirrorRepeating = true
                    service.keyboardOverlay?.triggerKeyPress(currentKey)
                    mirrorRepeatHandler.postDelayed(mirrorRepeatRunnable, MIRROR_REPEAT_INTERVAL)
                }
            }, 150)
        } else {
            stopMirrorKeyRepeat()
        }
        return false
    }

    private fun handleTouchUp(x: Float, y: Float, mirrorX: Float, mirrorY: Float, action: Int): Boolean {
        if (isMirrorDragActive) {
            mirrorKeyboardView?.highlightKey("BKSP", false, 0)

            val upKey = service.keyboardOverlay?.getKeyAtPosition(x, y)
            val isDroppedOnBksp = (upKey == "BKSP" || upKey == "⌫" || upKey == "BACKSPACE")

            if (isHoveringBackspace || isDroppedOnBksp) {
                service.keyboardOverlay?.blockPrediction(draggedPredictionIndex)
                mirrorKeyboardView?.removeCandidateAtIndex(draggedPredictionIndex)
                service.showToast("Blocked Prediction")
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

        orientationModeHandler.removeCallbacks(orientationModeTimeout)
        val wasRepeating = isMirrorRepeating
        stopMirrorKeyRepeat()

        if (isInOrientationMode) {
            isInOrientationMode = false
            service.keyboardOverlay?.setOrientationMode(false)
            mirrorTrailView?.clear()
            service.keyboardOverlay?.clearOrientationTrail()

            if (!wasRepeating) {
                service.keyboardOverlay?.handleDeferredTap(x, y)
            }
        } else {
            mirrorTrailView?.clear()
        }

        mirrorKeyboardView?.animate()?.alpha(0.3f)?.setDuration(200)?.start()
        mirrorFadeHandler.removeCallbacks(mirrorFadeRunnable)
        mirrorFadeHandler.postDelayed(mirrorFadeRunnable, 2000)

        return false
    }

    private fun stopMirrorKeyRepeat() {
        mirrorRepeatHandler.removeCallbacks(mirrorRepeatRunnable)
        mirrorRepeatKey = null
        isMirrorRepeating = false
    }

    /**
     * Gets the current mirror keyboard parameters (for saving).
     */
    fun getParams(): WindowManager.LayoutParams? = mirrorKeyboardParams
}