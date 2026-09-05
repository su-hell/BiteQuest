package com.healthybitquest.app.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Snapshot of stored player progress from local SharedPreferences.
 */
public class PlayerProgress {

    private final String playerName;
    private final int totalPoints;
    private final int highestScore;
    private final int gamesCompleted;
    private final int correctAnswers;
    private final int questionsAttempted;
    private final Set<String> badgesUnlocked;

    public PlayerProgress(String playerName,
                          int totalPoints,
                          int highestScore,
                          int gamesCompleted,
                          int correctAnswers,
                          int questionsAttempted,
                          Set<String> badgesUnlocked) {
        this.playerName = playerName == null ? "" : playerName;
        this.totalPoints = Math.max(0, totalPoints);
        this.highestScore = Math.max(0, highestScore);
        this.gamesCompleted = Math.max(0, gamesCompleted);
        this.correctAnswers = Math.max(0, correctAnswers);
        this.questionsAttempted = Math.max(0, questionsAttempted);
        this.badgesUnlocked = badgesUnlocked == null
                ? new LinkedHashSet<>()
                : new LinkedHashSet<>(badgesUnlocked);
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public int getGamesCompleted() {
        return gamesCompleted;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getQuestionsAttempted() {
        return questionsAttempted;
    }

    public Set<String> getBadgesUnlocked() {
        return Collections.unmodifiableSet(badgesUnlocked);
    }

    public int getBadgeCount() {
        return badgesUnlocked.size();
    }

    public int getAccuracyPercent() {
        if (questionsAttempted <= 0) {
            return 0;
        }
        return Math.round((correctAnswers * 100f) / questionsAttempted);
    }
}
