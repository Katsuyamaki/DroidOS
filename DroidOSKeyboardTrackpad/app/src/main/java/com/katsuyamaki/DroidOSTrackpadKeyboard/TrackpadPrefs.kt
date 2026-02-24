package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context

class TrackpadPrefs {
    var cursorSpeed = 2.5f
    var scrollSpeed = 1.0f
    var prefTapScroll = true
    var prefVibrate = false
    var prefReverseScroll = true
    var prefAlpha = 200
    var prefBgAlpha = 0
    var prefKeyboardAlpha = 200
    var prefHandleSize = 60
    var prefVPosLeft = false
    var prefHPosTop = false
    var prefLocked = false
    var prefHandleTouchSize = 80
    var prefScrollTouchSize = 80
    var prefScrollVisualSize = 4
    var prefCursorSize = 50
    var prefKeyScale = 69 // Default to 69% to match Reset Position (0.55 ratio)
    var prefUseAltScreenOff = true
    var prefAutomationEnabled = true
    var prefBubbleX = 50
    var prefBubbleY = 300
    var prefAnchored = false
    var prefBubbleSize = 100
    var prefBubbleIconIndex = 0
    var prefBubbleAlpha = 255
    var prefPersistentService = false
    var prefBubbleIncludeTrackpad = true
    var prefBubbleIncludeKeyboard = true
    var prefShowKBAboveDock = true // Default ON - position overlay KB above DockIME toolbar
    var prefBlockSoftKeyboard = false
    var prefPredictionAggression = 1.1f
    var prefSpacebarMouseExtended = false
    var prefAlwaysPreferGboard = true  // NEW: Fight Samsung's keyboard takeover
    var hardkeyVolUpTap = "none"
    var hardkeyVolUpDouble = "none"
    var hardkeyVolUpHold = "none"
    var hardkeyVolDownTap = "none"
    var hardkeyVolDownDouble = "none"
    var hardkeyVolDownHold = "none"
    var hardkeyPowerDouble = "none"
    var doubleTapMs = 300
    var holdDurationMs = 400
    var displayOffMode = "alternate"
    var prefVirtualMirrorMode = false
    var prefMirrorOrientDelayMs = 1000L  // Default 1 second orientation delay
    var prefMirrorAlpha = 200
    var prefMirrorX = -1      // -1 = auto center
    var prefMirrorY = 0
    var prefMirrorWidth = -1  // -1 = auto
    var prefMirrorHeight = -1 // -1 = auto
    var prefOverrideSystemShortcuts = true
    var customModKey = 0 // To persist across view rebuilds

    fun load(context: Context) {
        val p = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        cursorSpeed = p.getFloat("cursor_speed", 2.5f)
        scrollSpeed = p.getFloat("scroll_speed", 0.6f) // CHANGED: Default 0.6f (Slider 6)
        prefTapScroll = p.getBoolean("tap_scroll", true)
        prefVibrate = p.getBoolean("vibrate", true)
        prefReverseScroll = p.getBoolean("reverse_scroll", false) // CHANGED: Default false
        prefAlpha = p.getInt("alpha", 50) // CHANGED: Default 50
        prefBgAlpha = p.getInt("bg_alpha", 220) // CHANGED: Default 220
        prefKeyboardAlpha = p.getInt("keyboard_alpha", 255) // CHANGED: Default 255
        prefHandleSize = p.getInt("handle_size", 14) // CHANGED: Default 14
        prefVPosLeft = p.getBoolean("v_pos_left", false)
        prefHPosTop = p.getBoolean("h_pos_top", false)
        prefAnchored = p.getBoolean("anchored", false)
        prefHandleTouchSize = p.getInt("handle_touch_size", 80)
        prefShowKBAboveDock = p.getBoolean("show_kb_above_dock", true)
        prefScrollTouchSize = p.getInt("scroll_touch_size", 80)
        prefScrollVisualSize = p.getInt("scroll_visual_size", 4)
        prefCursorSize = p.getInt("cursor_size", 50)
        prefKeyScale = p.getInt("keyboard_key_scale", 69) // Default 69 to match resetPosition
        prefUseAltScreenOff = p.getBoolean("use_alt_screen_off", true)
        prefAutomationEnabled = p.getBoolean("automation_enabled", false)
        prefBubbleX = p.getInt("bubble_x", -1)
        prefBubbleY = p.getInt("bubble_y", -1)
        prefBubbleSize = p.getInt("bubble_size", 100)
        prefBubbleIconIndex = p.getInt("bubble_icon_index", 0)
        prefBubbleAlpha = p.getInt("bubble_alpha", 255)
        prefPersistentService = p.getBoolean("persistent_service", false)
        prefBubbleIncludeTrackpad = p.getBoolean("bubble_include_trackpad", true)
        prefBubbleIncludeKeyboard = p.getBoolean("bubble_include_keyboard", true)
        prefBlockSoftKeyboard = p.getBoolean("block_soft_kb", false)
        prefPredictionAggression = p.getFloat("prediction_aggression", 1.1f)
        PredictionEngine.instance.speedThreshold = prefPredictionAggression
        prefSpacebarMouseExtended = p.getBoolean("spacebar_mouse_extended", false)
        hardkeyVolUpTap = p.getString("hardkey_vol_up_tap", "none") ?: "none"
        hardkeyVolUpDouble = p.getString("hardkey_vol_up_double", "none") ?: "none"
        hardkeyVolUpHold = p.getString("hardkey_vol_up_hold", "none") ?: "none"
        hardkeyVolDownTap = p.getString("hardkey_vol_down_tap", "none") ?: "none"
        hardkeyVolDownDouble = p.getString("hardkey_vol_down_double", "none") ?: "none"
        hardkeyVolDownHold = p.getString("hardkey_vol_down_hold", "none") ?: "none"
        hardkeyPowerDouble = p.getString("hardkey_power_double", "none") ?: "none"
        doubleTapMs = p.getInt("double_tap_ms", 300)
        holdDurationMs = p.getInt("hold_duration_ms", 400)
        prefVirtualMirrorMode = false
        prefOverrideSystemShortcuts = p.getBoolean("override_system_shortcuts", true)
        customModKey = p.getInt("custom_mod_key", 0)
        prefMirrorOrientDelayMs = p.getLong("mirror_orient_delay_ms", 1000L)
        prefMirrorAlpha = p.getInt("mirror_alpha", 200)
        prefMirrorX = p.getInt("mirror_x", -1)
        prefMirrorY = p.getInt("mirror_y", 0)
        prefMirrorWidth = p.getInt("mirror_width", -1)
        prefMirrorHeight = p.getInt("mirror_height", -1)
    }

