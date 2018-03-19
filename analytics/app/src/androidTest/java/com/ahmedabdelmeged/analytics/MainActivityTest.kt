package com.ahmedabdelmeged.analytics

import android.support.annotation.StringRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var mActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun mainActivityTest() {
        // Select favorite food if the dialog appears
        selectFavoriteFoodIfPossible("Hot Dogs")

        // Initial title
        checkTitleText(R.string.pattern1_title)

        // Swipe, make sure we see the right titles
        val viewPager = onView(withId(R.id.pager))
        viewPager.check(matches(isDisplayed()))

        // Swipe left
        viewPager.perform(swipeLeft())
        checkTitleText(R.string.pattern2_title)

        // Swipe left
        viewPager.perform(swipeLeft())
        checkTitleText(R.string.pattern3_title)

        // Swipe back right
        viewPager.perform(swipeRight())
        checkTitleText(R.string.pattern2_title)
    }

    private fun checkTitleText(@StringRes resId: Int) {
        onView(withText(resId))
                .check(matches(isDisplayed()))
    }

    private fun selectFavoriteFoodIfPossible(food: String) {
        try {
            val appCompatTextView = onView(
                    allOf<View>(withId(android.R.id.text1), withText(food),
                            withParent(allOf(withId(R.id.select_dialog_listview),
                                    withParent(withId(R.id.contentPanel)))),
                            isDisplayed()))
            appCompatTextView.perform(click())
        } catch (e: NoMatchingViewException) {
            // This is ok
        }

    }
}
