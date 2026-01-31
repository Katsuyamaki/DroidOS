package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * =================================================================================
 * CLASS: InterAppCommandReceiver
 * SUMMARY: Static BroadcastReceiver for inter-app communication.
 *          Receives commands from DroidOS Launcher and ADB, then forwards them
 *          to the OverlayService. This ensures commands work even when the
 *          OverlayService's dynamic receiver isn't registered (background state).
 * 
 * USAGE (ADB):
 *   adb shell am broadcast -a com.katsuyamaki.DroidOSTrackpadKeyboard.SOFT_RESTART
 *   adb shell am broadcast -a com.katsuyamaki.DroidOSTrackpadKeyboard.MOVE_TO_VIRTUAL --ei DISPLAY_ID 2
 *   adb shell am broadcast -a com.katsuyamaki.DroidOSTrackpadKeyboard.RETURN_TO_PHYSICAL --ei DISPLAY_ID 0
 *   adb shell am broadcast -a com.katsuyamaki.DroidOSTrackpadKeyboard.ENFORCE_ZORDER
 *   adb shell am broadcast -a com.katsuyamaki.DroidOSTrackpadKeyboard.TOGGLE_VIRTUAL_MIRROR
 *   adb shell am broadcast -a com.katsuyamaki.DroidOSTrackpadKeyboard.GET_STATUS
 * =================================================================================
 */
class InterAppCommandReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "InterAppCmdReceiver"
        
        // Action constants
        const val ACTION_SOFT_RESTART = "com.katsuyamaki.DroidOSTrackpadKeyboard.SOFT_RESTART"
        const val ACTION_MOVE_TO_VIRTUAL = "com.katsuyamaki.DroidOSTrackpadKeyboard.MOVE_TO_VIRTUAL"
        const val ACTION_RETURN_TO_PHYSICAL = "com.katsuyamaki.DroidOSTrackpadKeyboard.RETURN_TO_PHYSICAL"
        const val ACTION_ENFORCE_ZORDER = "com.katsuyamaki.DroidOSTrackpadKeyboard.ENFORCE_ZORDER"
        const val ACTION_TOGGLE_VIRTUAL_MIRROR = "com.katsuyamaki.DroidOSTrackpadKeyboard.TOGGLE_VIRTUAL_MIRROR"
        const val ACTION_GET_STATUS = "com.katsuyamaki.DroidOSTrackpadKeyboard.GET_STATUS"
        
        // Extra keys
        const val EXTRA_DISPLAY_ID = "DISPLAY_ID"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        val action = intent.action ?: return
        Log.d(TAG, "Received inter-app command: $action")
        
        // Forward the command to OverlayService via startService
        // The OverlayService will handle the actual logic
        val serviceIntent = Intent(context, OverlayService::class.java).apply {
            this.action = action
            
            // Copy all extras from the original intent
            intent.extras?.let { extras ->
                putExtras(extras)
            }
        }
        
        try {
            // Start the service with the command
            // Using startService because AccessibilityService handles its own lifecycle
            context.startService(serviceIntent)
            Log.d(TAG, "Forwarded command to OverlayService: $action")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to forward command to OverlayService", e)
            
            // If service start fails, try broadcasting directly
            // The OverlayService's dynamic receiver might pick it up
            try {
                val broadcastIntent = Intent(action).apply {
                    setPackage(context.packageName)
                    intent.extras?.let { extras ->
                        putExtras(extras)
                    }
                }
                context.sendBroadcast(broadcastIntent)
                Log.d(TAG, "Sent internal broadcast as fallback: $action")
            } catch (e2: Exception) {
                Log.e(TAG, "Fallback broadcast also failed", e2)
            }
        }
    }
}
// =================================================================================
// END CLASS: InterAppCommandReceiver
// =================================================================================
