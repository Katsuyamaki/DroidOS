## Plan: Map keyboard hide button to open menu (like DroidOSPro)

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/OverlayService.kt
REASON: Expose a small helper so KeyboardOverlay can request opening the Trackpad menu without accessing private `menuManager` directly.
SEARCH_BLOCK:
```
    private fun toggleDrawer() {
```
REPLACE_BLOCK:
```
    fun openMenuFromKeyboard(): Boolean {
        return try {
            if (menuManager != null) {
                menuManager?.show()
                enforceZOrder()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun toggleDrawer() {
```
END_OF_UPDATE_BLOCK

FILE_UPDATE: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/KeyboardOverlay.kt
REASON: Change the ▼ hide key to open the menu when available, falling back to close/hide behavior (matches DroidOSPro UX).
SEARCH_BLOCK:
```
        if (key == KeyboardView.SpecialKey.HIDE_KEYBOARD) {
            onCloseAction() // Calls the close/hide action passed from Service
            return
        }
```
REPLACE_BLOCK:
```
        if (key == KeyboardView.SpecialKey.HIDE_KEYBOARD) {
            val service = context as? OverlayService
            val openedMenu = service?.openMenuFromKeyboard() == true
            if (!openedMenu) {
                onCloseAction() // Fallback: close/hide overlay
            }
            return
        }
```
END_OF_UPDATE_BLOCK

