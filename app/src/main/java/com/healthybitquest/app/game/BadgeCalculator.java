package com.healthybitquest.app.game;

import com.healthybitquest.app.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple score-based badge rules.
 */
public class BadgeCalculator {

    public static List<String> badgesForScore(int gameScore) {
        List<String> badges = new ArrayList<>();
        if (gameScore >= AppConstants.BADGE_EXPLORER_MIN) {
            badges.add(AppConstants.BADGE_EXPLORER);
        }
        if (gameScore >= AppConstants.BADGE_HERO_MIN) {
            badges.add(AppConstants.BADGE_HERO);
        }
        if (gameScore >= AppConstants.BADGE_CHAMPION_MIN) {
            badges.add(AppConstants.BADGE_CHAMPION);
        }
        return badges;
    }

    public static String summaryForScore(int gameScore) {
        if (gameScore >= AppConstants.BADGE_CHAMPION_MIN) {
            return "Perfect! You are a Nutrition Champion!";
        }
        if (gameScore >= AppConstants.BADGE_HERO_MIN) {
            return "Great job! You unlocked Healthy Hero!";
        }
        if (gameScore >= AppConstants.BADGE_EXPLORER_MIN) {
            return "Well done! You unlocked Food Explorer!";
        }
        return "Keep practising — you can unlock a badge next time!";
    }
}
