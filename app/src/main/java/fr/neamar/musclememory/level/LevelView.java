package fr.neamar.musclememory.level;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;

import java.util.ArrayList;

import fr.neamar.musclememory.LevelStore;

public class LevelView extends TouchEventView implements Invalidatable {
    public final static int WAITING_FOR_ALL_CIRCLES = 0;
    public final static int RUNNING = 2;
    public final static int WON = 3;
    public final static int LOST = 4;

    public int state = WAITING_FOR_ALL_CIRCLES;

    private OnLevelFinished onLevelFinished = null;

    private ArrayList<GamePath> paths = new ArrayList<>();

    public String title = "";
    private long startDate;

    private ValueAnimator antiCheatAnimator;

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentText = "Touch all circles to start.";

        // We're just looking for a ticker on the same frequency that we move our circle
        // otherwise, if you don't move your finger at all, the circles would keep moving
        // and we'd never notice that you're now outside
        antiCheatAnimator = ValueAnimator.ofFloat(0, 1);
        antiCheatAnimator.setDuration(1000);
        antiCheatAnimator.setRepeatCount(ValueAnimator.INFINITE);
        final int[] counter = {0};
        antiCheatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                counter[0] += 1;
                if(counter[0] % 10 == 0) {
                    ensureAllCirclesCovered();
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw all paths
        for (GamePath path : paths) {
            path.onDrawPath(canvas);
        }

        // And then all circles
        for (GamePath path : paths) {
            path.onDrawCircle(canvas);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        ensureAllCirclesCovered();

        return true;
    }

    private void ensureAllCirclesCovered() {
        // Do we have all circles covered?
        boolean allCirclesCovered = true;
        for (GamePath path : paths) {
            PointF circle = path.circlePosition;
            boolean covered = false;
            for (int i = 0; i < activePointers.size(); i++) {
                PointF pointer = activePointers.valueAt(i);
                double squaredDistance = Math.pow(circle.x - pointer.x, 2) + Math.pow(circle.y - pointer.y, 2);
                if (squaredDistance < path.circleRadius * path.circleRadius) {
                    covered = true;
                    path.setCurrentlyCovered(true);
                    break;
                }
            }

            if (!covered) {
                allCirclesCovered = false;
                path.setCurrentlyCovered(false);
            }
        }

        if (allCirclesCovered) {
            if (state == WAITING_FOR_ALL_CIRCLES) {
                start();
            }
        }

        if (!allCirclesCovered) {
            if (state == RUNNING) {
                reset();
            }
        }
    }

    public void start() {
        currentText = "Stay in the circle!";
        setState(RUNNING);
        startDate = System.currentTimeMillis();
        antiCheatAnimator.start();
    }


    public void reset() {
        currentText = "You've lost! Retry.";
        setState(LOST);
        setState(WAITING_FOR_ALL_CIRCLES);
        antiCheatAnimator.end();

        if (onLevelFinished != null) {
            onLevelFinished.levelFinished(false, System.currentTimeMillis() - startDate);
        }
    }

    public void onPathCompleted() {
        boolean allPathCompleted = true;
        for (GamePath path : paths) {
            if (!path.pathCompleted) {
                allPathCompleted = false;
                break;
            }
        }

        if (allPathCompleted) {
            currentText = "GG WP";
            setState(WON);
            if (onLevelFinished != null) {
                onLevelFinished.levelFinished(true, System.currentTimeMillis() - startDate);
            }
        }
    }

    public void setCurrentLevel(int level, int subLevel) {
        Pair<String, ArrayList<GamePath>> data = LevelStore.getPathsForLevel(this, getWidth(), getHeight(), level, subLevel);
        title = data.first;
        paths = data.second;
        setState(WAITING_FOR_ALL_CIRCLES);
        invalidate();
    }

    public void setOnLevelFinished(OnLevelFinished onLevelFinished) {
        this.onLevelFinished = onLevelFinished;
    }

    public void setState(int newState) {
        this.state = newState;
        for (GamePath path : paths) {
            path.onStateChange(newState);
        }
    }

    public int getPathsCount() {
        return paths.size();
    }

    public long getLevelDuration() {
        long duration = 0;
        for (GamePath path : paths) {
            duration = Math.max(path.getDuration(), duration);
        }

        return duration;
    }

    public float getProgress() {
        float progress = 1;
        for (GamePath path : paths) {
            progress = Math.min(path.progress, progress);
        }

        return progress;
    }

    public void onStop() {
        antiCheatAnimator.removeAllUpdateListeners();
        antiCheatAnimator.cancel();
        for (GamePath path : paths) {
            path.onStop();
        }

    }
}
