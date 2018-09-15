package fr.neamar.musclememory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;

import java.util.ArrayList;

public class LevelView extends TouchEventView {
    public final static int WAITING_FOR_ALL_CIRCLES = 0;
    public final static int RUNNING = 2;
    public final static int WON = 3;

    public int state = WAITING_FOR_ALL_CIRCLES;

    private OnLevelFinished onLevelFinished = null;

    private ArrayList<GamePath> paths = new ArrayList<>();

    public String title = "";
    private long startDate;

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentText = "Touch all circles to start.";
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw all paths
        for (GamePath path : paths) {
            path.onDraw(canvas);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        // Do we have all circles covered?
        boolean allCirclesCovered = true;
        for(GamePath path: paths) {
            PointF circle = path.circlePosition;
            boolean covered = false;
            for(int i = 0; i < activePointers.size(); i++) {
                PointF pointer = activePointers.valueAt(i);
                double squaredDistance = Math.pow(circle.x - pointer.x, 2) + Math.pow(circle.y - pointer.y, 2);
                if(squaredDistance < path.circleRadius * path.circleRadius) {
                    covered = true;
                    path.currentlyCovered = true;
                    break;
                }
            }

            if(!covered) {
                allCirclesCovered = false;
                path.currentlyCovered = false;
            }
        }

        if(allCirclesCovered) {
            if(state == WAITING_FOR_ALL_CIRCLES) {
                start();
            }
        }

        if(!allCirclesCovered) {
            if(state == RUNNING) {
                reset();
            }
        }

        return true;
    }

    public void start() {
        currentText = "Stay in the circle!";
        setState(RUNNING);
        for (GamePath path : paths) {
            path.start();
        }
        startDate = System.currentTimeMillis();
    }


    public void reset() {
        currentText = "You've lost! Retry.";
        setState(WAITING_FOR_ALL_CIRCLES);

        if(onLevelFinished != null) {
            onLevelFinished.levelFinished(false, System.currentTimeMillis() - startDate);
        }

        for (GamePath path : paths) {
            path.reset();
        }
    }

    public void onPathCompleted() {
        boolean allPathCompleted = true;
        for (GamePath path : paths) {
            if(!path.pathCompleted) {
                allPathCompleted = false;
                break;
            }
        }

        if(allPathCompleted) {
            currentText = "GG WP";
            setState(WON);
            if(onLevelFinished != null) {
                onLevelFinished.levelFinished(true, System.currentTimeMillis() - startDate);
            }
        }
    }

    public void setCurrentLevel(int i) {
        Pair<String, ArrayList<GamePath>> data = LevelStore.getPathsForLevel(this, i);
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
        for(GamePath path:paths) {
            duration = Math.max(path.getDuration(), duration);
        }

        return duration;
    }

    public float getProgress() {
        float progress = 1;
        for(GamePath path:paths) {
            progress = Math.min(path.progress, progress);
        }

        return progress;
    }
}
