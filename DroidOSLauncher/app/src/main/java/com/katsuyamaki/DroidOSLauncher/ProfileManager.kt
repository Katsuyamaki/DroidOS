package com.katsuyamaki.DroidOSLauncher

import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manages profile save/load/delete operations.
 * Extracts profile logic from FloatingLauncherService.
 */
class ProfileManager(
    private val context: Context
) {
    companion object {
        private const val TAG = "ProfileManager"
    }

    interface Callback {
        // State getters
        fun getSelectedAppsQueue(): MutableList<MainActivity.AppInfo>
        fun getAllAppsList(): List<MainActivity.AppInfo>
        fun getDrawerView(): android.view.View?
        fun getSelectedLayoutType(): Int
        fun setSelectedLayoutType(type: Int)
        fun getSelectedResolutionIndex(): Int
        fun setSelectedResolutionIndex(index: Int)
        fun getCurrentDpiSetting(): Int
        fun setCurrentDpiSetting(dpi: Int)
        fun getTopMarginPercent(): Int
        fun setTopMarginPercent(value: Int)
        fun getBottomMarginPercent(): Int
        fun setBottomMarginPercent(value: Int)
        fun getAutoAdjustMarginForIME(): Boolean
        fun setAutoAdjustMarginForIME(value: Boolean)
        fun getCurrentOrientationMode(): Int
        fun setCurrentOrientationMode(mode: Int)
        fun getCurrentDisplayId(): Int
        fun getActiveCustomLayoutName(): String?
        fun setActiveCustomLayoutName(name: String?)
        fun getActiveProfileName(): String?
        fun setActiveProfileName(name: String?)
        fun getCurrentProfileSaveMode(): Int
        fun getPackageBlank(): String
        fun isInstantMode(): Boolean
        fun getShellService(): IShellService?
        fun getLayoutCustomDynamic(): Int
        
        // Actions
        fun onToast(msg: String)
        fun onOrientSuffix(): String
        fun onApplyOrientation()
        fun onApplyLayoutImmediate()
        fun onUpdateSelectedAppsDock()
        fun onSwitchMode(mode: Int)
        fun onSwitchToProfilesMode()
        fun onGetLayoutRects(): List<android.graphics.Rect>
    }

    private var callback: Callback? = null

    fun setCallback(cb: Callback) {
        callback = cb
    }

    fun saveProfile() {
        val cb = callback ?: return
        val drawerView = cb.getDrawerView() ?: return
        
        var name = drawerView.findViewById<android.widget.EditText>(R.id.rofi_search_bar)?.text?.toString()?.trim()
        if (name.isNullOrEmpty()) { 
            val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val modePrefix = when (cb.getCurrentProfileSaveMode()) {
                1 -> "Layout_"
                2 -> "Queue_"
                else -> "Profile_"
            }
            name = "$modePrefix$timestamp" 
        }
        
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        
        when (cb.getCurrentProfileSaveMode()) {
            0 -> {
                // Layout + Apps: Save everything including margins + orientation
                val pkgs = selectedAppsQueue.map { it.packageName }
                AppPreferences.saveProfile(context, name, cb.getSelectedLayoutType(), cb.getSelectedResolutionIndex(), cb.getCurrentDpiSetting(), pkgs, 0, cb.getTopMarginPercent(), cb.getBottomMarginPercent(), cb.getAutoAdjustMarginForIME(), cb.getCurrentOrientationMode())
            }
            1 -> {
                // Layout Only: Save settings including margins + orientation, no apps
                AppPreferences.saveProfile(context, name, cb.getSelectedLayoutType(), cb.getSelectedResolutionIndex(), cb.getCurrentDpiSetting(), emptyList(), 1, cb.getTopMarginPercent(), cb.getBottomMarginPercent(), cb.getAutoAdjustMarginForIME(), cb.getCurrentOrientationMode())
            }
            2 -> {
                // App Queue Only: Save apps, use -1 for layout settings to indicate "don't change"
                val pkgs = selectedAppsQueue.map { it.packageName }
                AppPreferences.saveProfile(context, name, -1, -1, -1, pkgs, 2, -1, -1, false, 0)
            }
        }
        
        val modeLabel = when (cb.getCurrentProfileSaveMode()) {
            1 -> "Layout"
            2 -> "Queue"
            else -> "Profile"
        }
        cb.onToast("Saved $modeLabel: $name")
        drawerView.findViewById<android.widget.EditText>(R.id.rofi_search_bar)?.setText("")
        cb.onSwitchToProfilesMode()
    }

    fun loadProfile(name: String) {
        val cb = callback ?: return
        val data = AppPreferences.getProfileData(context, name) ?: return
        try { 
            val parts = data.split("|")
            val profileType = parts[0].toInt()
            val layoutType = parts[1].toInt()
            val resIndex = parts[2].toInt()
            val dpiSetting = parts[3].toInt()
            
            // Parse margin settings if available (new format has 8+ parts)
            val topMar = if (parts.size >= 8) parts[4].toInt() else 0
            val botMar = if (parts.size >= 8) parts[5].toInt() else 0
            val autoMar = if (parts.size >= 8) parts[6] == "1" else false
            val pkgList = if (parts.size >= 8) {
                parts[7].split(",").filter { it.isNotEmpty() }
            } else if (parts.size > 4) {
                parts[4].split(",").filter { it.isNotEmpty() }
            } else emptyList()
            // Parse orientation mode (index 8 in newest format: 9+ parts)
            val orientMode = if (parts.size >= 9) parts[8].toIntOrNull() ?: 0 else 0
            
            when (profileType) {
                0 -> loadProfileLayoutAndApps(name, layoutType, resIndex, dpiSetting, pkgList, topMar, botMar, autoMar, orientMode)
                1 -> loadProfileLayoutOnly(name, layoutType, resIndex, dpiSetting, topMar, botMar, autoMar, orientMode)
                2 -> loadProfileQueueOnly(name, pkgList)
                else -> loadProfileLayoutAndApps(name, layoutType, resIndex, dpiSetting, pkgList, topMar, botMar, autoMar, orientMode)
            }
        } catch (e: Exception) { 
            // Fallback for old profile format (no type prefix)
            try {
                val parts = data.split("|")
                val layoutType = parts[0].toInt()
                val resIndex = parts[1].toInt()
                val dpiSetting = parts[2].toInt()
                val pkgList = parts[3].split(",").filter { it.isNotEmpty() }
                loadProfileLayoutAndApps(name, layoutType, resIndex, dpiSetting, pkgList, 0, 0, false)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to load profile", e2) 
            }
        } 
    }

    fun deleteProfile(name: String) {
        AppPreferences.deleteProfile(context, name)
        callback?.onToast("Deleted: $name")
    }

    // Profile Type 0: Layout + Apps - Opens apps from profile, minimizes others
    private fun loadProfileLayoutAndApps(name: String, layoutType: Int, resIndex: Int, dpiSetting: Int, pkgList: List<String>, topMar: Int = 0, botMar: Int = 0, autoMar: Boolean = false, orientMode: Int = 0) {
        val cb = callback ?: return
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        val allAppsList = cb.getAllAppsList()
        val PACKAGE_BLANK = cb.getPackageBlank()
        val currentDisplayId = cb.getCurrentDisplayId()
        val shellService = cb.getShellService()
        
        // Apply layout settings
        cb.setSelectedLayoutType(layoutType)
        cb.setSelectedResolutionIndex(resIndex)
        cb.setCurrentDpiSetting(dpiSetting)
        
        // Apply orientation from profile
        cb.setCurrentOrientationMode(orientMode)
        AppPreferences.saveOrientationMode(context, currentDisplayId, orientMode)
        cb.onApplyOrientation()
        
        // Apply margin settings
        cb.setTopMarginPercent(topMar)
        cb.setBottomMarginPercent(botMar)
        cb.setAutoAdjustMarginForIME(autoMar)
        val os = cb.onOrientSuffix()
        AppPreferences.setTopMarginPercent(context, currentDisplayId, topMar)
        AppPreferences.setTopMarginPercent(context, currentDisplayId, topMar, os)
        AppPreferences.setBottomMarginPercent(context, currentDisplayId, botMar)
        AppPreferences.setBottomMarginPercent(context, currentDisplayId, botMar, os)
        AppPreferences.setAutoAdjustMarginForIME(context, autoMar)
        AppPreferences.setAutoAdjustMarginForIME(context, autoMar, os)
        
        AppPreferences.saveLastLayout(context, layoutType, currentDisplayId)
        AppPreferences.saveLastLayout(context, layoutType, currentDisplayId, os)
        if (layoutType != cb.getLayoutCustomDynamic()) {
            cb.setActiveCustomLayoutName(null)
            AppPreferences.saveLastCustomLayoutName(context, null, currentDisplayId)
            AppPreferences.saveLastCustomLayoutName(context, null, currentDisplayId, os)
        }
        AppPreferences.saveDisplayResolution(context, currentDisplayId, resIndex)
        AppPreferences.saveDisplayDpi(context, currentDisplayId, dpiSetting)
        
        // Get current queue packages for comparison
        val profilePkgs = pkgList.toSet()
        
        // Minimize apps not in profile (don't kill - preserve user work)
        val appsToMinimize = selectedAppsQueue.filter { 
            !it.isMinimized && it.packageName != PACKAGE_BLANK && it.packageName !in profilePkgs 
        }
        for (app in appsToMinimize) {
            app.isMinimized = true
        }
        if (appsToMinimize.isNotEmpty()) {
            Thread {
                for (app in appsToMinimize) {
                    try {
                        val tid = shellService?.getTaskId(app.getBasePackage(), null) ?: -1
                        if (tid != -1) shellService?.moveTaskToBack(tid)
                    } catch (e: Exception) {}
                }
            }.start()
        }
        
        // Build new queue from profile
        val newQueue = mutableListOf<MainActivity.AppInfo>()
        for (pkg in pkgList) {
            if (pkg == PACKAGE_BLANK) {
                newQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK, null))
            } else {
                // Check if app is already in queue
                val existingApp = selectedAppsQueue.find { it.packageName == pkg }
                if (existingApp != null) {
                    existingApp.isMinimized = false
                    newQueue.add(existingApp)
                } else {
                    // App not in queue - find in app list and add
                    val app = allAppsList.find { it.packageName == pkg }
                    if (app != null) {
                        app.isMinimized = false
                        newQueue.add(app)
                    }
                }
            }
        }
        
        selectedAppsQueue.clear()
        selectedAppsQueue.addAll(newQueue)
        
        cb.setActiveProfileName(name)
        cb.onUpdateSelectedAppsDock()
        // Force refresh both recyclers to prevent visual duplicates
        val drawerView = cb.getDrawerView()
        drawerView?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.selected_apps_recycler)?.adapter?.notifyDataSetChanged()
        drawerView?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        cb.onToast("Loaded: $name")
        
        if (cb.isInstantMode()) cb.onApplyLayoutImmediate()
    }
    
    // Profile Type 1: Layout Only - Applies layout settings, keeps current apps
    private fun loadProfileLayoutOnly(name: String, layoutType: Int, resIndex: Int, dpiSetting: Int, topMar: Int = 0, botMar: Int = 0, autoMar: Boolean = false, orientMode: Int = 0) {
        val cb = callback ?: return
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        val PACKAGE_BLANK = cb.getPackageBlank()
        val currentDisplayId = cb.getCurrentDisplayId()
        val shellService = cb.getShellService()
        
        // Apply layout settings
        cb.setSelectedLayoutType(layoutType)
        cb.setSelectedResolutionIndex(resIndex)
        cb.setCurrentDpiSetting(dpiSetting)
        
        // Apply orientation from profile
        cb.setCurrentOrientationMode(orientMode)
        AppPreferences.saveOrientationMode(context, currentDisplayId, orientMode)
        cb.onApplyOrientation()
        
        // Apply margin settings
        cb.setTopMarginPercent(topMar)
        cb.setBottomMarginPercent(botMar)
        cb.setAutoAdjustMarginForIME(autoMar)
        val os = cb.onOrientSuffix()
        AppPreferences.setTopMarginPercent(context, currentDisplayId, topMar)
        AppPreferences.setTopMarginPercent(context, currentDisplayId, topMar, os)
        AppPreferences.setBottomMarginPercent(context, currentDisplayId, botMar)
        AppPreferences.setBottomMarginPercent(context, currentDisplayId, botMar, os)
        AppPreferences.setAutoAdjustMarginForIME(context, autoMar)
        AppPreferences.setAutoAdjustMarginForIME(context, autoMar, os)
        
        AppPreferences.saveLastLayout(context, layoutType, currentDisplayId)
        AppPreferences.saveLastLayout(context, layoutType, currentDisplayId, os)
        if (layoutType != cb.getLayoutCustomDynamic()) {
            cb.setActiveCustomLayoutName(null)
            AppPreferences.saveLastCustomLayoutName(context, null, currentDisplayId)
            AppPreferences.saveLastCustomLayoutName(context, null, currentDisplayId, os)
        }
        AppPreferences.saveDisplayResolution(context, currentDisplayId, resIndex)
        AppPreferences.saveDisplayDpi(context, currentDisplayId, dpiSetting)
        
        // Get layout slot count
        val layoutRects = cb.onGetLayoutRects()
        val maxSlots = layoutRects.size
        
        // If more active apps than slots, minimize excess
        val activeApps = selectedAppsQueue.filter { !it.isMinimized && it.packageName != PACKAGE_BLANK }
        if (activeApps.size > maxSlots) {
            val appsToMinimize = activeApps.drop(maxSlots)
            for (app in appsToMinimize) {
                app.isMinimized = true
            }
            Thread {
                for (app in appsToMinimize) {
                    try {
                        val tid = shellService?.getTaskId(app.getBasePackage(), null) ?: -1
                        if (tid != -1) shellService?.moveTaskToBack(tid)
                    } catch (e: Exception) {}
                }
            }.start()
        }
        
        cb.setActiveProfileName(name)
        cb.onUpdateSelectedAppsDock()
        // Force refresh both recyclers
        val drawerView = cb.getDrawerView()
        drawerView?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.selected_apps_recycler)?.adapter?.notifyDataSetChanged()
        drawerView?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        cb.onToast("Loaded Layout: $name")
        
        if (cb.isInstantMode()) cb.onApplyLayoutImmediate()
    }
    
    // Profile Type 2: App Queue Only - Applies app queue, keeps current layout settings
    private fun loadProfileQueueOnly(name: String, pkgList: List<String>) {
        val cb = callback ?: return
        val selectedAppsQueue = cb.getSelectedAppsQueue()
        val allAppsList = cb.getAllAppsList()
        val PACKAGE_BLANK = cb.getPackageBlank()
        val shellService = cb.getShellService()
        
        val profilePkgs = pkgList.toSet()
        
        // Minimize apps not in profile (don't kill - preserve user work)
        val appsToMinimize = selectedAppsQueue.filter { 
            !it.isMinimized && it.packageName != PACKAGE_BLANK && it.packageName !in profilePkgs 
        }
        for (app in appsToMinimize) {
            app.isMinimized = true
        }
        if (appsToMinimize.isNotEmpty()) {
            Thread {
                for (app in appsToMinimize) {
                    try {
                        val tid = shellService?.getTaskId(app.getBasePackage(), null) ?: -1
                        if (tid != -1) shellService?.moveTaskToBack(tid)
                    } catch (e: Exception) {}
                }
            }.start()
        }
        
        // Build new queue from profile
        val newQueue = mutableListOf<MainActivity.AppInfo>()
        for (pkg in pkgList) {
            if (pkg == PACKAGE_BLANK) {
                newQueue.add(MainActivity.AppInfo(" (Blank Space)", PACKAGE_BLANK, null))
            } else {
                // Check if app is already in queue
                val existingApp = selectedAppsQueue.find { it.packageName == pkg }
                if (existingApp != null) {
                    existingApp.isMinimized = false
                    newQueue.add(existingApp)
                } else {
                    // App not in queue - find in app list and add
                    val app = allAppsList.find { it.packageName == pkg }
                    if (app != null) {
                        app.isMinimized = false
                        newQueue.add(app)
                    }
                }
            }
        }
        
        selectedAppsQueue.clear()
        selectedAppsQueue.addAll(newQueue)
        
        cb.setActiveProfileName(name)
        cb.onUpdateSelectedAppsDock()
        // Force refresh both recyclers to prevent visual duplicates
        val drawerView = cb.getDrawerView()
        drawerView?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.selected_apps_recycler)?.adapter?.notifyDataSetChanged()
        drawerView?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rofi_recycler_view)?.adapter?.notifyDataSetChanged()
        cb.onToast("Loaded Queue: $name")
        
        if (cb.isInstantMode()) cb.onApplyLayoutImmediate()
    }
}