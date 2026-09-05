package com.healthybitquest.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.healthybitquest.app.model.PlayerProgress;
import com.healthybitquest.app.storage.LocalStorageManager;

public class ProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        LocalStorageManager storage = new LocalStorageManager(this);
        PlayerProgress progress = storage.getProgress();

        TextView nameText = findViewById(R.id.textProgressName);
        TextView pointsText = findViewById(R.id.textProgressPoints);
        TextView highScoreText = findViewById(R.id.textProgressHighScore);
        TextView gamesText = findViewById(R.id.textProgressGames);
        TextView correctText = findViewById(R.id.textProgressCorrect);
        TextView attemptedText = findViewById(R.id.textProgressAttempted);
        TextView accuracyText = findViewById(R.id.textProgressAccuracy);
        TextView badgesText = findViewById(R.id.textProgressBadges);

        nameText.setText(getString(R.string.progress_player, progress.getPlayerName()));
        pointsText.setText(getString(R.string.progress_points, progress.getTotalPoints()));
        highScoreText.setText(getString(R.string.progress_high_score, progress.getHighestScore()));
        gamesText.setText(getString(R.string.progress_games, progress.getGamesCompleted()));
        correctText.setText(getString(R.string.progress_correct, progress.getCorrectAnswers()));
        attemptedText.setText(getString(R.string.progress_attempted, progress.getQuestionsAttempted()));
        accuracyText.setText(getString(R.string.progress_accuracy, progress.getAccuracyPercent()));

        if (progress.getBadgeCount() == 0) {
            badgesText.setText(R.string.progress_no_badges);
        } else {
            badgesText.setText(getString(R.string.progress_badges,
                    String.join("\n", progress.getBadgesUnlocked())));
        }

        Button backButton = findViewById(R.id.buttonProgressBack);
        backButton.setOnClickListener(v -> finish());
    }
}
