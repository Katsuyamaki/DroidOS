package com.katsuyamaki.DroidOSLauncher

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import rikka.shizuku.Shizuku

class PermissionActivity : Activity(), Shizuku.OnRequestPermissionResultListener {

    private lateinit var btnGrantOverlay: LinearLayout
    private lateinit var btnGrantRestricted: LinearLayout  // NEW: App Info button
    private lateinit var btnGrantShizuku: LinearLayout
    private lateinit var btnGrantAccessibility: LinearLayout
    
    private lateinit var iconOverlay: ImageView
    private lateinit var iconRestricted: ImageView  // NEW: Restricted settings icon
    private lateinit var iconShizuku: ImageView
    private lateinit var iconAccessibility: ImageView
    
    private lateinit var btnContinue: Button

    private val uiHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private val statusChecker = object : Runnable {
        override fun run() {
            refreshUI()
            uiHandler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        // Bind Views
        btnGrantOverlay = findViewById(R.id.btn_perm_overlay)
        btnGrantRestricted = findViewById(R.id.btn_perm_restricted)  // NEW
        btnGrantShizuku = findViewById(R.id.btn_perm_shizuku)
        btnGrantAccessibility = findViewById(R.id.btn_perm_accessibility)
        
        iconOverlay = findViewById(R.id.icon_status_overlay)
        iconRestricted = findViewById(R.id.icon_status_restricted)  // NEW
        iconShizuku = findViewById(R.id.icon_status_shizuku)
        iconAccessibility = findViewById(R.id.icon_status_accessibility)
        
        btnContinue = findViewById(R.id.btn_continue)

        // --- 1. OVERLAY PERMISSION ---
        btnGrantOverlay.setOnClickListener {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 101)
        }

        // =======================================================================
        // BUTTON: App Info / Restricted Settings
        // SUMMARY: Opens the App Info page where the user can tap the 3-dot
        //          hamburger menu (top right) and select "Allow Restricted Settings".
        //          This is required on Android 13+ for sideloaded APKs to enable
        //          accessibility services.
        // =======================================================================
        btnGrantRestricted.setOnClickListener {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
                Toast.makeText(
                    this,
                    "Tap ⋮ menu (top right) → 'Allow Restricted Settings'",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Could not open App Info", Toast.LENGTH_SHORT).show()
            }
        }
        // =======================================================================
        // END BUTTON: App Info / Restricted Settings
        // =======================================================================

        // --- 2. SHIZUKU PERMISSION ---
        btnGrantShizuku.setOnClickListener {
            try {
                if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                    Shizuku.requestPermission(0)
                } else {
                    Toast.makeText(this, "Shizuku already granted", Toast.LENGTH_SHORT).show()
                    refreshUI()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Shizuku not running. Please start Shizuku first.", Toast.LENGTH_LONG).show()
            }
        }

        // --- 3. ACCESSIBILITY PERMISSION (With Disclosure) ---
        btnGrantAccessibility.setOnClickListener {
            showAccessibilityDisclosure()
        }

        btnContinue.setOnClickListener {
            if (hasAllPermissions()) {
                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            }
        }

        Shizuku.addRequestPermissionResultListener(this)
    }

    private fun showAccessibilityDisclosure() {
        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Accessibility Service Required")
            .setMessage("DroidOS Launcher uses AccessibilityService for active-window detection, hotkey handling, and window automation actions used by tiling/taskbar workflows (including global actions such as Home/Back).\n\n" +
                        "DroidOS does not transmit personal data to external servers. Configuration and automation state are stored locally on-device.")
            .setPositiveButton("Agree & Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                Toast.makeText(this, "Find 'DroidOS Launcher' and enable it", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("No Thanks", null)
            .show()
    }



    override fun onResume() {
        super.onResume()
        refreshUI()
        uiHandler.post(statusChecker) // Start polling
    }

    override fun onPause() {
        super.onPause()
        uiHandler.removeCallbacks(statusChecker) // Stop polling
    }

    override fun onDestroy() {
        super.onDestroy()
        uiHandler.removeCallbacks(statusChecker)
        Shizuku.removeRequestPermissionResultListener(this)
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        refreshUI()
    }

    private fun refreshUI() {
        val hasOverlay = Settings.canDrawOverlays(this)
        val hasShizuku = try { Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED } catch(e: Exception) { false }
        val hasAccessibility = isAccessibilityServiceEnabled(this, FloatingLauncherService::class.java)
        
        // 1. Restricted Settings (Implicit check: if accessibility is on, restricted is done)
        if (hasAccessibility) {
            updateItem(btnGrantRestricted, iconRestricted, true)
            iconRestricted.setColorFilter(Color.GREEN)
        } else {
            // Can't check explicitly, so we leave it clickable but don't mark green
            updateItem(btnGrantRestricted, iconRestricted, false)
        }

        // 2. Overlay
        updateItem(btnGrantOverlay, iconOverlay, hasOverlay)
        
        // 3. Accessibility
        updateItem(btnGrantAccessibility, iconAccessibility, hasAccessibility)
        
        // 4. Shizuku
        updateItem(btnGrantShizuku, iconShizuku, hasShizuku)

        // ENABLE BUTTON LOGIC
        if (hasOverlay && hasShizuku && hasAccessibility) {
            btnContinue.isEnabled = true
            btnContinue.alpha = 1.0f
            btnContinue.text = "LAUNCH DROIDOS"
            btnContinue.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#3DDC84"))
        } else {
            btnContinue.isEnabled = false
            btnContinue.alpha = 0.5f
            
            // Helpful text based on what is missing
            btnContinue.text = when {
                !hasOverlay -> "Step 2: Enable Overlay"
                !hasAccessibility -> "Step 3: Enable Accessibility"
                !hasShizuku -> "Step 4: Grant Shizuku"
                else -> "Grant Permissions to Continue"
            }
        }
    }

    private fun updateItem(container: LinearLayout, icon: ImageView, granted: Boolean) {
        if (granted) {
            icon.setImageResource(android.R.drawable.checkbox_on_background)
            icon.setColorFilter(Color.GREEN)
            container.isClickable = false
            container.alpha = 0.6f
        } else {
            icon.setImageResource(android.R.drawable.checkbox_off_background)
            icon.setColorFilter(Color.RED)
            container.isClickable = true
            container.alpha = 1.0f
        }
    }

    private fun hasAllPermissions(): Boolean {
        val hasOverlay = Settings.canDrawOverlays(this)
        val hasShizuku = try { Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED } catch(e: Exception) { false }
        val hasAccessibility = isAccessibilityServiceEnabled(this, FloatingLauncherService::class.java)
        return hasOverlay && hasShizuku && hasAccessibility
    }

    private fun isAccessibilityServiceEnabled(context: Context, service: Class<*>): Boolean {
        try {
            val expectedComponentName = android.content.ComponentName(context, service)
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            
            val stringSplitter = android.text.TextUtils.SimpleStringSplitter(':')
            stringSplitter.setString(enabledServicesSetting)
            
            while (stringSplitter.hasNext()) {
                val componentNameString = stringSplitter.next()
                val enabledComponent = android.content.ComponentName.unflattenFromString(componentNameString)
                if (enabledComponent != null && enabledComponent == expectedComponentName) return true
            }
        } catch (e: Exception) {

        }
        return false
    }
}
