package com.katsuyamaki.DroidOSTrackpadKeyboard

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
import android.view.InputDevice // <--- ADD THIS LINE
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
    // =================================================================================
    // SPACEBAR MOUSE EXTENDED MODE - DRAG HANDLE INDICATOR
    // SUMMARY: Reference to the drag handle indicator so we can change its color
    //          (red when extended mode is active, grey when inactive).
    // =================================================================================
    private var dragHandleIndicator: View? = null
    // =================================================================================
    // END BLOCK: DRAG HANDLE INDICATOR REFERENCE
    // =================================================================================
    private var cachedCustomModKey = 0
    private var keyboardParams: WindowManager.LayoutParams? = null

    // =================================================================================
    // LAUNCHER BLOCKED SHORTCUTS
    // SUMMARY: Set of shortcuts registered in the Launcher. Passed to KeyboardView.
    // =================================================================================
    private var launcherBlockedShortcuts: Set<String> = emptySet()
    // =================================================================================
    // END BLOCK: LAUNCHER BLOCKED SHORTCUTS
    // =================================================================================
    private var isVisible = false
    private val predictionEngine = PredictionEngine.instance
    // State Variables
    private var isMoving = false

    private var isResizing = false
    // [NEW] Track scale internally to avoid slow SharedPreferences reads during drag
    private var internalScale = 1.0f
    private var dragStartScale = 1.0f
    private var dragStartHeight = 0

    // Mouse Tracking
    private var lastMouseX = 0f
    private var lastMouseY = 0f
    private var isMouseDragging = false

    private var isAnchored = false
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialWindowX = 0
    private var initialWindowY = 0
    private var initialWidth = 0
    private var initialHeight = 0

    private val TAG = "KeyboardOverlay"

    // [FIX] Track the physical device ID to ignore injected events (Anti-Loop)
    private var activeFingerDeviceId = -1
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
    // =================================================================================
    // ORIGINAL CASE TRACKING
    // SUMMARY: Stores the word with original casing (e.g., "DroidOS", "don't")
    //          while currentComposingWord stores lowercase for dictionary lookup.
    // =================================================================================
    private var originalCaseWord = StringBuilder()
    // =================================================================================
    // END BLOCK: ORIGINAL CASE TRACKING
    // =================================================================================
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
    private var currentDisplayId = targetDisplayId

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            screenWidth = bounds.width()
            screenHeight = bounds.height()
        } else {
            val metrics = android.util.DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(metrics)
            screenWidth = metrics.widthPixels
            screenHeight = metrics.heightPixels
        }
    }


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

    // =================================================================================
    // ARROW KEYS SWIPE CALLBACK
    // SUMMARY: Forwards arrow key swipe from KeyboardView to OverlayService
    // =================================================================================
    var onArrowSwipe: ((Float, Float) -> Unit)? = null
    var onMouseScroll: ((Float, Float) -> Unit)? = null
    // =================================================================================
    // END BLOCK: ARROW KEYS SWIPE CALLBACK
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
        // 1. Update Class-Level Screen Dimensions
        this.screenWidth = width
        this.screenHeight = height
        this.currentDisplayId = displayId

        // 2. Apply Dynamic Resize Logic (Same as Reset)
        // This ensures that when entering split-screen or rotating, the keyboard
        // recalculates its perfect size (90% width, 300dp height) instead of stretching.
        val newWidth = (width * 0.90f).toInt().coerceIn(300, 1200)
        
        // Calculate Height: 300dp * Scale * Density
        val density = context.resources.displayMetrics.density
        val scale = if (internalScale > 0f) internalScale else 0.69f
        val baseHeightDp = 300f
        val newHeight = (baseHeightDp * scale * density).toInt()

        // 3. Update Window Params
        keyboardWidth = newWidth
        keyboardHeight = newHeight
        
        keyboardParams?.let {
            it.width = newWidth
            it.height = newHeight
            
            // Optional: Re-center keyboard on screen change
            it.x = (width - newWidth) / 2
            it.y = height / 2

            try {
                windowManager.updateViewLayout(keyboardContainer, it)
                syncMirrorRatio(newWidth, newHeight)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }





    fun updateScale(scale: Float) {
        internalScale = scale // [FIX] Track scale state
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
            val os = orientSuffix()
            prefs.edit()
                .putInt("keyboard_x_d${currentDisplayId}$os", x)
                .putInt("keyboard_y_d${currentDisplayId}$os", y)
                .putInt("keyboard_width_d${currentDisplayId}$os", width)
                .putInt("keyboard_height_d${currentDisplayId}$os", height)
                .apply()
        }
    }
    
    // =================================================================================
    // FUNCTION: setWindowBoundsWithScale
    // SUMMARY: Atomically sets window bounds AND key scale to prevent desync.
    //          The scale is calculated from the target height to ensure keys fit perfectly.
    //          Base height at 100% scale = 300dp.
    // =================================================================================
    fun setWindowBoundsWithScale(x: Int, y: Int, width: Int, height: Int) {
        val density = context.resources.displayMetrics.density
        val baseHeightPx = 300f * density
        
        // Calculate scale from target height (keys should fit the window perfectly)
        val targetScale = (height.toFloat() / baseHeightPx).coerceIn(0.3f, 1.5f)
        
        // Update internal scale and key view
        internalScale = targetScale
        keyboardView?.setScale(targetScale)
        
        // Update window bounds
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
                saveKeyboardScale() // Save scale with size to keep them in sync
            } catch (e: Exception) {}
        } else {
            // Save bounds and scale even if hidden
            val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            val os = orientSuffix()
            prefs.edit()
                .putInt("keyboard_x_d${currentDisplayId}$os", x)
                .putInt("keyboard_y_d${currentDisplayId}$os", y)
                .putInt("keyboard_width_d${currentDisplayId}$os", width)
                .putInt("keyboard_height_d${currentDisplayId}$os", height)
                .putInt("keyboard_key_scale$os", (targetScale * 100).toInt())
                .apply()
        }
        
        Log.d(TAG, "setWindowBoundsWithScale: h=$height, scale=$targetScale")
    }
    // =================================================================================
    // END BLOCK: setWindowBoundsWithScale
    // =================================================================================
   
    fun setAnchored(anchored: Boolean) {
        isAnchored = anchored
    }

    fun setVibrationEnabled(enabled: Boolean) {
        keyboardView?.setVibrationEnabled(enabled)
    }
    // Helper for OverlayService Profile Load
    fun updatePosition(x: Int, y: Int) {
        if (keyboardContainer == null || keyboardParams == null) {
            // Save to prefs if hidden
            context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
                .putInt("keyboard_x_d${currentDisplayId}${orientSuffix()}", x)
                .putInt("keyboard_y_d${currentDisplayId}${orientSuffix()}", y)
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
            .getInt("keyboard_x_d${currentDisplayId}${orientSuffix()}", 0)
    }
    

    fun getViewY(): Int {
        if (keyboardParams != null) return keyboardParams!!.y
        return context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .getInt("keyboard_y_d${currentDisplayId}${orientSuffix()}", 0)
    }
    
    fun getViewWidth(): Int = keyboardWidth
    fun getViewHeight(): Int = keyboardHeight
    fun getScale(): Float = internalScale // [FIX] Added accessor
    fun getKeyboardView(): KeyboardView? = keyboardView
    fun getContainerView(): View? = keyboardContainer // NEW
    fun getSuggestionStripHeight(): Int = keyboardView?.getSuggestionStripHeight() ?: 0


    
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




    // [Removed duplicate accessors to fix build error] 




    fun resetPosition() {
        if (keyboardParams == null) return
        
        // 1. Set Scale to 0.69f (Standard Reset Scale)
        val defaultScale = 0.69f
        internalScale = defaultScale
        keyboardView?.setScale(defaultScale)
        
        // Save preference
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
             .edit().putInt("keyboard_key_scale", 69).apply()

        // 2. Reset Rotation state
        currentRotation = 0
        keyboardContainer?.rotation = 0f
        keyboardView?.rotation = 0f
        keyboardView?.translationX = 0f
        keyboardView?.translationY = 0f

        // 3. Calculate Dimensions
        // Width: 90% of screen, capped at 1200px to prevent ultra-wide windows
        val defaultWidth = (screenWidth * 0.90f).toInt().coerceIn(300, 1200)
        
        // Height: Fixed Physical Height Calculation
        // Problem: Calculating height as % of width fails on wide screens (Beam Pro), creating huge empty space.
        // Solution: Use fixed DP height.
        // 300dp = Physical height of 7 rows (derived from your Flip 7 1080p setup).
        // This ensures the window is exactly tall enough for the keys on ANY device.
        val density = context.resources.displayMetrics.density
        val baseHeightDp = 300f
        val defaultHeight = (baseHeightDp * defaultScale * density).toInt()
        
        // Position: Center
        val defaultX = (screenWidth - defaultWidth) / 2
        val defaultY = (screenHeight / 2)

        // 4. Update Params
        keyboardWidth = defaultWidth
        keyboardHeight = defaultHeight
        
        // Reset Drag Anchors
        dragStartHeight = defaultHeight
        dragStartScale = defaultScale
        
        keyboardParams?.x = defaultX
        keyboardParams?.y = defaultY
        keyboardParams?.width = defaultWidth
        keyboardParams?.height = defaultHeight

        // 5. Force View to Fill Window
        if (keyboardView != null) {
            val lp = keyboardView!!.layoutParams as FrameLayout.LayoutParams
            lp.width = FrameLayout.LayoutParams.MATCH_PARENT
            lp.height = FrameLayout.LayoutParams.MATCH_PARENT 
            keyboardView!!.layoutParams = lp
        }

        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
        } catch (e: Exception) {}
        
        saveKeyboardPosition()
        saveKeyboardSize()
        
        // 6. Sync Mirror
        syncMirrorRatio(defaultWidth, defaultHeight)
    }





    // [END ROTATION FIX] 


    fun show() { 
        if (isVisible) return
        try { 
            val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            currentAlpha = prefs.getInt("keyboard_alpha", 200)


            createKeyboardWindow()

            // [NEW] Initialize internal scale from prefs once on show
            // Default to 69 (0.69f) if missing, to match resetPosition
            val os = orientSuffix()
        val savedScale = prefs.getInt("keyboard_key_scale$os", prefs.getInt("keyboard_key_scale", 69)) / 100f
            internalScale = savedScale
            dragStartScale = savedScale // Init for safety



            // [MODIFIED] Removed forced 0.55 aspect ratio listener to allow independent resizing.
            // Sync is now handled via explicit broadcast in resize functions.


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

    fun setInputCaptureMode(active: Boolean) {
        keyboardView?.setInputCaptureMode(active)
    }

fun setCustomModKey(keyCode: Int) {
        cachedCustomModKey = keyCode // Cache it
        keyboardView?.setCustomModKey(keyCode) // Apply to current view
    }

    fun setOverrideSystemShortcuts(enabled: Boolean) {
        keyboardView?.setOverrideSystemShortcuts(enabled)
    }

    // =================================================================================
    // FUNCTION: setLauncherBlockedShortcuts
    // SUMMARY: Updates the set of shortcuts that should be blocked.
    //          Stores locally AND attempts to forward to KeyboardView if it exists.
    //          If KeyboardView doesn't exist yet, the shortcuts will be applied
    //          when show() creates it.
    // @param shortcuts - Set of "modifier|keyCode" strings from Launcher
    // =================================================================================
    fun setLauncherBlockedShortcuts(shortcuts: Set<String>) {
        launcherBlockedShortcuts = shortcuts
        Log.d(TAG, "Updated launcher blocked shortcuts: ${shortcuts.size} entries")

        // Try to apply to existing KeyboardView (may be null if not shown yet)
        if (keyboardView != null) {
            keyboardView?.setLauncherBlockedShortcuts(shortcuts)
            Log.d(TAG, "Applied blocked shortcuts to existing KeyboardView")
        } else {
            Log.d(TAG, "KeyboardView is null - shortcuts will be applied when show() is called")
        }
    }
    // =================================================================================
    // END FUNCTION: setLauncherBlockedShortcuts
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
    

    // [MODIFIED] Legacy support: Redirects to the new smart resize logic
    fun resizeWindow(dw: Int, dh: Int) {
        if (!isVisible || keyboardParams == null) return
        
        // Calculate target dimensions based on current params
        val currentW = if (keyboardParams!!.width > 0) keyboardParams!!.width else keyboardContainer?.width ?: 300
        val currentH = if (keyboardParams!!.height > 0) keyboardParams!!.height else keyboardContainer?.height ?: 200

        // Pass to the new centralized function that handles Scaling + Sync
        applyWindowResize(currentW + dw, currentH + dh)
        
        // [FIX] Save state so it persists
        saveKeyboardSize()
        saveKeyboardScale()
    }


    // =================================================================================
    // NEW: Centralized Resize Logic with Auto-Scale and Mirror Sync
    // =================================================================================

    // [MODIFIED] Deterministic Resizing Logic (Fixes Lag & Mismatch)
    private fun applyWindowResize(width: Int, height: Int) {
        if (keyboardParams == null) return

        val newWidth = max(300, width)
        val newHeight = max(150, height)
        
        // 1. Calculate New Scale Deterministically
        // Formula: NewScale = StartScale * (NewHeight / StartHeight)
        // This ensures the keys grow exactly proportional to the window drag.
        if (dragStartHeight > 0 && newHeight != dragStartHeight) {
             val heightRatio = newHeight.toFloat() / dragStartHeight.toFloat()
             val targetScale = dragStartScale * heightRatio
             
             // Update Internal State
             internalScale = targetScale
             keyboardView?.setScale(internalScale)
        }

        // 2. Update Window Params
        keyboardParams!!.width = newWidth
        keyboardParams!!.height = newHeight
        keyboardWidth = newWidth
        keyboardHeight = newHeight

        try {
            windowManager.updateViewLayout(keyboardContainer, keyboardParams)
            onSizeChanged?.invoke()
        } catch (e: Exception) {}

        // 3. Sync Mirror Aspect Ratio
        syncMirrorRatio(newWidth, newHeight)
    }


    fun handleResizeDpad(keyCode: Int): Boolean {
        if (!isVisible || keyboardParams == null) return false
        
        val STEP = 20
        var w = keyboardParams!!.width
        var h = keyboardParams!!.height
        var changed = false

        when (keyCode) {
            // Horizontal Only
            KeyEvent.KEYCODE_DPAD_LEFT -> { w -= STEP; changed = true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { w += STEP; changed = true }
            
            // Vertical Only
            KeyEvent.KEYCODE_DPAD_UP -> { h -= STEP; changed = true }   // Shrink Height
            KeyEvent.KEYCODE_DPAD_DOWN -> { h += STEP; changed = true } // Grow Height
        }

        if (changed) {
            applyWindowResize(w, h)
            saveKeyboardSize()  // [FIX] Save size immediately
            saveKeyboardScale() // [FIX] Save scale immediately
            return true
        }
        return false
    }

    private fun syncMirrorRatio(width: Int, height: Int) {
        try {
            val ratio = width.toFloat() / height.toFloat()
            val intent = android.content.Intent("com.katsuyamaki.DroidOSLauncher.SYNC_KEYBOARD_RATIO")
            intent.putExtra("ratio", ratio)
            intent.putExtra("width", width)
            intent.putExtra("height", height)
            intent.setPackage("com.katsuyamaki.DroidOSLauncher")
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            android.util.Log.e("KeyboardOverlay", "Failed to sync mirror ratio", e)
        }
    }





    private fun createKeyboardWindow() {
        // Defensive cleanup — remove old container if still attached
        if (keyboardContainer != null) {
            try { windowManager.removeView(keyboardContainer) } catch (e: Exception) {}
            keyboardContainer = null; keyboardView = null
        }

        // [FIX] Use custom FrameLayout to intercept ALL touches.
        keyboardContainer = object : FrameLayout(context) {
            
            // SHIELD 1: Handle Mouse Scroll (BT Mouse support)
            // NOTE: Hover-to-cursor movement was removed - it was an old test feature
            override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
                if (event.isFromSource(InputDevice.SOURCE_MOUSE) ||
                    event.isFromSource(InputDevice.SOURCE_MOUSE_RELATIVE)) {

                    // Handle mouse scroll wheel
                    if (event.actionMasked == MotionEvent.ACTION_SCROLL) {
                        val v = event.getAxisValue(MotionEvent.AXIS_VSCROLL)
                        val h = event.getAxisValue(MotionEvent.AXIS_HSCROLL)
                        onMouseScroll?.invoke(h, v)
                        return true
                    }

                    // Consume other mouse events to prevent interference
                    return true
                }
                return super.dispatchGenericMotionEvent(event)
            }

            // SHIELD 2: Block Injected/Obscured Touches
            override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                
                // [CRITICAL FIX] OBSCURED FILTER
                // If the touch is passing through another window (like our Cursor Overlay),
                // Android flags it as OBSCURED. We reject these to prevent the cursor loop.
                if ((event.flags and MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0 ||
                    (event.flags and MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED) != 0) {
                    return true
                }

                // 1. Block Non-Fingers (Except Mouse)
                val toolType = event.getToolType(0)
                val isMouse = toolType == MotionEvent.TOOL_TYPE_MOUSE || event.isFromSource(InputDevice.SOURCE_MOUSE)

                if (toolType != MotionEvent.TOOL_TYPE_FINGER && !isMouse) return true

                // 2. Block Virtual/Null Devices
                if (event.device == null || event.deviceId <= 0) return true

                // 3. Handle Mouse Inputs (Clicks/Drags)
                if (isMouse) {
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            lastMouseX = event.x
                            lastMouseY = event.y
                            isMouseDragging = false
                            onTouchDown?.invoke() // Start drag state in Service
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val dx = event.x - lastMouseX
                            val dy = event.y - lastMouseY

                            if (kotlin.math.abs(dx) > 0 || kotlin.math.abs(dy) > 0) isMouseDragging = true

                            lastMouseX = event.x
                            lastMouseY = event.y
                            // Pass as Drag (isDragging=true)
                            onCursorMove?.invoke(dx, dy, true)
                        }
                        MotionEvent.ACTION_UP -> {
                            onTouchUp?.invoke()
                            if (!isMouseDragging) {
                                // Static click = Tap
                                onCursorClick?.invoke(false)
                            }
                            isMouseDragging = false
                        }
                    }
                    return true
                }

                val devId = event.deviceId
                val action = event.actionMasked

                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Lock onto the first physical finger. 
                        if (activeFingerDeviceId != -1 && activeFingerDeviceId != devId) {
                            return true
                        }
                        activeFingerDeviceId = devId
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // GHOST BLOCK: If no physical finger is locked, IGNORE ALL MOVES.
                        if (activeFingerDeviceId == -1) return true
                        
                        // Strict Lock: Only allow the specific locked physical device.
                        if (devId != activeFingerDeviceId) return true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        if (devId == activeFingerDeviceId) {
                            activeFingerDeviceId = -1
                        }
                    }
                }
                
                return super.dispatchTouchEvent(event)
            }
        }

        // [IMPORTANT] Enable security filter on the view itself as a backup
        keyboardContainer?.filterTouchesWhenObscured = true

        val containerBg = GradientDrawable()
        val fillColor = (currentAlpha shl 24) or (0x1A1A1A)
        containerBg.setColor(fillColor)
        containerBg.cornerRadius = 16f
        keyboardContainer?.background = containerBg
        
        keyboardContainer?.isFocusable = true
        keyboardContainer?.isFocusableInTouchMode = true
        keyboardContainer?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (handleResizeDpad(keyCode)) {
                    return@setOnKeyListener true
                }
            }
            false
        }

        // 1. The Keyboard Keys
        keyboardView = KeyboardView(context)

        // [FIX] Apply cached Custom Mod Key immediately on creation
        if (cachedCustomModKey != 0) {
            keyboardView?.setCustomModKey(cachedCustomModKey)
        }

        // SHIELD 3: Backup Generic Motion Listener
        keyboardView?.setOnGenericMotionListener { _, event ->
            if (event.isFromSource(InputDevice.SOURCE_MOUSE) || 
                event.isFromSource(InputDevice.SOURCE_MOUSE_RELATIVE)) {
                return@setOnGenericMotionListener true
            }
            false
        }

