package fr.neamar.musclememory.level;

import android.graphics.Canvas;
import android.graphics.Paint;

class Particle {
    public static final boolean SHOULD_DISAPPEAR = true;

    private float x;
    private float y;
    private double cosTheta;
    private double sinTheta;
    private double ttl;
    private double speed;
    private float createdAt;
    private double halfLife;

    Particle(float x, float y, float tanX, float tanY, float currentProgress, float maxProgress) {
        this.x = x;
        this.y = y;
        this.createdAt = currentProgress;

        double theta = -Math.PI / 4 + Math.atan2(-tanY, -tanX) + Math.random() * Math.PI / 2;
        cosTheta = Math.cos(theta);
        sinTheta = Math.sin(theta);
        ttl = Math.random() * (maxProgress - currentProgress);
        halfLife = ttl / 2;
        speed = 5;// + 10 * Math.random();
    }

    boolean onDraw(Canvas canvas, Paint paint, float currentProgress) {
        if (createdAt + ttl < currentProgress) {
            return SHOULD_DISAPPEAR;
        }

        int alpha = 255;
        if(createdAt + halfLife < currentProgress) {
            alpha = 255 - (int) (((currentProgress - createdAt - halfLife) / halfLife) * 255);
        }

        float newX = (float) (x + speed * cosTheta);
        float newY = (float) (y + speed * sinTheta);

        paint.setAlpha(alpha);
        canvas.drawLine(x, y, newX, newY, paint);
        paint.setAlpha(1);
        x = newX;
        y = newY;

        return false;
    }
}
