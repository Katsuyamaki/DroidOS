package com.katsuyamaki.DroidOSLauncher

import android.view.KeyEvent

// === MODE CONSTANTS ===
const val MODE_SEARCH = 0      // App picker tab
const val MODE_LAYOUTS = 2     // Layout selection (skips 1)
const val MODE_RESOLUTION = 3  // Resolution settings
const val MODE_DPI = 4         // DPI settings
const val MODE_BLACKLIST = 5   // Blacklist management tab
const val MODE_PROFILES = 6    // Profiles tab
const val MODE_KEYBINDS = 7    // Keybinds tab
const val MODE_SETTINGS = 8    // Settings tab
const val MODE_REFRESH = 9     // Refresh Rate Tab

// === LAYOUT CONSTANTS ===
const val LAYOUT_FULL = 1
const val LAYOUT_SIDE_BY_SIDE = 2
const val LAYOUT_TOP_BOTTOM = 5
const val LAYOUT_TRI_EVEN = 3
const val LAYOUT_CORNERS = 4
const val LAYOUT_TRI_SIDE_MAIN_SIDE = 6
const val LAYOUT_QUAD_ROW_EVEN = 7
const val LAYOUT_QUAD_TALL_SHORT = 8
const val LAYOUT_HEX_TALL_SHORT = 9
const val LAYOUT_CUSTOM_DYNAMIC = 99

// === SERVICE CONSTANTS ===
const val CHANNEL_ID = "OverlayServiceChannel"
const val LAUNCHER_TAG = "FloatingService"
const val DEBUG_TAG = "DROIDOS_DEBUG"
const val ACTION_OPEN_DRAWER = "com.katsuyamaki.DroidOSLauncher.OPEN_DRAWER"
const val ACTION_UPDATE_ICON = "com.katsuyamaki.DroidOSLauncher.UPDATE_ICON"
const val ACTION_CYCLE_DISPLAY = "com.katsuyamaki.DroidOSLauncher.CYCLE_DISPLAY"
const val HIGHLIGHT_COLOR = 0xFF00A0E9.toInt()

// === SUPPORTED KEYS MAP ===
val SUPPORTED_KEYS = linkedMapOf(
    "A" to KeyEvent.KEYCODE_A, "B" to KeyEvent.KEYCODE_B, "C" to KeyEvent.KEYCODE_C,
    "D" to KeyEvent.KEYCODE_D, "E" to KeyEvent.KEYCODE_E, "F" to KeyEvent.KEYCODE_F,
    "G" to KeyEvent.KEYCODE_G, "H" to KeyEvent.KEYCODE_H, "I" to KeyEvent.KEYCODE_I,
    "J" to KeyEvent.KEYCODE_J, "K" to KeyEvent.KEYCODE_K, "L" to KeyEvent.KEYCODE_L,
    "M" to KeyEvent.KEYCODE_M, "N" to KeyEvent.KEYCODE_N, "O" to KeyEvent.KEYCODE_O,
    "P" to KeyEvent.KEYCODE_P, "Q" to KeyEvent.KEYCODE_Q, "R" to KeyEvent.KEYCODE_R,
    "S" to KeyEvent.KEYCODE_S, "T" to KeyEvent.KEYCODE_T, "U" to KeyEvent.KEYCODE_U,
    "V" to KeyEvent.KEYCODE_V, "W" to KeyEvent.KEYCODE_W, "X" to KeyEvent.KEYCODE_X,
    "Y" to KeyEvent.KEYCODE_Y, "Z" to KeyEvent.KEYCODE_Z,
    "0" to KeyEvent.KEYCODE_0, "1" to KeyEvent.KEYCODE_1, "2" to KeyEvent.KEYCODE_2,
    "3" to KeyEvent.KEYCODE_3, "4" to KeyEvent.KEYCODE_4, "5" to KeyEvent.KEYCODE_5,
    "6" to KeyEvent.KEYCODE_6, "7" to KeyEvent.KEYCODE_7, "8" to KeyEvent.KEYCODE_8, "9" to KeyEvent.KEYCODE_9,
    "Left" to KeyEvent.KEYCODE_DPAD_LEFT, "Right" to KeyEvent.KEYCODE_DPAD_RIGHT,
    "Up" to KeyEvent.KEYCODE_DPAD_UP, "Down" to KeyEvent.KEYCODE_DPAD_DOWN,
    "Space" to KeyEvent.KEYCODE_SPACE, "Enter" to KeyEvent.KEYCODE_ENTER,
    "Tab" to KeyEvent.KEYCODE_TAB, "Esc" to KeyEvent.KEYCODE_ESCAPE,
    "Backspace" to KeyEvent.KEYCODE_DEL, "Delete" to KeyEvent.KEYCODE_FORWARD_DEL,
    "Home" to KeyEvent.KEYCODE_MOVE_HOME, "End" to KeyEvent.KEYCODE_MOVE_END,
    "PageUp" to KeyEvent.KEYCODE_PAGE_UP, "PageDn" to KeyEvent.KEYCODE_PAGE_DOWN,
    "F1" to KeyEvent.KEYCODE_F1, "F2" to KeyEvent.KEYCODE_F2, "F3" to KeyEvent.KEYCODE_F3,
    "F4" to KeyEvent.KEYCODE_F4, "F5" to KeyEvent.KEYCODE_F5, "F6" to KeyEvent.KEYCODE_F6,
    "F7" to KeyEvent.KEYCODE_F7, "F8" to KeyEvent.KEYCODE_F8, "F9" to KeyEvent.KEYCODE_F9,
    "F10" to KeyEvent.KEYCODE_F10, "F11" to KeyEvent.KEYCODE_F11, "F12" to KeyEvent.KEYCODE_F12
)

