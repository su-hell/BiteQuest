package com.healthybitquest.app;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.healthybitquest.app.game.AnswerResult;
import com.healthybitquest.app.game.BadgeCalculator;
import com.healthybitquest.app.game.GameManager;
import com.healthybitquest.app.model.FoodItem;
import com.healthybitquest.app.storage.LocalStorageManager;
import com.healthybitquest.app.utils.JsonLoader;
import com.healthybitquest.app.utils.SoundPlayer;

import java.util.List;

public class GameActivity extends AppCompatActivity {

    private final GameManager gameManager = new GameManager();
    private final JsonLoader jsonLoader = new JsonLoader();
    private final SoundPlayer soundPlayer = new SoundPlayer();

    private LocalStorageManager storage;

    private View loadingPanel;
    private View errorPanel;
    private View gamePanel;
    private TextView errorText;
    private TextView questionText;
    private TextView scoreText;
    private TextView foodNameText;
    private TextView feedbackText;
    private ImageView foodImage;
    private View foodCard;
    private View healthyDrop;
    private View lessHealthyDrop;
    private ProgressBar questionProgress;
    private Button continueButton;
    private Button tryAgainButton;
    private Button soundToggleButton;

    private boolean dragInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        storage = new LocalStorageManager(this);
        bindViews();
        soundPlayer.load(this);
        setupDragAndDrop();
        loadGameData();
    }

    @Override
    protected void onDestroy() {
        soundPlayer.release();
        super.onDestroy();
    }

    private void bindViews() {
        loadingPanel = findViewById(R.id.panelLoading);
        errorPanel = findViewById(R.id.panelError);
        gamePanel = findViewById(R.id.panelGame);
        errorText = findViewById(R.id.textError);
        questionText = findViewById(R.id.textQuestionNumber);
        scoreText = findViewById(R.id.textGameScore);
        foodNameText = findViewById(R.id.textFoodName);
        feedbackText = findViewById(R.id.textFeedback);
        foodImage = findViewById(R.id.imageFood);
        foodCard = findViewById(R.id.foodCard);
        healthyDrop = findViewById(R.id.dropHealthy);
        lessHealthyDrop = findViewById(R.id.dropLessHealthy);
        questionProgress = findViewById(R.id.progressQuestions);
        continueButton = findViewById(R.id.buttonContinue);
        tryAgainButton = findViewById(R.id.buttonTryAgain);
        soundToggleButton = findViewById(R.id.buttonSoundToggle);

        Button retryLoadButton = findViewById(R.id.buttonRetryLoad);
        retryLoadButton.setOnClickListener(v -> loadGameData());
        continueButton.setOnClickListener(v -> onContinueClicked());
        tryAgainButton.setOnClickListener(v -> resetCurrentQuestionForRetry());
        soundToggleButton.setOnClickListener(v -> toggleSound());
    }

    private void toggleSound() {
        boolean muted = !soundPlayer.isMuted();
        soundPlayer.setMuted(muted);
        soundToggleButton.setText(muted ? R.string.sound_turn_on : R.string.sound_mute);
        soundToggleButton.setContentDescription(
                getString(muted ? R.string.sound_turn_on : R.string.sound_mute));
    }

    private void loadGameData() {
        showLoading();
        jsonLoader.loadFromAssetsAsync(this)
                .thenAccept(foods -> runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    startGame(foods);
                }))
                .exceptionally(error -> {
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        showError(getString(R.string.error_load_game));
                    });
                    return null;
                });
    }

    private void startGame(List<FoodItem> foods) {
        try {
            gameManager.startNewGame(foods);
        } catch (IllegalStateException exception) {
            showError(getString(R.string.error_load_game));
            return;
        }
        questionProgress.setMax(gameManager.getTotalQuestions());
        showGame();
        displayCurrentQuestion();
    }

    private void setupDragAndDrop() {
        foodCard.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (gameManager.isCurrentQuestionResolved() || dragInProgress) {
                    return true;
                }
                view.getParent().requestDisallowInterceptTouchEvent(true);
                ClipData data = ClipData.newPlainText("food", "food");
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
                dragInProgress = view.startDragAndDrop(data, shadow, view, 0);
                return true;
            }
            return false;
        });

        View.OnDragListener dropListener = (view, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription() != null
                            && event.getClipDescription().hasMimeType(
                            android.content.ClipDescription.MIMETYPE_TEXT_PLAIN);
                case DragEvent.ACTION_DRAG_ENTERED:
                    view.setAlpha(0.7f);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    view.setAlpha(1f);
                    if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                        dragInProgress = false;
                    }
                    return true;
                case DragEvent.ACTION_DROP:
                    view.setAlpha(1f);
                    dragInProgress = false;
                    String category = AppConstants.CATEGORY_HEALTHY;
                    if (view.getId() == R.id.dropLessHealthy) {
                        category = AppConstants.CATEGORY_LESS_HEALTHY;
                    }
                    handleDrop(category);
                    return true;
                default:
                    return false;
            }
        };

        healthyDrop.setOnDragListener(dropListener);
        lessHealthyDrop.setOnDragListener(dropListener);
    }

    private void handleDrop(String category) {
        if (gameManager.isCurrentQuestionResolved()) {
            return;
        }

        AnswerResult result = gameManager.checkAnswer(category);
        if (result.isCorrect()) {
            storage.addPoints(result.getPointsAwarded());
            soundPlayer.playSuccess();
            feedbackText.setTextColor(ContextCompat.getColor(this, R.color.feedback_correct));
            feedbackText.setText(getString(R.string.feedback_correct, result.getExplanation()));
            continueButton.setVisibility(View.VISIBLE);
            tryAgainButton.setVisibility(View.GONE);
            foodCard.setAlpha(0.4f);
            updateScoreLabel();
        } else {
            soundPlayer.playIncorrect();
            feedbackText.setTextColor(ContextCompat.getColor(this, R.color.feedback_incorrect));
            feedbackText.setText(getString(R.string.feedback_incorrect, result.getExplanation()));
            continueButton.setVisibility(View.VISIBLE);
            tryAgainButton.setVisibility(View.VISIBLE);
        }
    }

    private void resetCurrentQuestionForRetry() {
        feedbackText.setText(R.string.drag_instruction);
        feedbackText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        continueButton.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
        foodCard.setAlpha(1f);
    }

    private void onContinueClicked() {
        if (!gameManager.isCurrentQuestionResolved()) {
            gameManager.skipCurrentQuestion();
        }
        gameManager.moveToNextQuestion();
        if (gameManager.isFinished()) {
            finishGame();
        } else {
            displayCurrentQuestion();
        }
    }

    private void displayCurrentQuestion() {
        FoodItem food = gameManager.getCurrentFood();
        if (food == null) {
            finishGame();
            return;
        }

        dragInProgress = false;
        foodCard.setAlpha(1f);
        continueButton.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
        feedbackText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        feedbackText.setText(R.string.drag_instruction);

        questionText.setText(getString(R.string.question_number,
                gameManager.getCurrentQuestionNumber(),
                gameManager.getTotalQuestions()));
        updateScoreLabel();
        foodNameText.setText(food.getName());
        foodImage.setImageResource(resolveFoodImage(food.getImage()));
        questionProgress.setProgress(gameManager.getCurrentQuestionNumber());
    }

    private void updateScoreLabel() {
        scoreText.setText(getString(R.string.game_score, gameManager.getScore()));
    }

    private int resolveFoodImage(String imageName) {
        if (imageName == null || imageName.trim().isEmpty()) {
            return R.drawable.food_default;
        }
        int resourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        if (resourceId == 0) {
            return R.drawable.food_default;
        }
        return resourceId;
    }

    private void finishGame() {
        storage.recordFinishedGame(
                gameManager.getScore(),
                gameManager.getCorrectCount(),
                gameManager.getTotalQuestions(),
                BadgeCalculator.badgesForScore(gameManager.getScore())
        );

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(AppConstants.EXTRA_GAME_SCORE, gameManager.getScore());
        intent.putExtra(AppConstants.EXTRA_CORRECT, gameManager.getCorrectCount());
        intent.putExtra(AppConstants.EXTRA_INCORRECT, gameManager.getIncorrectCount());
        startActivity(intent);
        finish();
    }

    private void showLoading() {
        loadingPanel.setVisibility(View.VISIBLE);
        errorPanel.setVisibility(View.GONE);
        gamePanel.setVisibility(View.GONE);
    }

    private void showGame() {
        loadingPanel.setVisibility(View.GONE);
        errorPanel.setVisibility(View.GONE);
        gamePanel.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        loadingPanel.setVisibility(View.GONE);
        gamePanel.setVisibility(View.GONE);
        errorPanel.setVisibility(View.VISIBLE);
        errorText.setText(message);
    }
}
