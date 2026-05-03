## Plan: Fix keyboard overlay wrong dimensions after hide/restore

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Persist and restore keyboard overlay bounds + key scale per-display *and* per-orientation, matching DroidOSPro behavior, to eliminate intermittent wrong dimensions after hide/restore.
SEARCH_BLOCK:
```
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
```
REPLACE_BLOCK:
```
    fun setScreenDimensions(width: Int, height: Int, displayId: Int) {
        // 1. Update Class-Level Screen Dimensions
        this.screenWidth = width
        this.screenHeight = height
        this.currentDisplayId = displayId

        // 2. [FIX] Reload saved scale for THIS display + orientation so restore/rotate
        // doesn’t reuse stale scale and produce wrong container height / internal gaps.
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val os = orientSuffix()
        val savedScale = getSavedScalePercent(prefs, os) / 100f
        internalScale = savedScale
        keyboardView?.setScale(savedScale)

        // 3. Apply Dynamic Resize Logic (Same as Reset)
        // This ensures that when entering split-screen or rotating, the keyboard
        // recalculates its perfect size (90% width, 300dp height) instead of stretching.
        val newWidth = (width * 0.90f).toInt().coerceIn(300, 1200)

        // Calculate Height: 300dp * Scale * Density
        val density = context.resources.displayMetrics.density
        val baseHeightDp = 300f
        val newHeight = (baseHeightDp * savedScale * density).toInt()

        // 4. Update Window Params
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
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Key bounds + scale prefs per display and orientation; keep legacy fallback and dual-write for migration.
SEARCH_BLOCK:
```
    private fun saveKeyboardSize() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_width_d$currentDisplayId", keyboardWidth).putInt("keyboard_height_d$currentDisplayId", keyboardHeight).apply() }
    private fun saveKeyboardScale() { 
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .edit().putInt("keyboard_key_scale", (internalScale * 100).toInt()).apply() 
    }
    private fun saveKeyboardPosition() { context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit().putInt("keyboard_x_d$currentDisplayId", keyboardParams?.x ?: 0).putInt("keyboard_y_d$currentDisplayId", keyboardParams?.y ?: 0).apply() }
    private fun loadKeyboardSizeForDisplay(displayId: Int) { val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE); keyboardWidth = prefs.getInt("keyboard_width_d$displayId", keyboardWidth); keyboardHeight = prefs.getInt("keyboard_height_d$displayId", keyboardHeight) }
```
REPLACE_BLOCK:
```
    private fun orientSuffix(): String = if (screenWidth > screenHeight) "_L" else "_P"

    // Prefer per-display+orientation scale, fallback to legacy global key for migration.
    private fun getSavedScalePercent(prefs: android.content.SharedPreferences, os: String = orientSuffix()): Int {
        val displayKey = "keyboard_key_scale_d${currentDisplayId}$os"
        val legacyKey = "keyboard_key_scale"
        return prefs.getInt(displayKey, prefs.getInt(legacyKey, 69))
    }

    private fun saveKeyboardSize() {
        val os = orientSuffix()
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("keyboard_width_d${currentDisplayId}$os", keyboardWidth)
            .putInt("keyboard_height_d${currentDisplayId}$os", keyboardHeight)
            .apply()
    }

    private fun saveKeyboardScale() {
        val os = orientSuffix()
        val scaleVal = (internalScale * 100).toInt()
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .edit()
            // New (Pro-matching)
            .putInt("keyboard_key_scale_d${currentDisplayId}$os", scaleVal)
            // Legacy (migration/back-compat)
            .putInt("keyboard_key_scale", scaleVal)
            .apply()
    }

    private fun saveKeyboardPosition() {
        val os = orientSuffix()
        context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("keyboard_x_d${currentDisplayId}$os", keyboardParams?.x ?: 0)
            .putInt("keyboard_y_d${currentDisplayId}$os", keyboardParams?.y ?: 0)
            .apply()
    }

    private fun loadKeyboardSizeForDisplay(displayId: Int) {
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val os = orientSuffix()
        // New keys first, fallback to legacy unsuffixed keys.
        keyboardWidth = prefs.getInt("keyboard_width_d${displayId}$os", prefs.getInt("keyboard_width_d$displayId", keyboardWidth))
        keyboardHeight = prefs.getInt("keyboard_height_d${displayId}$os", prefs.getInt("keyboard_height_d$displayId", keyboardHeight))
    }
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Ensure hidden-save bounds also use per-display+orientation keys with legacy dual-write.
SEARCH_BLOCK:
```
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
```
REPLACE_BLOCK:
```
        } else {
            // Even if hidden, save the new bounds so they apply on next show
            val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            val os = orientSuffix()
            prefs.edit()
                .putInt("keyboard_x_d${currentDisplayId}$os", x)
                .putInt("keyboard_y_d${currentDisplayId}$os", y)
                .putInt("keyboard_width_d${currentDisplayId}$os", width)
                .putInt("keyboard_height_d${currentDisplayId}$os", height)
                // Legacy (migration/back-compat)
                .putInt("keyboard_x_d$currentDisplayId", x)
                .putInt("keyboard_y_d$currentDisplayId", y)
                .putInt("keyboard_width_d$currentDisplayId", width)
                .putInt("keyboard_height_d$currentDisplayId", height)
                .apply()
        }
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Ensure updatePosition hidden-save uses per-display+orientation keys with legacy dual-write.
SEARCH_BLOCK:
```
        if (keyboardContainer == null || keyboardParams == null) {
            // Save to prefs if hidden
            context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
                .putInt("keyboard_x_d$currentDisplayId", x)
                .putInt("keyboard_y_d$currentDisplayId", y)
                .apply()
            return
        }
