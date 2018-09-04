package fr.neamar.musclememory;

import android.animation.ValueAnimator;
import android.util.Pair;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

public class LevelStore {
    public static Pair<String, ArrayList<GamePath>> getPathsForLevel(LevelView parent, int level) {

        int width = parent.getWidth();
        int height = parent.getHeight();
        String title = "";

        ArrayList<GamePath> paths = new ArrayList<>();
        if (level == 0) {
            title = "intro_1_line";
            GamePath path = new GamePath(parent, initializeDefaultAnimator(3000));
            path.moveTo(150, height / 2);
            path.lineTo(width - 150, height / 2);

            paths.add(path);
        } else if (level == 1) {
            title = "intro_1_curve";
            GamePath path = new GamePath(parent, initializeDefaultAnimator(3000));
            path.moveTo(150, height / 2);
            path.cubicTo(150, 0, width - 150, height, width - 150, height / 2);

            paths.add(path);
        } else if (level == 2) {
            title = "intro_2_lines";
            GamePath path = new GamePath(parent, initializeDefaultAnimator(3000));
            path.moveTo(150, height / 4);
            path.lineTo(width - 150, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeDefaultAnimator(3000));
            secondPath.moveTo(150, 3 * height / 4);
            secondPath.lineTo(width - 150, 3 * height / 4);
            paths.add(secondPath);
        } else if (level == 3) {
            title = "intro_2_lines_reverse_big";
            GamePath path = new GamePath(parent, initializeDefaultAnimator(5000), 150);
            path.moveTo(150, height / 4);
            path.lineTo(width - 150, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeDefaultAnimator(5000), 150);
            secondPath.moveTo(width - 150, 3 * height / 4);
            secondPath.lineTo(150, 3 * height / 4);
            paths.add(secondPath);
        } else if (level == 4) {
            title = "intro_2_lines_reverse_small";
            GamePath path = new GamePath(parent, initializeDefaultAnimator(5500));
            path.moveTo(150, height / 4);
            path.lineTo(width - 150, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeDefaultAnimator(5500));
            secondPath.moveTo(width - 150, 3 * height / 4);
            secondPath.lineTo(150, 3 * height / 4);
            paths.add(secondPath);
        } else if (level == 5) {
            title = "u_curve";
            GamePath path = new GamePath(parent, initializeDefaultAnimator(5000));
            path.moveTo(150, height / 4);
            path.lineTo(width - 350, height / 4);
            path.cubicTo(width, height / 4, width, 3 * height / 4, width - 350, 3 * height / 4);
            path.lineTo(150, 3 * height / 4);
            paths.add(path);
        } else if (level == 6) {
            title = "2_half_rectangles";
            GamePath path = new GamePath(parent, initializeDefaultAnimator(6000), 120);
            path.moveTo(350, height / 4);
            path.lineTo(width - 150, height / 4);
            path.lineTo(width - 150, 3 * height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeDefaultAnimator(6000), 120);
            secondPath.moveTo(width - 350, 3 * height / 4);
            secondPath.lineTo(150, 3 * height / 4);
            secondPath.lineTo(150, height / 4);
            paths.add(secondPath);
        } else if (level == 7) {
            title = "a_circle";
            GamePath path = new GamePath(parent, initializeDefaultAnimator(6000));
            path.moveTo(width / 2, 70);
            path.cubicTo(width, 70, width, height - 70, width / 2, height - 70);
            path.cubicTo(0, height - 70, 0, 70, width / 2, 70);
            paths.add(path);
        } else if (level == 8) {
            title = "2_half_rectangles_and_a_point";
            GamePath path = new GamePath(parent, initializeDefaultAnimator(6000), 120);
            path.moveTo(350, height / 4);
            path.lineTo(width - 150, height / 4);
            path.lineTo(width - 150, 3 * height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeDefaultAnimator(6000), 120);
            secondPath.moveTo(width - 350, 3 * height / 4);
            secondPath.lineTo(150, 3 * height / 4);
            secondPath.lineTo(150, height / 4);
            paths.add(secondPath);

            GamePath thirdPath = new GamePath(parent, initializeDefaultAnimator(4500), 120);
            thirdPath.moveTo(width / 2 - 1, height / 2);
            thirdPath.lineTo(width / 2 + 1, height / 2);
            paths.add(thirdPath);
        } else if (level == 9) {
            title = "3_lines_1_reversed";
            GamePath path = new GamePath(parent, initializeDefaultAnimator(4500), 120);
            path.moveTo(150, height / 4);
            path.lineTo(width - 250, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeDefaultAnimator(4500), 120);
            secondPath.moveTo(150, 3 * height / 4);
            secondPath.lineTo(width - 250, 3 * height / 4);
            paths.add(secondPath);

            GamePath thirdPath = new GamePath(parent, initializeDefaultAnimator(4500), 120);
            thirdPath.moveTo(width - 150, height / 2);
            thirdPath.lineTo(250, height / 2);
            paths.add(thirdPath);
        } else {
            throw new RuntimeException("Unknown level.");
        }

        for (GamePath path : paths) {
            path.build();
        }

        return new Pair<>(title, paths);
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
