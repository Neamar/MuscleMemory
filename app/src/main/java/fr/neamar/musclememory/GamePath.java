package fr.neamar.musclememory;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.util.Pair;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

public class GamePath extends Path {
    private float progress = 0;

    private LevelView parent;
    private Paint linePaint;
    private Paint circlePaint;
    private Paint coveredCirclePaint;

    public PointF circlePosition;
    public int CIRCLE_RADIUS = 90;
    public boolean currentlyCovered = false;

    private ValueAnimator progressAnimator;


    private ArrayList<Pair<Float, PointF>> progressPoints;

    GamePath(LevelView parent, int width, int height) {
        this.parent = parent;

        // Initialize Paints
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(5);
        linePaint.setStyle(Paint.Style.STROKE);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(5);
        circlePaint.setStyle(Paint.Style.STROKE);

        coveredCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        coveredCirclePaint.setColor(Color.GREEN);
        coveredCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // Prepare the Path that will be drawn (lines, curves, ?)
        initializePath(width, height);
        initializeAnimator();

        // Approximate the path via line segments (we'll use them to move the circle along the path)
        float[] approximated = PathCompat.approximate(this, 0.5f);
        progressPoints = new ArrayList<>(approximated.length / 3);
        for(int i = 0; i < approximated.length; i += 3) {
            progressPoints.add(new Pair<>(approximated[i], new PointF(approximated[i + 1], approximated[i + 2])));
        }

        // Initialize path position
        circlePosition = progressPoints.get(0).second;
    }

    private void initializePath(int width, int height) {
        moveTo(150, height / 2);
        cubicTo(150, 0, width - 150, height, width - 150, height / 2);
    }

    private void initializeAnimator() {
        progressAnimator = ValueAnimator.ofFloat(0, 1);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.e("WTF", "RUNNING0");
                progress = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });
        progressAnimator.setDuration(5000);
        progressAnimator.setRepeatCount(15);
        progressAnimator.setInterpolator(new LinearInterpolator());

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

    public void start() {
        progressAnimator.start();
    }


    public void reset() {
        progressAnimator.cancel();
        progress = 0;
        parent.invalidate();
    }

    public void onDraw(Canvas canvas) {
        canvas.drawPath(this, linePaint);

        circlePosition = getPointOnPath(progress);
        canvas.drawCircle(circlePosition.x, circlePosition.y, CIRCLE_RADIUS, currentlyCovered ? coveredCirclePaint : circlePaint);
    }
}
