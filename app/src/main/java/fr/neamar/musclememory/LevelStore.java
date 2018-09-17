package fr.neamar.musclememory;

import android.animation.ValueAnimator;
import android.util.Pair;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

public class LevelStore {
    public static Pair<String, ArrayList<GamePath>> getPathsForLevel(Invalidatable parent, int width, int height, int level, int subLevel) {
        String title;
        int cX = width / 2;
        int cY = height / 2;

        ArrayList<GamePath> paths = new ArrayList<>();
        if (level == 0 && subLevel == 0) {
            title = "intro_1_line";
            GamePath path = new GamePath(parent, initializeAnimator(3000));
            path.moveTo(150, cY);
            path.lineTo(width - 150, cY);

            paths.add(path);
        } else if (level == 0 && subLevel == 1) {
            title = "intro_1_curve";
            GamePath path = new GamePath(parent, initializeAnimator(3000));
            path.moveTo(150, cY);
            path.cubicTo(150, 0, width - 150, height, width - 150, cY);

            paths.add(path);
        } else if (level == 1 && subLevel == 0) {
            title = "intro_2_lines";
            GamePath path = new GamePath(parent, initializeAnimator(3000), 120);
            path.moveTo(150, height / 4);
            path.lineTo(width - 150, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeAnimator(3000), 120);
            secondPath.moveTo(150, 3 * height / 4);
            secondPath.lineTo(width - 150, 3 * height / 4);
            paths.add(secondPath);
        } else if (level == 1 && subLevel == 1) {
            title = "intro_2_lines";
            GamePath path = new GamePath(parent, initializeAnimator(3000));
            path.moveTo(150, height / 4);
            path.lineTo(width - 150, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeAnimator(3000));
            secondPath.moveTo(150, 3 * height / 4);
            secondPath.lineTo(width - 150, 3 * height / 4);
            paths.add(secondPath);
        } else if (level == 2 && subLevel == 0) {
            title = "intro_2_lines_reverse_big";
            GamePath path = new GamePath(parent, initializeAnimator(5000), 150);
            path.moveTo(150, height / 4);
            path.lineTo(width - 150, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeAnimator(5000), 150);
            secondPath.moveTo(width - 150, 3 * height / 4);
            secondPath.lineTo(150, 3 * height / 4);
            paths.add(secondPath);
        } else if (level == 2 && subLevel == 1) {
            title = "intro_2_lines_reverse_small";
            GamePath path = new GamePath(parent, initializeAnimator(5500));
            path.moveTo(150, height / 4);
            path.lineTo(width - 150, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeAnimator(5500));
            secondPath.moveTo(width - 150, 3 * height / 4);
            secondPath.lineTo(150, 3 * height / 4);
            paths.add(secondPath);
        } else if (level == 3 && subLevel == 0) {
            title = "u_curve";
            GamePath path = new GamePath(parent, initializeAnimator(5000));
            path.moveTo(150, height / 4);
            path.lineTo(width - 350, height / 4);
            path.cubicTo(width, height / 4, width, 3 * height / 4, width - 350, 3 * height / 4);
            path.lineTo(150, 3 * height / 4);
            paths.add(path);
        } else if (level == 3 && subLevel == 1) {
            title = "2_half_rectangles";
            GamePath path = new GamePath(parent, initializeAnimator(6000), 120);
            path.moveTo(350, height / 4);
            path.lineTo(width - 150, height / 4);
            path.lineTo(width - 150, 3 * height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeAnimator(6000), 120);
            secondPath.moveTo(width - 350, 3 * height / 4);
            secondPath.lineTo(150, 3 * height / 4);
            secondPath.lineTo(150, height / 4);
            paths.add(secondPath);
        } else if (level == 4 && subLevel == 0) {
            title = "a_circle";
            GamePath path = new GamePath(parent, initializeAnimator(6000));
            path.moveTo(cX, 70);
            path.cubicTo(width, 70, width, height - 70, cX, height - 70);
            path.cubicTo(0, height - 70, 0, 70, cX, 70);
            paths.add(path);
        } else if (level == 4 && subLevel == 1) {
            title = "a_circle_fast";
            GamePath path = new GamePath(parent, initializeAnimator(3000));
            path.moveTo(cX, 70);
            path.cubicTo(width, 70, width, height - 70, cX, height - 70);
            path.cubicTo(0, height - 70, 0, 70, cX, 70);
            paths.add(path);
            paths.add(path);
        } else if (level == 5 && subLevel == 0) {
            title = "butterfly";
            GamePath path = new GamePath(parent, initializeAnimator(6000));
            path.moveTo(cX, cY);
            path.cubicTo(-100, cY, cX, -100, cX, cY);
            path.cubicTo(cX, height + 100, width + 100, cY, cX, cY);
            path.cubicTo(-100, cY, cX, height + 100, cX, cY);
            path.cubicTo(cX, -100, width + 100, cY, cX, cY);
            paths.add(path);
        } else if (level == 5 && subLevel == 1) {
            title = "butterfly";
            GamePath path = new GamePath(parent, initializeAnimator(6000));
            path.moveTo(cX, cY);
            path.cubicTo(-100, cY, cX, -100, cX, cY);
            path.cubicTo(cX, height + 100, width + 100, cY, cX, cY);
            paths.add(path);
        } else if (level == 6 && subLevel == 0) {
            title = "2_circles";
            GamePath path = new GamePath(parent, initializeAnimator(6000), 100);
            path.moveTo(width / 4, 100);
            path.cubicTo(cX, 100, cX, height - 100, width / 4, height - 100);
            path.cubicTo(0, height - 100, 0, 100, width / 4, 100);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeAnimator(6000), 100);
            secondPath.moveTo(3 * width / 4, 100);
            secondPath.cubicTo(width, 100, width, height - 100, 3 * width / 4, height - 100);
            secondPath.cubicTo(cX, height - 100, cX, 100, 3 * width / 4, 100);
            paths.add(secondPath);
        } else if (level == 6 && subLevel == 1) {
            title = "2_circles_reversed";
            GamePath path = new GamePath(parent, initializeAnimator(6000), 100);
            path.moveTo(width / 4, 100);
            path.cubicTo(cX, 100, cX, height - 100, width / 4, height - 100);
            path.cubicTo(0, height - 100, 0, 100, width / 4, 100);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeAnimator(6000), 100);
            secondPath.moveTo(3 * width / 4, 100);
            secondPath.cubicTo(cX, 100, cX, height - 100, 3 * width / 4, height - 100);
            secondPath.cubicTo(width, height - 100, width, 100, 3 * width / 4, 100);
            paths.add(secondPath);
        } else if (level == 7 && subLevel == 0) {
            title = "solid_spiral";
            GamePath path = new GamePath(parent, initializeAnimator(6000), 90);

            int p = 90;
            int D = Math.min(cX, cY);
            path.moveTo(cX, height - p); // 1
            path.lineTo(cX - D, D); // 2
            path.lineTo(cX, p); // 3
            path.lineTo(cX + D, D); // 4
            path.lineTo(cX + p, height - 2 * p); // 5
            path.lineTo(cX - D + 2 * p, D); // 6
            path.lineTo(cX, 3 * p); // 7
            path.lineTo(cX + D - 2 * p, D); // 8
            path.lineTo(cX + p, height - 4 * p); // 9
            path.lineTo(cX - D + 4 * p, D); // 10

            paths.add(path);
        } else if (level == 7 && subLevel == 1) {
            title = "intro_1_curve";
            GamePath path = new GamePath(parent, initializeAnimator(3000));
            path.moveTo(150, cY);
            path.cubicTo(150, 0, width - 150, height, width - 150, cY);

            paths.add(path);
        } else if (level == 8 && subLevel == 0) {
            title = "2_half_rectangles_and_a_point";
            GamePath path = new GamePath(parent, initializeAnimator(6000), 120);
            path.moveTo(350, 120);
            path.lineTo(width - 150, 120);
            path.lineTo(width - 150, cY);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeAnimator(6000), 120);
            secondPath.moveTo(width - 350, height - 120);
            secondPath.lineTo(150, height - 120);
            secondPath.lineTo(150, cY);
            paths.add(secondPath);

            GamePath thirdPath = new GamePath(parent, initializeAnimator(4500), 120);
            thirdPath.moveTo(cX - 1, cY);
            thirdPath.lineTo(cX + 1, cY);
            paths.add(thirdPath);
        } else if (level == 8 && subLevel == 1) {
            title = "3_lines_1_reversed";
            GamePath path = new GamePath(parent, initializeAnimator(4500), 120);
            path.moveTo(150, height / 4);
            path.lineTo(width - 250, height / 4);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeAnimator(4500), 120);
            secondPath.moveTo(150, 3 * height / 4);
            secondPath.lineTo(width - 250, 3 * height / 4);
            paths.add(secondPath);

            GamePath thirdPath = new GamePath(parent, initializeAnimator(4500), 120);
            thirdPath.moveTo(width - 150, cY);
            thirdPath.lineTo(250, cY);
            paths.add(thirdPath);
        } else {
            throw new RuntimeException("Unknown level.");
        }

        for (GamePath path : paths) {
            path.build();
        }

        return new Pair<>(title, paths);
    }

    public static int getLevelCount() {
        return 9;
    }

    private static ValueAnimator initializeAnimator(int duration) {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 1);

        progressAnimator.setDuration(duration);
        progressAnimator.setInterpolator(new LinearInterpolator());

        return progressAnimator;
    }
}
