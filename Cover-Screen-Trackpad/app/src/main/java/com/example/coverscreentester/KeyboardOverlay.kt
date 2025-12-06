package com.example.coverscreentester

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import kotlin.math.max

class KeyboardOverlay(
    private val context: Context,
    private val windowManager: WindowManager,
    private val shellService: IShellService?,
    private val targetDisplayId: Int
) : KeyboardView.KeyboardListener {

    private var keyboardContainer: FrameLayout? = null
    private var keyboardView: KeyboardView? = null
    private var keyboardParams: WindowManager.LayoutParams? = null
    private var isVisible = false

    private var isMoving = false
    private var isResizing = false
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialWindowX = 0
    private var initialWindowY = 0
    private var initialWidth = 0
    private var initialHeight = 0

    private val TAG = "KeyboardOverlay"
    private var keyboardWidth = 500
    private var keyboardHeight = 260
    private var screenWidth = 720
    private var screenHeight = 748
    
    private var currentDisplayId = 0

    fun setScreenDimensions(width: Int, height: Int, displayId: Int = 0) {
        screenWidth = width
        screenHeight = height
        currentDisplayId = displayId
        
        loadKeyboardSizeForDisplay(displayId)
        
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        if (!prefs.contains("keyboard_width_d$displayId")) {
            keyboardWidth = (width * 0.95f).toInt().coerceIn(300, 650)
            keyboardHeight = (height * 0.36f).toInt().coerceIn(180, 320)
        }
    }

    fun updateScale(scale: Float) {
        if (keyboardView == null) return
        
        keyboardView?.setScale(scale)
        
        val newHeight = (260 * scale).toInt().coerceAtLeast(180)
        keyboardHeight = newHeight
        
        if (isVisible && keyboardParams != null) {
            keyboardParams?.height = newHeight
            try {
                windowManager.updateViewLayout(keyboardContainer, keyboardParams)
                saveKeyboardSize()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun show() {
        if (isVisible) return
        try { createKeyboardWindow(); isVisible = true } 
        catch (e: Exception) { Log.e(TAG, "Failed to show keyboard", e) }
    }

    fun hide() {
        if (!isVisible) return
        try { windowManager.removeView(keyboardContainer); keyboardContainer = null; keyboardView = null; isVisible = false } 
        catch (e: Exception) { Log.e(TAG, "Failed to hide keyboard", e) }
    }

    fun toggle() { if (isVisible) hide() else show() }
    fun isShowing(): Boolean = isVisible

    fun moveWindow(dx: Int, dy: Int) {
        if (!isVisible || keyboardParams == null) return
        keyboardParams!!.x += dx
        keyboardParams!!.y += dy
        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
            saveKeyboardPosition()
        } catch (e: Exception) {}
    }
    
    fun resizeWindow(dw: Int, dh: Int) {
         if (!isVisible || keyboardParams == null) return
         keyboardParams!!.width = max(280, keyboardParams!!.width + dw)
         keyboardParams!!.height = max(180, keyboardParams!!.height + dh)
         keyboardWidth = keyboardParams!!.width
         keyboardHeight = keyboardParams!!.height
         try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
            saveKeyboardSize()
         } catch (e: Exception) {}
    }

    private fun createKeyboardWindow() {
        keyboardContainer = FrameLayout(context)
        val containerBg = GradientDrawable()
        containerBg.setColor(Color.parseColor("#1A1A1A"))
        containerBg.cornerRadius = 16f
        containerBg.setStroke(2, Color.parseColor("#3DDC84"))
        keyboardContainer?.background = containerBg

        keyboardView = KeyboardView(context)
        keyboardView?.setKeyboardListener(this)
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        keyboardView?.setVibrationEnabled(prefs.getBoolean("vibrate", true))
        
        val scale = prefs.getInt("keyboard_key_scale", 100) / 100f
        keyboardView?.setScale(scale)

        val kbParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        kbParams.setMargins(6, 28, 6, 6)
        keyboardContainer?.addView(keyboardView, kbParams)

        addDragHandle()
        addResizeHandle()
        addCloseButton()
        addTargetLabel()

        val savedX = prefs.getInt("keyboard_x_d$currentDisplayId", (screenWidth - keyboardWidth) / 2)
        val savedY = prefs.getInt("keyboard_y_d$currentDisplayId", screenHeight - keyboardHeight - 10)

        keyboardParams = WindowManager.LayoutParams(
            keyboardWidth, keyboardHeight,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        keyboardParams?.gravity = Gravity.TOP or Gravity.LEFT
        keyboardParams?.x = savedX
        keyboardParams?.y = savedY
        windowManager.addView(keyboardContainer, keyboardParams)
    }

    private fun addDragHandle() {
        val handle = FrameLayout(context)
        val handleParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 28)
        handleParams.gravity = Gravity.TOP
        val indicator = View(context)
        val indicatorBg = GradientDrawable()
        indicatorBg.setColor(Color.parseColor("#555555"))
        indicatorBg.cornerRadius = 3f
        indicator.background = indicatorBg
        val indicatorParams = FrameLayout.LayoutParams(50, 5)
        indicatorParams.gravity = Gravity.CENTER
        indicatorParams.topMargin = 8
        handle.addView(indicator, indicatorParams)
        handle.setOnTouchListener { _, event -> handleDrag(event); true }
        keyboardContainer?.addView(handle, handleParams)
    }

    private fun addResizeHandle() {
        val handle = FrameLayout(context)
        val handleParams = FrameLayout.LayoutParams(36, 36)
        handleParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        val indicator = View(context)
        val indicatorBg = GradientDrawable()
        indicatorBg.setColor(Color.parseColor("#3DDC84"))
        indicatorBg.cornerRadius = 4f
        indicator.background = indicatorBg
        indicator.alpha = 0.7f
        val indicatorParams = FrameLayout.LayoutParams(14, 14)
        indicatorParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        indicatorParams.setMargins(0, 0, 6, 6)
        handle.addView(indicator, indicatorParams)
        handle.setOnTouchListener { _, event -> handleResize(event); true }
        keyboardContainer?.addView(handle, handleParams)
    }

    private fun addCloseButton() {
        val button = FrameLayout(context)
        val buttonParams = FrameLayout.LayoutParams(28, 28)
        buttonParams.gravity = Gravity.TOP or Gravity.RIGHT
        buttonParams.setMargins(0, 2, 4, 0)
        val closeText = TextView(context)
        closeText.text = "X"
        closeText.setTextColor(Color.parseColor("#FF5555"))
        closeText.textSize = 12f
        closeText.gravity = Gravity.CENTER
        button.addView(closeText, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        button.setOnClickListener { hide() }
        keyboardContainer?.addView(button, buttonParams)
    }

    private fun addTargetLabel() {
        val label = TextView(context)
        label.text = "Display $targetDisplayId"
        label.setTextColor(Color.parseColor("#888888"))
        label.textSize = 9f
        val labelParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        labelParams.gravity = Gravity.TOP or Gravity.LEFT
        labelParams.setMargins(8, 6, 0, 0)
        keyboardContainer?.addView(label, labelParams)
    }

    private fun handleDrag(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { isMoving = true; initialTouchX = event.rawX; initialTouchY = event.rawY; initialWindowX = keyboardParams?.x ?: 0; initialWindowY = keyboardParams?.y ?: 0 }
            MotionEvent.ACTION_MOVE -> { if (isMoving) { keyboardParams?.x = initialWindowX + (event.rawX - initialTouchX).toInt(); keyboardParams?.y = initialWindowY + (event.rawY - initialTouchY).toInt(); try { windowManager.updateViewLayout(keyboardContainer, keyboardParams) } catch (e: Exception) {} } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isMoving = false; saveKeyboardPosition() }
        }
        return true
    }

    private fun handleResize(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { isResizing = true; initialTouchX = event.rawX; initialTouchY = event.rawY; initialWidth = keyboardParams?.width ?: keyboardWidth; initialHeight = keyboardParams?.height ?: keyboardHeight }
            MotionEvent.ACTION_MOVE -> { if (isResizing) { keyboardParams?.width = max(280, initialWidth + (event.rawX - initialTouchX).toInt()); keyboardParams?.height = max(180, initialHeight + (event.rawY - initialTouchY).toInt()); keyboardWidth = keyboardParams?.width ?: keyboardWidth; keyboardHeight = keyboardParams?.height ?: keyboardHeight; try { windowManager.updateViewLayout(keyboardContainer, keyboardParams) } catch (e: Exception) {} } }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { isResizing = false; saveKeyboardSize() }
        }
        return true
    }

    private fun saveKeyboardSize() {
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
            .putInt("keyboard_width_d$currentDisplayId", keyboardWidth)
            .putInt("keyboard_height_d$currentDisplayId", keyboardHeight)
            .apply()
    }

    private fun saveKeyboardPosition() {
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
            .putInt("keyboard_x_d$currentDisplayId", keyboardParams?.x ?: 0)
            .putInt("keyboard_y_d$currentDisplayId", keyboardParams?.y ?: 0)
            .apply()
    }

    private fun loadKeyboardSizeForDisplay(displayId: Int) {
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        keyboardWidth = prefs.getInt("keyboard_width_d$displayId", keyboardWidth)
        keyboardHeight = prefs.getInt("keyboard_height_d$displayId", keyboardHeight)
    }

    override fun onKeyPress(keyCode: Int, char: Char?) { injectKey(keyCode) }
    
    override fun onTextInput(text: String) {
        if (shellService == null) return
        Thread {
            try {
                val escaped = escapeForShell(text)
                val cmd = "input -d $targetDisplayId text $escaped"
                shellService.runCommand(cmd)
            } catch (e: Exception) { Log.e(TAG, "Text injection failed", e) }
        }.start()
    }

    override fun onSpecialKey(key: KeyboardView.SpecialKey) {
        val keyCode = when (key) {
            KeyboardView.SpecialKey.BACKSPACE -> KeyEvent.KEYCODE_DEL
            KeyboardView.SpecialKey.ENTER -> KeyEvent.KEYCODE_ENTER
            KeyboardView.SpecialKey.SPACE -> KeyEvent.KEYCODE_SPACE
            KeyboardView.SpecialKey.TAB -> KeyEvent.KEYCODE_TAB
            KeyboardView.SpecialKey.ESCAPE -> KeyEvent.KEYCODE_ESCAPE
            KeyboardView.SpecialKey.ARROW_UP -> KeyEvent.KEYCODE_DPAD_UP
            KeyboardView.SpecialKey.ARROW_DOWN -> KeyEvent.KEYCODE_DPAD_DOWN
            KeyboardView.SpecialKey.ARROW_LEFT -> KeyEvent.KEYCODE_DPAD_LEFT
            KeyboardView.SpecialKey.ARROW_RIGHT -> KeyEvent.KEYCODE_DPAD_RIGHT
            KeyboardView.SpecialKey.HOME -> KeyEvent.KEYCODE_MOVE_HOME
            KeyboardView.SpecialKey.END -> KeyEvent.KEYCODE_MOVE_END
            KeyboardView.SpecialKey.DELETE -> KeyEvent.KEYCODE_FORWARD_DEL
            KeyboardView.SpecialKey.SHIFT -> KeyEvent.KEYCODE_SHIFT_LEFT
            KeyboardView.SpecialKey.CTRL -> KeyEvent.KEYCODE_CTRL_LEFT
            KeyboardView.SpecialKey.ALT -> KeyEvent.KEYCODE_ALT_LEFT
            else -> return
        }
        injectKey(keyCode)
    }

    private fun injectKey(keyCode: Int) {
        if (shellService == null) return
        Thread {
            try {
                val cmd = "input -d $targetDisplayId keyevent $keyCode"
                shellService.runCommand(cmd)
            } catch (e: Exception) { Log.e(TAG, "Key injection failed", e) }
        }.start()
    }
    
    private fun escapeForShell(text: String): String {
        val sb = StringBuilder()
        for (c in text) {
            when (c) {
                ' ' -> sb.append("%s")
                '\'' -> sb.append("'")
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '`' -> sb.append("\\`")
                '$' -> sb.append("\\$")
                '&' -> sb.append("\\&")
                '|' -> sb.append("\\|")
                ';' -> sb.append("\\;")
                '(' -> sb.append("\\(")
                ')' -> sb.append("\\)")
                '<' -> sb.append("\\<")
                '>' -> sb.append("\\>")
                '!' -> sb.append("\\!")
                '?' -> sb.append("\\?")
                '*' -> sb.append("\\*")
                '[' -> sb.append("\\[")
                ']' -> sb.append("\\]")
                '{' -> sb.append("\\{")
                '}' -> sb.append("\\}")
                '#' -> sb.append("\\#")
                '~' -> sb.append("\\~")
                '^' -> sb.append("\\^")
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }
}
