package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
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
    
    // Config
    private var currentWidth = 450
    // We use PROPORTIONAL spacing to ensure Aspect Ratio matches on all screens
    // 0 margin on container, slight margin on keys handled by spacing
    private val KEY_SPACING_RATIO = 0.005f 
    
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

    private val ROW_NUMS = listOf(
        KeyDef("1", KeyEvent.KEYCODE_1), KeyDef("2", KeyEvent.KEYCODE_2), KeyDef("3", KeyEvent.KEYCODE_3),
        KeyDef("4", KeyEvent.KEYCODE_4), KeyDef("5", KeyEvent.KEYCODE_5), KeyDef("6", KeyEvent.KEYCODE_6),
        KeyDef("7", KeyEvent.KEYCODE_7), KeyDef("8", KeyEvent.KEYCODE_8), KeyDef("9", KeyEvent.KEYCODE_9),
        KeyDef("0", KeyEvent.KEYCODE_0)
    )
    
    private val ROW_SYMS = listOf(
        KeyDef("@", KeyEvent.KEYCODE_AT), KeyDef("#", KeyEvent.KEYCODE_POUND), KeyDef("$", KeyEvent.KEYCODE_4), 
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
        // Zero padding - we fill the aspect ratio box completely
        mainContainer.setPadding(0, 0, 0, 0)
        
        mainContainer.addView(createRow(if (isSymbols) ROW_NUMS else ROW_1))
        mainContainer.addView(createRow(if (isSymbols) ROW_SYMS else ROW_2))
        mainContainer.addView(createRow(ROW_3))
        mainContainer.addView(createRow(ROW_4))
        mainContainer.addView(createRow(ARROWS))

        // Match Parent (which is strictly controlled by Window Size)
        root.addView(mainContainer, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        
        // --- SYNC LOCKER ---
        // Ensure Physical Keyboard strictly follows the 0.55 Ratio
        root.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
            val width = right - left
            val height = bottom - top
            if (width > 0) {
                val targetHeight = (width * 0.55f).toInt()
                if (abs(height - targetHeight) > 10) {
                    layoutParams?.height = targetHeight
                    try { windowManager.updateViewLayout(keyboardLayout, layoutParams) } catch (e: Exception) {}
                }
            }
        }
        
        return root
    }

    private fun createRow(keys: List<KeyDef>): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = LinearLayout.HORIZONTAL
        
        // Vertical Weight 1.0 -> 5 Rows = 20% height each
        val rowParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
        rowParams.weight = 1.0f
        row.layoutParams = rowParams
        
        val MAX_ROW_WEIGHT = 10f
        val currentWeight = keys.map { it.weight }.sum()
        val missingWeight = MAX_ROW_WEIGHT - currentWeight
        
        if (missingWeight > 0.1f) {
            val spacer = View(context)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            params.weight = missingWeight / 2f
            row.addView(spacer, params)
        }
        
        // Font scaled to Width
        val fontSize = (currentWidth / 30f).coerceIn(10f, 22f)
        val marginPx = (currentWidth * KEY_SPACING_RATIO).toInt().coerceAtLeast(1)
        
        for (k in keys) {
            val btn = TextView(context)
            val label = if (!isSymbols && isShifted && k.label.length == 1) k.label.uppercase() else k.label
            
            btn.text = label
            btn.setTextColor(Color.WHITE)
            btn.textSize = fontSize
            btn.gravity = Gravity.CENTER
            btn.typeface = Typeface.DEFAULT_BOLD
            
            val keyBg = GradientDrawable()
            keyBg.setColor(if (k.isSpecial) Color.parseColor("#444444") else Color.parseColor("#2A2A2A"))
            keyBg.cornerRadius = 10f
            btn.background = keyBg
            
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            params.weight = k.weight
            params.setMargins(marginPx, marginPx, marginPx, marginPx)
            row.addView(btn, params)
            
            btn.setOnClickListener {
                handleKeyPress(k)
                btn.alpha = 0.5f
                btn.postDelayed({ btn.alpha = 1.0f }, 50)
            }
        }
        
        if (missingWeight > 0.1f) {
            val spacer = View(context)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            params.weight = missingWeight / 2f
            row.addView(spacer, params)
        }

        return row
    }

    private fun handleKeyPress(k: KeyDef) {
        when (k.code) {
            -1 -> { isShifted = !isShifted; refreshLayout() }
            -2 -> { isSymbols = !isSymbols; refreshLayout() }
            else -> {
                keyInjector(k.code)
                if (isShifted) { isShifted = false; refreshLayout() }
            }
        }
    }

    fun show(width: Int, height: Int) {
        if (isVisible) return
        
        currentWidth = width
        // Strict Start
        val targetHeight = (width * 0.55f).toInt()
        
        layoutParams = WindowManager.LayoutParams(
            currentWidth,
            targetHeight, 
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            android.graphics.PixelFormat.TRANSLUCENT
        )
        
        layoutParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        layoutParams?.y = 0 

        keyboardLayout = createView() as FrameLayout
        windowManager.addView(keyboardLayout, layoutParams)
        isVisible = true
    }

    fun hide() {
        if (!isVisible) return
        try { windowManager.removeView(keyboardLayout) } catch (e: Exception) {}
        isVisible = false
        keyboardLayout = null
    }
    
    fun toggle(width: Int, height: Int) { if (isVisible) hide() else show(width, height) }

    private fun refreshLayout() {
        if (!isVisible) return
        val p = keyboardLayout?.layoutParams
        windowManager.removeView(keyboardLayout)
        keyboardLayout = createView() as FrameLayout
        windowManager.addView(keyboardLayout, p)
    }
}
