# Code Update: Consolidate Click & Drag Logic

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadMenuManager.kt

### Description
Removes the redundant "Left Drag" and "Right Drag" options from the hardkey selection menu, as the standard click actions will now handle dragging natively.

### Code
Find the `hardkeyActions` list and remove the drag options.

```kotlin
    // =========================
    // HARDKEY ACTIONS LIST
    // =========================
    private val hardkeyActions = listOf(
        "none" to "None",
        "left_click" to "Left Click (Hold to Drag)",
        "right_click" to "Right Click (Hold to Drag)",
        "scroll_up" to "Scroll Up",
        "scroll_down" to "Scroll Down",
        "display_toggle" to "Display Toggle",
        "display_toggle_alt" to "Display (Alt Mode)",
        "display_toggle_std" to "Display (Std Mode)",
        "display_wake" to "Display Wake",
        "alt_position" to "Alt KB Position",
        "toggle_keyboard" to "Toggle Keyboard",
        "toggle_trackpad" to "Toggle Trackpad",
        "open_menu" to "Open Menu",
        "reset_cursor" to "Reset Cursor"
    )
```

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
### Description
Updates executeHardkeyAction to handle left_click and right_click as direct state pass-throughs.
 * ACTION_DOWN: Calls startKeyDrag (which injects ACTION_DOWN).
 * ACTION_UP: Calls stopKeyDrag (which injects ACTION_UP).
We also need to update onKeyEvent to pass the KeyEvent.ACTION_DOWN or UP state to executeHardkeyAction so it knows which phase of the click to perform.

### Code Update 1: Update onKeyEvent signatures
In OverlayService.kt, update onKeyEvent to pass the action state.

Replace the entire onKeyEvent method with this logic that checks if an action is a "Click" type and handles it instantly, otherwise falls back to the timer system.
```kotlin
    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (isPreviewMode || !isTrackpadVisible) return super.onKeyEvent(event)
        
        val action = event.action
        val keyCode = event.keyCode
        
        // Helper to check if an action is a direct mouse operation
        fun isDirectMouseAction(actionId: String): Boolean {
            return actionId == "left_click" || actionId == "right_click"
        }

        // =========================
        // VOLUME UP
        // =========================
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // If configured as a simple click/drag button, execute immediately
            // We use the "Hold" preference slot as the primary binding for single-button usage
            if (isDirectMouseAction(prefs.hardkeyVolUpHold)) {
                executeHardkeyAction(prefs.hardkeyVolUpHold, action)
                return true
            }
            
            // Standard Timer Logic for non-mouse actions (menus, toggles)
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
                    if (!volUpHoldTriggered) {
                        // Handle tap/double-tap logic here...
                        // (Use existing logic for taps)
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
             if (isDirectMouseAction(prefs.hardkeyVolDownHold)) {
                executeHardkeyAction(prefs.hardkeyVolDownHold, action)
                return true
            }
            
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
                    if (!volDownHoldTriggered) {
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

### Code Update 2: Update executeHardkeyAction signature and logic
Update the function signature to accept keyEventAction (defaulting to UP for legacy calls) and handle the direct injection.
```kotlin
    private fun executeHardkeyAction(actionId: String, keyEventAction: Int = KeyEvent.ACTION_UP) {
        when (actionId) {
            "none" -> { /* Do nothing */ }
            
            "left_click" -> {
                if (keyEventAction == KeyEvent.ACTION_DOWN) {
                    if (!volUpDragActive) { // Re-using state var to track physical press state
                        volUpDragActive = true
                        startKeyDrag(MotionEvent.BUTTON_PRIMARY)
                    }
                } else {
                    volUpDragActive = false
                    stopKeyDrag(MotionEvent.BUTTON_PRIMARY)
                }
            }
            
            "right_click" -> {
                 if (keyEventAction == KeyEvent.ACTION_DOWN) {
                    if (!volDownDragActive) {
                        volDownDragActive = true
                        startKeyDrag(MotionEvent.BUTTON_SECONDARY)
                    }
                } else {
                    volDownDragActive = false
                    stopKeyDrag(MotionEvent.BUTTON_SECONDARY)
                }
            }
            
            // ... (rest of the actions remain the same, just wrapped in 'if (keyEventAction == ACTION_UP)')
            "scroll_up" -> {
                if (keyEventAction == KeyEvent.ACTION_UP) {
                    val dist = BASE_SWIPE_DISTANCE * prefs.scrollSpeed
                    performSwipe(0f, -dist)
                }
            }
            // ... apply this pattern to all other momentary actions
```
