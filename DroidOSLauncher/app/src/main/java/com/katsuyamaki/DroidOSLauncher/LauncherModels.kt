package com.katsuyamaki.DroidOSLauncher

import android.graphics.Rect

/**
 * Data models used by FloatingLauncherService and its adapters.
 * Extracted to reduce file size and improve organization.
 */

// === COMMAND SYSTEM ===
data class CommandDef(
    val id: String, 
    val label: String, 
    val argCount: Int, 
    val description: String
)

data class KeybindOption(
    val def: CommandDef, 
    var modifier: Int, 
    var keyCode: Int
)

data class CustomModConfigOption(val currentKeyCode: Int)

// === LAYOUT OPTIONS ===
data class LayoutOption(
    val name: String, 
    val type: Int, 
    val isCustomSaved: Boolean = false, 
    val customRects: List<Rect>? = null
)

data class ResolutionOption(
    val name: String, 
    val command: String, 
    val index: Int
)

data class DpiOption(val currentDpi: Int)

// === PROFILE OPTIONS ===
data class ProfileOption(
    val name: String, 
    val isCurrent: Boolean, 
    val layout: Int, 
    val resIndex: Int, 
    val dpi: Int, 
    val apps: List<String>, 
    val profileType: Int = 0, 
    val topMargin: Int = 0, 
    val bottomMargin: Int = 0, 
    val autoAdjustMargin: Boolean = false, 
    val orientMode: Int = 0
)

// === UI SETTINGS OPTIONS ===
data class FontSizeOption(val currentSize: Float)
data class HeightOption(val currentPercent: Int)
data class WidthOption(val currentPercent: Int)
data class MarginOption(val type: Int, val currentPercent: Int) // type: 0=Top, 1=Bottom
data class IconOption(val name: String)
data class BubbleSizeOption(val currentPercent: Int)
data class TimeoutOption(val seconds: Int)

// === ACTION OPTIONS ===
data class ActionOption(val name: String, val action: () -> Unit)
data class ToggleOption(val name: String, var isEnabled: Boolean, val onToggle: (Boolean) -> Unit)

// === REFRESH RATE OPTIONS ===
data class RefreshHeaderOption(val text: String)
data class LegendOption(val text: String)
data class RefreshItemOption(
    val label: String,
    val targetRate: Float,
    val isSelected: Boolean,
    val isAvailable: Boolean = true
)

// === SINGLETON MARKER ===
object CustomResInputOption