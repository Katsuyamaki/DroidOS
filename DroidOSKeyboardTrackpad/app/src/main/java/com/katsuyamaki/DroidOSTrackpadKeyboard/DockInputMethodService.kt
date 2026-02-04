package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.BroadcastReceiver
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.SystemClock
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView

/**
 * DroidOS Input Dock (formerly Null Keyboard).
 * Provides a minimal toolbar for essential actions while keeping the screen clear.
 * Acts as the injection target for the DroidOS Overlay Trackpad/Keyboard.
 */
class DockInputMethodService : InputMethodService() {

    companion object {
        private const val TAG = "DockIME"
        // Keep the original broadcast action for backward compatibility with OverlayService
        private const val BROADCAST_ACTION_TEXT = "com.katsuyamaki.DroidOSTrackpadKeyboard.INJECT_TEXT"
        private const val BROADCAST_ACTION_KEY = "com.katsuyamaki.DroidOSTrackpadKeyboard.INJECT_KEY"
        private const val BROADCAST_ACTION_DELETE = "com.katsuyamaki.DroidOSTrackpadKeyboard.INJECT_DELETE"
    }

    private var dockView: View? = null

    private var launcherTiledActive = false
    private var forceFullUpdate = false // Flag to force full setInputView() on next update
    private var lastTiledStateTime = 0L // Timestamp of last TILED_STATE broadcast

    private val tiledStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val newState = intent?.getBooleanExtra("TILED_ACTIVE", false) ?: false
            android.util.Log.w(TAG, ">>> TILED_STATE received: $newState (was $launcherTiledActive, inputShown=$isInputViewShown)")

