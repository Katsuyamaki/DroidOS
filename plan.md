## Feasibility Plan: Hands‑Free “Read Screen + Tap/Type/Scroll” (TerminalAssistant ⇄ DroidOS / DroidOSPro)

This is a feasibility/architecture plan for sub‑agents to implement an “AI UI snapshot + action” bridge.

### Goal
- Termux/TerminalAssistant can ask “what’s on screen?” and request actions (tap/scroll/type).
- DroidOS (FOSS) and/or DroidOSPro executes actions via its privileged bridge and returns structured results back into the TerminalAssistant tmux session.

### Recommended implementation path (reuse what you already built)
- **Control channel:** Termux uses `am broadcast` (same pattern as `ta-droidospro-broadcast.sh`) into the launcher.
- **Return channel:** Launcher uses the existing Termux `RUN_COMMAND` bridge (present in Pro today; if missing in FOSS, add an equivalent small bridge) to inject snapshot/action results back into the tmux assistant pane.
- **Sensor priority:** Accessibility window snapshot first; UIAutomator dump XML second; screenshot+OCR last.
- **Actuators:** use privileged shell bridge to call `input -d <displayId> tap/swipe/text` (or equivalent injection service).

### New code requirement (keep modular; new separate `.kt`)
- Add a dedicated file (example name):
  - `DroidOSLauncher/.../ai/AiUiBridge.kt` (or the FOSS launcher’s equivalent module path)
- Responsibilities:
  - Parse incoming broadcast intents.
  - Build a compact “UI snapshot” (elements with label/text + clickable + bounds).
  - Execute requested actions (tap/scroll/type) through the shell bridge.
  - Return results to Termux through the bridge.

### Security
- Gate these actions behind an “enabled” toggle and/or sender allowlist (Termux + your own packages).

---

FILE_UPDATE: Cover-Screen-Launcher/app/build.gradle.kts
REASON: Update namespace and applicationId to DroidOSFOSSLauncher
SEARCH_BLOCK:
```
    namespace = "com.example.quadrantlauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.katsuyamaki.DroidOSLauncher"
```
REPLACE_BLOCK:
```
    namespace = "com.katsuyamaki.DroidOSFOSSLauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.katsuyamaki.DroidOSFOSSLauncher"
```
END_OF_UPDATE_BLOCK

---

## Fix Plan: Keyboard overlay wrong dimensions after hide/restore (DroidOS F-Droid build)

### Symptom (reported)
- After the keyboard overlay is hidden and later restored, it will sometimes reappear with the wrong dimensions/aspect ratio and show excess internal gaps within the keyboard overlay.

### What DroidOSPro changed (source of truth)
- In `DroidOSPro/DroidOSKeyboardTrackpad/.../KeyboardOverlay.kt` the persisted **window bounds** and **key scale** are saved/loaded **per display AND per orientation** using an `orientSuffix()` ( `_L` / `_P` ) suffix.
- `setScreenDimensions(width, height, displayId)` **reloads the saved scale for that display+orientation** before computing the “300dp * scale * density” height, preventing stale scale causing a wrong height on restore/rotation.
- Related persistence keys in Pro look like:
  - `keyboard_x_d${displayId}${orientSuffix()}`
  - `keyboard_y_d${displayId}${orientSuffix()}`
  - `keyboard_width_d${displayId}${orientSuffix()}`
  - `keyboard_height_d${displayId}${orientSuffix()}`
  - `keyboard_key_scale_d${displayId}${orientSuffix()}`

### Root cause hypothesis for FOSS (current DroidOS)
- FOSS currently persists:
  - bounds as `keyboard_*_d${displayId}` (no orientation suffix)
  - key scale as a single global `keyboard_key_scale`
- If the device rotates (or window metrics change) while the overlay is hidden, restore can load **portrait-sized bounds** into **landscape**, or apply a stale global scale, leading to incorrect overlay sizing and visible gaps.

