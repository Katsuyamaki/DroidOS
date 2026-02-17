package com.katsuyamaki.DroidOSLauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 * Interface exposing Service actions needed by extracted adapters.
 * Implemented by FloatingLauncherService.
 */
interface LauncherActionHandler {
    // Context access
    val handlerContext: Context
    val handlerPackageManager: PackageManager
    
    // State access - Queue
    val selectedAppsQueue: MutableList<MainActivity.AppInfo>
    val displayList: MutableList<Any>
    var activePackageName: String?
    var pendingArg1: Int
    var currentFontSize: Float
    
    // State access - Layout/Profile
    var selectedLayoutType: Int
    var activeCustomLayoutName: String?
    var selectedResolutionIndex: Int
    var activeProfileName: String?
    
    // State access - Navigation
    var selectedListIndex: Int
    var currentFocusArea: Int
    var queueSelectedIndex: Int
    var queueCommandPending: CommandDef?
    var queueCommandSourceIndex: Int
    var showSlotNumbersInQueue: Boolean
    
    // State access - Reorder
    var reorderSelectionIndex: Int
    var isReorderTapEnabled: Boolean
    
    // State access - Edit modes
    var isLayoutNameEditMode: Boolean
    var isProfileNameEditMode: Boolean
    var autoResizeEnabled: Boolean
    
    // App actions
    fun addToSelection(app: MainActivity.AppInfo)
    fun toggleFavorite(app: MainActivity.AppInfo)
    fun refreshSearchList()
    
    // Layout/profile actions
    fun selectLayout(layout: LayoutOption)
    fun loadProfile(name: String)
    fun saveProfile()
    fun applyResolution(option: ResolutionOption)
    fun applyRefreshRate(rate: Float)
    
    // UI actions
    fun switchMode(mode: Int)
    fun safeToast(message: String)
    fun dismissKeyboardAndRestore()
    fun updateDrawerHeight(isKeyboardMode: Boolean)
    fun pickIcon()
    
    // Queue command handling
    fun handleCommandInput(slotNumber: Int)
    fun handleWindowManagerCommand(intent: Intent)
    fun startReorderMode(index: Int)
    fun swapReorderItem(targetIndex: Int)
    fun endReorderMode(triggerInstantMode: Boolean)
    
    // Keybind actions
    fun showKeyPicker(commandId: String, modifier: Int)
    fun buildAdbCommand(commandId: String): String?
    fun broadcastKeybindsToKeyboard()
    
    // Utility
    fun getLayoutName(type: Int): String
    fun getRatioName(index: Int): String
    
    // DPI/Font/Drawer sizing
    fun selectDpi(dpi: Int)
    fun changeFontSize(newSize: Float)
    fun changeBubbleSize(delta: Int)
    fun changeDrawerHeight(delta: Int)
    fun changeDrawerWidth(delta: Int)
    var currentDrawerHeightPercent: Int
    var currentDrawerWidthPercent: Int
    var topMarginPercent: Int
    var bottomMarginPercent: Int
    var currentDisplayId: Int
    val unfilteredDisplayList: MutableList<Any>
    var customModKey: Int
    
    // Layout application
    fun applyLayoutImmediate(focusPackage: String?)
    fun setupVisualQueue()
    fun orientSuffix(): String
    fun sendCustomModToTrackpad()
    
    // Keybind constants
    val MOD_CUSTOM: Int
    val SUPPORTED_KEYS: Map<String, Int>
    
    // Key code helpers
    fun getKeyCodeFromChar(c: Char): Int
    fun getCharFromKeyCode(code: Int): String
}