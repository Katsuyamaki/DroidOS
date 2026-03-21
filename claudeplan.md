# Debug Bubble Icon Auto Margin

## Problem
logOverlayKbDiag is empty so no logs appear. Need direct logging.

---

FILE_UPDATE: DroidOSKeyboardTrackpad/app/src/main/java/com/katsuyamaki/DroidOSTrackpadKeyboard/OverlayService.kt
REASON: Add direct logging to debug bubble icon auto margin path
SEARCH_BLOCK:
```kotlin
        val currentIme = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.DEFAULT_INPUT_METHOD)
        val isDockIMEConfigured = currentIme?.contains("DroidOSTrackpadKeyboard") == true
        val dockModePrefs = getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE)
        val isDockModeEnabled = dockModePrefs.getBoolean("dock_mode_d$currentDisplayId", dockModePrefs.getBoolean("dock_mode", false))
        if (isNowVisible && prefs.prefShowKBAboveDock && isDockIMEConfigured && isDockModeEnabled) {
```
REPLACE_BLOCK:
```kotlin
        val currentIme = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.DEFAULT_INPUT_METHOD)
        val isDockIMEConfigured = currentIme?.contains("DroidOSTrackpadKeyboard") == true
        val dockModePrefs = getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE)
        val isDockModeEnabled = dockModePrefs.getBoolean("dock_mode_d$currentDisplayId", dockModePrefs.getBoolean("dock_mode", false))
        android.util.Log.d("KBBlocker", "toggleKB: isNowVisible=$isNowVisible showAboveDock=${prefs.prefShowKBAboveDock} dockImeConfigured=$isDockIMEConfigured dockModeEnabled=$isDockModeEnabled displayId=$currentDisplayId lastMargin=$lastDockMarginPercent")
        if (isNowVisible && prefs.prefShowKBAboveDock && isDockIMEConfigured && isDockModeEnabled) {
```
END_OF_UPDATE_BLOCK

