package com.katsuyamaki.DroidOSLauncher

import android.graphics.Rect

// Data classes for adapter display items
data class LayoutOption(
    val name: String,
    val type: Int,
    val isCustomSaved: Boolean = false,
    val customRects: List<Rect>? = null
)

data class ResolutionOption(val name: String, val command: String, val index: Int)
data class DpiOption(val currentDpi: Int)
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
data class FontSizeOption(val currentSize: Float)
data class HeightOption(val currentPercent: Int)
data class WidthOption(val currentPercent: Int)
data class MarginOption(val type: Int, val currentPercent: Int) // type: 0=Top, 1=Bottom
data class IconOption(val name: String)
data class BubbleSizeOption(val currentPercent: Int)
data class ActionOption(val name: String, val action: () -> Unit)
data class ToggleOption(
    val name: String,
    var isEnabled: Boolean,
    val canToggle: Boolean = true,
    val disabledNote: String? = null,
    val onToggle: (Boolean) -> Unit
)
data class TimeoutOption(val seconds: Int)
data class RefreshHeaderOption(val text: String)
data class LegendOption(val text: String)
data class RefreshItemOption(
    val label: String,
    val targetRate: Float,
    val isAvailable: Boolean,
    val isSelected: Boolean
)
object CustomResInputOption
data class CommandDef(val id: String, val label: String, val argCount: Int, val description: String)
data class KeybindOption(val def: CommandDef, var modifier: Int, var keyCode: Int)
data class CustomModConfigOption(val currentKeyCode: Int)

// Layout type constants
object LayoutTypes {
    const val LAYOUT_FULL = 0
    const val LAYOUT_SPLIT_H = 1
    const val LAYOUT_SPLIT_V = 2
    const val LAYOUT_GRID_4 = 3
    const val LAYOUT_STACK_3 = 4
    const val LAYOUT_CUSTOM_DYNAMIC = 100
}

// Mode constants
object LauncherModes {
    const val MODE_SEARCH = 0
    const val MODE_LAYOUTS = 2
    const val MODE_RESOLUTION = 3
    const val MODE_DPI = 4
    const val MODE_BLACKLIST = 5
    const val MODE_PROFILES = 6
    const val MODE_KEYBINDS = 7
    const val MODE_SETTINGS = 8
    const val MODE_REFRESH = 9
}

// Focus area constants
object FocusAreas {
    const val FOCUS_SEARCH = 0
    const val FOCUS_QUEUE = 1
    const val FOCUS_LIST = 2
}

// Package constants
const val PACKAGE_BLANK = "internal.blank.spacer"