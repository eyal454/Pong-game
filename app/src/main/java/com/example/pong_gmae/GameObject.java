package com.example.pong_gmae;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class GameObject {

    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected RectF rect;
    protected Paint paint;


    public GameObject(float x, float y, float width, float height, int color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        paint = new Paint();
        paint.setColor(color);
        rect = new RectF(x, y, x + width, y + height);
    }


    public RectF getRect() {
        return rect;
    }

    public abstract void update(long fps);

    public abstract void draw(Canvas canvas);
}