// =================================================================================
        // SPACEBAR MOUSE CURSOR MOVE CALLBACK BINDING
        // SUMMARY: Forwards cursor movement from KeyboardView's spacebar trackpad feature
        //          to OverlayService. The isDragging parameter indicates whether user is
        //          performing a hold-to-drag operation (true) or just moving cursor (false).
        //          When isDragging=true, OverlayService injects ACTION_MOVE with TOUCHSCREEN
        //          source; when false, it injects HOVER_MOVE with MOUSE source.
        // =================================================================================
        keyboardView?.cursorMoveAction = { dx, dy, isDragging ->
            // Pass through isDragging state to enable hold-to-drag functionality
            onCursorMove?.invoke(dx, dy, isDragging)
        }
        // =================================================================================
        // END BLOCK: SPACEBAR MOUSE CURSOR MOVE CALLBACK BINDING
        // =================================================================================

        // =================================================================================
        // ARROW KEYS SWIPE CALLBACK BINDING
        // SUMMARY: Forwards arrow swipe from KeyboardView to OverlayService
        // =================================================================================
        keyboardView?.onArrowSwipe = { dx, dy ->
            onArrowSwipe?.invoke(dx, dy)
        }
        // =================================================================================
        // END BLOCK: ARROW KEYS SWIPE CALLBACK BINDING
        // =================================================================================

        keyboardView?.cursorClickAction = { isRight ->
            onCursorClick?.invoke(isRight)
            // Fix: Reset swipe state on click to prevent accidental full-word delete
            lastCommittedSwipeWord = null
            resetComposition()
        }

        keyboardView?.touchDownAction = { 
            onTouchDown?.invoke()
            // Fix: Reset swipe state on drag start
            lastCommittedSwipeWord = null
            resetComposition()
        }
        
        keyboardView?.touchUpAction = { onTouchUp?.invoke() }
        
        keyboardView?.touchTapAction = { 
            onTouchTap?.invoke()
            // Fix: Reset swipe state on tap
            lastCommittedSwipeWord = null
            resetComposition() 
        }


        keyboardView?.mirrorTouchCallback = { x, y, action ->
            val cb = onMirrorTouch
            if (cb != null) cb.invoke(x, y, action) else false
        }

        keyboardView?.setKeyboardListener(this)
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        keyboardView?.setVibrationEnabled(prefs.getBoolean("vibrate", true))

        // =================================================================================
        // APPLY LAUNCHER BLOCKED SHORTCUTS TO NEW KEYBOARDVIEW
        // SUMMARY: The launcherBlockedShortcuts may have been set before the KeyboardView
        //          was created. Now that it exists, sync the blocked shortcuts to it.
        // =================================================================================
        if (launcherBlockedShortcuts.isNotEmpty()) {
            keyboardView?.setLauncherBlockedShortcuts(launcherBlockedShortcuts)
            Log.d(TAG, "Applied ${launcherBlockedShortcuts.size} blocked shortcuts to new KeyboardView")
        }
        // =================================================================================
        // END BLOCK: APPLY LAUNCHER BLOCKED SHORTCUTS TO NEW KEYBOARDVIEW
        // =================================================================================

        // [FIX] Load saved scale and update Internal State immediately
        // Use orientation-aware key with fallback to base key then 69 default
        val os0 = orientSuffix()
        val scale = prefs.getInt("keyboard_key_scale$os0", prefs.getInt("keyboard_key_scale", 69)) / 100f
        internalScale = scale
        keyboardView?.setScale(scale)
        
        keyboardView?.alpha = currentAlpha / 255f

        val kbParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        kbParams.setMargins(2, 28, 2, 0)
        keyboardContainer?.addView(keyboardView, kbParams)

        // 2. The Swipe Trail Overlay
        val trailView = SwipeTrailView(context)
        val trailParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        trailParams.setMargins(2, 28, 2, 0)
        keyboardContainer?.addView(trailView, trailParams)
        keyboardView?.attachTrailView(trailView)

        // ORIENTATION TRAIL VIEW
        orientationTrailView = SwipeTrailView(context)
        orientationTrailView?.setTrailColor(0xFFFF9900.toInt())
        val orientTrailParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        orientTrailParams.setMargins(2, 28, 2, 0)
        keyboardContainer?.addView(orientationTrailView, orientTrailParams)


        addDragHandle(); addResizeHandle(); addCloseButton(); addTargetLabel()

        // [FIX] Calculate Dynamic Defaults for "First Load"
        // This prevents the keyboard from loading with wrong aspect ratios (like WRAP_CONTENT)
        // if no preferences exist yet.
        
        // 1. Calculate Default Width (90% Screen)
        val defaultWidth = (screenWidth * 0.90f).toInt().coerceIn(300, 1200)
        
        // 2. Calculate Default Height (300dp * Scale)
        // If no scale is saved, we default to 0.69f (The "Reset" Scale)
        val density = context.resources.displayMetrics.density
        val os = orientSuffix()
        val savedScale = prefs.getInt("keyboard_key_scale$os", prefs.getInt("keyboard_key_scale", 69)) / 100f
        val baseHeightDp = 300f
        val defaultHeight = (baseHeightDp * savedScale * density).toInt()
        
        val defaultX = (screenWidth - defaultWidth) / 2
        val defaultY = (screenHeight / 2)

        // 3. Load from Prefs (using our calculated defaults as fallback)
        val savedW = prefs.getInt("keyboard_width_d${currentDisplayId}$os", prefs.getInt("keyboard_width_d$currentDisplayId", defaultWidth))
        val savedH = prefs.getInt("keyboard_height_d${currentDisplayId}$os", prefs.getInt("keyboard_height_d$currentDisplayId", defaultHeight))
        val savedX = prefs.getInt("keyboard_x_d${currentDisplayId}$os", prefs.getInt("keyboard_x_d$currentDisplayId", defaultX))
        val savedY = prefs.getInt("keyboard_y_d${currentDisplayId}$os", prefs.getInt("keyboard_y_d$currentDisplayId", defaultY))

        // 4. Bounds validation — clamp so KB is visible on this display
        val clampedW = savedW.coerceIn(100, screenWidth)
        val clampedH = savedH.coerceIn(100, screenHeight)
        val clampedX = savedX.coerceIn(-clampedW + 50, screenWidth - 50)
        val clampedY = savedY.coerceIn(-clampedH + 50, screenHeight - 50)
        val offScreenThreshold = screenWidth / 2
        val useDefaults = (kotlin.math.abs(savedX - clampedX) > offScreenThreshold ||
                          kotlin.math.abs(savedY - clampedY) > offScreenThreshold)

        // 5. Set Fields
        keyboardWidth = if (useDefaults) defaultWidth else clampedW
        keyboardHeight = if (useDefaults) defaultHeight else clampedH

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
        keyboardParams?.x = if (useDefaults) defaultX else clampedX
        keyboardParams?.y = if (useDefaults) defaultY else clampedY

