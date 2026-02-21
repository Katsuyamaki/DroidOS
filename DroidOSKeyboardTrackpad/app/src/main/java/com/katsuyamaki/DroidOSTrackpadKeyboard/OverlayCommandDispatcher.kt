package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Intent

/**
 * OverlayCommandDispatcher
 * Handles all broadcast commands for OverlayService.
 * Consolidates logic from commandReceiver and switchReceiver.
 */
class OverlayCommandDispatcher(private val service: OverlayService) {

    companion object {
        private const val TAG = "OverlayCommandDispatcher"
    }

    /**
     * Handles incoming broadcast intents.
     * Routes commands to appropriate service methods.
     */
    fun handleCommand(intent: Intent?) {
        val action = intent?.action ?: return

        // Helper to match command suffixes (supports old and new package names)
        fun matches(suffix: String): Boolean = action.endsWith(suffix)

        when {
            // === COMMAND RECEIVER ACTIONS ===
            matches("SOFT_RESTART") && !action.contains("SWITCH") -> {

                // Check if this is from switchReceiver (has DISPLAY_ID) or commandReceiver
                if (intent.hasExtra("DISPLAY_ID")) {
                    val targetDisplayId = intent.getIntExtra("DISPLAY_ID", service.currentDisplayId)
                    service.handler.post {
                        service.saveCurrentState()
                        service.removeOldViews()
                        service.handler.postDelayed({
                            service.setupUI(targetDisplayId)
                            service.enforceZOrder()
                            service.showToast("Trackpad Soft Restarted")
                        }, 200)
                    }
                } else {
                    service.performSoftRestart()
                }
            }

            matches("ENFORCE_ZORDER") -> {

                service.handler.post { service.enforceZOrder() }
            }

            matches("MOVE_TO_DISPLAY") -> {
                val targetId = intent.getIntExtra("displayId", 0)

                service.handler.post {
                    service.saveCurrentState()
                    service.removeOldViews()
                    service.setupUI(targetId)
                    service.enforceZOrder()
                }
            }

            matches("TOGGLE_MIRROR") || matches("TOGGLE_VIRTUAL_MIRROR") -> {

                service.handler.post { service.toggleVirtualMirrorMode() }
            }

            matches("OPEN_DRAWER") -> {

                service.handler.post { service.toggleDrawer() }
            }

            matches("STOP_SERVICE") -> {

                service.forceExit()
            }

            matches("SET_INPUT_CAPTURE") -> {
                val capture = intent.getBooleanExtra("CAPTURE", false)

                service.keyboardOverlay?.setInputCaptureMode(capture)
            }

            matches("SET_CUSTOM_MOD") -> {
                val keyCode = intent.getIntExtra("KEYCODE", 0)

                service.prefs.customModKey = keyCode
                service.keyboardOverlay?.setCustomModKey(keyCode)
            }

            matches("SET_NUM_LAYER") -> {
                val active = intent.getBooleanExtra("ACTIVE", false)
                service.keyboardOverlay?.setNumberLayerOverride(active)
            }

            matches("UPDATE_KEYBINDS") -> {
                val keybinds = intent.getStringArrayListExtra("KEYBINDS")

                if (keybinds != null) {
                    service.launcherBlockedShortcuts.clear()
                    service.launcherBlockedShortcuts.addAll(keybinds)
                    service.keyboardOverlay?.setLauncherBlockedShortcuts(service.launcherBlockedShortcuts)

                }
            }

            // === SWITCH RECEIVER ACTIONS ===
            action == "SWITCH_DISPLAY" -> service.switchDisplay()

            action == "CYCLE_INPUT_TARGET" -> service.cycleInputTarget()

            action == "RESET_CURSOR" -> service.resetCursorCenter()

            action == "SET_CURSOR_POS" -> {
                val x = intent.getFloatExtra("X", -1f)
                val y = intent.getFloatExtra("Y", -1f)
                if (x >= 0 && y >= 0) service.setCursorPosition(x, y)
            }

            action == "TOGGLE_DEBUG" -> service.toggleDebugMode()

            action == "com.katsuyamaki.DroidOSTrackpadKeyboard.LIST_KEYBOARDS" -> {
                Thread {
                    try {
                        val allImes = service.shellService?.runCommand("ime list -a -s") ?: ""
                        val enabledImes = service.shellService?.runCommand("ime list -s") ?: ""
                        val result = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.LIST_KEYBOARDS_RESULT")
                        result.setPackage(service.packageName)
                        result.putExtra("ALL_IMES", allImes)
                        result.putExtra("ENABLED_IMES", enabledImes)
                        service.sendBroadcast(result)
                    } catch (e: Exception) {

                    }
                }.start()
            }

            action == "com.katsuyamaki.DroidOSTrackpadKeyboard.SWITCH_KEYBOARD" -> {
                val imeId = intent.getStringExtra("IME_ID") ?: return
                val imeName = intent.getStringExtra("IME_NAME") ?: "keyboard"
                Thread {
                    try {
                        service.shellService?.runCommand("settings put secure default_input_method $imeId")
                        service.shellService?.runCommand("ime set $imeId")
                        service.handler.post { service.showToast("Switched to $imeName") }
                    } catch (e: Exception) {
                        service.handler.post { service.showToast("Failed to switch keyboard") }
                    }
                }.start()
            }

            action == "FORCE_KEYBOARD" || action == "TOGGLE_CUSTOM_KEYBOARD" -> {
                service.handleKeyboardToggle(intent)
            }

            action == "PRESERVE_KEYBOARD" -> {
                service.preserveKeyboardUntil = System.currentTimeMillis() + 1000L
                android.util.Log.d("OverlayService", "PRESERVE_KB: received via dispatcher, set until=${service.preserveKeyboardUntil}")
            }

            action == "DOCK_PREF_CHANGED" -> {
                service.handleDockPrefChanged(intent)
            }

            action == "APPLY_DOCK_MODE" -> {
                service.handleApplyDockMode(intent)
            }

            action == "DOCK_POPUP_VISIBLE" -> {
                val popupVisible = intent.getBooleanExtra("VISIBLE", false)
                service.handleDockPopupVisibility(popupVisible)
            }

            action == "OPEN_MENU" -> {
                service.menuManager?.show()
                service.enforceZOrder()
            }

            action == "SET_TRACKPAD_VISIBILITY" -> {
                val visible = intent.getBooleanExtra("VISIBLE", true)
                val menuDisplayId = intent.getIntExtra("MENU_DISPLAY_ID", -1)
                if (visible) {
                    service.setTrackpadVisibility(true)
                } else {
                    if (menuDisplayId == -1 || menuDisplayId == service.currentDisplayId) {
                        service.setTrackpadVisibility(false)
                    }
                }
            }

            action == "SET_PREVIEW_MODE" -> {
                service.setPreviewMode(intent.getBooleanExtra("PREVIEW_MODE", false))
            }

            action == "VOICE_TYPE_TRIGGERED" -> {
                service.handleVoiceTypeTriggered()
            }

            action == "REQUEST_VOICE_INPUT" -> {
                service.handler.post {
                    android.widget.Toast.makeText(service, "Voice Requested...", android.widget.Toast.LENGTH_SHORT).show()
                }
                service.triggerVoiceTyping()
            }

            action == Intent.ACTION_SCREEN_ON -> {
                if (service.currentDisplayId == 1 && service.prefs.prefBlockSoftKeyboard) {
                    service.imeManager?.triggerAggressiveBlocking()
                }
            }

            matches("MOVE_TO_VIRTUAL") -> {

                val virtualDisplayId = intent.getIntExtra("DISPLAY_ID", 2)
                service.handler.post { service.moveToVirtualDisplayAndEnableMirror(virtualDisplayId) }
            }

            matches("RETURN_TO_PHYSICAL") -> {

                val physicalDisplayId = intent.getIntExtra("DISPLAY_ID", 0)
                service.handler.post { service.returnToPhysicalDisplay(physicalDisplayId) }
            }

            matches("GET_STATUS") -> {

                service.showToast("D:${service.currentDisplayId} T:${service.inputTargetDisplayId} M:${if(service.prefs.prefVirtualMirrorMode) "ON" else "OFF"}")
            }
        }
    }
}