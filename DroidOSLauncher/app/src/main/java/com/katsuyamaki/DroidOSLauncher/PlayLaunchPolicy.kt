package com.katsuyamaki.DroidOSLauncher

/** Pure decision gate; Android still performs the authoritative launch permission check. */
internal object PlayLaunchPolicy {
    fun isAvailableDisplay(
        displayId: Int,
        present: Boolean,
        supportsSecondaryDisplayActivities: Boolean
    ): Boolean = displayId >= 0 && present &&
        (displayId == 0 || supportsSecondaryDisplayActivities)
}
