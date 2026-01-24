package com.example.quadrantlauncher

import android.content.Context

object AppPreferences {

    private const val PREFS_NAME = "AppLauncherPrefs"
    private const val KEY_FAVORITES = "KEY_FAVORITES"
    private const val KEY_LAST_LAYOUT = "KEY_LAST_LAYOUT"
    private const val KEY_LAST_CUSTOM_LAYOUT_NAME = "KEY_LAST_CUSTOM_LAYOUT_NAME"
    private const val KEY_PROFILES = "KEY_PROFILES"
    private const val KEY_CUSTOM_LAYOUTS = "KEY_CUSTOM_LAYOUTS"
    private const val KEY_FONT_SIZE = "KEY_FONT_SIZE"
    private const val KEY_ICON_URI = "KEY_ICON_URI"
    
    // Settings
    private const val KEY_KILL_ON_EXECUTE = "KEY_KILL_ON_EXECUTE"
    private const val KEY_TARGET_DISPLAY_INDEX = "KEY_TARGET_DISPLAY_INDEX"
    private const val KEY_IS_INSTANT_MODE = "KEY_IS_INSTANT_MODE"
    private const val KEY_LAST_QUEUE = "KEY_LAST_QUEUE"
    private const val KEY_SHOW_SHIZUKU_WARNING = "KEY_SHOW_SHIZUKU_WARNING"
    private const val KEY_REORDER_TIMEOUT = "KEY_REORDER_TIMEOUT"
    private const val KEY_USE_ALT_SCREEN_OFF = "KEY_USE_ALT_SCREEN_OFF" // New
    private const val KEY_AUTO_RESTART_TRACKPAD = "KEY_AUTO_RESTART_TRACKPAD"
    private const val KEY_AUTO_RESTART_ON_CLOSE = "KEY_AUTO_RESTART_ON_CLOSE"
    private const val KEY_LAUNCHER_ACTIVE = "KEY_LAUNCHER_ACTIVE"

    // === BLACKLIST STORAGE - START ===
    // Stores blacklisted apps using "packageName:activityName" format
    // This allows us to blacklist "com.google.android.googlequicksearchbox:.SearchActivity"
    // while keeping "com.google.android.googlequicksearchbox:robin.main.MainActivity" (Gemini) available
    private const val KEY_BLACKLIST = "KEY_BLACKLIST"
    // === BLACKLIST STORAGE - END ===

    // Reorder Methods
    private const val KEY_REORDER_METHOD_DRAG = "KEY_REORDER_METHOD_DRAG"
    private const val KEY_REORDER_METHOD_TAP = "KEY_REORDER_METHOD_TAP"
    private const val KEY_REORDER_METHOD_SCROLL = "KEY_REORDER_METHOD_SCROLL"
    
    // Drawer Geometry
    private const val KEY_DRAWER_HEIGHT = "KEY_DRAWER_HEIGHT"
    private const val KEY_DRAWER_WIDTH = "KEY_DRAWER_WIDTH"
    private const val KEY_AUTO_RESIZE_KEYBOARD = "KEY_AUTO_RESIZE_KEYBOARD"
    
    // Custom Resolutions
    private const val KEY_CUSTOM_RESOLUTION_NAMES = "KEY_CUSTOM_RESOLUTION_NAMES"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun savePackage(context: Context, key: String, packageName: String) {
        getPrefs(context).edit().putString(key, packageName).apply()
    }

    fun loadPackage(context: Context, key: String): String? {
        return getPrefs(context).getString(key, null)
    }

    fun getSimpleName(pkg: String?): String {
        if (pkg == null) return "Select App"
        val name = pkg.substringAfterLast('.')
        return if (name.isNotEmpty()) name else pkg
    }

    fun getFavorites(context: Context): MutableSet<String> {
        return getPrefs(context).getStringSet(KEY_FAVORITES, mutableSetOf()) ?: mutableSetOf()
    }

    fun isFavorite(context: Context, packageName: String): Boolean {
        return getFavorites(context).contains(packageName)
    }

