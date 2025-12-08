package com.example.coverscreentester

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.abs

class KeyboardManager(
    private val context: Context,
    private val windowManager: WindowManager,
    private val keyInjector: (Int) -> Unit
) {
    var keyboardLayout: FrameLayout? = null
    var layoutParams: WindowManager.LayoutParams? = null
    
    private var isShifted = false
    private var isSymbols = false
    private var isVisible = false
    
    // UI Constants
    private val KEY_HEIGHT = 45.dp
    private val ROW_MARGIN = 2.dp
    private val KEY_MARGIN = 2.dp
    private var keyboardWidth = 450 // Default, will resize
    private var keyboardHeight = 350
    
    // Data Classes
    data class KeyDef(val label: String, val code: Int, val weight: Float = 1f, val isSpecial: Boolean = false)

    private val ROW_1 = listOf(
        KeyDef("q", KeyEvent.KEYCODE_Q), KeyDef("w", KeyEvent.KEYCODE_W), KeyDef("e", KeyEvent.KEYCODE_E),
        KeyDef("r", KeyEvent.KEYCODE_R), KeyDef("t", KeyEvent.KEYCODE_T), KeyDef("y", KeyEvent.KEYCODE_Y),
        KeyDef("u", KeyEvent.KEYCODE_U), KeyDef("i", KeyEvent.KEYCODE_I), KeyDef("o", KeyEvent.KEYCODE_O),
        KeyDef("p", KeyEvent.KEYCODE_P)
    )
    
    private val ROW_2 = listOf(
        KeyDef("a", KeyEvent.KEYCODE_A), KeyDef("s", KeyEvent.KEYCODE_S), KeyDef("d", KeyEvent.KEYCODE_D),
        KeyDef("f", KeyEvent.KEYCODE_F), KeyDef("g", KeyEvent.KEYCODE_G), KeyDef("h", KeyEvent.KEYCODE_H),
        KeyDef("j", KeyEvent.KEYCODE_J), KeyDef("k", KeyEvent.KEYCODE_K), KeyDef("l", KeyEvent.KEYCODE_L)
    )
    
    private val ROW_3 = listOf(
        KeyDef("SHIFT", -1, 1.5f, true),
        KeyDef("z", KeyEvent.KEYCODE_Z), KeyDef("x", KeyEvent.KEYCODE_X), KeyDef("c", KeyEvent.KEYCODE_C),
        KeyDef("v", KeyEvent.KEYCODE_V), KeyDef("b", KeyEvent.KEYCODE_B), KeyDef("n", KeyEvent.KEYCODE_N),
        KeyDef("m", KeyEvent.KEYCODE_M),
        KeyDef("⌫", KeyEvent.KEYCODE_DEL, 1.5f, true)
    )
    
    private val ROW_4 = listOf(
        KeyDef("?123", -2, 1.5f, true),
        KeyDef(",", KeyEvent.KEYCODE_COMMA), 
        KeyDef("SPACE", KeyEvent.KEYCODE_SPACE, 4f), 
        KeyDef(".", KeyEvent.KEYCODE_PERIOD),
        KeyDef("ENTER", KeyEvent.KEYCODE_ENTER, 1.5f, true)
    )

    private val ARROWS = listOf(
        KeyDef("◄", KeyEvent.KEYCODE_DPAD_LEFT, 1f, true),
        KeyDef("▲", KeyEvent.KEYCODE_DPAD_UP, 1f, true),
        KeyDef("▼", KeyEvent.KEYCODE_DPAD_DOWN, 1f, true),
        KeyDef("►", KeyEvent.KEYCODE_DPAD_RIGHT, 1f, true)
    )

    // Number Row (Replaces Row 1 in Symbol Mode)
    private val ROW_NUMS = listOf(
        KeyDef("1", KeyEvent.KEYCODE_1), KeyDef("2", KeyEvent.KEYCODE_2), KeyDef("3", KeyEvent.KEYCODE_3),
        KeyDef("4", KeyEvent.KEYCODE_4), KeyDef("5", KeyEvent.KEYCODE_5), KeyDef("6", KeyEvent.KEYCODE_6),
        KeyDef("7", KeyEvent.KEYCODE_7), KeyDef("8", KeyEvent.KEYCODE_8), KeyDef("9", KeyEvent.KEYCODE_9),
        KeyDef("0", KeyEvent.KEYCODE_0)
    )
    
    private val ROW_SYMS = listOf(
        KeyDef("@", KeyEvent.KEYCODE_AT), KeyDef("#", KeyEvent.KEYCODE_POUND), KeyDef("$", KeyEvent.KEYCODE_4), // $ shares 4 shift usually, simplified here
        KeyDef("%", KeyEvent.KEYCODE_5), KeyDef("&", KeyEvent.KEYCODE_7), KeyDef("-", KeyEvent.KEYCODE_MINUS),
        KeyDef("+", KeyEvent.KEYCODE_PLUS), KeyDef("(", KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN), KeyDef(")", KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN)
    )

    fun createView(): View {
        val root = FrameLayout(context)
        val bg = GradientDrawable()
        bg.setColor(Color.parseColor("#EE121212"))
        bg.cornerRadius = 20f
        bg.setStroke(2, Color.parseColor("#44FFFFFF"))
        root.background = bg

        val mainContainer = LinearLayout(context)
        mainContainer.orientation = LinearLayout.VERTICAL
        mainContainer.setPadding(10, 20, 10, 10)
        
        // Add Rows
        mainContainer.addView(createRow(if (isSymbols) ROW_NUMS else ROW_1))
        mainContainer.addView(createRow(if (isSymbols) ROW_SYMS else ROW_2))
        mainContainer.addView(createRow(ROW_3))
        mainContainer.addView(createRow(ROW_4))
        mainContainer.addView(createRow(ARROWS))

        root.addView(mainContainer, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        
        // Add Resize Handle (Bottom Right)
        val resizeHandle = View(context)
        resizeHandle.setBackgroundColor(Color.parseColor("#803DDC84"))
        val rhParams = FrameLayout.LayoutParams(50, 50)
        rhParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        root.addView(resizeHandle, rhParams)
        
        resizeHandle.setOnTouchListener { _, event -> handleResize(event) }
        
        // Add Move Handle (Top Center)
        val moveHandle = View(context)
        moveHandle.setBackgroundColor(Color.parseColor("#40FFFFFF"))
        val mhParams = FrameLayout.LayoutParams(100, 15)
        mhParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        mhParams.topMargin = 5
        root.addView(moveHandle, mhParams)
        
        moveHandle.setOnTouchListener { _, event -> handleMove(event) }

        return root
    }

    private fun createRow(keys: List<KeyDef>): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        
        for (k in keys) {
            val btn = TextView(context)
            val label = if (!isSymbols && isShifted && k.label.length == 1) k.label.uppercase() else k.label
            
            btn.text = label
            btn.setTextColor(Color.WHITE)
            btn.textSize = 14f
            btn.gravity = Gravity.CENTER
            btn.typeface = Typeface.DEFAULT_BOLD
            
            val keyBg = GradientDrawable()
            keyBg.setColor(if (k.isSpecial) Color.parseColor("#444444") else Color.parseColor("#2A2A2A"))
            keyBg.cornerRadius = 10f
            btn.background = keyBg
            
            val params = LinearLayout.LayoutParams(0, KEY_HEIGHT)
            params.weight = k.weight
            params.setMargins(KEY_MARGIN, ROW_MARGIN, KEY_MARGIN, ROW_MARGIN)
            row.addView(btn, params)
            
            btn.setOnClickListener {
                handleKeyPress(k)
                // Visual feedback
                btn.alpha = 0.5f
                btn.postDelayed({ btn.alpha = 1.0f }, 50)
            }
        }
        return row
    }

    private fun handleKeyPress(k: KeyDef) {
        when (k.code) {
            -1 -> { // SHIFT
                isShifted = !isShifted
                refreshLayout()
            }
            -2 -> { // SYMBOLS
                isSymbols = !isSymbols
                refreshLayout()
            }
            else -> {
                // If it's a letter and shifted, we rely on the injected keycode being consistent
                // In a real key event injection, KeyCode_A + CapsLock/Shift state is needed.
                // For simplicity, Shizuku injects the raw keycode. 
                // To support true caps, we might need to inject CAPS_LOCK toggle or shift modifier.
                // Current simplified approach: Just inject the code.
                keyInjector(k.code)
                
                // Auto unshift after one char (standard mobile behavior)
                if (isShifted) {
                    isShifted = false
                    refreshLayout()
                }
            }
        }
    }

    fun show(width: Int, height: Int) {
        if (isVisible) return
        
        keyboardWidth = width
        keyboardHeight = height

        layoutParams = WindowManager.LayoutParams(
            keyboardWidth,
            WindowManager.LayoutParams.WRAP_CONTENT, // Height dynamic based on rows
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or 
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            android.graphics.PixelFormat.TRANSLUCENT
        )
        layoutParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        layoutParams?.y = 50 // Margin from bottom

        keyboardLayout = createView() as FrameLayout
        windowManager.addView(keyboardLayout, layoutParams)
        isVisible = true
    }

    fun hide() {
        if (!isVisible) return
        try {
            windowManager.removeView(keyboardLayout)
        } catch (e: Exception) {}
        isVisible = false
        keyboardLayout = null
    }
    
    fun toggle(width: Int, height: Int) {
        if (isVisible) hide() else show(width, height)
    }

    private fun refreshLayout() {
        if (!isVisible) return
        val p = keyboardLayout?.layoutParams
        windowManager.removeView(keyboardLayout)
        keyboardLayout = createView() as FrameLayout
        windowManager.addView(keyboardLayout, p)
    }

    // --- Helpers ---
    private val Int.dp: Int get() = (this * context.resources.displayMetrics.density).toInt()
    
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    
    private fun handleResize(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                layoutParams?.let { initialX = it.width; initialY = it.height } // abusing x/y vars for w/h
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.rawX - initialTouchX).toInt()
                layoutParams?.width = (initialX + dx).coerceAtLeast(300)
                // Height updates automatically due to WRAP_CONTENT, but we could force scale
                windowManager.updateViewLayout(keyboardLayout, layoutParams)
                return true
            }
        }
        return false
    }

    private fun handleMove(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                layoutParams?.let { initialX = it.x; initialY = it.y }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
//                 val dx = (initialTouchX - event.rawX).toInt() // Inverse for bottom gravity sometimes depending on config
                val dy = (initialTouchY - event.rawY).toInt()
                // For Gravity.BOTTOM, positive Y moves UP
                layoutParams?.y = (initialY + dy)
                windowManager.updateViewLayout(keyboardLayout, layoutParams)
                return true
            }
        }
        return false
    }
}
