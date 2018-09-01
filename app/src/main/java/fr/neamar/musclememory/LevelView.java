package fr.neamar.musclememory;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

public class LevelView extends TouchEventView {
    private final int WAITING_FOR_ALL_CIRCLES = 0;
    private final int RUNNING = 2;

    private int state = WAITING_FOR_ALL_CIRCLES;

    private ArrayList<GamePath> paths = new ArrayList<>();

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLevel();
        currentText = "Touch all circles to start.";
    }

    private void initLevel() {
        post(new Runnable() {
            @Override
            public void run() {
                GamePath newPath = new GamePath(LevelView.this, getWidth(), getHeight());
                paths.add(newPath);
            }
        });

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
                Log.e("WTF","Starting");
                start();
            }
        }

        if(!allCirclesCovered) {
            if(state == RUNNING) {
                Log.e("WTF","Stopping");
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
}
