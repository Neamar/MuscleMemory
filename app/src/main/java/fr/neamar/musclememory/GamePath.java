package fr.neamar.musclememory;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class GamePath extends Path {
    private Paint mPaint;

    GamePath(int width, int height) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        moveTo(150, height / 2);
        lineTo(width - 150, height / 2);
    }

    public void onDraw(Canvas canvas) {
        canvas.drawPath(this, mPaint);
    }
}