### Proposed minimal port into DroidOS (do not touch DroidOSPro)

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Persist and restore keyboard overlay bounds + key scale per-display *and* per-orientation, matching DroidOSPro behavior, to eliminate intermittent wrong dimensions after hide/restore.
NOTE (reviewer): `saveKeyboardScale()` already exists in FOSS; update it (don’t add a duplicate).

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

VALIDATION:
- Manual: toggle keyboard hide/show repeatedly; rotate device while hidden; confirm restored overlay matches expected size without internal gaps.
- Sanity: ensure existing users’ stored keyboard size/position still loads via legacy fallback.


FILE_UPDATE: Cover-Screen-Trackpad/app/build.gradle.kts
REASON: Update namespace and applicationId to DroidOSFOSSKeyboardTrackpad
SEARCH_BLOCK:
```
    namespace = "com.example.coverscreentester"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.katsuyamaki.DroidOSTrackpadKeyboard"
```
REPLACE_BLOCK:
```
    namespace = "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad"
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/proguard-rules.pro
REASON: Update proguard keep rules to new FOSS package name
SEARCH_BLOCK:
```
-keep class com.example.quadrantlauncher.ShellUserService { *; }
-keep class com.example.quadrantlauncher.IShellService { *; }
-keep interface com.example.quadrantlauncher.IShellService { *; }
```
REPLACE_BLOCK:
```
-keep class com.katsuyamaki.DroidOSFOSSLauncher.ShellUserService { *; }
-keep class com.katsuyamaki.DroidOSFOSSLauncher.IShellService { *; }
-keep interface com.katsuyamaki.DroidOSFOSSLauncher.IShellService { *; }
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/aidl/com/example/quadrantlauncher/IShellService.aidl
REASON: Update AIDL package declaration to new FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher;
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher;
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/aidl/com/example/coverscreentester/IShellService.aidl
REASON: Update AIDL package declaration to new FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester;
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad;
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/AndroidManifest.xml
REASON: Add permission declaration, cross-app permission usage, and queries entry for FOSS keyboard trackpad package
SEARCH_BLOCK:
```
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```
REPLACE_BLOCK:
```
    <permission
        android:name="com.katsuyamaki.DroidOSFOSSLauncher.permission.INTERNAL_COMMAND"
        android:protectionLevel="signature" />

    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/AndroidManifest.xml
REASON: Add queries entry for new FOSS keyboard trackpad package (Android 11+ visibility)
SEARCH_BLOCK:
```
    <queries>
        <package android:name="moe.shizuku.privileged.api" />
        <package android:name="rikka.shizuku.ui" />
    </queries>
```
REPLACE_BLOCK:
```
    <queries>
        <package android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad" />
        <package android:name="moe.shizuku.privileged.api" />
        <package android:name="rikka.shizuku.ui" />
    </queries>
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/AndroidManifest.xml
REASON: Add permission declaration and cross-app permission/queries for FOSS launcher; update all action strings from old package names to FOSS names
SEARCH_BLOCK:
```
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <queries>
        <package android:name="moe.shizuku.privileged.api" />
        <package android:name="rikka.shizuku.ui" />
```
REPLACE_BLOCK:
```
    <permission
        android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.permission.INTERNAL_COMMAND"
        android:protectionLevel="signature" />

    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="com.katsuyamaki.DroidOSFOSSLauncher.permission.INTERNAL_COMMAND" />

    <queries>
        <package android:name="com.katsuyamaki.DroidOSFOSSLauncher" />
        <package android:name="moe.shizuku.privileged.api" />
        <package android:name="rikka.shizuku.ui" />
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/AndroidManifest.xml
REASON: Update InterAppCommandReceiver action strings and ADB comment from old package names to FOSS names; add permission guard
SEARCH_BLOCK:
```
        <!-- =================================================================================
             INTER-APP COMMAND RECEIVER
             SUMMARY: Static receiver to handle commands from DroidOS Launcher and ADB.
                      Allows soft restart, z-order fixes, and virtual display coordination
                      without requiring the Activity to be in foreground.
             USAGE (ADB): adb shell am broadcast -a com.example.coverscreentester.SOFT_RESTART
             ================================================================================= -->
                <receiver android:name=".InterAppCommandReceiver" android:enabled="true" android:exported="true">
                    <intent-filter>
                        <action android:name="com.example.coverscreentester.SOFT_RESTART" />
                        <action android:name="com.example.coverscreentester.MOVE_TO_VIRTUAL" />
                        <action android:name="com.example.coverscreentester.RETURN_TO_PHYSICAL" />
                        <action android:name="com.example.coverscreentester.ENFORCE_ZORDER" />
                        <action android:name="com.example.coverscreentester.TOGGLE_VIRTUAL_MIRROR" />
                        <action android:name="com.example.coverscreentester.OPEN_DRAWER" />
                        <action android:name="com.example.coverscreentester.GET_STATUS" />
                        <action android:name="com.example.coverscreentester.STOP_SERVICE" />
        
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.SOFT_RESTART" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.MOVE_TO_VIRTUAL" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.RETURN_TO_PHYSICAL" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.ENFORCE_ZORDER" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.TOGGLE_VIRTUAL_MIRROR" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.OPEN_DRAWER" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.GET_STATUS" />
                        <action android:name="com.katsuyamaki.DroidOSTrackpadKeyboard.STOP_SERVICE" />
                    </intent-filter>
                </receiver>
        <!-- END BLOCK: INTER-APP COMMAND RECEIVER -->
