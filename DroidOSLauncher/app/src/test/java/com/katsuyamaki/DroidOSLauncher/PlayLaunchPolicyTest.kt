package com.katsuyamaki.DroidOSLauncher

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayLaunchPolicyTest {
    @Test fun defaultDisplayWithoutSecondaryFeature() {
        assertTrue(PlayLaunchPolicy.isAvailableDisplay(0, true, false))
    }

    @Test fun secondaryDisplayRequiresFeatureAndVisibility() {
        assertFalse(PlayLaunchPolicy.isAvailableDisplay(1, true, false))
        assertFalse(PlayLaunchPolicy.isAvailableDisplay(1, false, true))
        assertTrue(PlayLaunchPolicy.isAvailableDisplay(1, true, true))
    }

    @Test fun invalidAndStaleDisplaysFailClosed() {
        assertFalse(PlayLaunchPolicy.isAvailableDisplay(-1, true, true))
        assertFalse(PlayLaunchPolicy.isAvailableDisplay(0, false, true))
    }
}
