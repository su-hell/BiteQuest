package com.healthybitquest.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        Button startButton = findViewById(R.id.buttonStartGame);
        startButton.setOnClickListener(v -> {
            startActivity(new Intent(this, GameActivity.class));
            finish();
        });
    }
}
