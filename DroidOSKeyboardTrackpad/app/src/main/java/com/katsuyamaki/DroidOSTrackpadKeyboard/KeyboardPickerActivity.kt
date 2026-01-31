package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardPickerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Transparent touchable view — tap to dismiss if needed
        val view = View(this)
        view.setBackgroundColor(0x00000000)
        view.isClickable = true
        view.setOnClickListener { finish() }
        setContentView(view)
        
        // Launch picker after short delay for forceSystemKeyboardVisible() to complete
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                try {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showInputMethodPicker()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // Auto-finish this transparent activity after giving user time to pick
                Handler(Looper.getMainLooper()).postDelayed({ finish() }, 8000)
            }
        }, 500)
    }
}
