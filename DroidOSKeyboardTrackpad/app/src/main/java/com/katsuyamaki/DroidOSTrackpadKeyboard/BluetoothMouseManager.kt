package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.SystemClock
import android.util.Log
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
        Log.w(BT_TAG, "┌─────────────────────────────────────────────────────────┐")
        Log.w(BT_TAG, "│ CREATE REQUESTED: createBtMouseCaptureOverlay()         │")
        Log.w(BT_TAG, "└─────────────────────────────────────────────────────────┘")
        Log.d(BT_TAG, "├─ Trigger Source:", Exception("Creation Stack Trace"))
        Log.d(BT_TAG, "├─ isBtMouseCaptureActive: $isBtMouseCaptureActive")
        Log.d(BT_TAG, "├─ windowManager null?: ${windowManager == null}")
        Log.d(BT_TAG, "├─ inputTargetDisplayId: ${service.inputTargetDisplayId}")
        Log.d(BT_TAG, "├─ currentDisplayId: ${service.currentDisplayId}")

        if (isBtMouseCaptureActive) {
            Log.d(BT_TAG, "├─ SKIP: Already active")
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
            Log.d(BT_TAG, "├─ ★ SUCCESS: BT Mouse Capture Overlay ADDED ★")
            hideSystemCursor()
            Log.d(BT_TAG, "└─ Overlay active, system cursor hidden")
            service.showToast("BT Mouse Capture: ON")
        } catch (e: Exception) {
            Log.e(BT_TAG, "├─ ✗ FAILED to add overlay: ${e.message}", e)
            btMouseCaptureLayout = null
        }
    }

    fun removeBtMouseCaptureOverlay() {
        Log.w(BT_TAG, "┌─────────────────────────────────────────────────────────┐")
        Log.w(BT_TAG, "│ REMOVE REQUESTED: removeBtMouseCaptureOverlay()         │")
        Log.w(BT_TAG, "└─────────────────────────────────────────────────────────┘")
        Log.w(BT_TAG, ">>> REMOVAL TRIGGER TRACE <<<", Exception("Who called remove?"))
        Log.d(BT_TAG, "├─ isBtMouseCaptureActive: $isBtMouseCaptureActive")

        if (!isBtMouseCaptureActive) {
            Log.d(BT_TAG, "├─ SKIP: Not active (Logical state was already false)")
            return
        }

        try {
            btMouseCaptureLayout?.let {
                if (it.isAttachedToWindow) {
                    windowManager.removeView(it)
                    Log.d(BT_TAG, "├─ ★ SUCCESS: Overlay REMOVED ★")
                }
            }
        } catch (e: Exception) {
            Log.w(BT_TAG, "├─ Error removing overlay: ${e.message}")
        }

        btMouseCaptureLayout = null
        isBtMouseCaptureActive = false
        lastBtMouseX = 0f
        lastBtMouseY = 0f
        showSystemCursor()
        Log.d(BT_TAG, "└─ Cleanup complete, system cursor restored")
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
        Log.d(BT_TAG, "hideSystemCursor() called")
        if (service.shellService != null) {
            try {
                service.shellService?.setSystemCursorVisibility(false)
                systemCursorHidden = true
                Log.d(BT_TAG, "├─ ★ System cursor HIDDEN via Shizuku")
                return
            } catch (e: Exception) {
                Log.e(BT_TAG, "├─ Shizuku cursor hide failed", e)
            }
        }
        try {
            val imClass = Class.forName("android.hardware.input.InputManager")
            val getInstance = imClass.getMethod("getInstance")
            val inputManager = getInstance.invoke(null)
            val setCursorVisibility = imClass.getMethod("setCursorVisibility", Boolean::class.javaPrimitiveType)
            setCursorVisibility.invoke(inputManager, false)
            systemCursorHidden = true
            Log.d(BT_TAG, "├─ ★ System cursor HIDDEN via Local Reflection")
        } catch (e: Exception) {
            Log.w(BT_TAG, "├─ setCursorVisibility not available, trying pointer_speed method")
            try {
                service.shellService?.runCommand("settings put system pointer_speed -7")
                systemCursorHidden = true
                Log.d(BT_TAG, "├─ Set pointer_speed to -7 (fallback)")
            } catch (e2: Exception) {
                Log.e(BT_TAG, "├─ Failed to hide cursor: ${e2.message}")
            }
        }
    }

    private fun showSystemCursor() {
        Log.d(BT_TAG, "showSystemCursor() called")
        if (!systemCursorHidden) {
            Log.d(BT_TAG, "├─ Cursor wasn't hidden, skipping")
            return
        }
        if (service.shellService != null) {
            try {
                service.shellService?.setSystemCursorVisibility(true)
                systemCursorHidden = false
                Log.d(BT_TAG, "├─ ★ System cursor SHOWN via Shizuku")
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
            Log.d(BT_TAG, "├─ ★ System cursor SHOWN via Local Reflection")
        } catch (e: Exception) {
            Log.w(BT_TAG, "├─ setCursorVisibility not available, trying pointer_speed method")
            try {
                service.shellService?.runCommand("settings put system pointer_speed 0")
                systemCursorHidden = false
                Log.d(BT_TAG, "├─ Reset pointer_speed to 0 (fallback)")
            } catch (e2: Exception) {
                Log.e(BT_TAG, "├─ Failed to show cursor: ${e2.message}")
            }
        }
    }

    fun handleInputDeviceChange() {
        if (isBtMouseCaptureActive) {
            Log.d(BT_TAG, "Input device change detected - Refreshing Cursor Visibility")
            hideSystemCursor()
        }
    }

    fun bringToFront() {
        if (isBtMouseCaptureActive && btMouseCaptureLayout != null && btMouseCaptureLayout?.isAttachedToWindow == true) {
            try {
                windowManager.removeView(btMouseCaptureLayout)
                windowManager.addView(btMouseCaptureLayout, btMouseCaptureParams)
                Log.d(BT_TAG, "Z-Order: BT Capture Overlay moved to TOP")
            } catch (e: Exception) {
                Log.e(BT_TAG, "Z-Order: Failed to move BT Capture", e)
            }
        }
    }
}
