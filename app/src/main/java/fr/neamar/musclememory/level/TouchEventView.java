package fr.neamar.musclememory.level;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;


public class TouchEventView extends View {
    public String currentText = "";

    public int currentFps = 0;
    public int minFps = 500;
    public String fpsText = "? fps";
    public ValueAnimator fpsAnimator;

    private int[] colors = {Color.parseColor("#5C6BC0"), Color.parseColor("#3949AB"), Color.parseColor("#283593"), Color.parseColor("#1A237E")};

    protected SparseArray<PointF> activePointers;

    private Paint mPaint;
    private Paint textPaint;

    public TouchEventView(Context context, AttributeSet attrs) {
        super(context, attrs);

        fpsAnimator = ValueAnimator.ofInt(0, 10);
        fpsAnimator.setDuration(1000);
        fpsAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                minFps = Math.min(minFps, currentFps);
                fpsText = currentFps + " fps, min " + minFps;
                currentFps = 0;
                super.onAnimationRepeat(animation);
            }
        });
        fpsAnimator.setRepeatCount(ValueAnimator.INFINITE);
        fpsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentFps += 1;
            }
        });
        fpsAnimator.start();

        initView();
    }

    private void initView() {
        activePointers = new SparseArray<>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(20);
        textPaint.setColor(Color.BLACK);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                // We have a new pointer. Lets add it to the list of pointers
                int pointerIndex = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndex);

                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                activePointers.put(pointerId, f);
                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = activePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                int pointerId = event.getPointerId(event.getActionIndex());

                activePointers.remove(pointerId);
                break;
            }
        }
        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw all pointers
        for (int size = activePointers.size(), i = 0; i < size; i++) {
            PointF point = activePointers.valueAt(i);
            if (point != null) {
                mPaint.setColor(colors[i % colors.length]);
                canvas.drawLine(0, point.y, getWidth(), point.y, mPaint);
                canvas.drawLine(point.x, 0, point.x, getHeight(), mPaint);
            }
        }
        canvas.drawText(currentText, 10, 40, textPaint);
        canvas.drawText(fpsText, 10, getHeight() - 50, textPaint);
    }

    protected void onDestroy() {
        fpsAnimator.removeAllUpdateListeners();
        fpsAnimator.removeAllListeners();
        fpsAnimator.cancel();
    }
}
