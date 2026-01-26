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


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnBack;
    EditText playerName;

    String userInput = playerName.getText().toString();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        btnBack= findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        playerName = findViewById(R.id.PlayerName);





    }



    @Override
    public void onClick(View v) {

        SharedPreferences prefs = getSharedPreferences("PongPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit(); // Get the editor
        editor.putString("playerName", userInput);
        editor.apply();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


}