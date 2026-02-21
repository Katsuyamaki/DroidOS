package com.katsuyamaki.DroidOSLauncher

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
    private const val KEY_ALLOW_EXTERNAL_BROADCAST_CMDS = "KEY_ALLOW_EXTERNAL_BROADCAST_CMDS"

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

    // === ASPECT RATIO DETECTION ===
    fun getAspectRatioCategory(width: Int, height: Int): String {
        val w = maxOf(width, height)
        val h = minOf(width, height)
        val ratio = w.toFloat() / h.toFloat()
        return when {
            ratio < 1.4f -> "4_3"
            ratio < 1.65f -> "16_10"
            ratio < 1.85f -> "16_9"
            ratio < 2.2f -> "21_9"
            ratio < 2.8f -> "24_10"
            ratio < 3.8f -> "32_9"
            else -> "WIDE"
        }
    }

    private fun getDisplayCategory(displayId: Int): String {
        return if (displayId > 1) "VIRTUAL" else "D$displayId"
    }

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
    
    // --- ORIENTATION SUFFIX HELPER ---
    // Returns "_L" for landscape, "_P" for portrait based on screen dimensions
    fun orientSuffix(screenW: Int, screenH: Int): String {
        return if (screenW > screenH) "_L" else "_P"
    }

    // --- GLOBAL LAYOUT PREFS ---
    
    private fun getLayoutKey(displayId: Int): String {
        return if (displayId > 1) "KEY_LAST_LAYOUT_VIRTUAL" else "KEY_LAST_LAYOUT_D$displayId"
    }
    
    private fun getCustomLayoutNameKey(displayId: Int): String {
        return if (displayId > 1) "KEY_LAST_CUSTOM_NAME_VIRTUAL" else "KEY_LAST_CUSTOM_NAME_D$displayId"
    }

    fun saveLastLayout(context: Context, layoutId: Int, displayId: Int) {
        getPrefs(context).edit().putInt(getLayoutKey(displayId), layoutId).apply()
    }

    fun getLastLayout(context: Context, displayId: Int): Int {
        return getPrefs(context).getInt(getLayoutKey(displayId), 5)
    }
    
    fun saveLastCustomLayoutName(context: Context, name: String?, displayId: Int) {
        getPrefs(context).edit().putString(getCustomLayoutNameKey(displayId), name).apply()
    }

    fun getLastCustomLayoutName(context: Context, displayId: Int): String? {
        return getPrefs(context).getString(getCustomLayoutNameKey(displayId), null)
    }

    // --- ORIENTATION-AWARE OVERLOADS ---
    // These persist per-display AND per-orientation (landscape/portrait)

    fun saveLastLayout(context: Context, layoutId: Int, displayId: Int, oSuffix: String) {
        getPrefs(context).edit().putInt(getLayoutKey(displayId) + oSuffix, layoutId).apply()
    }

    fun getLastLayout(context: Context, displayId: Int, oSuffix: String): Int? {
        val key = getLayoutKey(displayId) + oSuffix
        val prefs = getPrefs(context)
        return if (prefs.contains(key)) prefs.getInt(key, 5) else null
    }

    fun saveLastCustomLayoutName(context: Context, name: String?, displayId: Int, oSuffix: String) {
        getPrefs(context).edit().putString(getCustomLayoutNameKey(displayId) + oSuffix, name).apply()
    }

    fun getLastCustomLayoutName(context: Context, displayId: Int, oSuffix: String): String? {
        return getPrefs(context).getString(getCustomLayoutNameKey(displayId) + oSuffix, null)
    }

    fun setTopMarginPercent(context: Context, displayId: Int, percent: Int, oSuffix: String) {
        getPrefs(context).edit().putInt("MARGIN_TOP_${getDisplayCategory(displayId)}$oSuffix", percent).apply()
    }

    fun getTopMarginPercent(context: Context, displayId: Int, oSuffix: String): Int? {
        val prefs = getPrefs(context)
        val categoryKey = "MARGIN_TOP_${getDisplayCategory(displayId)}$oSuffix"
        if (prefs.contains(categoryKey)) return prefs.getInt(categoryKey, 5)

        val legacyExactKey = "MARGIN_TOP_D$displayId$oSuffix"
        if (prefs.contains(legacyExactKey)) return prefs.getInt(legacyExactKey, 5)

        if (displayId > 1) {
            val legacyVirtual = prefs.all.keys
                .asSequence()
                .filter { it.startsWith("MARGIN_TOP_D") && it.endsWith(oSuffix) }
                .mapNotNull { key -> key.removePrefix("MARGIN_TOP_D").removeSuffix(oSuffix).toIntOrNull() }
                .firstOrNull { it > 1 }
            if (legacyVirtual != null) return prefs.getInt("MARGIN_TOP_D$legacyVirtual$oSuffix", 5)
        }

        return null
    }

    fun setBottomMarginPercent(context: Context, displayId: Int, percent: Int, oSuffix: String) {
        getPrefs(context).edit().putInt("MARGIN_BOTTOM_${getDisplayCategory(displayId)}$oSuffix", percent).apply()
    }

    fun getBottomMarginPercent(context: Context, displayId: Int, oSuffix: String): Int? {
        val prefs = getPrefs(context)
        val categoryKey = "MARGIN_BOTTOM_${getDisplayCategory(displayId)}$oSuffix"
        if (prefs.contains(categoryKey)) return prefs.getInt(categoryKey, 25)

        val legacyExactKey = "MARGIN_BOTTOM_D$displayId$oSuffix"
        if (prefs.contains(legacyExactKey)) return prefs.getInt(legacyExactKey, 25)

        if (displayId > 1) {
            val legacyVirtual = prefs.all.keys
                .asSequence()
                .filter { it.startsWith("MARGIN_BOTTOM_D") && it.endsWith(oSuffix) }
                .mapNotNull { key -> key.removePrefix("MARGIN_BOTTOM_D").removeSuffix(oSuffix).toIntOrNull() }
                .firstOrNull { it > 1 }
            if (legacyVirtual != null) return prefs.getInt("MARGIN_BOTTOM_D$legacyVirtual$oSuffix", 25)
        }

        return null
    }

    fun setAutoAdjustMarginForIME(context: Context, displayId: Int, enable: Boolean, oSuffix: String) {
        getPrefs(context).edit().putBoolean("auto_adjust_margin_ime_${getDisplayCategory(displayId)}$oSuffix", enable).apply()
    }

    fun getAutoAdjustMarginForIME(context: Context, displayId: Int, oSuffix: String): Boolean? {
        val key = "auto_adjust_margin_ime_${getDisplayCategory(displayId)}$oSuffix"
        val prefs = getPrefs(context)
        return if (prefs.contains(key)) prefs.getBoolean(key, true) else null
    }

    fun getAutoAdjustMarginForIME(context: Context, oSuffix: String): Boolean? {
        val key = "auto_adjust_margin_ime$oSuffix"
        val prefs = getPrefs(context)
        return if (prefs.contains(key)) prefs.getBoolean(key, true) else null
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

    // --- PER-DISPLAY ORIENTATION MODE ---
    // 0 = System Default, 1 = Portrait, 2 = Landscape
    fun saveOrientationMode(context: Context, displayId: Int, mode: Int) {
        getPrefs(context).edit().putInt("ORIENT_D$displayId", mode).apply()
    }

    fun getOrientationMode(context: Context, displayId: Int): Int {
        return getPrefs(context).getInt("ORIENT_D$displayId", 0)
    }

    // --- PROFILES ---
    fun getProfileNames(context: Context): MutableSet<String> {
        return getPrefs(context).getStringSet(KEY_PROFILES, mutableSetOf()) ?: mutableSetOf()
    }

    fun saveProfile(context: Context, name: String, layout: Int, resIndex: Int, dpi: Int, apps: List<String>, profileType: Int = 0, topMargin: Int = 0, bottomMargin: Int = 0, autoAdjustMargin: Boolean = false, orientMode: Int = 0) {
        val names = getProfileNames(context)
        val newNames = HashSet(names)
        newNames.add(name)
        getPrefs(context).edit().putStringSet(KEY_PROFILES, newNames).apply()
        val appString = apps.joinToString(",")
        val autoMarginStr = if (autoAdjustMargin) "1" else "0"
        // New format: type|layout|resIndex|dpi|topMargin|bottomMargin|autoAdjust|apps|orient
        val data = "$profileType|$layout|$resIndex|$dpi|$topMargin|$bottomMargin|$autoMarginStr|$appString|$orientMode"
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
    
    private const val KEY_BUBBLE_SIZE = "bubble_size_percent"
    
    fun saveBubbleSize(context: Context, percent: Int) {
        getPrefs(context).edit().putInt(KEY_BUBBLE_SIZE, percent).apply()
    }
    
    fun getBubbleSize(context: Context): Int {
        return getPrefs(context).getInt(KEY_BUBBLE_SIZE, 100)
    }
    
    fun setDrawerHeightPercent(context: Context, percent: Int) {
        getPrefs(context).edit().putInt(KEY_DRAWER_HEIGHT, percent).apply()
    }
    
    fun getDrawerHeightPercent(context: Context): Int {
        return getPrefs(context).getInt(KEY_DRAWER_HEIGHT, 55)
    }
    
    fun setDrawerWidthPercent(context: Context, percent: Int) {
        getPrefs(context).edit().putInt(KEY_DRAWER_WIDTH, percent).apply()
    }
    
    fun getDrawerWidthPercent(context: Context): Int {
        return getPrefs(context).getInt(KEY_DRAWER_WIDTH, 75)
    }

    // === PER-DISPLAY + RESOLUTION SETTINGS ===
    
    fun setDrawerHeightPercentForConfig(context: Context, displayId: Int, aspectRatio: String, percent: Int) {
        getPrefs(context).edit().putInt("DRAWER_H_${getDisplayCategory(displayId)}_$aspectRatio", percent).apply()
    }

    fun getDrawerHeightPercentForConfig(context: Context, displayId: Int, aspectRatio: String): Int? {
        val key = "DRAWER_H_${getDisplayCategory(displayId)}_$aspectRatio"
        val prefs = getPrefs(context)
        return if (prefs.contains(key)) prefs.getInt(key, 55) else null
    }

    fun setDrawerHeightPercentForConfig(context: Context, displayId: Int, aspectRatio: String, percent: Int, oSuffix: String) {
        getPrefs(context).edit().putInt("DRAWER_H_${getDisplayCategory(displayId)}_$aspectRatio$oSuffix", percent).apply()
    }

    fun getDrawerHeightPercentForConfig(context: Context, displayId: Int, aspectRatio: String, oSuffix: String): Int? {
        val key = "DRAWER_H_${getDisplayCategory(displayId)}_$aspectRatio$oSuffix"
        val prefs = getPrefs(context)
        return if (prefs.contains(key)) prefs.getInt(key, 55) else null
    }

    fun setDrawerWidthPercentForConfig(context: Context, displayId: Int, aspectRatio: String, percent: Int) {
        getPrefs(context).edit().putInt("DRAWER_W_${getDisplayCategory(displayId)}_$aspectRatio", percent).apply()
    }

    fun getDrawerWidthPercentForConfig(context: Context, displayId: Int, aspectRatio: String): Int? {
        val key = "DRAWER_W_${getDisplayCategory(displayId)}_$aspectRatio"
        val prefs = getPrefs(context)
        return if (prefs.contains(key)) prefs.getInt(key, 75) else null
    }

    fun setDrawerWidthPercentForConfig(context: Context, displayId: Int, aspectRatio: String, percent: Int, oSuffix: String) {
        getPrefs(context).edit().putInt("DRAWER_W_${getDisplayCategory(displayId)}_$aspectRatio$oSuffix", percent).apply()
    }

    fun getDrawerWidthPercentForConfig(context: Context, displayId: Int, aspectRatio: String, oSuffix: String): Int? {
        val key = "DRAWER_W_${getDisplayCategory(displayId)}_$aspectRatio$oSuffix"
        val prefs = getPrefs(context)
        return if (prefs.contains(key)) prefs.getInt(key, 75) else null
    }

    fun setBubbleSizeForConfig(context: Context, displayId: Int, aspectRatio: String, percent: Int) {
        getPrefs(context).edit().putInt("BUBBLE_SIZE_${getDisplayCategory(displayId)}_$aspectRatio", percent).apply()
    }

    fun getBubbleSizeForConfig(context: Context, displayId: Int, aspectRatio: String): Int? {
        val key = "BUBBLE_SIZE_${getDisplayCategory(displayId)}_$aspectRatio"
        val prefs = getPrefs(context)
        return if (prefs.contains(key)) prefs.getInt(key, 100) else null
    }

    fun setBubbleSizeForConfig(context: Context, displayId: Int, aspectRatio: String, percent: Int, oSuffix: String) {
        getPrefs(context).edit().putInt("BUBBLE_SIZE_${getDisplayCategory(displayId)}_$aspectRatio$oSuffix", percent).apply()
    }

    fun getBubbleSizeForConfig(context: Context, displayId: Int, aspectRatio: String, oSuffix: String): Int? {
        val key = "BUBBLE_SIZE_${getDisplayCategory(displayId)}_$aspectRatio$oSuffix"
        val prefs = getPrefs(context)
        return if (prefs.contains(key)) prefs.getInt(key, 100) else null
    }

    fun setBubblePositionForConfig(context: Context, displayId: Int, aspectRatio: String,
                                    xPx: Int, yPx: Int, screenW: Int, screenH: Int) {
        val xPermille = (xPx.toFloat() / screenW * 1000).toInt().coerceIn(0, 1000)
        val yPermille = (yPx.toFloat() / screenH * 1000).toInt().coerceIn(0, 1000)
        getPrefs(context).edit().putString("BUBBLE_POS_${getDisplayCategory(displayId)}_$aspectRatio", "$xPermille|$yPermille").apply()
    }

    fun getBubblePositionForConfig(context: Context, displayId: Int, aspectRatio: String,
                                    screenW: Int, screenH: Int): Pair<Int, Int>? {
        val key = "BUBBLE_POS_${getDisplayCategory(displayId)}_$aspectRatio"
        val data = getPrefs(context).getString(key, null) ?: return null
        return try {
            val parts = data.split("|")
            if (parts.size != 2) return null
            Pair((parts[0].toInt() / 1000f * screenW).toInt(), (parts[1].toInt() / 1000f * screenH).toInt())
        } catch (e: Exception) { null }
    }

    fun setBubblePositionForConfig(context: Context, displayId: Int, aspectRatio: String,
                                    xPx: Int, yPx: Int, screenW: Int, screenH: Int, oSuffix: String) {
        val xPermille = (xPx.toFloat() / screenW * 1000).toInt().coerceIn(0, 1000)
        val yPermille = (yPx.toFloat() / screenH * 1000).toInt().coerceIn(0, 1000)
        getPrefs(context).edit().putString("BUBBLE_POS_${getDisplayCategory(displayId)}_$aspectRatio$oSuffix", "$xPermille|$yPermille").apply()
    }

    fun getBubblePositionForConfig(context: Context, displayId: Int, aspectRatio: String,
                                    screenW: Int, screenH: Int, oSuffix: String): Pair<Int, Int>? {
        val key = "BUBBLE_POS_${getDisplayCategory(displayId)}_$aspectRatio$oSuffix"
        val data = getPrefs(context).getString(key, null) ?: return null
        return try {
            val parts = data.split("|")
            if (parts.size != 2) return null
            Pair((parts[0].toInt() / 1000f * screenW).toInt(), (parts[1].toInt() / 1000f * screenH).toInt())
        } catch (e: Exception) { null }
    }
    
    fun setAutoResizeKeyboard(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_AUTO_RESIZE_KEYBOARD, enable).apply()
    }
    
    fun getAutoResizeKeyboard(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_AUTO_RESIZE_KEYBOARD, false)
    }

    fun setAutoAdjustMarginForIME(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean("auto_adjust_margin_ime", enable).apply()
    }

    fun getAutoAdjustMarginForIME(context: Context): Boolean {
        return getPrefs(context).getBoolean("auto_adjust_margin_ime", true)
    }

    // Track when DroidOS IME has been detected via IME_VISIBILITY broadcast.
    // This is the most reliable indicator on Flip cover screen where system settings
    // may not reflect the actual IME in use.
    fun setDroidOsImeDetected(context: Context, detected: Boolean) {
        getPrefs(context).edit().putBoolean("droidos_ime_detected", detected).apply()
    }

    fun getDroidOsImeDetected(context: Context): Boolean {
        return getPrefs(context).getBoolean("droidos_ime_detected", false)
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
        return getPrefs(context).getBoolean(KEY_AUTO_RESTART_TRACKPAD, true) // Default ON
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
    
    // [FIX] Persist minimized states so UNMINIMIZE works after queue reload
    private const val KEY_MINIMIZED_PACKAGES = "minimized_packages"
    
    fun saveMinimizedPackages(context: Context, packages: Set<String>) {
        val str = packages.joinToString(",")
        getPrefs(context).edit().putString(KEY_MINIMIZED_PACKAGES, str).apply()
    }
    
    fun getMinimizedPackages(context: Context): Set<String> {
        val str = getPrefs(context).getString(KEY_MINIMIZED_PACKAGES, "") ?: ""
        if (str.isEmpty()) return emptySet()
        return str.split(",").filter { it.isNotEmpty() }.toSet()
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

    fun setAllowExternalBroadcastCommands(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_ALLOW_EXTERNAL_BROADCAST_CMDS, enable).apply()
    }

    fun getAllowExternalBroadcastCommands(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ALLOW_EXTERNAL_BROADCAST_CMDS, true)
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
        return getPrefs(context).getBoolean(KEY_REORDER_METHOD_DRAG, true) // Default ON
    }
    
    fun setReorderTap(context: Context, enable: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_REORDER_METHOD_TAP, enable).apply()
    }
    
    fun getReorderTap(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_REORDER_METHOD_TAP, false) // Default OFF
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
        val savedData = getPrefs(context).getString("BIND_$cmdId", null)
        
        if (savedData != null) {
            return try {
                val parts = savedData.split("|")
                Pair(parts[0].toInt(), parts[1].toInt())
            } catch (e: Exception) {
                Pair(0, 0)
            }
        }

        // DEFAULTS (Alt = 2)
        // O=43, H=36, L=40, I=37, J=38, K=39, X=52, Space=62, F=34, G=35, N=42, M=41, P=44
        return when (cmdId) {
            "SWAP" -> Pair(2, 43)              // Alt + O
            "SWAP_ACTIVE_LEFT" -> Pair(2, 36)  // Alt + H
            "SWAP_ACTIVE_RIGHT" -> Pair(2, 40) // Alt + L
            "HIDE" -> Pair(2, 37)              // Alt + I
            "MINIMIZE" -> Pair(2, 38)          // Alt + J
            "UNMINIMIZE" -> Pair(2, 39)        // Alt + K
            "KILL" -> Pair(2, 52)              // Alt + X
            "OPEN_DRAWER" -> Pair(2, 62)       // Alt + Space
            "SET_FOCUS" -> Pair(2, 34)         // Alt + F
            "FOCUS_LAST" -> Pair(2, 35)        // Alt + G
            "MINIMIZE_ALL" -> Pair(2, 42)      // Alt + N
            "RESTORE_ALL" -> Pair(2, 41)       // Alt + M
            "MOVE_TO" -> Pair(2, 44)           // Alt + P
            "OPEN_MOVE_TO" -> Pair(2, 54)      // Alt + Z
            "OPEN_SWAP" -> Pair(2, 47)         // Alt + S
            else -> Pair(0, 0)
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

    // --- BOTTOM MARGIN (Per Display Category) ---
    // Keys format: MARGIN_BOTTOM_D0, MARGIN_BOTTOM_D1, MARGIN_BOTTOM_VIRTUAL
    
    fun setBottomMarginPercent(context: Context, displayId: Int, percent: Int) {
        getPrefs(context).edit().putInt("MARGIN_BOTTOM_${getDisplayCategory(displayId)}", percent).apply()
    }

    fun getBottomMarginPercent(context: Context, displayId: Int): Int {
        val prefs = getPrefs(context)
        val categoryKey = "MARGIN_BOTTOM_${getDisplayCategory(displayId)}"
        if (prefs.contains(categoryKey)) return prefs.getInt(categoryKey, 25)

        val legacyExactKey = "MARGIN_BOTTOM_D$displayId"
        if (prefs.contains(legacyExactKey)) return prefs.getInt(legacyExactKey, 25)

        if (displayId > 1) {
            val legacyVirtual = prefs.all.keys
                .asSequence()
                .filter { it.startsWith("MARGIN_BOTTOM_D") }
                .mapNotNull { key -> key.removePrefix("MARGIN_BOTTOM_D").toIntOrNull() }
                .firstOrNull { it > 1 }
            if (legacyVirtual != null) return prefs.getInt("MARGIN_BOTTOM_D$legacyVirtual", 25)
        }

        return 25
    }

    // --- TOP MARGIN (Per Display Category) ---
    
    fun setTopMarginPercent(context: Context, displayId: Int, percent: Int) {
        getPrefs(context).edit().putInt("MARGIN_TOP_${getDisplayCategory(displayId)}", percent).apply()
    }

    fun getTopMarginPercent(context: Context, displayId: Int): Int {
        val prefs = getPrefs(context)
        val categoryKey = "MARGIN_TOP_${getDisplayCategory(displayId)}"
        if (prefs.contains(categoryKey)) return prefs.getInt(categoryKey, 5)

        val legacyExactKey = "MARGIN_TOP_D$displayId"
        if (prefs.contains(legacyExactKey)) return prefs.getInt(legacyExactKey, 5)

        if (displayId > 1) {
            val legacyVirtual = prefs.all.keys
                .asSequence()
                .filter { it.startsWith("MARGIN_TOP_D") }
                .mapNotNull { key -> key.removePrefix("MARGIN_TOP_D").toIntOrNull() }
                .firstOrNull { it > 1 }
            if (legacyVirtual != null) return prefs.getInt("MARGIN_TOP_D$legacyVirtual", 5)
        }

        return 5
    }

    // --- DEFAULT LAYOUT RENAMING ---
    fun saveDefaultLayoutName(context: Context, type: Int, name: String) {
        getPrefs(context).edit().putString("DEF_LAYOUT_NAME_$type", name).apply()
    }

    fun getDefaultLayoutName(context: Context, type: Int): String? {
        return getPrefs(context).getString("DEF_LAYOUT_NAME_$type", null)
    }

    fun clearDefaultLayoutNames(context: Context) {
        val editor = getPrefs(context).edit()
        // Clear known IDs
        val knownIds = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        for (id in knownIds) {
            editor.remove("DEF_LAYOUT_NAME_$id")
        }
        editor.apply()
    }

    // --- LAYOUT LEGEND TEXT ---
    private const val DEFAULT_LEGEND = "R×C = Rows × Cols · [1:2:1] = Col ratio · [R 3:1] = Row ratio"
    
    fun saveLegendText(context: Context, text: String) {
        getPrefs(context).edit().putString("LAYOUT_LEGEND_TEXT", text).apply()
    }

    fun getLegendText(context: Context): String {
        return getPrefs(context).getString("LAYOUT_LEGEND_TEXT", DEFAULT_LEGEND) ?: DEFAULT_LEGEND
    }
}
