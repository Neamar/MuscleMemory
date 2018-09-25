package fr.neamar.musclememory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.Pair;

import java.util.ArrayList;

public class GamePath extends Path {
    private final static int START_COLOR = Color.argb(235, 74, 138, 255);
    private final static int END_COLOR = Color.argb(235, 30, 200, 30);
    private final static int LOST_COLOR = Color.argb(235, 200, 30, 30);
    public final static int CIRCLE_ORIGINAL_COLOR = Color.argb(248, 255, 255, 255);

    public float progress = 0;
    private ValueAnimator progressAnimator;

    private Invalidatable parent;
    private Paint linePaint;
    private Paint blurredLinePaint;

    private Paint fillCirclePaint;
    private Paint circlePaint;
    private Paint pulsatingCirclePaint;
    public PointF circlePosition = new PointF();
    public int circleRadius;
    private ValueAnimator pulseAnimator;
    private ValueAnimator dyingPulseAnimator;
    private ValueAnimator progressColorAnimator;
    private ValueAnimator lostColorAnimator;

    private PathMeasure pathMeasure;
    private float pathLength;
    private Path partialPath = new Path();
    private final float[] position = new float[2];

    private boolean currentlyCovered = false;
    public boolean pathCompleted = false;

    public float fakeProgress = 0;
    private ValueAnimator fakeProgressAnimator;
    public PointF fakeCirclePosition = new PointF();


    private ArrayList<Pair<Float, PointF>> progressPoints;

    GamePath(Invalidatable parent, ValueAnimator progressAnimator) {
        this(parent, progressAnimator, 90);
    }

    GamePath(final Invalidatable parent, ValueAnimator progressAnimator, int circleRadius) {
        this.parent = parent;
        this.progressAnimator = progressAnimator;
        this.circleRadius = circleRadius;

        // Initialize Paints
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(CIRCLE_ORIGINAL_COLOR);
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

        pulseAnimator = ValueAnimator.ofFloat(20, 90);
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

        progressColorAnimator = ValueAnimator.ofArgb(START_COLOR, END_COLOR);
        progressColorAnimator.setDuration(progressAnimator.getDuration());
        progressColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                blurredLinePaint.setColor((int) animation.getAnimatedValue());
                circlePaint.setColor((int) animation.getAnimatedValue());
                parent.invalidate();
            }
        });

        fakeProgressAnimator = progressAnimator.clone();
        fakeProgressAnimator.setRepeatCount(ValueAnimator.INFINITE);
        fakeProgressAnimator.start();

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
        fakeProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fakeProgress = (float) animation.getAnimatedValue();
                if (fakeProgress * pathLength > 6 * circleRadius) {
                    fakeProgressAnimator.start();
                }
                parent.invalidate();
            }
        });
    }

    public void build() {
        pathMeasure = new PathMeasure(this, false);
        pathLength = pathMeasure.getLength();

        // Initialize path position
        getPointOnPath(0, circlePosition);
    }

    public void onStateChange(int state) {
        if (state == LevelView.RUNNING) {
            pathCompleted = false;

            progressAnimator.start();
            progressColorAnimator.start();
            if (lostColorAnimator != null) {
                lostColorAnimator.end();
            }
            fakeProgressAnimator.end();
        }
        if (state == LevelView.LOST) {

            if (!currentlyCovered) {
                lostColorAnimator = ValueAnimator.ofArgb((int) progressColorAnimator.getAnimatedValue(), LOST_COLOR, START_COLOR);
            } else {
                lostColorAnimator = ValueAnimator.ofArgb((int) progressColorAnimator.getAnimatedValue(), START_COLOR);
            }
            lostColorAnimator.setDuration(500);
            lostColorAnimator.start();
            lostColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    blurredLinePaint.setColor((int) animation.getAnimatedValue());
                    parent.invalidate();
                }
            });

            pathCompleted = false;
            progressAnimator.cancel();
            progress = 0;
            progressColorAnimator.cancel();
            fakeProgressAnimator.start();

        }

        parent.invalidate();
    }

    public void setCurrentlyCovered(boolean covered) {
        if (covered == this.currentlyCovered) {
            return;
        }

        this.currentlyCovered = covered;
        if (covered) {
            circlePaint.setColor(START_COLOR);

            pulseAnimator.cancel();

            dyingPulseAnimator = ValueAnimator.ofFloat((float) pulseAnimator.getAnimatedValue(), 0);
            dyingPulseAnimator.setDuration(600);
            dyingPulseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    pulsatingCirclePaint.setStrokeWidth((float) animation.getAnimatedValue());
                    parent.invalidate();
                }
            });
            dyingPulseAnimator.start();
        } else {
            dyingPulseAnimator.cancel();
            pulseAnimator.start();
            circlePaint.setColor(CIRCLE_ORIGINAL_COLOR);
        }
    }

    private void getPointOnPath(float progress, PointF point) {
        pathMeasure.getPosTan(progress * pathLength, position, null);
        point.x = position[0];
        point.y = position[1];
    }

    public PointF getStartingPoint() {
        PointF p = new PointF();
        getPointOnPath(0, p);
        return p;
    }

    public void onDraw(Canvas canvas) {
        if (progress == 0 && fakeProgress * pathLength < 3 * circleRadius) {
            getPointOnPath(fakeProgress, fakeCirclePosition);
            float fakeProgressRadius = fakeProgress * pathLength <= 1.5 * circleRadius ? 20 : 40 - 40 * (pathLength * fakeProgress)/(circleRadius * 3);
            canvas.drawCircle(fakeCirclePosition.x, fakeCirclePosition.y, fakeProgressRadius, linePaint);
            canvas.drawCircle(fakeCirclePosition.x, fakeCirclePosition.y, fakeProgressRadius, blurredLinePaint);
        }

        partialPath.reset();
        pathMeasure.getSegment(progress * pathLength, pathLength, partialPath, true);
        partialPath.rLineTo(0.0f, 0.0f); // workaround to display on hardware accelerated canvas as described in docs
        canvas.drawPath(this, linePaint);
        canvas.drawPath(partialPath, blurredLinePaint);

        getPointOnPath(progress, circlePosition);

        canvas.drawCircle(circlePosition.x, circlePosition.y, circleRadius, pulsatingCirclePaint);
        canvas.drawCircle(circlePosition.x, circlePosition.y, circleRadius, fillCirclePaint);
        canvas.drawCircle(circlePosition.x, circlePosition.y, circleRadius, circlePaint);
    }

    public long getDuration() {
        return progressAnimator.getDuration();
    }
}
