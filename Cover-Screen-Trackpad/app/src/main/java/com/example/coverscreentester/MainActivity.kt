package com.example.coverscreentester

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity(), Shizuku.OnRequestPermissionResultListener {

    private lateinit var statusText: TextView
    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // UI References
        val btnRestricted = findViewById<Button>(R.id.btnStep1Restricted)
        val btnAccessibility = findViewById<Button>(R.id.btnStep2Accessibility)
        val btnOverlay = findViewById<Button>(R.id.btnStep3Overlay)
        val btnShizuku = findViewById<Button>(R.id.btnStep4Shizuku)
        
        btnStart = findViewById(R.id.btnStartApp)
        statusText = findViewById(R.id.shizukuStatus)

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
        // Ensure listener is registered immediately
        Shizuku.addRequestPermissionResultListener(this)
        
        btnShizuku.setOnClickListener {
            if (Shizuku.getBinder() == null) {
                statusText.text = "Shizuku not running!"
                Toast.makeText(this, "Please start Shizuku app first", Toast.LENGTH_LONG).show()
            } else {
                Shizuku.requestPermission(0)
            }
        }

        // Start App
        btnStart.setOnClickListener {
            if (checkCriticalPermissions()) {
                startTrackpadService()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatusUI()
    }

    private fun updateStatusUI() {
        // Check Shizuku
        val shizukuOk = try {
            Shizuku.getBinder() != null && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) { false }
        
        if (shizukuOk) {
            statusText.text = "Shizuku: GRANTED"
            statusText.setTextColor(getColor(android.R.color.holo_green_light))
        } else {
            statusText.text = "Shizuku: Not Granted"
            statusText.setTextColor(getColor(android.R.color.holo_orange_light))
        }

        // Enable Start button only if Overlay is granted (Absolute minimum)
        // Accessibility is harder to check strictly without loop, but we can warn.
        btnStart.isEnabled = Settings.canDrawOverlays(this)
    }

    private fun checkCriticalPermissions(): Boolean {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Step 3 (Overlay) is required!", Toast.LENGTH_SHORT).show()
            return false
        }
        // Optional: Check Accessibility enabled via specific service check if desired
        // For now, we rely on user confirmation or app behavior
        return true
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            updateStatusUI()
            Toast.makeText(this, "Shizuku Granted!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTrackpadService() {
        val intent = Intent(this, OverlayService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        finish() // Minimize to background
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(this)
    }
}