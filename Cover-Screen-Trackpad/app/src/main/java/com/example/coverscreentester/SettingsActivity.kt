
package com.example.coverscreentester

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch

class SettingsActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("TrackpadPrefs", Context.MODE_PRIVATE)

        // 1. Define UI Elements
        val swTapScroll = findViewById<Switch>(R.id.swTapScroll)
        val swVibrate = findViewById<Switch>(R.id.swVibrate)
        val swReverseScroll = findViewById<Switch>(R.id.swReverseScroll) // Matches XML
        val swVPos = findViewById<Switch>(R.id.swVPos)
        val swHPos = findViewById<Switch>(R.id.swHPos)
        
        // ... rest of the file ...

        
        val seekBarCursor = findViewById<SeekBar>(R.id.seekBarCursorSpeed)
        val seekBarScroll = findViewById<SeekBar>(R.id.seekBarScrollSpeed)
        val seekCursorSize = findViewById<SeekBar>(R.id.seekBarCursorSize)
        val seekAlpha = findViewById<SeekBar>(R.id.seekBarAlpha)
        val seekHandleSize = findViewById<SeekBar>(R.id.seekBarHandleSize)
        val seekScrollVisual = findViewById<SeekBar>(R.id.seekBarScrollVisual)
        
        val seekHandleTouch = findViewById<SeekBar>(R.id.seekBarHandleTouch)
        val seekScrollTouch = findViewById<SeekBar>(R.id.seekBarScrollTouch)
        
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // =================================================================================
        // 2. LOAD SAVED VALUES
        // =================================================================================
        swTapScroll.isChecked = prefs.getBoolean("tap_scroll", true)
        swVibrate.isChecked = prefs.getBoolean("vibrate", true)
        swReverseScroll.isChecked = prefs.getBoolean("reverse_scroll", false)
        swVPos.isChecked = prefs.getBoolean("v_pos_left", false)
        swHPos.isChecked = prefs.getBoolean("h_pos_top", false)
        
        seekBarCursor.progress = ((prefs.getFloat("cursor_speed", 2.5f)) * 10).toInt()
        seekBarScroll.progress = ((prefs.getFloat("scroll_speed", 1.0f)) * 10).toInt()
        
        seekCursorSize.progress = prefs.getInt("cursor_size", 50)
        seekAlpha.progress = prefs.getInt("alpha", 50)
        seekHandleSize.progress = prefs.getInt("handle_size", 14)
        seekScrollVisual.progress = prefs.getInt("scroll_visual_size", 4)
        
        seekHandleTouch.progress = prefs.getInt("handle_touch_size", 60)
        seekScrollTouch.progress = prefs.getInt("scroll_touch_size", 60)

        // =================================================================================
        // 3. LISTENERS
        // =================================================================================
        fun createPreviewListener(target: String): SeekBar.OnSeekBarChangeListener {
            return object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val intent = Intent(this@SettingsActivity, OverlayService::class.java)
                    intent.action = "PREVIEW_UPDATE"
                    intent.putExtra("TARGET", target)
                    intent.putExtra("VALUE", progress)
                    startService(intent)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        }

        seekCursorSize.setOnSeekBarChangeListener(createPreviewListener("cursor_size"))
        seekAlpha.setOnSeekBarChangeListener(createPreviewListener("alpha"))
        seekHandleSize.setOnSeekBarChangeListener(createPreviewListener("handle_size"))
        seekScrollVisual.setOnSeekBarChangeListener(createPreviewListener("scroll_visual"))
        seekHandleTouch.setOnSeekBarChangeListener(createPreviewListener("handle_touch"))
        seekScrollTouch.setOnSeekBarChangeListener(createPreviewListener("scroll_touch"))

        // =================================================================================
        // 4. SAVE BUTTON LOGIC
        // =================================================================================
        btnSave.setOnClickListener {
            val cVal = if (seekBarCursor.progress < 1) 0.1f else seekBarCursor.progress / 10f
            val sVal = if (seekBarScroll.progress < 1) 0.1f else seekBarScroll.progress / 10f
            
            prefs.edit()
                .putFloat("cursor_speed", cVal)
                .putFloat("scroll_speed", sVal)
                .putBoolean("tap_scroll", swTapScroll.isChecked)
                .putBoolean("vibrate", swVibrate.isChecked)
                .putBoolean("reverse_scroll", swReverseScroll.isChecked)
                .putBoolean("v_pos_left", swVPos.isChecked)
                .putBoolean("h_pos_top", swHPos.isChecked)
                .putInt("cursor_size", seekCursorSize.progress)
                .putInt("alpha", seekAlpha.progress)
                .putInt("handle_size", seekHandleSize.progress)
                .putInt("scroll_visual_size", seekScrollVisual.progress)
                .putInt("handle_touch_size", seekHandleTouch.progress)
                .putInt("scroll_touch_size", seekScrollTouch.progress)
                .apply()

            val intent = Intent(this, OverlayService::class.java)
            intent.action = "RELOAD_PREFS"
            startService(intent)
            finish()
        }

        btnBack.setOnClickListener { finish() }
    }
}
