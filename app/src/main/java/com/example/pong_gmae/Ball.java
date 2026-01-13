package com.example.pong_gmae;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Ball extends GameObject{
    private float xVelocity;
    private float yVelocity;

    public Ball(float x, float y, float size, int color) {
        super(x, y, size, size, color);

        xVelocity = 200;
        yVelocity = -200;
    }

    @Override
    public void update(long fps) {
        // move the top left corner
        x = x + (xVelocity / fps);
        y = y + (yVelocity / fps);

        // Sync box
        rect.left = x;
        rect.top = y;
        rect.right = x + width;
        rect.bottom = y + height;

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(rect, paint);
    }

    //reverse direction when collision
    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = -xVelocity;
    }

    public void resetX(float newX) {
        this.x = newX;
        this.rect.left = x;
        this.rect.right = x + width;
    }

    public void resetY(float newY) {
        this.y = newY;
        this.rect.top = y;
        this.rect.bottom = y + height;
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void increaseSpeed() {
        // Increase speed by 10%
        xVelocity = xVelocity * 1.1f;
        yVelocity = yVelocity * 1.1f;
    }
}
