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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPlay, btnScore, btnSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnSet = findViewById(R.id.btnSet);
        btnPlay = findViewById(R.id.btnPlay);
        btnScore = findViewById(R.id.btnScore);
        btnSet.setOnClickListener(this);
        btnScore.setOnClickListener(this);
        btnPlay.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        if (btnSet == view)
        {
            Intent settings = new Intent(this,SettingsActivity.class);
            startActivity(settings);
        }

        if (btnPlay == view)
        {
            Intent play = new Intent(this,ChooseActivity.class);
            startActivity(play);
        }

        if (btnScore == view)
        {
            Intent score = new Intent(this,ScoreActivity.class);
            startActivity(score);
        }
    }
}