package com.katsuyamaki.DroidOSLauncher

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent

/**
 * Handles keyboard input for the Launcher.
 * Extracts input handling logic from FloatingLauncherService.
 */
class LauncherInputHandler(
    private val context: Context,
    private val callback: InputCallback
) {
    companion object {
        private const val TAG = "LauncherInputHandler"
        const val MOD_CUSTOM = -999
    }

    // Callback interface for service communication
    interface InputCallback {
        // State getters
        fun getCustomModKey(): Int
        fun isCustomModLatched(): Boolean
        fun setCustomModLatched(value: Boolean)
        fun getPendingCommandId(): String?
        fun getVqCursorIndex(): Int
        fun setVqCursorIndex(value: Int)
        fun getSelectedAppsQueueSize(): Int
        fun getAvailableCommands(): List<CommandDef>
        fun getKeybind(cmdId: String): Pair<Int, Int>
        
        // Actions
        fun onToast(msg: String)
        fun onAbortCommandMode()
        fun onShowVisualQueue(prompt: String, highlightIndex: Int)
        fun onHandleCommandInput(num: Int)
        fun onTriggerCommand(cmd: CommandDef)
        fun getVisualQueuePromptText(): String
        
        // For super call
        fun onUnhandledKeyEvent(event: KeyEvent): Boolean
    }

    /**
     * Hardware key event handler (AccessibilityService.onKeyEvent)
     */
    fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return false

        val keyCode = event.keyCode
        val metaState = event.metaState

        // CHECK CUSTOM MODIFIER
        val customModKey = callback.getCustomModKey()
        if (customModKey != 0 && keyCode == customModKey) {
            callback.setCustomModLatched(true)
            callback.onToast("Custom Mod Active...")
            return true
        }

        // DEBUG: Log every key press
        val keyName = KeyEvent.keyCodeToString(keyCode)
        val metaStr = if (metaState != 0) "Meta($metaState)" else "None"
        Log.d("DroidOS_Keys", "INPUT: Key=$keyName($keyCode) Meta=$metaStr($metaState)")

        // 1. INPUT MODE (Entering Numbers or Arrow Navigation)
        if (callback.getPendingCommandId() != null) {
            if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                callback.onAbortCommandMode()
                return true
            }

            // Arrow key navigation in visual queue
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                val cursorIndex = callback.getVqCursorIndex()
                if (cursorIndex > 0) {
                    callback.setVqCursorIndex(cursorIndex - 1)
                    callback.onShowVisualQueue(callback.getVisualQueuePromptText(), cursorIndex - 1)
                }
                return true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                val cursorIndex = callback.getVqCursorIndex()
                if (cursorIndex < callback.getSelectedAppsQueueSize() - 1) {
                    callback.setVqCursorIndex(cursorIndex + 1)
                    callback.onShowVisualQueue(callback.getVisualQueuePromptText(), cursorIndex + 1)
                }
                return true
            }
            
            // Space or Enter confirms the arrow-key selection
            if (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                callback.onHandleCommandInput(callback.getVqCursorIndex() + 1)
                return true
            }

            // Handle Numbers
            val num = keyEventToNumber(keyCode)
            if (num != -1) {
                callback.onHandleCommandInput(num)
                return true
            }

            // Any other key cancels
            callback.onAbortCommandMode()
            return true
        }

        // 2. TRIGGER MODE (Detecting Hotkeys)
        var commandTriggered = false

        for (cmd in callback.getAvailableCommands()) {
            val bind = callback.getKeybind(cmd.id)

            if (bind.second != 0 && bind.second == keyCode) {
                Log.d("DroidOS_Keys", " -> Key Match for '${cmd.label}'. Checking modifiers...")
                Log.d("DroidOS_Keys", "    Required: ${bind.first} | Current: $metaState")

                if (checkModifiers(metaState, bind.first, callback.isCustomModLatched())) {
                    Log.d("DroidOS_Keys", "    MATCH! Triggering...")
                    callback.onTriggerCommand(cmd)
                    commandTriggered = true
                    break
                } else {
                    Log.d("DroidOS_Keys", "    Modifier Mismatch.")
                }
            }
        }

        // AUTO-RESET LATCH
        if (callback.isCustomModLatched()) {
            callback.setCustomModLatched(false)
            callback.onToast(if (commandTriggered) "Command Executed" else "Command Cancelled")
            return true
        }

        if (commandTriggered) return true

        return callback.onUnhandledKeyEvent(event)
    }

    /**
     * Check if required modifiers match current state
     */
    fun checkModifiers(currentMeta: Int, requiredMeta: Int, isLatched: Boolean): Boolean {
        if (requiredMeta == 0) return true
        
        // Custom Modifier Logic
        if (requiredMeta == MOD_CUSTOM) {
            return isLatched
        }
        
        // Standard Android Meta Flags
        return (currentMeta and requiredMeta) != 0
    }

    /**
     * Convert KeyEvent keycode to number (0-9)
     */
    fun keyEventToNumber(code: Int): Int {
        return when (code) {
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> code - KeyEvent.KEYCODE_0
            in KeyEvent.KEYCODE_NUMPAD_0..KeyEvent.KEYCODE_NUMPAD_9 -> code - KeyEvent.KEYCODE_NUMPAD_0
            // QWERTY top-row fallback
            KeyEvent.KEYCODE_Q -> 1
            KeyEvent.KEYCODE_W -> 2
            KeyEvent.KEYCODE_E -> 3
            KeyEvent.KEYCODE_R -> 4
            KeyEvent.KEYCODE_T -> 5
            KeyEvent.KEYCODE_Y -> 6
            KeyEvent.KEYCODE_U -> 7
            KeyEvent.KEYCODE_I -> 8
            KeyEvent.KEYCODE_O -> 9
            KeyEvent.KEYCODE_P -> 0
            else -> -1
        }
    }
}
