package com.healthybitquest.app.game;

/**
 * Result of checking one drag-and-drop answer.
 */
public class AnswerResult {

    private final boolean correct;
    private final int pointsAwarded;
    private final int newScore;
    private final String explanation;

    private AnswerResult(boolean correct, int pointsAwarded, int newScore, String explanation) {
        this.correct = correct;
        this.pointsAwarded = pointsAwarded;
        this.newScore = newScore;
        this.explanation = explanation == null ? "" : explanation;
    }

    public static AnswerResult correct(int pointsAwarded, int newScore, String explanation) {
        return new AnswerResult(true, pointsAwarded, newScore, explanation);
    }

    public static AnswerResult incorrect(String explanation) {
        return new AnswerResult(false, 0, 0, explanation);
    }

    public boolean isCorrect() {
        return correct;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public int getNewScore() {
        return newScore;
    }

    public String getExplanation() {
        return explanation;
    }
}
