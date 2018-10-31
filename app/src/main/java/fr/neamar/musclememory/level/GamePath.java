package fr.neamar.musclememory.level;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.HashSet;
import java.util.Iterator;

public class GamePath extends Path {
    private final static int START_COLOR = Color.parseColor("#7E57C2");
    private final static int END_COLOR = Color.parseColor("#311B92");
    private final static int LOST_COLOR = Color.parseColor("#C62828");
    public final static int UNSELECTED_CIRCLE_COLOR = Color.parseColor("#616161");

    private final static int TRACER_DURATION = 1000;
    private final static int TRACER_COOLDOWN = 500;

    float progress = 0;
    private ValueAnimator progressAnimator;

    private Invalidatable parent;
    private Paint linePaint;

    private Paint fillCirclePaint;
    private Paint circlePaint;
    PointF circlePosition = new PointF();
    private int baseCircleRadius;
    public float circleRadius;
    private ValueAnimator pulseAnimator;
    private ValueAnimator dyingPulseAnimator;
    private ValueAnimator progressColorAnimator;
    private ValueAnimator lostColorAnimator;

    private PathMeasure pathMeasure;
    private float pathLength;
    private Path partialPath = new Path();
    private final float[] position = new float[2];
    private final float[] tangent = new float[2];

    private boolean currentlyCovered = false;
    boolean pathCompleted = false;

    // Small circle to tell you how fast the level will be
    private float tracerProgress = 0;
    private ValueAnimator tracerProgressAnimator;
    private PointF tracerCirclePosition = new PointF();
    private float tracerCurrentPlayTime;
    private float tracerMaxProgress;

    private HashSet<Particle> particles = new HashSet<>(40);

    private float maxProgress = 0f;
    private PointF maxProgressCirclePosition = new PointF();

    public GamePath(Invalidatable parent, ValueAnimator progressAnimator) {
        this(parent, progressAnimator, 90);
    }

    public GamePath(final Invalidatable parent, ValueAnimator progressAnimator, int circleRadius) {
        this.parent = parent;
        this.progressAnimator = progressAnimator;
        this.circleRadius = circleRadius;
        this.baseCircleRadius = circleRadius;

        initializePaints();
        initializeAnimators();
    }

    private void initializePaints() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(START_COLOR);
        linePaint.setDither(true);
        linePaint.setStrokeWidth(10f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        circlePaint = new Paint();
        circlePaint.set(linePaint);
        circlePaint.setColor(UNSELECTED_CIRCLE_COLOR);

        fillCirclePaint = new Paint();
        fillCirclePaint.setColor(Color.WHITE);
        fillCirclePaint.setStyle(Paint.Style.FILL);
    }

