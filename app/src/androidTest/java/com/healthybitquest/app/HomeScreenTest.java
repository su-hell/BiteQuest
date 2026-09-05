package com.healthybitquest.app;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import android.content.Context;

import com.healthybitquest.app.storage.LocalStorageManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class HomeScreenTest {

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.getSharedPreferences("healthy_bite_quest_prefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
        LocalStorageManager storage = new LocalStorageManager(context);
        storage.savePlayerName("Sanju");
        storage.addPoints(40);
    }

    @Test
    public void homeShowsWelcomePointsAndButtons() {
        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(withId(R.id.textWelcome)).check(matches(withText(containsString("Sanju"))));
            onView(withId(R.id.textHomePoints)).check(matches(withText(containsString("40"))));
            onView(withId(R.id.buttonPlay)).check(matches(isDisplayed()));
            onView(withId(R.id.buttonProgress)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void progressScreenReadsStoredPoints() {
        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(HomeActivity.class)) {
            onView(withId(R.id.buttonProgress)).perform(click());
            onView(withId(R.id.textProgressPoints)).check(matches(withText(containsString("40"))));
            onView(withId(R.id.textProgressName)).check(matches(withText(containsString("Sanju"))));
        }
    }
}
