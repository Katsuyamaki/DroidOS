package com.example.coverscreentester

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt
import android.annotation.SuppressLint
import android.util.Log

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    interface KeyboardListener {
        fun onKeyPress(keyCode: Int, char: Char?, metaState: Int)
        fun onTextInput(text: String)
        fun onSpecialKey(key: SpecialKey, metaState: Int)
        fun onScreenToggle()
        fun onScreenModeChange()
        fun onSuggestionClick(text: String) // New Callback
        fun onSwipeDetected(path: List<android.graphics.PointF>) // New
    }

    enum class SpecialKey {
        BACKSPACE, ENTER, SPACE, SHIFT, CAPS_LOCK, SYMBOLS, ABC,
        TAB, ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT,
        HOME, END, DELETE, ESCAPE, CTRL, ALT,
        VOL_UP, VOL_DOWN, MUTE, BACK_NAV, FWD_NAV, VOICE_INPUT, HIDE_KEYBOARD
    }

    enum class KeyboardState {
        LOWERCASE, UPPERCASE, CAPS_LOCK, SYMBOLS_1, SYMBOLS_2
    }

    private var listener: KeyboardListener? = null
    private var currentState = KeyboardState.LOWERCASE
    private var vibrationEnabled = true
    
    private var isCtrlActive = false
    private var isAltActive = false
    
    private var isVoiceActive = false
    
    private val BASE_KEY_HEIGHT = 40
    private val BASE_FONT_SIZE = 14f
    private var scaleFactor = 1.0f
    
    private var keyHeight = BASE_KEY_HEIGHT
    private var keySpacing = 2
    private var fontSize = BASE_FONT_SIZE

    // --- REPEAT LOGIC ---
    private val repeatHandler = Handler(Looper.getMainLooper())
    private var currentRepeatKey: String? = null
    private var isRepeating = false
    private val REPEAT_INITIAL_DELAY = 400L
    private val REPEAT_INTERVAL = 50L 

    // --- MULTITOUCH STATE ---
    private val activePointers = SparseArray<View>()
    
    // Caps Lock Logic
    private var capsLockPending = false
    private val capsHandler = Handler(Looper.getMainLooper())

    private val repeatRunnable = object : Runnable {
        override fun run() {
            currentRepeatKey?.let { key ->
                if (isRepeating) {
                    handleKeyPress(key, fromRepeat = true)
                    repeatHandler.postDelayed(this, REPEAT_INTERVAL)
                }
            }
        }
    }
    
    private val capsLockRunnable = Runnable {
        capsLockPending = true
        toggleCapsLock()
    }

    // UI Elements for Suggestions
    private var suggestionStrip: LinearLayout? = null
    private var cand1: TextView? = null
    private var cand2: TextView? = null
    private var cand3: TextView? = null

