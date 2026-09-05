package com.healthybitquest.app.game;

import com.healthybitquest.app.AppConstants;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BadgeCalculatorTest {

    @Test
    public void noBadgeBelowFifty() {
        assertTrue(BadgeCalculator.badgesForScore(40).isEmpty());
    }

    @Test
    public void explorerAtFifty() {
        List<String> badges = BadgeCalculator.badgesForScore(50);
        assertEquals(1, badges.size());
        assertTrue(badges.contains(AppConstants.BADGE_EXPLORER));
    }

    @Test
    public void heroAtEighty() {
        List<String> badges = BadgeCalculator.badgesForScore(80);
        assertEquals(2, badges.size());
        assertTrue(badges.contains(AppConstants.BADGE_EXPLORER));
        assertTrue(badges.contains(AppConstants.BADGE_HERO));
    }

    @Test
    public void championAtOneHundred() {
        List<String> badges = BadgeCalculator.badgesForScore(100);
        assertEquals(3, badges.size());
        assertTrue(badges.contains(AppConstants.BADGE_CHAMPION));
    }
}
