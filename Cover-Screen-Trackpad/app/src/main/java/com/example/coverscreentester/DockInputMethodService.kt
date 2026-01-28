package com.example.coverscreentester

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
        private const val BROADCAST_ACTION_TEXT = "com.example.coverscreentester.INJECT_TEXT"
        private const val BROADCAST_ACTION_KEY = "com.example.coverscreentester.INJECT_KEY"
        private const val BROADCAST_ACTION_DELETE = "com.example.coverscreentester.INJECT_DELETE"
    }

    private var dockView: View? = null

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

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(BROADCAST_ACTION_TEXT)
            addAction(BROADCAST_ACTION_KEY)
            addAction(BROADCAST_ACTION_DELETE)
        }
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(inputReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(inputReceiver, filter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(inputReceiver) } catch (e: Exception) {}
    }

    override fun onWindowShown() {
        super.onWindowShown()
        loadDockPrefs()
        
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
            android.util.Log.d(TAG, "Dock shown - applying dock mode")
            val intent = Intent("APPLY_DOCK_MODE")
            intent.setPackage(packageName)
            intent.putExtra("enabled", true)
            sendBroadcast(intent)
            
            // Apply auto resize if enabled
            if (prefAutoResize) {
                updateInputViewHeight()
            }
        }
    }
    
    override fun onWindowHidden() {
        super.onWindowHidden()
        loadDockPrefs()
        
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
    
    private fun loadDockPrefs() {
        val prefs = getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE)
        prefAutoShowOverlay = prefs.getBoolean("auto_show_overlay", false)
        prefDockMode = prefs.getBoolean("dock_mode", false)
        prefAutoResize = prefs.getBoolean("auto_resize", false)
    }
    
    private fun saveDockPrefs() {
        getSharedPreferences("DockIMEPrefs", Context.MODE_PRIVATE).edit()
            .putBoolean("auto_show_overlay", prefAutoShowOverlay)
            .putBoolean("dock_mode", prefDockMode)
            .putBoolean("auto_resize", prefAutoResize)
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
        }
        updateToggleVisuals()
        
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
            saveDockPrefs()
            updateToggleVisuals()
            
            val intent = Intent("DOCK_PREF_CHANGED")
            intent.setPackage(packageName)
            intent.putExtra("dock_mode", prefDockMode)
            sendBroadcast(intent)
            
            android.util.Log.d(TAG, "Dock mode: $prefDockMode")
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
        
        // Show above the anchor
        popupWindow?.showAsDropDown(anchor, 0, -anchor.height - 200)
    }
    // =================================================================================
    // END BLOCK: showDockPopup
    // =================================================================================

    // =================================================================================
    // FUNCTION: updateInputViewHeight
    // SUMMARY: Adjusts the IME input view height based on Auto Resize setting.
    //          When enabled, adds transparent space above the toolbar to push apps up.
    //          This makes full-screen apps resize to accommodate the keyboard area.
    // =================================================================================
    private fun updateInputViewHeight() {
        if (dockView == null) return
        
        if (prefAutoResize && prefDockMode) {
            // Get overlay keyboard height from shared prefs (set by OverlayService)
            val prefs = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
            val density = resources.displayMetrics.density
            val defaultKbHeight = (275 * density).toInt()
            val overlayKbHeight = prefs.getInt("keyboard_height_d1", defaultKbHeight)
            
            // Total height = overlay keyboard height
            val toolbarHeight = (40 * density).toInt() // Our visible toolbar
            val transparentSpace = (overlayKbHeight - toolbarHeight).coerceAtLeast(0)
            
            android.util.Log.d(TAG, "updateInputViewHeight: overlayKb=$overlayKbHeight, toolbar=$toolbarHeight, transparent=$transparentSpace")
            
            // Create a wrapper with transparent space at top, toolbar at bottom
            dockView?.setPadding(0, transparentSpace, 0, 0)
            
            // Request the IME framework to use the new height
            setInputView(createInputViewWrapper(overlayKbHeight))
        } else {
            // Reset to normal - just the toolbar
            dockView?.setPadding(0, 0, 0, 0)
            setInputView(dockView)
        }
    }
    
    // =================================================================================
    // FUNCTION: createInputViewWrapper
    // SUMMARY: Creates a wrapper view with specified height, toolbar at bottom.
    // =================================================================================
    private fun createInputViewWrapper(totalHeight: Int): View {
        val density = resources.displayMetrics.density
        val toolbarHeight = (40 * density).toInt()
        val transparentSpace = (totalHeight - toolbarHeight).coerceAtLeast(0)
        
        // Create container
        val container = android.widget.FrameLayout(this)
        container.layoutParams = android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            totalHeight
        )
        
        // Remove dockView from old parent if needed
        (dockView?.parent as? android.view.ViewGroup)?.removeView(dockView)
        
        // Add dock at bottom
        val dockParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            toolbarHeight
        )
        dockParams.gravity = android.view.Gravity.BOTTOM
        container.addView(dockView, dockParams)
        
        // The top area stays transparent (no view added)
        
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

    // Ensure we keep the connection active
    override fun onEvaluateInputViewShown(): Boolean {
        return true
    }
}
