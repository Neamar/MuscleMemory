package fr.neamar.musclememory;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

public class LevelStore {
    public static ArrayList<GamePath> getPathsForLevel(LevelView parent, int level) {

        int width = parent.getWidth();
        int height = parent.getHeight();

        ArrayList<GamePath> paths = new ArrayList<>();
        switch (level) {
            case 0:
                GamePath path = new GamePath(parent, initializeDefaultAnimator(3000));
                path.moveTo(150, height / 2);
                path.cubicTo(150, 0, width - 150, height, width - 150, height / 2);

                paths.add(path);
            default:
                break;
        }

        for(GamePath path:paths) {
            path.build();
        }

        return paths;
    }

    private static ValueAnimator initializeDefaultAnimator() {
        return initializeDefaultAnimator(5000);
    }

    private static ValueAnimator initializeDefaultAnimator(int duration) {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 1);

        progressAnimator.setDuration(duration);
        progressAnimator.setInterpolator(new LinearInterpolator());

        return progressAnimator;
    }
}