    fun toggleFavorite(context: Context, packageName: String): Boolean {
        val favorites = getFavorites(context)
        val newSet = HashSet(favorites)
        val isAdded: Boolean
        if (newSet.contains(packageName)) {
            newSet.remove(packageName)
            isAdded = false
        } else {
            newSet.add(packageName)
            isAdded = true
        }
        getPrefs(context).edit().putStringSet(KEY_FAVORITES, newSet).apply()
        return isAdded
    }
    
    // --- GLOBAL LAYOUT PREFS ---
    fun saveLastLayout(context: Context, layoutId: Int) {
        getPrefs(context).edit().putInt(KEY_LAST_LAYOUT, layoutId).apply()
    }

    fun getLastLayout(context: Context): Int {
        return getPrefs(context).getInt(KEY_LAST_LAYOUT, 2)
    }
    
    fun saveLastCustomLayoutName(context: Context, name: String?) {
        getPrefs(context).edit().putString(KEY_LAST_CUSTOM_LAYOUT_NAME, name).apply()
    }

    fun getLastCustomLayoutName(context: Context): String? {
        return getPrefs(context).getString(KEY_LAST_CUSTOM_LAYOUT_NAME, null)
    }

    // --- PER-DISPLAY SETTINGS ---
    
    fun saveDisplayResolution(context: Context, displayId: Int, resIndex: Int) {
        getPrefs(context).edit().putInt("RES_D$displayId", resIndex).apply()
    }

    fun getDisplayResolution(context: Context, displayId: Int): Int {
        return getPrefs(context).getInt("RES_D$displayId", 0)
    }

    fun saveDisplayDpi(context: Context, displayId: Int, dpi: Int) {
        getPrefs(context).edit().putInt("DPI_D$displayId", dpi).apply()
    }

    fun getDisplayDpi(context: Context, displayId: Int): Int {
        return getPrefs(context).getInt("DPI_D$displayId", -1)
    }

    // --- PROFILES ---
    fun getProfileNames(context: Context): MutableSet<String> {
        return getPrefs(context).getStringSet(KEY_PROFILES, mutableSetOf()) ?: mutableSetOf()
    }

    fun saveProfile(context: Context, name: String, layout: Int, resIndex: Int, dpi: Int, apps: List<String>) {
        val names = getProfileNames(context)
        val newNames = HashSet(names)
        newNames.add(name)
        getPrefs(context).edit().putStringSet(KEY_PROFILES, newNames).apply()
        val appString = apps.joinToString(",")
        val data = "$layout|$resIndex|$dpi|$appString"
        getPrefs(context).edit().putString("PROFILE_$name", data).apply()
    }

    fun getProfileData(context: Context, name: String): String? {
        return getPrefs(context).getString("PROFILE_$name", null)
    }

    fun deleteProfile(context: Context, name: String) {
        val names = getProfileNames(context)
        val newNames = HashSet(names)
        newNames.remove(name)
        getPrefs(context).edit().putStringSet(KEY_PROFILES, newNames).remove("PROFILE_$name").apply()
    }

    fun renameProfile(context: Context, oldName: String, newName: String): Boolean {
        if (oldName == newName) return false
        if (newName.isEmpty()) return false
        val names = getProfileNames(context)
        if (!names.contains(oldName)) return false
        val data = getProfileData(context, oldName) ?: return false
        val newNames = HashSet(names)
        newNames.remove(oldName)
        newNames.add(newName)
        getPrefs(context).edit().putStringSet(KEY_PROFILES, newNames).apply()
        getPrefs(context).edit().putString("PROFILE_$newName", data).remove("PROFILE_$oldName").apply()
        return true
    }

    // --- CUSTOM LAYOUTS ---
    fun getCustomLayoutNames(context: Context): MutableSet<String> {
        return getPrefs(context).getStringSet(KEY_CUSTOM_LAYOUTS, mutableSetOf()) ?: mutableSetOf()
    }

