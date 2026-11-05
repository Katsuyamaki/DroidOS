package com.katsuyamaki.DroidOSLauncher

import java.util.Locale

/**
 * Emergency containment for elevated activity-start and Accessibility toggles
 * in the Play flavor. This is NOT a general command allowlist or privilege sandbox.
 */
internal object PlayPrivilegedCommandPolicy {
    fun isProhibited(command: String): Boolean {
        val normalized = command.trim().lowercase(Locale.ROOT)
        return normalized.startsWith("am start ") ||
            normalized.startsWith("am start-") ||
            normalized.startsWith("cmd activity start") ||
            normalized.startsWith("am task move-task-to-display") ||
            normalized.startsWith("am task move-task-to-front") ||
            normalized.startsWith("settings put secure enabled_accessibility_services") ||
            normalized.startsWith("settings put secure accessibility_enabled")
    }
}
