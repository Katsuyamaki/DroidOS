package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ProfilesActivity : Activity() {

    private lateinit var tvStats: TextView
    private lateinit var tvList: TextView
    private lateinit var btnSave: Button
    private lateinit var btnReset: Button
    private lateinit var btnClose: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles)

        // FORCE WINDOW SIZE
        try {
            val dm = resources.displayMetrics
            val targetWidth = (450 * dm.density).toInt().coerceAtMost(dm.widthPixels)
            window.setLayout(targetWidth, ViewGroup.LayoutParams.MATCH_PARENT)
            window.setGravity(Gravity.CENTER)
        } catch (e: Exception) {}

        tvStats = findViewById(R.id.tvCurrentStats)
        tvList = findViewById(R.id.tvProfileList)
        btnSave = findViewById(R.id.btnSaveCurrent)
        btnReset = findViewById(R.id.btnResetCurrent)
        btnClose = findViewById(R.id.btnClose)

        refreshUI()

        btnSave.setOnClickListener {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "SAVE_LAYOUT"
            startService(intent)
            Toast.makeText(this, "Saved for this Aspect Ratio!", Toast.LENGTH_SHORT).show()
            tvList.postDelayed({ refreshUI() }, 500)
        }

        btnReset.setOnClickListener {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "DELETE_PROFILE"
            startService(intent)
            Toast.makeText(this, "Profile Deleted. Resetting...", Toast.LENGTH_SHORT).show()
            tvList.postDelayed({ refreshUI() }, 500)
        }

        btnClose.setOnClickListener { finish() }
    }

    private fun refreshUI() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        
        val width = metrics.widthPixels.toFloat()
        val height = metrics.heightPixels.toFloat()
        val ratio = width / height
        val ratioKey = String.format("%.1f", ratio)
        
        tvStats.text = "Ratio: $ratioKey\nRes: ${metrics.widthPixels} x ${metrics.heightPixels}\nDensity: ${metrics.density}"

        val prefs = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)
        val allEntries = prefs.all
        val sb = StringBuilder()
        
        val foundProfiles = HashSet<String>()
        
        for (key in allEntries.keys) {
            if (key.startsWith("profile_") && key.endsWith("_xp")) {
                val parts = key.split("_")
                if (parts.size >= 2) {
                    foundProfiles.add(parts[1])
                }
            }
        }
        
        if (foundProfiles.isEmpty()) {
            sb.append("No saved profiles.")
        } else {
            for (p in foundProfiles) {
                sb.append("• Ratio $p: Saved\n")
            }
        }
        
        tvList.text = sb.toString()
    }
}
