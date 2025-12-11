# Code Update: Robust Landing Page & Crash Prevention

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/MainActivity.kt

### Description
Rewrites `MainActivity` to safely handle Shizuku permission checks. If the Shizuku service is dead (e.g., after a crash or reboot), standard permission checks throw a `RuntimeException`, causing the app to crash on launch. This update catches those errors, treats them as "Permission Missing," and forces the Landing Page to open so the user can fix it.

### Code
Replace the **entire file content** with this safer implementation:

```kotlin
package com.example.coverscreentester

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Display
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Safe Permission Checks (Prevents crash if Shizuku is dead)
        val isShizukuReady = safeCheckShizuku()
        val isAccessibilityReady = isAccessibilityEnabled()

        // 2. Decide: Auto-Start or Show UI?
        // Only run headless if EVERYTHING is perfectly ready.
        if (isShizukuReady && isAccessibilityReady) {
            startOverlayService()
            finish()
            return
        }

        // 3. Setup Mode: Show UI if any permission is missing or service crashed
        setContentView(R.layout.activity_main)
        setupUI(isShizukuReady, isAccessibilityReady)
    }

    private fun safeCheckShizuku(): Boolean {
        return try {
            // Check binder first to avoid exceptions
            if (!Shizuku.pingBinder()) return false
            // Then check permission
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            // If Shizuku throws (e.g. IllegalStateException), assume not ready
            false
        }
    }

    private fun setupUI(isShizukuReady: Boolean, isAccessibilityReady: Boolean) {
        // Status Text
        val statusText = findViewById<TextView>(R.id.text_status)
        updateStatusText(statusText, isShizukuReady, isAccessibilityReady)

        // Fix Permissions Button
        findViewById<Button>(R.id.btn_app_info).setOnClickListener {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                Toast.makeText(this, "Go to: 3 Dots (Top Right) > Allow Restricted Settings", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error opening App Info", Toast.LENGTH_SHORT).show()
            }
        }

        // Request Shizuku if missing (safely)
        if (!isShizukuReady) {
            try {
                Shizuku.requestPermission(0)
            } catch (e: Exception) {
                // Ignore errors if Shizuku app isn't even installed
            }
        }

        // Toggle/Enable Button Logic
        findViewById<Button>(R.id.btn_toggle).setOnClickListener {
            if (!isAccessibilityEnabled()) {
                Toast.makeText(this, "Please Enable Accessibility Service", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } else if (!safeCheckShizuku()) {
                 Toast.makeText(this, "Shizuku not ready. Open Shizuku app.", Toast.LENGTH_LONG).show()
            } else {
                startOverlayService()
                Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        
        // Wire up other buttons
        findViewById<Button>(R.id.btn_close).setOnClickListener { finish() }
        
        // Disable features until ready
        val ready = isShizukuReady && isAccessibilityReady
        findViewById<Button>(R.id.btn_profiles).isEnabled = ready
        findViewById<Button>(R.id.btn_settings).isEnabled = ready
        
        findViewById<Button>(R.id.btn_help).setOnClickListener { 
            if(!ready) Toast.makeText(this, "Please enable permissions first.", Toast.LENGTH_SHORT).show() 
        }
    }

    private fun updateStatusText(textView: TextView, shizukuOK: Boolean, accessOK: Boolean) {
        if (!shizukuOK) {
            textView.text = "Status: Shizuku Not Running/Permitted"
            textView.setTextColor(0xFFFFFF00.toInt()) // Yellow
        } else if (!accessOK) {
            textView.text = "Status: Accessibility Service Disabled"
            textView.setTextColor(0xFFFF0000.toInt()) // Red
        } else {
            textView.text = "Status: Ready"
            textView.setTextColor(0xFF00FF00.toInt()) // Green
        }
    }

    private fun isAccessibilityEnabled(): Boolean {
        val expectedComponentName = ComponentName(this, OverlayService::class.java)
        val enabledServicesSetting = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        
        val colonSplitter = android.text.TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledComponent = ComponentName.unflattenFromString(componentNameString)
            if (enabledComponent != null && enabledComponent == expectedComponentName) {
                return true
            }
        }
        return false
    }

    private fun startOverlayService() {
        var targetDisplayId = Display.DEFAULT_DISPLAY
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                display?.let { targetDisplayId = it.displayId }
            } else {
                @Suppress("DEPRECATION")
                targetDisplayId = windowManager.defaultDisplay.displayId
            }
        } catch (e: Exception) { e.printStackTrace() }

        val intent = Intent(this, OverlayService::class.java).apply {
            action = "OPEN_MENU"
            putExtra("DISPLAY_ID", targetDisplayId)
            putExtra("FORCE_MOVE", true)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