    fun saveCustomLayout(context: Context, name: String, rectsData: String) {
        val names = getCustomLayoutNames(context)
        val newNames = HashSet(names)
        newNames.add(name)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_LAYOUTS, newNames).apply()
        getPrefs(context).edit().putString("LAYOUT_$name", rectsData).apply()
    }

    fun getCustomLayoutData(context: Context, name: String): String? {
        return getPrefs(context).getString("LAYOUT_$name", null)
    }
    
    fun deleteCustomLayout(context: Context, name: String) {
        val names = getCustomLayoutNames(context)
        val newNames = HashSet(names)
        newNames.remove(name)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_LAYOUTS, newNames).remove("LAYOUT_$name").apply()
    }
    
    fun renameCustomLayout(context: Context, oldName: String, newName: String): Boolean {
        if (oldName == newName) return false
        if (newName.isEmpty()) return false
        val names = getCustomLayoutNames(context)
        if (!names.contains(oldName)) return false
        val data = getCustomLayoutData(context, oldName) ?: return false
        val newNames = HashSet(names)
        newNames.remove(oldName)
        newNames.add(newName)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_LAYOUTS, newNames).apply()
        getPrefs(context).edit().putString("LAYOUT_$newName", data).remove("LAYOUT_$oldName").apply()
        return true
    }
    
    // --- CUSTOM RESOLUTIONS ---
    fun getCustomResolutionNames(context: Context): MutableSet<String> {
        return getPrefs(context).getStringSet(KEY_CUSTOM_RESOLUTION_NAMES, mutableSetOf()) ?: mutableSetOf()
    }

    fun saveCustomResolution(context: Context, name: String, value: String) {
        val names = getCustomResolutionNames(context)
        val newNames = HashSet(names)
        newNames.add(name)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_RESOLUTION_NAMES, newNames).apply()
        getPrefs(context).edit().putString("RES_$name", value).apply()
    }
    
    fun getCustomResolutionValue(context: Context, name: String): String? {
        return getPrefs(context).getString("RES_$name", null)
    }

    fun deleteCustomResolution(context: Context, name: String) {
        val names = getCustomResolutionNames(context)
        val newNames = HashSet(names)
        newNames.remove(name)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_RESOLUTION_NAMES, newNames).remove("RES_$name").apply()
    }
    
    fun renameCustomResolution(context: Context, oldName: String, newName: String): Boolean {
        if (oldName == newName) return false
        if (newName.isEmpty()) return false
        val names = getCustomResolutionNames(context)
        if (!names.contains(oldName)) return false
        val data = getCustomResolutionValue(context, oldName) ?: return false
        val newNames = HashSet(names)
        newNames.remove(oldName)
        newNames.add(newName)
        getPrefs(context).edit().putStringSet(KEY_CUSTOM_RESOLUTION_NAMES, newNames).apply()
        getPrefs(context).edit().putString("RES_$newName", data).remove("RES_$oldName").apply()
        return true
    }

    // --- FONT SIZE & ICONS & DRAWER ---
    fun saveFontSize(context: Context, size: Float) {
        getPrefs(context).edit().putFloat(KEY_FONT_SIZE, size).apply()
    }

    fun getFontSize(context: Context): Float {
        return getPrefs(context).getFloat(KEY_FONT_SIZE, 16f)
    }

    fun saveIconUri(context: Context, uri: String) {
        getPrefs(context).edit().putString(KEY_ICON_URI, uri).apply()
    }

    fun getIconUri(context: Context): String? {
        return getPrefs(context).getString(KEY_ICON_URI, null)
    }
    
    fun setDrawerHeightPercent(context: Context, percent: Int) {
        getPrefs(context).edit().putInt(KEY_DRAWER_HEIGHT, percent).apply()
    }
    
    fun getDrawerHeightPercent(context: Context): Int {
        return getPrefs(context).getInt(KEY_DRAWER_HEIGHT, 70)
    }
    
    fun setDrawerWidthPercent(context: Context, percent: Int) {
        getPrefs(context).edit().putInt(KEY_DRAWER_WIDTH, percent).apply()
    }
    
    fun getDrawerWidthPercent(context: Context): Int {
        return getPrefs(context).getInt(KEY_DRAWER_WIDTH, 90)
    }
    
    fun setAutoResizeKeyboard(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_AUTO_RESIZE_KEYBOARD, enable).apply()
    }
    
    fun getAutoResizeKeyboard(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_AUTO_RESIZE_KEYBOARD, true)
    }

    // --- SETTINGS ---
    fun setKillOnExecute(context: Context, kill: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_KILL_ON_EXECUTE, kill).apply()
    }

    fun getKillOnExecute(context: Context): Boolean {
        // Default is FALSE for Kill On Execute
        return getPrefs(context).getBoolean(KEY_KILL_ON_EXECUTE, false)
    }

    fun setAutoRestartTrackpad(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_AUTO_RESTART_TRACKPAD, enable).apply()
    }

    fun getAutoRestartTrackpad(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_AUTO_RESTART_TRACKPAD, false) // Default Off
    }

    fun setAutoRestartOnClose(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_AUTO_RESTART_ON_CLOSE, enable).apply()
    }

    fun getAutoRestartOnClose(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_AUTO_RESTART_ON_CLOSE, true) // Default true
    }

    fun setLauncherActive(context: Context, active: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_LAUNCHER_ACTIVE, active).apply()
    }

    fun getLauncherActive(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_LAUNCHER_ACTIVE, true) // Default true
    }

    fun setTargetDisplayIndex(context: Context, index: Int) {
        getPrefs(context).edit().putInt(KEY_TARGET_DISPLAY_INDEX, index).apply()
    }

    fun getTargetDisplayIndex(context: Context): Int {
        return getPrefs(context).getInt(KEY_TARGET_DISPLAY_INDEX, 1)
    }

    fun setInstantMode(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_IS_INSTANT_MODE, enable).apply()
    }

    fun getInstantMode(context: Context): Boolean {
        // Default is TRUE for Instant Mode
        return getPrefs(context).getBoolean(KEY_IS_INSTANT_MODE, true)
    }
    
    fun saveLastQueue(context: Context, apps: List<String>) {
        val str = apps.joinToString(",")
        getPrefs(context).edit().putString(KEY_LAST_QUEUE, str).apply()
    }
    
    fun getLastQueue(context: Context): List<String> {
        val str = getPrefs(context).getString(KEY_LAST_QUEUE, "") ?: ""
        if (str.isEmpty()) return emptyList()
        return str.split(",").filter { it.isNotEmpty() }
    }
    
    fun setShowShizukuWarning(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SHOW_SHIZUKU_WARNING, enable).apply()
    }

    fun getShowShizukuWarning(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_SHOW_SHIZUKU_WARNING, true)
    }
    
    fun setUseAltScreenOff(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_USE_ALT_SCREEN_OFF, enable).apply()
    }

    fun getUseAltScreenOff(context: Context): Boolean {
        // Default false (use standard SurfaceControl method)
        return getPrefs(context).getBoolean(KEY_USE_ALT_SCREEN_OFF, false)
    }
    
    // --- REORDER PREFERENCES ---
    fun setReorderTimeout(context: Context, seconds: Int) {
        getPrefs(context).edit().putInt(KEY_REORDER_TIMEOUT, seconds).apply()
    }
    
    fun getReorderTimeout(context: Context): Int {
        return getPrefs(context).getInt(KEY_REORDER_TIMEOUT, 2) // Default 2 seconds
    }
    
    fun setReorderDrag(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_REORDER_METHOD_DRAG, enable).apply()
    }
    
    fun getReorderDrag(context: Context): Boolean {
        // CHANGED: Default to FALSE so Tap works out of box
        return getPrefs(context).getBoolean(KEY_REORDER_METHOD_DRAG, false)
    }
    
    fun setReorderTap(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_REORDER_METHOD_TAP, enable).apply()
    }
    
    fun getReorderTap(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_REORDER_METHOD_TAP, true) // Default Enabled
    }
    
    fun setReorderScroll(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_REORDER_METHOD_SCROLL, enable).apply()
    }
    
    fun getReorderScroll(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_REORDER_METHOD_SCROLL, true) // Default Enabled
    }

    // === BLACKLIST METHODS - START ===
    fun getBlacklist(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_BLACKLIST, emptySet()) ?: emptySet()
    }

    fun isBlacklisted(context: Context, identifier: String): Boolean {
        return getBlacklist(context).contains(identifier)
    }

    fun addToBlacklist(context: Context, identifier: String) {
        val current = getBlacklist(context).toMutableSet()
        current.add(identifier)
        getPrefs(context).edit().putStringSet(KEY_BLACKLIST, current).apply()
    }

    fun removeFromBlacklist(context: Context, identifier: String) {
        val current = getBlacklist(context).toMutableSet()
        current.remove(identifier)
        getPrefs(context).edit().putStringSet(KEY_BLACKLIST, current).apply()
    }

    fun toggleBlacklist(context: Context, identifier: String): Boolean {
        return if (isBlacklisted(context, identifier)) {
            removeFromBlacklist(context, identifier)
            false
        } else {
            addToBlacklist(context, identifier)
            true
        }
    }
    // === BLACKLIST METHODS - END ===

    // === KEYBIND METHODS ===
    // Stores "MODIFIER|KEYCODE" string for a command ID

    fun saveKeybind(context: Context, cmdId: String, modifier: Int, keyCode: Int) {
        val value = "$modifier|$keyCode"
        getPrefs(context).edit().putString("BIND_$cmdId", value).apply()
    }

    fun getKeybind(context: Context, cmdId: String): Pair<Int, Int> {
        val data = getPrefs(context).getString("BIND_$cmdId", null) ?: return Pair(0, 0)
        return try {
            val parts = data.split("|")
            Pair(parts[0].toInt(), parts[1].toInt())
        } catch (e: Exception) {
            Pair(0, 0)
        }
    }

    fun clearKeybind(context: Context, cmdId: String) {
        getPrefs(context).edit().remove("BIND_$cmdId").apply()
    }

