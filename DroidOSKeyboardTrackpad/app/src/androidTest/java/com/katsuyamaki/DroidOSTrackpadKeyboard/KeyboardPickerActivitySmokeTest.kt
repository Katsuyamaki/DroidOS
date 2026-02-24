package com.katsuyamaki.DroidOSTrackpadKeyboard

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KeyboardPickerActivitySmokeTest {

    @Test
    fun showsPickerDialogWhenKeyboardListResultArrives() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        ActivityScenario.launch(KeyboardPickerActivity::class.java).use {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            Thread.sleep(120)

            val droidIme = "${appContext.packageName}/com.katsuyamaki.DroidOSTrackpadKeyboard.DockInputMethodService"
            val gboardIme = "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME"

            val resultIntent = Intent("com.katsuyamaki.DroidOSTrackpadKeyboard.LIST_KEYBOARDS_RESULT").apply {
                setPackage(appContext.packageName)
                putExtra("ALL_IMES", "$droidIme\n$gboardIme")
                putExtra("ENABLED_IMES", "$droidIme\n$gboardIme")
            }
            appContext.sendBroadcast(resultIntent)

            onView(withText("Select Keyboard")).check(matches(isDisplayed()))
            onView(withText("Cancel")).perform(click())
        }
    }
}