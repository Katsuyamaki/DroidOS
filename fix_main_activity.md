# Code Update: Force UI Display & Safe Permission Checks

## File: Cover-Screen-Trackpad/app/src/main/java/com/example/coverscreentester/MainActivity.kt

### Description
Rewrites `MainActivity` to prioritize user control and stability.
1.  **Always Shows UI**: Removes the logic that `finish()`es the activity immediately if permissions are granted.
2.  **Safe Checks**: Prevents `IllegalStateException` crashes by wrapping Shizuku checks.
3.  **Reactive Status**: Updates the "Status" text dynamically when permissions change.

### Code
Replace the **entire file content** with:

```kotlin
package com.example.coverscreentester

import android.content.ComponentName
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

class MainActivity : AppCompatActivity(), Shizuku.OnBinderReceivedListener, Shizuku.OnRequestPermissionResultListener {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Initialize UI Elements
        statusText = findViewById(R.id.text_status)
        val btnAppInfo = findViewById<Button>(R.id.btn_app_info)
        val btnToggle = findViewById<Button>(R.id.btn_toggle)
        val btnClose = findViewById<Button>(R.id.btn_close)
        val btnHelp = findViewById<Button>(R.id.btn_help)
        
        // 2. Setup Listeners
        // Safe registration of Shizuku listeners
        try {
            Shizuku.addBinderReceivedListener(this)
            Shizuku.addRequestPermissionResultListener(this)
        } catch (e: Throwable) {
            // Shizuku not installed or failed to hook
        }

        // Fix Permissions Button
        btnAppInfo.setOnClickListener {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                Toast.makeText(this, "Enable 'Allow Restricted Settings' here if needed.", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error opening App Info", Toast.LENGTH_SHORT).show()
            }
        }

        // Toggle/Start Button
        btnToggle.setOnClickListener {
            if (!isAccessibilityEnabled()) {
                Toast.makeText(this, "Please Enable Accessibility Service", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } else if (!safeCheckShizuku()) {
                requestShizukuSafely()
            } else {
                startOverlayService()
                Toast.makeText(this, "Service Starting...", Toast.LENGTH_SHORT).show()
                // We do NOT finish() here, so user can see if it actually works.
            }
        }

        btnClose.setOnClickListener { finish() }
        btnHelp.setOnClickListener { 
            Toast.makeText(this, "Check Status above. Green = Ready.", Toast.LENGTH_SHORT).show() 
        }

        // 3. Initial Status Check
        updateStatus()
        
        // 4. Auto-request Shizuku if missing (but safe)
        if (!safeCheckShizuku()) {
            requestShizukuSafely()
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            Shizuku.removeBinderReceivedListener(this)
            Shizuku.removeRequestPermissionResultListener(this)
        } catch (e: Throwable) {}
    }

    // --- Shizuku Callbacks ---
    override fun onBinderReceived() {
        updateStatus()
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        updateStatus()
    }

    // --- Helper Functions ---

    private fun updateStatus() {
        val isShizukuReady = safeCheckShizuku()
        val isAccessReady = isAccessibilityEnabled()

        if (!isShizukuReady) {
            statusText.text = "Status: Shizuku Not Ready"
            statusText.setTextColor(0xFFFFFF00.toInt()) // Yellow
        } else if (!isAccessReady) {
            statusText.text = "Status: Accessibility Service Disabled"
            statusText.setTextColor(0xFFFF0000.toInt()) // Red
        } else {
            statusText.text = "Status: Ready to Launch"
            statusText.setTextColor(0xFF00FF00.toInt()) // Green
        }
    }

    private fun safeCheckShizuku(): Boolean {
        return try {
            if (!Shizuku.pingBinder()) return false
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Throwable) {
            false
        }
    }

    private fun requestShizukuSafely() {
        try {
            if (Shizuku.pingBinder() && Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                Shizuku.requestPermission(0)
            }
        } catch (e: Throwable) {
            // Ignore
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
