package com.katsuyamaki.DroidOSLauncher

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    companion object {
        const val SELECTED_APP_PACKAGE = "com.katsuyamaki.DroidOSLauncher.SELECTED_APP_PACKAGE"
    }

    // === APP INFO DATA CLASS - START ===
    // Represents an installed app with package name, activity class, and state info
    // getIdentifier() returns a unique string for app identification including className when needed
    data class AppInfo(
        val label: String,
        val packageName: String,
        val className: String? = null,
        var isFavorite: Boolean = false,
        var isMinimized: Boolean = false
    ) {
        // Returns unique identifier for the app
        fun getIdentifier(): String {
            return if (!className.isNullOrEmpty() && packageName == "com.google.android.googlequicksearchbox") {
                if (className.lowercase().contains("assistant") || className.lowercase().contains("gemini")) {
                    "$packageName:gemini"
                } else {
                    packageName
                }
            } else {
                packageName
            }
        }
        
        // === GET BASE PACKAGE - START ===
        // Returns the base package name without any suffix
        // Use this for shell commands that need the actual Android package name
        fun getBasePackage(): String {
            return if (packageName.contains(":")) {
                packageName.substringBefore(":")
            } else {
                packageName
            }
        }
        // === GET BASE PACKAGE - END ===

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is AppInfo) return false
            return packageName == other.packageName && className == other.className && label == other.label
        }

        override fun hashCode(): Int {
            var result = packageName.hashCode()
            result = 31 * result + (className?.hashCode() ?: 0)
            result = 31 * result + label.hashCode()
            return result
        }
    }
    // === APP INFO DATA CLASS - END ===

    /* * FUNCTION: onCreate
     * SUMMARY: Detects the display ID where the app icon was clicked and
     * passes it to the service to ensure the bubble follows the user.
     */
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

        // Use commit() for synchronous save
        prefs.edit().putLong("last_launch", now).putInt("launch_count", count).commit()

        if (count >= 3) {
            // VIBRATE ALERT
            val v = getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                v.vibrate(android.os.VibrationEffect.createOneShot(500, 255))
            } else {
                @Suppress("DEPRECATION") v.vibrate(500)
            }

            Toast.makeText(this, "!!! DROIDOS RESET !!!", Toast.LENGTH_LONG).show()

            // 1. Reset State
            prefs.edit().clear().commit()

            // 2. Kill Service via Intent
            val stopIntent = Intent(this, FloatingLauncherService::class.java)
            stopService(stopIntent)

            // 3. Force Kill Process
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(0)
            }, 500)

            finish()
            return
        }

        // [NEW] Wallpaper Mode for Virtual Displays
        // Used as a "Desktop Background" to hold focus when apps are minimized.
        if (intent.getBooleanExtra("WALLPAPER_MODE", false)) {
            val view = android.view.View(this)
            view.setBackgroundColor(android.graphics.Color.BLACK)
            
            // SAFETY 1: Close immediately if tapped (Fail-safe)
            view.setOnClickListener { finish() }
            
            // SAFETY 2: Close immediately if we land on a Physical Display (0 or 1)
            // This handles cases where Android moves the activity when glasses are unplugged.
            val checkDisplay = {
                val currentDisplay = display // Capture 'this.display' into a local variable
                if (currentDisplay != null && currentDisplay.displayId < 2) {

                    finish()
                }
            }

            // Check on load
            checkDisplay()
            
            // Check whenever window focus changes (e.g. display move)
            view.viewTreeObserver.addOnWindowFocusChangeListener { hasFocus ->
                if (hasFocus) {
                    checkDisplay()
                }
            }
            
            setContentView(view)
            return
        }

        // Redirect to PermissionActivity if essential permissions are missing
        if (!hasAllPermissions()) {
            val intent = Intent(this, PermissionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // Determine which display this activity is running on
        val displayId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display?.displayId ?: 0
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.displayId
        }



        // Start service and pass the current display ID to recall the bubble
        val serviceIntent = Intent(this, FloatingLauncherService::class.java)
        serviceIntent.putExtra("DISPLAY_ID", displayId)
        startService(serviceIntent)

        // Finish immediately so the launcher remains a service-only overlay
        finish()
    }

    private fun hasAllPermissions(): Boolean {
        // 1. Overlay
        if (!Settings.canDrawOverlays(this)) return false

        // 2. Shizuku
        val shizukuGranted = try {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
        if (!shizukuGranted) return false

        // 3. Accessibility
        if (!isAccessibilityServiceEnabled(this, FloatingLauncherService::class.java)) return false

        // 4. Notifications removed (Not strictly required for service to run)

        return true
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
