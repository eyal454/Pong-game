package com.example.pong_gmae;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.Display;
import android.graphics.Point;

public class GameActivity extends Activity {

    private PongGame pongGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        pongGame = new PongGame(this, size.x, size.y);
        setContentView(pongGame);

    }

    @Override
    protected void onResume() {
        super.onResume();
        pongGame.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pongGame.pause();
    }
}