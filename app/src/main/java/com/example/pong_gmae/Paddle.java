package com.example.pong_gmae;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;


public class Paddle extends GameObject{
    //(0 = not moving, 1 = right, -1 = left)
    private int movementState = 0;
    private float speed = 350;

    public Paddle(float x, float y, float width, float height, int color) {
        super(x, y, width, height, color);
    }

    @Override
    public void update(long fps) {
        if (movementState != 0) {
            float newX = x + (movementState * speed / fps);
            x = newX;

            rect.left = x;
            rect.right = x + width;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
    }

    public void setMovementState(int state) {
        this.movementState = state;
    }








}
