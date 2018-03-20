package com.ahmedabdelmeged.config

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.LargeTest
import android.view.View
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.Matchers.`is`

/**
 * Created by Ahmed Abd-Elmeged on 3/20/2018.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var rule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testFetchConfig() {
        // Click fetch config button
        onView(withText(R.string.fetch_remote_welcome_message))
                .check(matches(isDisplayed()))
                .perform(click())

        // Watch for success Toast
        onView(withText(startsWith("Fetch Succeeded")))
                .inRoot(withDecorView(not<View>(`is`(rule.activity.window.decorView))))
                .check(matches(isDisplayed()))
    }

}
