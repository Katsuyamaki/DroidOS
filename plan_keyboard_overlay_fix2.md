## Plan: Fix Kotlin compile error (duplicate `val os`)

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Avoid conflicting declarations by using a distinct variable for bounds-orientation keys in createKeyboardWindow().
SEARCH_BLOCK:
```
        // 3. Load from Prefs (using our calculated defaults as fallback)
        val os = orientSuffix()
        val savedW = prefs.getInt("keyboard_width_d${currentDisplayId}$os", prefs.getInt("keyboard_width_d$currentDisplayId", defaultWidth))
        val savedH = prefs.getInt("keyboard_height_d${currentDisplayId}$os", prefs.getInt("keyboard_height_d$currentDisplayId", defaultHeight))
        val savedX = prefs.getInt("keyboard_x_d${currentDisplayId}$os", prefs.getInt("keyboard_x_d$currentDisplayId", defaultX))
        val savedY = prefs.getInt("keyboard_y_d${currentDisplayId}$os", prefs.getInt("keyboard_y_d$currentDisplayId", defaultY))
```
REPLACE_BLOCK:
```
        // 3. Load from Prefs (using our calculated defaults as fallback)
        val boundsOs = orientSuffix()
        val savedW = prefs.getInt("keyboard_width_d${currentDisplayId}$boundsOs", prefs.getInt("keyboard_width_d$currentDisplayId", defaultWidth))
        val savedH = prefs.getInt("keyboard_height_d${currentDisplayId}$boundsOs", prefs.getInt("keyboard_height_d$currentDisplayId", defaultHeight))
        val savedX = prefs.getInt("keyboard_x_d${currentDisplayId}$boundsOs", prefs.getInt("keyboard_x_d$currentDisplayId", defaultX))
        val savedY = prefs.getInt("keyboard_y_d${currentDisplayId}$boundsOs", prefs.getInt("keyboard_y_d$currentDisplayId", defaultY))
```
END_OF_UPDATE_BLOCK