```
REPLACE_BLOCK:
```
        <!-- =================================================================================
             INTER-APP COMMAND RECEIVER
             SUMMARY: Static receiver to handle commands from DroidOS Launcher and ADB.
                      Allows soft restart, z-order fixes, and virtual display coordination
                      without requiring the Activity to be in foreground.
             USAGE (ADB): adb shell am broadcast -a com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.SOFT_RESTART
             ================================================================================= -->
                <receiver
                    android:name=".InterAppCommandReceiver"
                    android:enabled="true"
                    android:exported="true"
                    android:permission="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.permission.INTERNAL_COMMAND">
                    <intent-filter>
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.SOFT_RESTART" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.MOVE_TO_VIRTUAL" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.RETURN_TO_PHYSICAL" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.ENFORCE_ZORDER" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.TOGGLE_VIRTUAL_MIRROR" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.OPEN_DRAWER" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.GET_STATUS" />
                        <action android:name="com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.STOP_SERVICE" />
                    </intent-filter>
                </receiver>
        <!-- END BLOCK: INTER-APP COMMAND RECEIVER -->
```
END_OF_UPDATE_BLOCK


FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/AppPreferences.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/IconPickerActivity.kt
REASON: Update package declaration and broadcast action to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/IconPickerActivity.kt
REASON: Update broadcast action string to FOSS launcher package
SEARCH_BLOCK:
```
                sendBroadcast(Intent("com.example.quadrantlauncher.UPDATE_ICON"))
```
REPLACE_BLOCK:
```
                sendBroadcast(Intent("com.katsuyamaki.DroidOSFOSSLauncher.UPDATE_ICON"))
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/MainActivity.kt
REASON: Update package declaration and constant to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/MainActivity.kt
REASON: Update SELECTED_APP_PACKAGE constant to FOSS launcher package
SEARCH_BLOCK:
```
        const val SELECTED_APP_PACKAGE = "com.example.quadrantlauncher.SELECTED_APP_PACKAGE"
```
REPLACE_BLOCK:
```
        const val SELECTED_APP_PACKAGE = "com.katsuyamaki.DroidOSFOSSLauncher.SELECTED_APP_PACKAGE"
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/MenuActivity.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/PermissionActivity.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/QuadrantActivity.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShizukuHelper.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/SplitActivity.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/TriSplitActivity.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK


FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
REASON: Update all cross-app references from old coverscreentester package to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
REASON: Update all action strings from old DroidOSTrackpadKeyboard to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSTrackpadKeyboard
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
REASON: Update all launcher action strings from old DroidOSLauncher to FOSS launcher package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSLauncher
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShellUserService.kt
REASON: Update package declaration to FOSS launcher package
SEARCH_BLOCK:
```
package com.example.quadrantlauncher
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShellUserService.kt
REASON: Update coverscreentester cross-app reference to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShellUserService.kt
REASON: Update DroidOSLauncher self-reference to FOSS launcher package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSLauncher
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK


FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/InterAppCommandReceiver.kt
REASON: Update package declaration and all action string constants from old coverscreentester package to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardManager.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Update cross-app launcher references to FOSS launcher package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSLauncher
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardPickerActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardUtils.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardView.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardView.kt
REASON: Update cross-app launcher references to FOSS launcher package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSLauncher
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/MainActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ManualAdjustActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/NullInputMethodService.kt
REASON: Update package declaration and all inject action strings from old coverscreentester to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Update package declaration and all self-references from old coverscreentester to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Update cross-app launcher references to FOSS launcher package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSLauncher
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSLauncher
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Update old DroidOSTrackpadKeyboard references to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.katsuyamaki.DroidOSTrackpadKeyboard
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/PredictionEngine.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ProfilesActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/SettingsActivity.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ShellUserService.kt
REASON: Update package declaration and IShellService import from old coverscreentester to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
com.example.coverscreentester
```
REPLACE_BLOCK:
```
com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ShizukuInputHandler.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/SwipeTrailView.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadMenuAdapter.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadMenuManager.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadPrefs.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadService.kt
REASON: Update package declaration to FOSS keyboard trackpad package
SEARCH_BLOCK:
```
package com.example.coverscreentester
```
REPLACE_BLOCK:
```
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad
```
END_OF_UPDATE_BLOCK


