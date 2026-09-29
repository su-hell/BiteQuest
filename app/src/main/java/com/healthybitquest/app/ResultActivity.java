package com.healthybitquest.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.healthybitquest.app.game.BadgeCalculator;
import com.healthybitquest.app.storage.LocalStorageManager;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score = getIntent().getIntExtra(AppConstants.EXTRA_GAME_SCORE, 0);
        int correct = getIntent().getIntExtra(AppConstants.EXTRA_CORRECT, 0);
        int incorrect = getIntent().getIntExtra(AppConstants.EXTRA_INCORRECT, 0);
        int accuracy = (correct + incorrect) <= 0
                ? 0
                : Math.round((correct * 100f) / (correct + incorrect));

        TextView scoreText = findViewById(R.id.textResultScore);
        TextView playerNameText = findViewById(R.id.textResultPlayerName);
        TextView correctText = findViewById(R.id.textResultCorrect);
        TextView accuracyText = findViewById(R.id.textResultAccuracy);
        TextView badgeText = findViewById(R.id.textResultBadge);
        TextView messageText = findViewById(R.id.textResultMessage);

        String playerName = new LocalStorageManager(this).getPlayerName();
        playerNameText.setText(getString(R.string.result_player_name, playerName));
        scoreText.setText(getString(R.string.result_score, score, AppConstants.MAX_SCORE));
        correctText.setText(getString(R.string.result_correct, correct, incorrect + correct, incorrect));
        accuracyText.setText(getString(R.string.result_accuracy, accuracy));
        messageText.setText(BadgeCalculator.summaryForScore(score));

        List<String> badges = BadgeCalculator.badgesForScore(score);
        if (badges.isEmpty()) {
            badgeText.setText(R.string.result_no_badge);
        } else {
            badgeText.setText(getString(R.string.result_badges, String.join(", ", badges)));
        }

        Button playAgain = findViewById(R.id.buttonPlayAgain);
        Button backHome = findViewById(R.id.buttonBackHome);

        playAgain.setOnClickListener(v -> {
            startActivity(new Intent(this, InstructionsActivity.class));
            finish();
        });
        backHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
