package com.example.coverscreentester

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

    private lateinit var btnFixRestricted: Button
    private lateinit var btnOpenAccessibility: Button
    private lateinit var btnStartCheck: Button
    private lateinit var btnSwitchDisplay: Button
    
    // Track if we've already initialized the UI
    private var uiInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check permissions FIRST before deciding what to do
        if (checkCriticalPermissions()) {
            // All permissions granted - skip UI and launch/recall service
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
        
        btnFixRestricted = findViewById(R.id.btn_fix_restricted)
        btnOpenAccessibility = findViewById(R.id.btn_open_accessibility)
        btnStartCheck = findViewById(R.id.btn_start_check)
        btnSwitchDisplay = findViewById(R.id.btn_switch_display)
        
        // Register Shizuku listener
        Shizuku.addRequestPermissionResultListener(this)
        
        // Setup click listeners
        btnFixRestricted.setOnClickListener { 
            openAppInfo()
        }
        
        btnOpenAccessibility.setOnClickListener { 
            openAccessibilitySettings() 
        }
        
        btnStartCheck.setOnClickListener {
            // First check Shizuku
            if (!isShizukuReady()) {
                requestShizukuPermission()
                return@setOnClickListener
            }
            
            if (checkCriticalPermissions()) {
                launchOverlayServiceAndFinish()
            } else {
                showMissingPermissionsToast()
            }
        }
        
        btnSwitchDisplay.setOnClickListener {
            // Send broadcast to switch display if service is running
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "SWITCH_DISPLAY"
            startService(intent)
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
        val isOverlayReady = Settings.canDrawOverlays(this)
        val shizukuReady = isShizukuReady()
        
        // Update accessibility button color
        if (isAccessibilityReady) {
            btnOpenAccessibility.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF4CAF50.toInt()) // Green
            btnOpenAccessibility.text = "2. Accessibility ✓"
        } else {
            btnOpenAccessibility.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF3DDC84.toInt())
            btnOpenAccessibility.text = "2. Enable Accessibility"
        }
        
        // Update start button based on all permissions
        if (isAccessibilityReady && isOverlayReady && shizukuReady) {
            btnStartCheck.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF4CAF50.toInt()) // Green
            btnStartCheck.text = "LAUNCH TRACKPAD"
        } else if (!shizukuReady) {
            btnStartCheck.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFFF9800.toInt()) // Orange
            btnStartCheck.text = "3. GRANT SHIZUKU"
        } else {
            btnStartCheck.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF2196F3.toInt()) // Blue
            btnStartCheck.text = "3. CHECK PERMISSIONS & START"
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
        // Robust display ID detection
        val displayId = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            this.display?.displayId ?: 0
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.displayId
        }

        android.util.Log.d("DroidOS_Trackpad", "MainActivity: Triggering recall to Display $displayId")

        val intent = Intent(this, OverlayService::class.java)
        intent.putExtra("displayId", displayId)
        intent.putExtra("isRecall", true)
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        finish()
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
        // 1. Overlay
        if (!Settings.canDrawOverlays(this)) {
            return false
        }

        // 2. Accessibility
        if (!isAccessibilityEnabled()) {
            return false
        }

        // 3. Shizuku
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
            !Settings.canDrawOverlays(this) -> {
                Toast.makeText(this, "Missing: Overlay Permission (tap 'Allow Restricted Settings')", Toast.LENGTH_LONG).show()
            }
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
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (service in enabledServices) {
            if (service.resolveInfo.serviceInfo.packageName == packageName) {
                return true
            }
        }
        return false
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
            Toast.makeText(this, "Tap 'Allow restricted settings' at the bottom", Toast.LENGTH_LONG).show()
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