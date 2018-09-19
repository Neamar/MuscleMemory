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
        int D = Math.min(cX, cY);

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
            title = "solid_spiral_triangle";
            GamePath path = new GamePath(parent, initializeAnimator(6000));
            int p = D / 4;
            float H = (float) (Math.sqrt(7) * p);
            path.moveTo(cX, cY + 3 * p); // 1
            path.rLineTo(-3 * p, -H); // 2
            path.rLineTo(H, -3 * p); // 3
            path.rLineTo(3 * p, H); // 4
            path.rLineTo(-3 * H / 4, 9 * p / 4); // 5
            path.rLineTo(-9 * p / 4, -3 * H / 4); // 6
            path.rLineTo(H / 2, -3 * p / 2); // 7
            path.rLineTo(3 * p / 2, H / 2); // 8
            path.rLineTo(-H / 4, 3 * p / 4); // 9
            path.rLineTo(-3 * p / 4, -H / 4); // 9

            paths.add(path);
        } else if (level == 7 && subLevel == 1) {
            title = "solid_spiral_trigo";
            GamePath path = new GamePath(parent, initializeAnimator(6000));
            int radius;
            for (int i = 9; i >= 0; i--) {
                radius = D * (10 - i) / 10;
                int x = (int) (cX + radius * Math.cos(i * Math.PI / 2));
                int y = (int) (cY + radius * Math.sin(i * Math.PI / 2));
                if (i == 9) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            paths.add(path);
        } else if (level == 8 && subLevel == 0) {
            title = "spiral_circle";
            GamePath path = new GamePath(parent, initializeAnimator(8000));
            int radius = D - 50;
            int radiusDecay = radius / 8;
            path.moveTo(cX + radius, cY);
            drawCircleQuadrant(path, 0, cX, cY, radius);
            drawCircleQuadrant(path, 1, cX, cY, radius);
            radius -= radiusDecay;
            int newcX = cX - radiusDecay;
            drawCircleQuadrant(path, 2, newcX, cY, radius);
            drawCircleQuadrant(path, 3, newcX, cY, radius);
            radius -= radiusDecay;
            newcX = newcX + radiusDecay;
            drawCircleQuadrant(path, 0, newcX, cY, radius);
            drawCircleQuadrant(path, 1, newcX, cY, radius);
            radius -= radiusDecay;
            newcX = newcX - radiusDecay;
            drawCircleQuadrant(path, 2, newcX, cY, radius);
            drawCircleQuadrant(path, 3, newcX, cY, radius);
            radius -= radiusDecay;
            newcX = newcX + radiusDecay;
            drawCircleQuadrant(path, 0, newcX, cY, radius);
            drawCircleQuadrant(path, 1, newcX, cY, radius);
            radius -= radiusDecay;
            newcX = newcX - radiusDecay;
            drawCircleQuadrant(path, 2, newcX, cY, radius);
            drawCircleQuadrant(path, 3, newcX, cY, radius);

            paths.add(path);
        } else if (level == 8 && subLevel == 1) {
            title = "tear";
            GamePath path = new GamePath(parent, initializeAnimator(6000));
            float c = (float) 0.5519;
            int radius = D - 90;
            float cRadius = c * radius;
            path.moveTo(cX + radius, cY);
            path.cubicTo(cX + radius, cY - cRadius, cX + cRadius, cY - radius, cX, cY - radius);
            path.cubicTo(cX - cRadius, cY - radius, cX - radius, cY - cRadius, cX - radius, cY - radius);
            path.cubicTo(cX - radius, cY + cRadius, cX - cRadius, cY + radius, cX, cY + radius);
            path.cubicTo(cX + cRadius, cY + radius, cX + radius, cY + cRadius, cX + radius, cY);
            paths.add(path);
        } else if (level == 9 && subLevel == 0) {
            title = "candy_same_side";
            int w = width / 10;
            int h = height / 6;
            GamePath path = new GamePath(parent, initializeAnimator(8000));
            path.moveTo(w, 5 * h);
            path.lineTo(w, h);
            path.lineTo(3 * w, h);
            path.lineTo(4 * w, 3 * h);
            path.lineTo(6 * w, 3 * h);
            path.lineTo(7 * w, 5 * h);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeAnimator(8000));
            secondPath.moveTo(3 * w, 5 * h);
            secondPath.lineTo(4 * w, 3 * h);
            secondPath.lineTo(6 * w, 3 * h);
            secondPath.lineTo(7 * w, h);
            secondPath.lineTo(9 * w, h);
            secondPath.lineTo(9 * w, 5 * h);


            paths.add(secondPath);
        } else if (level == 9 && subLevel == 1) {
            title = "candy_cross";
            int w = width / 13;
            int h = height / 6;
            GamePath path = new GamePath(parent, initializeAnimator(6000));
            path.moveTo(2 * w, 2 * h);
            path.lineTo(3 * w, 3 * h);
            path.lineTo(7 * w, 3 * h);
            path.lineTo(9 * w, 5 * h);
            path.lineTo(12 * w, 5 * h);
            paths.add(path);

            GamePath secondPath = new GamePath(parent, initializeAnimator(6000));
            secondPath.moveTo(12 * w, 5 * h);
            secondPath.lineTo(12 * w, h);
            secondPath.lineTo(9 * w, h);
            secondPath.lineTo(7 * w, 3 * h);
            secondPath.lineTo(3 * w, 3 * h);
            secondPath.lineTo(2 * w, 4 * h);

            paths.add(secondPath);
        } else if (level == 10 && subLevel == 0) {
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
        } else if (level == 10 && subLevel == 1) {
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
        return 10;
    }

    private static ValueAnimator initializeAnimator(int duration) {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 1);

        progressAnimator.setDuration(duration);
        progressAnimator.setInterpolator(new LinearInterpolator());

        return progressAnimator;
    }

    private static void drawCircleQuadrant(GamePath path, int quadrant, int cX, int cY, float radius) {
        // see http://spencermortensen.com/articles/bezier-circle/
        float c = (float) 0.5519;
        float cRadius = c * radius;
        if (quadrant == 0) {
            path.cubicTo(cX + radius, cY - cRadius, cX + cRadius, cY - radius, cX, cY - radius);
        } else if (quadrant == 1) {
            path.cubicTo(cX - cRadius, cY - radius, cX - radius, cY - cRadius, cX - radius, cY);
        } else if (quadrant == 2) {
            path.cubicTo(cX - radius, cY + cRadius, cX - cRadius, cY + radius, cX, cY + radius);
        } else if (quadrant == 3) {
            path.cubicTo(cX + cRadius, cY + radius, cX + radius, cY + cRadius, cX + radius, cY);
        } else {
            throw new RuntimeException("Invalid quadrant");
        }
    }
}
