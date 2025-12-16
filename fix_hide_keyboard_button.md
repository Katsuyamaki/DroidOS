# Code Update: Add "Hide Keyboard" button and fix modifier keys

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardView.kt

### `SpecialKey` (Enum)
**Action:** Replace Enum to add `HIDE_KEYBOARD`.

```kotlin
    enum class SpecialKey {
        BACKSPACE, ENTER, SPACE, SHIFT, CAPS_LOCK, SYMBOLS, ABC,
        TAB, ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT,
        HOME, END, DELETE, ESCAPE, CTRL, ALT,
        VOL_UP, VOL_DOWN, MUTE, BACK_NAV, FWD_NAV, VOICE_INPUT, HIDE_KEYBOARD
    }
```

### `navRow` (Property)
**Action:** Replace `navRow` definition to include `"HIDE_KB"`.

```kotlin
    // Row 6 (Moved SCREEN to far left, HIDE_KB next to it)
    private val navRow = listOf("SCREEN", "HIDE_KB", "MUTE", "VOL-", "VOL+", "BACK", "FWD", "MIC")
```

### `getDisplayText`
**Action:** Replace function to handle `"HIDE_KB"` label.

```kotlin
    private fun getDisplayText(key: String): String = when (key) {
        "SHIFT" -> if (currentState == KeyboardState.CAPS_LOCK) "⬆" else "⇧"
        "BKSP" -> "⌫"; "ENTER" -> "↵"; "SPACE" -> " "
        "SYM", "SYM1", "SYM2" -> "?123"; "ABC" -> "ABC"
        "TAB" -> "⇥"; "CTRL" -> "Ctrl"; "ALT" -> "Alt"; "ESC" -> "Esc"
        "←" -> "◀"; "→" -> "▶"; "↑" -> "▲"; "↓" -> "▼"
        "MUTE" -> "Mute"; "VOL-" -> "Vol-"; "VOL+" -> "Vol+"
        "BACK" -> "Back"; "FWD" -> "Fwd"; "MIC" -> "🎤"
        "SCREEN" -> if (isSymbolsActive()) "MODE" else "📺"
        "HIDE_KB" -> "▼"
        else -> key
    }
```

### `handleKeyPress`
**Action:** Replace function to handle `"HIDE_KB"` press.

