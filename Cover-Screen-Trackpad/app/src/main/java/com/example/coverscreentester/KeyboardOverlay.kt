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

    // =================================================================================
    // VIRTUAL MIRROR ORIENTATION MODE VARIABLES
    // SUMMARY: State for orientation mode when virtual mirror is active.
    //          During orientation mode, an orange trail is shown and key input is blocked
    //          until the finger stops moving for the configured delay.
    // =================================================================================
    private var isOrientationModeActive = false
    private var orientationTrailView: SwipeTrailView? = null
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR ORIENTATION MODE VARIABLES
    // =================================================================================

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

    // =================================================================================
    // VIRTUAL MIRROR CALLBACK
    // SUMMARY: Callback to forward touch events to OverlayService for mirror sync.
    //          Returns true if touch should be consumed (orientation mode active).
    // =================================================================================
    var onMirrorTouch: ((Float, Float, Int) -> Boolean)? = null // x, y, action -> consumed
    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR CALLBACK
    // =================================================================================

    // Layer change callback for syncing mirror keyboard
    var onLayerChanged: ((KeyboardView.KeyboardState) -> Unit)? = null

    // =================================================================================
    // CALLBACK: onSuggestionsChanged
    // SUMMARY: Called whenever the suggestion bar is updated. Used to sync mirror keyboard.
    // =================================================================================
    var onSuggestionsChanged: ((List<KeyboardView.Candidate>) -> Unit)? = null
    var onSizeChanged: (() -> Unit)? = null
    // =================================================================================
    // END BLOCK: onSuggestionsChanged
    // =================================================================================



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
    fun getKeyboardView(): KeyboardView? = keyboardView

    
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

            // --- FIX: Strict Aspect Ratio Sync ---
            // We force the Window Height to be exactly 55% of the Width.
            // This guarantees that the Physical Keyboard and Mirror Keyboard 
            // have the exact same shape, ensuring the Touch Trail aligns perfectly.
            keyboardContainer?.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
                val width = right - left
                val height = bottom - top
                
                if (width > 0) {
                    // Ratio 0.55 (Keys are slightly taller than square to fit text comfortably)
                    // 10 keys wide, 5 rows tall. 0.5 would be square. 0.55 prevents cutoff.
                    val targetHeight = (width * 0.55f).toInt()
                    
                    if (kotlin.math.abs(height - targetHeight) > 5) {
                        keyboardParams?.height = targetHeight
                        try { 
                            windowManager.updateViewLayout(keyboardContainer, keyboardParams) 
                        } catch (e: Exception) { }
                    }
                }
            }

            // Set initial params to match logic
            if (keyboardParams?.width ?: 0 > 0) {
                 keyboardParams?.height = ((keyboardParams?.width ?: 100) * 0.55f).toInt()
            }

            isVisible = true
            if (currentRotation != 0) setRotation(currentRotation)
        } catch (e: Exception) { android.util.Log.e("KeyboardOverlay", "Failed to show keyboard", e) } 
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

    // =================================================================================
    // VIRTUAL MIRROR ORIENTATION MODE METHODS
    // SUMMARY: Methods for managing orientation mode during virtual mirror operation.
    //          These handle the orange trail that helps users locate their finger
    //          position on the physical keyboard without looking at the screen.
    // =================================================================================

    // =================================================================================
    // FUNCTION: setOrientationMode
    // SUMMARY: Enables or disables orientation mode. When enabled, an orange trail
    //          is shown and key input is blocked until the mode ends.
    // @param active - true to enable orientation mode, false to disable
    // =================================================================================
    fun setOrientationMode(active: Boolean) {
        isOrientationModeActive = active
        keyboardView?.setOrientationModeActive(active)

        if (!active) {
            // Clear the orange trail when exiting orientation mode
            orientationTrailView?.clear()
        }
    }
    // =================================================================================
    // END BLOCK: setOrientationMode
    // =================================================================================

    // =================================================================================
    // FUNCTION: startOrientationTrail
    // SUMMARY: Starts a new orange orientation trail at the specified position.
    // @param x - Starting X coordinate
    // @param y - Starting Y coordinate
    // =================================================================================
    fun startOrientationTrail(x: Float, y: Float) {
        orientationTrailView?.clear()
        orientationTrailView?.addPoint(x, y)
    }
    // =================================================================================
    // END BLOCK: startOrientationTrail
    // =================================================================================

    // =================================================================================
    // FUNCTION: addOrientationTrailPoint
    // SUMMARY: Adds a point to the orange orientation trail.
    // @param x - X coordinate of new point
    // @param y - Y coordinate of new point
    // =================================================================================
    fun addOrientationTrailPoint(x: Float, y: Float) {
        orientationTrailView?.addPoint(x, y)
    }
    // =================================================================================
    // END BLOCK: addOrientationTrailPoint
    // =================================================================================

    // =================================================================================
    // FUNCTION: clearOrientationTrail
    // SUMMARY: Clears the orange orientation trail.
    // =================================================================================
    fun clearOrientationTrail() {
        orientationTrailView?.clear()
    }
    // =================================================================================
    // END BLOCK: clearOrientationTrail
    // =================================================================================

    // =================================================================================
    // FUNCTION: setOrientationTrailColor
    // SUMMARY: Sets the color of the orientation trail on the physical display.
    // =================================================================================
    fun setOrientationTrailColor(color: Int) {
        orientationTrailView?.setTrailColor(color)
    }
    // =================================================================================
    // END BLOCK: setOrientationTrailColor
    // =================================================================================

    // =================================================================================
    // FUNCTION: startSwipeFromCurrentPosition
    // SUMMARY: Called when switching from orange to blue trail mid-gesture.
    //          Initializes swipe tracking so the path starts from the given position.
    // =================================================================================
    fun startSwipeFromCurrentPosition(x: Float, y: Float) {
        keyboardView?.startSwipeFromPosition(x, y)
    }
    // =================================================================================
    // END BLOCK: startSwipeFromCurrentPosition
    // =================================================================================

    // =================================================================================
    // FUNCTION: handleDeferredTap
    // SUMMARY: Forwards deferred tap to KeyboardView for single key press in mirror mode.
    // =================================================================================

    fun handleDeferredTap(x: Float, y: Float) {
        // CRITICAL: If a tap is detected, immediately stop any pending repeat logic.
        // This prevents the "stuck" state where the system thinks you are still holding the key.
        stopMirrorRepeat()
        keyboardView?.handleDeferredTap(x, y)
    }