// =================================================================================
    // GESTURE TYPING STATE
    // SUMMARY: Variables for swipe/gesture typing detection. Swipe is only tracked for
    //          single-finger gestures. Multitouch cancels swipe detection to prevent
    //          false triggers during fast two-thumb typing.
    // =================================================================================
    private var swipeTrail: SwipeTrailView? = null
    private val keyCenters = HashMap<String, android.graphics.PointF>()
    private var isSwiping = false
    private val SWIPE_THRESHOLD = 35f // pixels of movement to trigger swipe mode (increased from 20)
    private val SWIPE_MIN_DISTANCE = 80f // minimum start-to-end distance for valid swipe
    private val SWIPE_MIN_PATH_POINTS = 10 // minimum path points for valid swipe (increased from 5)
    private var startTouchX = 0f
    private var startTouchY = 0f
    private var swipePointerId = -1 // Track which pointer started the swipe (-1 = none)

    // Store the full path for the decoder
    private val currentPath = ArrayList<android.graphics.PointF>()
    // =================================================================================
    // END BLOCK: GESTURE TYPING STATE
    // =================================================================================


    // --- SPACEBAR TRACKPAD VARIABLES ---
    var isPredictiveBarEmpty: Boolean = false
    
    // Actions triggered by Spacebar Trackpad
    var cursorMoveAction: ((Float, Float) -> Unit)? = null
    var cursorClickAction: ((Boolean) -> Unit)? = null
    
    private var spacebarPointerId = -1
    private var isSpaceTrackpadActive = false
    private var lastSpaceX = 0f
    private var lastSpaceY = 0f
    private val touchSlop = 15f 
    private val cursorSensitivity = 2.5f 
    private var currentActiveKey: View? = null

    private fun handleSpacebarClick(xRelativeToKey: Float, keyWidth: Int) {
        val zone = xRelativeToKey / keyWidth
        // 0: Left (0-33%), 2: Middle (33-66%), 1: Right (66-100%)
        val isRightClick = zone > 0.66f
        
        // Trigger click in OverlayService
        cursorClickAction?.invoke(isRightClick)
        
        performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }

    private fun moveMouse(dx: Float, dy: Float) {
        if (dx == 0f && dy == 0f) return
        
        // Send delta to OverlayService to move the fake cursor
        cursorMoveAction?.invoke(dx * cursorSensitivity, dy * cursorSensitivity)
    }



    // We attach the trail view externally from the Overlay
    fun attachTrailView(view: SwipeTrailView) {
        this.swipeTrail = view
    }

    // Expose key centers for the overlay to retrieve
    fun getKeyCenters(): Map<String, android.graphics.PointF> {
        return keyCenters
    }

    private val lowercaseRows = listOf(
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("SHIFT", "z", "x", "c", "v", "b", "n", "m", "BKSP")
    )

    private val uppercaseRows = listOf(
        listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
        listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
        listOf("SHIFT", "Z", "X", "C", "V", "B", "N", "M", "BKSP")
    )

    private val symbols1Rows = listOf(
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        listOf("@", "#", "\$", "%", "&", "-", "+", "(", ")"),
        listOf("SYM2", "*", "\"", "'", ":", ";", "!", "?", "BKSP")
    )

    private val symbols2Rows = listOf(
        listOf("~", "`", "|", "^", "=", "{", "}", "[", "]", "\\"),
        listOf("<", ">", "/", "_", "©", "®", "™", "°", "•"),
        // Replaced MODE with root to keep layout balanced, MODE is now contextual on SCREEN key
        listOf("√", "€", "£", "¥", "¢", "§", "¶", "∆", "BKSP")
    )

    // Row 4 
    private val row4Lower = listOf("SYM", ",", "SPACE", ".")
    private val row4Sym = listOf("ABC", ",", "SPACE", ".")

    // Row 5 
    private val arrowRow = listOf("TAB", "CTRL", "ALT", "←", "↑", "↓", "→", "ESC")
    
    // Row 6 (Moved SCREEN to far left)
    private val navRow = listOf("SCREEN", "HIDE_KB", "MUTE", "VOL-", "VOL+", "BACK", "FWD", "MIC")

    // Keys allowed to repeat when held
    private val alwaysRepeatable = setOf(
        "BKSP", "SPACE", "←", "→", "↑", "↓", "VOL+", "VOL-", "FWD", "BACK", "DELETE"
    )

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.parseColor("#1A1A1A"))
        setPadding(4, 4, 4, 4)
        buildKeyboard()
    }

    fun setKeyboardListener(l: KeyboardListener) { listener = l }
    fun setVibrationEnabled(enabled: Boolean) { vibrationEnabled = enabled }
    
    fun setScale(scale: Float) {
        this.scaleFactor = scale.coerceIn(0.5f, 2.0f)
        this.keyHeight = (BASE_KEY_HEIGHT * scaleFactor).toInt()
        this.fontSize = BASE_FONT_SIZE * scaleFactor
        buildKeyboard()
    }

    fun setVoiceActive(active: Boolean) {
        if (isVoiceActive != active) {
            isVoiceActive = active
            // Try to find and update just the MIC key to save resources
            val micView = findViewWithTag<View>("MIC")
            if (micView != null) {
                setKeyVisual(micView, false, "MIC")
            } else {
                invalidate() // Fallback: Redraw full view
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed) {
            mapKeys()
        }
    }

    private fun mapKeys() {
        keyCenters.clear()

        // 1. Get the absolute position of the KeyboardView itself
        val parentLoc = IntArray(2)
        this.getLocationOnScreen(parentLoc)
        val parentX = parentLoc[0]
        val parentY = parentLoc[1]

        // 2. Traverse all children to find tagged TextViews
        fun traverse(view: View) {
            if (view is android.view.ViewGroup) {
                for (i in 0 until view.childCount) {
                    traverse(view.getChildAt(i))
                }
            }

            // Check if this view (could be ViewGroup or TextView) has a tag
            if (view.tag is String) {
                val key = view.tag as String
                // We only care about single letters for swipe decoding (A-Z)
                if (key.length == 1 && Character.isLetter(key[0])) {
                    val loc = IntArray(2)
                    view.getLocationOnScreen(loc)

                    // Calculate center relative to the KeyboardView (0,0 is top-left of keyboard)
                    // This matches the MotionEvent coordinates we get in dispatchTouchEvent
                    val centerX = (loc[0] - parentX) + (view.width / 2f)
                    val centerY = (loc[1] - parentY) + (view.height / 2f)

                    keyCenters[key.uppercase()] = android.graphics.PointF(centerX, centerY)
                    // Also store lowercase for easier matching
                    keyCenters[key.lowercase()] = android.graphics.PointF(centerX, centerY)
                }
            }
        }
        traverse(this)

        android.util.Log.d("DroidOS_Swipe", "Keys mapped: ${keyCenters.size / 2} (Unique Letters)")
        if (keyCenters.isNotEmpty()) {
             android.util.Log.d("DroidOS_Swipe", "Example 'H': ${keyCenters["h"]}")
        }
    }

    fun setSuggestions(words: List<String>) {
        if (suggestionStrip == null) return

        val w1 = words.getOrNull(0) ?: ""
        val w2 = words.getOrNull(1) ?: ""
        val w3 = words.getOrNull(2) ?: ""

        cand1?.text = w1
        cand1?.visibility = if (w1.isNotEmpty()) View.VISIBLE else View.INVISIBLE
        cand1?.setOnClickListener { if (w1.isNotEmpty()) listener?.onSuggestionClick(w1) }

        cand2?.text = w2
        cand2?.visibility = if (w2.isNotEmpty()) View.VISIBLE else View.INVISIBLE
        cand2?.setOnClickListener { if (w2.isNotEmpty()) listener?.onSuggestionClick(w2) }

        cand3?.text = w3
        cand3?.visibility = if (w3.isNotEmpty()) View.VISIBLE else View.INVISIBLE
        cand3?.setOnClickListener { if (w3.isNotEmpty()) listener?.onSuggestionClick(w3) }

        // Show strip if we have suggestions, hide if empty (optional, keeping visible prevents jumpiness)
        // suggestionStrip?.visibility = if (words.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun buildKeyboard() {
        removeAllViews()

        // --- 1. SUGGESTION STRIP ---
        suggestionStrip = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (35 * scaleFactor).toInt()) // Slightly shorter than keys
            setBackgroundColor(Color.parseColor("#222222"))
            setPadding(0, 0, 0, 4)
        }

        fun createCandidateView(): TextView {
            return TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                textSize = fontSize * 0.9f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setBackgroundResource(android.R.drawable.list_selector_background)
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
            }
        }

        cand1 = createCandidateView(); suggestionStrip?.addView(cand1)
        // Divider
        val div1 = View(context).apply { layoutParams = LinearLayout.LayoutParams(1, LayoutParams.MATCH_PARENT); setBackgroundColor(Color.GRAY) }
        suggestionStrip?.addView(div1)

        cand2 = createCandidateView(); suggestionStrip?.addView(cand2)
        // Divider
        val div2 = View(context).apply { layoutParams = LinearLayout.LayoutParams(1, LayoutParams.MATCH_PARENT); setBackgroundColor(Color.GRAY) }
        suggestionStrip?.addView(div2)

        cand3 = createCandidateView(); suggestionStrip?.addView(cand3)

        addView(suggestionStrip)
        // --- END SUGGESTION STRIP ---
        
        val topRows = when (currentState) {
            KeyboardState.LOWERCASE -> lowercaseRows
            KeyboardState.UPPERCASE, KeyboardState.CAPS_LOCK -> uppercaseRows
            KeyboardState.SYMBOLS_1 -> symbols1Rows
            KeyboardState.SYMBOLS_2 -> symbols2Rows
        }
        for ((index, rowKeys) in topRows.withIndex()) {
            addView(createRow(rowKeys, index))
        }
        
        val bottomContainer = LinearLayout(context)
        bottomContainer.orientation = HORIZONTAL
        bottomContainer.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        
        val leftCol = LinearLayout(context)
        leftCol.orientation = VERTICAL
        val leftParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 8.5f)
        leftCol.layoutParams = leftParams
        
        val r4Keys = if (currentState == KeyboardState.LOWERCASE || currentState == KeyboardState.UPPERCASE || currentState == KeyboardState.CAPS_LOCK) row4Lower else row4Sym
        leftCol.addView(createRow(r4Keys, 3))
        leftCol.addView(createRow(arrowRow, 4))
        
        bottomContainer.addView(leftCol)
        
        val enterContainer = FrameLayout(context)
        val enterParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 1.5f)
        enterParams.setMargins(dpToPx(keySpacing), dpToPx(keySpacing), 0, 0)
        enterContainer.layoutParams = enterParams
        
        val enterKey = createKey("ENTER", 1f)
        val kParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        enterKey.layoutParams = kParams
        
        enterContainer.addView(enterKey)
        bottomContainer.addView(enterContainer)
        
        addView(bottomContainer)
        
        addView(createRow(navRow, 5))
    }

    private fun createRow(keys: List<String>, rowIndex: Int): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        row.gravity = Gravity.CENTER
        row.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(keyHeight)).apply {
            setMargins(0, dpToPx(keySpacing), 0, 0)
        }
        if (rowIndex == 1) row.setPadding(dpToPx((12 * scaleFactor).toInt()), 0, dpToPx((12 * scaleFactor).toInt()), 0)
        
        for (key in keys) { 
            val weight = getKeyWeight(key, rowIndex)
            row.addView(createKey(key, weight)) 
        }
        return row
    }

    private fun getKeyWeight(key: String, rowIndex: Int): Float {
        if (rowIndex >= 4) return 1f 
        return when (key) {
            "SPACE" -> 4.0f
            "SHIFT", "BKSP" -> 1.5f
            "SYM", "SYM1", "SYM2", "ABC" -> 1.3f
            else -> 1f
        }
    }

    private fun createKey(key: String, weight: Float): View {
        val container = FrameLayout(context)
        val params = if (weight > 0) {
            LayoutParams(0, LayoutParams.MATCH_PARENT, weight)
        } else {
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        
        params.setMargins(dpToPx(keySpacing), 0, dpToPx(keySpacing), 0)
        container.layoutParams = params

        val keyView = TextView(context)
        keyView.gravity = Gravity.CENTER
        val rowFontSize = if (key in arrowRow || key in navRow) fontSize - 4 else fontSize
        keyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowFontSize)
        keyView.setTextColor(Color.WHITE)
        keyView.text = getDisplayText(key)

        val bg = GradientDrawable()
        bg.cornerRadius = dpToPx(6).toFloat()
        bg.setColor(getKeyColor(key))
        keyView.background = bg
        
        val viewParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        container.addView(keyView, viewParams)

        container.tag = key
        return container
    }

    // --- MULTITOUCH HANDLING ---

