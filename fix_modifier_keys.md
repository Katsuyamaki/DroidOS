# Code Update: Fix Modifier Keys and Keyboard Handle Revert

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt

### Function: `keyboardHandle`
**Action:** Replace function to revert behavior.
**Reason:** The top-left trackpad handle now simply toggles the Keyboard overlay (visible/hidden) instead of minimizing the entire UI.

```kotlin
    private fun keyboardHandle(event: MotionEvent): Boolean { 
        if (event.action == MotionEvent.ACTION_UP) {
            // Reverted: Just toggle keyboard visibility
            toggleCustomKeyboard()
        } 
        return true 
    }
```

### Function: `injectKeyFromKeyboard`
**Action:** Replace function to pass `metaState`.
**Reason:** Updated the injection logic so that when using the Null Keyboard (to block the system keyboard), modifier keys (Shift, Ctrl, Alt, Symbols) are correctly passed from the overlay to the input field.

```kotlin
    fun injectKeyFromKeyboard(keyCode: Int, metaState: Int) {
        Thread {
            try {
                // 1. CHECK ACTUAL SYSTEM STATE
                // Don't trust our internal boolean. Trust the Android Settings.
                val currentIme = android.provider.Settings.Secure.getString(contentResolver, "default_input_method") ?: ""
                val isNullKeyboardActive = currentIme.contains(packageName) && currentIme.contains("NullInputMethodService")

                if (isNullKeyboardActive) {
                    // STRATEGY A: NATIVE (Cleanest)
                    // Only works if we successfully switched to our keyboard
                    val intent = Intent("com.example.coverscreentester.INJECT_KEY")
                    intent.setPackage(packageName)
                    intent.putExtra("keyCode", keyCode)
                    intent.putExtra("metaState", metaState) // Pass modifiers (Shift/Alt/etc)
                    sendBroadcast(intent)
                } else {
                    // STRATEGY B: SHIZUKU INJECTION (Fallback / Gboard Active)
                    // Works even if switching failed. Uses Device ID 1 to mimic hardware.
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_DOWN, metaState, inputTargetDisplayId, 1)
                    Thread.sleep(10)
                    shellService?.injectKey(keyCode, KeyEvent.ACTION_UP, metaState, inputTargetDisplayId, 1)
                }
            } catch (e: Exception) { 
                Log.e(TAG, "Key injection failed", e) 
            }
        }.start()
    }
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/NullInputMethodService.kt

### Function: `receiver` (Object Definition)
**Action:** Replace the `receiver` object to handle `metaState` in `INJECT_KEY`.
**Reason:** Ensures modifier keys are correctly passed to the system input.

```kotlin
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                // COMMAND 1: SWITCH KEYBOARD (Restore Gboard)
                "com.example.coverscreentester.RESTORE_IME" -> {
                    val targetIme = intent.getStringExtra("target_ime")
                    if (!targetIme.isNullOrEmpty()) {
                        try {
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            // Using the Service's Token proves we are the active IME and have permission to switch
                            val token = window?.window?.attributes?.token
                            if (token != null) {
                                imm.setInputMethod(token, targetIme)
                            }
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
                
                // COMMAND 2: TYPE KEY (Native Input)
                "com.example.coverscreentester.INJECT_KEY" -> {
                    val keyCode = intent.getIntExtra("keyCode", 0)
                    val metaState = intent.getIntExtra("metaState", 0)
                    
                    if (keyCode != 0 && currentInputConnection != null) {
                        val now = System.currentTimeMillis()
                        // Send DOWN with Meta State
                        currentInputConnection.sendKeyEvent(
                            KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0, metaState)
                        )
                        // Send UP with Meta State
                        currentInputConnection.sendKeyEvent(
                            KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0, metaState)
                        )
                    }
                }
            }
        }
    }
```