// =================================================================================
    // FUNCTION: getKeyAtPosition
    // SUMMARY: Returns the key tag at the given position, or null if no key found.
    //          Used by mirror mode to check if finger is on a repeatable key.
    // =================================================================================
    fun getKeyAtPosition(x: Float, y: Float): String? {
        return keyboardView?.getKeyAtPosition(x, y)
    }
    // =================================================================================
    // END BLOCK: getKeyAtPosition
    // =================================================================================

    // =================================================================================

    // =================================================================================
    // FUNCTION: triggerKeyPress (Updated with Tap-Reset Fix)
    // SUMMARY: Triggers a key press by key tag for Mirror Mode.
    //          Includes 400ms initial delay + Watchdog timeout.
    //          Now robustly resets if the sequence is broken.
    // =================================================================================
    private var activeRepeatKey: String? = null
    private var lastMirrorKeyTime = 0L
    private val mirrorRepeatHandler = Handler(Looper.getMainLooper())
    private val REPEAT_START_DELAY = 400L
    private val REPEAT_INTERVAL = 50L 
    
    // Watchdog: If no input received for 150ms, assume key was released
    // Increased to 150ms to be more tolerant of input jitters
    private val MIRROR_INPUT_TIMEOUT = 150L 

    private val mirrorRepeatRunnable = object : Runnable {
        override fun run() {
            val key = activeRepeatKey ?: return
            val now = System.currentTimeMillis()
            
            // Watchdog Check
            if (now - lastMirrorKeyTime > MIRROR_INPUT_TIMEOUT) {
                stopMirrorRepeat()
                return
            }

            // Fire event
            keyboardView?.triggerKeyPress(key)
            mirrorRepeatHandler.postDelayed(this, REPEAT_INTERVAL)
        }
    }

    // Changed from private to private-but-accessible-internally (or keep private if handleDeferredTap is in same class)
    private fun stopMirrorRepeat() {
        activeRepeatKey = null
        mirrorRepeatHandler.removeCallbacks(mirrorRepeatRunnable)
    }


    fun blockPrediction(index: Int) {
        keyboardView?.blockPredictionAtIndex(index)
    }


    fun triggerKeyPress(keyTag: String) {
        val isRepeatable = keyTag in setOf("BKSP", "DEL", "◄", "▲", "▼", "►", "←", "↑", "↓", "→", "VOL+", "VOL-", "VOL_UP", "VOL_DOWN")

        if (!isRepeatable) {
            stopMirrorRepeat()
            keyboardView?.triggerKeyPress(keyTag)
            return
        }

        val now = System.currentTimeMillis()

        if (keyTag == activeRepeatKey) {
            // Update watchdog time
            lastMirrorKeyTime = now
            
            // ROBUSTNESS FIX: If for some reason the handler isn't running (race condition),
            // restart it to ensure we don't get stuck in a silent state.
            if (!mirrorRepeatHandler.hasCallbacks(mirrorRepeatRunnable)) {
                mirrorRepeatHandler.postDelayed(mirrorRepeatRunnable, REPEAT_START_DELAY)
            }
        } else {
            // New Key Sequence
            stopMirrorRepeat()
            
            activeRepeatKey = keyTag
            lastMirrorKeyTime = now
            
            // Start Delay (Wait 400ms before first fire)
            mirrorRepeatHandler.postDelayed(mirrorRepeatRunnable, REPEAT_START_DELAY)
        }
    }
   // FUNCTION: getKeyboardState
    // SUMMARY: Gets current keyboard state (layer) from KeyboardView.
    // =================================================================================
    fun getKeyboardState(): KeyboardView.KeyboardState? {
        return keyboardView?.getKeyboardState()
    }

    // =================================================================================
    // FUNCTION: setKeyboardState
    // SUMMARY: Sets keyboard state (layer) in KeyboardView.
    // =================================================================================
    fun setKeyboardState(state: KeyboardView.KeyboardState) {
        keyboardView?.setKeyboardState(state)
    }

    // =================================================================================
    // FUNCTION: getCtrlAltState
    // SUMMARY: Gets current Ctrl/Alt modifier states from KeyboardView.
    // =================================================================================
    fun getCtrlAltState(): Pair<Boolean, Boolean>? {
        return keyboardView?.getCtrlAltState()
    }

    // =================================================================================
    // FUNCTION: setCtrlAltState
    // SUMMARY: Sets Ctrl/Alt modifier states in KeyboardView.
    // =================================================================================
    fun setCtrlAltState(ctrl: Boolean, alt: Boolean) {
        keyboardView?.setCtrlAltState(ctrl, alt)
    }
    // =================================================================================
    // END BLOCK: State accessor functions for mirror sync
    // =================================================================================

    // =================================================================================
    // FUNCTION: updateSuggestionsWithSync
    // SUMMARY: Sets suggestions on the keyboard view AND notifies callback for mirror sync.
    // =================================================================================
    private fun updateSuggestionsWithSync(candidates: List<KeyboardView.Candidate>) {
        keyboardView?.setSuggestions(candidates)
        onSuggestionsChanged?.invoke(candidates)
    }
    // =================================================================================
    // END BLOCK: updateSuggestionsWithSync
    // =================================================================================

    // =================================================================================
    // FUNCTION: isInOrientationMode
    // SUMMARY: Returns whether orientation mode is currently active.
    // @return true if orientation mode is active
    // =================================================================================
    fun isInOrientationMode(): Boolean {
        return isOrientationModeActive
    }
    // =================================================================================
    // END BLOCK: isInOrientationMode
    // =================================================================================

    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR ORIENTATION MODE METHODS
    // =================================================================================

    fun moveWindow(dx: Int, dy: Int) {
        if (!isVisible || keyboardParams == null) return
        keyboardParams!!.x += dx; keyboardParams!!.y += dy
        try { 
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
            saveKeyboardPosition()
            onSizeChanged?.invoke()
        } catch (e: Exception) {}
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
         
         try { 
             windowManager.updateViewLayout(keyboardContainer, keyboardParams)
             saveKeyboardSize()
             onSizeChanged?.invoke()
         } catch (e: Exception) {}
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

        // =================================================================================
        // VIRTUAL MIRROR MODE - WIRE CALLBACK DIRECTLY TO KEYBOARDVIEW
        // SUMMARY: The only way to intercept touches before KeyboardView processes them
        //          is to have KeyboardView call us directly. Container listeners don't
        //          work because child views receive touches first.
        // =================================================================================
        keyboardView?.mirrorTouchCallback = { x, y, action ->
            val cb = onMirrorTouch
            if (cb != null) {
                cb.invoke(x, y, action)
            } else {
                false
            }
        }
        // =================================================================================
        // END BLOCK: VIRTUAL MIRROR MODE - WIRE CALLBACK
        // =================================================================================


        

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

        // =================================================================================
        // ORIENTATION TRAIL VIEW (for virtual mirror mode)
        // SUMMARY: A separate trail view for orange orientation trails. Layered on top
        //          of the normal blue swipe trail so both can be visible simultaneously
        //          if needed. Normally invisible; only shows during orientation mode.
        // =================================================================================
        orientationTrailView = SwipeTrailView(context)
        orientationTrailView?.setTrailColor(0xFFFF9900.toInt()) // Orange color
        val orientTrailParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        orientTrailParams.setMargins(6, 28, 6, 6)
        keyboardContainer?.addView(orientationTrailView, orientTrailParams)
        // =================================================================================
        // END BLOCK: ORIENTATION TRAIL VIEW
        // =================================================================================

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
                // DISABLE AUTO-LEARN
                // if (word.length >= 2) predictionEngine.learnWord(context, word)
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
                // DISABLE AUTO-LEARN
                // if (word.length >= 2) predictionEngine.learnWord(context, word)
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
    // SUMMARY: Handles when user taps a word in the prediction bar.
    //          SCENARIO 1: Swipe Correction (Replaces last committed word)
    //          SCENARIO 2: Manual Typing (Replaces current composing characters)
    // =================================================================================
    override fun onSuggestionClick(text: String, isNew: Boolean) {
        android.util.Log.d("DroidOS_Prediction", "Suggestion clicked: '$text' (isNew=$isNew)")

        // 1. Learn word if it was flagged as New
        if (isNew) {
            predictionEngine.learnWord(context, text)
        }

        // 2. Handle Deletion (Key Injection)
        if (!lastCommittedSwipeWord.isNullOrEmpty()) {
            // SCENARIO 1: Correcting a previously swiped word
            // We must delete the full word + the space we added
            val deleteCount = lastCommittedSwipeWord!!.length
            for (i in 0 until deleteCount) {
                injectKey(KeyEvent.KEYCODE_DEL, 0)
            }
        } else if (currentComposingWord.isNotEmpty()) {
            // SCENARIO 2: Completing a manually typed word (e.g. "partia" -> "partially")
            // We delete the characters typed so far
            val deleteCount = currentComposingWord.length
            for (i in 0 until deleteCount) {
                injectKey(KeyEvent.KEYCODE_DEL, 0)
            }
        }

        // 3. Insert new word (always add space for flow)
        val newText = "$text "
        injectText(newText)
        
        // 4. Update State
        lastCommittedSwipeWord = newText
        currentComposingWord.clear() // Reset manual typing state
        
        // Clear suggestions immediately since we just committed
        updateSuggestionsWithSync(emptyList()) 
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

    // Layer change notification for mirror keyboard sync
    override fun onLayerChanged(state: KeyboardView.KeyboardState) {
        onLayerChanged?.invoke(state)
    }

    // =================================================================================
    // FUNCTION: onSwipeDetected
    // SUMMARY: Handles swipe gesture completion. Runs decoding in background thread.
    //          OPTIMIZED: Reduced logging for better performance.
    // =================================================================================
    override fun onSwipeDetected(path: List<android.graphics.PointF>) {
        if (keyboardView == null) return
        
        val keyMap = keyboardView?.getKeyCenters()
        if (keyMap.isNullOrEmpty()) return

        Thread {
            try {
                val suggestions = predictionEngine.decodeSwipe(path, keyMap)

                if (suggestions.isNotEmpty()) {
                    handler.post {
                        var bestMatch = suggestions[0]
                        if (isSentenceStart) {
                            bestMatch = bestMatch.replaceFirstChar { 
                                if (it.isLowerCase()) it.titlecase() else it.toString() 
                            }
                        }

                        val isCap = Character.isUpperCase(bestMatch.firstOrNull() ?: ' ')
                        val displaySuggestions = if (isCap) {
                            suggestions.map { it.replaceFirstChar { c -> c.titlecase() } }
                        } else {
                            suggestions
                        }.map { KeyboardView.Candidate(it, isNew = false) }

                        updateSuggestionsWithSync(displaySuggestions)

                        // Commit text with proper spacing
                        var textToCommit = bestMatch
                        if (currentComposingWord.isNotEmpty()) {
                            textToCommit = " $bestMatch"
                            currentComposingWord.clear()
                        }
                        textToCommit = "$textToCommit "

                        injectText(textToCommit)
                        lastCommittedSwipeWord = textToCommit
                        isSentenceStart = false
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DroidOS_Swipe", "Swipe decode error: ${e.message}")
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: onSwipeDetected
    // =================================================================================

    // =================================================================================
    // FUNCTION: onSwipeProgress (LIVE SWIPE PREVIEW)
    // SUMMARY: Called during swipe to show real-time predictions as user swipes.
    //          Uses a lightweight prediction that doesn't commit text.
    //          This helps users see what word will be typed and helps debug.
    // =================================================================================
// =================================================================================
    // FUNCTION: onSwipeProgress (LIVE SWIPE PREVIEW - Single Prediction)
    // SUMMARY: Called during swipe to show real-time prediction as user swipes.
    //          Shows ONLY the top prediction (like GBoard) for cleaner UX.
    //          Full suggestions shown on swipe completion in onSwipeDetected.
    // =================================================================================
    override fun onSwipeProgress(path: List<android.graphics.PointF>) {
        if (keyboardView == null || path.size < 5) return
        
        val keyMap = keyboardView?.getKeyCenters()
        if (keyMap.isNullOrEmpty()) return

        // Run prediction in background to avoid UI lag
        Thread {
            try {
                val suggestions = predictionEngine.decodeSwipePreview(path, keyMap)

                if (suggestions.isNotEmpty()) {
                    handler.post {
                        // Get only the TOP prediction
                        var topPrediction = suggestions[0]
                        
                        // Apply capitalization if at sentence start
                        if (isSentenceStart) {
                            topPrediction = topPrediction.replaceFirstChar { 
                                if (it.isLowerCase()) it.titlecase() else it.toString() 
                            }
                        }

                        // Show ONLY the top prediction (single item list)
                        val singleSuggestion = listOf(KeyboardView.Candidate(topPrediction, isNew = false))
                        updateSuggestionsWithSync(singleSuggestion)
                    }
                }
            } catch (e: Exception) {
                // Silently ignore errors during preview
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: onSwipeProgress
    // =================================================================================
    // =================================================================================
    // END BLOCK: onSwipeProgress
    // =================================================================================

    // =================================================================================
    // FUNCTION: updateSuggestions
    // SUMMARY: Updates the suggestion bar based on current composing word.
    //          Shows raw input + dictionary suggestions, filtering blocked words.
    // =================================================================================
    private fun updateSuggestions() {
        val prefix = currentComposingWord.toString()
        if (prefix.isEmpty()) {
            updateSuggestionsWithSync(emptyList())
            return
        }


        // 1. Get dictionary suggestions (already filtered for blocked words)
        val suggestions = predictionEngine.getSuggestions(prefix, 3)
        val candidates = ArrayList<KeyboardView.Candidate>()

        // 2. Add Raw Input as first option (Always allow raw input to enable unblocking)
        // If the word is blocked, it won't be in the dictionary (hasWord=false), so it will show as New (Cyan).
        // Clicking it will trigger learnWord(), which now unblocks it.
        val rawExists = predictionEngine.hasWord(prefix)
        candidates.add(KeyboardView.Candidate(prefix, isNew = !rawExists))


        // 3. Add dictionary suggestions (avoiding duplicates)
        for (s in suggestions) {
            // Don't show if it looks exactly like the raw input (ignore case)
            if (!s.equals(prefix, ignoreCase = true)) {
                candidates.add(KeyboardView.Candidate(s, isNew = false))
            }
        }

        updateSuggestionsWithSync(candidates.take(3))
    }
    // =================================================================================
    // END BLOCK: updateSuggestions
    // =================================================================================

    private fun resetComposition() {
        currentComposingWord.clear()
        updateSuggestionsWithSync(emptyList())
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
