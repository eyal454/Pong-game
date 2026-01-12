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


    // volatile means this variable is accessed from different threads
    private volatile boolean playing;

    private Canvas canvas;
    private Paint paint;

    private int screenX;
    private int screenY;

    // game objects
    private Paddle paddle;
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
        // paddle is centered at the bottom 100 pixels wide
        paddle = new Paddle(screenX / 2f, screenY - 50, screenX / 5f, 20, Color.WHITE);

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
        // update entities
        paddle.update(fps);
        ball.update(fps);

        // collision

        //  ball hits the Paddle
        if (RectF.intersects(paddle.getRect(), ball.getRect())) {

            ball.reverseYVelocity();

            ball.resetY(paddle.getRect().top - ball.getHeight() - 2);

        }

        // ball hits bottom screen (game over)
        if (ball.getRect().bottom > screenY) {
            ball.reverseYVelocity();
            // add here minus 1 life when game over
            startNewGame();
        }

        // ball hits top screen
        if (ball.getRect().top < 0) {
            ball.reverseYVelocity();
            // Clear the wall to prevent sticking
            ball.resetY(1);
        }

        // 4. Ball hits Left Wall
        if (ball.getRect().left < 0) {
            ball.reverseXVelocity();
            ball.resetX(1);
        }

        // 5. Ball hits Right Wall
        if (ball.getRect().right > screenX) {
            ball.reverseXVelocity();
            ball.resetX(screenX - ball.getWidth() - 1);
        }
    }

    private void draw() {
        // Make sure the drawing surface is valid or the app will crash
        if (surfaceHolder.getSurface().isValid()) {

            // Lock the canvas to draw
            canvas = surfaceHolder.lockCanvas();

            // Draw Background Color (Black)
            canvas.drawColor(Color.BLACK); // Clears the screen

            // Draw the Game Objects
            // We pass the canvas to them so they can draw themselves
            paddle.draw(canvas);
            ball.draw(canvas);

            // Draw text (Score, FPS, etc)
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            canvas.drawText("FPS: " + fps, 20, 50, paint);

            // Unlock the canvas to show the image
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    // Handle User Input
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // Use bitmasking to handle multi-touch correctly
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player touched the screen
            case MotionEvent.ACTION_DOWN:

                // If touch is on the right half of the screen
                if (motionEvent.getX() > screenX / 2f) {
                    paddle.setMovementState(1); // Move Right
                }
                // If touch is on the left half
                else {
                    paddle.setMovementState(-1); // Move Left
                }
                break;

            // Player lifted finger
            case MotionEvent.ACTION_UP:
                paddle.setMovementState(0); // Stop Moving
                break;
        }
        return true;
    }

    // Method to pause the thread (called from MainActivity)
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to resume the thread (called from MainActivity)
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


}