FILE_CREATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/DroidOSConstants.kt
REASON: Centralized package names and action strings for easy future migration (mirrors DroidOSPro pattern)
CONTENT_BLOCK:
```kotlin
package com.katsuyamaki.DroidOSFOSSLauncher

/**
 * Centralized constants for DroidOS FOSS inter-app communication.
 * All package names and broadcast action strings live here so a future
 * package rename only requires changing this file + manifests + gradle.
 */
object DroidOSConstants {

    // ── Package identifiers ──
    const val LAUNCHER_PACKAGE = "com.katsuyamaki.DroidOSFOSSLauncher"
    const val KEYBOARD_PACKAGE = "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad"

    // ── Launcher action strings ──
    const val ACTION_TOGGLE_VIRTUAL_DISPLAY = "$LAUNCHER_PACKAGE.TOGGLE_VIRTUAL_DISPLAY"
    const val ACTION_OPEN_DRAWER           = "$LAUNCHER_PACKAGE.OPEN_DRAWER"
    const val ACTION_UPDATE_ICON           = "$LAUNCHER_PACKAGE.UPDATE_ICON"
    const val ACTION_CYCLE_DISPLAY         = "$LAUNCHER_PACKAGE.CYCLE_DISPLAY"
    const val ACTION_WINDOW_MANAGER        = "$LAUNCHER_PACKAGE.WINDOW_MANAGER"
    const val ACTION_REQUEST_CUSTOM_MOD_SYNC = "$LAUNCHER_PACKAGE.REQUEST_CUSTOM_MOD_SYNC"
    const val ACTION_REMOTE_KEY            = "$LAUNCHER_PACKAGE.REMOTE_KEY"
    const val ACTION_REQUEST_KEYBINDS      = "$LAUNCHER_PACKAGE.REQUEST_KEYBINDS"
    const val ACTION_SYNC_KEYBOARD_RATIO   = "$LAUNCHER_PACKAGE.SYNC_KEYBOARD_RATIO"

    // ── Launcher permission ──
    const val PERMISSION_LAUNCHER_INTERNAL = "$LAUNCHER_PACKAGE.permission.INTERNAL_COMMAND"

    // ── Keyboard action strings ──
    const val KB_ACTION_INJECT_TEXT        = "$KEYBOARD_PACKAGE.INJECT_TEXT"
    const val KB_ACTION_INJECT_KEY         = "$KEYBOARD_PACKAGE.INJECT_KEY"
    const val KB_ACTION_INJECT_DELETE      = "$KEYBOARD_PACKAGE.INJECT_DELETE"
    const val KB_ACTION_SOFT_RESTART       = "$KEYBOARD_PACKAGE.SOFT_RESTART"
    const val KB_ACTION_MOVE_TO_VIRTUAL    = "$KEYBOARD_PACKAGE.MOVE_TO_VIRTUAL"
    const val KB_ACTION_RETURN_TO_PHYSICAL = "$KEYBOARD_PACKAGE.RETURN_TO_PHYSICAL"
    const val KB_ACTION_ENFORCE_ZORDER     = "$KEYBOARD_PACKAGE.ENFORCE_ZORDER"
    const val KB_ACTION_TOGGLE_VIRTUAL_MIRROR = "$KEYBOARD_PACKAGE.TOGGLE_VIRTUAL_MIRROR"
    const val KB_ACTION_OPEN_DRAWER        = "$KEYBOARD_PACKAGE.OPEN_DRAWER"
    const val KB_ACTION_GET_STATUS         = "$KEYBOARD_PACKAGE.GET_STATUS"
    const val KB_ACTION_STOP_SERVICE       = "$KEYBOARD_PACKAGE.STOP_SERVICE"
    const val KB_ACTION_MOVE_TO_DISPLAY    = "$KEYBOARD_PACKAGE.MOVE_TO_DISPLAY"
    const val KB_ACTION_SET_INPUT_CAPTURE  = "$KEYBOARD_PACKAGE.SET_INPUT_CAPTURE"
    const val KB_ACTION_SET_NUM_LAYER      = "$KEYBOARD_PACKAGE.SET_NUM_LAYER"
    const val KB_ACTION_SET_CUSTOM_MOD     = "$KEYBOARD_PACKAGE.SET_CUSTOM_MOD"
    const val KB_ACTION_SET_TRACKPAD_VISIBILITY = "$KEYBOARD_PACKAGE.SET_TRACKPAD_VISIBILITY"
    const val KB_ACTION_UPDATE_KEYBINDS    = "$KEYBOARD_PACKAGE.UPDATE_KEYBINDS"

    // ── Keyboard permission ──
    const val PERMISSION_KEYBOARD_INTERNAL = "$KEYBOARD_PACKAGE.permission.INTERNAL_COMMAND"

    // ── IME component IDs ──
    const val NULL_IME_ID = "$KEYBOARD_PACKAGE.NullInputMethodService"
    const val OVERLAY_SERVICE_CLASS = "$KEYBOARD_PACKAGE.OverlayService"
}
```
END_OF_UPDATE_BLOCK

