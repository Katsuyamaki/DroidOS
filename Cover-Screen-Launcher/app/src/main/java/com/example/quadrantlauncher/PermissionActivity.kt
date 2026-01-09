package com.example.quadrantlauncher

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
            .setMessage("This app uses the Accessibility Service API to display floating windows and perform global actions (like Home/Back) on top of other apps.\n\n" +
                        "No data is collected, stored, or shared. This permission is strictly used for the launcher functionality.")
            .setPositiveButton("Agree & Grant") { _, _ ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                Toast.makeText(this, "Find 'Quadrant Launcher' and enable it", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Not Now", null)
            .show()
    }

    override fun onResume() {
        super.onResume() 
        refreshUI()
    }

    override fun onDestroy() {
        super.onDestroy() 
        Shizuku.removeRequestPermissionResultListener(this)
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        refreshUI()
    }

    private fun refreshUI() {
        val hasOverlay = Settings.canDrawOverlays(this)
        val hasShizuku = try { Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED } catch(e: Exception) { false }
        val hasAccessibility = isAccessibilityServiceEnabled(this, FloatingLauncherService::class.java)
        
        updateItem(btnGrantOverlay, iconOverlay, hasOverlay)
        updateItem(btnGrantShizuku, iconShizuku, hasShizuku)
        updateItem(btnGrantAccessibility, iconAccessibility, hasAccessibility)

        if (hasOverlay && hasShizuku && hasAccessibility) {
            btnContinue.isEnabled = true
            btnContinue.alpha = 1.0f
            btnContinue.text = "Start Launcher"
        } else {
            btnContinue.isEnabled = false
            btnContinue.alpha = 0.5f
            btnContinue.text = "Grant Permissions to Continue"
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
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val serviceInfo = enabledService.resolveInfo.serviceInfo
            if (serviceInfo.packageName == context.packageName && serviceInfo.name == service.name) {
                return true
            }
        }
        return false
    }
}