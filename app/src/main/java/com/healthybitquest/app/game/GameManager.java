package com.healthybitquest.app.game;

import com.healthybitquest.app.AppConstants;
import com.healthybitquest.app.model.FoodItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Scoring and question flow for one 10-question round.
 */
public class GameManager {

    private final List<FoodItem> questions = new ArrayList<>();
    private int currentIndex;
    private int score;
    private int correctCount;
    private int incorrectCount;
    private boolean finished;
    private boolean currentQuestionResolved;

    public void startNewGame(List<FoodItem> allFoods) {
        startNewGame(allFoods, new Random());
    }

    public void startNewGame(List<FoodItem> allFoods, Random random) {
        if (allFoods == null) {
            throw new IllegalArgumentException("Food list cannot be null.");
        }

        List<FoodItem> validFoods = new ArrayList<>();
        for (FoodItem food : allFoods) {
            if (food != null && food.isValid()) {
                validFoods.add(food);
            }
        }

        if (validFoods.size() < AppConstants.QUESTIONS_PER_GAME) {
            throw new IllegalStateException("Need at least "
                    + AppConstants.QUESTIONS_PER_GAME
                    + " valid foods to start a game.");
        }

        Collections.shuffle(validFoods, random);
        questions.clear();
        questions.addAll(validFoods.subList(0, AppConstants.QUESTIONS_PER_GAME));

        currentIndex = 0;
        score = 0;
        correctCount = 0;
        incorrectCount = 0;
        finished = false;
        currentQuestionResolved = false;
    }

    public FoodItem getCurrentFood() {
        if (finished || currentIndex < 0 || currentIndex >= questions.size()) {
            return null;
        }
        return questions.get(currentIndex);
    }

    public int getCurrentQuestionNumber() {
        return currentIndex + 1;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public int getScore() {
        return score;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isCurrentQuestionResolved() {
        return currentQuestionResolved;
    }

    /**
     * Checks the dropped category. Correct answers award points once.
     * Incorrect answers do not award points and do not advance the question.
     */
    public AnswerResult checkAnswer(String droppedCategory) {
        FoodItem current = getCurrentFood();
        if (current == null || currentQuestionResolved) {
            return AnswerResult.incorrect("");
        }

        boolean isCorrect = current.getCategory().equalsIgnoreCase(droppedCategory);
        if (isCorrect) {
            score += AppConstants.POINTS_PER_CORRECT;
            correctCount++;
            currentQuestionResolved = true;
            return AnswerResult.correct(
                    AppConstants.POINTS_PER_CORRECT,
                    score,
                    current.getExplanation()
            );
        }

        return AnswerResult.incorrect(current.getExplanation());
    }

    /**
     * Skip the current question without points after an incorrect attempt.
     */
    public void skipCurrentQuestion() {
        if (finished || currentQuestionResolved) {
            return;
        }
        incorrectCount++;
        currentQuestionResolved = true;
    }

    /**
     * Moves to the next question. Call only after the current question is resolved.
     */
    public void moveToNextQuestion() {
        if (!currentQuestionResolved || finished) {
            return;
        }
        currentIndex++;
        currentQuestionResolved = false;
        if (currentIndex >= questions.size()) {
            finished = true;
        }
    }

    public int getAccuracyPercent() {
        int attempted = correctCount + incorrectCount;
        if (attempted <= 0) {
            return 0;
        }
        return Math.round((correctCount * 100f) / attempted);
    }

    public static int calculateScore(int correctAnswers) {
        return Math.max(0, correctAnswers) * AppConstants.POINTS_PER_CORRECT;
    }
}
