package com.example.coverscreentester

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(0)
            Toast.makeText(this, "Please grant Shizuku permission", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // --- ROBUST DISPLAY DETECTION ---
        var targetDisplayId = Display.DEFAULT_DISPLAY
        
        try {
            // 1. Try modern API (R+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                display?.let { 
                    targetDisplayId = it.displayId 
                }
            } else {
                // 2. Try WindowManager (Legacy)
                @Suppress("DEPRECATION")
                targetDisplayId = windowManager.defaultDisplay.displayId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Start Service
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

        finish()
    }
}
