package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ManualAdjustActivity : Activity() {

    private var isMoveMode = true
    private var isKeyboardTarget = false
    private val STEP_SIZE = 10

    private lateinit var textMode: TextView
    private lateinit var btnToggle: Button
    private lateinit var btnToggleTarget: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_adjust)

        textMode = findViewById(R.id.text_mode)
        btnToggle = findViewById(R.id.btn_toggle_mode)
        btnToggleTarget = findViewById(R.id.btn_toggle_target)

        updateModeUI()

        btnToggle.setOnClickListener {
            isMoveMode = !isMoveMode
            updateModeUI()
        }

        btnToggleTarget.setOnClickListener {
            isKeyboardTarget = !isKeyboardTarget
            updateModeUI()
        }

        findViewById<Button>(R.id.btn_up).setOnClickListener { sendAdjust(0, -STEP_SIZE) }
        findViewById<Button>(R.id.btn_down).setOnClickListener { sendAdjust(0, STEP_SIZE) }
        findViewById<Button>(R.id.btn_left).setOnClickListener { sendAdjust(-STEP_SIZE, 0) }
        findViewById<Button>(R.id.btn_right).setOnClickListener { sendAdjust(STEP_SIZE, 0) }

        // RESET BUTTON
        findViewById<Button>(R.id.btn_center).setOnClickListener {
            val targetStr = if (isKeyboardTarget) "KEYBOARD" else "TRACKPAD"
            Toast.makeText(this, "Resetting $targetStr", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, OverlayService::class.java)
            intent.action = "RESET_POSITION"
            intent.putExtra("TARGET", targetStr)
            startService(intent)
        }

        // ROTATE BUTTON
        findViewById<Button>(R.id.btn_rotate).setOnClickListener {
            val targetStr = if (isKeyboardTarget) "KEYBOARD" else "TRACKPAD"
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "ROTATE"
            intent.putExtra("TARGET", targetStr)
            startService(intent)
        }

        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }
    }

    private fun updateModeUI() {
        val targetText = if (isKeyboardTarget) "KEYBOARD" else "TRACKPAD"
        val actionText = if (isMoveMode) "POSITION" else "SIZE"

        textMode.text = "$targetText: $actionText"

        if (isKeyboardTarget) {
            textMode.setTextColor(Color.MAGENTA)
            btnToggleTarget.text = "Target: KEYBOARD"
        } else {
            textMode.setTextColor(if (isMoveMode) Color.GREEN else Color.CYAN)
            btnToggleTarget.text = "Target: TRACKPAD"
        }

        btnToggle.text = if (isMoveMode) "Mode: Position" else "Mode: Size"
    }

    private fun sendAdjust(xChange: Int, yChange: Int) {
        val intent = Intent(this, OverlayService::class.java)
        intent.action = "MANUAL_ADJUST"
        intent.putExtra("TARGET", if (isKeyboardTarget) "KEYBOARD" else "TRACKPAD")

        if (isMoveMode) {
            intent.putExtra("DX", xChange)
            intent.putExtra("DY", yChange)
        } else {
            intent.putExtra("DW", xChange)
            intent.putExtra("DH", yChange)
        }
        startService(intent)
    }
}
