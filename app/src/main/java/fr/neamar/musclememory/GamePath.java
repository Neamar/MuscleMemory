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
    private Paint linePaint;
    private Paint circlePaint;

    private PointF circlePosition;
    private ArrayList<Pair<Float, PointF>> progressPoints;

    GamePath(int width, int height) {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(5);
        linePaint.setStyle(Paint.Style.STROKE);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        circlePaint.setColor(Color.GREEN);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        drawPath(width, height);

        float[] approximated = PathCompat.approximate(this, 0.5f);

        progressPoints = new ArrayList<>(approximated.length / 3);
        for(int i = 0; i < approximated.length; i += 3) {
            progressPoints.add(new Pair<>(approximated[i], new PointF(approximated[i + 1], approximated[i + 2])));
            Log.e("WTF", "at " + approximated[i] + " is " + new PointF(approximated[i + 1], approximated[i + 2]));
        }
        circlePosition = progressPoints.get(0).second;
    }

    private void drawPath(int width, int height) {
        moveTo(150, height / 2);
        cubicTo(150, 0, width - 150, height, width - 150, height / 2);
    }

    private PointF getPointOnPath(float progress) {
        // Find point before
        Pair<Float, PointF> pointBefore = null;
        Pair<Float, PointF> pointAfter = null;

        for(Pair<Float, PointF> pair : progressPoints) {
            if(pair.first > progress) {
                pointAfter = pair;
                break;
            }
            pointBefore = pair;
        }
        if(pointAfter == null) {
            pointAfter = progressPoints.get(progressPoints.size() - 1);
            pointBefore = progressPoints.get(progressPoints.size() - 2);
        }
        assert pointBefore != null;

        // Find the "intermediate" progress value (where are we between those two points, as a value between 0 and 1)
        float intermediateProgress = (progress - pointBefore.first) / (pointAfter.first - pointBefore.first);
        return new PointF(pointBefore.second.x + intermediateProgress * (pointAfter.second.x - pointBefore.second.x),
                pointBefore.second.y + intermediateProgress * (pointAfter.second.y - pointBefore.second.y));
    }


    public void onDraw(Canvas canvas, float progress) {
        canvas.drawPath(this, linePaint);

        circlePosition = getPointOnPath(progress);
        canvas.drawCircle(circlePosition.x, circlePosition.y, 90, circlePaint);
    }
}
