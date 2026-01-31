package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.View

/**
 * A dedicated Activity to hold Keyboard Focus.
 * We use an Activity instead of a Service Overlay because Android 
 * aggressively kills keyboards attached to non-focusable windows 
 * or inactive displays.
 */
class KeyboardActivity : Activity() {

    private lateinit var inputField: EditText
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make window transparent
        window.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Ensure we get focus
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        
        setContentView(R.layout.activity_keyboard)

        inputField = findViewById(R.id.et_remote_input)
        statusText = findViewById(R.id.tv_status)
        
        // Dismiss if user taps the transparent top area
        findViewById<View>(R.id.view_touch_dismiss).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_close).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_clear).setOnClickListener {
            inputField.setText("")
        }

        inputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0 && s != null) {
                    val char = s[start]
                    sendCharToService(char)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                // Do NOT clear text automatically. It causes keyboard flicker.
            }
        })
        
        inputField.requestFocus()
    }

    private fun sendCharToService(c: Char) {
        val intent = Intent("INJECT_CHAR")
        intent.setPackage(packageName)
        intent.putExtra("CHAR", c)
        sendBroadcast(intent)
    }
}
