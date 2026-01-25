package com.example.pong_gmae;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;

public class PongGame extends SurfaceView implements Runnable {

    private Thread gameThread = null;
    private SurfaceHolder surfaceHolder;
    private volatile boolean playing;
    private boolean isGameOver = false;

    // Game Objects
    private Paddle paddle1;
    private Paddle paddle2;
    // CHANGED: We now use a List because "Double Ball" mode needs 2 balls!
    private ArrayList<Ball> balls;

    // Scores and Screen
    private int player1Score = 0;
    private int player2Score = 0;
    private int screenX;
    private int screenY;
    private Canvas canvas;
    private Paint paint;

    // Timing
    private long fps = 60;
    private long timeThisFrame;

    // GAME MODES
    private String currentMode; // "CLASSIC", "ENDLESS", "DOUBLE"

    public PongGame(Context context, int x, int y, String mode) {
        super(context);
        this.screenX = x;
        this.screenY = y;
        this.currentMode = mode; // Save the mode chosen by the user

        surfaceHolder = getHolder();
        paint = new Paint();

        // 1. Setup Paddles
        paddle1 = new Paddle(50, screenY / 2f, 20, screenY / 4f, Color.WHITE);
        paddle2 = new Paddle(screenX - 70, screenY / 2f, 20, screenY / 4f, Color.WHITE);

        // Make AI faster
        paddle2.setSpeed(600);

        // 2. Initialize the Ball List
        balls = new ArrayList<>();

        startNewGame();
    }

    // This runs whenever a point is scored or the game starts
    private void startNewGame() {
        balls.clear();

        // LOGIC: How many balls do we need?
        if (currentMode.equals("DOUBLE")) {
            // Add Ball 1
            Ball b1 = new Ball(screenX / 2f, screenY / 2f, 25, Color.CYAN);
            b1.resetBall(); // Randomize direction
            balls.add(b1);

            // Add Ball 2 (Slightly delayed or different color)
            Ball b2 = new Ball(screenX / 2f, screenY / 2f, 25, Color.YELLOW);
            b2.resetBall();
            balls.add(b2);
        } else {
            // Just one ball for Classic and Endless
            Ball b = new Ball(screenX / 2f, screenY / 2f, 25, Color.WHITE);
            b.resetBall();
            balls.add(b);
        }

        // If Classic game just ended, reset scores
        if (isGameOver) {
            player1Score = 0;
            player2Score = 0;
            isGameOver = false;
        }
    }

