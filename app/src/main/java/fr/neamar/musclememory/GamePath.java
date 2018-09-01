package fr.neamar.musclememory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Pair;

import java.util.ArrayList;

public class GamePath extends Path {
    public float progress = 0;

    private LevelView parent;
    private Paint linePaint;
    private Paint circlePaint;
    private Paint coveredCirclePaint;

    public PointF circlePosition;
    public int CIRCLE_RADIUS = 90;

    public boolean currentlyCovered = false;
    public boolean pathCompleted = false;

    private ValueAnimator progressAnimator;


    private ArrayList<Pair<Float, PointF>> progressPoints;

    GamePath(LevelView parent, ValueAnimator progressAnimator) {
        this.parent = parent;
        this.progressAnimator = progressAnimator;

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

        // Add listeners on the animation
        initializeAnimator();
    }

    private void initializeAnimator() {
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(progress == 1) {
                    pathCompleted = true;
                    parent.onPathCompleted();
                }
            }
        });
    }

    public void build() {
        // Approximate the path via line segments (we'll use them to move the circle along the path)
        float[] approximated = PathCompat.approximate(this, 0.5f);
        progressPoints = new ArrayList<>(approximated.length / 3);
        for (int i = 0; i < approximated.length; i += 3) {
            progressPoints.add(new Pair<>(approximated[i], new PointF(approximated[i + 1], approximated[i + 2])));
        }

        // Initialize path position
        circlePosition = progressPoints.get(0).second;
    }

    private PointF getPointOnPath(float progress) {
        // Find point before
        Pair<Float, PointF> pointBefore = null;
        Pair<Float, PointF> pointAfter = null;

        for (Pair<Float, PointF> pair : progressPoints) {
            if (pair.first > progress) {
                pointAfter = pair;
                break;
            }
            pointBefore = pair;
        }
        if (pointAfter == null) {
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
        pathCompleted = false;
        progressAnimator.start();
    }


    public void reset() {
        pathCompleted = false;
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
