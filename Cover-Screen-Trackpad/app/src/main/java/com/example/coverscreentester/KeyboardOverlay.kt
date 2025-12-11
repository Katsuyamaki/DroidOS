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

/*
 * ======================================================================================
 * CRITICAL REGRESSION CHECKLIST - KEYBOARD OVERLAY
 * ======================================================================================
 * 1. ROTATION LOGIC:
 * - `setRotation(angle)` MUST SWAP width/height for 90°/270° orientations.
 * - Child view translation (X/Y) is required to center the rotated view in the swapped window.
 * 2. WINDOW FLAGS: FLAG_LAYOUT_NO_LIMITS + LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES.
 * 3. INPUT INJECTION: Use shellService.injectKey / runCommand("input text").
 * 4. SCALING & RESIZING: updateScale modifies height. Resize handle works.
 * 5. ALPHA: Support independent opacity via updateAlpha().
 * 6. PRESETS: Support absolute positioning via setWindowBounds().
 * ======================================================================================
 */

class KeyboardOverlay(
    private val context: Context,
    private val windowManager: WindowManager,
    private val shellService: IShellService?,
    private val targetDisplayId: Int,
    private val onScreenToggleAction: () -> Unit,
    private val onScreenModeChangeAction: () -> Unit
) : KeyboardView.KeyboardListener {

    private var keyboardContainer: FrameLayout? = null
    private var keyboardView: KeyboardView? = null
    private var keyboardParams: WindowManager.LayoutParams? = null
    private var isVisible = false

    // =========================
    // KEYBOARD OVERLAY STATE VARIABLES
    // isAnchored blocks handle drag/resize when true
    // =========================
    private var isMoving = false
    private var isResizing = false
    private var isAnchored = false  // NEW: Anchor mode to disable handle drag/resize
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialWindowX = 0
    private var initialWindowY = 0
    private var initialWidth = 0
    private var initialHeight = 0
    // =========================
    // END STATE VARIABLES
    // =========================
    private val TAG = "KeyboardOverlay"
    private var keyboardWidth = 500
    private var keyboardHeight = 260
    private var screenWidth = 720
    private var screenHeight = 748
    private var currentRotation = 0
    private var currentAlpha = 200
    
    private var currentDisplayId = 0

    fun setScreenDimensions(width: Int, height: Int, displayId: Int = 0) {
        screenWidth = width
        screenHeight = height
        currentDisplayId = displayId
        loadKeyboardSizeForDisplay(displayId)
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        currentAlpha = prefs.getInt("keyboard_alpha", 200)
        if (!prefs.contains("keyboard_width_d$displayId")) {
            keyboardWidth = (width * 0.95f).toInt().coerceIn(300, 650)
            keyboardHeight = (height * 0.36f).toInt().coerceIn(180, 320) 
        }
    }

    fun updateScale(scale: Float) {
        if (keyboardView == null) return
        keyboardView?.setScale(scale)
        val newHeight = (320 * scale).toInt().coerceAtLeast(180)
        keyboardHeight = newHeight
        if (isVisible && keyboardParams != null) {
            keyboardParams?.height = newHeight
            try { windowManager.updateViewLayout(keyboardContainer, keyboardParams); saveKeyboardSize() } catch (e: Exception) {}
        }
    }
    
    // --- RESTORED: Opacity Control ---
    // =========================
    // UPDATE ALPHA - Applies opacity to BOTH background container AND key views
    // This allows users to see apps behind the keyboard
    // Alpha range: 0-255 (0 = fully transparent, 255 = fully opaque)
    // =========================
    fun updateAlpha(alpha: Int) {
        currentAlpha = alpha
        if (isVisible && keyboardContainer != null) {
            // 1. Update background container opacity
            val bg = keyboardContainer?.background as? GradientDrawable
            if (bg != null) {
                val strokeColor = Color.parseColor("#3DDC84")
                val fillColor = (alpha shl 24) or (0x1A1A1A)
                bg.setColor(fillColor)
                bg.setStroke(2, strokeColor)
            }
            
            // 2. Update KeyboardView (keys) opacity
            // Convert 0-255 to 0.0-1.0 for View.setAlpha()
            val normalizedAlpha = alpha / 255f
            keyboardView?.alpha = normalizedAlpha
            
            keyboardContainer?.invalidate()
        }
    }
    // =========================
    // END UPDATE ALPHA
    // =========================
    
    // --- RESTORED: Preset Positioning ---
    fun setWindowBounds(x: Int, y: Int, width: Int, height: Int) {
        keyboardWidth = width
        keyboardHeight = height
        if (isVisible && keyboardParams != null) {
            keyboardParams?.x = x
            keyboardParams?.y = y
            keyboardParams?.width = width
            keyboardParams?.height = height
            try { 
                windowManager.updateViewLayout(keyboardContainer, keyboardParams)
                saveKeyboardPosition()
                saveKeyboardSize()
            } catch (e: Exception) {}
        }
    }
   

// =========================
    // SET ANCHORED - Called from OverlayService when anchor toggle changes
    // Blocks keyboard handle drag/resize when true
    // =========================
    fun setAnchored(anchored: Boolean) {
        isAnchored = anchored
    }
    // =========================
    // END SET ANCHORED
    // =========================


    fun getWidth(): Int = keyboardWidth
    fun getHeight(): Int = keyboardHeight
    
    fun setRotation(angle: Int) {
        if (!isVisible || keyboardContainer == null || keyboardParams == null) {
            currentRotation = angle 
            return
        }

        currentRotation = angle
        val isPortrait = (angle == 90 || angle == 270)
        
        val targetW = if (isPortrait) keyboardHeight else keyboardWidth
        val targetH = if (isPortrait) keyboardWidth else keyboardHeight
        
        keyboardParams?.width = targetW
        keyboardParams?.height = targetH
        
        keyboardContainer?.rotation = angle.toFloat()
        
        if (keyboardView != null) {
            val lp = keyboardView!!.layoutParams as FrameLayout.LayoutParams
            if (isPortrait) {
                lp.width = keyboardWidth
                lp.height = keyboardHeight
                keyboardView?.translationX = (targetW - keyboardWidth) / 2f
                keyboardView?.translationY = (targetH - keyboardHeight) / 2f
            } else {
                lp.width = FrameLayout.LayoutParams.MATCH_PARENT
                lp.height = FrameLayout.LayoutParams.MATCH_PARENT
                keyboardView?.translationX = 0f
                keyboardView?.translationY = 0f
            }
            keyboardView?.layoutParams = lp
        }

        try { windowManager.updateViewLayout(keyboardContainer, keyboardParams) } catch (e: Exception) {}
    }

    fun show() { 
        if (isVisible) return
        try { 
            createKeyboardWindow()
            isVisible = true
            if (currentRotation != 0) setRotation(currentRotation)
        } catch (e: Exception) { Log.e(TAG, "Failed to show keyboard", e) } 
    }
    
    fun hide() { if (!isVisible) return; try { windowManager.removeView(keyboardContainer); keyboardContainer = null; keyboardView = null; isVisible = false } catch (e: Exception) { Log.e(TAG, "Failed to hide keyboard", e) } }
    fun toggle() { if (isVisible) hide() else show() }
    fun isShowing(): Boolean = isVisible

    fun moveWindow(dx: Int, dy: Int) {
        if (!isVisible || keyboardParams == null) return
        keyboardParams!!.x += dx; keyboardParams!!.y += dy
        try { windowManager.updateViewLayout(keyboardContainer, keyboardParams); saveKeyboardPosition() } catch (e: Exception) {}
    }
    
    fun resizeWindow(dw: Int, dh: Int) {
         if (!isVisible || keyboardParams == null) return
         keyboardParams!!.width = max(280, keyboardParams!!.width + dw); keyboardParams!!.height = max(180, keyboardParams!!.height + dh)
         keyboardWidth = keyboardParams!!.width; keyboardHeight = keyboardParams!!.height
         try { windowManager.updateViewLayout(keyboardContainer, keyboardParams); saveKeyboardSize() } catch (e: Exception) {}
    }

    private fun createKeyboardWindow() {
        keyboardContainer = FrameLayout(context)
        val containerBg = GradientDrawable(); 
        val fillColor = (currentAlpha shl 24) or (0x1A1A1A)
        containerBg.setColor(fillColor)
        containerBg.cornerRadius = 16f; containerBg.setStroke(2, Color.parseColor("#3DDC84"))
        keyboardContainer?.background = containerBg
        
        keyboardView = KeyboardView(context)
        keyboardView?.setKeyboardListener(this)
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        keyboardView?.setVibrationEnabled(prefs.getBoolean("vibrate", true))
        val scale = prefs.getInt("keyboard_key_scale", 100) / 100f; keyboardView?.setScale(scale)
        val kbParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        kbParams.setMargins(6, 28, 6, 6)
        keyboardContainer?.addView(keyboardView, kbParams)
        addDragHandle(); addResizeHandle(); addCloseButton(); addTargetLabel()
        val savedX = prefs.getInt("keyboard_x_d$currentDisplayId", (screenWidth - keyboardWidth) / 2)
        val savedY = prefs.getInt("keyboard_y_d$currentDisplayId", screenHeight - keyboardHeight - 10)
        keyboardParams = WindowManager.LayoutParams(keyboardWidth, keyboardHeight, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
             keyboardParams?.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        keyboardParams?.gravity = Gravity.TOP or Gravity.LEFT; keyboardParams?.x = savedX; keyboardParams?.y = savedY
        windowManager.addView(keyboardContainer, keyboardParams)
    }

    private fun addDragHandle() {
        val handle = FrameLayout(context); val handleParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 28); handleParams.gravity = Gravity.TOP
        val indicator = View(context); val indicatorBg = GradientDrawable(); indicatorBg.setColor(Color.parseColor("#555555")); indicatorBg.cornerRadius = 3f; indicator.background = indicatorBg
        val indicatorParams = FrameLayout.LayoutParams(50, 5); indicatorParams.gravity = Gravity.CENTER; indicatorParams.topMargin = 8
        handle.addView(indicator, indicatorParams); handle.setOnTouchListener { _, event -> handleDrag(event); true }
        keyboardContainer?.addView(handle, handleParams)
    }

    private fun addResizeHandle() {
        val handle = FrameLayout(context); val handleParams = FrameLayout.LayoutParams(36, 36); handleParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        val indicator = View(context); val indicatorBg = GradientDrawable(); indicatorBg.setColor(Color.parseColor("#3DDC84")); indicatorBg.cornerRadius = 4f; indicator.background = indicatorBg; indicator.alpha = 0.7f
        val indicatorParams = FrameLayout.LayoutParams(14, 14); indicatorParams.gravity = Gravity.BOTTOM or Gravity.RIGHT; indicatorParams.setMargins(0, 0, 6, 6)
        handle.addView(indicator, indicatorParams); handle.setOnTouchListener { _, event -> handleResize(event); true }
        keyboardContainer?.addView(handle, handleParams)
    }

    private fun addCloseButton() {
        val button = FrameLayout(context); val buttonParams = FrameLayout.LayoutParams(28, 28); buttonParams.gravity = Gravity.TOP or Gravity.RIGHT; buttonParams.setMargins(0, 2, 4, 0)
        val closeText = TextView(context); closeText.text = "X"; closeText.setTextColor(Color.parseColor("#FF5555")); closeText.textSize = 12f; closeText.gravity = Gravity.CENTER
        button.addView(closeText, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)); button.setOnClickListener { hide() }
        keyboardContainer?.addView(button, buttonParams)
    }

    private fun addTargetLabel() {
        val label = TextView(context); label.text = "Display $targetDisplayId"; label.setTextColor(Color.parseColor("#888888")); label.textSize = 9f
        val labelParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT); labelParams.gravity = Gravity.TOP or Gravity.LEFT; labelParams.setMargins(8, 6, 0, 0)
        keyboardContainer?.addView(label, labelParams)
    }

    // =========================
    // HANDLE DRAG - Processes keyboard overlay drag/move gestures
    // Returns early if anchored to prevent accidental movement
    // =========================
    private fun handleDrag(event: MotionEvent): Boolean {
        if (isAnchored) return true  // Anchored: block drag
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { 
                isMoving = true
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                initialWindowX = keyboardParams?.x ?: 0
                initialWindowY = keyboardParams?.y ?: 0 
            }
            MotionEvent.ACTION_MOVE -> { 
                if (isMoving) { 
                    keyboardParams?.x = initialWindowX + (event.rawX - initialTouchX).toInt()
                    keyboardParams?.y = initialWindowY + (event.rawY - initialTouchY).toInt()
                    try { windowManager.updateViewLayout(keyboardContainer, keyboardParams) } catch (e: Exception) {} 
                } 
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { 
                isMoving = false
                saveKeyboardPosition() 
            }
        }
        return true
    }
    // =========================
    // END HANDLE DRAG
    // =========================


    // =========================
    // HANDLE RESIZE - Processes keyboard overlay resize gestures
    // Returns early if anchored to prevent accidental resizing
    // =========================
    private fun handleResize(event: MotionEvent): Boolean {
        if (isAnchored) return true  // Anchored: block resize
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { 
                isResizing = true
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                initialWidth = keyboardParams?.width ?: keyboardWidth
                initialHeight = keyboardParams?.height ?: keyboardHeight 
            }
            MotionEvent.ACTION_MOVE -> { 
                if (isResizing) { 
                    keyboardParams?.width = max(280, initialWidth + (event.rawX - initialTouchX).toInt())
                    keyboardParams?.height = max(180, initialHeight + (event.rawY - initialTouchY).toInt())
                    keyboardWidth = keyboardParams?.width ?: keyboardWidth
                    keyboardHeight = keyboardParams?.height ?: keyboardHeight
                    try { windowManager.updateViewLayout(keyboardContainer, keyboardParams) } catch (e: Exception) {} 
                } 
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { 
                isResizing = false
                saveKeyboardSize() 
            }
        }
        return true
    }
    // =========================
    // END HANDLE RESIZE
    // =========================


    private fun saveKeyboardSize() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_width_d$currentDisplayId", keyboardWidth).putInt("keyboard_height_d$currentDisplayId", keyboardHeight).apply() }
    private fun saveKeyboardPosition() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_x_d$currentDisplayId", keyboardParams?.x ?: 0).putInt("keyboard_y_d$currentDisplayId", keyboardParams?.y ?: 0).apply() }
    private fun loadKeyboardSizeForDisplay(displayId: Int) { val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE); keyboardWidth = prefs.getInt("keyboard_width_d$displayId", keyboardWidth); keyboardHeight = prefs.getInt("keyboard_height_d$displayId", keyboardHeight) }

    override fun onKeyPress(keyCode: Int, char: Char?, metaState: Int) { injectKey(keyCode, metaState) }
    
    override fun onTextInput(text: String) {
        if (shellService == null) return
        Thread { try { val cmd = "input -d $targetDisplayId text \"$text\""; shellService.runCommand(cmd) } catch (e: Exception) { Log.e(TAG, "Text injection failed", e) } }.start()
    }

    override fun onScreenToggle() { onScreenToggleAction() }
    override fun onScreenModeChange() { onScreenModeChangeAction() }

    override fun onSpecialKey(key: KeyboardView.SpecialKey, metaState: Int) {
        if (key == KeyboardView.SpecialKey.VOICE_INPUT) {
            triggerVoiceTyping()
            return
        }
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
            KeyboardView.SpecialKey.MUTE -> KeyEvent.KEYCODE_VOLUME_MUTE
            KeyboardView.SpecialKey.VOL_UP -> KeyEvent.KEYCODE_VOLUME_UP
            KeyboardView.SpecialKey.VOL_DOWN -> KeyEvent.KEYCODE_VOLUME_DOWN
            KeyboardView.SpecialKey.BACK_NAV -> KeyEvent.KEYCODE_BACK
            KeyboardView.SpecialKey.FWD_NAV -> KeyEvent.KEYCODE_FORWARD
            else -> return
        }
        injectKey(keyCode, metaState)
    }

    private fun triggerVoiceTyping() {
        if (shellService == null) return
        Thread {
            try {
                val output = shellService.runCommand("ime list -a -s")
                val voiceIme = output.lines().find { it.contains("google", true) && it.contains("voice", true) }
                    ?: output.lines().find { it.contains("voice", true) }
                if (voiceIme != null) {
                    shellService.runCommand("ime set $voiceIme")
                }
            } catch (e: Exception) { Log.e(TAG, "Voice Switch Failed", e) }
        }.start()
    }

    private fun injectKey(keyCode: Int, metaState: Int) {
        if (shellService == null) return
        Thread {
            try {
                shellService.injectKey(keyCode, KeyEvent.ACTION_DOWN, metaState, targetDisplayId)
                Thread.sleep(20)
                shellService.injectKey(keyCode, KeyEvent.ACTION_UP, metaState, targetDisplayId)
            } catch (e: Exception) { Log.e(TAG, "Key injection failed", e) }
        }.start()
    }
}
