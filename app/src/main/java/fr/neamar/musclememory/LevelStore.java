package fr.neamar.musclememory;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

public class LevelStore {
    public static ArrayList<GamePath> getPathsForLevel(LevelView parent, int level) {

        int width = parent.getWidth();
        int height = parent.getHeight();

        ArrayList<GamePath> paths = new ArrayList<>();
        if (level == 0) {
            GamePath path = new GamePath(parent, initializeDefaultAnimator(3000));
            path.moveTo(150, height / 2);
            path.lineTo(width - 150, height / 2);

            paths.add(path);
        } else if (level == 1) {
            GamePath path = new GamePath(parent, initializeDefaultAnimator(3000));
            path.moveTo(150, height / 2);
            path.cubicTo(150, 0, width - 150, height, width - 150, height / 2);

            paths.add(path);
        } else if (level == 2) {
            GamePath path = new GamePath(parent, initializeDefaultAnimator(3000));
            path.moveTo(150, height / 4);
            path.lineTo(width - 150, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeDefaultAnimator(3000));
            secondPath.moveTo(150, 3 * height / 4);
            secondPath.lineTo(width - 150, 3 * height / 4);
            paths.add(secondPath);
        } else if (level == 3) {
            GamePath path = new GamePath(parent, initializeDefaultAnimator(5000), 150);
            path.moveTo(150, height / 4);
            path.lineTo(width - 150, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeDefaultAnimator(5000), 150);
            secondPath.moveTo(width - 150, 3 * height / 4);
            secondPath.lineTo(150, 3 * height / 4);
            paths.add(secondPath);
        } else if (level == 4) {
            GamePath path = new GamePath(parent, initializeDefaultAnimator(5500));
            path.moveTo(150, height / 4);
            path.lineTo(width - 150, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeDefaultAnimator(5500));
            secondPath.moveTo(width - 150, 3 * height / 4);
            secondPath.lineTo(150, 3 * height / 4);
            paths.add(secondPath);
        } else if (level == 5) {
            GamePath path = new GamePath(parent, initializeDefaultAnimator(5500));
            path.moveTo(150, height / 4);
            path.lineTo(width - 350, height / 4);
            path.cubicTo(width, height / 4, width, 3 * height / 4, width - 350, 3 * height / 4);
            path.lineTo(150, 3 * height / 4);


            paths.add(path);
        } else {
            throw new RuntimeException("Unknown level.");
        }

        for (GamePath path : paths) {
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
