package com.example.pong_gmae;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PongGame extends SurfaceView implements Runnable {

    private Thread gameThread = null;
    private SurfaceHolder surfaceHolder;

    private int player1Score = 0;
    private int player2Score = 0;


    // volatile means this variable is accessed from different threads
    private volatile boolean playing;

    private Canvas canvas;
    private Paint paint;

    private int screenX;
    private int screenY;

    // game objects
    private Paddle paddle1; // Human (Left)
    private Paddle paddle2; // AI (Right)
    private Ball ball;



    private long fps;
    private long timeThisFrame;

    // constructor
    public PongGame(Context context, int x, int y) {
        super(context);

        this.screenX = x;
        this.screenY = y;

        // start drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        // make paddle and ball
        // Player 1: Left side
        paddle1 = new Paddle(50, screenY / 2f, 20, screenY / 4f, Color.WHITE);

        // Player 2 (AI): Right side
        paddle2 = new Paddle(screenX - 70, screenY / 2f, 20, screenY / 4f, Color.WHITE);

        // ball starts in the middle
        ball = new Ball(screenX / 2f, screenY / 2f, 25, Color.WHITE);

        startNewGame();
    }

    private void startNewGame() {
        // reset ball position
        ball = new Ball(screenX / 2f, screenY / 2f, 25, Color.WHITE);
    }

    @Override
    public void run() {
        while (playing) {
            long startFrameTime = System.currentTimeMillis();

            //update positions
            update();

            // draw the frame
            draw();

            // calculate fps to control speed
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update() {
        ball.update(fps);
        paddle1.update(fps);

        // simple ai logic
        // If ball is lower than paddle center, move down
        if (ball.getRect().centerY() > paddle2.getRect().centerY()) {
            paddle2.setMovementState(1);
        } else {
            paddle2.setMovementState(-1);
        }
        paddle2.update(fps);

        // collision detection

        // 1. Bounce off Top and Bottom
        if (ball.getRect().top < 0 || ball.getRect().bottom > screenY) {
            ball.reverseYVelocity();
        }

        // 2. Hit Left Paddle (Player 1)
        if (RectF.intersects(paddle1.getRect(), ball.getRect())) {
            ball.reverseXVelocity();
            ball.resetX(paddle1.getRect().right + 1);
            ball.increaseSpeed();
        }

        // 3. Hit Right Paddle (AI)
        if (RectF.intersects(paddle2.getRect(), ball.getRect())) {
            ball.reverseXVelocity();
            ball.resetX(paddle2.getRect().left - ball.getWidth() - 1);
            ball.increaseSpeed();
        }

        // 4. Scoring (Game Over / Reset)
        if (ball.getRect().left < 0 || ball.getRect().right > screenX) {
            startNewGame();
        }

        // Ball goes past Left side (Player 1 missed)
        if (ball.getRect().left < 0) {
            player2Score++; // AI gets a point
            startNewGame();
        }

        // Ball goes past Right side (AI missed)
        if (ball.getRect().right > screenX) {
            player1Score++; // player gets a point
            startNewGame();
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK); // Clear screen

            // Draw Game Objects
            paddle1.draw(canvas);
            paddle2.draw(canvas);
            ball.draw(canvas);

            // draw scores
            paint.setColor(Color.WHITE);
            paint.setTextSize(100); // Make it big

            // Player 1 Score (Left Side)
            canvas.drawText("" + player1Score, screenX / 4f, 150, paint);

            // Player 2 Score (Right Side)
            canvas.drawText("" + player2Score, (screenX / 4f) * 3, 150, paint);

            // Optional: Draw a dashed center line
            paint.setStrokeWidth(5);
            for(int i = 0; i < screenY; i += 50) {
                canvas.drawLine(screenX / 2f, i, screenX / 2f, i + 25, paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // If touch is in the top half of the screen, move up
                if (motionEvent.getY() < screenY / 2f) {
                    paddle1.setMovementState(-1);
                } else {
                    paddle1.setMovementState(1);
                }
                break;

            case MotionEvent.ACTION_UP:
                paddle1.setMovementState(0);
                break;
        }
        return true;
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


}