windowManager.addView(keyboardContainer, keyboardParams)
        updateAlpha(currentAlpha)
        
        // [FIX] Initialize Resize Anchors so D-pad/Scaling works immediately
        // This prevents the "Background resizes but Keys don't" bug on fresh load.
        dragStartHeight = keyboardHeight
        dragStartScale = internalScale

        // =================================================================================
        // SPACEBAR MOUSE EXTENDED MODE - APPLY PREFERENCE ON VIEW CREATION
        // =================================================================================
        val extendedModePref = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .getBoolean("spacebar_mouse_extended", false)
        keyboardView?.setSpacebarExtendedMode(extendedModePref)
        android.util.Log.d("KeyboardOverlay", "Applied spacebar extended mode: $extendedModePref")
        
        // Connect callback for drag handle color updates
        keyboardView?.onExtendedModeChanged = { isActive ->
            updateDragHandleColor(isActive)
        }
        // =================================================================================
        // END BLOCK: SPACEBAR MOUSE EXTENDED MODE INITIALIZATION
        // =================================================================================
    }





    // =================================================================================
    // FUNCTION: addDragHandle
    // SUMMARY: Creates the drag handle at the top of the keyboard. The indicator bar
    //          changes color based on spacebar mouse extended mode state:
    //          - Grey (#555555): Normal state
    //          - Red (#FF5555): Extended mode active (tap to exit)
    //          Tapping (not dragging) the handle while in extended mode exits the mode.
    // =================================================================================
    private fun addDragHandle() {
        val handle = FrameLayout(context)
        val handleParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 28)
        handleParams.gravity = Gravity.TOP
        
        // Create indicator bar
        val indicator = View(context)
        val indicatorBg = GradientDrawable()
        indicatorBg.setColor(Color.parseColor("#555555"))
        indicatorBg.cornerRadius = 3f
        indicator.background = indicatorBg
        
        // Store reference for color updates
        dragHandleIndicator = indicator
        
        val indicatorParams = FrameLayout.LayoutParams(50, 5)
        indicatorParams.gravity = Gravity.CENTER
        indicatorParams.topMargin = 8
        handle.addView(indicator, indicatorParams)
        
        // Variables to detect tap vs drag
        var touchStartX = 0f
        var touchStartY = 0f
        var isDragGesture = false
        val tapThreshold = 10f // pixels - movement less than this is a tap
        
        handle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchStartX = event.rawX
                    touchStartY = event.rawY
                    isDragGesture = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = kotlin.math.abs(event.rawX - touchStartX)
                    val dy = kotlin.math.abs(event.rawY - touchStartY)
                    if (dx > tapThreshold || dy > tapThreshold) {
                        isDragGesture = true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    // If it was a tap (not a drag) and extended mode is active, exit mode
                    if (!isDragGesture) {
                        val kbView = keyboardView
                        if (kbView != null && kbView.isInSpacebarMouseMode()) {
                            android.util.Log.d("KeyboardOverlay", "Drag handle tapped - exiting extended mode")
                            kbView.exitSpacebarMouseMode()
                            updateDragHandleColor(false)
                            // Haptic feedback
                            handle.performHapticFeedback(android.view.HapticFeedbackConstants.REJECT)
                            return@setOnTouchListener true
                        }
                    }
                }
            }
            // Pass to normal drag handling
            handleDrag(event)
            true
        }
        
        keyboardContainer?.addView(handle, handleParams)
    }
    // =================================================================================
    // END BLOCK: addDragHandle
    // =================================================================================
    // =================================================================================
    // FUNCTION: updateDragHandleColor
    // SUMMARY: Updates the drag handle indicator color based on spacebar mouse extended
    //          mode state. Red when active (indicating tap to exit), grey when inactive.
    //          Called from KeyboardView when extended mode state changes.
    // =================================================================================
    fun updateDragHandleColor(extendedModeActive: Boolean) {
        val indicator = dragHandleIndicator ?: return
        val bg = indicator.background as? GradientDrawable ?: return
        
        if (extendedModeActive) {
            bg.setColor(Color.parseColor("#FF5555")) // Red - tap to exit
        } else {
            bg.setColor(Color.parseColor("#555555")) // Grey - normal
        }
        indicator.invalidate()
        
        android.util.Log.d("KeyboardOverlay", "Drag handle color updated: extended=$extendedModeActive")
    }
    // =================================================================================
    // END BLOCK: updateDragHandleColor
    // =================================================================================

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
                
                initialWidth = keyboardParams?.width ?: keyboardWidth
                initialHeight = keyboardParams?.height ?: keyboardHeight
                
                // Resolve WRAP_CONTENT to actual pixels
                if (initialWidth < 0) initialWidth = keyboardContainer?.width ?: 300
                if (initialHeight < 0) initialHeight = keyboardContainer?.height ?: 200
                
                // [FIX] Capture stable start values for deterministic scaling
                dragStartHeight = initialHeight
                dragStartScale = internalScale
            }


            MotionEvent.ACTION_MOVE -> {
                if (isResizing) {
                    val dX = (event.rawX - initialTouchX).toInt()
                    val dY = (event.rawY - initialTouchY).toInt()
                    
                    val newW = initialWidth + dX
                    val newH = initialHeight + dY
                    
                    // Use the shared function to handle visual update + scale + sync
                    // Note: We might want to throttle 'saveKeyboardSize' inside applyWindowResize during drag
                    // but for now this ensures visual consistency.
                    applyWindowResize(newW, newH)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isResizing = false
                saveKeyboardSize()
                saveKeyboardScale() // [FIX] Persist scale so aspect ratio survives hide/unhide
            }
        }
        return true
    }
    // [END RESIZE FIX]

    private fun orientSuffix(): String = if (screenWidth > screenHeight) "_L" else "_P"
    private fun saveKeyboardSize() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_width_d${currentDisplayId}${orientSuffix()}", keyboardWidth).putInt("keyboard_height_d${currentDisplayId}${orientSuffix()}", keyboardHeight).apply() }
    private fun saveKeyboardScale() {
        val scaleVal = (internalScale * 100).toInt()
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .edit().putInt("keyboard_key_scale${orientSuffix()}", scaleVal).putInt("keyboard_key_scale", scaleVal).apply()
    }
    private fun saveKeyboardPosition() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_x_d${currentDisplayId}${orientSuffix()}", keyboardParams?.x ?: 0).putInt("keyboard_y_d${currentDisplayId}${orientSuffix()}", keyboardParams?.y ?: 0).apply() }
    private fun loadKeyboardSizeForDisplay(displayId: Int) { val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE); val os = orientSuffix(); keyboardWidth = prefs.getInt("keyboard_width_d${displayId}$os", keyboardWidth); keyboardHeight = prefs.getInt("keyboard_height_d${displayId}$os", keyboardHeight) }


    // =================================================================================
    // FUNCTION: onKeyPress
    // SUMMARY: Handles key press events from the keyboard. Manages composing word state,
    //          sentence start detection, and auto-learning. Special handling for
    //          punctuation after swiped words to remove the trailing space.
    // =================================================================================
    // =================================================================================
    // FUNCTION: onKeyPress
    // SUMMARY: Handles key press events from the keyboard. Manages composing word state,
    //          sentence start detection, and auto-learning.
    //          
    // UPDATED: 
    //   - Apostrophe (') is now part of words for "don't", "won't", etc.
    //   - Tracks originalCaseWord for "DroidOS", "iPhone" etc.
    //   - Shift keys don't clear composition
    //   - Properly clears prediction bar on space/enter
    // =================================================================================
    override fun onKeyPress(keyCode: Int, char: Char?, metaState: Int) {
        // android.util.Log.d("DroidOS_Key", "Press: keyCode=$keyCode char='$char' meta=$metaState")

        // --- SHIFT KEY: Ignore completely, don't affect composition ---
        if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || 
            keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT ||
            keyCode == KeyEvent.KEYCODE_CAPS_LOCK) {
            // Don't inject shift as a key, just return
            // The keyboard handles shift state internally
            return
        }

        // --- PUNCTUATION AFTER SWIPE: Remove trailing space ---
        val isEndingPunctuation = char != null && (char == '.' || char == ',' || char == '!' ||
                                          char == '?' || char == ';' || char == ':' || char == '"')

        if (isEndingPunctuation && lastCommittedSwipeWord != null && lastCommittedSwipeWord!!.endsWith(" ")) {
            injectKey(KeyEvent.KEYCODE_DEL, 0)
            android.util.Log.d("DroidOS_Swipe", "PUNCTUATION: Removed trailing space before '$char'")
            lastCommittedSwipeWord = lastCommittedSwipeWord!!.trimEnd()
        }

        // 1. Inject the key event
        injectKey(keyCode, metaState)

