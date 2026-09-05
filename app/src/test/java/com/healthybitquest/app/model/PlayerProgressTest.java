package com.healthybitquest.app.model;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class PlayerProgressTest {

    @Test
    public void accuracyUsesCorrectOverAttempted() {
        PlayerProgress progress = new PlayerProgress(
                "Sanju", 80, 80, 1, 8, 10, Collections.emptySet());
        assertEquals(80, progress.getAccuracyPercent());
        assertEquals(80, progress.getTotalPoints());
        assertEquals("Sanju", progress.getPlayerName());
    }

    @Test
    public void missingValuesDefaultSafely() {
        PlayerProgress progress = new PlayerProgress(null, -5, 0, 0, 0, 0, null);
        assertEquals("", progress.getPlayerName());
        assertEquals(0, progress.getTotalPoints());
        assertEquals(0, progress.getAccuracyPercent());
        assertEquals(0, progress.getBadgeCount());
    }
}
