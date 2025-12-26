package com.example.coverscreentester

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.media.AudioRecordingConfiguration
import android.os.Build
import android.os.Handler
import android.os.Looper
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
    private val targetDisplayId: Int,
    private val onScreenToggleAction: () -> Unit,
    private val onScreenModeChangeAction: () -> Unit,
    private val onCloseAction: () -> Unit // New Parameter
) : KeyboardView.KeyboardListener {

    private var keyboardContainer: FrameLayout? = null
    private var keyboardView: KeyboardView? = null
    private var keyboardParams: WindowManager.LayoutParams? = null
    private var isVisible = false
    private val predictionEngine = PredictionEngine.instance
    // State Variables
    private var isMoving = false
    private var isResizing = false
    private var isAnchored = false
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialWindowX = 0
    private var initialWindowY = 0
    private var initialWidth = 0
    private var initialHeight = 0

    private val TAG = "KeyboardOverlay"

    // --- PREDICTION STATE ---

    private var currentComposingWord = StringBuilder()
    private val handler = Handler(Looper.getMainLooper())

    // NEW: Track sentence context and swipe history
    private var lastCommittedSwipeWord: String? = null
    private var isSentenceStart = true

    // Helper to inject text via OverlayService
    private fun injectText(text: String) {
        (context as? OverlayService)?.injectText(text)
    }

    // FIX Default height to WRAP_CONTENT (-2) to avoid cutting off rows
    private var keyboardWidth = 500
    private var keyboardHeight = WindowManager.LayoutParams.WRAP_CONTENT 
    
    private var screenWidth = 720
    private var screenHeight = 748
    private var currentRotation = 0
    private var currentAlpha = 200
    private var currentDisplayId = 0


    // Callbacks to talk back to OverlayService
    var onCursorMove: ((Float, Float, Boolean) -> Unit)? = null // dx, dy, isDragging
    var onCursorClick: ((Boolean) -> Unit)? = null
    var onTouchDown: (() -> Unit)? = null
    var onTouchUp: (() -> Unit)? = null
    var onTouchTap: (() -> Unit)? = null



    fun setScreenDimensions(width: Int, height: Int, displayId: Int) {
        // We no longer update inputHandler here
        
        keyboardParams?.let {
            it.width = width
            it.height = height
            try {
                windowManager.updateViewLayout(keyboardContainer, it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    fun updateScale(scale: Float) {
        if (keyboardView == null) return
        keyboardView?.setScale(scale)
        
        // FIX: Removed forced reset of keyboardHeight to WRAP_CONTENT.
        // We now respect the existing keyboardHeight (whether it's fixed pixels from a manual resize
        // or WRAP_CONTENT from default).
        
        if (isVisible && keyboardParams != null) {
            // If the window is set to WRAP_CONTENT, we might need to poke the WM to re-measure
            // effectively, but we shouldn't change the param value itself if it's already -2.
            // If it is fixed pixels, we leave it alone.
            
            // We only need to update layout if we want to ensure constraints are met,
            // but simply invalidating the view is usually enough for internal changes.
            // To be safe, we update the view layout with the *current* params.
            try { 
                windowManager.updateViewLayout(keyboardContainer, keyboardParams)
                // Do NOT call saveKeyboardSize() here. Scaling shouldn't change the 
                // "Window Size Preference" (Container), only the content scale.
            } catch (e: Exception) {}
        }
    }
    
    fun updateAlpha(alpha: Int) {
        currentAlpha = alpha
        if (isVisible && keyboardContainer != null) {
            val bg = keyboardContainer?.background as? GradientDrawable
            if (bg != null) {
                val fillColor = (alpha shl 24) or (0x1A1A1A)
                bg.setColor(fillColor)
                bg.setStroke(2, Color.parseColor("#44FFFFFF"))
            }
            val normalizedAlpha = alpha / 255f
            keyboardView?.alpha = normalizedAlpha
            keyboardContainer?.invalidate()
        }
    }
    
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
        } else {
            // Even if hidden, save the new bounds so they apply on next show
            val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putInt("keyboard_x_d$currentDisplayId", x)
                .putInt("keyboard_y_d$currentDisplayId", y)
                .putInt("keyboard_width_d$currentDisplayId", width)
                .putInt("keyboard_height_d$currentDisplayId", height)
                .apply()
        }
    }
   
    fun setAnchored(anchored: Boolean) {
        isAnchored = anchored
    }

    // Helper for OverlayService Profile Load
    fun updatePosition(x: Int, y: Int) {
        if (keyboardContainer == null || keyboardParams == null) {
            // Save to prefs if hidden
            context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
                .putInt("keyboard_x_d$currentDisplayId", x)
                .putInt("keyboard_y_d$currentDisplayId", y)
                .apply()
            return
        }
        keyboardParams?.x = x
        keyboardParams?.y = y
        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
            saveKeyboardPosition()
        } catch (e: Exception) { e.printStackTrace() }
    }

    // Helper for OverlayService Profile Load
    fun updateSize(w: Int, h: Int) {
        keyboardWidth = w
        keyboardHeight = h
        
        if (keyboardContainer == null || keyboardParams == null) {
            saveKeyboardSize()
            return
        }
        keyboardParams?.width = w
        keyboardParams?.height = h
        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
            saveKeyboardSize()
        } catch (e: Exception) { e.printStackTrace() }
    }
    
    // Robust Getters: Return live values if visible, otherwise return saved Prefs
    fun getViewX(): Int {
        if (keyboardParams != null) return keyboardParams!!.x
        return context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .getInt("keyboard_x_d$currentDisplayId", 0)
    }
    
    fun getViewY(): Int {
        if (keyboardParams != null) return keyboardParams!!.y
        return context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .getInt("keyboard_y_d$currentDisplayId", 0)
    }
    
    fun getViewWidth(): Int = keyboardWidth
    fun getViewHeight(): Int = keyboardHeight
    
    // [START ROTATION FIX]
    fun setRotation(angle: Int) {
        currentRotation = angle
        if (!isVisible || keyboardContainer == null || keyboardParams == null || keyboardView == null) return

        val isPortrait = (angle == 90 || angle == 270)

        // 1. Determine Logical Dimensions (Unrotated size)
        // We rely on keyboardWidth/Height being the canonical "Landscape" size.
        val baseW = keyboardWidth
        val baseH = keyboardHeight 

        // 2. Configure WINDOW Params (The touchable area on screen)
        // If rotated, we swap the dimensions passed to WindowManager
        if (isPortrait) {
            keyboardParams?.width = if (baseH == -2) WindowManager.LayoutParams.WRAP_CONTENT else baseH
            keyboardParams?.height = if (baseW == -2) WindowManager.LayoutParams.WRAP_CONTENT else baseW
        } else {
            keyboardParams?.width = if (baseW == -2) WindowManager.LayoutParams.WRAP_CONTENT else baseW
            keyboardParams?.height = if (baseH == -2) WindowManager.LayoutParams.WRAP_CONTENT else baseH
        }

        // 3. Configure VIEW Params (The Internal Content)
        // The View must ALWAYS be the logical size (e.g. Wide) to layout keys in rows correctly.
        val lp = keyboardView!!.layoutParams as FrameLayout.LayoutParams
        lp.width = if (baseW == -2) FrameLayout.LayoutParams.WRAP_CONTENT else baseW
        lp.height = if (baseH == -2) FrameLayout.LayoutParams.WRAP_CONTENT else baseH
        keyboardView!!.layoutParams = lp

        // 4. Apply Rotation to View (Not Container)
        keyboardView!!.rotation = angle.toFloat()
        keyboardContainer!!.rotation = 0f // Ensure container is NOT rotated

        // 5. Update Layout
        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
        } catch (e: Exception) {}

        // 6. Post-Layout Alignment
        // We must translate the view to re-center it because rotation happens around the pivot (center).
        // Since we swapped the Window dimensions, the centers might not align by default without this.
        keyboardView!!.post { alignRotatedView() }
    }

    private fun alignRotatedView() {
        if (keyboardView == null) return
        
        val angle = currentRotation
        val w = keyboardView!!.measuredWidth
        val h = keyboardView!!.measuredHeight
        
        // When rotated 90/270, the "Visual" width matches the View's Height, and vice versa.
        // We translate the view so its visual center matches the window's center.
        
        when (angle) {
            90, 270 -> {
                val tx = (h - w) / 2f
                val ty = (w - h) / 2f
                keyboardView!!.translationX = tx
                keyboardView!!.translationY = ty
            }
            else -> {
                keyboardView!!.translationX = 0f
                keyboardView!!.translationY = 0f
            }
        }
    }

    fun cycleRotation() {
        if (keyboardContainer == null) return
        val nextRotation = (currentRotation + 90) % 360
        setRotation(nextRotation)
    }

    fun resetPosition() {
        if (keyboardParams == null) return
        
        // 1. Reset Rotation state
        currentRotation = 0
        keyboardContainer?.rotation = 0f
        keyboardView?.rotation = 0f
        keyboardView?.translationX = 0f
        keyboardView?.translationY = 0f

        // 2. Calculate Defaults
        val defaultWidth = (screenWidth * 0.90f).toInt().coerceIn(300, 1200) // CHANGED: 90% width
        val defaultHeight = WindowManager.LayoutParams.WRAP_CONTENT
        val defaultX = (screenWidth - defaultWidth) / 2
        val defaultY = (screenHeight / 2) // Place in middle

        // 3. Update State & Params
        keyboardWidth = defaultWidth
        keyboardHeight = defaultHeight
        
        keyboardParams?.x = defaultX
        keyboardParams?.y = defaultY
        keyboardParams?.width = defaultWidth
        keyboardParams?.height = defaultHeight

        // 4. Update View Constraints
        if (keyboardView != null) {
            val lp = keyboardView!!.layoutParams as FrameLayout.LayoutParams
            lp.width = defaultWidth
            lp.height = FrameLayout.LayoutParams.WRAP_CONTENT
            keyboardView!!.layoutParams = lp
        }

        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
        } catch (e: Exception) {}
        
        saveKeyboardPosition()
        saveKeyboardSize()
    }
    // [END ROTATION FIX]

    fun show() { 
        if (isVisible) return
        try { 
            val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            currentAlpha = prefs.getInt("keyboard_alpha", 200)

            createKeyboardWindow()
            isVisible = true
            if (currentRotation != 0) setRotation(currentRotation)
        } catch (e: Exception) { Log.e(TAG, "Failed to show keyboard", e) } 
    }
    
    fun hide() { 
        if (!isVisible) return
        try { 
            windowManager.removeView(keyboardContainer)
            keyboardContainer = null
            keyboardView = null
            isVisible = false 
        } catch (e: Exception) { Log.e(TAG, "Failed to hide keyboard", e) } 
    }
    
    fun toggle() { if (isVisible) hide() else show() }
    fun isShowing(): Boolean = isVisible

    fun setFocusable(focusable: Boolean) {
        try {
            if (keyboardContainer == null || keyboardParams == null) return

            if (focusable) {
                // Remove NOT_FOCUSABLE (Make it focusable)
                keyboardParams?.flags = keyboardParams?.flags?.and(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv())
            } else {
                // Add NOT_FOCUSABLE (Make it click-through for focus purposes)
                keyboardParams?.flags = keyboardParams?.flags?.or(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            }
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // =================================================================================
    // FUNCTION: setVoiceActive
    // SUMMARY: Passes the voice state down to the keyboard view.
    // =================================================================================
    fun setVoiceActive(active: Boolean) {
        keyboardView?.setVoiceActive(active)
    }
    // =================================================================================
    // END BLOCK: setVoiceActive
    // =================================================================================

    fun moveWindow(dx: Int, dy: Int) {
        if (!isVisible || keyboardParams == null) return
        keyboardParams!!.x += dx; keyboardParams!!.y += dy
        try { windowManager.updateViewLayout(keyboardContainer, keyboardParams); saveKeyboardPosition() } catch (e: Exception) {}
    }
    
    fun resizeWindow(dw: Int, dh: Int) {
         if (!isVisible || keyboardParams == null) return
         
         // If current height is WRAP_CONTENT (-2), start from current measured height
         var currentH = keyboardParams!!.height
         if (currentH == WindowManager.LayoutParams.WRAP_CONTENT) {
             currentH = keyboardContainer?.height ?: 260
         }
         
         var currentW = keyboardParams!!.width
         if (currentW == WindowManager.LayoutParams.WRAP_CONTENT) {
             currentW = keyboardContainer?.width ?: 500
         }

         keyboardParams!!.width = max(280, currentW + dw)
         keyboardParams!!.height = max(180, currentH + dh)
         
         keyboardWidth = keyboardParams!!.width
         keyboardHeight = keyboardParams!!.height
         
         try { windowManager.updateViewLayout(keyboardContainer, keyboardParams); saveKeyboardSize() } catch (e: Exception) {}
    }

    private fun createKeyboardWindow() {
        keyboardContainer = FrameLayout(context)
        val containerBg = GradientDrawable()
        val fillColor = (currentAlpha shl 24) or (0x1A1A1A)
        containerBg.setColor(fillColor)
        containerBg.cornerRadius = 16f
        containerBg.setStroke(2, Color.parseColor("#44FFFFFF"))
        keyboardContainer?.background = containerBg

        // 1. The Keyboard Keys
        // --- FIX Connect Shell for Spacebar Trackpad ---
        // Pass the shell command capability to the view so it can inject mouse events


        // Initialize Handler using 'targetDisplayId' (the class property), NOT 'inputTargetDisplayId'

        keyboardView = KeyboardView(context)

        // Bind KeyboardView events to our OverlayService callbacks
        keyboardView?.cursorMoveAction = { dx, dy, isDragging ->
            onCursorMove?.invoke(dx, dy, isDragging)
        }

        keyboardView?.cursorClickAction = { isRight ->
            onCursorClick?.invoke(isRight)
        }

        // Touch Primitives
        keyboardView?.touchDownAction = { onTouchDown?.invoke() }
        keyboardView?.touchUpAction = { onTouchUp?.invoke() }
        keyboardView?.touchTapAction = { onTouchTap?.invoke() }


        

        // ------------------------------------------------
        keyboardView?.setKeyboardListener(this)
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        keyboardView?.setVibrationEnabled(prefs.getBoolean("vibrate", true))
        val scale = prefs.getInt("keyboard_key_scale", 100) / 100f; keyboardView?.setScale(scale)
        keyboardView?.alpha = currentAlpha / 255f

        val kbParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        kbParams.setMargins(6, 28, 6, 6)
        keyboardContainer?.addView(keyboardView, kbParams)

        // 2. The Swipe Trail Overlay (Must match keyboard params to align coordinates)
        val trailView = SwipeTrailView(context)
        val trailParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        trailParams.setMargins(6, 28, 6, 6) // Exact same margins as keyboard
        keyboardContainer?.addView(trailView, trailParams)

        // Link them
        keyboardView?.attachTrailView(trailView)

        addDragHandle(); addResizeHandle(); addCloseButton(); addTargetLabel()

        val savedX = prefs.getInt("keyboard_x_d$currentDisplayId", (screenWidth - keyboardWidth) / 2)
        val savedY = prefs.getInt("keyboard_y_d$currentDisplayId", screenHeight - 350 - 10)

        keyboardParams = WindowManager.LayoutParams(
            keyboardWidth,
            keyboardHeight,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        keyboardParams?.gravity = Gravity.TOP or Gravity.LEFT
        keyboardParams?.x = savedX
        keyboardParams?.y = savedY

        windowManager.addView(keyboardContainer, keyboardParams)
        updateAlpha(currentAlpha)
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
        // CHANGED: Call onCloseAction to notify Service (handles IME toggle & automation)
        button.addView(closeText, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)); button.setOnClickListener { onCloseAction() }
        keyboardContainer?.addView(button, buttonParams)
    }

    private fun addTargetLabel() {
        val label = TextView(context); label.text = "Display $targetDisplayId"; label.setTextColor(Color.parseColor("#888888")); label.textSize = 9f
        val labelParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT); labelParams.gravity = Gravity.TOP or Gravity.LEFT; labelParams.setMargins(8, 6, 0, 0)
        keyboardContainer?.addView(label, labelParams)
    }

    private fun handleDrag(event: MotionEvent): Boolean {
        if (isAnchored) return true
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

    // [START RESIZE FIX]
    private fun handleResize(event: MotionEvent): Boolean {
        if (isAnchored) return true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isResizing = true
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                // Capture the current WINDOW dimensions (Visual)
                initialWidth = keyboardParams?.width ?: keyboardWidth
                initialHeight = keyboardParams?.height ?: keyboardHeight
                
                // Handle WRAP_CONTENT case for initial values
                if (initialWidth < 0) initialWidth = keyboardContainer?.width ?: 300
                if (initialHeight < 0) initialHeight = keyboardContainer?.height ?: 200
            }
            MotionEvent.ACTION_MOVE -> {
                if (isResizing) {
                    val dX = (event.rawX - initialTouchX).toInt()
                    val dY = (event.rawY - initialTouchY).toInt()
                    
                    // 1. Calculate New VISUAL Dimensions (Window Size)
                    val newVisualW = max(280, initialWidth + dX)
                    val newVisualH = max(180, initialHeight + dY)
                    
                    // 2. Update Window Params
                    keyboardParams?.width = newVisualW
                    keyboardParams?.height = newVisualH
                    
                    // 3. Update LOGICAL Dimensions (Keyboard State)
                    // If rotated, dragging "Width" actually changes the Keyboard's "Height" (Rows)
                    if (currentRotation == 90 || currentRotation == 270) {
                        keyboardHeight = newVisualW
                        keyboardWidth = newVisualH
                    } else {
                        keyboardWidth = newVisualW
                        keyboardHeight = newVisualH
                    }
                    
                    // 4. Update Inner View Layout to match Logical Dimensions
                    if (keyboardView != null) {
                        val lp = keyboardView!!.layoutParams as FrameLayout.LayoutParams
                        lp.width = keyboardWidth
                        lp.height = keyboardHeight
                        keyboardView!!.layoutParams = lp
                        
                        // Re-center view if rotation is active
                        alignRotatedView()
                    }

                    try {
                        windowManager.updateViewLayout(keyboardContainer, keyboardParams)
                    } catch (e: Exception) {}
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isResizing = false
                saveKeyboardSize()
            }
        }
        return true
    }
    // [END RESIZE FIX]

    private fun saveKeyboardSize() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_width_d$currentDisplayId", keyboardWidth).putInt("keyboard_height_d$currentDisplayId", keyboardHeight).apply() }
    private fun saveKeyboardPosition() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_x_d$currentDisplayId", keyboardParams?.x ?: 0).putInt("keyboard_y_d$currentDisplayId", keyboardParams?.y ?: 0).apply() }
    private fun loadKeyboardSizeForDisplay(displayId: Int) { val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE); keyboardWidth = prefs.getInt("keyboard_width_d$displayId", keyboardWidth); keyboardHeight = prefs.getInt("keyboard_height_d$displayId", keyboardHeight) }


    // =================================================================================
    // FUNCTION: onKeyPress
    // SUMMARY: Handles key press events from the keyboard. Manages composing word state,
    //          sentence start detection, and auto-learning. Special handling for
    //          punctuation after swiped words to remove the trailing space.
    // =================================================================================
    override fun onKeyPress(keyCode: Int, char: Char?, metaState: Int) {
        android.util.Log.d("DroidOS_Key", "Press: $keyCode ('$char')")

        // --- PUNCTUATION AFTER SWIPE: Remove trailing space ---
        // If the last action was a swipe (which adds "word "), and user types punctuation,
        // we need to delete the trailing space first so we get "word." not "word ."
        val isPunctuation = char != null && (char == '.' || char == ',' || char == '!' ||
                                              char == '?' || char == ';' || char == ':' ||
                                              char == '\'' || char == '"')

        if (isPunctuation && lastCommittedSwipeWord != null && lastCommittedSwipeWord!!.endsWith(" ")) {
            // Delete the trailing space from the swiped word
            injectKey(KeyEvent.KEYCODE_DEL, 0)
            android.util.Log.d("DroidOS_Swipe", "PUNCTUATION: Removed trailing space before '$char'")

            // Update the swipe word to not include the space (in case they backspace)
            lastCommittedSwipeWord = lastCommittedSwipeWord!!.trimEnd()
        }

        // 1. Inject the key event
        injectKey(keyCode, metaState)

        // 2. Clear Swipe History on manual typing (but NOT for punctuation or shift)
        if (keyCode != KeyEvent.KEYCODE_SHIFT_LEFT &&
            keyCode != KeyEvent.KEYCODE_SHIFT_RIGHT &&
            !isPunctuation) {
            lastCommittedSwipeWord = null
        }

        // 3. Handle Backspace (Fixes "deleted text persists" bug)
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (currentComposingWord.isNotEmpty()) {
                currentComposingWord.deleteCharAt(currentComposingWord.length - 1)
                updateSuggestions()
            }
            return
        }

        // 4. Track Sentence Start
        if (keyCode == KeyEvent.KEYCODE_ENTER || char == '.' || char == '!' || char == '?') {
            isSentenceStart = true
            lastCommittedSwipeWord = null // Clear swipe state on sentence end

            // Auto-learn on punctuation
            if (currentComposingWord.isNotEmpty()) {
                val word = currentComposingWord.toString()
                if (word.length >= 2) {
                    predictionEngine.learnWord(context, word)
                }
            }
            currentComposingWord.clear()

        } else if (char != null && !Character.isWhitespace(char) && !isPunctuation) {
            isSentenceStart = false
        }

        // 5. Update Composing Word
        if (char != null && Character.isLetterOrDigit(char)) {
            currentComposingWord.append(char)
            updateSuggestions()
        } else if (char != null && Character.isWhitespace(char)) {
            // Space finishes a word
            if (currentComposingWord.isNotEmpty()) {
                val word = currentComposingWord.toString()
                if (word.length >= 2) {
                    predictionEngine.learnWord(context, word)
                }
            }
            currentComposingWord.clear()
            lastCommittedSwipeWord = null // Clear swipe state on space
            updateSuggestions()
        } else {
            // Other symbols clear composition
            currentComposingWord.clear()
            updateSuggestions()
        }
    }
    // =================================================================================
    // END BLOCK: onKeyPress with punctuation spacing fix
    // =================================================================================

    
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
        if (key == KeyboardView.SpecialKey.HIDE_KEYBOARD) {
            onCloseAction() // Calls the close/hide action passed from Service
            return
        }

        // =================================================================================
        // BACKSPACE HANDLING - SWIPE WORD DELETE
        // SUMMARY: If the last action was a swipe word commit, backspace deletes the
        //          entire word (plus trailing space). Otherwise, normal backspace behavior.
        //          This allows quick correction of mis-swiped words.
        // =================================================================================
        if (key == KeyboardView.SpecialKey.BACKSPACE) {
            // CHECK: Was the last input a swiped word?
            if (lastCommittedSwipeWord != null && lastCommittedSwipeWord!!.isNotEmpty()) {
                // Delete the entire swiped word (including trailing space)
                val deleteCount = lastCommittedSwipeWord!!.length
                android.util.Log.d("DroidOS_Swipe", "BACKSPACE: Deleting swiped word '${lastCommittedSwipeWord}' ($deleteCount chars)")

                for (i in 0 until deleteCount) {
                    injectKey(KeyEvent.KEYCODE_DEL, 0)
                }

                // Clear the swipe history so next backspace is normal
                lastCommittedSwipeWord = null

                // Don't inject another backspace - we already deleted
                return
            }

            // Normal backspace: delete from composing word
            if (currentComposingWord.isNotEmpty()) {
                currentComposingWord.deleteCharAt(currentComposingWord.length - 1)
                updateSuggestions()
            }
        } else if (key == KeyboardView.SpecialKey.SPACE) {
            // Space clears swipe history (user is continuing to type)
            lastCommittedSwipeWord = null
            resetComposition()
        } else {
            // Enter, Tabs, Arrows all break the current word chain
            lastCommittedSwipeWord = null
            resetComposition()
        }
        // =================================================================================
        // END BLOCK: BACKSPACE HANDLING - SWIPE WORD DELETE
        // =================================================================================

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

    // =================================================================================
    // FUNCTION: onSuggestionClick
    // SUMMARY: Handles when user taps a word in the prediction bar. Two modes:
    //          1. Swipe Correction: Replaces the last swiped word entirely
    //          2. Manual Typing Completion: Replaces partially typed word
    //          Uses small delays between deletes and text injection to ensure
    //          proper sequencing on the input field.
    // =================================================================================
    override fun onSuggestionClick(text: String, isNew: Boolean) {
        android.util.Log.d("DroidOS_Prediction", "Suggestion clicked: '$text' (isNew=$isNew, swipeWord='$lastCommittedSwipeWord', composing='$currentComposingWord')")

        // 1. Learn word if it was flagged as New
        if (isNew) {
            predictionEngine.learnWord(context, text)
            android.util.Log.d("DroidOS_Prediction", "Learned new word: $text")
        }

        // 2. Commit logic
        // --- LOGIC: Swipe Correction ---
        if (lastCommittedSwipeWord != null && lastCommittedSwipeWord!!.isNotEmpty()) {
            val deleteCount = lastCommittedSwipeWord!!.length
            android.util.Log.d("DroidOS_Prediction", "SWIPE REPLACE: Deleting $deleteCount chars, inserting '$text'")

            // Delete in a background thread with small delays for reliability
            Thread {
                try {
                    // Delete character by character with small delay
                    for (i in 0 until deleteCount) {
                        injectKey(KeyEvent.KEYCODE_DEL, 0)
                        Thread.sleep(5) // Small delay between deletes
                    }

                    // Small pause before injecting new text
                    Thread.sleep(20)

                    // Inject new word (Maintain capitalization of the replaced word)
                    var newText = text
                    val wasCap = Character.isUpperCase(lastCommittedSwipeWord!!.firstOrNull() ?: ' ')
                    if (wasCap) {
                        newText = newText.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    }

                    val finalText = "$newText "
                    injectText(finalText)

                    // Update history on UI thread
                    handler.post {
                        lastCommittedSwipeWord = finalText
                        updateSuggestions()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DroidOS_Prediction", "Suggestion replace failed: ${e.message}")
                }
            }.start()

        } else {
            // --- LOGIC: Manual Typing Completion ---
            val charsToDelete = currentComposingWord.length
            android.util.Log.d("DroidOS_Prediction", "MANUAL REPLACE: Deleting $charsToDelete chars, inserting '$text'")

            if (charsToDelete == 0) {
                // Nothing to delete, just insert
                injectText("$text ")
                resetComposition()
                return
            }

            // Delete and insert in background thread for reliability
            Thread {
                try {
                    // Delete character by character with small delay
                    for (i in 0 until charsToDelete) {
                        injectKey(KeyEvent.KEYCODE_DEL, 0)
                        Thread.sleep(5) // Small delay between deletes
                    }

                    // Small pause before injecting new text
                    Thread.sleep(20)

                    // Inject the full word + space
                    injectText("$text ")

                    // Reset on UI thread
                    handler.post {
                        resetComposition()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DroidOS_Prediction", "Suggestion insert failed: ${e.message}")
                }
            }.start()
        }
    }
    // =================================================================================
    // END BLOCK: onSuggestionClick with reliable replacement
    // =================================================================================

    // =================================================================================
    // FUNCTION: onSuggestionDropped
    // SUMMARY: Called when user drags a suggestion to backspace to delete/block it.
    //          DEBUG: Logging to confirm this is being called.
    // =================================================================================
    override fun onSuggestionDropped(text: String) {
        android.util.Log.d("DroidOS_Drag", ">>> onSuggestionDropped CALLED: '$text'")

        // Block the word
        predictionEngine.blockWord(context, text)

        android.widget.Toast.makeText(context, "Removed: $text", android.widget.Toast.LENGTH_SHORT).show()

        // Refresh suggestions to remove the blocked word
        updateSuggestions()

        android.util.Log.d("DroidOS_Drag", "<<< onSuggestionDropped COMPLETE: '$text' blocked")
    }
    // =================================================================================
    // END BLOCK: onSuggestionDropped with debug logging
    // =================================================================================

    // =================================================================================
    // FUNCTION: onSwipeDetected
    // SUMMARY: Handles swipe gesture completion. Runs decoding in background thread.
    //          LOGGING: Logs at every decision point to diagnose silent failures.
    // =================================================================================
    override fun onSwipeDetected(path: List<android.graphics.PointF>) {
        // LOG: Entry point - proves we got here from KeyboardView
        android.util.Log.d("DroidOS_Swipe", ">>> onSwipeDetected ENTRY (${path.size} points)")

        // CHECK: keyboardView exists
        if (keyboardView == null) {
            android.util.Log.e("DroidOS_Swipe", "ABORT: keyboardView is NULL")
            return
        }

        // CHECK: keyMap exists and has keys
        val keyMap = keyboardView?.getKeyCenters()
        if (keyMap == null) {
            android.util.Log.e("DroidOS_Swipe", "ABORT: getKeyCenters() returned NULL")
            return
        }
        if (keyMap.isEmpty()) {
            android.util.Log.e("DroidOS_Swipe", "ABORT: keyMap is EMPTY (0 keys)")
            return
        }

        android.util.Log.d("DroidOS_Swipe", "keyMap OK: ${keyMap.size} keys loaded")

        Thread {
            try {
                android.util.Log.d("DroidOS_Swipe", "--- Decoding Thread Started ---")

                val suggestions = predictionEngine.decodeSwipe(path, keyMap)

                android.util.Log.d("DroidOS_Swipe", "--- Decoding Complete: ${suggestions.size} suggestions ---")

                if (suggestions.isNotEmpty()) {
                    handler.post {
                        var bestMatch = suggestions[0]
                        if (isSentenceStart) {
                            bestMatch = bestMatch.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        }

                        val isCap = Character.isUpperCase(bestMatch.firstOrNull() ?: ' ')
                        val displaySuggestions = if (isCap) {
                            suggestions.map { it.replaceFirstChar { c -> c.titlecase() } }
                        } else {
                            suggestions
                        }.map { KeyboardView.Candidate(it, isNew = false) }

                        keyboardView?.setSuggestions(displaySuggestions)

                        // =================================================================================
                        // SWIPE TEXT COMMIT WITH SPACE HANDLING
                        // SUMMARY: If user was typing letters before swiping, we need to add a space
                        //          between the typed letters and the swiped word.
                        //          Example: Type "a" then swipe "dog" = "a dog" not "adog"
                        // =================================================================================
                        var textToCommit = bestMatch

                        // Check if there are uncommitted typed letters that need a space after them
                        if (currentComposingWord.isNotEmpty()) {
                            // User typed some letters, then started swiping
                            // We need to "finish" the typed word with a space first
                            android.util.Log.d("DroidOS_Swipe", "Composing word exists: '$currentComposingWord' - adding space before swipe")
                            textToCommit = " $bestMatch"
                            currentComposingWord.clear()
                        }

                        // Add trailing space
                        textToCommit = "$textToCommit "

                        injectText(textToCommit)
                        lastCommittedSwipeWord = textToCommit
                        isSentenceStart = false
                        // =================================================================================
                        // END BLOCK: SWIPE TEXT COMMIT WITH SPACE HANDLING
                        // =================================================================================

                        android.util.Log.d("DroidOS_Swipe", "SUCCESS: Committed '$bestMatch'")
                    }
                } else {
                    android.util.Log.e("DroidOS_Swipe", "FAIL: decodeSwipe returned EMPTY list")
                }
            } catch (e: Exception) {
                android.util.Log.e("DroidOS_Swipe", "CRASH in Swipe Thread: ${e.message}", e)
                e.printStackTrace()
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: onSwipeDetected with comprehensive logging
    // =================================================================================

    // =================================================================================
    // FUNCTION: updateSuggestions
    // SUMMARY: Updates the suggestion bar based on current composing word.
    //          Shows raw input + dictionary suggestions, filtering blocked words.
    // =================================================================================
    private fun updateSuggestions() {
        val prefix = currentComposingWord.toString()
        if (prefix.isEmpty()) {
            keyboardView?.setSuggestions(emptyList())
            return
        }

        // 1. Get dictionary suggestions (already filtered for blocked words)
        val suggestions = predictionEngine.getSuggestions(prefix, 3)

        val candidates = ArrayList<KeyboardView.Candidate>()

        // 2. Add Raw Input as first option (but NOT if it's blocked)
        val lowerPrefix = prefix.lowercase()
        val isBlocked = predictionEngine.isWordBlocked(lowerPrefix)

        if (!isBlocked) {
            val rawExists = predictionEngine.hasWord(prefix)
            candidates.add(KeyboardView.Candidate(prefix, isNew = !rawExists))
        }

        // 3. Add dictionary suggestions (avoiding duplicates)
        for (s in suggestions) {
            // Don't show if it looks exactly like the raw input (ignore case)
            if (!s.equals(prefix, ignoreCase = true)) {
                candidates.add(KeyboardView.Candidate(s, isNew = false))
            }
        }

        keyboardView?.setSuggestions(candidates.take(3))
    }
    // =================================================================================
    // END BLOCK: updateSuggestions
    // =================================================================================

    private fun resetComposition() {
        currentComposingWord.clear()
        keyboardView?.setSuggestions(emptyList())
    }

    private fun injectKey(keyCode: Int, metaState: Int) {
        (context as? OverlayService)?.injectKeyFromKeyboard(keyCode, metaState)
    }

    // --- Voice Logic & Mic Check Loop ---
    
    // Handler for the 1-second loop
    private val micCheckHandler = Handler(Looper.getMainLooper())
    
    // Runnable that checks if the Microphone is currently recording
    private val micCheckRunnable = object : Runnable {
        override fun run() {
            try {
                val audioManager = context.getSystemService(android.content.Context.AUDIO_SERVICE) as AudioManager
                
                // Use activeRecordingConfigurations (API 24+) to check if any app is recording
                var isMicOn = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (audioManager.activeRecordingConfigurations.isNotEmpty()) {
                        isMicOn = true
                    }
                }
                
                if (isMicOn) {
                    // Still recording, check again in 1 second
                    micCheckHandler.postDelayed(this, 1000)
                } else {
                    // Mic stopped (or not supported), turn off the green light
                    keyboardView?.setVoiceActive(false)
                }
            } catch (e: Exception) {
                // If check fails, fail safe to off
                keyboardView?.setVoiceActive(false)
            }
        }
    }

    private fun triggerVoiceTyping() {
        if (shellService == null) return

        // 1. UI: Turn Button Green Immediately
        keyboardView?.setVoiceActive(true)
        
        // 2. Start Monitoring Loop
        // Delay 3 seconds to allow the Voice IME to open and start recording
        micCheckHandler.removeCallbacks(micCheckRunnable)
        micCheckHandler.postDelayed(micCheckRunnable, 3000)

        // 3. Notify Service to stop blocking touches
        val intent = android.content.Intent("VOICE_TYPE_TRIGGERED")
        intent.setPackage(context.packageName)
        context.sendBroadcast(intent)

        // 4. Perform IME Switch via Shell
        Thread {
            try {
                // Fetch IME list and find Google Voice Typing
                val output = shellService?.runCommand("ime list -a -s") ?: ""
                val voiceIme = output.lines().find { it.contains("google", true) && it.contains("voice", true) } 
                    ?: output.lines().find { it.contains("voice", true) }
                
                if (voiceIme != null) {
                    shellService?.runCommand("ime set $voiceIme")
                } else {
                    android.util.Log.w(TAG, "Voice IME not found")
                    // If IME missing, turn off light
                    micCheckHandler.post { keyboardView?.setVoiceActive(false) }
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Voice Switch Failed", e)
                micCheckHandler.post { keyboardView?.setVoiceActive(false) }
            }
        }.start()
    }
}
