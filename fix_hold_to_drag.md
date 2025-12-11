## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt

### 1. Update Runnables (Around line 250)
// Replace volUpHoldRunnable and volDownHoldRunnable to use ACTION_DOWN

```kotlin
    // Hold detection runnables - trigger hold action after delay
    private val volUpHoldRunnable = Runnable {
        volUpHoldTriggered = true
        // Execute DOWN phase of the hold action (starts drag if mapped to left_click)
        executeHardkeyAction(prefs.hardkeyVolUpHold, KeyEvent.ACTION_DOWN)
    }
    private val volDownHoldRunnable = Runnable {
        volDownHoldTriggered = true
        // Execute DOWN phase of the hold action
        executeHardkeyAction(prefs.hardkeyVolDownHold, KeyEvent.ACTION_DOWN)
    }
```

### 2. Replace onKeyEvent function
// Replace the entire onKeyEvent function to remove the 'isDirectMouseAction' checks and handle the release logic properly.

```kotlin
    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (isPreviewMode || !isTrackpadVisible) return super.onKeyEvent(event)
        
        val action = event.action
        val keyCode = event.keyCode
        
        // =========================
        // VOLUME UP
        // =========================
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            when (action) {
                KeyEvent.ACTION_DOWN -> {
                    if (!isLeftKeyHeld) {
                        isLeftKeyHeld = true
                        volUpHoldTriggered = false
                        handler.postDelayed(volUpHoldRunnable, prefs.holdDurationMs.toLong())
                    }
                }
                KeyEvent.ACTION_UP -> {
                    isLeftKeyHeld = false
                    handler.removeCallbacks(volUpHoldRunnable)
                    
                    if (volUpHoldTriggered) {
                        // Hold was active (Timer fired DOWN). Now execute UP to finish drag.
                        executeHardkeyAction(prefs.hardkeyVolUpHold, KeyEvent.ACTION_UP)
                    } else {
                        // Timer didn't fire -> It's a Tap
                        val timeSinceLastTap = System.currentTimeMillis() - lastVolUpTime
                        lastVolUpTime = System.currentTimeMillis()
                        
                        if (timeSinceLastTap < prefs.doubleTapMs && volUpTapCount == 1) {
                            handler.removeCallbacks(volUpDoubleTapRunnable)
                            volUpTapCount = 0
                            executeHardkeyAction(prefs.hardkeyVolUpDouble, KeyEvent.ACTION_UP)
                        } else {
                            volUpTapCount = 1
                            handler.removeCallbacks(volUpDoubleTapRunnable)
                            handler.postDelayed(volUpDoubleTapRunnable, prefs.doubleTapMs.toLong())
                        }
                    }
                }
            }
            return true
        }

        // =========================
        // VOLUME DOWN
        // =========================
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            when (action) {
                KeyEvent.ACTION_DOWN -> {
                    if (!isRightKeyHeld) {
                        isRightKeyHeld = true
                        volDownHoldTriggered = false
                        handler.postDelayed(volDownHoldRunnable, prefs.holdDurationMs.toLong())
                    }
                }
                KeyEvent.ACTION_UP -> {
                    isRightKeyHeld = false
                    handler.removeCallbacks(volDownHoldRunnable)
                    
                    if (volDownHoldTriggered) {
                        // Finish the Hold action
                        executeHardkeyAction(prefs.hardkeyVolDownHold, KeyEvent.ACTION_UP)
                    } else {
                        // Tap Logic
                        val timeSinceLastTap = System.currentTimeMillis() - lastVolDownTime
                        lastVolDownTime = System.currentTimeMillis()
                        
                        if (timeSinceLastTap < prefs.doubleTapMs && volDownTapCount == 1) {
                            handler.removeCallbacks(volDownDoubleTapRunnable)
                            volDownTapCount = 0
                            executeHardkeyAction(prefs.hardkeyVolDownDouble, KeyEvent.ACTION_UP)
                        } else {
                            volDownTapCount = 1
                            handler.removeCallbacks(volDownDoubleTapRunnable)
                            handler.postDelayed(volDownDoubleTapRunnable, prefs.doubleTapMs.toLong())
                        }
                    }
                }
            }
            return true
        }

        return super.onKeyEvent(event)
    }
```