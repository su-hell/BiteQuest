package com.healthybitquest.app.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.healthybitquest.app.AppConstants;
import com.healthybitquest.app.model.PlayerProgress;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * All player progress is stored in SharedPreferences (Android local storage).
 * No database or network is used.
 */
public class LocalStorageManager {

    private static final String PREFS_NAME = "healthy_bite_quest_prefs";

    private static final String KEY_PLAYER_NAME = "player_name";
    private static final String KEY_TOTAL_POINTS = "total_points";
    private static final String KEY_HIGHEST_SCORE = "highest_score";
    private static final String KEY_GAMES_COMPLETED = "games_completed";
    private static final String KEY_CORRECT_ANSWERS = "correct_answers";
    private static final String KEY_QUESTIONS_ATTEMPTED = "questions_attempted";
    private static final String KEY_BADGES = "badges_unlocked";

    private final SharedPreferences preferences;

    public LocalStorageManager(Context context) {
        this.preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean hasPlayerName() {
        String name = preferences.getString(KEY_PLAYER_NAME, "");
        return name != null && !name.trim().isEmpty();
    }

    public String getPlayerName() {
        String name = preferences.getString(KEY_PLAYER_NAME, AppConstants.DEFAULT_PLAYER_NAME);
        if (name == null || name.trim().isEmpty()) {
            return AppConstants.DEFAULT_PLAYER_NAME;
        }
        return name.trim();
    }

    public void savePlayerName(String name) {
        String safeName = name == null ? "" : name.trim();
        if (safeName.isEmpty()) {
            safeName = AppConstants.DEFAULT_PLAYER_NAME;
        }
        preferences.edit().putString(KEY_PLAYER_NAME, safeName).apply();
    }

    public int getTotalPoints() {
        return preferences.getInt(KEY_TOTAL_POINTS, 0);
    }

    public void addPoints(int points) {
        if (points <= 0) {
            return;
        }
        preferences.edit().putInt(KEY_TOTAL_POINTS, getTotalPoints() + points).apply();
    }

    public int getHighestScore() {
        return preferences.getInt(KEY_HIGHEST_SCORE, 0);
    }

    public int getGamesCompleted() {
        return preferences.getInt(KEY_GAMES_COMPLETED, 0);
    }

    public int getCorrectAnswers() {
        return preferences.getInt(KEY_CORRECT_ANSWERS, 0);
    }

    public int getQuestionsAttempted() {
        return preferences.getInt(KEY_QUESTIONS_ATTEMPTED, 0);
    }

    public Set<String> getBadgesUnlocked() {
        String raw = preferences.getString(KEY_BADGES, "");
        LinkedHashSet<String> badges = new LinkedHashSet<>();
        if (raw == null || raw.trim().isEmpty()) {
            return badges;
        }
        String[] parts = raw.split("\\|");
        badges.addAll(Arrays.asList(parts));
        badges.remove("");
        return badges;
    }

    public int getBadgeCount() {
        return getBadgesUnlocked().size();
    }

    public void unlockBadges(Iterable<String> badges) {
        Set<String> stored = getBadgesUnlocked();
        boolean changed = false;
        for (String badge : badges) {
            if (badge != null && !badge.trim().isEmpty() && stored.add(badge.trim())) {
                changed = true;
            }
        }
        if (changed) {
            preferences.edit().putString(KEY_BADGES, joinBadges(stored)).apply();
        }
    }

    /**
     * Records a finished game. Points are added during play via {@link #addPoints(int)}
     * so they persist immediately; this method updates high score, games, accuracy, and badges.
     */
    public void recordFinishedGame(int gameScore, int correctThisGame, int questionsThisGame,
                                   Iterable<String> badgesEarned) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_HIGHEST_SCORE, Math.max(getHighestScore(), gameScore));
        editor.putInt(KEY_GAMES_COMPLETED, getGamesCompleted() + 1);
        editor.putInt(KEY_CORRECT_ANSWERS, getCorrectAnswers() + Math.max(0, correctThisGame));
        editor.putInt(KEY_QUESTIONS_ATTEMPTED,
                getQuestionsAttempted() + Math.max(0, questionsThisGame));

        Set<String> stored = getBadgesUnlocked();
        if (badgesEarned != null) {
            for (String badge : badgesEarned) {
                if (badge != null && !badge.trim().isEmpty()) {
                    stored.add(badge.trim());
                }
            }
        }
        editor.putString(KEY_BADGES, joinBadges(stored));
        editor.apply();
    }

    public PlayerProgress getProgress() {
        return new PlayerProgress(
                getPlayerName(),
                getTotalPoints(),
                getHighestScore(),
                getGamesCompleted(),
                getCorrectAnswers(),
                getQuestionsAttempted(),
                getBadgesUnlocked()
        );
    }

    private String joinBadges(Set<String> badges) {
        StringBuilder builder = new StringBuilder();
        for (String badge : badges) {
            if (builder.length() > 0) {
                builder.append('|');
            }
            builder.append(badge);
        }
        return builder.toString();
    }
}