FILE_CREATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/DroidOSConstants.kt
REASON: Mirror constants file in keyboard app for cross-app communication (mirrors DroidOSPro pattern)
CONTENT_BLOCK:
```kotlin
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad

/**
 * Centralized constants for DroidOS FOSS inter-app communication.
 * All package names and broadcast action strings live here so a future
 * package rename only requires changing this file + manifests + gradle.
 */
object DroidOSConstants {

    // ── Package identifiers ──
    const val LAUNCHER_PACKAGE = "com.katsuyamaki.DroidOSFOSSLauncher"
    const val KEYBOARD_PACKAGE = "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad"

    // ── Launcher action strings ──
    const val ACTION_TOGGLE_VIRTUAL_DISPLAY = "$LAUNCHER_PACKAGE.TOGGLE_VIRTUAL_DISPLAY"
    const val ACTION_OPEN_DRAWER           = "$LAUNCHER_PACKAGE.OPEN_DRAWER"
    const val ACTION_UPDATE_ICON           = "$LAUNCHER_PACKAGE.UPDATE_ICON"
    const val ACTION_CYCLE_DISPLAY         = "$LAUNCHER_PACKAGE.CYCLE_DISPLAY"
    const val ACTION_WINDOW_MANAGER        = "$LAUNCHER_PACKAGE.WINDOW_MANAGER"
    const val ACTION_REQUEST_CUSTOM_MOD_SYNC = "$LAUNCHER_PACKAGE.REQUEST_CUSTOM_MOD_SYNC"
    const val ACTION_REMOTE_KEY            = "$LAUNCHER_PACKAGE.REMOTE_KEY"
    const val ACTION_REQUEST_KEYBINDS      = "$LAUNCHER_PACKAGE.REQUEST_KEYBINDS"
    const val ACTION_SYNC_KEYBOARD_RATIO   = "$LAUNCHER_PACKAGE.SYNC_KEYBOARD_RATIO"

    // ── Launcher permission ──
    const val PERMISSION_LAUNCHER_INTERNAL = "$LAUNCHER_PACKAGE.permission.INTERNAL_COMMAND"

    // ── Keyboard action strings ──
    const val KB_ACTION_INJECT_TEXT        = "$KEYBOARD_PACKAGE.INJECT_TEXT"
    const val KB_ACTION_INJECT_KEY         = "$KEYBOARD_PACKAGE.INJECT_KEY"
    const val KB_ACTION_INJECT_DELETE      = "$KEYBOARD_PACKAGE.INJECT_DELETE"
    const val KB_ACTION_SOFT_RESTART       = "$KEYBOARD_PACKAGE.SOFT_RESTART"
    const val KB_ACTION_MOVE_TO_VIRTUAL    = "$KEYBOARD_PACKAGE.MOVE_TO_VIRTUAL"
    const val KB_ACTION_RETURN_TO_PHYSICAL = "$KEYBOARD_PACKAGE.RETURN_TO_PHYSICAL"
    const val KB_ACTION_ENFORCE_ZORDER     = "$KEYBOARD_PACKAGE.ENFORCE_ZORDER"
    const val KB_ACTION_TOGGLE_VIRTUAL_MIRROR = "$KEYBOARD_PACKAGE.TOGGLE_VIRTUAL_MIRROR"
    const val KB_ACTION_OPEN_DRAWER        = "$KEYBOARD_PACKAGE.OPEN_DRAWER"
    const val KB_ACTION_GET_STATUS         = "$KEYBOARD_PACKAGE.GET_STATUS"
    const val KB_ACTION_STOP_SERVICE       = "$KEYBOARD_PACKAGE.STOP_SERVICE"
    const val KB_ACTION_MOVE_TO_DISPLAY    = "$KEYBOARD_PACKAGE.MOVE_TO_DISPLAY"
    const val KB_ACTION_SET_INPUT_CAPTURE  = "$KEYBOARD_PACKAGE.SET_INPUT_CAPTURE"
    const val KB_ACTION_SET_NUM_LAYER      = "$KEYBOARD_PACKAGE.SET_NUM_LAYER"
    const val KB_ACTION_SET_CUSTOM_MOD     = "$KEYBOARD_PACKAGE.SET_CUSTOM_MOD"
    const val KB_ACTION_SET_TRACKPAD_VISIBILITY = "$KEYBOARD_PACKAGE.SET_TRACKPAD_VISIBILITY"
    const val KB_ACTION_UPDATE_KEYBINDS    = "$KEYBOARD_PACKAGE.UPDATE_KEYBINDS"

    // ── Keyboard permission ──
    const val PERMISSION_KEYBOARD_INTERNAL = "$KEYBOARD_PACKAGE.permission.INTERNAL_COMMAND"

    // ── IME component IDs ──
    const val NULL_IME_ID = "$KEYBOARD_PACKAGE.NullInputMethodService"
    const val OVERLAY_SERVICE_CLASS = "$KEYBOARD_PACKAGE.OverlayService"
}
```
END_OF_UPDATE_BLOCK

FILE_CREATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/ShizukuBinder.kt
REASON: Convert ShizukuBinder from Java to Kotlin to avoid directory-package mismatch (mirrors DroidOSPro pattern)
CONTENT_BLOCK:
```kotlin
package com.katsuyamaki.DroidOSFOSSLauncher

import android.content.ComponentName
import android.content.ServiceConnection
import rikka.shizuku.Shizuku

object ShizukuBinder {

    fun bind(component: ComponentName, connection: ServiceConnection, debug: Boolean, version: Int) {
        val args = Shizuku.UserServiceArgs(component)
            .processNameSuffix("shell")
            .daemon(false)
            .debuggable(debug)
            .version(version)
        Shizuku.bindUserService(args, connection)
    }

    fun unbind(component: ComponentName, connection: ServiceConnection) {
        val args = Shizuku.UserServiceArgs(component)
            .processNameSuffix("shell")
            .daemon(false)
        Shizuku.unbindUserService(args, connection, true)
    }
}
```
END_OF_UPDATE_BLOCK

