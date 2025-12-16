package com.example.coverscreentester

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.os.Build
import androidx.core.content.ContextCompat

class NullInputMethodService : InputMethodService() {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                // COMMAND 1: SWITCH KEYBOARD (Restore Gboard)
                "com.example.coverscreentester.RESTORE_IME" -> {
                    val targetIme = intent.getStringExtra("target_ime")
                    if (!targetIme.isNullOrEmpty()) {
                        try {
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            // Using the Service's Token proves we are the active IME and have permission to switch
                            val token = window?.window?.attributes?.token
                            if (token != null) {
                                imm.setInputMethod(token, targetIme)
                            }
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
                
                // COMMAND 2: TYPE KEY (Native Input)
                "com.example.coverscreentester.INJECT_KEY" -> {
                    val keyCode = intent.getIntExtra("keyCode", 0)
                    val metaState = intent.getIntExtra("metaState", 0)
                    
                    if (keyCode != 0 && currentInputConnection != null) {
                        val now = System.currentTimeMillis()
                        // Send DOWN with Meta State
                        currentInputConnection.sendKeyEvent(
                            KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0, metaState)
                        )
                        // Send UP with Meta State
                        currentInputConnection.sendKeyEvent(
                            KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0, metaState)
                        )
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction("com.example.coverscreentester.RESTORE_IME")
            addAction("com.example.coverscreentester.INJECT_KEY")
        }
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(receiver) } catch (e: Exception) {}
    }

    override fun onCreateInputView(): View {
        // Return a zero-sized, hidden view
        return View(this).apply { 
            layoutParams = android.view.ViewGroup.LayoutParams(0, 0)
            visibility = View.GONE
        }
    }
    
    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        // Crucial: Tell system NOT to allocate screen space for this keyboard
        return false 
    }
    
    override fun onEvaluateFullscreenMode(): Boolean = false // Important: Never take over full screen
}
