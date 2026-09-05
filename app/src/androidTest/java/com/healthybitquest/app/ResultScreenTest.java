package com.healthybitquest.app;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import android.content.Context;
import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ResultScreenTest {

    @Test
    public void resultScreenShowsFinalScoreAfterTenQuestions() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(AppConstants.EXTRA_GAME_SCORE, 80);
        intent.putExtra(AppConstants.EXTRA_CORRECT, 8);
        intent.putExtra(AppConstants.EXTRA_INCORRECT, 2);

        try (ActivityScenario<ResultActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withText(R.string.challenge_complete)).check(matches(isDisplayed()));
            onView(withId(R.id.textResultScore)).check(matches(withText(containsEighty())));
            onView(withId(R.id.buttonPlayAgain)).check(matches(isDisplayed()));
            onView(withId(R.id.buttonBackHome)).check(matches(isDisplayed()));
        }
    }

    private static org.hamcrest.Matcher<String> containsEighty() {
        return org.hamcrest.Matchers.containsString("80");
    }
}
