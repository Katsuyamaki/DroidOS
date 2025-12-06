package com.example.coverscreentester

import android.os.SystemClock
import android.util.Log
import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import com.example.coverscreentester.IShellService
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Method

class ShellUserService : IShellService.Stub() {

    private val TAG = "ShellUserService"
    private lateinit var inputManager: Any
    private lateinit var injectInputEventMethod: Method
    private val INJECT_MODE_ASYNC = 0
    private var isReflectionBroken = false

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
            isReflectionBroken = true
        }
    }

    override fun setWindowingMode(taskId: Int, mode: Int) {
        try { Runtime.getRuntime().exec("am task set-windowing-mode $taskId $mode").waitFor() } catch (e: Exception) { Log.e(TAG, "Failed to set window mode", e) }
    }

    override fun resizeTask(taskId: Int, left: Int, top: Int, right: Int, bottom: Int) {
        try { Runtime.getRuntime().exec("am task resize $taskId $left $top $right $bottom") } catch (e: Exception) { Log.e(TAG, "Failed to resize task", e) }
    }

    // UPDATED: Now captures and returns standard output
    override fun runCommand(cmd: String?): String {
        if (cmd == null) return ""
        return try {
            val process = Runtime.getRuntime().exec(cmd)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            process.waitFor()
            output.toString().trim()
        } catch (e: Exception) {
            Log.e(TAG, "Command failed: $cmd", e)
            ""
        }
    }

    override fun injectKey(keyCode: Int, action: Int, metaState: Int, displayId: Int) {
        var reflectionSuccess = false
        if (!isReflectionBroken && this::inputManager.isInitialized && this::injectInputEventMethod.isInitialized) {
            try {
                val now = SystemClock.uptimeMillis()
                val event = KeyEvent(now, now, action, keyCode, 0, metaState, -1, 0, 0, InputDevice.SOURCE_KEYBOARD)
                try {
                    val method = InputEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
                    method.invoke(event, displayId)
                } catch (e: Exception) {}
                injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
                reflectionSuccess = true
            } catch (e: Exception) {
                Log.w(TAG, "Reflection failed, falling back to shell")
                isReflectionBroken = true
            }
        }

        if (!reflectionSuccess && action == KeyEvent.ACTION_DOWN) {
            try {
                // Fallback shell injection
                Runtime.getRuntime().exec("input -d $displayId keyevent $keyCode")
            } catch (e: Exception) {
                Log.e(TAG, "Shell fallback failed", e)
            }
        }
    }

    override fun execClick(x: Float, y: Float, displayId: Int) {
        val downTime = SystemClock.uptimeMillis()
        injectInternal(MotionEvent.ACTION_DOWN, x, y, displayId, downTime, downTime, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_PRIMARY)
        try { Thread.sleep(50) } catch (e: InterruptedException) {}
        injectInternal(MotionEvent.ACTION_UP, x, y, displayId, downTime, SystemClock.uptimeMillis(), InputDevice.SOURCE_MOUSE, 0)
    }

    override fun execRightClick(x: Float, y: Float, displayId: Int) {
        val downTime = SystemClock.uptimeMillis()
        injectInternal(MotionEvent.ACTION_DOWN, x, y, displayId, downTime, downTime, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_SECONDARY)
        try { Thread.sleep(50) } catch (e: InterruptedException) {}
        injectInternal(MotionEvent.ACTION_UP, x, y, displayId, downTime, SystemClock.uptimeMillis(), InputDevice.SOURCE_MOUSE, 0)
    }

    override fun injectMouse(action: Int, x: Float, y: Float, displayId: Int, source: Int, buttonState: Int, downTime: Long) {
        injectInternal(action, x, y, displayId, downTime, SystemClock.uptimeMillis(), source, buttonState)
    }

    override fun injectScroll(x: Float, y: Float, vDistance: Float, hDistance: Float, displayId: Int) {
        if (isReflectionBroken) return
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
        } catch (e: Exception) { isReflectionBroken = true } finally { event?.recycle() }
    }

    private fun setDisplayId(event: MotionEvent, displayId: Int) {
        try {
            val method = MotionEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
            method.invoke(event, displayId)
        } catch (e: Exception) {}
    }

    private fun injectInternal(action: Int, x: Float, y: Float, displayId: Int, downTime: Long, eventTime: Long, source: Int, buttonState: Int) {
        if (isReflectionBroken) return
        val props = MotionEvent.PointerProperties(); props.id = 0
        props.toolType = if (source == InputDevice.SOURCE_MOUSE) MotionEvent.TOOL_TYPE_MOUSE else MotionEvent.TOOL_TYPE_FINGER
        val coords = MotionEvent.PointerCoords(); coords.x = x; coords.y = y
        coords.pressure = if (buttonState != 0 || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) 1.0f else 0.0f; coords.size = 1.0f
        var event: MotionEvent? = null
        try {
            event = MotionEvent.obtain(downTime, eventTime, action, 1, arrayOf(props), arrayOf(coords), 0, buttonState, 1.0f, 1.0f, 0, 0, source, 0)
            setDisplayId(event, displayId)
            injectInputEventMethod.invoke(inputManager, event, INJECT_MODE_ASYNC)
        } catch (e: Exception) { isReflectionBroken = true } finally { event?.recycle() }
    }
}
