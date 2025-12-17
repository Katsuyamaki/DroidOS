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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity(), Shizuku.OnRequestPermissionResultListener {

    // UI References
    private lateinit var tvStep1: TextView
    private lateinit var tvStep2: TextView
    private lateinit var tvStep3: TextView
    private lateinit var tvStep4: TextView
    private lateinit var btnStart: Button
    private lateinit var btnSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Bind Views
        tvStep1 = findViewById(R.id.tvStep1Title)
        tvStep2 = findViewById(R.id.tvStep2Title)
        tvStep3 = findViewById(R.id.tvStep3Title)
        tvStep4 = findViewById(R.id.tvStep4Title)
        
        val btnRestricted = findViewById<Button>(R.id.btnStep1Restricted)
        val btnAccessibility = findViewById<Button>(R.id.btnStep2Accessibility)
        val btnOverlay = findViewById<Button>(R.id.btnStep3Overlay)
        val btnShizuku = findViewById<Button>(R.id.btnStep4Shizuku)
        
        btnStart = findViewById(R.id.btnStartApp)
        btnSettings = findViewById(R.id.btnOpenSettings)

        // Step 1: Restricted Settings (App Info)
        btnRestricted.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        // Step 2: Accessibility
        btnAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        // Step 3: Overlay
        btnOverlay.setOnClickListener {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
        }

        // Step 4: Shizuku
        Shizuku.addRequestPermissionResultListener(this)
        btnShizuku.setOnClickListener {
            if (Shizuku.getBinder() == null) {
                Toast.makeText(this, "Shizuku not running!", Toast.LENGTH_SHORT).show()
            } else {
                Shizuku.requestPermission(0)
            }
        }

        // LAUNCH BUTTON: Starts Service (Clean Launch)
        btnStart.setOnClickListener {
            if (checkCriticalPermissions()) {
                val intent = Intent(this, OverlayService::class.java)
                // No specific action -> Just start/restart the service
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
                finish()
            }
        }

        // SETTINGS BUTTON: Starts Service AND Opens Menu (Previous Launch Logic)
        btnSettings.setOnClickListener {
            if (checkCriticalPermissions()) {
                val intent = Intent(this, OverlayService::class.java)
                intent.action = "OPEN_MENU" // Forces the settings menu to open
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatusUI()
    }

    private fun updateStatusUI() {
        // 1. Accessibility Check
        val accessGranted = isAccessibilityEnabled()
        val accessSymbol = if (accessGranted) "✅" else "❌"
        
        tvStep1.text = "Step 1: Unblock Restricted Settings  $accessSymbol"
        tvStep2.text = "Step 2: Accessibility Service  $accessSymbol"

        // 2. Overlay Check
        val overlayGranted = Settings.canDrawOverlays(this)
        val overlaySymbol = if (overlayGranted) "✅" else "❌"
        tvStep3.text = "Step 3: Overlay Permission  $overlaySymbol"

        // 3. Shizuku Check
        val shizukuGranted = try {
            Shizuku.getBinder() != null && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) { false }
        val shizukuSymbol = if (shizukuGranted) "✅" else "❌"
        tvStep4.text = "Step 4: Shizuku Permission  $shizukuSymbol"

        // Enable Launch buttons only if Overlay is granted
        val ready = overlayGranted
        btnStart.isEnabled = ready
        btnSettings.isEnabled = ready
    }

    private fun isAccessibilityEnabled(): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (service in enabledServices) {
            if (service.id.contains(packageName)) {
                return true
            }
        }
        return false
    }

    private fun checkCriticalPermissions(): Boolean {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Overlay Permission is required!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            updateStatusUI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(this)
    }
}