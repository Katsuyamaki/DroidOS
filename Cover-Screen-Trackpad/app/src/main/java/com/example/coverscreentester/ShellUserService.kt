package com.example.coverscreentester

import android.os.SystemClock
import android.util.Log
import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import com.example.coverscreentester.IShellService
import java.lang.reflect.Method

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"
    private lateinit var inputManager: Any
    private lateinit var injectInputEventMethod: Method
    private val INJECT_MODE_ASYNC = 0

    init {
        setupReflection()
    }

    private fun setupReflection() {
        try {
            val imClass = Class.forName("android.hardware.input.InputManager")
            val getInstance = imClass.getMethod("getInstance")
            inputManager = getInstance.invoke(null)!!

            injectInputEventMethod = imClass.getMethod(
                "injectInputEvent",
                InputEvent::class.java,
                Int::class.javaPrimitiveType
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup reflection", e)
        }
    }

    override fun setWindowingMode(taskId: Int, mode: Int) {
        try { Runtime.getRuntime().exec("am task set-windowing-mode $taskId $mode").waitFor() } catch (e: Exception) { Log.e(TAG, "Failed to set window mode", e) }
    }

    override fun resizeTask(taskId: Int, left: Int, top: Int, right: Int, bottom: Int) {
        try { Runtime.getRuntime().exec("am task resize $taskId $left $top $right $bottom") } catch (e: Exception) { Log.e(TAG, "Failed to resize task", e) }
    }

    override fun runCommand(cmd: String?): String {
        try { if (cmd != null) { Runtime.getRuntime().exec(cmd) } } catch (e: Exception) {}
        return "" 
    }

    override fun injectKey(keyCode: Int, action: Int) {
        if (action == KeyEvent.ACTION_DOWN) {
            try { Runtime.getRuntime().exec("input keyevent $keyCode") } catch (e: Exception) { Log.e(TAG, "Key injection failed", e) }
        }
    }

    override fun injectKeyOnDisplay(keyCode: Int, action: Int, displayId: Int) {
        if (!this::inputManager.isInitialized || !this::injectInputEventMethod.isInitialized) return
        val now = SystemClock.uptimeMillis()
        val event = KeyEvent(now, now, action, keyCode, 0, 0, -1, 0, 0, InputDevice.SOURCE_KEYBOARD)
        try {
            val method = InputEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
            method.invoke(event, displayId)
            injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
        } catch (e: Exception) { Log.e(TAG, "Key Injection on Display Failed", e) }
    }

    override fun execClick(x: Float, y: Float, displayId: Int) {
        val downTime = SystemClock.uptimeMillis()
        // FIX: Changed SOURCE_TOUCHSCREEN to SOURCE_MOUSE to align with cursor movement
        injectInternal(MotionEvent.ACTION_DOWN, x, y, displayId, downTime, downTime, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_PRIMARY)
        try { Thread.sleep(50) } catch (e: InterruptedException) {}
        injectInternal(MotionEvent.ACTION_UP, x, y, displayId, downTime, SystemClock.uptimeMillis(), InputDevice.SOURCE_MOUSE, 0)
    }

    override fun execRightClick(x: Float, y: Float, displayId: Int) {
        val downTime = SystemClock.uptimeMillis()
        // FIX: Changed SOURCE_TOUCHSCREEN to SOURCE_MOUSE
        injectInternal(MotionEvent.ACTION_DOWN, x, y, displayId, downTime, downTime, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_SECONDARY)
        try { Thread.sleep(50) } catch (e: InterruptedException) {}
        injectInternal(MotionEvent.ACTION_UP, x, y, displayId, downTime, SystemClock.uptimeMillis(), InputDevice.SOURCE_MOUSE, 0)
    }

    override fun injectMouse(action: Int, x: Float, y: Float, displayId: Int, source: Int, buttonState: Int, downTime: Long) {
        injectInternal(action, x, y, displayId, downTime, SystemClock.uptimeMillis(), source, buttonState)
    }

    override fun injectScroll(x: Float, y: Float, vDistance: Float, hDistance: Float, displayId: Int) {
        if (!this::inputManager.isInitialized || !this::injectInputEventMethod.isInitialized) return
        val now = SystemClock.uptimeMillis()
        val props = MotionEvent.PointerProperties(); props.id = 0; props.toolType = MotionEvent.TOOL_TYPE_MOUSE
        val coords = MotionEvent.PointerCoords(); coords.x = x; coords.y = y; coords.pressure = 1.0f; coords.size = 1.0f
        coords.setAxisValue(MotionEvent.AXIS_VSCROLL, vDistance)
        coords.setAxisValue(MotionEvent.AXIS_HSCROLL, hDistance)
        var event: MotionEvent? = null
        try {
            event = MotionEvent.obtain(now, now, MotionEvent.ACTION_SCROLL, 1, arrayOf(props), arrayOf(coords), 0, 0, 1.0f, 1.0f, 0, 0, InputDevice.SOURCE_MOUSE, 0)
            setDisplayId(event, displayId)
            injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
        } catch (e: Exception) { Log.e(TAG, "Scroll Injection failed", e) } finally { event?.recycle() }
    }

    private fun setDisplayId(event: MotionEvent, displayId: Int) {
        try {
            val method = MotionEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
            method.invoke(event, displayId)
        } catch (e: Exception) {}
    }

    private fun injectInternal(action: Int, x: Float, y: Float, displayId: Int, downTime: Long, eventTime: Long, source: Int, buttonState: Int) {
        if (!this::inputManager.isInitialized || !this::injectInputEventMethod.isInitialized) return
        val props = MotionEvent.PointerProperties(); props.id = 0
        props.toolType = if (source == InputDevice.SOURCE_MOUSE) MotionEvent.TOOL_TYPE_MOUSE else MotionEvent.TOOL_TYPE_FINGER
        val coords = MotionEvent.PointerCoords(); coords.x = x; coords.y = y
        coords.pressure = if (buttonState != 0 || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) 1.0f else 0.0f; coords.size = 1.0f
        var event: MotionEvent? = null
        try {
            event = MotionEvent.obtain(downTime, eventTime, action, 1, arrayOf(props), arrayOf(coords), 0, buttonState, 1.0f, 1.0f, 0, 0, source, 0)
            setDisplayId(event, displayId)
            injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
        } catch (e: Exception) { Log.e(TAG, "Injection failed", e) } finally { event?.recycle() }
    }
}