    private void initializeAnimators() {
        // Tie progress on the animator with progress on the path
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                if(pathMeasure != null) {
                    pathMeasure.getPosTan(progress * pathLength, position, tangent);
                    float maxProgress = progress + 1000f / animation.getDuration();

                    particles.add(new Particle(position[0], position[1], tangent[0], tangent[1], progress, maxProgress));
                }
                parent.invalidate();
            }
        });
        // Ensure we get notified when the path is completed
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (progress == 1) {
                    pathCompleted = true;
                    parent.onPathCompleted();
                }
            }
        });

        // Pulse the circle radius until level is started
        pulseAnimator = ValueAnimator.ofFloat(0, 30, 0);
        pulseAnimator.setDuration(500);
        pulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                GamePath.this.circleRadius = baseCircleRadius + (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });
        pulseAnimator.start();

        // Change color of the path with progress,
        // not started by default
        progressColorAnimator = ValueAnimator.ofArgb(START_COLOR, END_COLOR);
        progressColorAnimator.setDuration(progressAnimator.getDuration());
        progressColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                linePaint.setColor((int) animation.getAnimatedValue());
                circlePaint.setColor((int) animation.getAnimatedValue());
                parent.invalidate();
            }
        });

        // Display the tracer
        // started by default
        tracerProgressAnimator = progressAnimator.clone();
        tracerProgressAnimator.setRepeatCount(ValueAnimator.INFINITE);
        tracerProgressAnimator.start();
        tracerProgressAnimator.removeAllUpdateListeners();
        tracerMaxProgress = (float) TRACER_DURATION / progressAnimator.getDuration();
        tracerProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tracerProgress = (float) animation.getAnimatedValue();
                tracerCurrentPlayTime = tracerProgressAnimator.getCurrentPlayTime();
                // Restart animation after a certain delay
                if (tracerCurrentPlayTime > TRACER_DURATION + TRACER_COOLDOWN) {
                    tracerProgressAnimator.start();
                    if(!currentlyCovered) {
                        pulseAnimator.start();
                    }
                } else if (tracerCurrentPlayTime <= TRACER_DURATION) {
                    // Draw the tracer
                    parent.invalidate();
                }
            }
        });
    }

    // Called once all draw operations have been added to this item
    // Used to initialise all measurements
    public void build() {
        pathMeasure = new PathMeasure(this, false);
        pathLength = pathMeasure.getLength();

        // Initialize path position to display initial circle in the right position
        getPointOnPath(0, circlePosition);
    }

    void onStateChange(int state) {
        if (state == LevelView.RUNNING) {
            pathCompleted = false;

            progressAnimator.start();
            progressColorAnimator.start();
            if (lostColorAnimator != null) {
                lostColorAnimator.end();
            }
            tracerProgressAnimator.end();
            // .end() finishes the animation, but we want to make sure
            // we don't draw the tracer at the end before progressAnimator starts
            tracerCurrentPlayTime = TRACER_DURATION * 2;
        }
        if (state == LevelView.LOST) {
            if (lostColorAnimator != null) {
                lostColorAnimator.removeAllUpdateListeners();
                lostColorAnimator.cancel();
            }
            // Reinitialize path color, starting at current color,
            // with an intermediate "fail" color if this path is not covered
            // (meaning player just lost because of this path)
            // and finishing at the expected start color
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
                    linePaint.setColor((int) animation.getAnimatedValue());
                    parent.invalidate();
                }
            });

            // Update best position on screen
            if(progress > maxProgress) {
                maxProgress = progress;
                getPointOnPath(maxProgress, maxProgressCirclePosition);
            }

            pathCompleted = false;
            progressAnimator.cancel();
            progress = 0;
            progressColorAnimator.cancel();
            tracerProgressAnimator.start();
            particles.clear();
        }

        parent.invalidate();
    }

    void setCurrentlyCovered(boolean covered) {
        if (covered == this.currentlyCovered) {
            return;
        }

        this.currentlyCovered = covered;
        if (covered) {
            // We just touched the circle!
            // Decrease pulse radius until we're back to the expected value
            circlePaint.setColor(START_COLOR);

            pulseAnimator.cancel();

            dyingPulseAnimator = ValueAnimator.ofFloat((float) pulseAnimator.getAnimatedValue(), 0);
            dyingPulseAnimator.setDuration(600);
            dyingPulseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    circleRadius = baseCircleRadius + (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });
            dyingPulseAnimator.start();
        } else {
            // It's lost :( restart pulse and reset circle color
            dyingPulseAnimator.cancel();
            pulseAnimator.start();
            circlePaint.setColor(UNSELECTED_CIRCLE_COLOR);
        }
    }

    /**
     * Write position at progress into point
     *
     * @param progress required progress, between 0 and 1
     * @param point    a point to write coordinates into
     */
    private void getPointOnPath(float progress, PointF point) {
        pathMeasure.getPosTan(progress * pathLength, position, null);
        point.x = position[0];
        point.y = position[1];
    }

    /**
     * Where should the initial circle be?
     *
     * @return initial position at progress=0
     */
    public PointF getStartingPoint() {
        PointF p = new PointF();
        getPointOnPath(0, p);
        return p;
    }

    /**
     * Draw the current path into canvas
     *
     * @param canvas canvas to draw into
     */
    void onDrawPath(Canvas canvas) {
        if(progress > 0) {
            // Build a partial path containing the remaining path
            // and draw it on screen
            partialPath.reset();
            pathMeasure.getSegment(progress * pathLength, pathLength, partialPath, true);
            partialPath.rLineTo(0.0f, 0.0f); // workaround to display on hardware accelerated canvas as described in docs
            canvas.drawPath(partialPath, linePaint);
        }
        else {
            canvas.drawPath(this, linePaint);
        }

        // If unstarted, also display tracer
        if (progress == 0 && tracerCurrentPlayTime < TRACER_DURATION) {
            getPointOnPath(tracerProgress, tracerCirclePosition);
            float fakeProgressRadius = tracerCurrentPlayTime <= TRACER_DURATION / 2 ? 20 : 40 * (TRACER_DURATION - tracerCurrentPlayTime) / TRACER_DURATION;
            canvas.drawCircle(tracerCirclePosition.x, tracerCirclePosition.y, fakeProgressRadius, linePaint);
            canvas.drawCircle(tracerCirclePosition.x, tracerCirclePosition.y, fakeProgressRadius, fillCirclePaint);
        }

        for (Iterator<Particle> iterator = particles.iterator(); iterator.hasNext();) {
            Particle p = iterator.next();
            boolean state = p.onDraw(canvas, linePaint, progress);
            if(state == Particle.SHOULD_DISAPPEAR) {
                iterator.remove();
            }
        }

        if(maxProgress > tracerMaxProgress && progress < maxProgress) {
            // Mark the farthest position reached
            canvas.drawCircle(maxProgressCirclePosition.x, maxProgressCirclePosition.y, 5, linePaint);
            canvas.drawCircle(maxProgressCirclePosition.x, maxProgressCirclePosition.y, 5, fillCirclePaint);
        }
    }

    void onDrawCircle(Canvas canvas) {
        getPointOnPath(progress, circlePosition);

        canvas.drawCircle(circlePosition.x, circlePosition.y, circleRadius, fillCirclePaint);
        canvas.drawCircle(circlePosition.x, circlePosition.y, circleRadius, circlePaint);
    }

    long getDuration() {
        return progressAnimator.getDuration();
    }

    void onDestroy() {
        // Clean up animations to make sure future levels can have all CPU available
        progressAnimator.removeAllUpdateListeners();
        progressAnimator.cancel();
        pulseAnimator.removeAllUpdateListeners();
        pulseAnimator.cancel();
        progressColorAnimator.removeAllUpdateListeners();
        progressColorAnimator.cancel();
        tracerProgressAnimator.removeAllUpdateListeners();
        tracerProgressAnimator.cancel();
        if (dyingPulseAnimator != null) {
            dyingPulseAnimator.removeAllUpdateListeners();
            dyingPulseAnimator.cancel();
        }
        if (lostColorAnimator != null) {
            lostColorAnimator.removeAllUpdateListeners();
            lostColorAnimator.cancel();
        }
        particles.clear();
    }
}
