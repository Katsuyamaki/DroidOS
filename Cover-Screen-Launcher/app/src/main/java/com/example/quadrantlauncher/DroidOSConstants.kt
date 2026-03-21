package com.katsuyamaki.DroidOSFOSSLauncher

/**
 * Centralized constants for DroidOS FOSS inter-app communication.
 * All package names and broadcast action strings live here so a future
 * package rename only requires changing this file + manifests + gradle.
 */
object DroidOSConstants {

    // ── Package identifiers ──
    const val LAUNCHER_PACKAGE = "com.katsuyamaki.DroidOSFOSSLauncher"
    const val KEYBOARD_PACKAGE = "com.katsuyamaki.DroidOSFOSSKeyboardTrackpad"

    // ── Launcher action strings ──
    const val ACTION_TOGGLE_VIRTUAL_DISPLAY = "$LAUNCHER_PACKAGE.TOGGLE_VIRTUAL_DISPLAY"
    const val ACTION_OPEN_DRAWER           = "$LAUNCHER_PACKAGE.OPEN_DRAWER"
    const val ACTION_UPDATE_ICON           = "$LAUNCHER_PACKAGE.UPDATE_ICON"
    const val ACTION_CYCLE_DISPLAY         = "$LAUNCHER_PACKAGE.CYCLE_DISPLAY"
    const val ACTION_WINDOW_MANAGER        = "$LAUNCHER_PACKAGE.WINDOW_MANAGER"
    const val ACTION_REQUEST_CUSTOM_MOD_SYNC = "$LAUNCHER_PACKAGE.REQUEST_CUSTOM_MOD_SYNC"
    const val ACTION_REMOTE_KEY            = "$LAUNCHER_PACKAGE.REMOTE_KEY"
    const val ACTION_REQUEST_KEYBINDS      = "$LAUNCHER_PACKAGE.REQUEST_KEYBINDS"
    const val ACTION_SYNC_KEYBOARD_RATIO   = "$LAUNCHER_PACKAGE.SYNC_KEYBOARD_RATIO"

    // ── Launcher permission ──
    const val PERMISSION_LAUNCHER_INTERNAL = "$LAUNCHER_PACKAGE.permission.INTERNAL_COMMAND"

    // ── Keyboard action strings ──
    const val KB_ACTION_INJECT_TEXT        = "$KEYBOARD_PACKAGE.INJECT_TEXT"
    const val KB_ACTION_INJECT_KEY         = "$KEYBOARD_PACKAGE.INJECT_KEY"
    const val KB_ACTION_INJECT_DELETE      = "$KEYBOARD_PACKAGE.INJECT_DELETE"
    const val KB_ACTION_SOFT_RESTART       = "$KEYBOARD_PACKAGE.SOFT_RESTART"
    const val KB_ACTION_MOVE_TO_VIRTUAL    = "$KEYBOARD_PACKAGE.MOVE_TO_VIRTUAL"
    const val KB_ACTION_RETURN_TO_PHYSICAL = "$KEYBOARD_PACKAGE.RETURN_TO_PHYSICAL"
    const val KB_ACTION_ENFORCE_ZORDER     = "$KEYBOARD_PACKAGE.ENFORCE_ZORDER"
    const val KB_ACTION_TOGGLE_VIRTUAL_MIRROR = "$KEYBOARD_PACKAGE.TOGGLE_VIRTUAL_MIRROR"
    const val KB_ACTION_OPEN_DRAWER        = "$KEYBOARD_PACKAGE.OPEN_DRAWER"
    const val KB_ACTION_GET_STATUS         = "$KEYBOARD_PACKAGE.GET_STATUS"
    const val KB_ACTION_STOP_SERVICE       = "$KEYBOARD_PACKAGE.STOP_SERVICE"
    const val KB_ACTION_MOVE_TO_DISPLAY    = "$KEYBOARD_PACKAGE.MOVE_TO_DISPLAY"
    const val KB_ACTION_SET_INPUT_CAPTURE  = "$KEYBOARD_PACKAGE.SET_INPUT_CAPTURE"
    const val KB_ACTION_SET_NUM_LAYER      = "$KEYBOARD_PACKAGE.SET_NUM_LAYER"
    const val KB_ACTION_SET_CUSTOM_MOD     = "$KEYBOARD_PACKAGE.SET_CUSTOM_MOD"
    const val KB_ACTION_SET_TRACKPAD_VISIBILITY = "$KEYBOARD_PACKAGE.SET_TRACKPAD_VISIBILITY"
    const val KB_ACTION_UPDATE_KEYBINDS    = "$KEYBOARD_PACKAGE.UPDATE_KEYBINDS"

    // ── Keyboard permission ──
    const val PERMISSION_KEYBOARD_INTERNAL = "$KEYBOARD_PACKAGE.permission.INTERNAL_COMMAND"

    // ── IME component IDs ──
    const val NULL_IME_ID = "$KEYBOARD_PACKAGE.NullInputMethodService"
    const val OVERLAY_SERVICE_CLASS = "$KEYBOARD_PACKAGE.OverlayService"
}