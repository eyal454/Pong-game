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

    View View1, View2, View3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose);

        View1 = findViewById(R.id.View1);
        View2 = findViewById(R.id.View2);
        View3 = findViewById(R.id.View3);
        View1.setOnClickListener(this);
        View2.setOnClickListener(this);
        View3.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (View1 == view)
        {
            Intent intent = new Intent(ChooseActivity.this, GameActivity.class);
            intent.putExtra("MODE", "CLASSIC");
            startActivity(intent);

        }

        if (View2 == view)
        {
            Intent intent = new Intent(ChooseActivity.this, GameActivity.class);
            intent.putExtra("MODE", "ENDLESS");
            startActivity(intent);

        }

        if (View3 == view)
        {
            Intent intent = new Intent(ChooseActivity.this, GameActivity.class);
            intent.putExtra("MODE", "DOUBLE");
            startActivity(intent);

        }


    }
}