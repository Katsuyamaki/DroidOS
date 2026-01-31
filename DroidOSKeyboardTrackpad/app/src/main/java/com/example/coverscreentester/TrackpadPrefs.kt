package com.example.coverscreentester

class TrackpadPrefs {
    var cursorSpeed = 2.5f
    var scrollSpeed = 6.0f
    var prefTapScroll = true
    var prefVibrate = false
    var prefReverseScroll = false
    var prefAlpha = 50
    var prefBgAlpha = 220
    var prefKeyboardAlpha = 255
    var prefHandleSize = 14
    var prefVPosLeft = false
    var prefHPosTop = false
    var prefLocked = false
    var prefHandleTouchSize = 80
    var prefScrollTouchSize = 80
    var prefScrollVisualSize = 4
    var prefCursorSize = 50
    var prefKeyScale = 135
    var prefUseAltScreenOff = true
    var prefAutomationEnabled = true
    var prefBubbleX = 50
    var prefBubbleY = 300
    var prefAnchored = false
    var prefBubbleSize = 100
    var prefBubbleIconIndex = 0
    var prefBubbleAlpha = 255
    var prefPersistentService = false
    var prefBlockSoftKeyboard = false

    // =================================================================================
    // VIRTUAL MIRROR MODE PREFERENCES
    // SUMMARY: Settings for displaying a mirror keyboard on remote/AR display.
    //          When enabled, touching the physical keyboard shows an orange orientation
    //          trail on both displays. After finger stops for orientDelayMs, normal
    //          keyboard input resumes.
    // =================================================================================
    var prefVirtualMirrorMode = false
    var prefMirrorOrientDelayMs = 400L  // Default 400ms orientation delay


    // =================================================================================
    // END BLOCK: VIRTUAL MIRROR MODE PREFERENCES
    // =================================================================================

    var prefOverrideSystemShortcuts = true
    var customModKey = 0 // To persist across view rebuilds

    var hardkeyVolUpTap = "left_click"
    var hardkeyVolUpDouble = "left_click"
    var hardkeyVolUpHold = "left_click"
    var hardkeyVolDownTap = "toggle_keyboard"
    var hardkeyVolDownDouble = "open_menu"
    var hardkeyVolDownHold = "action_back"
    var hardkeyPowerDouble = "none"
    var doubleTapMs = 300
    var holdDurationMs = 400
    var displayOffMode = "alternate"
}