FILE_CREATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/ShizukuBinder.kt
REASON: Convert ShizukuBinder from Java to Kotlin to avoid directory-package mismatch (mirrors DroidOSPro pattern)
CONTENT_BLOCK:
```kotlin
package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad

import android.content.ComponentName
import android.content.ServiceConnection
import rikka.shizuku.Shizuku

object ShizukuBinder {

    fun bind(component: ComponentName, connection: ServiceConnection, debug: Boolean, version: Int) {
        val args = Shizuku.UserServiceArgs(component)
            .processNameSuffix("shell")
            .daemon(false)
            .debuggable(debug)
            .version(version)
        Shizuku.bindUserService(args, connection)
    }

    fun unbind(component: ComponentName, connection: ServiceConnection) {
        val args = Shizuku.UserServiceArgs(component)
            .processNameSuffix("shell")
            .daemon(false)
        Shizuku.unbindUserService(args, connection, true)
    }
}
```
END_OF_UPDATE_BLOCK


FILE_UPDATE: Cover-Screen-Launcher/app/src/main/java/com/example/quadrantlauncher/FloatingLauncherService.kt
REASON: Remove legacy fallback block that became redundant after package rename (duplicates Step 1)
SEARCH_BLOCK:
```
        // 3. Legacy Fallback (Old Package Name)
        try {
            val oldIntent = Intent()
            oldIntent.component = ComponentName(
                "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad", 
                "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.OverlayService"
            )
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(oldIntent) else startService(oldIntent)
            return
        } catch (e: Exception) {
            // Legacy Service failed -> Try Legacy Activity
            try {
                val oldLaunchIntent = packageManager.getLaunchIntentForPackage("com.katsuyamaki.DroidOSFOSSKeyboardTrackpad")
                if (oldLaunchIntent != null) {
                    oldLaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(oldLaunchIntent)
                    return
                }
            } catch (e2: Exception) {}
        }

        // 4. Final Resort: Broadcast (Only works if app is already alive)
```
REPLACE_BLOCK:
```
        // 3. Final Resort: Broadcast (Only works if app is already alive)
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Deduplicate prefix list in commandFilter registration (both became identical after rename)
SEARCH_BLOCK:
```
            val prefixes = listOf("com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.", "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.")
```
REPLACE_BLOCK:
```
            val prefixes = listOf("com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.")
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Deduplicate prefix list in accessibility event filter registration (both became identical after rename)
SEARCH_BLOCK:
```
                        val prefixes = listOf(
                            "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad.",
                            "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad."
                        )
```
REPLACE_BLOCK:
```
                        val prefixes = listOf(
                            "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad."
                        )
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Update dual-prefix comment to reflect single FOSS prefix
SEARCH_BLOCK:
```
        // Register BOTH new and old prefixes to support all scripts/buttons
```
REPLACE_BLOCK:
```
        // Register FOSS package prefix for all command actions
