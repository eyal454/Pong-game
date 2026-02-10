package com.example.pong_gmae;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch; // <--- This import was missing!
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    // 1. Declare variables
    private EditText playerNameInput;
    private Button backButton;
    private Switch soundSwitch; // <--- New variable for the switch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 2. Initialize Views
        playerNameInput = findViewById(R.id.PlayerName);
        backButton = findViewById(R.id.btnBack);

        // --- NEW: Find the switch from your XML ---
        soundSwitch = findViewById(R.id.SoundSwitch);

        // 3. Load Saved Data
        SharedPreferences prefs = getSharedPreferences("PongPrefs", MODE_PRIVATE);

        // Load Name
        String existingName = prefs.getString("playerName", "");
        playerNameInput.setText(existingName);

        // --- NEW: Load Sound Switch State ---
        // If no setting exists yet, default to true (Sound ON)
        boolean isSoundOn = prefs.getBoolean("sound_switch", true);
        soundSwitch.setChecked(isSoundOn);

        // 4. Save and Exit when "Back" is clicked
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();

                // Save Name
                String nameToSave = playerNameInput.getText().toString();
                if (!nameToSave.isEmpty()) {
                    editor.putString("playerName", nameToSave);
                }

                // --- NEW: Save the Switch State ---
                // This saves "true" if switch is On, "false" if Off
                editor.putBoolean("sound_switch", soundSwitch.isChecked());

                editor.apply(); // Saves the data

                Toast.makeText(SettingsActivity.this, "Settings Saved!", Toast.LENGTH_SHORT).show();

                // Close Settings and go back to Menu
                finish();
            }
        });
    }
}