    @Override
    public void run() {
        while (playing) {
            long startFrameTime = System.currentTimeMillis();

            if (!isGameOver) {
                update();
            }
            draw();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update() {
        // Update AI and Player Paddles
        paddle1.update(fps);
        paddle2.update(fps);

        // Keep paddles on screen
        if (paddle1.getRect().top < 0) paddle1.moveTo(0 + paddle1.getHeight()/2, screenY);
        if (paddle1.getRect().bottom > screenY) paddle1.moveTo(screenY - paddle1.getHeight()/2, screenY);
        if (paddle2.getRect().top < 0) paddle2.y = 0;
        if (paddle2.getRect().bottom > screenY) paddle2.y = screenY - paddle2.getHeight();

        // AI logic
        if (!balls.isEmpty()) {
            Ball targetBall = null;
            float maxX = -1; // Start with a very small number

            // Loop through ALL balls to find the best target
            for (Ball b : balls) {
                // Logic:
                // 1. Is the ball moving towards the AI? (Velocity X > 0)
                // 2. Is this ball closer to the AI than the last one we checked? (Higher X is closer to right side)
                if (b.getxVelocity() > 0 && b.getRect().right > maxX) {
                    maxX = b.getRect().right;
                    targetBall = b;
                }
            }

            // Fallback: If no ball is coming towards AI (all moving left),
            // just track the ball closest to the center of the screen to stay ready.
            if (targetBall == null) {
                // Find closest ball generally
                float closestDist = Float.MAX_VALUE;
                for (Ball b : balls) {
                    float dist = Math.abs(paddle2.getRect().centerX() - b.getRect().centerX());
                    if (dist < closestDist) {
                        closestDist = dist;
                        targetBall = b;
                    }
                }
            }

            // Move the AI paddle towards the identified targetBall
            if (targetBall != null) {
                if (targetBall.getRect().centerY() > paddle2.getRect().centerY() + 10) {
                    paddle2.setMovementState(1); // Move Down
                } else if (targetBall.getRect().centerY() < paddle2.getRect().centerY() - 10) {
                    paddle2.setMovementState(-1); // Move Up
                } else {
                    paddle2.setMovementState(0); // Stop shaking if aligned
                }
            }
        }

        // --- UPDATE ALL BALLS ---
        for (int i = 0; i < balls.size(); i++) {
            Ball b = balls.get(i);
            b.update(fps);

            // 1. Screen Top/Bottom Collision
            if (b.getRect().top < 0 || b.getRect().bottom > screenY) {
                b.reverseYVelocity();
                if (b.getRect().top < 0) b.resetY(0);
                if (b.getRect().bottom > screenY) b.resetY(screenY - b.getHeight());
            }

            // 2. Player 1 Collision (Radial Bounce)
            if (RectF.intersects(paddle1.getRect(), b.getRect())) {
                b.reverseXVelocity();
                b.resetX(paddle1.getRect().right + 1);

                float relativeIntersectY = (paddle1.getRect().centerY() - b.getRect().centerY());
                float normalizedIntersectY = (relativeIntersectY / (paddle1.getHeight() / 2));
                float bounceAngle = normalizedIntersectY * Math.abs(b.getxVelocity());
                b.setyVelocity(-bounceAngle);

                b.increaseSpeed();
            }

            // 3. AI Collision (Radial Bounce)
            if (RectF.intersects(paddle2.getRect(), b.getRect())) {
                b.reverseXVelocity();
                b.resetX(paddle2.getRect().left - b.getWidth() - 1);

                float relativeIntersectY = (paddle2.getRect().centerY() - b.getRect().centerY());
                float normalizedIntersectY = (relativeIntersectY / (paddle2.getHeight() / 2));
                float bounceAngle = normalizedIntersectY * Math.abs(b.getxVelocity());
                b.setyVelocity(-bounceAngle);

                b.increaseSpeed();
            }

            // 4. SCORING & GAME MODES

            // --- Ball went past Player 1 (Left side) ---
            if (b.getRect().left < 0) {
                if (currentMode.equals("ENDLESS")) {
                    isGameOver = true; // One miss = Death
                } else {
                    player2Score++;
                    checkWinCondition(); // Check if game ended (Classic)
                    b.resetBall();      // Reset this specific ball
                    b.resetX(screenX / 2f);
                    b.resetY(screenY / 2f);
                }
            }

            // --- Ball went past AI (Right side) ---
            if (b.getRect().right > screenX) {
                player1Score++;
                checkWinCondition(); // Check if game ended (Classic)
                b.resetBall();
                b.resetX(screenX / 2f);
                b.resetY(screenY / 2f);
            }
        }
    }

    private void checkWinCondition() {
        // Rule: Classic = First to 5
        if (currentMode.equals("CLASSIC")) {
            if (player1Score >= 5 || player2Score >= 5) {
                isGameOver = true;
            }
        }

        // Rule: Double Ball = First to 10 (Since scoring is faster)
        else if (currentMode.equals("DOUBLE")) {
            if (player1Score >= 10 || player2Score >= 10) {
                isGameOver = true;
            }
        }

        // Note: "ENDLESS" mode is handled directly in the update() loop
        // because it ends immediately when a ball is missed.
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            if (isGameOver) {
                // Setup paint for centered text
                paint.setColor(Color.WHITE);
                paint.setTextAlign(Paint.Align.CENTER); // THIS IS THE KEY FIX

                // Draw "GAME OVER" in the middle
                paint.setTextSize(120);
                canvas.drawText("GAME OVER", screenX / 2f, screenY / 2f - 100, paint);

                // Draw final score
                paint.setTextSize(60);
                canvas.drawText("Final Score: " + player1Score + " - " + player2Score,
                        screenX / 2f, screenY / 2f, paint);

                // Draw Button 1: Restart
                paint.setColor(Color.GREEN);
                canvas.drawText("Tap Center to Restart", screenX / 2f, screenY / 2f + 150, paint);

                // Draw Button 2: Menu
                paint.setColor(Color.RED);
                canvas.drawText("Tap Bottom to Exit to Menu", screenX / 2f, screenY / 2f + 300, paint);

                // Reset alignment for other drawing code
                paint.setTextAlign(Paint.Align.LEFT);

            } else {
                // 1. Draw Dotted Line
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(5);
                for(int i = 0; i < screenY; i += 50) {
                    canvas.drawLine(screenX / 2f, i, screenX / 2f, i + 25, paint);
                }

                // 2. Draw Objects
                paddle1.draw(canvas);
                paddle2.draw(canvas);
                for (Ball b : balls) { b.draw(canvas); }

                // 3. Draw Scores
                paint.setTextAlign(Paint.Align.CENTER); // Center the scores too
                paint.setTextSize(100);
                canvas.drawText("" + player1Score, screenX / 4f, 150, paint);
                canvas.drawText("" + player2Score, (screenX / 4f) * 3, 150, paint);
                paint.setTextAlign(Paint.Align.LEFT); // Reset
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isGameOver && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            float touchY = motionEvent.getY();

            // If they tap the bottom area (Exit to Menu)
            if (touchY > (screenY * 0.75f)) {
                // This stops the game and closes the activity, going back to the menu
                playing = false;
                ((Activity) getContext()).finish();
            }
            // If they tap anywhere else (Restart)
            else {
                isGameOver = false;
                player1Score = 0;
                player2Score = 0;
                startNewGame();
            }
            return true;
        }

        // Normal Paddle Control
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
        try { gameThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}