// =================================================================================
    // FUNCTION: dispatchTouchEvent
    // SUMMARY: Intercepts touch events to detect swipe/gesture typing. Key safeguards:
    //          1. Only tracks swipe for single-finger gestures (pointer index 0)
    //          2. Multitouch (second finger down) cancels any active swipe detection
    //          3. Requires minimum movement threshold AND minimum path distance
    //          4. Validates swipe has enough points and traveled enough distance
    //          This prevents false swipe triggers during fast two-thumb typing.
    // =================================================================================

    override fun dispatchTouchEvent(event: android.view.MotionEvent): Boolean {
        // --- 1. PREVENT SWIPE TRAIL ON SPACEBAR ---
        // If the touch starts on the SPACE key, we skip the swipe detection logic entirely.
        // This ensures we don't draw the green line or trigger the swipe decoder 
        // when the user is just trying to use the trackpad.
        if (event.actionMasked == android.view.MotionEvent.ACTION_DOWN) {
            val touchedView = findKeyView(event.x, event.y)
            if (touchedView?.tag == "SPACE") {
                // Pass directly to onTouchEvent (standard handling) and skip swipe logic
                return super.dispatchTouchEvent(event)
            }
        }

        // --- 2. EXISTING SWIPE / GESTURE LOGIC ---
        val superResult = super.dispatchTouchEvent(event)
        val action = event.actionMasked
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        when (action) {
            android.view.MotionEvent.ACTION_DOWN -> {
                // First finger down - initialize potential swipe tracking
                isSwiping = false
                swipePointerId = pointerId
                startTouchX = event.x
                startTouchY = event.y
                swipeTrail?.clear()
                swipeTrail?.addPoint(event.x, event.y)
                currentPath.clear()
                currentPath.add(android.graphics.PointF(event.x, event.y))
            }
            
            android.view.MotionEvent.ACTION_POINTER_DOWN -> {
                // Second finger touched - CANCEL swipe detection (user is typing with two thumbs)
                if (isSwiping) {
                    isSwiping = false
                    swipeTrail?.clear()
                    swipeTrail?.visibility = View.INVISIBLE
                    currentPath.clear()
                }
                swipePointerId = -1 // Disable swipe tracking for this gesture
            }
            
            android.view.MotionEvent.ACTION_MOVE -> {
                // Only track movement for the original swipe pointer
                if (swipePointerId == -1) return superResult
                
                // Find the index of our tracked pointer
                val trackedIndex = event.findPointerIndex(swipePointerId)
                if (trackedIndex == -1) return superResult
                
                val currentX = event.getX(trackedIndex)
                val currentY = event.getY(trackedIndex)
                
                if (!isSwiping) {
                    val dx = Math.abs(currentX - startTouchX)
                    val dy = Math.abs(currentY - startTouchY)
                    // Require movement in BOTH axes or significant movement in one
                    val totalMovement = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                    if (totalMovement > SWIPE_THRESHOLD) {
                        isSwiping = true
                        currentRepeatKey = null
                        repeatHandler.removeCallbacks(repeatRunnable)
                        swipeTrail?.visibility = View.VISIBLE
                    }
                }

                if (isSwiping) {
                    swipeTrail?.addPoint(currentX, currentY)
                    // Sample historical points for smoother path
                    if (event.historySize > 0) {
                        for (h in 0 until event.historySize) {
                            val hx = event.getHistoricalX(trackedIndex, h)
                            val hy = event.getHistoricalY(trackedIndex, h)
                            currentPath.add(android.graphics.PointF(hx, hy))
                        }
                    }
                    currentPath.add(android.graphics.PointF(currentX, currentY))
                }
            }
            
            android.view.MotionEvent.ACTION_UP -> {
                if (isSwiping && pointerId == swipePointerId) {
                    swipeTrail?.clear()
                    swipeTrail?.visibility = View.INVISIBLE

                    // Validate swipe before triggering decoder
                    val isValidSwipe = validateSwipe()
                    
                    if (isValidSwipe) {
                        // Send a copy of the path to decoder
                        listener?.onSwipeDetected(ArrayList(currentPath))
                    }

                    isSwiping = false
                    swipePointerId = -1
                    currentPath.clear()
                    return true
                }
                // Clean up even if this wasn't our tracked pointer
                swipeTrail?.clear()
                swipePointerId = -1
            }
            
            android.view.MotionEvent.ACTION_POINTER_UP -> {
                // One finger lifted but another still down - just clean up if it was our pointer
                if (pointerId == swipePointerId) {
                    isSwiping = false
                    swipePointerId = -1
                    swipeTrail?.clear()
                    swipeTrail?.visibility = View.INVISIBLE
                    currentPath.clear()
                }
            }
            
            android.view.MotionEvent.ACTION_CANCEL -> {
                isSwiping = false
                swipePointerId = -1
                swipeTrail?.clear()
                swipeTrail?.visibility = View.INVISIBLE
                currentPath.clear()
            }
        }
        return superResult
    }


    // =================================================================================
    // FUNCTION: validateSwipe
    // SUMMARY: Checks if the recorded path qualifies as a valid swipe gesture.
    //          Requirements: minimum number of points AND minimum start-to-end distance.
    //          This filters out short taps that accumulated a few points from finger wobble.
    // =================================================================================
    private fun validateSwipe(): Boolean {
        if (currentPath.size < SWIPE_MIN_PATH_POINTS) {
            return false
        }
        
        // Check start-to-end distance (not total path length)
        val startPt = currentPath.firstOrNull() ?: return false
        val endPt = currentPath.lastOrNull() ?: return false
        val dx = endPt.x - startPt.x
        val dy = endPt.y - startPt.y
        val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        
        return distance >= SWIPE_MIN_DISTANCE
    }
    // =================================================================================
    // END BLOCK: dispatchTouchEvent and validateSwipe
    // =================================================================================





    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        val x = event.getX(pointerIndex)
        val y = event.getY(pointerIndex)

        val touchedView = findKeyView(x, y)
        val keyTag = touchedView?.tag as? String

        // --- SPACEBAR TRACKPAD HANDLING ---
        if ((keyTag == "SPACE" && action == MotionEvent.ACTION_DOWN) || spacebarPointerId == pointerId) {
            when (action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    if (keyTag == "SPACE") {
                        spacebarPointerId = pointerId
                        lastSpaceX = x
                        lastSpaceY = y
                        isSpaceTrackpadActive = false
                        // Visual feedback only
                        if (touchedView != null) setKeyVisual(touchedView, true, "SPACE")
                        return true
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (pointerId == spacebarPointerId) {
                        val dx = x - lastSpaceX
                        val dy = y - lastSpaceY
                        
                        // Detect Drag vs Tap
                        if (!isSpaceTrackpadActive) {
                            if (kotlin.math.hypot(dx, dy) > touchSlop) {
                                isSpaceTrackpadActive = true
                                performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            }
                        }
                        
                        if (isSpaceTrackpadActive) {
                            moveMouse(dx, dy)
                            lastSpaceX = x
                            lastSpaceY = y
                        }
                        return true
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    if (pointerId == spacebarPointerId) {
                        // Reset Visual
                        // We search for space view again just to be safe
                        val spaceView = if (touchedView?.tag == "SPACE") touchedView else findViewWithTag("SPACE")
                        if (spaceView != null) setKeyVisual(spaceView, false, "SPACE")

                        if (!isSpaceTrackpadActive) {
                            // TAP DETECTED
                            if (isPredictiveBarEmpty) {
                                // --- MOUSE CLICK MODE ---
                                var keyLeft = 0f
                                if (touchedView != null) {
                                    val row = touchedView.parent as? View
                                    if (row != null) keyLeft = row.x + touchedView.x
                                }
                                val relativeX = x - keyLeft
                                val width = touchedView?.width ?: 100
                                handleSpacebarClick(relativeX, width)
                            } else {
                                // --- NORMAL SPACE INPUT ---
                                listener?.onSpecialKey(SpecialKey.SPACE, 0)
                                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            }
                        }
                        spacebarPointerId = -1
                        isSpaceTrackpadActive = false
                        return true
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (pointerId == spacebarPointerId) {
                        spacebarPointerId = -1
                        isSpaceTrackpadActive = false
                        val spaceView = findViewWithTag<View>("SPACE")
                        if (spaceView != null) setKeyVisual(spaceView, false, "SPACE")
                        return true
                    }
                }
            }
        }

        // --- STANDARD KEYBOARD HANDLING (Fixes Stuck Highlights) ---
        // We track the active key and update it as the finger slides.
        
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (touchedView != null && keyTag != null && keyTag != "SPACE") {
                    currentActiveKey = touchedView
                    onKeyDown(keyTag, touchedView)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // If we slid to a new key
                if (touchedView != currentActiveKey) {
                    // Deactivate old
                    currentActiveKey?.let { 
                        val oldTag = it.tag as? String
                        if (oldTag != null) setKeyVisual(it, false, oldTag) // Just visual off, don't commit input on slide-off
                    }
                    
                    // Activate new
                    if (touchedView != null && keyTag != null && keyTag != "SPACE") {
                        currentActiveKey = touchedView
                        onKeyDown(keyTag, touchedView) // Visual on + Haptic
                    } else {
                        currentActiveKey = null
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                currentActiveKey?.let {
                    val tag = it.tag as? String
                    if (tag != null) {
                        // Commit the input
                        onKeyUp(tag, it)
                    }
                }
                currentActiveKey = null
            }
            MotionEvent.ACTION_CANCEL -> {
                currentActiveKey?.let {
                    val tag = it.tag as? String
                    if (tag != null) setKeyVisual(it, false, tag)
                }
                currentActiveKey = null
            }
        }
        
        return true
    }





    private fun findKeyView(targetX: Float, targetY: Float): View? {
        return findKeyRecursively(this, targetX, targetY)
    }

    private fun findKeyRecursively(parent: ViewGroup, targetX: Float, targetY: Float): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.visibility != View.VISIBLE) continue
            val cx = child.x
            val cy = child.y
            if (targetX >= cx && targetX < cx + child.width && targetY >= cy && targetY < cy + child.height) {
                if (child.tag != null) return child
                if (child is ViewGroup) return findKeyRecursively(child, targetX - cx, targetY - cy)
            }
        }
        return null
    }

