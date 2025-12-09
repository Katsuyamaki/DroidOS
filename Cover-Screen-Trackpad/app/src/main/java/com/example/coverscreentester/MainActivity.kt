package com.example.coverscreentester

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Display
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No setContentView() - this is a trampoline Activity

        // 1. Check if Shizuku is granted
        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            // If not granted, we must show UI to request it
            Shizuku.requestPermission(0)
            Toast.makeText(this, "Please grant Shizuku permission", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 2. Determine current display ID
        val currentDisplayId = display?.displayId ?: Display.DEFAULT_DISPLAY

        // 3. Start Service with "OPEN_MENU" action on this display
        val intent = Intent(this, OverlayService::class.java).apply {
            action = "OPEN_MENU"
            putExtra("DISPLAY_ID", currentDisplayId)
            putExtra("FORCE_MOVE", true) // Ensure it moves here
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        // 4. Close this activity immediately
        finish()
    }
}