    fun save(context: Context) {
        val e = context.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        e.putFloat("cursor_speed", cursorSpeed)
        e.putFloat("scroll_speed", scrollSpeed)
        e.putBoolean("tap_scroll", prefTapScroll)
        e.putBoolean("vibrate", prefVibrate)
        e.putBoolean("reverse_scroll", prefReverseScroll)
        e.putInt("alpha", prefAlpha)
        e.putInt("bg_alpha", prefBgAlpha)
        e.putInt("keyboard_alpha", prefKeyboardAlpha)
        e.putInt("handle_size", prefHandleSize)
        e.putBoolean("v_pos_left", prefVPosLeft)
        e.putBoolean("h_pos_top", prefHPosTop)
        e.putBoolean("anchored", prefAnchored)
        e.putInt("handle_touch_size", prefHandleTouchSize)
        e.putInt("scroll_touch_size", prefScrollTouchSize)
        e.putInt("scroll_visual_size", prefScrollVisualSize)
        e.putInt("cursor_size", prefCursorSize)
        e.putInt("keyboard_key_scale", prefKeyScale)
        e.putBoolean("use_alt_screen_off", prefUseAltScreenOff)
        e.putBoolean("automation_enabled", prefAutomationEnabled)
        e.putInt("bubble_x", prefBubbleX)
        e.putInt("bubble_y", prefBubbleY)
        e.putInt("bubble_size", prefBubbleSize)
        e.putInt("bubble_icon_index", prefBubbleIconIndex)
        e.putInt("bubble_alpha", prefBubbleAlpha)
        e.putBoolean("persistent_service", prefPersistentService)
        e.putBoolean("bubble_include_trackpad", prefBubbleIncludeTrackpad)
        e.putBoolean("bubble_include_keyboard", prefBubbleIncludeKeyboard)
        e.putBoolean("block_soft_kb", prefBlockSoftKeyboard)
        e.putBoolean("show_kb_above_dock", prefShowKBAboveDock)
        e.putFloat("prediction_aggression", prefPredictionAggression)
        e.putBoolean("spacebar_mouse_extended", prefSpacebarMouseExtended)
        e.putString("hardkey_vol_up_tap", hardkeyVolUpTap)
        e.putString("hardkey_vol_up_double", hardkeyVolUpDouble)
        e.putString("hardkey_vol_up_hold", hardkeyVolUpHold)
        e.putString("hardkey_vol_down_tap", hardkeyVolDownTap)
        e.putString("hardkey_vol_down_double", hardkeyVolDownDouble)
        e.putString("hardkey_vol_down_hold", hardkeyVolDownHold)
        e.putString("hardkey_power_double", hardkeyPowerDouble)
        e.putInt("double_tap_ms", doubleTapMs)
        e.putInt("hold_duration_ms", holdDurationMs)
        e.putBoolean("virtual_mirror_mode", prefVirtualMirrorMode)
        e.putLong("mirror_orient_delay_ms", prefMirrorOrientDelayMs)
        e.putBoolean("override_system_shortcuts", prefOverrideSystemShortcuts)
        e.putInt("custom_mod_key", customModKey)
        e.putInt("mirror_alpha", prefMirrorAlpha)
        e.putInt("mirror_x", prefMirrorX)
        e.putInt("mirror_y", prefMirrorY)
        e.putInt("mirror_width", prefMirrorWidth)
        e.putInt("mirror_height", prefMirrorHeight)
        e.apply()
    }
}
