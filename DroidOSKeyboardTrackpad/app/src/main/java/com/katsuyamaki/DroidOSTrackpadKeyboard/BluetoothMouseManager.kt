package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.SystemClock
import android.view.Gravity
import android.view.InputDevice
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout

class BluetoothMouseManager(private val service: OverlayService, private val windowManager: WindowManager) {

    private var btMouseCaptureLayout: FrameLayout? = null
    private var btMouseCaptureParams: WindowManager.LayoutParams? = null
    internal var isBtMouseCaptureActive = false
    private var lastBtMouseX = 0f
    private var lastBtMouseY = 0f
    private var isBtMouseDragging = false
    private var systemCursorHidden = false

    private val BT_TAG = "BT_MOUSE_CAPTURE"

    fun createBtMouseCaptureOverlay() {
        if (isBtMouseCaptureActive) {
            return
        }

        lastBtMouseX = 0f
        lastBtMouseY = 0f
        isBtMouseDragging = false

        btMouseCaptureLayout = object : FrameLayout(service) {

            init {
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        this.pointerIcon = android.view.PointerIcon.getSystemIcon(context, android.view.PointerIcon.TYPE_NULL)
                    } catch(e: Exception) {}
                }
            }

            override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
                val isMouseSource = event.isFromSource(InputDevice.SOURCE_MOUSE) ||
                                    event.isFromSource(InputDevice.SOURCE_MOUSE_RELATIVE)

                if (!isMouseSource) {
                    return super.dispatchGenericMotionEvent(event)
                }

                when (event.actionMasked) {
                    MotionEvent.ACTION_HOVER_MOVE -> {
                        if (lastBtMouseX == 0f && lastBtMouseY == 0f) {
                            lastBtMouseX = event.x
                            lastBtMouseY = event.y
                            return true
                        }

                        val rawDx = event.x - lastBtMouseX
                        val rawDy = event.y - lastBtMouseY
                        lastBtMouseX = event.x
                        lastBtMouseY = event.y

                        val (scaledDx, scaledDy) = scaleBtMouseMovement(rawDx, rawDy)
                        service.handleExternalMouseMove(scaledDx, scaledDy, false)
                        return true
                    }

                    MotionEvent.ACTION_SCROLL -> {
                        val vScroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL)
                        val hScroll = event.getAxisValue(MotionEvent.AXIS_HSCROLL)
                        service.injectScroll(hScroll * 10f, vScroll * 10f)
                        return true
                    }

