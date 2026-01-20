package com.example.pong_gmae;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;

public class PongGame extends SurfaceView implements Runnable {

    private Thread gameThread = null;
    private SurfaceHolder surfaceHolder;

    private int player1Score = 0;
    private int player2Score = 0;

    private volatile boolean playing;

    private Canvas canvas;
    private Paint paint;

    private int screenX;
    private int screenY;

    private Paddle paddle1;
    private Paddle paddle2;
    private Ball ball;

    private long fps = 60;
    private long timeThisFrame;

    public PongGame(Context context, int x, int y) {
        super(context);
        this.screenX = x;
        this.screenY = y;

        surfaceHolder = getHolder();
        paint = new Paint();

        // Player 1 (Left)
        paddle1 = new Paddle(50, screenY / 2f, 20, screenY / 4f, Color.WHITE);
        // Player 2 (AI - Right)
        paddle2 = new Paddle(screenX - 70, screenY / 2f, 20, screenY / 4f, Color.WHITE);

        paddle2.setSpeed(600);

        ball = new Ball(screenX / 2f, screenY / 2f, 25, Color.WHITE);

        startNewGame();
    }

    private void startNewGame() {

        ball = new Ball(screenX / 2f, screenY / 2f, 25, Color.WHITE);
        ball.resetX(screenX / 2f);
        ball.resetY(screenY / 2f);
        ball.resetBall();

    }

    @Override
    public void run() {
        while (playing) {
            long startFrameTime = System.currentTimeMillis();

            update();
            draw();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update() {
        ball.update(fps);
        paddle1.update(fps);
        paddle2.update(fps); // Move AI update here for consistency

        // --- NEW: Keep Paddles inside the screen ---
        // Clamp Player 1
        if (paddle1.getRect().top < 0) { paddle1.getRect().top = 0; paddle1.y = 0; }
        if (paddle1.getRect().bottom > screenY) { paddle1.getRect().bottom = screenY; paddle1.y = screenY - paddle1.getHeight(); }

        // Clamp AI
        if (paddle2.getRect().top < 0) { paddle2.getRect().top = 0; paddle2.y = 0; }
        if (paddle2.getRect().bottom > screenY) { paddle2.getRect().bottom = screenY; paddle2.y = screenY - paddle2.getHeight(); }
        // -------------------------------------------

        // Simple AI Logic
        if (ball.getRect().centerY() > paddle2.getRect().centerY()) {
            paddle2.setMovementState(1); // Move Down
        } else {
            paddle2.setMovementState(-1); // Move Up
        }

        // Ball Collisions with Top/Bottom of Screen
        if (ball.getRect().top < 0 || ball.getRect().bottom > screenY) {
            ball.reverseYVelocity();
            // Nudge ball back onto screen to prevent sticking
            if(ball.getRect().top < 0) ball.resetY(0);
            if(ball.getRect().bottom > screenY) ball.resetY(screenY - ball.getHeight());
        }

        // Hit Left Paddle (Player)
        if (RectF.intersects(paddle1.getRect(), ball.getRect())) {
            ball.reverseXVelocity();
            ball.resetX(paddle1.getRect().right + 1);

            // - = hit top half, + = hit bottom half
            float relativeIntersectY = (paddle1.getRect().centerY() - ball.getRect().centerY());
            float normalizedIntersectY = (relativeIntersectY / (paddle1.getHeight() / 2));

            float bounceAngle = normalizedIntersectY * Math.abs(ball.getxVelocity());
            ball.setyVelocity(-bounceAngle);

            ball.increaseSpeed();
        }

        // Hit Right Paddle (AI)
        if (RectF.intersects(paddle2.getRect(), ball.getRect())) {
            ball.reverseXVelocity();
            ball.resetX(paddle2.getRect().left - ball.getWidth() - 1);

            // Calculate hit position for AI paddle
            float relativeIntersectY = (paddle2.getRect().centerY() - ball.getRect().centerY());
            float normalizedIntersectY = (relativeIntersectY / (paddle2.getHeight() / 2));

            float bounceAngle = normalizedIntersectY * Math.abs(ball.getxVelocity());
            ball.setyVelocity(-bounceAngle);

            ball.increaseSpeed();
        }

        // --- FIX: SCORING LOGIC ---
        // (Removed the generic 'startNewGame' block that was deleting the ball before checking score)

        // Ball goes past Left side (Player 1 missed -> AI gets point)
        if (ball.getRect().left < 0) {
            player2Score++;
            startNewGame();
        }

        // Ball goes past Right side (AI missed -> Player 1 gets point)
        if (ball.getRect().right > screenX) {
            player1Score++;
            startNewGame();
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            paddle1.draw(canvas);
            paddle2.draw(canvas);
            ball.draw(canvas);

            paint.setColor(Color.WHITE);
            paint.setTextSize(100);

            // Draw Scores
            canvas.drawText("" + player1Score, screenX / 4f, 150, paint);
            canvas.drawText("" + player2Score, (screenX / 4f) * 3, 150, paint);

            // Center Line
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
            case MotionEvent.ACTION_MOVE:
                paddle1.moveTo(motionEvent.getY(), screenY);
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