// ===================================================================================
// BROADCAST COMMAND CHECKLIST - Adding a New Command
// ===================================================================================
// When creating a new broadcast command, update the following locations:
//
// 1. AVAILABLE_COMMANDS (below)
//    - Add CommandDef("CMD_ID", "Label", argCount, "Description")
//    - argCount: 0 = no args, 1 = single INDEX, 2 = INDEX_A + INDEX_B
//
// 2. AppPreferences.kt - getDefaultKeybind()
//    - Add default keybind: "CMD_ID" -> Pair(modifier, keyCode)
//    - Modifiers: 0=None, 2=Alt (matches META_ALT_ON), 1=Shift, etc.
//    - NOTE: Modifier values must match Android KeyEvent.META_* flags for HW keyboard
//
// 3. handleWindowManagerCommand() - Command Handler
//    - Add "CMD_ID" -> { ... } case in the when block
//    - For drawer-opening commands (like OPEN_MOVE_TO), call triggerCommand()
//
// 4. buildAdbCommand() - ADB Command String
//    - Add "CMD_ID" -> "adb shell am broadcast ..." for Settings display
//
// HARDWARE KEYBOARD FLOW:
//    - onKeyEvent() loops through AVAILABLE_COMMANDS and checks keybinds
//    - When matched, it calls triggerCommand(cmd)
//    - For argCount=0: executes immediately OR handles special cases
//    - For argCount>=1: enters visual queue input mode
//
// 5. FOR INTERACTIVE COMMANDS (like OPEN_MOVE_TO, OPEN_SWAP):
//    These require additional state management and UI handling:
//
//    a) State Variables (near line 378):
//       - Add isYourModeActive: Boolean = false
//       - Reuse openMoveToApp and showSlotNumbersInQueue if similar flow
//
//    b) triggerCommand() - Mode Initialization (CRITICAL FOR HW KEYBOARD):
//       - Add special case INSIDE the "if (cmd.argCount == 0)" block
//       - MUST include ALL of these steps:
//         1. Set your mode flag = true (reset other mode flags)
//         2. Reset openMoveToApp = null
//         3. Reset showSlotNumbersInQueue = false
//         4. Open drawer: if (!isExpanded) toggleDrawer()
//         5. Switch mode: switchMode(MODE_SEARCH)
//         6. Set focus: currentFocusArea = FOCUS_SEARCH
//         7. Setup search bar: et?.setText(""); et?.requestFocus()
//         8. Show prompt: debugStatusView?.text = "Your Mode: Select an app"
//         9. RETURN to prevent falling through to immediate execution
//
//    c) executeYourCommand() Function:
//       - Create function to execute the command after user selection
//       - Reset mode state, release keyboard capture, perform action
//
//    d) cancelOpenMoveToMode() - Mode Cancellation:
//       - Add your mode flag reset
//
//    e) handleRemoteKeyEvent() - FOCUS_QUEUE Section:
//       - DPAD_UP/DOWN: Add navigation block check for your mode
//       - ENTER: Add confirmation handler for your mode
//       - ESCAPE: Add cancellation handler for your mode
//       - SPACE: Add confirmation handler for your mode
//       - Number keys (else block): Add slot selection for your mode
//       - Update debugStatusView visibility checks
//
//    f) searchBar setOnKeyListener:
//       - DPAD_UP/DOWN in FOCUS_QUEUE: Add navigation block
//       - ESCAPE: Add cancellation handler
//       - Number keys section: Add slot selection
//       - Update debugStatusView visibility checks
//
//    g) drawerView setOnKeyListener - FOCUS_QUEUE Section:
//       - DPAD_UP/DOWN: Add navigation block
//       - ENTER/SPACE: Add confirmation handler
//       - Update debugStatusView visibility checks
//       - Queue navigation text: Add mode check
//
//    h) toggleDrawer():
//       - Add mode check to skip state reset when opening for your command
//       - Add mode check to skip showQueueDebugState()
//
//    i) addToSelection() - App Selection Intercept:
//       - Add your mode to the intercept condition
//
// REFERENCE: See OPEN_MOVE_TO and OPEN_SWAP implementations for examples
// ===================================================================================
val AVAILABLE_COMMANDS = listOf(
    CommandDef("OPEN_DRAWER", "Toggle Drawer", 0, "Show/Hide Launcher"),
    CommandDef("SET_FOCUS", "Set Focus", 1, "Focus app in slot #"),
    CommandDef("FOCUS_LAST", "Focus Last", 0, "Switch to previous app"),
    CommandDef("KILL", "Kill App", 1, "Force stop app in slot"),
    CommandDef("SWAP_ACTIVE_LEFT", "Move Active Left", 0, "Move focused window to prev slot"),
    CommandDef("SWAP_ACTIVE_RIGHT", "Move Active Right", 0, "Move focused window to next slot"),
    CommandDef("MINIMIZE", "Minimize", 1, "Minimize slot (shift others)"),
    CommandDef("UNMINIMIZE", "Restore", 1, "Restore app in slot"),
    CommandDef("HIDE", "Hide (Blank)", 1, "Replace slot with blank space"),
    CommandDef("SWAP", "Swap Slots", 2, "Swap app in Slot A with Slot B"),
    CommandDef("MOVE_TO", "Move To", 2, "Move app to slot # (shifts others)"),
    CommandDef("OPEN_MOVE_TO", "Open & Move To", 0, "Open app and place in slot #"),
    CommandDef("OPEN_SWAP", "Open & Swap", 0, "Open app and swap with slot #"),
    CommandDef("MINIMIZE_ALL", "Minimize All", 0, "Minimize all tiled apps"),
    CommandDef("RESTORE_ALL", "Restore All", 0, "Restore minimized apps to slots")
)