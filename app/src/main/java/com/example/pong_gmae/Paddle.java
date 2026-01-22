package com.example.pong_gmae;

import android.graphics.Canvas;
import android.graphics.RectF;

public class Paddle extends GameObject {
    //(0 = not moving, 1 = down, -1 = up)
    private int movementState = 0;
    private float speed = 500;

    public Paddle(float x, float y, float width, float height, int color) {
        super(x, y, width, height, color);
    }

    @Override
    public void update(long fps) {
        if (movementState != 0) {
            // FIX: Change 'x' to 'y' to move Vertically
            y = y + (movementState * speed / fps);

            // FIX: Update top/bottom for vertical movement
            rect.top = y;
            rect.bottom = y + height;
        }
    }

    public void moveTo(float newY, float screenY) {
        // Center the paddle on the finger position
        y = newY - (height / 2);

        // Prevent paddle from going off the screen
        if (y < 0) y = 0;
        if (y + height > screenY) y = screenY - height;

        // Update the hit box
        rect.top = y;
        rect.bottom = y + height;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
    }

    public void setMovementState(int state) {
        this.movementState = state;
    }

    public void setSpeed(float newSpeed) {
        this.speed = newSpeed;
    }
}



