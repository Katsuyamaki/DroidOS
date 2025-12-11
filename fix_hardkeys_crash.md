# Code Update: Fix Hardkeys Menu Crash (Replace Dialog with Submenu)

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/TrackpadMenuManager.kt

### Description
Replaces the `showActionPicker` method. Instead of crashing by trying to open an `AlertDialog` from a Service context, it now loads the options directly into the existing `RecyclerView`, creating a safe "sub-menu" experience.

### Code
Find the `showActionPicker` function (it should be near line 380) and replace it **entirely** with this implementation:

```kotlin
    // =========================
    // SHOW ACTION PICKER - In-Menu Replacement for Dialog
    // Replaces the current menu list with selection options to avoid Service/Dialog crashes
    // =========================
    private fun showActionPicker(prefKey: String, currentValue: String) {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        
        // 1. Header
        list.add(TrackpadMenuAdapter.MenuItem("Select Action", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // 2. Cancel / Back Option
        list.add(TrackpadMenuAdapter.MenuItem("<< Go Back", android.R.drawable.ic_menu_revert, TrackpadMenuAdapter.Type.ACTION) {
            // Reload the previous tab to "go back"
            loadTab(TAB_HARDKEYS)
        })

        // 3. Action Options
        for ((id, name) in hardkeyActions) {
            // Show a "Check" icon if this is the currently selected value
            // Otherwise show 0 (no icon) or a generic dot
            val iconRes = if (id == currentValue) android.R.drawable.checkbox_on_background else 0
            
            list.add(TrackpadMenuAdapter.MenuItem(name, iconRes, TrackpadMenuAdapter.Type.ACTION) {
                // On Click: Update Pref and Go Back
                service.updatePref(prefKey, id)
                loadTab(TAB_HARDKEYS)
            })
        }
        
        // 4. Update the View
        recyclerView?.adapter = TrackpadMenuAdapter(list)
    }
    // =========================
    // END SHOW ACTION PICKER
    // =========================
