package fr.neamar.musclememory;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

public class LevelView extends TouchEventView {
    public float progress = 0;

    private ArrayList<GamePath> paths = new ArrayList<>();

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLevel();

        ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 1);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.setDuration(5000);
        progressAnimator.setRepeatCount(15);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();
    }

    private void initLevel() {
        post(new Runnable() {
            @Override
            public void run() {
                GamePath newPath = new GamePath(getWidth(), getHeight());
                paths.add(newPath);    }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw all paths
        for(GamePath path: paths) {
            path.onDraw(canvas, progress);
        }
    }
}
