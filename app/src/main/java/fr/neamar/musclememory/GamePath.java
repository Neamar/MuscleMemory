package fr.neamar.musclememory;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

public class GamePath extends Path {
    private Paint mPaint;
    private PointF circlePosition = new PointF();
    private ArrayList<Pair<Float, PointF>> mPoints;
    private float progress = 0;

    GamePath(int width, int height) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        drawPath(width, height);

        float[] approximated = approximate(0.5f);

        mPoints = new ArrayList<>(approximated.length / 3);
        for(int i = 0; i < approximated.length; i += 3) {
            mPoints.add(new Pair<>(approximated[i], new PointF(approximated[i + 1], approximated[i + 2])));
            Log.e("WTF", "at " + approximated[i] + " is " + new PointF(approximated[i + 1], approximated[i + 2]));
        }
        circlePosition = mPoints.get(0).second;
    }

    private void drawPath(int width, int height) {
        moveTo(150, height / 2);
        lineTo(width - 150, height / 2);
    }

    private PointF getPointOnPath(float progress) {
        // Find point before
        Pair<Float, PointF> pointBefore = null;
        Pair<Float, PointF> pointAfter = null;

        for(Pair<Float, PointF> pair : mPoints) {
            if(pair.first > progress) {
                pointAfter = pair;
                break;
            }
            pointBefore = pair;
        }
        if(pointAfter == null) {
            pointAfter = mPoints.get(mPoints.size() - 1);
            pointBefore = mPoints.get(mPoints.size() - 2);
        }

        // Find the "intermediate" progress value (where are we between those two points, as a value between 0 and 1)
        float intermediateProgress = (progress - pointBefore.first) / (pointAfter.first - pointBefore.first);
        return new PointF(pointBefore.second.x + intermediateProgress * (pointAfter.second.x - pointBefore.second.x),
                pointBefore.second.y + intermediateProgress * (pointAfter.second.y - pointBefore.second.y));
    }


    public void onDraw(Canvas canvas) {
        canvas.drawPath(this, mPaint);

        progress = Math.min(1, progress + 0.01f);
        if(progress == 1) {
            progress = 0;
        }
        circlePosition = getPointOnPath(progress);
        canvas.drawCircle(circlePosition.x, circlePosition.y, 90, mPaint);
    }
}
