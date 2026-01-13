package com.example.pong_gmae;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener {

    View View1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose);

        View1 = findViewById(R.id.View1);
        View1.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent play = new Intent(this, GameActivity.class);
        startActivity(play);
    }
}