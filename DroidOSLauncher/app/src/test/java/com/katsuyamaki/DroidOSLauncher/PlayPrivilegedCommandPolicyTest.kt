package com.katsuyamaki.DroidOSLauncher

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayPrivilegedCommandPolicyTest {
    @Test fun backgroundStartsAndAccessibilityTogglesAreBlocked() {
        assertTrue(PlayPrivilegedCommandPolicy.isProhibited("am start --display 1 -n test/.Main"))
        assertTrue(PlayPrivilegedCommandPolicy.isProhibited("  AM START-FOREGROUND-SERVICE -n test/.S"))
        assertTrue(PlayPrivilegedCommandPolicy.isProhibited("cmd activity start-activity ..."))
        assertTrue(PlayPrivilegedCommandPolicy.isProhibited("am task move-task-to-display 123 1"))
        assertTrue(PlayPrivilegedCommandPolicy.isProhibited("settings put secure enabled_accessibility_services x"))
        assertTrue(PlayPrivilegedCommandPolicy.isProhibited("settings put secure accessibility_enabled 1"))
    }

    @Test fun userDirectedDisplayAndWindowCommandsRemainAvailable() {
        assertFalse(PlayPrivilegedCommandPolicy.isProhibited("wm density 320 -d 1"))
        assertFalse(PlayPrivilegedCommandPolicy.isProhibited("am task resize 123 0 0 100 100"))
        assertFalse(PlayPrivilegedCommandPolicy.isProhibited("settings put system user_rotation 0"))
    }
}
