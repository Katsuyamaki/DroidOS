package com.example.coverscreentester

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import android.view.ViewGroup
import java.util.ArrayList

class TrackpadMenuManager(
    private val context: Context,
    private val windowManager: WindowManager,
    private val service: OverlayService
) {
    private var drawerView: View? = null
    private var recyclerView: RecyclerView? = null
    private var drawerParams: WindowManager.LayoutParams? = null
    private var isVisible = false
    
    // Manual Adjust State
    private var isResizeMode = false // Default to Move Mode

    // Tab Constants - Order must match layout_trackpad_drawer.xml tab order
    private val TAB_MAIN = 0
    private val TAB_PRESETS = 1
    private val TAB_MOVE = 2
    private val TAB_KB_MOVE = 3
    private val TAB_CONFIG = 4
    private val TAB_TUNE = 5
    private val TAB_HARDKEYS = 6    // Hardkey bindings configuration
    private val TAB_BUBBLE = 7      // Bubble customization
    private val TAB_PROFILES = 8
    private val TAB_HELP = 9
    
    private var currentTab = TAB_MAIN

    fun show() {
        if (isVisible) return
        if (drawerView == null) setupDrawer()
        try {
            windowManager.addView(drawerView, drawerParams)
            isVisible = true
            loadTab(currentTab)
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun hide() {
        if (!isVisible) return
        try {
            windowManager.removeView(drawerView)
            isVisible = false
        } catch (e: Exception) { }
    }

    fun toggle() {
        if (isVisible) hide() else show()
    }

    fun isShowing(): Boolean = isVisible

    // Robust Force Re-Add for Z-order management
    fun bringToFront() {
        if (isVisible && drawerView != null) {
            try { windowManager.removeView(drawerView) } catch (e: Exception) {}
            try { windowManager.addView(drawerView, drawerParams) } catch (e: Exception) {}
        }
    }

    private fun setupDrawer() {
        val themedContext = android.view.ContextThemeWrapper(context, R.style.Theme_CoverScreenTester)
        val inflater = LayoutInflater.from(themedContext)
        drawerView = inflater.inflate(R.layout.layout_trackpad_drawer, null)

        drawerView?.findViewById<View>(R.id.btn_close_menu)?.setOnClickListener { hide() }
        
        recyclerView = drawerView?.findViewById(R.id.menu_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        // Updated Tab Mapping - Order matches layout_trackpad_drawer.xml
        val tabs = listOf(
            R.id.tab_main to TAB_MAIN,
            R.id.tab_presets to TAB_PRESETS,
            R.id.tab_move to TAB_MOVE,
            R.id.tab_kb_move to TAB_KB_MOVE,
            R.id.tab_config to TAB_CONFIG,
            R.id.tab_tune to TAB_TUNE,
            R.id.tab_hardkeys to TAB_HARDKEYS,  // Hardkey bindings
            R.id.tab_bubble to TAB_BUBBLE,
            R.id.tab_profiles to TAB_PROFILES,
            R.id.tab_help to TAB_HELP
        )

        for ((id, index) in tabs) {
            drawerView?.findViewById<ImageView>(id)?.setOnClickListener { 
                loadTab(index) 
            }
        }

        drawerParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, // Width: wrap card
            WindowManager.LayoutParams.WRAP_CONTENT, // Height: wrap card
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        drawerParams?.gravity = Gravity.CENTER

        // Simplified listener: Only needed to consume touches on the menu itself
        drawerView?.setOnTouchListener { _, _ ->
            true // Consume touch so it doesn't pass through the menu card
        }
    }

    private fun loadTab(index: Int) {
        currentTab = index
        updateTabIcons(index)
        
        val items = when(index) {
            TAB_MAIN -> getMainItems()
            TAB_PRESETS -> getPresetItems()
            TAB_MOVE -> getMoveItems(false)
            TAB_KB_MOVE -> getMoveItems(true)
            TAB_CONFIG -> getConfigItems()
            TAB_TUNE -> getTuneItems()
            TAB_HARDKEYS -> getHardkeyItems()   // Hardkey bindings configuration
            TAB_BUBBLE -> getBubbleItems()
            TAB_PROFILES -> getProfileItems()
            TAB_HELP -> getHelpItems()
            else -> emptyList()
        }
        
        recyclerView?.adapter = TrackpadMenuAdapter(items)
    }

    private fun updateTabIcons(activeIdx: Int) {
        val tabIds = listOf(R.id.tab_main, R.id.tab_presets, R.id.tab_move, R.id.tab_kb_move, R.id.tab_config, R.id.tab_tune, R.id.tab_hardkeys, R.id.tab_bubble, R.id.tab_profiles, R.id.tab_help)
        for ((i, id) in tabIds.withIndex()) {
            val view = drawerView?.findViewById<ImageView>(id)
            if (i == activeIdx) view?.setColorFilter(Color.parseColor("#3DDC84")) 
            else view?.setColorFilter(Color.GRAY)
        }
    }

    // =========================
    // GET MAIN ITEMS - Generates main menu items list
    // Includes Anchor toggle to lock trackpad/keyboard position
    // =========================
    private fun getMainItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("Shizuku Status", android.R.drawable.ic_dialog_info, TrackpadMenuAdapter.Type.INFO)) 
        
        list.add(TrackpadMenuAdapter.MenuItem("Reset Bubble Position", android.R.drawable.ic_menu_myplaces, TrackpadMenuAdapter.Type.ACTION) { 
            service.resetBubblePosition()
            hide()
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("Move Trackpad Here", R.drawable.ic_tab_move, TrackpadMenuAdapter.Type.ACTION) { service.forceMoveToCurrentDisplay(); hide() })
        list.add(TrackpadMenuAdapter.MenuItem("Target: ${if(service.inputTargetDisplayId == service.currentDisplayId) "Local" else "Remote"}", R.drawable.ic_cursor, TrackpadMenuAdapter.Type.ACTION) { service.cycleInputTarget(); loadTab(TAB_MAIN) })
        
        // --- ANCHOR TOGGLE: Locks trackpad and keyboard position/size ---
        list.add(TrackpadMenuAdapter.MenuItem("Anchor (Lock Position)", 
            if(p.prefAnchored) R.drawable.ic_lock_closed else R.drawable.ic_lock_open, 
            TrackpadMenuAdapter.Type.TOGGLE, 
            if(p.prefAnchored) 1 else 0) { v ->
            service.updatePref("anchored", v)
            loadTab(TAB_MAIN)  // Refresh to update icon
        })
        // --- END ANCHOR TOGGLE ---
        
        list.add(TrackpadMenuAdapter.MenuItem("Toggle Keyboard", R.drawable.ic_tab_keyboard, TrackpadMenuAdapter.Type.ACTION) { service.toggleCustomKeyboard() })
        list.add(TrackpadMenuAdapter.MenuItem("Reset Cursor", android.R.drawable.ic_menu_rotate, TrackpadMenuAdapter.Type.ACTION) { service.resetCursorCenter() })
        list.add(TrackpadMenuAdapter.MenuItem("Hide App", android.R.drawable.ic_menu_close_clear_cancel, TrackpadMenuAdapter.Type.ACTION) { service.hideApp() })
        list.add(TrackpadMenuAdapter.MenuItem("Force Kill Service", android.R.drawable.ic_delete, TrackpadMenuAdapter.Type.ACTION) { service.forceExit() })
        return list
    }
    // =========================
    // END GET MAIN ITEMS
    // =========================


    
// =========================
    // GET PRESET ITEMS - Layout presets for split screen modes
    // Freeform (type 0) loads saved profile, NOT a split preset
    // =========================
    private fun getPresetItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        list.add(TrackpadMenuAdapter.MenuItem("SPLIT SCREEN PRESETS", R.drawable.ic_tab_move, TrackpadMenuAdapter.Type.INFO))
        
        // Freeform FIRST - this loads the saved profile (not a split preset)
        list.add(TrackpadMenuAdapter.MenuItem("Freeform (Use Profile)", android.R.drawable.ic_menu_edit, TrackpadMenuAdapter.Type.ACTION) { 
            service.applyLayoutPreset(0)
            hide()
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("KB Top / TP Bottom", R.drawable.ic_tab_keyboard, TrackpadMenuAdapter.Type.ACTION) { 
            service.applyLayoutPreset(1)
            hide()
        })
        list.add(TrackpadMenuAdapter.MenuItem("TP Top / KB Bottom", R.drawable.ic_tab_move, TrackpadMenuAdapter.Type.ACTION) { 
            service.applyLayoutPreset(2)
            hide()
        })
        return list
    }
    // =========================
    // END GET PRESET ITEMS
    // =========================
    private fun getMoveItems(isKeyboard: Boolean): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val target = if (isKeyboard) "Keyboard" else "Trackpad"
        
        // 1. Mode Switcher (Toggle Item)
        val modeText = if (isResizeMode) "Resize (Size)" else "Position (Move)"
        val modeIcon = if (isResizeMode) android.R.drawable.ic_menu_crop else android.R.drawable.ic_menu_mylocation
        
        list.add(TrackpadMenuAdapter.MenuItem("Mode: $modeText", modeIcon, TrackpadMenuAdapter.Type.ACTION) {
            isResizeMode = !isResizeMode
            loadTab(currentTab) // Refresh UI to update text
        })
        
        // 2. The D-Pad
        val actionText = if (isResizeMode) "Resize" else "Move"
        list.add(TrackpadMenuAdapter.MenuItem("$target $actionText", R.drawable.ic_tab_move, TrackpadMenuAdapter.Type.DPAD) { cmd ->
            val step = 20
            val command = cmd as String
            
            // isResizeMode determines whether we move X/Y or change W/H
            when(command) {
                "UP" -> service.manualAdjust(isKeyboard, isResizeMode, 0, -step)
                "DOWN" -> service.manualAdjust(isKeyboard, isResizeMode, 0, step)
                "LEFT" -> service.manualAdjust(isKeyboard, isResizeMode, -step, 0)
                "RIGHT" -> service.manualAdjust(isKeyboard, isResizeMode, step, 0)
                "CENTER" -> service.resetTrackpadPosition()
            }
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("Rotate 90°", android.R.drawable.ic_menu_rotate, 
            TrackpadMenuAdapter.Type.ACTION) { service.performRotation() })
            
        if (isKeyboard) {
            val p = service.prefs
            list.add(TrackpadMenuAdapter.MenuItem("Keyboard Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefKeyboardAlpha) { v ->
                service.updatePref("keyboard_alpha", v)
            })
        }
            
        return list
    }

    // =========================
    // GET CONFIG ITEMS - Trackpad configuration settings
    // Keyboard Opacity moved to getTuneItems()
    // ========================= 

    private fun getConfigItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("Cursor Speed", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.SLIDER, (p.cursorSpeed * 10).toInt()) { v -> service.updatePref("cursor_speed", (v as Int) / 10f) })
        list.add(TrackpadMenuAdapter.MenuItem("Scroll Speed", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.SLIDER, (p.scrollSpeed * 10).toInt()) { v -> service.updatePref("scroll_speed", (v as Int) / 10f) })
        
        // Max 255 for Alpha
        list.add(TrackpadMenuAdapter.MenuItem("Trackpad Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefAlpha, 255) { v -> service.updatePref("alpha", v) })
        
        list.add(TrackpadMenuAdapter.MenuItem("Handle Size", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefHandleSize / 2) { v -> service.updatePref("handle_size", v) })        
        
        // Max 200 for Scroll Width
        list.add(TrackpadMenuAdapter.MenuItem("Scroll Bar Width", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefScrollTouchSize, 200) { v -> service.updatePref("scroll_size", v) })
        
        list.add(TrackpadMenuAdapter.MenuItem("Cursor Size", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefCursorSize) { v -> service.updatePref("cursor_size", v) })
        list.add(TrackpadMenuAdapter.MenuItem("Reverse Scroll", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefReverseScroll) 1 else 0) { v -> service.updatePref("reverse_scroll", v) })
        list.add(TrackpadMenuAdapter.MenuItem("Tap to Scroll", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefTapScroll) 1 else 0) { v -> service.updatePref("tap_scroll", v) })
        list.add(TrackpadMenuAdapter.MenuItem("Haptic Feedback", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefVibrate) 1 else 0) { v -> service.updatePref("vibrate", v) })
        return list
    }
    // =========================
    // END GET CONFIG ITEMS
    // =========================

    // =========================
    // GET TUNE ITEMS - Keyboard configuration settings
    // Contains Keyboard Opacity, Scale, and Auto Display Off
    // =========================
    private fun getTuneItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        // Max 255 for Alpha
        list.add(TrackpadMenuAdapter.MenuItem("Keyboard Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefKeyboardAlpha, 255) { v -> service.updatePref("keyboard_alpha", v) })
        // Max 200 for Scale (200%)
        list.add(TrackpadMenuAdapter.MenuItem("Keyboard Scale", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefKeyScale, 200) { v -> service.updatePref("keyboard_key_scale", v) })
        list.add(TrackpadMenuAdapter.MenuItem("Auto Display Off", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefAutomationEnabled) 1 else 0) { v -> service.updatePref("automation_enabled", v) })
        return list
    }
    // =========================
    // END GET TUNE ITEMS
    // =========================

    // =========================
    // HARDKEY ACTIONS LIST
    // =========================
    private val hardkeyActions = listOf(
        "none" to "None",
        "left_click" to "Left Click (Hold to Drag)",
        "right_click" to "Right Click (Hold to Drag)",
        "scroll_up" to "Scroll Up",
        "scroll_down" to "Scroll Down",
        "display_toggle_alt" to "Display (Alt Mode)",
        "display_toggle_std" to "Display (Std Mode)",
        "display_wake" to "Display Wake",
        "alt_position" to "Alt KB Position",
        "toggle_keyboard" to "Toggle Keyboard",
        "toggle_trackpad" to "Toggle Trackpad",
        "open_menu" to "Open Menu",
        "reset_cursor" to "Reset Cursor"
    )
    
    private fun getActionDisplayName(actionId: String): String {
        return hardkeyActions.find { it.first == actionId }?.second ?: actionId
    }
    // =========================
    // END HARDKEY ACTIONS LIST
    // =========================

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

    // =========================
    // GET HARDKEY ITEMS - Hardkey bindings configuration menu
    // Allows users to customize Vol Up/Down and Power button actions
    // =========================
    private fun getHardkeyItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        // === VOLUME UP SECTION ===
        list.add(TrackpadMenuAdapter.MenuItem("VOLUME UP", 0, TrackpadMenuAdapter.Type.HEADER))
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Tap: ${getActionDisplayName(p.hardkeyVolUpTap)}",
            android.R.drawable.ic_media_play,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_up_tap", p.hardkeyVolUpTap) })
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Double-Tap: ${getActionDisplayName(p.hardkeyVolUpDouble)}",
            android.R.drawable.ic_media_ff,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_up_double", p.hardkeyVolUpDouble) })
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Hold: ${getActionDisplayName(p.hardkeyVolUpHold)}",
            android.R.drawable.ic_menu_crop,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_up_hold", p.hardkeyVolUpHold) })
        
        // === VOLUME DOWN SECTION ===
        list.add(TrackpadMenuAdapter.MenuItem("VOLUME DOWN", 0, TrackpadMenuAdapter.Type.HEADER))
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Tap: ${getActionDisplayName(p.hardkeyVolDownTap)}",
            android.R.drawable.ic_media_play,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_down_tap", p.hardkeyVolDownTap) })
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Double-Tap: ${getActionDisplayName(p.hardkeyVolDownDouble)}",
            android.R.drawable.ic_media_ff,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_down_double", p.hardkeyVolDownDouble) })
        
        list.add(TrackpadMenuAdapter.MenuItem(
            "Hold: ${getActionDisplayName(p.hardkeyVolDownHold)}",
            android.R.drawable.ic_menu_crop,
            TrackpadMenuAdapter.Type.ACTION
        ) { showActionPicker("hardkey_vol_down_hold", p.hardkeyVolDownHold) })
        
        // === TIMING SECTION ===
        list.add(TrackpadMenuAdapter.MenuItem("TIMING", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // Max 500ms
        // Removed loadTab() call to prevent menu closing/refreshing while dragging
        list.add(TrackpadMenuAdapter.MenuItem(
            "Double-Tap Speed (ms)",
            android.R.drawable.ic_menu_recent_history,
            TrackpadMenuAdapter.Type.SLIDER,
            p.doubleTapMs,
            500 
        ) { v ->
            service.updatePref("double_tap_ms", v)
        })
        
        // Max 800ms
        // Removed loadTab() call to prevent menu closing/refreshing while dragging
        list.add(TrackpadMenuAdapter.MenuItem(
            "Hold Duration (ms)",
            android.R.drawable.ic_menu_recent_history,
            TrackpadMenuAdapter.Type.SLIDER,
            p.holdDurationMs,
            800 
        ) { v ->
            service.updatePref("hold_duration_ms", v)
        })
        
        return list
    }
    // =========================
    // END GET HARDKEY ITEMS
    // =========================


    // =========================
    // GET BUBBLE ITEMS - Bubble launcher customization
    // Size slider, Icon cycle, Opacity slider
    // =========================
    private fun getBubbleItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        // Info header
        list.add(TrackpadMenuAdapter.MenuItem("BUBBLE CUSTOMIZATION", android.R.drawable.ic_menu_info_details, TrackpadMenuAdapter.Type.INFO))
        
        // Size slider: 50-200 (50=half, 100=standard, 200=double)
        // Max 200
        list.add(TrackpadMenuAdapter.MenuItem("Bubble Size", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefBubbleSize, 200) { v ->
            service.updatePref("bubble_size", v)
        })
        
        // Icon cycle action
        val iconNames = arrayOf("Trackpad", "Cursor", "Main", "Keyboard", "Compass", "Location")
        val currentIconName = iconNames.getOrElse(p.prefBubbleIconIndex) { "Default" }
        list.add(TrackpadMenuAdapter.MenuItem("Icon: $currentIconName", android.R.drawable.ic_menu_gallery, TrackpadMenuAdapter.Type.ACTION) { 
            service.updatePref("bubble_icon", true)
            loadTab(TAB_BUBBLE) // Refresh to show new icon name
        })
        
        // Opacity slider: 50-255
        // Max 255
        list.add(TrackpadMenuAdapter.MenuItem("Bubble Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefBubbleAlpha, 255) { v ->
            service.updatePref("bubble_alpha", v)
        })
        
        // Reset button
        list.add(TrackpadMenuAdapter.MenuItem("Reset Bubble Position", android.R.drawable.ic_menu_revert, TrackpadMenuAdapter.Type.ACTION) { 
            service.resetBubblePosition()
        })
        
        return list
    }
    // =========================
    // END GET BUBBLE ITEMS
    // =========================

    private fun getProfileItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        list.add(TrackpadMenuAdapter.MenuItem("Current Screen: ${service.getProfileKey()}", R.drawable.ic_tab_profiles, TrackpadMenuAdapter.Type.INFO))
        list.add(TrackpadMenuAdapter.MenuItem("Save Layout", android.R.drawable.ic_menu_save, TrackpadMenuAdapter.Type.ACTION) { service.saveLayout() })
        list.add(TrackpadMenuAdapter.MenuItem("Delete Profile", android.R.drawable.ic_menu_delete, TrackpadMenuAdapter.Type.ACTION) { service.deleteCurrentProfile() })
        return list
    }

    private fun getHelpItems(): List<TrackpadMenuAdapter.MenuItem> {
        return listOf(TrackpadMenuAdapter.MenuItem(
            "TRACKPAD: Tap=Click, Touch+Move=Cursor\n\n" +
            "HARDKEYS: Configure in Hardkeys tab\n" +
            "• Tap/Double-Tap/Hold actions\n" +
            "• Vol Up, Vol Down, Power\n\n" +
            "KEYBOARD: Drag Top=Move, BottomRight=Resize",
            0, TrackpadMenuAdapter.Type.INFO))
    }
}
