package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.view.inputmethod.EditorInfo

// =================================================================================
// CLASS: NullInputMethodService
// SUMMARY: A "hollow" IME that displays nothing but maintains an InputConnection.
//          Receives commands via broadcast from the DroidOS keyboard overlay and
//          injects text/keys through the InputConnection for seamless text input.
//
//          This is necessary because overlay keyboards cannot directly access the
//          InputConnection of the focused app - only the active IME can do that.
//
//          FIXED: Now properly handles metaState for SHIFT, CTRL, ALT modifiers.
// =================================================================================
class NullInputMethodService : InputMethodService() {

    companion object {
        private const val TAG = "NullIME"
        private const val IME_TRACE_TAG = "IME_TRACE"
        private const val BROADCAST_ACTION_TEXT = "com.katsuyamaki.DroidOSTrackpadKeyboard.INJECT_TEXT"
        private const val BROADCAST_ACTION_KEY = "com.katsuyamaki.DroidOSTrackpadKeyboard.INJECT_KEY"
        private const val BROADCAST_ACTION_DELETE = "com.katsuyamaki.DroidOSTrackpadKeyboard.INJECT_DELETE"
        private const val EXTRA_REQUEST_ID = "requestId"
    }

    private var lastImeDropSignature: String? = null
    private var lastImeDropLogElapsed = 0L
    private val imeDropLogThrottleMs = 3000L

    private fun recordImeBroadcast(action: String, requestId: Long, success: Boolean, detail: String) {
        ImeBroadcastHealth.record(action, requestId, success, detail)
        if (success) {
            lastImeDropSignature = null
            return
        }
        if (detail == "fallback_claimed") {
            return
        }

        val now = SystemClock.elapsedRealtime()
        val signature = "$action|$detail"
        val shouldLog =
            signature != lastImeDropSignature || (now - lastImeDropLogElapsed) >= imeDropLogThrottleMs

        if (shouldLog) {
            android.util.Log.i(
                IME_TRACE_TAG,
                "event=IME_BROADCAST_DROP service=null action=${action.substringAfterLast(".")} reason=$detail request=$requestId"
            )
            lastImeDropSignature = signature
            lastImeDropLogElapsed = now
        }
    }

    // =================================================================================
    // BROADCAST RECEIVER: Handles commands from OverlayService (DroidOS Keyboard)
    // SUMMARY: Processes INJECT_TEXT, INJECT_KEY, and INJECT_DELETE broadcasts.
    //          INJECT_KEY now properly handles metaState for shift/ctrl/alt support.
    // =================================================================================
    private val inputReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            val requestId = intent.getLongExtra(EXTRA_REQUEST_ID, -1L)

            if (ImeBroadcastHealth.consumeFallbackRequest(requestId)) {
                recordImeBroadcast(action, requestId, false, "fallback_claimed")
                return
            }

            val ic = currentInputConnection
            if (ic == null) {
                recordImeBroadcast(action, requestId, false, "ic_null")
                return
            }

