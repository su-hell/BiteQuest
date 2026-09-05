package com.healthybitquest.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.healthybitquest.app.storage.LocalStorageManager;

/**
 * Simple first-launch (and optional change-name) screen. No passwords or accounts.
 */
public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        LocalStorageManager storage = new LocalStorageManager(this);
        EditText nameInput = findViewById(R.id.inputPlayerName);
        Button saveButton = findViewById(R.id.buttonSaveName);

        boolean changingName = getIntent().getBooleanExtra(AppConstants.EXTRA_CHANGE_NAME, false);
        if (changingName && storage.hasPlayerName()) {
            nameInput.setText(storage.getPlayerName());
        }

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText() == null ? "" : nameInput.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, R.string.error_enter_name, Toast.LENGTH_SHORT).show();
                return;
            }
            storage.savePlayerName(name);
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