```kotlin
    private fun handleKeyPress(key: String, fromRepeat: Boolean = false) {
        var meta = getMetaState()
        if (fromRepeat && !isKeyRepeatable(key)) return

        when (key) {
            "CTRL" -> { if (!fromRepeat) { isCtrlActive = !isCtrlActive; buildKeyboard() } }
            "ALT" -> { if (!fromRepeat) { isAltActive = !isAltActive; buildKeyboard() } }
            "SHIFT" -> { /* Handled in onKeyUp/Down */ }
            
            "BKSP" -> listener?.onSpecialKey(SpecialKey.BACKSPACE, meta)
            "ENTER" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.ENTER, meta) }
            "SPACE" -> listener?.onSpecialKey(SpecialKey.SPACE, meta)
            "TAB" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.TAB, meta) }
            "ESC" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.ESCAPE, meta) }
            
            "←" -> listener?.onSpecialKey(SpecialKey.ARROW_LEFT, meta)
            "→" -> listener?.onSpecialKey(SpecialKey.ARROW_RIGHT, meta)
            "↑" -> listener?.onSpecialKey(SpecialKey.ARROW_UP, meta)
            "↓" -> listener?.onSpecialKey(SpecialKey.ARROW_DOWN, meta)
            
            "MUTE" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.MUTE, meta) }
            "VOL-" -> listener?.onSpecialKey(SpecialKey.VOL_DOWN, meta)
            "VOL+" -> listener?.onSpecialKey(SpecialKey.VOL_UP, meta)
            "BACK" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.BACK_NAV, meta) }
            "FWD" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.FWD_NAV, meta) }
            "MIC" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.VOICE_INPUT, meta) }
            
            "SCREEN" -> { 
                if (!fromRepeat) {
                    if (isSymbolsActive()) {
                        listener?.onScreenModeChange()
                    } else {
                        listener?.onScreenToggle()
                    }
                }
            }
            "HIDE_KB" -> { if (!fromRepeat) listener?.onSpecialKey(SpecialKey.HIDE_KEYBOARD, meta) }
            
            "SYM", "SYM1" -> { if (!fromRepeat) { currentState = KeyboardState.SYMBOLS_1; buildKeyboard() } }
            "SYM2" -> { if (!fromRepeat) { currentState = KeyboardState.SYMBOLS_2; buildKeyboard() } }
            "ABC" -> { if (!fromRepeat) { currentState = KeyboardState.LOWERCASE; buildKeyboard() } }
            
            else -> {
                if (key.length == 1) {
                    val char = key[0]
                    val pair = getSymbolKeyCode(char)
                    val code = pair.first
                    val shiftNeeded = pair.second
                    if (shiftNeeded) meta = meta or KeyEvent.META_SHIFT_ON
                    listener?.onKeyPress(code, char, meta)
                    if (!fromRepeat && currentState == KeyboardState.UPPERCASE) { 
                        currentState = KeyboardState.LOWERCASE
                        buildKeyboard()
                    }
                }
            }
        }
        if (!fromRepeat && key != "CTRL" && key != "ALT" && key != "SHIFT") {
            if (isCtrlActive || isAltActive) {
                isCtrlActive = false; isAltActive = false; buildKeyboard()
            }
        }
    }
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt

### `onSpecialKey`
**Action:** Replace function to handle the new Hide action.

```kotlin
    override fun onSpecialKey(key: KeyboardView.SpecialKey, metaState: Int) {
        if (key == KeyboardView.SpecialKey.VOICE_INPUT) {
            triggerVoiceTyping()
            return
        }
        if (key == KeyboardView.SpecialKey.HIDE_KEYBOARD) {
            onCloseAction() // Calls the close/hide action passed from Service
            return
        }
        val keyCode = when (key) {
            KeyboardView.SpecialKey.BACKSPACE -> KeyEvent.KEYCODE_DEL
            KeyboardView.SpecialKey.ENTER -> KeyEvent.KEYCODE_ENTER
            KeyboardView.SpecialKey.SPACE -> KeyEvent.KEYCODE_SPACE
            KeyboardView.SpecialKey.TAB -> KeyEvent.KEYCODE_TAB
            KeyboardView.SpecialKey.ESCAPE -> KeyEvent.KEYCODE_ESCAPE
            KeyboardView.SpecialKey.ARROW_UP -> KeyEvent.KEYCODE_DPAD_UP
            KeyboardView.SpecialKey.ARROW_DOWN -> KeyEvent.KEYCODE_DPAD_DOWN
            KeyboardView.SpecialKey.ARROW_LEFT -> KeyEvent.KEYCODE_DPAD_LEFT
            KeyboardView.SpecialKey.ARROW_RIGHT -> KeyEvent.KEYCODE_DPAD_RIGHT
            KeyboardView.SpecialKey.HOME -> KeyEvent.KEYCODE_MOVE_HOME
            KeyboardView.SpecialKey.END -> KeyEvent.KEYCODE_MOVE_END
            KeyboardView.SpecialKey.DELETE -> KeyEvent.KEYCODE_FORWARD_DEL
            KeyboardView.SpecialKey.MUTE -> KeyEvent.KEYCODE_VOLUME_MUTE
            KeyboardView.SpecialKey.VOL_UP -> KeyEvent.KEYCODE_VOLUME_UP
            KeyboardView.SpecialKey.VOL_DOWN -> KeyEvent.KEYCODE_VOLUME_DOWN
            KeyboardView.SpecialKey.BACK_NAV -> KeyEvent.KEYCODE_BACK
            KeyboardView.SpecialKey.FWD_NAV -> KeyEvent.KEYCODE_FORWARD
            else -> return
        }
        injectKey(keyCode, metaState)
    }
```