            lastTiledStateTime = System.currentTimeMillis()
            if (newState != launcherTiledActive) {
                launcherTiledActive = newState
                android.util.Log.w(TAG, ">>> TILED_STATE: changed to $newState, recomputing")
                // Force the system to recompute insets with the new tiled state
                window?.window?.decorView?.requestLayout()
                if (prefAutoResize && prefDockMode) {
                    // Switching to fullscreen needs full setInputView() for insets
                    if (!newState) {
                        forceFullUpdate = true
                    }
                    updateInputViewHeight()
                }
            }
        }
    }

    // Receiver to handle text injection from the Overlay Trackpad
    private val inputReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val ic = currentInputConnection ?: return
            
            when (intent?.action) {
                BROADCAST_ACTION_TEXT -> {
                    val text = intent.getStringExtra("text")
                    if (!text.isNullOrEmpty()) ic.commitText(text, 1)
                }
                BROADCAST_ACTION_KEY -> {
                    val code = intent.getIntExtra("keyCode", 0)
                    val metaState = intent.getIntExtra("metaState", 0)
                    if (code > 0) sendKeyEventWithMeta(ic, code, metaState)
                }
                BROADCAST_ACTION_DELETE -> {
                    val length = intent.getIntExtra("length", 1)
                    if (length > 0) ic.deleteSurroundingText(length, 0)
                }
            }
        }
    }

    override fun onWindowShown() {
        super.onWindowShown()
        loadDockPrefs()
        
        // Trust the current launcherTiledActive value - TILED_STATE broadcasts 
        // keep it accurate. Don't reset it here.
        
        android.util.Log.w(TAG, ">>> onWindowShown: tiled=$launcherTiledActive, autoResize=$prefAutoResize, dockMode=$prefDockMode, scale=$prefResizeScale")
        
        // Notify Launcher that IME is now visible
        val imeShowIntent = Intent("com.katsuyamaki.DroidOSLauncher.IME_VISIBILITY")
        imeShowIntent.setPackage("com.katsuyamaki.DroidOSLauncher")
        imeShowIntent.putExtra("VISIBLE", true)
        imeShowIntent.putExtra("IS_TILED", launcherTiledActive)
        sendBroadcast(imeShowIntent)
        
        // Auto-show overlay keyboard if enabled
        if (prefAutoShowOverlay) {






            android.util.Log.d(TAG, "Dock shown - auto-showing overlay keyboard")
            val intent = Intent("TOGGLE_CUSTOM_KEYBOARD")
            intent.setPackage(packageName)
            intent.putExtra("FORCE_SHOW", true)
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            sendBroadcast(intent)
        }
        
        // Apply dock mode if enabled
        if (prefDockMode) {
            android.util.Log.d(TAG, "Dock shown - applying dock mode (autoResize=$prefAutoResize, margin=$prefResizeScale%)")
            val intent = Intent("APPLY_DOCK_MODE")
            intent.setPackage(packageName)
            intent.putExtra("enabled", true)
            if (prefAutoResize) {
                intent.putExtra("resize_to_margin", prefResizeScale)
            }
            sendBroadcast(intent)
            
            if (prefAutoResize) {
                updateInputViewHeight()
            }
        }
    }
    
    override fun onWindowHidden() {
        super.onWindowHidden()
        loadDockPrefs()
        
        // Notify Launcher that IME is now hidden
        // [FIX] Add FORCE_RETILE to ensure apps resize when manually hiding via X button
        val imeHideIntent = Intent("com.katsuyamaki.DroidOSLauncher.IME_VISIBILITY")
        imeHideIntent.setPackage("com.katsuyamaki.DroidOSLauncher")
        imeHideIntent.putExtra("VISIBLE", false)
        imeHideIntent.putExtra("IS_TILED", launcherTiledActive)
        imeHideIntent.putExtra("FORCE_RETILE", true)
        sendBroadcast(imeHideIntent)
        
        // [FIX] Force inset recomputation when window hidden
        if (prefAutoResize && prefDockMode) {
            window?.window?.decorView?.requestLayout()
        }
        
        // Auto-hide overlay keyboard if enabled


        if (prefAutoShowOverlay) {
            android.util.Log.d(TAG, "Dock hidden - auto-hiding overlay keyboard")
            val intent = Intent("TOGGLE_CUSTOM_KEYBOARD")
            intent.setPackage(packageName)
            intent.putExtra("FORCE_HIDE", true)
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            sendBroadcast(intent)
        }
    }

    override fun onCreateInputView(): View {
        try {
            loadDockPrefs()
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            dockView = inflater.inflate(R.layout.layout_input_dock, null)
            setupDockListeners(dockView!!)
            return dockView!!


        } catch (e: Exception) {
            // Safety Fallback: Return 0-size view if XML fails (prevents OverlayService crash)
            android.util.Log.e(TAG, "Failed to inflate Dock UI", e)
            return View(this).apply { 
                layoutParams = android.view.ViewGroup.LayoutParams(0, 0)
                visibility = View.GONE
            }
        }
    }

    // =================================================================================
    // DOCK POPUP MENU STATE
    // =================================================================================
    private var popupWindow: android.widget.PopupWindow? = null
    private var prefAutoShowOverlay = false
    private var prefDockMode = false
    private var prefAutoResize = false
    private var prefResizeScale = 0 // Default 0% (Range 0-50%)
    private var prefSyncMargin = false
    private var prefShowKBAboveDock = true // Default ON when dock mode is enabled
    
    private val ACTION_MARGIN_CHANGED = "com.katsuyamaki.DroidOSLauncher.MARGIN_CHANGED"
    private val ACTION_SET_MARGIN = "com.katsuyamaki.DroidOSLauncher.SET_MARGIN_BOTTOM"
    
    // Callback to update UI if popup is open
    private var onMarginUpdatedCallback: ((Int) -> Unit)? = null





    private val marginReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_MARGIN_CHANGED && prefSyncMargin) {
                val percent = intent.getIntExtra("PERCENT", 0)
                // Always update if different
                if (prefResizeScale != percent) {
                    prefResizeScale = percent
                    saveDockPrefs()
                    updateInputViewHeight()
                    
                    // Update UI if visible
                    onMarginUpdatedCallback?.invoke(percent)
                }
            }
        }
    }


    // [FIX] Receiver to hide DockIME when overlay KB is manually hidden
    private val hideDockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.katsuyamaki.DroidOSTrackpadKeyboard.HIDE_DOCK_IME") {
                android.util.Log.w(TAG, ">>> HIDE_DOCK_IME received - hiding self")
                requestHideSelf(0)
            }
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(BROADCAST_ACTION_TEXT)
            addAction(BROADCAST_ACTION_KEY)
            addAction(BROADCAST_ACTION_DELETE)
            addAction(ACTION_MARGIN_CHANGED)
        }
        val tiledFilter = IntentFilter("com.katsuyamaki.DroidOSTrackpadKeyboard.TILED_STATE")
        val hideFilter = IntentFilter("com.katsuyamaki.DroidOSTrackpadKeyboard.HIDE_DOCK_IME")
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(inputReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            registerReceiver(marginReceiver, IntentFilter(ACTION_MARGIN_CHANGED), Context.RECEIVER_EXPORTED)
            registerReceiver(tiledStateReceiver, tiledFilter, Context.RECEIVER_EXPORTED)
            registerReceiver(hideDockReceiver, hideFilter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(inputReceiver, filter)
            registerReceiver(marginReceiver, IntentFilter(ACTION_MARGIN_CHANGED))
            registerReceiver(tiledStateReceiver, tiledFilter)
            registerReceiver(hideDockReceiver, hideFilter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(inputReceiver) } catch (e: Exception) {}
        try { unregisterReceiver(marginReceiver) } catch (e: Exception) {}
        try { unregisterReceiver(tiledStateReceiver) } catch (e: Exception) {}
        try { unregisterReceiver(hideDockReceiver) } catch (e: Exception) {}
    }



    private fun loadDockPrefs() {
        val prefs = getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE)
        prefAutoShowOverlay = prefs.getBoolean("auto_show_overlay", false)
        prefDockMode = prefs.getBoolean("dock_mode", false)
        prefAutoResize = prefs.getBoolean("auto_resize", false)
        prefResizeScale = prefs.getInt("auto_resize_scale", 0)
        prefSyncMargin = prefs.getBoolean("sync_margin", false)
        prefShowKBAboveDock = prefs.getBoolean("show_kb_above_dock", true) // Default ON
    }
    
    private fun saveDockPrefs() {
        getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE).edit()
            .putBoolean("auto_show_overlay", prefAutoShowOverlay)
            .putBoolean("dock_mode", prefDockMode)
            .putBoolean("auto_resize", prefAutoResize)
            .putInt("auto_resize_scale", prefResizeScale)
            .putBoolean("sync_margin", prefSyncMargin)
            .putBoolean("show_kb_above_dock", prefShowKBAboveDock)
            .apply()
    }
    // =================================================================================

    // END BLOCK: DOCK POPUP STATE
    // =================================================================================




    // =================================================================================
    // FUNCTION: setupDockListeners
    // SUMMARY: Sets up click handlers for all dock buttons.
    //          KB button has swipe-up gesture to show popup menu.
    // =================================================================================
    private fun setupDockListeners(view: View) {
        loadDockPrefs()
        
        val btnKeyboard = view.findViewById<View>(R.id.btn_dock_keyboard)
        val btnVoice = view.findViewById<View>(R.id.btn_dock_voice)
        val btnPaste = view.findViewById<View>(R.id.btn_dock_paste)
        val btnSwitch = view.findViewById<View>(R.id.btn_dock_switch)
        val btnHide = view.findViewById<View>(R.id.btn_dock_hide)

        // 1. KB Button - Tap to toggle, Swipe Up OR Long Press for popup menu
        var kbTouchStartY = 0f
        var kbTouchStartX = 0f
        var kbTouchStartTime = 0L
        var kbLongPressTriggered = false
        val swipeThreshold = 30f // Lower threshold for easier swipe detection
        val longPressDelay = 400L // ms
        
        val longPressRunnable = Runnable {
            kbLongPressTriggered = true
            android.util.Log.d(TAG, "KB long press -> showing popup")
            // Vibrate feedback
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? android.os.Vibrator
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                vibrator?.vibrate(android.os.VibrationEffect.createOneShot(30, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
            showDockPopup(btnKeyboard!!)
        }
        val longPressHandler = android.os.Handler(android.os.Looper.getMainLooper())
        
        btnKeyboard?.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    kbTouchStartY = event.rawY
                    kbTouchStartX = event.rawX
                    kbTouchStartTime = System.currentTimeMillis()
                    kbLongPressTriggered = false
                    // Start long press timer
                    longPressHandler.postDelayed(longPressRunnable, longPressDelay)
                    true
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    val deltaY = kbTouchStartY - event.rawY
                    val deltaX = kotlin.math.abs(event.rawX - kbTouchStartX)
                    // Cancel long press if user is swiping
                    if (deltaY > 15f || deltaX > 15f) {
                        longPressHandler.removeCallbacks(longPressRunnable)
                    }
                    // Check for swipe up during move
                    if (deltaY > swipeThreshold && deltaX < swipeThreshold * 2 && !kbLongPressTriggered) {
                        longPressHandler.removeCallbacks(longPressRunnable)
                        kbLongPressTriggered = true
                        android.util.Log.d(TAG, "KB swipe up -> showing popup")
                        showDockPopup(v)
                    }
                    true
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    longPressHandler.removeCallbacks(longPressRunnable)
                    val deltaY = kbTouchStartY - event.rawY
                    val duration = System.currentTimeMillis() - kbTouchStartTime
                    
                    if (!kbLongPressTriggered) {
                        // Normal tap - toggle keyboard (only if no popup was shown)
                        if (duration < longPressDelay && kotlin.math.abs(deltaY) < swipeThreshold) {
                            android.util.Log.d(TAG, "Dock KB tap -> TOGGLE_CUSTOM_KEYBOARD")
                            val intent = Intent("TOGGLE_CUSTOM_KEYBOARD")
                            intent.setPackage(packageName)
                            intent.putExtra("FORCE_SHOW", true)
                            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                            sendBroadcast(intent)
                        }
                    }
                    v.performClick()
                    true
                }
                else -> false
            }
        }





        // 2. Voice Input
        // FIX: Use SHORT action - OverlayService listens for "REQUEST_VOICE_INPUT"
        btnVoice?.setOnClickListener {
            android.util.Log.d(TAG, "Dock MIC pressed -> REQUEST_VOICE_INPUT")
            val intent = Intent("REQUEST_VOICE_INPUT")
            intent.setPackage(packageName)
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            sendBroadcast(intent)
        }

        // 3. Paste
        btnPaste?.setOnClickListener {
            android.util.Log.d(TAG, "Dock PST pressed -> pasting clipboard")
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip()) {
                val item = clipboard.primaryClip?.getItemAt(0)
                val text = item?.text
                if (!text.isNullOrEmpty()) {
                    currentInputConnection?.commitText(text, 1)
                }
            }
        }

        // 4. Switch IME
        btnSwitch?.setOnClickListener {
            android.util.Log.d(TAG, "Dock IME pressed -> showing picker")
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }

        // 5. Hide Dock
        btnHide?.setOnClickListener {
            android.util.Log.d(TAG, "Dock X pressed -> hiding")
            requestHideSelf(0)
        }
    }
    // =================================================================================
    // END BLOCK: setupDockListeners
    // =================================================================================

    // =================================================================================
    // FUNCTION: showDockPopup
    // SUMMARY: Shows popup menu above the KB button with toggle options.
    //          Auto Resize option is only enabled when Dock Mode is active.
    // =================================================================================
    private fun showDockPopup(anchor: View) {
        // Dismiss existing popup if any
        popupWindow?.dismiss()
        
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.layout_dock_popup, null)
        
        // Setup toggle visuals
        val toggleAutoShow = popupView.findViewById<View>(R.id.toggle_auto_show)
        val toggleDockMode = popupView.findViewById<View>(R.id.toggle_dock_mode)
        val toggleAutoResize = popupView.findViewById<View>(R.id.toggle_auto_resize)
        val iconAutoShow = popupView.findViewById<android.widget.ImageView>(R.id.icon_auto_show)
        val iconDockMode = popupView.findViewById<android.widget.ImageView>(R.id.icon_dock_mode)
        val iconAutoResize = popupView.findViewById<android.widget.ImageView>(R.id.icon_auto_resize)
        val textAutoResize = popupView.findViewById<android.widget.TextView>(R.id.text_auto_resize)
        val optionAutoResize = popupView.findViewById<View>(R.id.option_auto_resize)
        
        val containerSlider = popupView.findViewById<View>(R.id.container_resize_slider)
        val dividerResize = popupView.findViewById<View>(R.id.divider_resize)
        val textSliderLabel = popupView.findViewById<android.widget.TextView>(R.id.text_resize_label)
        val seekResize = popupView.findViewById<android.widget.SeekBar>(R.id.seekbar_resize_height)
        val checkSync = popupView.findViewById<android.widget.CheckBox>(R.id.checkbox_sync_margin)
        
        // Option 2b: Show KB Above Dock
        val optionKBAboveDock = popupView.findViewById<View>(R.id.option_kb_above_dock)
        val toggleKBAboveDock = popupView.findViewById<View>(R.id.toggle_kb_above_dock)
        val iconKBAboveDock = popupView.findViewById<android.widget.ImageView>(R.id.icon_kb_above_dock)
        val textKBAboveDock = popupView.findViewById<android.widget.TextView>(R.id.text_kb_above_dock)

        fun updateToggleVisuals() {
            // Option 1 & 2 - always enabled
            toggleAutoShow?.setBackgroundColor(if (prefAutoShowOverlay) 0xFF3DDC84.toInt() else 0xFF555555.toInt())
            toggleDockMode?.setBackgroundColor(if (prefDockMode) 0xFF3DDC84.toInt() else 0xFF555555.toInt())
            iconAutoShow?.setColorFilter(if (prefAutoShowOverlay) 0xFF3DDC84.toInt() else 0xFF888888.toInt())
            iconDockMode?.setColorFilter(if (prefDockMode) 0xFF3DDC84.toInt() else 0xFF888888.toInt())
            
            // Option 3 - Auto Resize: Only enabled when Dock Mode is ON
            val autoResizeEnabled = prefDockMode
            optionAutoResize?.alpha = if (autoResizeEnabled) 1.0f else 0.4f
            optionAutoResize?.isClickable = autoResizeEnabled
            
            if (autoResizeEnabled) {
                toggleAutoResize?.setBackgroundColor(if (prefAutoResize) 0xFF3DDC84.toInt() else 0xFF555555.toInt())
                iconAutoResize?.setColorFilter(if (prefAutoResize) 0xFF3DDC84.toInt() else 0xFF888888.toInt())
                textAutoResize?.setTextColor(0xFFFFFFFF.toInt())
            } else {
                toggleAutoResize?.setBackgroundColor(0xFF333333.toInt())
                iconAutoResize?.setColorFilter(0xFF555555.toInt())
                textAutoResize?.setTextColor(0xFF666666.toInt())
                // Also disable auto resize if dock mode is off
                if (prefAutoResize) {
                    prefAutoResize = false
                    saveDockPrefs()
                }
            }
            
            // Slider Visibility
            if (prefAutoResize && prefDockMode) {
                containerSlider?.visibility = View.VISIBLE
                dividerResize?.visibility = View.VISIBLE
            } else {
                containerSlider?.visibility = View.GONE
                dividerResize?.visibility = View.GONE
            }
            
            // Option 2b - KB Above Dock: Only enabled when Dock Mode is ON
            optionKBAboveDock?.alpha = if (prefDockMode) 1.0f else 0.4f
            optionKBAboveDock?.isClickable = prefDockMode
            optionKBAboveDock?.visibility = if (prefDockMode) View.VISIBLE else View.GONE
            
            if (prefDockMode) {
                toggleKBAboveDock?.setBackgroundColor(if (prefShowKBAboveDock) 0xFF3DDC84.toInt() else 0xFF555555.toInt())
                iconKBAboveDock?.setColorFilter(if (prefShowKBAboveDock) 0xFF3DDC84.toInt() else 0xFF888888.toInt())
                textKBAboveDock?.setTextColor(0xFFCCCCCC.toInt())
            } else {
                toggleKBAboveDock?.setBackgroundColor(0xFF333333.toInt())
                iconKBAboveDock?.setColorFilter(0xFF555555.toInt())
                textKBAboveDock?.setTextColor(0xFF666666.toInt())
            }
        }
        updateToggleVisuals()
        
        // Register callback for external updates (from Launcher)
        onMarginUpdatedCallback = { newPercent ->
            seekResize?.progress = newPercent
            textSliderLabel?.text = "Bottom Margin: $newPercent%"
        }
        
        // Cleanup callback on dismiss
        popupWindow?.setOnDismissListener {
            onMarginUpdatedCallback = null
        }

        // Setup Sync Checkbox
        checkSync?.isChecked = prefSyncMargin
        checkSync?.setOnCheckedChangeListener { _, isChecked ->
            prefSyncMargin = isChecked
            saveDockPrefs()
            
            // If turned ON, immediately sync values
            if (isChecked) {
                // 1. Force Launcher to match IME
                val intent = Intent(ACTION_SET_MARGIN)
                intent.setPackage("com.katsuyamaki.DroidOSLauncher") // Explicit Target
                intent.putExtra("PERCENT", prefResizeScale)
                sendBroadcast(intent)
            }
        }

        // Setup Slider
        seekResize?.progress = prefResizeScale
        textSliderLabel?.text = "Bottom Margin: $prefResizeScale%"
        
        seekResize?.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    prefResizeScale = progress
                    textSliderLabel?.text = "Bottom Margin: $progress%"
                }
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                saveDockPrefs()
                updateInputViewHeight()
                
                // Sync to Launcher if enabled
                if (prefSyncMargin) {
                    val intent = Intent(ACTION_SET_MARGIN)
                    intent.setPackage("com.katsuyamaki.DroidOSLauncher") // Explicit Target
                    intent.putExtra("PERCENT", prefResizeScale)
                    sendBroadcast(intent)
                }
                
                // Notify OverlayService to resize keyboard to fit margin
                val kbIntent = Intent("DOCK_PREF_CHANGED")
                kbIntent.setPackage(packageName)
                kbIntent.putExtra("resize_to_margin", prefResizeScale)
                kbIntent.putExtra("show_kb_above_dock", prefShowKBAboveDock)
                sendBroadcast(kbIntent)
            }
        })


        
        // Option 1: Auto-show overlay

        popupView.findViewById<View>(R.id.option_auto_show)?.setOnClickListener {
            prefAutoShowOverlay = !prefAutoShowOverlay
            saveDockPrefs()
            updateToggleVisuals()
            
            val intent = Intent("DOCK_PREF_CHANGED")
            intent.setPackage(packageName)
            intent.putExtra("auto_show_overlay", prefAutoShowOverlay)
            sendBroadcast(intent)
            
            android.util.Log.d(TAG, "Auto-show overlay: $prefAutoShowOverlay")
        }
        
        // Option 2: Dock mode
        popupView.findViewById<View>(R.id.option_dock_mode)?.setOnClickListener {
            prefDockMode = !prefDockMode
            // Auto-enable KB Above Dock when dock mode is first enabled
            if (prefDockMode && !getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE).contains("show_kb_above_dock")) {
                prefShowKBAboveDock = true
            }
            saveDockPrefs()
            updateToggleVisuals()
            
            val intent = Intent("DOCK_PREF_CHANGED")
            intent.setPackage(packageName)
            intent.putExtra("dock_mode", prefDockMode)
            intent.putExtra("show_kb_above_dock", prefShowKBAboveDock && prefDockMode)
            sendBroadcast(intent)
            
            android.util.Log.d(TAG, "Dock mode: $prefDockMode")
        }
        
        // Option 2b: Show KB Above Dock (sub-option of Dock Mode)
        optionKBAboveDock?.setOnClickListener {
            if (!prefDockMode) return@setOnClickListener
            
            prefShowKBAboveDock = !prefShowKBAboveDock
            saveDockPrefs()
            updateToggleVisuals()
            
            // Notify OverlayService to reposition keyboard
            val intent = Intent("DOCK_PREF_CHANGED")
            intent.setPackage(packageName)
            intent.putExtra("show_kb_above_dock", prefShowKBAboveDock)
            sendBroadcast(intent)
            
            android.util.Log.d(TAG, "Show KB Above Dock: $prefShowKBAboveDock")
        }
        
        // Option 3: Auto Resize Apps (only when dock mode enabled)
        optionAutoResize?.setOnClickListener {
            if (!prefDockMode) return@setOnClickListener // Ignore if dock mode is off
            
            prefAutoResize = !prefAutoResize
            saveDockPrefs()
            updateToggleVisuals()
            updateInputViewHeight()
            
            val intent = Intent("DOCK_PREF_CHANGED")
            intent.setPackage(packageName)
            intent.putExtra("auto_resize", prefAutoResize)
            sendBroadcast(intent)
            
            // Sync auto-adjust margin setting to launcher
            val launcherIntent = Intent("com.katsuyamaki.DroidOSLauncher.SET_AUTO_ADJUST_MARGIN")
            launcherIntent.setPackage("com.katsuyamaki.DroidOSLauncher")
            launcherIntent.putExtra("ENABLED", prefAutoResize)
            sendBroadcast(launcherIntent)
            
            android.util.Log.d(TAG, "Auto resize: $prefAutoResize")


        }
        
        // Create popup window
        popupWindow = android.widget.PopupWindow(
            popupView,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            true // focusable
        )
        
        popupWindow?.elevation = 16f
        popupWindow?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(0xFF2A2A2A.toInt()))
        
        // HELPER: Calculate position and show/update
        fun positionAndShow() {
            // Measure content to calculate correct offset (Popup grows upwards)
            popupView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), 
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val h = popupView.measuredHeight
            
            // Calculate Y offset: -AnchorHeight (top of button) - PopupHeight (move popup above)
            val yOff = -anchor.height - h
            
            if (popupWindow?.isShowing == true) {
                popupWindow?.update(anchor, 0, yOff, -1, -1)
            } else {
                popupWindow?.showAsDropDown(anchor, 0, yOff)
            }
        }

        // Update listener to reposition when size changes (e.g. slider toggled)
        optionAutoResize?.setOnClickListener {
            if (!prefDockMode) return@setOnClickListener
            
            prefAutoResize = !prefAutoResize
            saveDockPrefs()
            updateToggleVisuals()
            updateInputViewHeight()
            
            // Recalculate position since height changed
            positionAndShow()
            
            val intent = Intent("DOCK_PREF_CHANGED")
            intent.setPackage(packageName)
            intent.putExtra("auto_resize", prefAutoResize)
            sendBroadcast(intent)
            
            // Sync auto-adjust margin setting to launcher
            val launcherIntent = Intent("com.katsuyamaki.DroidOSLauncher.SET_AUTO_ADJUST_MARGIN")
            launcherIntent.setPackage("com.katsuyamaki.DroidOSLauncher")
            launcherIntent.putExtra("ENABLED", prefAutoResize)
            sendBroadcast(launcherIntent)
        }

        // Initial Show
        positionAndShow()


    }
    // =================================================================================
    // END BLOCK: showDockPopup
    // =================================================================================

    // =================================================================================
    // FUNCTION: updateInputViewHeight
    // SUMMARY: Adjusts the IME input view height based on Auto Resize setting.
    //          Calculates TOTAL height as percentage of REAL screen height.
    //          SUBTRACTS Navigation Bar height to prevent overlap with Launcher tiles.
    //          (Launcher tiles end at [Screen - Margin]. IME starts at [Screen - Nav - IME_H].
    //           If IME_H = Margin, then IME overlaps tiles by Nav height. 
    //           So we need IME_H = Margin - Nav).
    // [FIX] Avoid calling setInputView() when only adjusting margin, as it triggers
    //       Android's ADJUST_RESIZE behavior which moves tiled freeform windows.
    //       Instead, we update the existing wrapper's layout params directly.
    // =================================================================================
    private var currentInputWrapper: android.view.View? = null
    private var lastCalculatedHeight: Int = -1
    
    private fun updateInputViewHeight() {
        if (dockView == null) return
        
        if (prefAutoResize && prefDockMode) {
            val wm = getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
            
            // 1. Get Real Screen Height (Physical pixels)
            val metrics = android.util.DisplayMetrics()
            wm.defaultDisplay.getRealMetrics(metrics)
            val screenHeight = metrics.heightPixels
            
            // 2. Get Navigation Bar Height (Insets)
            val windowMetrics = wm.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                android.view.WindowInsets.Type.navigationBars() or 
                android.view.WindowInsets.Type.displayCutout()
            )
            val navHeight = insets.bottom
            
            // 3. Calculate Margin Height (Desired clear space)
            // The overlay keyboard now resizes to fit this margin, so no need to add its height
            val marginHeight = (screenHeight * (prefResizeScale / 100f)).toInt()
            
            // 4. Calculate IME Window Height
            // Subtract nav height because IME sits ON TOP of nav bar, while margin is from physical bottom.
            // [FIX] Subtract extra 2px safety buffer to handle rounding errors and ensure
            // the IME window stays strictly below the Launcher's tiling line.
            val correctedHeight = (marginHeight - navHeight - 2).coerceAtLeast(0)
            
            android.util.Log.d(TAG, "updateInputViewHeight: ON. Margin=$marginHeight, Nav=$navHeight -> IME Height=$correctedHeight, tiled=$launcherTiledActive")
            
            // [FIX] Check if we already have a wrapper with the same height structure
            // If the wrapper exists and is attached, just request a layout update instead of setInputView()
            // This prevents Android from triggering ADJUST_RESIZE on tiled windows
            // BUT for fullscreen apps (!launcherTiledActive), we NEED Android to recalculate insets
            val existingWrapper = currentInputWrapper
            val canSoftUpdate = existingWrapper != null && 
                existingWrapper.parent != null && 
                isInputViewShown &&
                lastCalculatedHeight > 0 &&
                launcherTiledActive && // Only soft update for tiled apps
                !forceFullUpdate // Respect forced full update flag
                
            if (canSoftUpdate) {
                // Update the existing wrapper's height via layout params
                // This is a "soft" update that doesn't trigger IME inset recalculation
                val density = resources.displayMetrics.density
                val toolbarHeight = (40 * density).toInt()
                val safeTotalHeight = correctedHeight.coerceAtLeast(toolbarHeight)
                
                existingWrapper!!.layoutParams?.let { params ->
                    params.height = safeTotalHeight
                    existingWrapper.layoutParams = params
                }
                existingWrapper.requestLayout()
                lastCalculatedHeight = correctedHeight
                
                android.util.Log.d(TAG, "updateInputViewHeight: SOFT UPDATE (tiled) h=$safeTotalHeight")
            } else {
                // First time, wrapper not attached, OR fullscreen app - do full setInputView()
                val wrapper = createInputViewWrapper(correctedHeight)
                currentInputWrapper = wrapper
                lastCalculatedHeight = correctedHeight
                forceFullUpdate = false // Clear flag after full update
                setInputView(wrapper)
                android.util.Log.d(TAG, "updateInputViewHeight: FULL UPDATE (setInputView) h=$correctedHeight fullscreen=${!launcherTiledActive}")
            }
        } else {
            // Reset to normal - just the toolbar
            android.util.Log.d(TAG, "updateInputViewHeight: OFF. Dock Only.")
            
            // CRITICAL: Must detach from any previous wrapper before re-using
            (dockView?.parent as? android.view.ViewGroup)?.removeView(dockView)
            
            dockView?.setPadding(0, 0, 0, 0)
            dockView?.visibility = View.VISIBLE
            currentInputWrapper = null
            lastCalculatedHeight = -1
            setInputView(dockView)
        }
    }

    
    // =================================================================================
    // FUNCTION: createInputViewWrapper
    // SUMMARY: Creates a wrapper view with specified height, toolbar at bottom.
    //          Uses FrameLayout with overridden onMeasure to strictly enforce height.
    // =================================================================================
    private fun createInputViewWrapper(totalHeight: Int): View {
        val density = resources.displayMetrics.density
        val toolbarHeight = (40 * density).toInt()
        
        // Safety: Ensure total height is at least toolbar height
        val safeTotalHeight = totalHeight.coerceAtLeast(toolbarHeight)
        
        // 1. Create FrameLayout Container with Enforced Height
        // We override onMeasure to guarantee the IME service respects this size
        val container = object : android.widget.FrameLayout(this) {
            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                val heightSpec = View.MeasureSpec.makeMeasureSpec(safeTotalHeight, View.MeasureSpec.EXACTLY)
                super.onMeasure(widthMeasureSpec, heightSpec)
            }
        }
        
        // Force layout params
        container.layoutParams = android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            safeTotalHeight
        )
        container.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        // 2. Prepare Dock View
        (dockView?.parent as? android.view.ViewGroup)?.removeView(dockView)
        dockView?.setPadding(0, 0, 0, 0)
        dockView?.visibility = View.VISIBLE
        
        // 3. Add Dock to Bottom
        val dockParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            toolbarHeight
        )
        dockParams.gravity = android.view.Gravity.BOTTOM
        container.addView(dockView, dockParams)
        
        return container
    }
    // =================================================================================
    // END BLOCK: updateInputViewHeight
    // =================================================================================

    // Helper: Send key with meta state
    private fun sendKeyEventWithMeta(ic: InputConnection, keyCode: Int, metaState: Int) {





        val eventTime = SystemClock.uptimeMillis()
        val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0, metaState, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE, android.view.InputDevice.SOURCE_KEYBOARD)
        val upEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0, metaState, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE, android.view.InputDevice.SOURCE_KEYBOARD)
        ic.sendKeyEvent(downEvent)
        ic.sendKeyEvent(upEvent)
    }

    // Suppress insets for tiled apps (Launcher handles resize), report normally for fullscreen
    override fun onComputeInsets(outInsets: InputMethodService.Insets) {
        super.onComputeInsets(outInsets)
        if (isInputViewShown && dockView != null) {
            val viewH = window?.window?.decorView?.height ?: 0
            val wrapperH = currentInputWrapper?.height ?: -1
            android.util.Log.w(TAG, ">>> onComputeInsets: tiled=$launcherTiledActive, autoResize=$prefAutoResize, dockMode=$prefDockMode, viewH=$viewH, wrapperH=$wrapperH, lastCalcH=$lastCalculatedHeight")
            
            if (prefAutoResize && prefDockMode && launcherTiledActive) {
                // Tiled: Launcher handles resize, suppress insets to avoid double-resize
                outInsets.contentTopInsets = viewH
                outInsets.visibleTopInsets = viewH
                outInsets.touchableInsets = InputMethodService.Insets.TOUCHABLE_INSETS_FRAME
                android.util.Log.w(TAG, ">>> onComputeInsets: TILED MODE - suppressing insets (contentTop=$viewH)")
            } else if (prefAutoResize && prefDockMode) {
                // [FIX] Fullscreen: Let Android use our wrapper's actual height for insets
                // Setting contentTopInsets=0 means "content starts at top of IME frame"
                // Android will measure our wrapper and resize the app accordingly
                outInsets.contentTopInsets = 0
                outInsets.visibleTopInsets = 0
                outInsets.touchableInsets = InputMethodService.Insets.TOUCHABLE_INSETS_CONTENT
                android.util.Log.w(TAG, ">>> onComputeInsets: FULLSCREEN MODE - using default insets (wrapper=$wrapperH)")
            } else {
                // No auto-resize: minimal toolbar only
                outInsets.contentTopInsets = 0
                outInsets.visibleTopInsets = 0
                outInsets.touchableInsets = InputMethodService.Insets.TOUCHABLE_INSETS_CONTENT
                android.util.Log.w(TAG, ">>> onComputeInsets: NO AUTO-RESIZE - minimal toolbar")
            }
        } else {
            android.util.Log.w(TAG, ">>> onComputeInsets: SKIPPED (inputViewShown=${isInputViewShown}, dockView=${dockView != null})")
        }
    }




    // [FIX] Prevent full-screen extraction mode (landscape/small screen)
    // We always want to behave as a docked bar.
    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    // Ensure we keep the connection active
    override fun onEvaluateInputViewShown(): Boolean {
        return super.onEvaluateInputViewShown()
    }
}