                    MotionEvent.ACTION_HOVER_ENTER, MotionEvent.ACTION_HOVER_EXIT -> {
                        return true
                    }
                }

                return true
            }

            override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                val toolType = event.getToolType(0)
                val isMouse = toolType == MotionEvent.TOOL_TYPE_MOUSE ||
                              event.isFromSource(InputDevice.SOURCE_MOUSE)

                if (!isMouse) {
                    if (service.forwardTouchToSiblings(event)) {
                        return true
                    }
                    return false
                }

                val isRightButton = (event.actionButton == MotionEvent.BUTTON_SECONDARY)

                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        if (!isRightButton) {
                             lastBtMouseX = event.x
                             lastBtMouseY = event.y
                             isBtMouseDragging = false
                             service.handleExternalTouchDown()
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val rawDx = event.x - lastBtMouseX
                        val rawDy = event.y - lastBtMouseY

                        if (kotlin.math.abs(rawDx) > 0 || kotlin.math.abs(rawDy) > 0) {
                            isBtMouseDragging = true
                        }

                        lastBtMouseX = event.x
                        lastBtMouseY = event.y

                        val (scaledDx, scaledDy) = scaleBtMouseMovement(rawDx, rawDy)
                        service.handleExternalMouseMove(scaledDx, scaledDy, true)
                    }

                    MotionEvent.ACTION_UP -> {
                        if (isRightButton) {
                             service.inputHandler.performClick(service.cursorX, service.cursorY, service.inputTargetDisplayId, true)
                        } else {
                             service.handleExternalTouchUp()
                        }
                        isBtMouseDragging = false
                    }
                }
                return true
            }
        }

        btMouseCaptureLayout?.setBackgroundColor(0x00000000)

        btMouseCaptureParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.LEFT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        try {
            windowManager.addView(btMouseCaptureLayout, btMouseCaptureParams)
            isBtMouseCaptureActive = true
            hideSystemCursor()
            service.showToast("BT Mouse Capture: ON")
        } catch (e: Exception) {
            btMouseCaptureLayout = null
        }
    }

    fun removeBtMouseCaptureOverlay() {
        if (!isBtMouseCaptureActive) {
            return
        }

        try {
            btMouseCaptureLayout?.let {
                if (it.isAttachedToWindow) {
                    windowManager.removeView(it)

                }
            }
        } catch (e: Exception) {

        }

        btMouseCaptureLayout = null
        isBtMouseCaptureActive = false
        lastBtMouseX = 0f
        lastBtMouseY = 0f
        showSystemCursor()

        service.showToast("BT Mouse Capture: OFF")
    }

    private fun scaleBtMouseMovement(rawDx: Float, rawDy: Float): Pair<Float, Float> {
        if (service.inputTargetDisplayId == service.currentDisplayId) {
            return Pair(rawDx * service.prefs.cursorSpeed, rawDy * service.prefs.cursorSpeed)
        }

        val scaleX = service.targetScreenWidth.toFloat() / service.uiScreenWidth.toFloat()
        val scaleY = service.targetScreenHeight.toFloat() / service.uiScreenHeight.toFloat()

        val scaledDx = rawDx * scaleX * service.prefs.cursorSpeed
        val scaledDy = rawDy * scaleY * service.prefs.cursorSpeed

        return Pair(scaledDx, scaledDy)
    }

    private fun hideSystemCursor() {

        if (service.shellService != null) {
            try {
                service.shellService?.setSystemCursorVisibility(false)
                systemCursorHidden = true

                return
            } catch (e: Exception) {
            }
        }
        try {
            val imClass = Class.forName("android.hardware.input.InputManager")
            val getInstance = imClass.getMethod("getInstance")
            val inputManager = getInstance.invoke(null)
            val setCursorVisibility = imClass.getMethod("setCursorVisibility", Boolean::class.javaPrimitiveType)
            setCursorVisibility.invoke(inputManager, false)
            systemCursorHidden = true

        } catch (e: Exception) {

            try {
                service.shellService?.runCommand("settings put system pointer_speed -7")
                systemCursorHidden = true

            } catch (e2: Exception) {
            }
        }
    }

    private fun showSystemCursor() {

        if (!systemCursorHidden) {

            return
        }
        if (service.shellService != null) {
            try {
                service.shellService?.setSystemCursorVisibility(true)
                systemCursorHidden = false

                return
            } catch (e: Exception) {}
        }
        try {
            val imClass = Class.forName("android.hardware.input.InputManager")
            val getInstance = imClass.getMethod("getInstance")
            val inputManager = getInstance.invoke(null)
            val setCursorVisibility = imClass.getMethod("setCursorVisibility", Boolean::class.javaPrimitiveType)
            setCursorVisibility.invoke(inputManager, true)
            systemCursorHidden = false

        } catch (e: Exception) {

            try {
                service.shellService?.runCommand("settings put system pointer_speed 0")
                systemCursorHidden = false

            } catch (e2: Exception) {
            }
        }
    }

    fun handleInputDeviceChange() {
        if (isBtMouseCaptureActive) {

            hideSystemCursor()
        }
    }

    fun bringToFront() {
        if (isBtMouseCaptureActive && btMouseCaptureLayout != null && btMouseCaptureLayout?.isAttachedToWindow == true) {
            try {
                windowManager.removeView(btMouseCaptureLayout)
                windowManager.addView(btMouseCaptureLayout, btMouseCaptureParams)

            } catch (e: Exception) {
            }
        }
    }
}
