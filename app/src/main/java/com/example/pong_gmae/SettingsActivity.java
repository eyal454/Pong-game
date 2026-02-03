package com.example.pong_gmae;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;


public class SettingsActivity extends AppCompatActivity {

    // 1. Declare variables
    private EditText playerNameInput;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 2. Initialize using YOUR EXACT XML IDs
        playerNameInput = findViewById(R.id.PlayerName); // Matches your XML
        backButton = findViewById(R.id.btnBack);         // Matches your XML

        // 3. Load the saved name (so the user sees what they typed before)
        SharedPreferences prefs = getSharedPreferences("PongPrefs", MODE_PRIVATE);
        String existingName = prefs.getString("playerName", "");
        playerNameInput.setText(existingName);

        // 4. Save and Exit when "Back" is clicked
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameToSave = playerNameInput.getText().toString();

                if (!nameToSave.isEmpty()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("playerName", nameToSave);
                    editor.apply(); // Saves the data

                    Toast.makeText(SettingsActivity.this, "Name Saved!", Toast.LENGTH_SHORT).show();
                }

                // Close Settings and go back to Menu
                finish();
            }
        });
    }
}

