package ru.sukharev.focustimer.focus

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.utils.Level
import ru.sukharev.focustimer.utils.LevelEntry
import ru.sukharev.focustimer.utils.SPACE
import ru.sukharev.focustimer.utils.toSeconds
import ru.sukharev.focustimer.utils.views.LevelAnimator


@RunWith(AndroidJUnit4::class)
class FocusActivityTest {

    @Rule
    @JvmField
    var activityTestRule: ActivityTestRule<FocusActivity> = ActivityTestRule(FocusActivity::class.java)

    @Test
    fun setLevel() {
        val activity = activityTestRule.activity
        val initLevel = LevelEntry(Level.ZERO, 0)
        activity?.runOnUiThread { activity.setLevel(initLevel) }
        val firstLevelUp = LevelEntry(Level.TWO, toSeconds(40))
        activity?.runOnUiThread { activity.setLevel(firstLevelUp) }
        Thread.sleep(LevelAnimator.TOTAL_ANIMATION_DURATION)
        onView(withId(R.id.focus_level_text)).check(matches(withText(activity.applicationContext.getString(R.string.level) +
                SPACE + firstLevelUp.level.getName(activity.applicationContext))))

        assertEquals(Level.TWO.maxPoints, activity?.getLevelMax())
        assertEquals(toSeconds(40), activity?.getLevelProgress())
        val secondLevelUp = LevelEntry(Level.THREE, toSeconds(20))
        activity?.runOnUiThread { activity.setLevel(secondLevelUp) }
        Thread.sleep(LevelAnimator.TOTAL_ANIMATION_DURATION)
        onView(withId(R.id.focus_level_text)).check(matches(withText(activity.applicationContext.getString(R.string.level) +
                SPACE + secondLevelUp.level.getName(activity.applicationContext))))
        assertEquals(toSeconds(20), activity?.getLevelProgress())
    }
}