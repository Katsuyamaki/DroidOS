package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context

internal class OverlayLayoutStateStore(private val service: OverlayService) {

    fun getLegacyProfileKey(): String {
        val mode = if (service.prefs.prefVirtualMirrorMode) "MIRROR" else "STD"
        val orientSuffix = if (service.uiScreenWidth > service.uiScreenHeight) "_L" else "_P"
        return "P_${service.uiScreenWidth}_${service.uiScreenHeight}_$mode$orientSuffix"
    }

    fun getProfileKey(): String {
        val mode = if (service.prefs.prefVirtualMirrorMode) "MIRROR" else "STD"
        val orientSuffix = if (service.uiScreenWidth > service.uiScreenHeight) "_L" else "_P"
        return "P_${service.uiScreenWidth}_${service.uiScreenHeight}_D${service.currentDisplayId}_$mode$orientSuffix"
    }

    fun restoreTrackpadBoundsOnly() {
        val p = service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val key = getProfileKey()
        val legacyKey = getLegacyProfileKey()
        val effectiveKey = when {
            p.contains("X_$key") -> key
            p.contains("X_$legacyKey") -> legacyKey
            else -> key
        }

        service.trackpadParams.x = p.getInt("X_$effectiveKey", service.trackpadParams.x)
        service.trackpadParams.y = p.getInt("Y_$effectiveKey", service.trackpadParams.y)
        service.trackpadParams.width = p.getInt("W_$effectiveKey", service.trackpadParams.width)
        service.trackpadParams.height = p.getInt("H_$effectiveKey", service.trackpadParams.height)

        service.prefs.prefBubbleX = when {
            p.contains("BUBBLE_X_$effectiveKey") -> p.getInt("BUBBLE_X_$effectiveKey", service.prefs.prefBubbleX)
            p.contains("bubble_x") -> p.getInt("bubble_x", service.prefs.prefBubbleX)
            else -> service.prefs.prefBubbleX
        }
        service.prefs.prefBubbleY = when {
            p.contains("BUBBLE_Y_$effectiveKey") -> p.getInt("BUBBLE_Y_$effectiveKey", service.prefs.prefBubbleY)
            p.contains("bubble_y") -> p.getInt("bubble_y", service.prefs.prefBubbleY)
            else -> service.prefs.prefBubbleY
        }
        service.prefs.prefBubbleSize = when {
            p.contains("BUBBLE_SIZE_$effectiveKey") -> p.getInt("BUBBLE_SIZE_$effectiveKey", service.prefs.prefBubbleSize)
            p.contains("bubble_size") -> p.getInt("bubble_size", service.prefs.prefBubbleSize)
            else -> service.prefs.prefBubbleSize
        }.coerceIn(50, 200)
    }

    fun getSavedProfileList(): List<String> {
        val p = service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val allKeys = p.all.keys
        val profiles = java.util.HashSet<String>()
        val regex = Regex("X_P_(\\d+)_(\\d+)(?:_D(\\d+))?(?:_(STD|MIRROR))?(?:_([LP]))?")

        for (key in allKeys) {
            if (!key.startsWith("X_P_")) continue
            val match = regex.matchEntire(key)
            if (match != null) {
                val w = match.groupValues[1]
                val h = match.groupValues[2]
                val suffix = match.groupValues.getOrNull(3)
                val orient = match.groupValues.getOrNull(4)

                var displayLabel = "$w x $h"
                if (suffix == "MIRROR") displayLabel += " VM"
                if (orient == "L") displayLabel += " Land"
                else if (orient == "P") displayLabel += " Port"

                profiles.add(displayLabel)
            }
        }
        return profiles.sorted()
    }

    fun saveCurrentState() {
        service.savedKbX = service.keyboardOverlay?.getViewX() ?: service.savedKbX
        service.savedKbY = service.keyboardOverlay?.getViewY() ?: service.savedKbY
        service.savedKbW = service.keyboardOverlay?.getViewWidth() ?: service.savedKbW
        service.savedKbH = service.keyboardOverlay?.getViewHeight() ?: service.savedKbH
        service.captureLiveBubbleState()

        val p = service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        val key = getProfileKey()
        p.putInt("X_$key", service.trackpadParams.x)
        p.putInt("Y_$key", service.trackpadParams.y)
        p.putInt("W_$key", service.trackpadParams.width)
        p.putInt("H_$key", service.trackpadParams.height)

        p.putInt("BUBBLE_X_$key", service.prefs.prefBubbleX)
        p.putInt("BUBBLE_Y_$key", service.prefs.prefBubbleY)
        p.putInt("BUBBLE_SIZE_$key", service.prefs.prefBubbleSize)

        p.apply()
    }

    fun saveLayout() {
        val currentKbX = service.keyboardOverlay?.getViewX() ?: service.savedKbX
        val currentKbY = service.keyboardOverlay?.getViewY() ?: service.savedKbY
        val currentKbW = service.keyboardOverlay?.getViewWidth() ?: service.savedKbW
        val currentKbH = service.keyboardOverlay?.getViewHeight() ?: service.savedKbH

        val liveScale = service.keyboardOverlay?.getScale() ?: (service.prefs.prefKeyScale / 100f)
        service.prefs.prefKeyScale = (liveScale * 100).toInt()
        service.captureLiveBubbleState()

        service.savedKbX = currentKbX
        service.savedKbY = currentKbY
        service.savedKbW = currentKbW
        service.savedKbH = currentKbH

        val p = service.getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE).edit()
        val key = getProfileKey()

