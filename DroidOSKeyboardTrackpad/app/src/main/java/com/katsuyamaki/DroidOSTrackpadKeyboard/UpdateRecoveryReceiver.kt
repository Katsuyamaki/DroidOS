package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

class UpdateRecoveryReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        if (!isAccessibilityEnabled(context)) return

        val targetDisplay = try {
            Settings.Global.getInt(context.contentResolver, "droidos_target_display", 0)
        } catch (e: Exception) {
            0
        }

        val restartIntent = Intent(context, OverlayService::class.java).apply {
            putExtra("force_start", true)
            putExtra("displayId", targetDisplay)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(restartIntent)
            } else {
                context.startService(restartIntent)
            }
        } catch (_: Exception) {
        }
    }

    private fun isAccessibilityEnabled(context: Context): Boolean {
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val service = ComponentName(context, OverlayService::class.java).flattenToShortString()
        return enabled.split(':').any { it.equals(service, ignoreCase = true) }
    }
}