            when (action) {
                BROADCAST_ACTION_TEXT -> {
                    val text = intent.getStringExtra("text")
                    if (text.isNullOrEmpty()) {
                        recordImeBroadcast(action, requestId, false, "empty_text")
                    } else {
                        ic.commitText(text, 1)
                        recordImeBroadcast(action, requestId, true, "ok")
                    }
                }
                BROADCAST_ACTION_KEY -> {
                    val code = intent.getIntExtra("keyCode", 0)
                    val metaState = intent.getIntExtra("metaState", 0)
                    if (code > 0) {
                        sendKeyEventWithMeta(ic, code, metaState)
                        recordImeBroadcast(action, requestId, true, "ok")
                    } else {
                        recordImeBroadcast(action, requestId, false, "invalid_key")
                    }
                }
                BROADCAST_ACTION_DELETE -> {
                    val length = intent.getIntExtra("length", 1)
                    if (length > 0) {
                        ic.deleteSurroundingText(length, 0)
                        recordImeBroadcast(action, requestId, true, "ok")
                    } else {
                        recordImeBroadcast(action, requestId, false, "invalid_delete")
                    }
                }
                else -> {
                    recordImeBroadcast(action, requestId, false, "unknown_action")
                }
            }
        }
    }
    // =================================================================================
    // END BLOCK: Broadcast Receiver
    // =================================================================================

    // =================================================================================
    // FUNCTION: sendKeyEventWithMeta
    // SUMMARY: Sends DOWN and UP KeyEvents with proper metaState support.
    //          This is the fix for SHIFT/CTRL/ALT not working.
    //
    //          The standard sendDownUpKeyEvents() method doesn't accept metaState,
    //          so we construct the KeyEvents manually with all required fields.
    //
    // PARAMETERS:
    //   - ic: The InputConnection to send events through
    //   - keyCode: The Android keycode (e.g., KEYCODE_A, KEYCODE_1)
    //   - metaState: Modifier flags (META_SHIFT_ON, META_CTRL_ON, META_ALT_ON)
    // =================================================================================
    private fun sendKeyEventWithMeta(ic: InputConnection, keyCode: Int, metaState: Int) {
        val eventTime = SystemClock.uptimeMillis()
        
        // Construct KEY_DOWN event with metaState
        val downEvent = KeyEvent(
            eventTime,              // downTime
            eventTime,              // eventTime
            KeyEvent.ACTION_DOWN,   // action
            keyCode,                // keyCode
            0,                      // repeat count
            metaState,              // metaState - THE KEY FIX!
            0,                      // deviceId
            0,                      // scanCode
            8,  // FLAG_FROM_SYSTEM - prevents Samsung UI flash
            android.view.InputDevice.SOURCE_KEYBOARD  // source
        )
        
        // Construct KEY_UP event with metaState
        val upEvent = KeyEvent(
            eventTime,              // downTime
            eventTime,              // eventTime
            KeyEvent.ACTION_UP,     // action
            keyCode,                // keyCode
            0,                      // repeat count
            metaState,              // metaState - THE KEY FIX!
            0,                      // deviceId
            0,                      // scanCode
            8,  // FLAG_FROM_SYSTEM - prevents Samsung UI flash
            android.view.InputDevice.SOURCE_KEYBOARD  // source
        )
        
        // Send both events through the InputConnection

        ic.sendKeyEvent(downEvent)
        ic.sendKeyEvent(upEvent)
        

    }
    // =================================================================================
    // END BLOCK: sendKeyEventWithMeta
    // =================================================================================

    override fun onCreate() {
        super.onCreate()

        
        // Register receiver for OverlayService communication
        val filter = IntentFilter().apply {
            addAction(BROADCAST_ACTION_TEXT)
            addAction(BROADCAST_ACTION_KEY)
            addAction(BROADCAST_ACTION_DELETE)
        }
        
        if (Build.VERSION.SDK_INT >= 33) { // TIRAMISU
            registerReceiver(inputReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(inputReceiver, filter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            unregisterReceiver(inputReceiver)
        } catch (e: Exception) {
            // Ignore if not registered
        }
    }

    // =================================================================================
    // FUNCTION: onCreateInputView
    // SUMMARY: Returns a zero-sized invisible view.
    //          The system requires an input view, but we don't want to show anything
    //          because the DroidOS overlay keyboard handles all visual display.
    // =================================================================================
    override fun onCreateInputView(): View {
        return View(this).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(0, 0)
            visibility = View.GONE
        }
    }
    // =================================================================================
    // END BLOCK: onCreateInputView
    // =================================================================================
    
    override fun onCreateCandidatesView(): View? {
        return null // No suggestions - DroidOS keyboard handles this
    }

    // =================================================================================
    // FUNCTION: onEvaluateInputViewShown
    // SUMMARY: Returns true so the system maintains an active InputConnection.
    //          Without this, the InputConnection may not be available when we need it.
    // =================================================================================
    override fun onEvaluateInputViewShown(): Boolean {
        return super.onEvaluateInputViewShown()
    }
    // =================================================================================
    // END BLOCK: onEvaluateInputViewShown
    // =================================================================================
}

// =================================================================================
// END FILE: NullInputMethodService.kt
// =================================================================================
