package com.katsuyamaki.DroidOSTrackpadKeyboard

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivitySmokeTest {

    @Test
    fun showsPrimaryActionButtons() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
            onView(withId(R.id.btnBack)).check(matches(isDisplayed()))
        }
    }
}