```
END_OF_UPDATE_BLOCK


---

## Reviewer Mode: Final Review of Keyboard Overlay Fix Plan

Current Patch plan has not yet been processed by builder.py. Here are my comments and feedback on plan.md we need to address before implementation:

* **Anchor verification**: All SEARCH_BLOCKs for the keyboard-overlay fix anchor to FOSS source byte-for-byte:
  - `setScreenDimensions` block ↔ `KeyboardOverlay.kt:177-213` ✓
  - Helpers block (`saveKeyboardSize`/`saveKeyboardScale`/`saveKeyboardPosition`/`loadKeyboardSizeForDisplay`) ↔ `KeyboardOverlay.kt:1488-1494` ✓
  - `setWindowBounds` hidden-save ↔ `KeyboardOverlay.kt:272-281` ✓
  - `updatePosition` hidden-save ↔ `KeyboardOverlay.kt:293-300` ✓
  - `getViewX`/`getViewY` ↔ `KeyboardOverlay.kt:327-338` ✓
  - `createKeyboardWindow` initial savedW/H/X/Y ↔ `KeyboardOverlay.kt:1248-1251` ✓
  - `show()` scale init ↔ `KeyboardOverlay.kt:514-518` ✓
  - `createKeyboardWindow` scale load ↔ `KeyboardOverlay.kt:1198-1202` ✓

* **Per-display + per-orientation coverage**: Every write/read of bounds (x/y/width/height) and scale on the live code paths now uses `keyboard_*_d${currentDisplayId}${orientSuffix()}` with chained legacy fallback for reads and dual-write (suffixed + legacy) for writes. ✓

* **`orientSuffix()` ordering**: Computed lazily inside each method, after `screenWidth/Height` updates have flowed through `setScreenDimensions`. No stale-orientation write hazards on the patched paths. ✓

* **`setScreenDimensions` scale propagation**: Now reloads orient-keyed scale, assigns to `internalScale`, and calls `keyboardView?.setScale(savedScale)` so the inner view doesn’t hold stale scale on rotation/restore. ✓

* **`saveKeyboardScale()` is updated, not duplicated** — patch correctly modifies the existing function at `:1489-1492`. ✓

* **Migration / fallback safety**: Read-side chained fallback (`getInt(suffixed, getInt(legacy, default))`) preserves existing users’ bounds on first launch after upgrade. Dual-write keeps legacy as last-known-state for any downgrade. Acceptable trade-off; legacy key tracks most-recent-orientation rather than being orientation-agnostic, but since suffixed key is always preferred when present, no observable regression.

* **Refactor logic integrity**: All REPLACE_BLOCKs preserve original control flow, exception handling, default values (69 for scale, computed defaults for bounds), and side effects (`syncMirrorRatio`, `windowManager.updateViewLayout`, `keyboardView?.setScale`). No behavior change beyond the prefs-key migration.

* **Whitespace sensitivity**: SEARCH_BLOCKs reproduce the source’s exact indentation (4-space class-body; trailing space on `internalScale = scale ` at `:1201` is preserved verbatim in the search anchor at plan `:252`). ✓

* **Kotlin `$` variable safety inside `cat << 'EOF'`**: Plan was authored with a quoted heredoc-style block (`cat << 'EOF' > plan.md`), so `${currentDisplayId}` and `$os` interpolations are preserved as literal Kotlin source rather than being shell-expanded. ✓

* **Optional / minor (non-blocking)**: `KeyboardOverlay.kt:1240` (`val savedScale = prefs.getInt("keyboard_key_scale", 69) / 100f` inside the `defaultHeight` fallback) still reads the legacy key directly. After the bounds chained-fallback patch this code path is reached only on truly fresh installs (no suffixed and no legacy bounds keys), so impact is negligible. Recommend migrating to `getSavedScalePercent(prefs)` in a follow-up for consistency, but this is not required for correctness of the reported symptom.

* **VALIDATION expansion (recommended additions)**:
  - Set distinct scales in portrait and landscape, hide overlay, rotate, show — confirm correct per-orientation scale on first show without an extra rotation event.
  - Set distinct bounds per orientation, hide, rotate, show — confirm correct per-orientation bounds on `createKeyboardWindow` first-show path.
  - Upgrade-from-legacy install: verify pre-upgrade bounds load via legacy fallback on first show; first save creates suffixed keys; subsequent rotation does not lose either orientation’s values.
  - Multi-display (cover screen + virtual mirror): verify per-display key isolation across orientation changes.

* **Verdict: PASS**. The patch plan is implementable as-is; bounds + scale persistence is now per-display × per-orientation across every live read/write site, with safe legacy migration. Builder.py can apply.

END_OF_REVIEW_BLOCK
