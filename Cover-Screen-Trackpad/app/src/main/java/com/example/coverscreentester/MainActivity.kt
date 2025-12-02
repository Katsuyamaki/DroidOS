package com.example.coverscreentester

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button
    private lateinit var hideAppButton: Button
    private lateinit var lockButton: Button
    private lateinit var profilesButton: Button
    private lateinit var settingsButton: Button
    private lateinit var helpButton: Button
    private lateinit var closeButton: Button
    private lateinit var btnManualAdjust: Button
    private lateinit var btnTargetSwitch: Button
    private lateinit var btnResetCursor: Button
    private lateinit var btnForceKeyboard: Button
    
    private var lastKnownDisplayId = -1
    
    // For 5x tap debug mode toggle
    private var resetCursorTapCount = 0
    private var lastResetCursorTapTime = 0L
    private val TAP_TIMEOUT = 1500L // 1.5 seconds to complete 5 taps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.text_status)
        toggleButton = findViewById(R.id.btn_toggle)
        hideAppButton = findViewById(R.id.btn_hide_app)
        lockButton = findViewById(R.id.btn_lock)
        profilesButton = findViewById(R.id.btn_profiles)
        settingsButton = findViewById(R.id.btn_settings)
        helpButton = findViewById(R.id.btn_help)
        closeButton = findViewById(R.id.btn_close)
        btnManualAdjust = findViewById(R.id.btn_manual_adjust)
        
        btnTargetSwitch = findViewById(R.id.btn_target_switch)
        btnTargetSwitch.setOnClickListener {
            val intent = Intent("CYCLE_INPUT_TARGET")
            intent.setPackage(packageName) 
            sendBroadcast(intent)
        }
        
        btnResetCursor = findViewById(R.id.btn_reset_cursor)
        btnResetCursor.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            
            // Check if we're within the tap timeout window
            if (currentTime - lastResetCursorTapTime > TAP_TIMEOUT) {
                resetCursorTapCount = 0
            }
            
            resetCursorTapCount++
            lastResetCursorTapTime = currentTime
            
            if (resetCursorTapCount >= 5) {
                // Toggle debug mode on 5th tap
                val intent = Intent("TOGGLE_DEBUG")
                intent.setPackage(packageName)
                sendBroadcast(intent)
                Toast.makeText(this, "Debug Mode Toggled", Toast.LENGTH_SHORT).show()
                resetCursorTapCount = 0
            } else {
                // Normal reset cursor behavior
                val intent = Intent("RESET_CURSOR")
                intent.setPackage(packageName)
                sendBroadcast(intent)
            }
        }
        
        btnForceKeyboard = findViewById(R.id.btn_force_keyboard)
        btnForceKeyboard.setOnClickListener {
            val intent = Intent("TOGGLE_CUSTOM_KEYBOARD")
            intent.setPackage(packageName)
            sendBroadcast(intent)
            Toast.makeText(this, "Toggling Keyboard...", Toast.LENGTH_SHORT).show()
        }

        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(0)
        }

        toggleButton.setOnClickListener {
            if (!isAccessibilityEnabled()) {
                Toast.makeText(this, "Please Enable Accessibility Service", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } else {
                // EXPLICIT MOVE: User clicked the button
                forceMoveCommand(true)
                Toast.makeText(this, "Moving Trackpad to this screen...", Toast.LENGTH_SHORT).show()
            }
        }
        
        hideAppButton.setOnClickListener {
            // Move app to background without closing
            moveTaskToBack(true)
        }

        settingsButton.setOnClickListener { 
            // Show trackpad in preview mode when entering settings
            val intent = Intent("SET_PREVIEW_MODE")
            intent.setPackage(packageName)
            intent.putExtra("PREVIEW_MODE", true)
            sendBroadcast(intent)
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        profilesButton.setOnClickListener {
            startActivity(Intent(this, ProfilesActivity::class.java))
        }
        
        btnManualAdjust.setOnClickListener {
            // Show trackpad in preview mode when entering manual adjust
            val intent = Intent("SET_PREVIEW_MODE")
            intent.setPackage(packageName)
            intent.putExtra("PREVIEW_MODE", true)
            sendBroadcast(intent)
            startActivity(Intent(this, ManualAdjustActivity::class.java))
        }

        helpButton.setOnClickListener { showHelpDialog() }
        lockButton.setOnClickListener { toggleLock() }

        closeButton.setOnClickListener {
            finishAffinity()
            System.exit(0)
        }

        if (savedInstanceState == null && isAccessibilityEnabled()) {
            checkAndMoveDisplay()
        }

        updateStatusUI()
        updateLockUI()
    }

    override fun onResume() {
        super.onResume()
        updateStatusUI()
        updateLockUI()
        if (isAccessibilityEnabled()) {
            checkAndMoveDisplay()
        }
        // Hide trackpad ONLY if on the SAME display as this menu
        val myDisplayId = display?.displayId ?: android.view.Display.DEFAULT_DISPLAY
        val intent = Intent("SET_TRACKPAD_VISIBILITY")
        intent.setPackage(packageName)
        intent.putExtra("VISIBLE", false)
        intent.putExtra("MENU_DISPLAY_ID", myDisplayId) // Pass ID to service
        sendBroadcast(intent)
    }
    
    override fun onPause() {
        super.onPause()
        // Show trackpad when leaving main menu (always show)
        val intent = Intent("SET_TRACKPAD_VISIBILITY")
        intent.setPackage(packageName)
        intent.putExtra("VISIBLE", true)
        sendBroadcast(intent)
        
        // Also disable preview mode
        val previewIntent = Intent("SET_PREVIEW_MODE")
        previewIntent.setPackage(packageName)
        previewIntent.putExtra("PREVIEW_MODE", false)
        sendBroadcast(previewIntent)
    }

    private fun checkAndMoveDisplay() {
        val currentDisplayId = display?.displayId ?: android.view.Display.DEFAULT_DISPLAY
        if (currentDisplayId != lastKnownDisplayId) {
            lastKnownDisplayId = currentDisplayId
            // IMPLICIT MOVE: Just updating known state, do not force if already open
            forceMoveCommand(false)
        }
    }

    private fun forceMoveCommand(isExplicit: Boolean) {
        val currentDisplayId = display?.displayId ?: android.view.Display.DEFAULT_DISPLAY
        val intent = Intent(this, OverlayService::class.java)
        intent.putExtra("DISPLAY_ID", currentDisplayId)
        if (isExplicit) {
            intent.putExtra("FORCE_MOVE", true)
        }
        startService(intent)
    }

    private fun updateStatusUI() {
        if (Shizuku.getBinder() == null) {
            statusText.text = "Status: Shizuku Not Running"
            statusText.setTextColor(0xFFFF0000.toInt())
        } else if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            statusText.text = "Status: Shizuku Permission Needed"
            statusText.setTextColor(0xFFFFFF00.toInt())
        } else if (isAccessibilityEnabled()) {
            statusText.text = "Status: Active"
            statusText.setTextColor(0xFF00FF00.toInt())
            toggleButton.text = "Move Trackpad Here"
        } else {
            statusText.text = "Status: Accessibility Needed"
            statusText.setTextColor(0xFFFFFF00.toInt())
            toggleButton.text = "Enable Trackpad"
        }
    }

    private fun isAccessibilityEnabled(): Boolean {
        val componentName = ComponentName(this, OverlayService::class.java)
        val enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        return enabledServices?.contains(componentName.flattenToString()) == true
    }

    private fun toggleLock() {
        val prefs = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val current = prefs.getBoolean("lock_position", false)
        prefs.edit().putBoolean("lock_position", !current).apply()
        updateLockUI()
        val intent = Intent(this, OverlayService::class.java)
        intent.action = "RELOAD_PREFS"
        startService(intent)
    }

    private fun updateLockUI() {
        val prefs = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val isLocked = prefs.getBoolean("lock_position", false)
        
        if (isLocked) {
            lockButton.text = "Position: Locked"
            lockButton.backgroundTintList = ColorStateList.valueOf(0xFFFF0000.toInt())
            lockButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_closed, 0, 0, 0)
        } else {
            lockButton.text = "Position: Unlocked"
            lockButton.backgroundTintList = ColorStateList.valueOf(0xFF3DDC84.toInt())
            lockButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_open, 0, 0, 0)
        }
    }

    private fun showHelpDialog() {
        val text = TextView(this)
        text.setPadding(50, 40, 50, 40)
        text.textSize = 14f
        text.text = """
            == TRACKPAD CONTROLS ==
            - Tap: Left Click
            - Vol Up + Drag: Drag/Select
            - Vol Down: Right Click (Back)
            - Vol Down (Hold 1s): Move trackpad
            
            == CORNER HANDLES ==
            - Top-Left TAP: Toggle Keyboard
            - Top-Left HOLD: Move trackpad
            - Top-Right HOLD: Move window
            - Bottom-Right HOLD: Resize window
            - Bottom-Left TAP: Open Menu
            
            == CUSTOM KEYBOARD ==
            - Full QWERTY layout
            - SHIFT tap = Single uppercase
            - SHIFT hold = Caps Lock (green)
            - ?123 = Symbol layers
            - Arrow/Tab/Esc keys at bottom
            - Drag top bar to move
            - Drag corner to resize
            
            == VIRTUAL DISPLAY ==
            1. Create Virtual Display
            2. Click 'Switch Local/Remote'
            3. PINK Border = Remote control
            
            == SECRET ==
            - Tap Reset Cursor 5x = Debug Mode
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Instructions")
            .setView(text)
            .setPositiveButton("Got it", null)
            .show()
    }
}