```
REPLACE_BLOCK:
```
        if (keyboardContainer == null || keyboardParams == null) {
            // Save to prefs if hidden
            val os = orientSuffix()
            context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
                .putInt("keyboard_x_d${currentDisplayId}$os", x)
                .putInt("keyboard_y_d${currentDisplayId}$os", y)
                // Legacy (migration/back-compat)
                .putInt("keyboard_x_d$currentDisplayId", x)
                .putInt("keyboard_y_d$currentDisplayId", y)
                .apply()
            return
        }
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Ensure getters read per-display+orientation bounds with legacy fallback when hidden.
SEARCH_BLOCK:
```
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
```
REPLACE_BLOCK:
```
    fun getViewX(): Int {
        if (keyboardParams != null) return keyboardParams!!.x
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val os = orientSuffix()
        return prefs.getInt("keyboard_x_d${currentDisplayId}$os", prefs.getInt("keyboard_x_d$currentDisplayId", 0))
    }
    

    fun getViewY(): Int {
        if (keyboardParams != null) return keyboardParams!!.y
        val prefs = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val os = orientSuffix()
        return prefs.getInt("keyboard_y_d${currentDisplayId}$os", prefs.getInt("keyboard_y_d$currentDisplayId", 0))
    }
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Seed initial show() restore with orientation-keyed scale (not global legacy key).
SEARCH_BLOCK:
```
            // [NEW] Initialize internal scale from prefs once on show
            // Default to 69 (0.69f) if missing, to match resetPosition
            val savedScale = prefs.getInt("keyboard_key_scale", 69) / 100f
            internalScale = savedScale
            dragStartScale = savedScale // Init for safety
```
REPLACE_BLOCK:
```
            // [NEW] Initialize internal scale from prefs once on show
            // Default to 69 (0.69f) if missing, to match resetPosition
            val os = orientSuffix()
            val savedScale = getSavedScalePercent(prefs, os) / 100f
            internalScale = savedScale
            dragStartScale = savedScale // Init for safety
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Seed createKeyboardWindow scale from per-display+orientation key (not global legacy key).
SEARCH_BLOCK:
```
        // [FIX] Load saved scale and update Internal State immediately
        // Use 69 as default to match resetPosition logic (prevent 1.0 mismatch)
        val scale = prefs.getInt("keyboard_key_scale", 69) / 100f
        internalScale = scale 
        keyboardView?.setScale(scale)
```
REPLACE_BLOCK:
```
        // [FIX] Load saved scale and update Internal State immediately
        // Use 69 as default to match resetPosition logic (prevent 1.0 mismatch)
        val os = orientSuffix()
        val scale = getSavedScalePercent(prefs, os) / 100f
        internalScale = scale 
        keyboardView?.setScale(scale)
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Seed initial keyboard bounds per orientation with legacy fallback.
SEARCH_BLOCK:
```
        val savedW = prefs.getInt("keyboard_width_d$currentDisplayId", defaultWidth)
        val savedH = prefs.getInt("keyboard_height_d$currentDisplayId", defaultHeight)
        val savedX = prefs.getInt("keyboard_x_d$currentDisplayId", defaultX)
        val savedY = prefs.getInt("keyboard_y_d$currentDisplayId", defaultY)
```
REPLACE_BLOCK:
```
        val os = orientSuffix()
        val savedW = prefs.getInt("keyboard_width_d${currentDisplayId}$os", prefs.getInt("keyboard_width_d$currentDisplayId", defaultWidth))
        val savedH = prefs.getInt("keyboard_height_d${currentDisplayId}$os", prefs.getInt("keyboard_height_d$currentDisplayId", defaultHeight))
        val savedX = prefs.getInt("keyboard_x_d${currentDisplayId}$os", prefs.getInt("keyboard_x_d$currentDisplayId", defaultX))
        val savedY = prefs.getInt("keyboard_y_d${currentDisplayId}$os", prefs.getInt("keyboard_y_d$currentDisplayId", defaultY))
```
END_OF_UPDATE_BLOCK