// =================================================================================
    // FUNCTION: onKeyDown
    // SUMMARY: Handles initial touch on a key. For swipe-compatible keys (single letters),
    //          we ONLY provide visual/haptic feedback here. The actual character input is
    //          deferred to onKeyUp to prevent double-letters during swipe typing.
    //          Special/modifier keys still trigger immediately for responsiveness.
    // =================================================================================
    private fun onKeyDown(key: String, view: View) {
        setKeyVisual(view, true, key)
        
        // Always provide haptic feedback on touch
        if (vibrationEnabled) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else { @Suppress("DEPRECATION") v?.vibrate(30) }
        }
        
        // Determine if this key should fire on DOWN (immediate) or UP (deferred for swipe)
        val isSwipeableKey = key.length == 1 && Character.isLetter(key[0])
        
        if (!isSwipeableKey) {
            // NON-SWIPEABLE KEYS: Fire immediately on down (for responsiveness)
            // This includes: BKSP, SPACE, ENTER, arrows, modifiers, symbols, numbers
            handleKeyPress(key, fromRepeat = false)
            
            // Start repeat timer for repeatable keys
            if (isKeyRepeatable(key)) {
                currentRepeatKey = key
                isRepeating = true
                repeatHandler.postDelayed(repeatRunnable, REPEAT_INITIAL_DELAY)
            }
        }
        // SWIPEABLE KEYS (letters): Do NOT fire here - wait for onKeyUp
        // This prevents double-letters when starting a swipe
        
        // SHIFT special handling (Caps Lock detection) - always runs
        if (key == "SHIFT") {
            capsLockPending = false
            capsHandler.postDelayed(capsLockRunnable, 500)
        }
    }
    // =================================================================================
    // END BLOCK: onKeyDown
    // =================================================================================

