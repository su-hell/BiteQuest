package com.healthybitquest.app.storage;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.healthybitquest.app.AppConstants;
import com.healthybitquest.app.game.BadgeCalculator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LocalStoragePersistenceTest {

    private LocalStorageManager storage;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.getSharedPreferences("healthy_bite_quest_prefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
        storage = new LocalStorageManager(context);
    }

    @Test
    public void pointsRemainAfterNewManagerInstance() {
        storage.savePlayerName("Sanju");
        storage.addPoints(100);
        storage.addPoints(30);

        LocalStorageManager reopened = new LocalStorageManager(
                InstrumentationRegistry.getInstrumentation().getTargetContext()
        );

        assertEquals("Sanju", reopened.getPlayerName());
        assertEquals(130, reopened.getTotalPoints());
    }

    @Test
    public void finishedGamePersistsBadgesAndHighScore() {
        storage.recordFinishedGame(100, 10, 10, BadgeCalculator.badgesForScore(100));

        LocalStorageManager reopened = new LocalStorageManager(
                InstrumentationRegistry.getInstrumentation().getTargetContext()
        );

        assertEquals(100, reopened.getHighestScore());
        assertEquals(1, reopened.getGamesCompleted());
        assertTrue(reopened.getBadgesUnlocked().contains(AppConstants.BADGE_CHAMPION));
    }
}
