package com.katsuyamaki.DroidOSTrackpadKeyboard

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
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup

import android.view.MotionEvent
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
    private val TAB_MIRROR = 4      // NEW: Mirror keyboard config
    private val TAB_CONFIG = 5
    private val TAB_TUNE = 6
    private val TAB_HARDKEYS = 7
    private val TAB_BUBBLE = 8
    private val TAB_PROFILES = 9
    private val TAB_HELP = 10
    
    private var currentTab = TAB_MAIN
    
    // [NEW] Debug Trigger Vars
    private var helpClickCount = 0
    private var lastHelpClickTime = 0L

    fun show() {
        if (isVisible) return
        if (drawerView == null) setupDrawer()
        try {
            windowManager.addView(drawerView, drawerParams)
            isVisible = true
            loadTab(currentTab)
            
            // CRITICAL FIX: Force Cursor and Bubble to top of stack
            // Since Menu was just added, it is currently on top. 
            // We must re-add the others to cover it.
            service.enforceZOrder()
            
        } catch (e: SecurityException) {
            android.widget.Toast.makeText(context, "Missing Overlay Permission! Open App to Fix.", android.widget.Toast.LENGTH_LONG).show()

        } catch (e: Exception) { 
 
        }
    }

    fun hide() {
        if (!isVisible) return
        try {
            windowManager.removeView(drawerView)
            isVisible = false
        } catch (e: Exception) { }
    }


    // [FIXED] Refresh the current tab to update UI text (e.g. "Restore Target: Gboard")
    fun refresh() {
        if (drawerView != null) {
            // Reload the currently open tab to refresh item text
            loadTab(currentTab)
        }
    }

    fun toggle() {
        if (isVisible) hide() else show()
    }
    
    // NEW: Helper to receive forwarded touches from overlay
    fun dispatchTouchToView(event: MotionEvent): Boolean {
        if (!isVisible || drawerView == null) return false
        
        val view = drawerView!!
        val loc = IntArray(2)
        view.getLocationOnScreen(loc)
        val x = loc[0].toFloat()
        val y = loc[1].toFloat()
        
        // Check bounds
        if (event.rawX >= x && event.rawX <= x + view.width && 
            event.rawY >= y && event.rawY <= y + view.height) {
            
            event.offsetLocation(-x, -y)
            val handled = view.dispatchTouchEvent(event)
            event.offsetLocation(x, y) // Restore for caller
            return handled
        }
        return false
    }

    fun bringToFront() {
        if (!isVisible || drawerView == null) return
        try {
            // Detach and Re-attach to move to top of Z-Order stack
            windowManager.removeView(drawerView)
            windowManager.addView(drawerView, drawerParams)
        } catch (e: Exception) {

        }
    }

    private fun setupDrawer() {
        // Use ContextWrapper to ensure correct theme (Matches Launcher)
        val themedContext = android.view.ContextThemeWrapper(context, R.style.Theme_CoverScreenTester)
        val inflater = LayoutInflater.from(themedContext)
        drawerView = inflater.inflate(R.layout.layout_trackpad_drawer, null)

        // FUNCTION: Force Tab Icons
        // Manually set the Mirror Tab icon to the new custom asset
        drawerView?.findViewById<ImageView>(R.id.tab_mirror)?.setImageResource(R.drawable.mirrorkb)

        // Close button logic
        drawerView?.findViewById<View>(R.id.btn_close_menu)?.setOnClickListener { hide() }
        
        recyclerView = drawerView?.findViewById(R.id.menu_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        // Tab click listeners
        val tabs = listOf(
            R.id.tab_main to TAB_MAIN,
            R.id.tab_presets to TAB_PRESETS,
            R.id.tab_move to TAB_MOVE,
            R.id.tab_kb_move to TAB_KB_MOVE,
            R.id.tab_mirror to TAB_MIRROR,      // NEW
            R.id.tab_config to TAB_CONFIG,
            R.id.tab_tune to TAB_TUNE,
            R.id.tab_hardkeys to TAB_HARDKEYS,
            R.id.tab_bubble to TAB_BUBBLE,
            R.id.tab_profiles to TAB_PROFILES,
            R.id.tab_help to TAB_HELP
        )

        for ((id, index) in tabs) {
            drawerView?.findViewById<ImageView>(id)?.setOnClickListener { 
                // [FIX] INSTRUCTIONS TAB - DEBUG TRIGGER (5 Clicks)
                if (id == R.id.tab_help) {
                    val now = System.currentTimeMillis()
                    if (now - lastHelpClickTime < 500) {
                        helpClickCount++
                    } else {
                        helpClickCount = 1
                    }
                    lastHelpClickTime = now

                    if (helpClickCount >= 5) {
                        // Toggle debug mode in the main Service
                        service.toggleDebugMode()
                        helpClickCount = 0
                    }
                }
                
                loadTab(index) 
            }
        }

        // =========================
        // WINDOW CONFIG (MATCHING DROIDOS LAUNCHER)
        // =========================
        drawerParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, 
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or 
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, 
            PixelFormat.TRANSLUCENT
        )
        // Explicitly set Gravity to TOP|START for absolute positioning
        drawerParams?.gravity = Gravity.TOP or Gravity.START
        
        // Initial Center Calculation
        val metrics = context.resources.displayMetrics
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels
        
        // Approx Menu Size (320dp width + margins, ~400dp height)
        val density = metrics.density
        val menuW = (450 * density).toInt() 
        val menuH = (580 * density).toInt()
        
        drawerParams?.x = (screenWidth - menuW) / 2
        drawerParams?.y = (screenHeight - menuH) / 2
        
        // =========================
        // INTERACTION LOGIC
        // =========================
        // 1. Background Click -> Removed (Handled by FLAG_NOT_TOUCH_MODAL)
        
        // 2. Menu Card Click -> Block (Consume)
        drawerView?.findViewById<View>(R.id.menu_container)?.setOnClickListener { 
            // Do nothing
        }
        
        // 3. DRAG HANDLE LOGIC
        val dragHandle = drawerView?.findViewById<View>(R.id.menu_drag_handle)
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        dragHandle?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = drawerParams!!.x
                    initialY = drawerParams!!.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    drawerParams!!.x = initialX + (event.rawX - initialTouchX).toInt()
                    drawerParams!!.y = initialY + (event.rawY - initialTouchY).toInt()
                    
                    try {
                        windowManager.updateViewLayout(drawerView, drawerParams)
                    } catch (e: Exception) {}
                    true
                }
                else -> false
            }
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
            TAB_MIRROR -> getMirrorItems()      // NEW
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
        val tabIds = listOf(R.id.tab_main, R.id.tab_presets, R.id.tab_move, R.id.tab_kb_move, R.id.tab_mirror, R.id.tab_config, R.id.tab_tune, R.id.tab_hardkeys, R.id.tab_bubble, R.id.tab_profiles, R.id.tab_help)
        for ((i, id) in tabIds.withIndex()) {
            val view = drawerView?.findViewById<ImageView>(id)
            if (i == activeIdx) view?.setColorFilter(Color.parseColor("#3DDC84")) 
            else view?.setColorFilter(Color.GRAY)
        }
    }

    // =========================
    // GET MAIN ITEMS - Generates main menu items list
    // =========================
    private fun getMainItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("MAIN CONTROLS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // --- COMMENTED OUT PER REQUEST ---
        /*
        list.add(TrackpadMenuAdapter.MenuItem("Switch Screen (0 <-> 1)", android.R.drawable.ic_menu_rotate, TrackpadMenuAdapter.Type.ACTION) { 
            service.switchDisplay() 
            hide()
        })
        */
        // ---------------------------------

        // --- ANCHOR TOGGLE: Locks trackpad and keyboard position/size ---


        list.add(TrackpadMenuAdapter.MenuItem("Anchor (Lock Position)", 
            if(p.prefAnchored) R.drawable.ic_lock_closed else R.drawable.ic_lock_open, 
            TrackpadMenuAdapter.Type.TOGGLE, 
            if(p.prefAnchored) 1 else 0) { v ->
            service.updatePref("anchored", v)
            loadTab(TAB_MAIN)  // Refresh to update icon
        })
        // --- END ANCHOR TOGGLE ---

        // NEW: Launch Proxy Activity for Picker (moved from Keyboard Settings tab)
        // Always opens our custom KeyboardPickerActivity which handles showing
        // DroidOS greyed out with a warning if it's not enabled yet
        list.add(TrackpadMenuAdapter.MenuItem("Keyboard Picker\nSelect 'DroidOS Keyboard Toolbar'\nFor More Features", android.R.drawable.ic_menu_agenda, TrackpadMenuAdapter.Type.ACTION) {
            hide() // Close menu

            service.forceSystemKeyboardVisible()

            // [FIX] Hide overlay keyboard so it doesn't cover the picker
            val hideKbIntent = android.content.Intent("TOGGLE_CUSTOM_KEYBOARD")
            hideKbIntent.setPackage(context.packageName)
            hideKbIntent.putExtra("FORCE_HIDE", true)
            context.sendBroadcast(hideKbIntent)

            try {
                val intent = android.content.Intent(context, KeyboardPickerActivity::class.java)
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch(e: Exception) {
                android.widget.Toast.makeText(context, "Error launching picker", android.widget.Toast.LENGTH_SHORT).show()
            }
        })

        // Dock KB to Bottom — mirrors the dock_mode setting from DockIME popup.
        // Allows controlling dock mode on cover screen where One UI hides the DockIME toolbar.
        val dockPrefs = context.getSharedPreferences("DockIMEPrefs", android.content.Context.MODE_PRIVATE)
        val isDockMode = dockPrefs.getBoolean("dock_mode_d${service.currentDisplayId}", dockPrefs.getBoolean("dock_mode", false))
        list.add(TrackpadMenuAdapter.MenuItem("Dock KB to Bottom", R.drawable.ic_dock_keyboard, TrackpadMenuAdapter.Type.TOGGLE,
            if(isDockMode) 1 else 0) { v ->
            service.updatePref("dock_to_bottom", v)
            loadTab(TAB_MAIN) // Refresh to update toggle state
        })

        // Subheading for bubble tap behavior
        list.add(TrackpadMenuAdapter.MenuItem("WHEN BUBBLE ICON TAPPED:", 0, TrackpadMenuAdapter.Type.SUBHEADER))

        // Toggle Trackpad — controls whether bubble hides/shows trackpad
        list.add(TrackpadMenuAdapter.MenuItem("  Toggle Trackpad", R.drawable.ic_cursor, TrackpadMenuAdapter.Type.TOGGLE,
            if(p.prefBubbleIncludeTrackpad) 1 else 0) { v ->
            service.updatePref("bubble_include_trackpad", v)
        })

        // Toggle Keyboard — controls whether bubble hides/shows keyboard
        list.add(TrackpadMenuAdapter.MenuItem("  Toggle Keyboard", R.drawable.ic_tab_keyboard, TrackpadMenuAdapter.Type.TOGGLE,
            if(p.prefBubbleIncludeKeyboard) 1 else 0) { v ->
            service.updatePref("bubble_include_keyboard", v)
        })

        // Virtual Display Control subheading
        list.add(TrackpadMenuAdapter.MenuItem("VIRTUAL DISPLAY CONTROL:", 0, TrackpadMenuAdapter.Type.SUBHEADER))

        // Renamed: "Toggle Remote Display" -> "Move Cursor to Next Display"
        list.add(TrackpadMenuAdapter.MenuItem("  Move Cursor to Next Display", R.drawable.ic_cursor, TrackpadMenuAdapter.Type.ACTION) { service.cycleInputTarget(); loadTab(TAB_MAIN)         })

        // FUNCTION: Virtual Mirror Toggle
        list.add(TrackpadMenuAdapter.MenuItem(
            "  Virtual Mirror Mode",
            R.drawable.mirrorkb,
            TrackpadMenuAdapter.Type.TOGGLE,
            if(p.prefVirtualMirrorMode) 1 else 0
        ) { _ ->
            service.toggleVirtualMirrorMode()
            hide()  // Close menu since display context may change
        })

        // Reset / Restart subheading
        list.add(TrackpadMenuAdapter.MenuItem("RESET / RESTART:", 0, TrackpadMenuAdapter.Type.SUBHEADER))

        list.add(TrackpadMenuAdapter.MenuItem("Reset Bubble Position", android.R.drawable.ic_menu_myplaces, TrackpadMenuAdapter.Type.ACTION) { 
            service.resetBubblePosition()
            hide()
        })

        list.add(TrackpadMenuAdapter.MenuItem("Recenter Cursor", android.R.drawable.ic_menu_rotate, TrackpadMenuAdapter.Type.ACTION) { service.resetCursorCenter() })

        // Moved from Bubble Customization tab
        list.add(TrackpadMenuAdapter.MenuItem("Auto Restart on Close", 
            android.R.drawable.ic_menu_manage, 
            TrackpadMenuAdapter.Type.TOGGLE, 
            if(p.prefPersistentService) 1 else 0) { v ->
            service.updatePref("persistent_service", v)
            loadTab(TAB_MAIN)
        })

        list.add(TrackpadMenuAdapter.MenuItem("Close/Restart App", android.R.drawable.ic_delete, TrackpadMenuAdapter.Type.ACTION) { service.forceExit() })

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
        list.add(TrackpadMenuAdapter.MenuItem("SPLIT SCREEN PRESETS", 0, TrackpadMenuAdapter.Type.HEADER))
        
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
        
        list.add(TrackpadMenuAdapter.MenuItem(if (isKeyboard) "KEYBOARD POSITION" else "TRACKPAD POSITION", 0, TrackpadMenuAdapter.Type.HEADER))
        
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
                "CENTER" -> {
                    if (isKeyboard) service.resetKeyboardPosition() else service.resetTrackpadPosition()
                }
            }
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("Rotate 90°", android.R.drawable.ic_menu_rotate, TrackpadMenuAdapter.Type.ACTION) {
            if (isKeyboard) service.rotateKeyboard() else service.performRotation()
        })
            
        if (isKeyboard) {
            val p = service.prefs
            list.add(TrackpadMenuAdapter.MenuItem("Keyboard Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefKeyboardAlpha, 255) { v ->
                service.updatePref("keyboard_alpha", v)
            })
        }
            
        return list
    }

    // =========================
    // GET MIRROR ITEMS - Mirror keyboard configuration
    // =========================
    private var isMirrorResizeMode = false
    

    private fun getMirrorItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("MIRROR KEYBOARD", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // [REMOVED] Virtual Mirror Mode Toggle (Redundant)
        
        list.add(TrackpadMenuAdapter.MenuItem("POSITION & SIZE", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        // Mode Switcher (Position vs Resize)
        val modeText = if (isMirrorResizeMode) "Resize (Size)" else "Position (Move)"
        val modeIcon = if (isMirrorResizeMode) android.R.drawable.ic_menu_crop else android.R.drawable.ic_menu_mylocation
        
        list.add(TrackpadMenuAdapter.MenuItem("Mode: $modeText", modeIcon, TrackpadMenuAdapter.Type.ACTION) {
            isMirrorResizeMode = !isMirrorResizeMode
            loadTab(currentTab) // Refresh UI
        })
        
        // D-Pad for position/size
        val actionText = if (isMirrorResizeMode) "Resize" else "Move"
        list.add(TrackpadMenuAdapter.MenuItem("Mirror $actionText", R.drawable.ic_tab_move, TrackpadMenuAdapter.Type.DPAD) { cmd ->
            val step = 20
            val command = cmd as String
            


// =================================================================================
            // MIRROR KEYBOARD DPAD CONTROLS  
            // MOVE MODE: UP = move up, DOWN = move down (intuitive)
            // RESIZE MODE: UP = grow taller, DOWN = shrink shorter (like dragging bottom edge)
            // =================================================================================
            when(command) {
                "UP" -> {
                    // Both modes: -step (move up / shrink)

                    service.adjustMirrorKeyboard(isMirrorResizeMode, 0, -step)
                }
                "DOWN" -> {
                    // Both modes: +step (move down / grow)

                    service.adjustMirrorKeyboard(isMirrorResizeMode, 0, step)
                }
                "LEFT" -> {

                    service.adjustMirrorKeyboard(isMirrorResizeMode, -step, 0)
                }
                "RIGHT" -> {

                    service.adjustMirrorKeyboard(isMirrorResizeMode, step, 0)
                }
                "CENTER" -> service.resetMirrorKeyboardPosition()
            }
            // =================================================================================
            // END BLOCK: MIRROR KEYBOARD DPAD CONTROLS
            // =================================================================================


        })
        
        list.add(TrackpadMenuAdapter.MenuItem("APPEARANCE", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        // Mirror Keyboard Opacity Slider
        list.add(TrackpadMenuAdapter.MenuItem("Mirror Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefMirrorAlpha, 255) { v ->
            service.updatePref("mirror_alpha", v)
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("TIMING", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        // Orange Trail Delay Slider (100ms - 3000ms, show as 0.1s - 3.0s)
        val currentDelayMs = p.prefMirrorOrientDelayMs
        list.add(TrackpadMenuAdapter.MenuItem("Orient Delay: ${currentDelayMs}ms", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, (currentDelayMs / 100).toInt(), 30) { v ->
            val newDelayMs = (v as Int) * 100L
            service.updatePref("mirror_orient_delay", newDelayMs)
            loadTab(currentTab) // Refresh to show new value
        })
        
        return list
    }

    // =========================
    // END GET MIRROR ITEMS
    // =========================

    // =========================
    // GET CONFIG ITEMS - Trackpad configuration settings
    // FIXED: Tap to Scroll Boolean Logic
    // ========================= 
    private fun getConfigItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("TRACKPAD SETTINGS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        list.add(TrackpadMenuAdapter.MenuItem("SENSITIVITY", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        list.add(TrackpadMenuAdapter.MenuItem("Cursor Speed", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.SLIDER, (p.cursorSpeed * 10).toInt()) { v -> service.updatePref("cursor_speed", (v as Int) / 10f) })
        list.add(TrackpadMenuAdapter.MenuItem("Scroll Speed", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.SLIDER, (p.scrollSpeed * 10).toInt(), 50) { v -> service.updatePref("scroll_speed", (v as Int) / 10f) })
        
        list.add(TrackpadMenuAdapter.MenuItem("APPEARANCE", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        list.add(TrackpadMenuAdapter.MenuItem("Border Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefAlpha, 255) { v -> service.updatePref("alpha", v) })
        list.add(TrackpadMenuAdapter.MenuItem("Background Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefBgAlpha, 255) { v -> service.updatePref("bg_alpha", v) })
        list.add(TrackpadMenuAdapter.MenuItem("Handle Size", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefHandleSize / 2) { v -> service.updatePref("handle_size", v) })        
        list.add(TrackpadMenuAdapter.MenuItem("Scroll Bar Width", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefScrollTouchSize, 200) { v -> service.updatePref("scroll_size", v) })
        list.add(TrackpadMenuAdapter.MenuItem("Cursor Size", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefCursorSize) { v -> service.updatePref("cursor_size", v) })
        
        list.add(TrackpadMenuAdapter.MenuItem("BEHAVIOR", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        list.add(TrackpadMenuAdapter.MenuItem("Reverse Scroll", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefReverseScroll) 1 else 0) { v -> service.updatePref("reverse_scroll", v) })
        
        // MODIFIED: Correct Boolean Check for Toast
        list.add(TrackpadMenuAdapter.MenuItem("Tap to Scroll", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefTapScroll) 1 else 0) { v -> 
            service.updatePref("tap_scroll", v)
            // Fix: Cast strictly to Boolean or check against false directly
            if (v == false) {
                android.widget.Toast.makeText(context, "Beta mouse scrolling is activated - warning - scroll slowly for optimal results", android.widget.Toast.LENGTH_LONG).show()
            }
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("Haptic Feedback", R.drawable.ic_tab_settings, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefVibrate) 1 else 0) { v -> service.updatePref("vibrate", v) })
        return list
    }
    // =========================
    // END GET CONFIG ITEMS
    // =========================

    // =========================
    // GET TUNE ITEMS - Keyboard configuration settings
    // =========================
    private fun getTuneItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        val p = service.prefs
        
        list.add(TrackpadMenuAdapter.MenuItem("KEYBOARD SETTINGS", 0, TrackpadMenuAdapter.Type.HEADER))
        

        list.add(TrackpadMenuAdapter.MenuItem("Keyboard Opacity", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.SLIDER, p.prefKeyboardAlpha, 255) { v -> service.updatePref("keyboard_alpha", v) })
                list.add(TrackpadMenuAdapter.MenuItem("Auto Display Off", R.drawable.ic_tab_tune, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefAutomationEnabled) 1 else 0) {
                    v -> service.updatePref("automation_enabled", v as Boolean)
                })

        // MOVED & RENAMED: Cover Screen KB Blocker
        list.add(TrackpadMenuAdapter.MenuItem("Cover Screen KB blocker (restart app after reverting)", android.R.drawable.ic_lock_lock, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefBlockSoftKeyboard) 1 else 0) {
            v -> service.updatePref("block_soft_kb", v as Boolean)
        })

        // =================================================================================
        // SPACEBAR MOUSE EXTENDED MODE TOGGLE
        // SUMMARY: When enabled, spacebar mouse mode stays active indefinitely. Mode only
        //          deactivates when tapping outside the keyboard overlay area. This allows
        //          continuous cursor control without the normal 1-second timeout.
        // =================================================================================
        list.add(TrackpadMenuAdapter.MenuItem("Spacebar Mouse Extended Mode", android.R.drawable.ic_menu_compass, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefSpacebarMouseExtended) 1 else 0) {
            v -> service.updatePref("spacebar_mouse_extended", v as Boolean)
        })

        // =================================================================================
        // END BLOCK: SPACEBAR MOUSE EXTENDED MODE TOGGLE
        // =================================================================================

        // Override System Shortcuts toggle
        list.add(TrackpadMenuAdapter.MenuItem("Override System Shortcuts", android.R.drawable.ic_menu_close_clear_cancel, TrackpadMenuAdapter.Type.TOGGLE, if(p.prefOverrideSystemShortcuts) 1 else 0) {
            v -> service.updatePref("override_system_shortcuts", v as Boolean)
        })

        // PREDICTION AGGRESSION SLIDER: Removed. Prediction now uses cached preview
        // results which are consistently accurate. The preference field is kept in
        // OverlayService for backwards compatibility with settings serialization.

        return list
    }
    // =========================
    // END GET TUNE ITEMS
    // =========================

    // =========================
    // HARDKEY ACTIONS LIST
    // =========================
    private val hardkeyActions = listOf(
        "none" to "None (System Default)",
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
        "reset_cursor" to "Reset Cursor",
        "toggle_bubble" to "Launcher Bubble", // <--- NEW ITEM
        "action_back" to "Back",
        "action_home" to "Home",
        "action_forward" to "Forward (Browser)",
        "action_vol_up" to "Volume Up",
        "action_vol_down" to "Volume Down"
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
        
        // NEW MAIN HEADER
        list.add(TrackpadMenuAdapter.MenuItem("KEYBINDS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // Subheader
        list.add(TrackpadMenuAdapter.MenuItem("VOLUME UP", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
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
        
        // Subheader
        list.add(TrackpadMenuAdapter.MenuItem("VOLUME DOWN", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
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
        
        // Subheader
        list.add(TrackpadMenuAdapter.MenuItem("TIMING", 0, TrackpadMenuAdapter.Type.SUBHEADER))
        
        // Max 500ms
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
        
        list.add(TrackpadMenuAdapter.MenuItem("BUBBLE CUSTOMIZATION", 0, TrackpadMenuAdapter.Type.HEADER))
        
        // Size slider: 50-200 (50=half, 100=standard, 200=double)
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
        val currentRes = "${service.currentDisplayId}: ${service.getProfileKey().replace("P_", "").replace("_", " x ")}"
        
        list.add(TrackpadMenuAdapter.MenuItem("LAYOUT PROFILES", 0, TrackpadMenuAdapter.Type.HEADER))
        
        list.add(TrackpadMenuAdapter.MenuItem("Current: $currentRes", R.drawable.ic_tab_profiles, TrackpadMenuAdapter.Type.INFO))
        
        // CHANGED TITLE
        list.add(TrackpadMenuAdapter.MenuItem("Save Layout and Presets", android.R.drawable.ic_menu_save, TrackpadMenuAdapter.Type.ACTION) { 
            service.saveLayout()
            drawerView?.postDelayed({ loadTab(TAB_PROFILES) }, 200)
        })

        // NEW: Reload Profile
        list.add(TrackpadMenuAdapter.MenuItem("Reload Profile", android.R.drawable.ic_popup_sync, TrackpadMenuAdapter.Type.ACTION) { 
            service.loadLayout() // Reloads based on current resolution/display
            drawerView?.postDelayed({ loadTab(TAB_PROFILES) }, 200)
        })
        
        list.add(TrackpadMenuAdapter.MenuItem("Delete Profile", android.R.drawable.ic_menu_delete, TrackpadMenuAdapter.Type.ACTION) { 
            service.deleteCurrentProfile()
            drawerView?.postDelayed({ loadTab(TAB_PROFILES) }, 200)
        })
        
        // --- SAVED LAYOUTS LIST ---
        list.add(TrackpadMenuAdapter.MenuItem("SAVED LAYOUTS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        val saved = service.getSavedProfileList()
        if (saved.isEmpty()) {
            list.add(TrackpadMenuAdapter.MenuItem("No saved layouts found.", 0, TrackpadMenuAdapter.Type.INFO))
        } else {
            for (res in saved) {
                list.add(TrackpadMenuAdapter.MenuItem("• $res", 0, TrackpadMenuAdapter.Type.INFO))
            }
        }
        
        return list
    }

    private fun getHelpItems(): List<TrackpadMenuAdapter.MenuItem> {
        val list = ArrayList<TrackpadMenuAdapter.MenuItem>()
        
        list.add(TrackpadMenuAdapter.MenuItem("INSTRUCTIONS", 0, TrackpadMenuAdapter.Type.HEADER))
        
        val text = 
            "TRACKPAD CONTROLS\n" +
            "• Tap: Left Click\n" +
            "• 2-Finger Tap: Right Click\n" +
            "• Hold + Slide: Drag & Drop\n" +
            "• Edge (Top/Bottom): V-Scroll\n" +
            "• Edge (Left/Right): H-Scroll\n\n" +
            "KEYBOARD OVERLAY\n" +
            "• Drag Top Bar: Move Window\n" +
            "• Drag Bottom-Right: Resize\n" +
            "• Hold Corner: Toggle Key/Mouse\n\n" +
            "HARDWARE KEYS\n" +
            "• Use the 'Hardkeys' tab to map\n" +
            "  Volume Up/Down to clicks,\n" +
            "  scrolling, or screen controls."
            
        list.add(TrackpadMenuAdapter.MenuItem(text, 0, TrackpadMenuAdapter.Type.INFO))
        
        list.add(TrackpadMenuAdapter.MenuItem("LAUNCHER & APP", 0, TrackpadMenuAdapter.Type.HEADER))
        val text2 = 
            "• Floating Bubble: Tap to open this menu. Drag to move.\n" +
            "• Setup App: Open 'DroidOS Trackpad' from your Android App Drawer to adjust permissions or restart the service."
        list.add(TrackpadMenuAdapter.MenuItem(text2, 0, TrackpadMenuAdapter.Type.INFO))
        
        return list
    }
}
