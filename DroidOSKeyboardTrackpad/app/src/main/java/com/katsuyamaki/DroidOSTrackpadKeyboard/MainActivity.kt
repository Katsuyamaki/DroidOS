package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.Toast
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

// =================================================================================
// MAINACTIVITY: Permission Landing Page and Service Launcher
// SUMMARY: This activity displays permission status and launches the OverlayService
//          once all required permissions are granted. On subsequent launches where
//          permissions are already granted, it skips the UI and goes directly to
//          recalling/launching the service.
// =================================================================================

class MainActivity : AppCompatActivity(), Shizuku.OnRequestPermissionResultListener {

    // Wizard UI Elements
    private lateinit var containerRestricted: android.widget.LinearLayout
    private lateinit var containerAccessibility: android.widget.LinearLayout
    private lateinit var containerShizuku: android.widget.LinearLayout

    private lateinit var iconRestricted: android.widget.ImageView
    private lateinit var iconAccessibility: android.widget.ImageView
    private lateinit var iconShizuku: android.widget.ImageView

    private lateinit var btnLaunch: android.widget.Button
    
    // Track if we've already initialized the UI
    private var uiInitialized = false

override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [SAFETY] PANIC RESET: Tap App Icon 3 times in 5 seconds to Reset
        val prefs = getSharedPreferences("PanicState", Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val lastLaunch = prefs.getLong("last_launch", 0)
        var count = prefs.getInt("launch_count", 0)

        if (now - lastLaunch < 5000) {
            count++
        } else {
            count = 1
        }
        prefs.edit().putLong("last_launch", now).putInt("launch_count", count).commit()

        if (count >= 3) {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                v.vibrate(android.os.VibrationEffect.createOneShot(500, 255))
            } else { @Suppress("DEPRECATION") v.vibrate(500) }

            Toast.makeText(this, "!!! TRACKPAD RESET !!!", Toast.LENGTH_LONG).show()

            // Send Force Reset Broadcast (Unblocks Keyboard, Wakes Screen)
            val resetIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.SOFT_RESTART")
            sendBroadcast(resetIntent)

            val stopIntent = Intent(this, OverlayService::class.java)
            stopService(stopIntent)

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(0)
            }, 500)

            finish()
            return
        }

        // DEBUG LOG
        val dId = intent.getIntExtra("displayId", -999)
        val force = intent.getBooleanExtra("force_start", false)
        val isRestart = intent.getBooleanExtra("IS_RESTART", false)
        android.util.Log.w("MainActivity", ">>> ON CREATE | Display: $dId | Force: $force | IsRestart: $isRestart <<<")
        
        if (dId != -999) {
            Toast.makeText(this, "Activity Woke: D$dId", Toast.LENGTH_SHORT).show()
        }
        
        // [FIX] Check for Force Start flag from Launcher (Hard Restart)
        // If this flag is present, we assume the Launcher has already handled 
        // permissions/shizuku and we should just start the service immediately.
        val isForceStart = intent.getBooleanExtra("force_start", false)
        
        // Check permissions OR Force Start flag
        if (isForceStart || checkCriticalPermissions()) {
            // All permissions granted (or forced) - skip UI and launch/recall service
            launchOverlayServiceAndFinish()
            return
        }
        
        // Permissions not granted - show the permission landing page
        initializePermissionUI()
    }
    
    // =================================================================================
    // FUNCTION: initializePermissionUI
    // SUMMARY: Sets up the permission status UI with buttons matching the actual layout.
    //          Only called when permissions are missing on first launch.
    // =================================================================================
    private fun initializePermissionUI() {
        setContentView(R.layout.activity_main)
        uiInitialized = true

        // Bind Views
        containerRestricted = findViewById(R.id.btn_fix_restricted)
        containerAccessibility = findViewById(R.id.btn_open_accessibility)
        containerShizuku = findViewById(R.id.btn_start_check)

        iconRestricted = findViewById(R.id.icon_restricted)
        iconAccessibility = findViewById(R.id.icon_accessibility)
        iconShizuku = findViewById(R.id.icon_shizuku)

        btnLaunch = findViewById(R.id.btn_switch_display)

        // Register Shizuku listener
        Shizuku.addRequestPermissionResultListener(this)

        // Click Listeners
        containerRestricted.setOnClickListener { openAppInfo() }
        containerAccessibility.setOnClickListener { showAccessibilityDisclosure() }



        containerShizuku.setOnClickListener {
            if (!isShizukuReady()) {
                requestShizukuPermission()
            } else {
                Toast.makeText(this, "Shizuku already active", Toast.LENGTH_SHORT).show()
                updateButtonStates()
            }
        }

        btnLaunch.setOnClickListener {
            // Final check
            if (checkCriticalPermissions()) {
                launchOverlayServiceAndFinish()
            } else {
                showMissingPermissionsToast()
            }
        }

        updateButtonStates()
    }
    // =================================================================================
    // END FUNCTION: initializePermissionUI
    // =================================================================================

    override fun onResume() {
        super.onResume()
        
        // Only update UI if we're showing the permission page
        if (uiInitialized) {
            // Check again in case user granted permissions and came back
            if (checkCriticalPermissions()) {
                launchOverlayServiceAndFinish()
                return
            }
            updateButtonStates()
        }
    }

    // =================================================================================
    // FUNCTION: updateButtonStates
    // SUMMARY: Updates button colors/states based on current permission status.
    // =================================================================================
    private fun updateButtonStates() {
        if (!uiInitialized) return

        val isAccessibilityReady = isAccessibilityEnabled()
        val isShizukuReady = isShizukuReady()

        // Helper to update row visual state
        fun updateRow(container: android.widget.LinearLayout, icon: android.widget.ImageView, isDone: Boolean) {
            if (isDone) {
                icon.setImageResource(android.R.drawable.checkbox_on_background)
                icon.setColorFilter(android.graphics.Color.GREEN)
                container.alpha = 0.5f // Dim when done
            } else {
                icon.setImageResource(android.R.drawable.checkbox_off_background)
                icon.setColorFilter(android.graphics.Color.RED)
                container.alpha = 1.0f
            }
        }

        // 1. Restricted Settings (Implicit: if Accessibility works, this is done)
        updateRow(containerRestricted, iconRestricted, isAccessibilityReady)
        if (isAccessibilityReady) iconRestricted.setColorFilter(android.graphics.Color.GREEN)

        // 2. Accessibility
        updateRow(containerAccessibility, iconAccessibility, isAccessibilityReady)

        // 3. Shizuku
        updateRow(containerShizuku, iconShizuku, isShizukuReady)

        // 4. Launch Button State
        if (isAccessibilityReady && isShizukuReady) {
            btnLaunch.isEnabled = true
            btnLaunch.alpha = 1.0f
            btnLaunch.text = "LAUNCH TRACKPAD"
            btnLaunch.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#3DDC84"))
        } else {
            btnLaunch.isEnabled = false
            btnLaunch.alpha = 0.5f

            // Helpful Guide Text
            btnLaunch.text = when {
                !isAccessibilityReady -> "Step 2: Enable Accessibility"
                !isShizukuReady -> "Step 3: Grant Shizuku"
                else -> "Grant Permissions First"
            }
        }
    }
    // =================================================================================
    // END FUNCTION: updateButtonStates
    // =================================================================================

    // =================================================================================
    // FUNCTION: launchOverlayServiceAndFinish
    // SUMMARY: Launches the OverlayService on the current display and finishes
    //          the activity. This is called when all permissions are granted.
    // =================================================================================



    private fun launchOverlayServiceAndFinish() {
        // 1. Create the Service Intent (Rename to 'serviceIntent' to avoid shadowing bug)
        val serviceIntent = Intent(this, OverlayService::class.java)
        
        // 2. Read from the Activity Intent (Use 'getIntent()' explicitly)
        val originIntent = getIntent()

        // [FIX] Forward the Display ID
        // We check 'originIntent' (from Launcher) and put into 'serviceIntent' (for Service)
        if (originIntent.hasExtra("displayId")) {
            val targetId = originIntent.getIntExtra("displayId", 0)
            serviceIntent.putExtra("displayId", targetId)
        }
        
        // Forward force_start flag
        if (originIntent.hasExtra("force_start")) {
             serviceIntent.putExtra("force_start", true)
        }

        // 3. Start Service
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // 4. Minimize to keep process alive
        moveTaskToBack(true)
        
        // 5. Finish after delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            finish()
        }, 1500)
    }



    // =================================================================================
    // END FUNCTION: launchOverlayServiceAndFinish
    // =================================================================================

    // =================================================================================
    // FUNCTION: checkCriticalPermissions
    // SUMMARY: Returns true only if ALL required permissions are granted.
    //          Does NOT show toasts - used for silent checking.
    // =================================================================================
    private fun checkCriticalPermissions(): Boolean {
        // [Fixed] Removed Overlay Permission check. 
        // Since we use TYPE_ACCESSIBILITY_OVERLAY, the separate 'Appear on Top' permission is not strictly required.
        
        // 1. Accessibility
        if (!isAccessibilityEnabled()) {
            return false
        }

        // 2. Shizuku
        if (!isShizukuReady()) {
            return false
        }

        return true
    }
    // =================================================================================
    // END FUNCTION: checkCriticalPermissions
    // =================================================================================

    // =================================================================================
    // FUNCTION: showMissingPermissionsToast
    // SUMMARY: Shows a specific toast about which permission is missing.
    // =================================================================================
    private fun showMissingPermissionsToast() {
        when {
            !isAccessibilityEnabled() -> {
                Toast.makeText(this, "Missing: Accessibility Service", Toast.LENGTH_SHORT).show()
            }
            !isShizukuReady() -> {
                Toast.makeText(this, "Missing: Shizuku Permission", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // =================================================================================
    // END FUNCTION: showMissingPermissionsToast
    // =================================================================================

    // =================================================================================
    // FUNCTION: isAccessibilityEnabled
    // SUMMARY: Checks if our accessibility service is enabled in system settings.
    // =================================================================================
    private fun isAccessibilityEnabled(): Boolean {
        // [FIX] Read Settings.Secure directly. 
        // AccessibilityManager only lists *running* services. After a force-stop, 
        // the service is not running yet, so AM returns false. 
        // But the *Permission* is still granted in Settings, so we check that source of truth.
        return try {
            val expectedServiceName = "$packageName/.OverlayService"
            val enabledServices = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: ""
            
            // Check if our service string exists in the setting
            val colonSplit = enabledServices.split(":")
            colonSplit.any { component ->
                component.equals(expectedServiceName, ignoreCase = true) || 
                component.contains("$packageName/")
            }
        } catch (e: Exception) {
            false
        }
    }
    // =================================================================================
    // END FUNCTION: isAccessibilityEnabled
    // =================================================================================
    
    // =================================================================================
    // FUNCTION: isShizukuReady
    // SUMMARY: Returns true if Shizuku is running and we have permission.
    // =================================================================================
    private fun isShizukuReady(): Boolean {
        return try {
            Shizuku.getBinder() != null && 
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }
    // =================================================================================
    // END FUNCTION: isShizukuReady
    // =================================================================================
    
    // =================================================================================
    // HELPER FUNCTIONS: Open Settings Pages
    // =================================================================================
    private fun showAccessibilityDisclosure() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Accessibility Service Disclosure")
            .setMessage("DroidOS Trackpad uses the AccessibilityService API to inject touch gestures (clicks and swipes) onto the screen and to monitor window focus for the 'Mouse Cursor' overlay.\n\n" +
                        "This allows you to control remote displays and apps using your phone as a trackpad. No personal data, keystrokes, or screen content is stored or transmitted.")
            .setPositiveButton("Agree") { _, _ ->
                openAccessibilitySettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAccessibilitySettings() {


        try {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open Accessibility settings", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openAppInfo() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
            Toast.makeText(this, "Tap 3 dot hamburger menu for 'Allow Restricted Settings' at top right corner. If not visible, enable at next step and return here.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open App Info", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun requestShizukuPermission() {
        try {
            if (Shizuku.getBinder() == null) {
                Toast.makeText(this, "Shizuku is not running. Please start Shizuku first.", Toast.LENGTH_LONG).show()
                return
            }
            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                Shizuku.requestPermission(0)
            } else {
                Toast.makeText(this, "Shizuku permission already granted", Toast.LENGTH_SHORT).show()
                updateButtonStates()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    // =================================================================================
    // END HELPER FUNCTIONS
    // =================================================================================

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Shizuku Granted!", Toast.LENGTH_SHORT).show()
            updateButtonStates()
            
            // Auto-launch if this was the last missing permission
            if (checkCriticalPermissions()) {
                launchOverlayServiceAndFinish()
            }
        } else {
            Toast.makeText(this, "Shizuku permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            Shizuku.removeRequestPermissionResultListener(this)
        } catch (e: Exception) {}
    }
}

// =================================================================================
// END FILE: MainActivity.kt
// =================================================================================