// =================================================================================
    // FUNCTION: onKeyUp
    // SUMMARY: Handles key release. For swipe-compatible keys (single letters), this is
    //          where we actually commit the character - BUT ONLY if we're not currently
    //          in a swipe gesture. This prevents double letters with swipe typing.
    //          Also handles SHIFT toggle and repeat cancellation.
    // =================================================================================
    private fun onKeyUp(key: String, view: View) {
        setKeyVisual(view, false, key)
        
        // Stop any active key repeat
        if (key == currentRepeatKey) stopRepeat()
        
        // Determine if this is a swipeable key that was deferred
        val isSwipeableKey = key.length == 1 && Character.isLetter(key[0])
        
        if (isSwipeableKey && !isSwiping) {
            // SWIPEABLE KEY + NOT SWIPING = Normal tap, commit the character now
            handleKeyPress(key, fromRepeat = false)
        }
        // If isSwiping is true, the swipe handler will take care of text input
        // so we don't commit anything here to avoid double letters
        
        // SHIFT toggle handling
        if (key == "SHIFT") {
            capsHandler.removeCallbacks(capsLockRunnable)
            if (!capsLockPending) toggleShift()
            capsLockPending = false
        }
    }
    // =================================================================================
    // END BLOCK: onKeyUp
    // =================================================================================

    private fun setKeyVisual(container: View, pressed: Boolean, key: String) {
        val tv = (container as? ViewGroup)?.getChildAt(0) as? TextView ?: return
        val bg = tv.background as? GradientDrawable ?: return
        if (pressed) bg.setColor(Color.parseColor("#3DDC84")) else bg.setColor(getKeyColor(key))
    }

    private fun stopRepeat() {
        isRepeating = false
        currentRepeatKey = null
        repeatHandler.removeCallbacks(repeatRunnable)
    }

    private fun isKeyRepeatable(key: String): Boolean {
        if (alwaysRepeatable.contains(key)) return true
        if (key.length == 1) return true
        return false
    }

    private fun getDisplayText(key: String): String = when (key) {
        "SHIFT" -> if (currentState == KeyboardState.CAPS_LOCK) "⬆" else "⇧"
        "BKSP" -> "⌫"; "ENTER" -> "↵"; "SPACE" -> " "
        "SYM", "SYM1", "SYM2" -> "?123"; "ABC" -> "ABC"
        "TAB" -> "⇥"; "CTRL" -> "Ctrl"; "ALT" -> "Alt"; "ESC" -> "Esc"
        "←" -> "◀"; "→" -> "▶"; "↑" -> "▲"; "↓" -> "▼"
        "MUTE" -> "Mute"; "VOL-" -> "Vol-"; "VOL+" -> "Vol+"
        "BACK" -> "Back"; "FWD" -> "Fwd"; "MIC" -> "🎤"
        "SCREEN" -> if (isSymbolsActive()) "MODE" else "📺"
        "HIDE_KB" -> "▼"
        else -> key
    }

    private fun getKeyColor(key: String): Int {
        if (key == "CTRL" && isCtrlActive) return Color.parseColor("#3DDC84")
        if (key == "ALT" && isAltActive) return Color.parseColor("#3DDC84")
        
        // NEW: Voice Active Indicator
        // UPDATED: Voice Key Color
        // Green if active, standard dark gray if inactive (removed red alert color)
        if (key == "MIC") {
            return if (isVoiceActive) Color.parseColor("#3DDC84") else Color.parseColor("#252525")
        }

        if (key == "SCREEN") {
            return if (isSymbolsActive()) Color.parseColor("#FF9800") else Color.parseColor("#FF5555")
        }
        
        if (key in arrowRow || key in navRow) return Color.parseColor("#252525")
        
        return when (key) {
            "SHIFT" -> when (currentState) {
                KeyboardState.CAPS_LOCK -> Color.parseColor("#3DDC84")
                KeyboardState.UPPERCASE -> Color.parseColor("#4A90D9")
                else -> Color.parseColor("#3A3A3A")
            }
            "ENTER" -> Color.parseColor("#4A90D9")
            "BKSP", "SYM", "SYM1", "SYM2", "ABC" -> Color.parseColor("#3A3A3A")
            "SPACE" -> Color.parseColor("#2D2D2D")
            else -> Color.parseColor("#2D2D2D")
        }
    }
    
    private fun isSymbolsActive(): Boolean {
        return currentState == KeyboardState.SYMBOLS_1 || currentState == KeyboardState.SYMBOLS_2
    }

    private fun getMetaState(): Int {
        var meta = 0
        if (isCtrlActive) meta = meta or 0x1000 
        if (isAltActive) meta = meta or 0x02 
        return meta
    }

    private fun handleKeyPress(key: String, fromRepeat: Boolean = false) {
        var meta = getMetaState()
        if (fromRepeat && !isKeyRepeatable(key)) return

        when (key) {
            "CTRL" -> { if (!fromRepeat) { isCtrlActive = !isCtrlActive; buildKeyboard() } }
            "ALT" -> { if (!fromRepeat) { isAltActive = !isAltActive; buildKeyboard() } }
            "SHIFT" -> { /* Handled in onKeyUp/Down */ }
            
            "BKSP" -> listener?.onSpecialKey(SpecialKey.BACKSPACE, meta)
            "ENTER" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.ENTER, meta) }
            "SPACE" -> listener?.onSpecialKey(SpecialKey.SPACE, meta)
            "TAB" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.TAB, meta) }
            "ESC" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.ESCAPE, meta) }
            
            "←" -> listener?.onSpecialKey(SpecialKey.ARROW_LEFT, meta)
            "→" -> listener?.onSpecialKey(SpecialKey.ARROW_RIGHT, meta)
            "↑" -> listener?.onSpecialKey(SpecialKey.ARROW_UP, meta)
            "↓" -> listener?.onSpecialKey(SpecialKey.ARROW_DOWN, meta)
            
            "MUTE" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.MUTE, meta) }
            "VOL-" -> listener?.onSpecialKey(SpecialKey.VOL_DOWN, meta)
            "VOL+" -> listener?.onSpecialKey(SpecialKey.VOL_UP, meta)
            "BACK" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.BACK_NAV, meta) }
            "FWD" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.FWD_NAV, meta) }
            "MIC" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.VOICE_INPUT, meta) }
            
            "SCREEN" -> { 
                if (!fromRepeat) {
                    if (isSymbolsActive()) {
                        listener?.onScreenModeChange()
                    } else {
                        listener?.onScreenToggle()
                    }
                }
            }
            "HIDE_KB" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.HIDE_KEYBOARD, meta) }
            
            "SYM", "SYM1" -> { if (!fromRepeat) { currentState = KeyboardState.SYMBOLS_1; buildKeyboard() } }
            "SYM2" -> { if (!fromRepeat) { currentState = KeyboardState.SYMBOLS_2; buildKeyboard() } }
            "ABC" -> { if (!fromRepeat) { currentState = KeyboardState.LOWERCASE; buildKeyboard() } }
            
            else -> {
                if (key.length == 1) {
                    val char = key[0]
                    val pair = getSymbolKeyCode(char)
                    val code = pair.first
                    val shiftNeeded = pair.second
                    if (shiftNeeded) meta = meta or KeyEvent.META_SHIFT_ON
                    listener?.onKeyPress(code, char, meta)
                    if (!fromRepeat && currentState == KeyboardState.UPPERCASE) { 
                        currentState = KeyboardState.LOWERCASE
                        buildKeyboard()
                    }
                }
            }
        }
        if (!fromRepeat && key != "CTRL" && key != "ALT" && key != "SHIFT") {
            if (isCtrlActive || isAltActive) {
                isCtrlActive = false; isAltActive = false; buildKeyboard()
            }
        }
    }

    private fun getSymbolKeyCode(c: Char): Pair<Int, Boolean> {
        return when (c) {
            in 'a'..'z' -> KeyEvent.keyCodeFromString("KEYCODE_${c.uppercase()}") to false
            in 'A'..'Z' -> KeyEvent.keyCodeFromString("KEYCODE_${c}") to true
            in '0'..'9' -> KeyEvent.keyCodeFromString("KEYCODE_${c}") to false
            ' ' -> KeyEvent.KEYCODE_SPACE to false
            '.' -> KeyEvent.KEYCODE_PERIOD to false
            ',' -> KeyEvent.KEYCODE_COMMA to false
            ';' -> KeyEvent.KEYCODE_SEMICOLON to false
            ':' -> KeyEvent.KEYCODE_SEMICOLON to true
            '=' -> KeyEvent.KEYCODE_EQUALS to false
            '+' -> KeyEvent.KEYCODE_PLUS to false
            '-' -> KeyEvent.KEYCODE_MINUS to false
            '_' -> KeyEvent.KEYCODE_MINUS to true
            '/' -> KeyEvent.KEYCODE_SLASH to false
            '?' -> KeyEvent.KEYCODE_SLASH to true
            '`' -> KeyEvent.KEYCODE_GRAVE to false
            '~' -> KeyEvent.KEYCODE_GRAVE to true
            '[' -> KeyEvent.KEYCODE_LEFT_BRACKET to false
            '{' -> KeyEvent.KEYCODE_LEFT_BRACKET to true
            ']' -> KeyEvent.KEYCODE_RIGHT_BRACKET to false
            '}' -> KeyEvent.KEYCODE_RIGHT_BRACKET to true
            '\\' -> KeyEvent.KEYCODE_BACKSLASH to false
            '|' -> KeyEvent.KEYCODE_BACKSLASH to true
            '\'' -> KeyEvent.KEYCODE_APOSTROPHE to false
            '"' -> KeyEvent.KEYCODE_APOSTROPHE to true
            '!' -> KeyEvent.KEYCODE_1 to true
            '@' -> KeyEvent.KEYCODE_2 to true
            '#' -> KeyEvent.KEYCODE_3 to true
            '$' -> KeyEvent.KEYCODE_4 to true
            '%' -> KeyEvent.KEYCODE_5 to true
            '^' -> KeyEvent.KEYCODE_6 to true
            '&' -> KeyEvent.KEYCODE_7 to true
            '*' -> KeyEvent.KEYCODE_8 to true
            '(' -> KeyEvent.KEYCODE_9 to true
            ')' -> KeyEvent.KEYCODE_0 to true
            '√' -> KeyEvent.KEYCODE_UNKNOWN to false // Filler
            else -> KeyEvent.KEYCODE_UNKNOWN to false
        }
    }

    private fun toggleShift() {
        currentState = when (currentState) {
            KeyboardState.LOWERCASE -> KeyboardState.UPPERCASE
            KeyboardState.UPPERCASE -> KeyboardState.LOWERCASE
            KeyboardState.CAPS_LOCK -> KeyboardState.LOWERCASE
            else -> currentState
        }
        buildKeyboard()
    }

    private fun toggleCapsLock() {
        currentState = when (currentState) {
            KeyboardState.LOWERCASE, KeyboardState.UPPERCASE -> KeyboardState.CAPS_LOCK
            KeyboardState.CAPS_LOCK -> KeyboardState.LOWERCASE
            else -> currentState
        }
        if (vibrationEnabled) {
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else { @Suppress("DEPRECATION") v?.vibrate(50) }
        }
        buildKeyboard()
    }

    private fun dpToPx(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
    ).roundToInt()

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopRepeat()
    }
}
