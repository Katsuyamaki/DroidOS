package com.katsuyamaki.DroidOSLauncher

import android.app.ActivityManager
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.util.Log

/**
 * API-only selected-display app launch gateway for the Play distribution.
 * This does not move tasks, bypass background-start restrictions, or guarantee
 * that an OEM will allow launch bounds or launch on private/cover displays.
 */
internal object PlayDisplayLauncher {
    fun launch(context: Context, intent: Intent, displayId: Int, bounds: Rect?): Boolean {
        if (displayId < 0) return false
        val displays = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
            ?: return false
        val present = displays.getDisplay(displayId) != null
        val secondarySupported = context.packageManager.hasSystemFeature(
            PackageManager.FEATURE_ACTIVITIES_ON_SECONDARY_DISPLAYS
        )
        if (!PlayLaunchPolicy.isAvailableDisplay(displayId, present, secondarySupported)) return false

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return false
        return try {
            // API 29+, required for a caller to check permission on a specific display.
            if (!activityManager.isActivityStartAllowedOnDisplay(context, displayId, intent)) {
                return false
            }
            val options = ActivityOptions.makeBasic().apply {
                setLaunchDisplayId(displayId)
                if (bounds != null && bounds.width() > 0 && bounds.height() > 0) {
                    setLaunchBounds(bounds)
                }
            }
            // Android may still deny this start due to background-activity-launch limits.
            context.startActivity(intent, options.toBundle())
            true
        } catch (error: RuntimeException) {
            Log.w("PlayDisplayLauncher", "Selected-display activity start denied", error)
            false
        }
    }

    fun launchPackage(
        context: Context,
        packageName: String,
        className: String?,
        displayId: Int,
        bounds: Rect?
    ): Boolean {
        val resolvedClass = AppCompatibilityRegistry.resolveLaunchClass(packageName, className)
        val launchIntent = if (resolvedClass != null) {
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                setClassName(packageName, resolvedClass)
            }
        } else {
            context.packageManager.getLaunchIntentForPackage(packageName)
        } ?: return false

        // New-session request: no attempt to migrate a running task.
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        return launch(context, launchIntent, displayId, bounds)
    }
}
