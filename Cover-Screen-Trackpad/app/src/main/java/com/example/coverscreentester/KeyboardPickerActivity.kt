package com.katsuyamaki.DroidOSFOSSKeyboardTrackpad

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardPickerActivity : Activity() {
    
    private var hasLaunchedPicker = false
    private var pickerWasOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Transparent touchable view
        val view = View(this)
        view.setBackgroundColor(0x00000000) 
        view.isClickable = true
        
        // Safety Net: If logic fails, user can tap screen to close
        view.setOnClickListener { finish() }
        setContentView(view)
        
        // Launch picker after window is ready
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) launchPicker()
        }, 300)
    }

    private fun launchPicker() {
        if (hasLaunchedPicker) return
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
            hasLaunchedPicker = true
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        
        if (hasLaunchedPicker) {
            if (!hasFocus) {
                // We lost focus -> The Picker Dialog is now showing
                pickerWasOpened = true
            } else if (hasFocus && pickerWasOpened) {
                // We regained focus -> The Picker Dialog just closed
                // We are done.
                finish()
            }
        }
    }
}
