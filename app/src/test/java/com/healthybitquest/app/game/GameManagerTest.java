package com.healthybitquest.app.game;

import com.healthybitquest.app.AppConstants;
import com.healthybitquest.app.model.FoodItem;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GameManagerTest {

    private GameManager gameManager;

    @Before
    public void setUp() {
        gameManager = new GameManager();
        gameManager.startNewGame(sampleFoods(), new Random(1));
    }

    @Test
    public void correctClassificationAwardsTenPoints() {
        FoodItem food = gameManager.getCurrentFood();
        AnswerResult result = gameManager.checkAnswer(food.getCategory());

        assertTrue(result.isCorrect());
        assertEquals(AppConstants.POINTS_PER_CORRECT, result.getPointsAwarded());
        assertEquals(10, gameManager.getScore());
        assertEquals(1, gameManager.getCorrectCount());
    }

    @Test
    public void incorrectClassificationAwardsNoPoints() {
        FoodItem food = gameManager.getCurrentFood();
        String wrong = food.isHealthy()
                ? AppConstants.CATEGORY_LESS_HEALTHY
                : AppConstants.CATEGORY_HEALTHY;

        AnswerResult result = gameManager.checkAnswer(wrong);

        assertFalse(result.isCorrect());
        assertEquals(0, result.getPointsAwarded());
        assertEquals(0, gameManager.getScore());
        assertFalse(result.getExplanation().isEmpty());
    }

    @Test
    public void eightCorrectAnswersEqualEightyPoints() {
        assertEquals(80, GameManager.calculateScore(8));
        assertEquals(100, GameManager.calculateScore(10));
        assertEquals(0, GameManager.calculateScore(0));
    }

    @Test
    public void gameFinishesAfterTenQuestions() {
        for (int i = 0; i < AppConstants.QUESTIONS_PER_GAME; i++) {
            FoodItem food = gameManager.getCurrentFood();
            gameManager.checkAnswer(food.getCategory());
            gameManager.moveToNextQuestion();
        }

        assertTrue(gameManager.isFinished());
        assertEquals(100, gameManager.getScore());
        assertEquals(10, gameManager.getCorrectCount());
        assertEquals(0, gameManager.getIncorrectCount());
    }

    @Test
    public void skipAfterIncorrectDoesNotAwardPoints() {
        FoodItem food = gameManager.getCurrentFood();
        String wrong = food.isHealthy()
                ? AppConstants.CATEGORY_LESS_HEALTHY
                : AppConstants.CATEGORY_HEALTHY;
        gameManager.checkAnswer(wrong);
        gameManager.skipCurrentQuestion();
        gameManager.moveToNextQuestion();

        assertEquals(0, gameManager.getScore());
        assertEquals(1, gameManager.getIncorrectCount());
        assertEquals(2, gameManager.getCurrentQuestionNumber());
    }

    private List<FoodItem> sampleFoods() {
        List<FoodItem> foods = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String category = i % 2 == 0
                    ? AppConstants.CATEGORY_HEALTHY
                    : AppConstants.CATEGORY_LESS_HEALTHY;
            foods.add(new FoodItem(
                    i,
                    "Food " + i,
                    category,
                    "food_default",
                    "Explanation for food " + i
            ));
        }
        return foods;
    }
}
