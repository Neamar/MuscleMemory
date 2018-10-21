package fr.neamar.musclememory.level;

import android.graphics.Canvas;
import android.graphics.Paint;

class Particle {
    public static final boolean SHOULD_DISAPPEAR = true;

    private float x;
    private float y;
    private double theta;
    private double ttl;
    private double speed;
    private float createdAt;
    private double halfLife;

    Particle(float x, float y, float tanX, float tanY, float currentProgress, float maxProgress) {
        this.x = x;
        this.y = y;
        this.createdAt = currentProgress;

        theta = -Math.PI / 4 + Math.atan2(-tanY, -tanX) + Math.random() * Math.PI / 2;
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

        float newX = (float) (x + speed * Math.cos(theta));
        float newY = (float) (y + speed * Math.sin(theta));

        int oldAlpha = paint.getAlpha();
        paint.setAlpha(alpha);
        canvas.drawLine(x, y, newX, newY, paint);
        paint.setAlpha(oldAlpha);
        x = newX;
        y = newY;

        return false;
    }
}