// Custom Modifier Key (Stored as KeyCode Int)
    fun saveCustomModKey(context: Context, keyCode: Int) {
        getPrefs(context).edit().putInt("CUSTOM_MOD_KEY", keyCode).apply()
    }

    fun getCustomModKey(context: Context): Int {
        return getPrefs(context).getInt("CUSTOM_MOD_KEY", 0)
    }

// Soft Keyboard Support (TextWatcher)
    fun setSoftKeyboardSupport(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean("SOFT_KB_SUPPORT", enabled).apply()
    }
    
    fun getSoftKeyboardSupport(context: Context): Boolean {
        return getPrefs(context).getBoolean("SOFT_KB_SUPPORT", false) // Default OFF for privacy
    }

    // Returns a list of all defined keybinds in format "modifier|keyCode"
    fun getAllKeybinds(context: Context): ArrayList<String> {
        val list = ArrayList<String>()
        val allPrefs = getPrefs(context).all
        for ((key, value) in allPrefs) {
            if (key.startsWith("BIND_") && value is String) {
                // value format is "modifier|keyCode"
                list.add(value)
            }
        }
        return list
    }

    // --- BOTTOM MARGIN (Per Display) ---
    // Keys format: MARGIN_BOTTOM_D0, MARGIN_BOTTOM_D1, etc.
    
    fun setBottomMarginPercent(context: Context, displayId: Int, percent: Int) {
        getPrefs(context).edit().putInt("MARGIN_BOTTOM_D$displayId", percent).apply()
    }

    fun getBottomMarginPercent(context: Context, displayId: Int): Int {
        return getPrefs(context).getInt("MARGIN_BOTTOM_D$displayId", 0)
    }

    // --- TOP MARGIN (Per Display) ---
    
    fun setTopMarginPercent(context: Context, displayId: Int, percent: Int) {
        getPrefs(context).edit().putInt("MARGIN_TOP_D$displayId", percent).apply()
    }

    fun getTopMarginPercent(context: Context, displayId: Int): Int {
        return getPrefs(context).getInt("MARGIN_TOP_D$displayId", 0)
    }
}
