package com.healthybitquest.app;

/**
 * Shared constants for scoring, badges, and category labels.
 */
public final class AppConstants {

    public static final int QUESTIONS_PER_GAME = 10;
    public static final int POINTS_PER_CORRECT = 10;
    public static final int MAX_SCORE = QUESTIONS_PER_GAME * POINTS_PER_CORRECT;

    public static final String CATEGORY_HEALTHY = "healthy";
    public static final String CATEGORY_LESS_HEALTHY = "less_healthy";

    public static final String BADGE_EXPLORER = "Food Explorer";
    public static final String BADGE_HERO = "Healthy Hero";
    public static final String BADGE_CHAMPION = "Nutrition Champion";

    public static final int BADGE_EXPLORER_MIN = 50;
    public static final int BADGE_HERO_MIN = 80;
    public static final int BADGE_CHAMPION_MIN = 100;

    public static final String EXTRA_GAME_SCORE = "extra_game_score";
    public static final String EXTRA_CORRECT = "extra_correct";
    public static final String EXTRA_INCORRECT = "extra_incorrect";
    public static final String EXTRA_CHANGE_NAME = "extra_change_name";

    public static final String DEFAULT_PLAYER_NAME = "Sanju";

    private AppConstants() {
    }
}