        p.putInt("X_$key", service.trackpadParams.x)
        p.putInt("Y_$key", service.trackpadParams.y)
        p.putInt("W_$key", service.trackpadParams.width)
        p.putInt("H_$key", service.trackpadParams.height)

        p.putInt("BUBBLE_X_$key", service.prefs.prefBubbleX)
        p.putInt("BUBBLE_Y_$key", service.prefs.prefBubbleY)
        p.putInt("BUBBLE_SIZE_$key", service.prefs.prefBubbleSize)

        val settingsStr = StringBuilder()
        settingsStr.append("${service.prefs.cursorSpeed};${service.prefs.scrollSpeed};${if(service.prefs.prefTapScroll) 1 else 0};${if(service.prefs.prefReverseScroll) 1 else 0};${service.prefs.prefAlpha};${service.prefs.prefBgAlpha};${service.prefs.prefKeyboardAlpha};${service.prefs.prefHandleSize};${service.prefs.prefHandleTouchSize};${service.prefs.prefScrollTouchSize};${service.prefs.prefScrollVisualSize};${service.prefs.prefCursorSize};${service.prefs.prefKeyScale};${if(service.prefs.prefAutomationEnabled) 1 else 0};${if(service.prefs.prefAnchored) 1 else 0};${service.prefs.prefBubbleSize};${service.prefs.prefBubbleAlpha};${service.prefs.prefBubbleIconIndex};${service.prefs.prefBubbleX};${service.prefs.prefBubbleY};${service.prefs.hardkeyVolUpTap};${service.prefs.hardkeyVolUpDouble};${service.prefs.hardkeyVolUpHold};${service.prefs.hardkeyVolDownTap};${service.prefs.hardkeyVolDownDouble};${service.prefs.hardkeyVolDownHold};${service.prefs.hardkeyPowerDouble};")
        settingsStr.append("${if(service.prefs.prefVibrate) 1 else 0};${if(service.prefs.prefVPosLeft) 1 else 0};${if(service.prefs.prefHPosTop) 1 else 0};")
        settingsStr.append("${service.prefs.prefPredictionAggression};")
        settingsStr.append("$currentKbX;$currentKbY;$currentKbW;$currentKbH;")

        val dockSavePrefs = service.getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE)
        val dockOs = if (service.uiScreenWidth > service.uiScreenHeight) "_L" else "_P"
        val saveDockMode = dockSavePrefs.getBoolean("dock_mode_d${service.currentDisplayId}$dockOs", dockSavePrefs.getBoolean("dock_mode_d${service.currentDisplayId}", dockSavePrefs.getBoolean("dock_mode", false)))
        val saveAutoShow = dockSavePrefs.getBoolean("auto_show_overlay$dockOs", dockSavePrefs.getBoolean("auto_show_overlay", false))
        val saveAutoResize = dockSavePrefs.getBoolean("auto_resize$dockOs", dockSavePrefs.getBoolean("auto_resize", false))
        val saveResizeScale = dockSavePrefs.getInt("auto_resize_scale$dockOs", dockSavePrefs.getInt("auto_resize_scale", 0))
        val saveSyncMargin = dockSavePrefs.getBoolean("sync_margin$dockOs", dockSavePrefs.getBoolean("sync_margin", false))
        val saveKBAboveDock = dockSavePrefs.getBoolean("show_kb_above_dock$dockOs", dockSavePrefs.getBoolean("show_kb_above_dock", true))
        settingsStr.append("${if(saveDockMode) 1 else 0};${if(saveAutoShow) 1 else 0};${if(saveAutoResize) 1 else 0};$saveResizeScale;${if(saveSyncMargin) 1 else 0};${if(saveKBAboveDock) 1 else 0}")

        p.putString("SETTINGS_$key", settingsStr.toString())

        if (service.prefs.prefVirtualMirrorMode) {
            val mirrorParams = service.mirrorManager?.getParams()
            val mX = mirrorParams?.x ?: service.prefs.prefMirrorX
            val mY = mirrorParams?.y ?: service.prefs.prefMirrorY
            val mW = mirrorParams?.width ?: service.prefs.prefMirrorWidth
            val mH = mirrorParams?.height ?: service.prefs.prefMirrorHeight
            val mAlpha = service.prefs.prefMirrorAlpha

            p.putInt("MIRROR_X_$key", mX)
            p.putInt("MIRROR_Y_$key", mY)
            p.putInt("MIRROR_W_$key", mW)
            p.putInt("MIRROR_H_$key", mH)
            p.putInt("MIRROR_ALPHA_$key", mAlpha)
        }

        p.apply()
        service.showToast("Layout Saved (${if(service.prefs.prefVirtualMirrorMode) "Mirror" else "Std"})")
    }
}