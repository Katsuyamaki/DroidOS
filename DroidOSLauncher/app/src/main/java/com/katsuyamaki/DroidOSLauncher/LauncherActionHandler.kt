package com.katsuyamaki.DroidOSLauncher

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.KeyEvent

/**
 * Interface that decouples UI Adapters from FloatingLauncherService.
 * Adapters call these methods instead of accessing the Service directly.
 */
interface LauncherActionHandler {
    // === CONTEXT ACCESS ===
    fun getContext(): Context
    fun getPackageManagerRef(): android.content.pm.PackageManager
    
    // === APP SELECTION ===
    fun addToSelection(app: MainActivity.AppInfo)
    fun toggleFavorite(app: MainActivity.AppInfo)
    fun refreshSearchList()
    
    // === NAVIGATION ===
    fun switchMode(mode: Int)
    fun selectLayout(option: LayoutOption)
    fun loadProfile(name: String)
    fun saveProfile()
    fun applyResolution(option: ResolutionOption)
    fun pickIcon()
    fun applyRefreshRate(targetRate: Float)
    
    // === SETTINGS ===
    fun selectDpi(value: Int)
    fun changeFontSize(newSize: Float)
    fun changeDrawerHeight(delta: Int)
    fun changeDrawerWidth(delta: Int)
    fun changeBubbleSize(delta: Int)
    fun changeTopMargin(percent: Int)
    fun changeBottomMargin(percent: Int)
    
    // === UI STATE ===
    fun dismissKeyboardAndRestore()
    fun updateDrawerHeight(isKeyboardMode: Boolean)
    fun safeToast(msg: String)
    fun getCurrentFontSize(): Float
    fun isAutoResizeEnabled(): Boolean
    
    // === COMMAND SYSTEM ===
    fun handleCommandInput(slotNum: Int)
    fun handleWindowManagerCommand(intent: android.content.Intent)
    
    // === QUEUE STATE (read-only for adapters) ===
    fun getSelectedAppsQueue(): List<MainActivity.AppInfo>
    fun getDisplayList(): List<Any>
    fun getCurrentFocusArea(): Int
    fun getQueueSelectedIndex(): Int
    fun getQueueCommandPending(): CommandDef?
    fun getQueueCommandSourceIndex(): Int
    fun getPendingArg1(): Int
    fun getActivePackageName(): String?
    fun isShowSlotNumbersInQueue(): Boolean
    fun getReorderSelectionIndex(): Int
    fun getSelectedListIndex(): Int
    fun isLayoutNameEditMode(): Boolean
    fun isProfileNameEditMode(): Boolean
    fun getActiveProfileName(): String?
    fun getSelectedLayoutType(): Int
    fun getActiveCustomLayoutName(): String?
    fun getSelectedResolutionIndex(): Int
    fun getCurrentDisplayId(): Int
    
    // === CONSTANTS ===
    companion object {
        const val FOCUS_SEARCH = 0
        const val FOCUS_QUEUE = 1
        const val FOCUS_LIST = 2
        const val PACKAGE_BLANK = "com.katsuyamaki.DroidOSLauncher.BLANK"
    }
}