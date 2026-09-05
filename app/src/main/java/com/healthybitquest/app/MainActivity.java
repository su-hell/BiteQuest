package com.healthybitquest.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.healthybitquest.app.storage.LocalStorageManager;

/**
 * Launcher activity. Sends new players to name entry, returning players to Home.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalStorageManager storage = new LocalStorageManager(this);
        Intent intent;
        if (storage.hasPlayerName()) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent = new Intent(this, NameActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
