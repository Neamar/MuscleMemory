package fr.neamar.musclememory;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;

public class LevelView extends TouchEventView {
    private ArrayList<GamePath> mPaths = new ArrayList<>();

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLevel();
    }

    private void initLevel() {
        post(new Runnable() {
            @Override
            public void run() {
                GamePath newPath = new GamePath(getWidth(), getHeight());
                mPaths.add(newPath);    }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw all paths
        for(GamePath path: mPaths) {
            path.onDraw(canvas);
        }
    }
}
