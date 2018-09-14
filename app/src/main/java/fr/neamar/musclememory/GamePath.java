package fr.neamar.musclememory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Pair;

import java.util.ArrayList;

public class GamePath extends Path {
    private final int START_COLOR = Color.argb(235, 74, 138, 255);
    private final int END_COLOR = Color.argb(235, 30, 200, 30);
    private final int CIRCLE_ORIGINAL_COLOR = Color.argb(248, 255, 255, 255);

    public float progress = 0;

    private LevelView parent;
    private Paint linePaint;
    private Paint blurredLinePaint;

    private Paint fillCirclePaint;
    private Paint circlePaint;
    private Paint pulsatingCirclePaint;
    public PointF circlePosition;
    public int circleRadius;
    private ValueAnimator pulseAnimator;
    private ValueAnimator colorAnimator;


    public boolean currentlyCovered = false;
    public boolean pathCompleted = false;

    private ValueAnimator progressAnimator;


    private ArrayList<Pair<Float, PointF>> progressPoints;

    GamePath(LevelView parent, ValueAnimator progressAnimator) {
        this(parent, progressAnimator, 90);
    }

    GamePath(final LevelView parent, ValueAnimator progressAnimator, int circleRadius) {
        this.parent = parent;
        this.progressAnimator = progressAnimator;
        this.circleRadius = circleRadius;

        // Initialize Paints
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.argb(248, 255, 255, 255));
        linePaint.setDither(true);
        linePaint.setStrokeWidth(10f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        BlurMaskFilter blurMaskFilter = new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL);
        blurredLinePaint = new Paint();
        blurredLinePaint.set(linePaint);
        blurredLinePaint.setColor(START_COLOR);
        blurredLinePaint.setStrokeWidth(30f);
        blurredLinePaint.setMaskFilter(blurMaskFilter);

        circlePaint = new Paint();
        circlePaint.set(linePaint);
        circlePaint.setColor(CIRCLE_ORIGINAL_COLOR);

        fillCirclePaint = new Paint();
        fillCirclePaint.setColor(Color.BLACK);
        fillCirclePaint.setStyle(Paint.Style.FILL);

        pulsatingCirclePaint = new Paint();
        pulsatingCirclePaint.set(blurredLinePaint);

        pulseAnimator = ValueAnimator.ofFloat(10, 90);
        pulseAnimator.setDuration(3000);
        pulseAnimator.setRepeatMode(ValueAnimator.REVERSE);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pulsatingCirclePaint.setStrokeWidth((float) animation.getAnimatedValue());
                parent.invalidate();
            }
        });
        pulseAnimator.start();

        colorAnimator = ValueAnimator.ofArgb(START_COLOR, END_COLOR);
        colorAnimator.setDuration(progressAnimator.getDuration());
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                blurredLinePaint.setColor((int) animation.getAnimatedValue());
                circlePaint.setColor((int) animation.getAnimatedValue());
                parent.invalidate();
            }
        });

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
                if (progress == 1) {
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
        colorAnimator.start();
    }


    public void reset() {
        pathCompleted = false;
        progressAnimator.cancel();
        colorAnimator.cancel();
        progress = 0;
        blurredLinePaint.setColor(START_COLOR);
        circlePaint.setColor(CIRCLE_ORIGINAL_COLOR);
        pulseAnimator.cancel();
        pulseAnimator.start();
        parent.invalidate();
    }

    public void onDraw(Canvas canvas) {
        canvas.drawPath(this, linePaint);
        canvas.drawPath(this, blurredLinePaint);

        circlePosition = getPointOnPath(progress);

        if(!currentlyCovered) {
            canvas.drawCircle(circlePosition.x, circlePosition.y, circleRadius, pulsatingCirclePaint);
        }

        canvas.drawCircle(circlePosition.x, circlePosition.y, circleRadius, fillCirclePaint);
        canvas.drawCircle(circlePosition.x, circlePosition.y, circleRadius, circlePaint);
    }

    public long getDuration() {
        return progressAnimator.getDuration();
    }
}
