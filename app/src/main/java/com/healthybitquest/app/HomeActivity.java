package com.healthybitquest.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.healthybitquest.app.model.PlayerProgress;
import com.healthybitquest.app.storage.LocalStorageManager;

public class HomeActivity extends AppCompatActivity {

    private LocalStorageManager storage;
    private TextView welcomeText;
    private TextView pointsText;
    private TextView badgesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        storage = new LocalStorageManager(this);
        welcomeText = findViewById(R.id.textWelcome);
        pointsText = findViewById(R.id.textHomePoints);
        badgesText = findViewById(R.id.textHomeBadges);

        Button playButton = findViewById(R.id.buttonPlay);
        Button progressButton = findViewById(R.id.buttonProgress);
        Button changeNameButton = findViewById(R.id.buttonChangeName);

        playButton.setOnClickListener(v ->
                startActivity(new Intent(this, InstructionsActivity.class)));
        progressButton.setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class)));
        changeNameButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NameActivity.class);
            intent.putExtra(AppConstants.EXTRA_CHANGE_NAME, true);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayerProgress progress = storage.getProgress();
        welcomeText.setText(getString(R.string.welcome_player, progress.getPlayerName()));
        pointsText.setText(getString(R.string.home_points, progress.getTotalPoints()));
        badgesText.setText(getString(R.string.home_badges, progress.getBadgeCount()));
    }
}