// 2. Handle Backspace - delete from composing word
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (currentComposingWord.isNotEmpty()) {
                currentComposingWord.deleteCharAt(currentComposingWord.length - 1)
                originalCaseWord.deleteCharAt(originalCaseWord.length - 1)
                android.util.Log.d("DroidOS_Compose", "BACKSPACE: Now composing '$originalCaseWord'")
                updateSuggestions()
            } else {
                // Composing word is empty, clear prediction bar
                updateSuggestionsWithSync(emptyList())
            }
            return
        }


        // 3. Handle Enter - clears everything
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            isSentenceStart = true
            predictionEngine.clearContext()  // Clear context at sentence boundary
            lastCommittedSwipeWord = null

            currentComposingWord.clear()
            originalCaseWord.clear()
            updateSuggestionsWithSync(emptyList())  // CLEAR prediction bar
            return
        }

                // 4. Handle Space - clears composition, clears prediction bar
        if (char != null && Character.isWhitespace(char)) {
            isSentenceStart = false
            lastCommittedSwipeWord = null
            predictionEngine.clearTemporaryPenalties() // Manual typing clears penalties
            currentComposingWord.clear()
            originalCaseWord.clear()
            updateSuggestionsWithSync(emptyList())  // CLEAR prediction bar
            return
        }


        // 5. Handle ending punctuation (. , ! ? ; : ") - clears composition
        if (isEndingPunctuation) {
            if (char == '.' || char == '!' || char == '?') {
                isSentenceStart = true
                predictionEngine.clearContext()  // Clear context at sentence boundary
            }
            lastCommittedSwipeWord = null

            currentComposingWord.clear()
            originalCaseWord.clear()
            updateSuggestionsWithSync(emptyList())  // CLEAR prediction bar
            return
        }

        // 6. Handle letters, digits, and apostrophe - ADD to composition
        if (char != null && (Character.isLetterOrDigit(char) || char == '\'')) {
            // Clear swipe history when manually typing
            lastCommittedSwipeWord = null
            
            // Add to composition trackers
            currentComposingWord.append(char.lowercaseChar())  // Lowercase for lookup
            originalCaseWord.append(char)  // Original case for display/saving
            
            isSentenceStart = false
            updateSuggestions()
            // android.util.Log.d("DroidOS_Compose", "Composing: '$originalCaseWord' (lookup: '$currentComposingWord')")
            return
        }

        // 7. Any other character - ignore, don't clear composition
        // This prevents random symbols from breaking the composition
        // android.util.Log.d("DroidOS_Key", "Ignored char: '$char'")
    }
    // =================================================================================
    // END BLOCK: onKeyPress with proper clearing and case tracking
    // =================================================================================
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

                // [FIX] Use Bulk Delete for reliability (especially with KB Blocker)
                (context as? OverlayService)?.injectBulkDelete(deleteCount)

                // NEGATIVE REINFORCEMENT: Penalize this word temporarily
                predictionEngine.penalizeWord(lastCommittedSwipeWord!!.trim())

                // Clear the swipe history so next backspace is normal
                lastCommittedSwipeWord = null

                
                // Also clear composition state
                currentComposingWord.clear()
                originalCaseWord.clear()
                updateSuggestionsWithSync(emptyList())

                // Don't inject another backspace - we already deleted
                return
            }

            // Normal backspace: delete from composing word
            if (currentComposingWord.isNotEmpty()) {
                currentComposingWord.deleteCharAt(currentComposingWord.length - 1)
                originalCaseWord.deleteCharAt(originalCaseWord.length - 1)
                android.util.Log.d("DroidOS_Compose", "BACKSPACE (special): Now composing '$originalCaseWord'")
                updateSuggestions()
            } else {
                // Nothing to delete from composition, clear prediction bar
                updateSuggestionsWithSync(emptyList())
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
            KeyboardView.SpecialKey.PAGE_UP -> KeyEvent.KEYCODE_PAGE_UP
            KeyboardView.SpecialKey.PAGE_DOWN -> KeyEvent.KEYCODE_PAGE_DOWN
            else -> return
        }
        injectKey(keyCode, metaState)
    }


    // =================================================================================
    // =================================================================================
    // FUNCTION: onSuggestionClick
    // SUMMARY: Handles when user taps a word in the prediction bar.
    //          Uses Gboard-style "completion" approach - instead of delete-all + retype,
    //          we calculate what's already typed correctly and only append the suffix.
    //          This avoids race conditions with Null Keyboard's async broadcast system.
    //
    //          SCENARIO 1: Swipe Correction - must delete previous swipe word, then type new
    //          SCENARIO 2: Manual Typing Completion - just append remaining suffix (no delete!)
    //          SCENARIO 3: Manual Typing with typo - delete only the mismatched portion
    // =================================================================================

    override fun onSuggestionClick(text: String, isNew: Boolean) {
        android.util.Log.d("DroidOS_Prediction", "=== SUGGESTION CLICK START ===")
        android.util.Log.d("DroidOS_Prediction", "Clicked word: '$text' (isNew=$isNew)")
        
        // FIX: Tell the engine we selected this word so it can learn!
        predictionEngine.recordSelection(context, text)
        
        // SUCCESS: Manual selection clears penalties
        predictionEngine.clearTemporaryPenalties()
        
        android.util.Log.d("DroidOS_Prediction", "currentComposingWord: '${currentComposingWord}'")

        android.util.Log.d("DroidOS_Prediction", "lastCommittedSwipeWord: '$lastCommittedSwipeWord'")

        // 1. Learn word if it was flagged as New
        if (isNew) {
            predictionEngine.learnWord(context, text, isSentenceStart)
            android.util.Log.d("DroidOS_Learn", "Learning new word: '$text' (sentenceStart=$isSentenceStart)")
        }

        // 2. Handle based on scenario
        if (!lastCommittedSwipeWord.isNullOrEmpty()) {
            // =====================================================================
            // SCENARIO 1: Correcting a previously swiped word
            // =====================================================================
            // We MUST delete the full swipe word because swipe doesn't leave
            // composing text - it commits directly. Use bulk delete for efficiency.
            val deleteCount = lastCommittedSwipeWord!!.length
            android.util.Log.d("DroidOS_Prediction", "SCENARIO 1: Swipe correction, deleting $deleteCount chars")
            
            (context as? OverlayService)?.injectBulkDelete(deleteCount)
            
            // Small delay to let delete complete before inserting
            // This is necessary because bulk delete is async via broadcast
            Handler(Looper.getMainLooper()).postDelayed({
                val newText = "$text "
                injectText(newText)
                
                // Update state
                lastCommittedSwipeWord = newText
                currentComposingWord.clear()
                originalCaseWord.clear()
                updateSuggestionsWithSync(emptyList())
                android.util.Log.d("DroidOS_Prediction", "Swipe correction complete: '$newText'")
            }, 50) // 50ms delay for delete to process
            
        } else if (currentComposingWord.isNotEmpty()) {
            // =====================================================================
            // SCENARIO 2 & 3: Manual Typing - Use Gboard-style completion
            // =====================================================================
            // Instead of delete-all + retype, calculate the suffix we need to append
            
            val typed = currentComposingWord.toString().lowercase()
            val target = text.lowercase()
            
            android.util.Log.d("DroidOS_Prediction", "Typed: '$typed', Target: '$target'")
            
            // Find how many characters at the start match
            var matchLength = 0
            for (i in typed.indices) {
                if (i < target.length && typed[i] == target[i]) {
                    matchLength++
                } else {
                    break // First mismatch found
                }
            }
            
            android.util.Log.d("DroidOS_Prediction", "Match length: $matchLength out of ${typed.length} typed")
            
            // Calculate what to delete (typos) and what to append (completion)
            val charsToDelete = typed.length - matchLength
            val suffixToAppend = if (matchLength < text.length) {
                text.substring(matchLength)
            } else {
                ""
            }
            
            android.util.Log.d("DroidOS_Prediction", "Will delete $charsToDelete chars, append: '$suffixToAppend'")
            
            if (charsToDelete > 0) {
                // SCENARIO 3: There's a typo - delete mismatched portion first
                (context as? OverlayService)?.injectBulkDelete(charsToDelete)
                
                // Delay before appending to let delete complete
                Handler(Looper.getMainLooper()).postDelayed({
                    val appendText = "$suffixToAppend "
                    if (appendText.length > 1) { // More than just space
                        injectText(appendText)
                    } else {
                        // Just space
                        injectText(" ")
                    }
                    
                    // Update state
                    lastCommittedSwipeWord = "$text "
                    currentComposingWord.clear()
                    originalCaseWord.clear()
                    updateSuggestionsWithSync(emptyList())
                    android.util.Log.d("DroidOS_Prediction", "Typo correction complete, appended: '$appendText'")
                }, 50) // 50ms delay
                
            } else {
                // SCENARIO 2: Pure completion - NO DELETE NEEDED!
                // This is the common case and is race-condition-free
                val appendText = "$suffixToAppend "
                injectText(appendText)
                
                // Update state immediately (no async delete to wait for)
                lastCommittedSwipeWord = "$text "
                currentComposingWord.clear()
                originalCaseWord.clear()
                updateSuggestionsWithSync(emptyList())
                android.util.Log.d("DroidOS_Prediction", "Pure completion: appended '$appendText'")
            }
            
        } else {
            // =====================================================================
            // SCENARIO 4: No composing word (edge case - just insert the word)
            // =====================================================================
            android.util.Log.d("DroidOS_Prediction", "SCENARIO 4: No composing word, inserting full word")
            val newText = "$text "
            injectText(newText)
            lastCommittedSwipeWord = newText
            updateSuggestionsWithSync(emptyList())
        }
        
        android.util.Log.d("DroidOS_Prediction", "=== SUGGESTION CLICK END ===")
    }
    // =================================================================================
    // END BLOCK: onSuggestionClick - Gboard-style completion approach
    // This implementation avoids delete race conditions by:
    // 1. For swipe corrections: uses bulk delete with small delay before insert
    // 2. For typing completion: only appends the suffix (no delete at all!)
    // 3. For typos: deletes only the mismatched portion, then appends
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

    // =================================================================================
    // FUNCTION: onSwipeDetectedTimed (PHASE 2 - Dual Algorithm with Color Coding)
    // SUMMARY: Receives swipe path and gets predictions from dual algorithm.
    //          Winner is selected based on swipe speed:
    //            - Slow swipes: PRECISE wins (GREEN text)
    //            - Fast swipes: SHAPE_CONTEXT wins (BLUE text)
    // =================================================================================
    override fun onSwipeDetectedTimed(path: List<TimedPoint>) {
        if (keyboardView == null || path.size < 3) return

        val keyMap = keyboardView?.getKeyCenters()
        if (keyMap.isNullOrEmpty()) return

        // Run dual prediction in background
        Thread {
            try {
                // decodeSwipeTimed now returns List<SwipeResult>
                val results = predictionEngine.decodeSwipeTimed(path, keyMap)
                
                if (results.isEmpty()) {
                    android.util.Log.d("DroidOS_Swipe", "DUAL DECODE: No suggestions returned")
                    return@Thread
                }
                
                val winner = results.first()
                android.util.Log.d("DroidOS_Swipe", "DUAL DECODE: Winner='${winner.word}' via ${winner.source}")

                handler.post {
                    var bestMatch = winner.word
                    val winningSource = winner.source
                    val isCap = isSentenceStart

                    if (isCap) {
                        bestMatch = bestMatch.replaceFirstChar { 
                            if (it.isLowerCase()) it.titlecase() else it.toString() 
                        }
                    }

                    // Build display suggestions with source info for color coding
                    val displaySuggestions = results.map { result ->
                        var word = result.word
                        if (isCap) {
                            word = word.replaceFirstChar { c -> c.titlecase() }
                        }
                        KeyboardView.Candidate(
                            text = word, 
                            isNew = false,
                            isCustom = predictionEngine.isCustomWord(result.word),
                            source = result.source  // Pass source for color coding!
                        )
                    }

                    updateSuggestionsWithSync(displaySuggestions)

                    // Commit text with proper spacing
                    var textToCommit = bestMatch
                    if (currentComposingWord.isNotEmpty()) {
                        textToCommit = " $bestMatch"
                        currentComposingWord.clear()
                    }
                    textToCommit = "$textToCommit "

                    injectText(textToCommit)
                    
                    // Update context model for future predictions
                    predictionEngine.updateContextModel(bestMatch)
                    
                    // SUCCESS: We committed a new word, so clear any previous backspace penalties
                    predictionEngine.clearTemporaryPenalties()
                    
                    lastCommittedSwipeWord = textToCommit
                    isSentenceStart = false
                    
                    // Clear composition state
                    currentComposingWord.clear()
                    originalCaseWord.clear()
                }
            } catch (e: Exception) {
                android.util.Log.e("DroidOS_Swipe", "Dual swipe decode error: ${e.message}")
                e.printStackTrace()
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: onSwipeDetectedTimed
    // =================================================================================

    // =================================================================================
    // FUNCTION: onSwipeDetected (Legacy - now just logs, actual work done in Timed version)
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

    // =================================================================================
    // FUNCTION: onSwipeProgress (PHASE 3 - Dual Live Preview)
    // SUMMARY: Shows BOTH algorithms' predictions during swiping.
    //          Left slot = Precise (GREEN), Middle slot = Shape (BLUE)
    //          This lets users see what both algorithms predict in real-time.
    // =================================================================================
    override fun onSwipeProgress(path: List<android.graphics.PointF>) {
        if (keyboardView == null || path.size < 5) return
        
        val keyMap = keyboardView?.getKeyCenters()
        if (keyMap.isNullOrEmpty()) return

        // Run dual preview in background
        Thread {
            try {
                val (preciseResult, shapeResult) = predictionEngine.decodeSwipeDualPreview(path, keyMap)

                // Only update if we have at least one result
                if (preciseResult != null || shapeResult != null) {
                    handler.post {
                        val isCap = isSentenceStart
                        val displaySuggestions = mutableListOf<KeyboardView.Candidate>()
                        
                        // LEFT SLOT: Precise prediction (GREEN)
                        if (preciseResult != null) {
                            var word = preciseResult.word
                            if (isCap) {
                                word = word.replaceFirstChar { c -> c.titlecase() }
                            }
                            displaySuggestions.add(KeyboardView.Candidate(
                                text = word,
                                isNew = false,
                                isCustom = predictionEngine.isCustomWord(preciseResult.word),
                                source = PredictionSource.PRECISE
                            ))
                        } else {
                            // Empty placeholder to maintain layout
                            displaySuggestions.add(KeyboardView.Candidate("", false, false, null))
                        }
                        
                        // MIDDLE SLOT: Shape/Context prediction (BLUE)
                        if (shapeResult != null) {
                            var word = shapeResult.word
                            if (isCap) {
                                word = word.replaceFirstChar { c -> c.titlecase() }
                            }
                            displaySuggestions.add(KeyboardView.Candidate(
                                text = word,
                                isNew = false,
                                isCustom = predictionEngine.isCustomWord(shapeResult.word),
                                source = PredictionSource.SHAPE_CONTEXT
                            ))
                        } else {
                            displaySuggestions.add(KeyboardView.Candidate("", false, false, null))
                        }
                        
                        // RIGHT SLOT: Empty during preview (filled on completion)
                        displaySuggestions.add(KeyboardView.Candidate("", false, false, null))

                        updateSuggestionsWithSync(displaySuggestions)
                    }
                }
            } catch (e: Exception) {
                // Silent fail for preview - don't spam logs
            }
        }.start()
    }
    // =================================================================================
    // END BLOCK: onSwipeProgress (Dual Live Preview)
    // =================================================================================


// =================================================================================
    // FUNCTION: updateSuggestions
    // SUMMARY: Updates the suggestion bar based on current composing word.
    //          
    // STYLING:
    //   - isNew=true (RED): Word not in any dictionary, can be added
    //   - isCustom=true (ITALIC): Word is in user dictionary  
    //   - Both false (WHITE BOLD): Word is in main dictionary
    // =================================================================================
    private fun updateSuggestions() {
        val prefix = currentComposingWord.toString()  // Lowercase for lookup
        val displayPrefix = originalCaseWord.toString()  // Original case for display
        
        if (prefix.isEmpty()) {
            updateSuggestionsWithSync(emptyList())
            return
        }

        // Strip apostrophe for dictionary lookup
        val lookupPrefix = prefix.replace("'", "")
        
        // Get dictionary suggestions
        val suggestions = if (lookupPrefix.isNotEmpty()) {
            predictionEngine.getSuggestions(lookupPrefix, 3)
        } else {
            emptyList()
        }
        
        val candidates = ArrayList<KeyboardView.Candidate>()

        // 1. Add Raw Input with ORIGINAL CASE as first option
        val rawExistsInMain = predictionEngine.hasWord(lookupPrefix) || predictionEngine.hasWord(prefix)
        val rawIsCustom = predictionEngine.isCustomWord(lookupPrefix) || predictionEngine.isCustomWord(prefix)
        val rawIsNew = !rawExistsInMain && !rawIsCustom
        
        candidates.add(KeyboardView.Candidate(
            text = displayPrefix, 
            isNew = rawIsNew,
            isCustom = rawIsCustom
        ))

// 2. Add dictionary suggestions (avoiding duplicates)
        // Apply display forms for proper capitalization (DroidOS, iPhone, etc.)
        for (s in suggestions) {
            val displayForm = predictionEngine.getDisplayForm(s)
            if (!displayForm.equals(lookupPrefix, ignoreCase = true) && 
                !displayForm.equals(prefix, ignoreCase = true) &&
                !displayForm.equals(displayPrefix, ignoreCase = true)) {
                // Check if this suggestion is a custom word
                val isCustom = predictionEngine.isCustomWord(s)
                candidates.add(KeyboardView.Candidate(
                    text = displayForm,  // Use display form with proper caps
                    isNew = false,
                    isCustom = isCustom
                ))
            }
        }

        updateSuggestionsWithSync(candidates.take(3))
        android.util.Log.d("DroidOS_Suggest", "Updated: ${candidates.map { "${it.text}(new=${it.isNew},custom=${it.isCustom})" }}")
    }
    // =================================================================================
    // END BLOCK: updateSuggestions with styling flags
    // =================================================================================

    // =================================================================================
    // FUNCTION: resetSwipeHistory
    // SUMMARY: Public access to reset swipe state. Called by OverlayService when
    //          external cursor movement (e.g. touching the app) is detected.
    // =================================================================================
    fun resetSwipeHistory() {
        if (lastCommittedSwipeWord != null) {
            android.util.Log.d("DroidOS_Swipe", "External cursor move detected -> Reset swipe history")
        }
        lastCommittedSwipeWord = null
        resetComposition()
    }

    private fun resetComposition() {
        currentComposingWord.clear()
        originalCaseWord.clear()
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

            // [FIX] Helper to fix Z-Order relative to Trackpad
        fun bringToFront() {
            if (!isVisible || keyboardContainer == null) return
            try {
                // Remove and Re-add to move to the top of the WindowManager stack
                windowManager.removeView(keyboardContainer)
                windowManager.addView(keyboardContainer, keyboardParams)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    // =================================================================================
    // FUNCTION: setNumberLayerOverride
    // SUMMARY: Automatically switches to Number/Symbol layer when Visual Queue is active.
    //          Saves previous state to restore later.
    // =================================================================================
    private var savedLayerState: KeyboardView.KeyboardState? = null

    fun setNumberLayerOverride(active: Boolean) {
        val kb = keyboardView ?: return
        if (active) {
            // Save current state if not already saved (prevents overwriting if called twice)
            if (savedLayerState == null) {
                savedLayerState = kb.getKeyboardState()
                android.util.Log.d(TAG, "Auto-Switch: Saved layer $savedLayerState, switching to SYMBOLS")
            }
            // SYMBOLS_1 contains the Number Row on main grid
            kb.setKeyboardState(KeyboardView.KeyboardState.SYMBOLS_1)
        } else {
            // Restore
            if (savedLayerState != null) {
                android.util.Log.d(TAG, "Auto-Switch: Restoring layer $savedLayerState")
                kb.setKeyboardState(savedLayerState!!)
                savedLayerState = null
            }
        }
    }

    // =================================================================================
    // FUNCTION: getKeyboardBounds
    // SUMMARY: Returns the screen bounds of the keyboard overlay for tap-outside detection.
    // =================================================================================
    fun getKeyboardBounds(): android.graphics.Rect? {
        val container = keyboardContainer ?: return null
        val params = keyboardParams ?: return null
        
        val location = IntArray(2)
        container.getLocationOnScreen(location)
        
        return android.graphics.Rect(
            location[0],
            location[1],
            location[0] + container.width,
            location[1] + container.height
        )
    }
    // =================================================================================
    // END BLOCK: getKeyboardBounds
    // =================================================================================
}
