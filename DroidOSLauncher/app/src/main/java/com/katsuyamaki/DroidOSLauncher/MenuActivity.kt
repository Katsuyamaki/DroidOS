package com.katsuyamaki.DroidOSLauncher

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import rikka.shizuku.Shizuku

class MenuActivity : Activity(), Shizuku.OnRequestPermissionResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- NEW: PERMISSION CHECK LANDING PAGE LOGIC ---
        if (!hasRequiredPermissions()) {
            val intent = Intent(this, PermissionActivity::class.java)
            startActivity(intent)
            finish() // Close MenuActivity so user can't go back without perms
            return
        }
        // ------------------------------------------------

        // 1. Shizuku Permission Check (Existing logic can remain or be removed as double-check)
        checkShizukuPermission() 

        // 2. Main Layout Container (Dark Theme)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setPadding(40, 60, 40, 40)
            setBackgroundColor(Color.parseColor("#1E1E1E")) // Dark Background
            gravity = Gravity.TOP
        }

        // --- TITLE HEADER ---
        val headerText = TextView(this).apply {
            text = "CoverScreen Launcher"
            textSize = 22f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                bottomMargin = 50
            }
        }
        mainLayout.addView(headerText)

        // --- PROFILE ROW (Horizontal: Text + Save Icon) ---
        val profileRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                bottomMargin = 60
            }
            gravity = Gravity.CENTER_VERTICAL
            // Add a subtle background to the row to make it distinct
            setBackgroundColor(Color.parseColor("#2D2D2D"))
            setPadding(20, 20, 20, 20)
        }

        // Profile Text (Left side)
        val profileText = TextView(this).apply {
            text = "Current: Default" 
            textSize = 18f
            setTextColor(Color.WHITE)
            // Weight 1 pushes the icon to the far right. 
            // Change to 0 and WRAP_CONTENT if you want icon immediately next to text.
            layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f) 
        }

        // Save Icon (Right side)
        val saveBtn = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_save)
            setBackgroundColor(Color.TRANSPARENT) // Transparent bg
            setColorFilter(Color.CYAN) // Cyan tint to make it pop
            setPadding(20, 0, 0, 0)
            setOnClickListener {
                Toast.makeText(this@MenuActivity, "Profile Saved (Placeholder)", Toast.LENGTH_SHORT).show()
                // TODO: Connect to AppPreferences.saveProfile logic
            }
        }

        profileRow.addView(profileText)
        profileRow.addView(saveBtn)
        mainLayout.addView(profileRow)

        // --- LAUNCHER BUTTONS ---
        
        // Button 1: 4-Quadrant
        val btnQuad = Button(this).apply {
            text = "Launch 4-Quadrant"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#444444"))
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                bottomMargin = 30
            }
            setOnClickListener {
                launchActivity(QuadrantActivity::class.java)
            }
        }

        // Button 2: Split-Screen
        val btnSplit = Button(this).apply {
            text = "Launch Split-Screen"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#444444"))
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            setOnClickListener {
                launchActivity(TriSplitActivity::class.java)
            }
        }

        mainLayout.addView(btnQuad)
        mainLayout.addView(btnSplit)

        setContentView(mainLayout)
    }

    // Helper function to check all required permissions
    private fun hasRequiredPermissions(): Boolean {
        val hasOverlay = android.provider.Settings.canDrawOverlays(this)
        val hasShizuku = try { 
            rikka.shizuku.Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED 
        } catch(e: Exception) { false }
        
        val hasNotif = if (android.os.Build.VERSION.SDK_INT >= 33) {
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else true

        return hasOverlay && hasShizuku && hasNotif
    }

    private fun checkShizukuPermission() {
        if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
            // Shizuku not running
        } else if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(0)
            Shizuku.addRequestPermissionResultListener(this)
        }
    }

    private fun launchActivity(cls: Class<*>) {
        try {
            val intent = Intent(this, cls)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Shizuku Granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(this)
    }
}
