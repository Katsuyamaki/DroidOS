
## [2026-01-30 17:14:42] UPDATE: OverlayService.kt
**Reason:** When spacebar mouse mode is active, prevent FORCE_HIDE from closing the overlay keyboard. Also re-send IME_VISIBILITY(true) to the launcher to counteract the DockIME's IME_VISIBILITY(false) that fired from onWindowHidden, keeping the auto-adjust margin in place.

### Search Block:
```
if (forceHide) {
                        // Force hide from Dock IME auto-sync
                        // Debounce: ignore FORCE_HIDE within 1s of FORCE_SHOW (IME flicker)
                        if (isCustomKeyboardVisible && System.currentTimeMillis() - lastForceShowTime > 1000) {
                            keyboardOverlay?.hide()
                            isCustomKeyboardVisible = false
                            // Save state so bubble tap can restore
                            pendingRestoreKeyboard = true
                            hasPendingRestore = true
                        }
```
### Replace Block:
```
if (forceHide) {
                        // Force hide from Dock IME auto-sync
                        // Debounce: ignore FORCE_HIDE within 1s of FORCE_SHOW (IME flicker)
                        val inSpacebarMouse = keyboardOverlay?.getKeyboardView()?.isInSpacebarMouseMode() == true
                        if (inSpacebarMouse) {
                            // Spacebar mouse mode is active — keep overlay alive.
                            // DockIME already sent IME_VISIBILITY(false) to launcher,
                            // so re-send true to preserve the auto-adjust margin.
                            val fixIntent = android.content.Intent("com.katsuyamaki.DroidOSLauncher.IME_VISIBILITY")
                            fixIntent.setPackage("com.katsuyamaki.DroidOSLauncher")
                            fixIntent.putExtra("VISIBLE", true)
                            sendBroadcast(fixIntent)
                            android.util.Log.d("OverlayService", "FORCE_HIDE blocked — spacebar mouse active")
                        } else if (isCustomKeyboardVisible && System.currentTimeMillis() - lastForceShowTime > 1000) {
                            keyboardOverlay?.hide()
                            isCustomKeyboardVisible = false
                            // Save state so bubble tap can restore
                            pendingRestoreKeyboard = true
                            hasPendingRestore = true
                        }
```

---
