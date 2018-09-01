package fr.neamar.musclememory;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

public class LevelView extends TouchEventView {
    private final int WAITING_FOR_ALL_CIRCLES = 0;
    private final int RUNNING = 2;
    private final int WON = 3;

    private int state = WAITING_FOR_ALL_CIRCLES;

    private ArrayList<GamePath> paths = new ArrayList<>();

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
                if(squaredDistance < path.CIRCLE_RADIUS * path.CIRCLE_RADIUS) {
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
        state = RUNNING;
        for (GamePath path : paths) {
            path.start();
        }
    }


    public void reset() {
        currentText = "You've lost! Retry.";
        state = WAITING_FOR_ALL_CIRCLES;
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
            state = WON;
        }
    }

    public void setCurrentLevel(int i) {
        paths = LevelStore.getPathsForLevel(this, i);
        invalidate();
    }
}
