package com.example.remotecontrollsystem.ui.view.move;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class AngularJoystickView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = AngularJoystickView.class.getSimpleName();

    private Paint outerPaint;
    private Paint joystickPaint;
    private float joystickRadius;
    private float posX;
    private float maxVel = 0.45f;
    float polarX;

    private RectF arcRect;

    private OnAngularJoystickMoveListener angularJoystickMoveListener;
    private Timer timer;
    private TimerTask timerTask;

    public AngularJoystickView(Context context) {
        super(context);
        init();
    }

    public AngularJoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        joystickPaint = new Paint();
        joystickPaint.setColor(Color.parseColor("#ed1c24"));

        outerPaint = new Paint();
        outerPaint.setColor(Color.parseColor("#9BA4B5"));
        outerPaint.setStyle(Paint.Style.STROKE);

        arcRect = new RectF();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        joystickRadius = (float) (getWidth() * 0.25 / 2);
        moveTo(getWidth() / 2f);

        int width = getWidth();
        int height = getHeight();
        float stroke = Math.min(width / 4f, height / 4f);

        outerPaint.setStrokeWidth(stroke);
        arcRect.set(stroke / 2f, stroke / 2f, width - stroke / 2f, height - stroke / 2f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        polarX = convertPxToPolar(eventX);
        posX = convertPolarToPx(polarX);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTimerTask();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTo(posX);
                break;
            case MotionEvent.ACTION_UP:
                stopTimerTask();
                moveTo(getWidth() / 2f);
                if (angularJoystickMoveListener != null) {
                    angularJoystickMoveListener.onMove(0);
                }
                break;
        }
        return true;
    }


    private void initializeTimerTask() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (angularJoystickMoveListener != null) {
                    angularJoystickMoveListener.onMove(-polarX * maxVel);
                    Log.d(TAG, String.valueOf(-polarX * maxVel));
                }
            }
        };
    }

    private void startTimerTask() {
        initializeTimerTask();
        timer.schedule(timerTask, 0, 100);
    }

    private void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private float convertPxToPolar(float x) {
        float centerX = getWidth() / 2f;
        float polarX = Math.min(1, (x - centerX) / centerX);
        if (polarX < -1) {
            polarX = -1;
        }
        return polarX;
    }

    private float convertPolarToPx(float x) {
        float centerX = getWidth() / 2f;
        float px = x * centerX + centerX;

        return px;
    }

    private void moveTo(float x) {
        posX = x;
        invalidate();
    }

    public float getMaxVel() {
        return maxVel;
    }

    public void setMaxVel(float maxVel) {
        this.maxVel = maxVel;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(arcRect, -45, 90, false, outerPaint);
        canvas.drawArc(arcRect, 135, 90, false, outerPaint);

        canvas.drawCircle(posX, getHeight() / 2f, joystickRadius, joystickPaint);
    }

    public void setAngularJoystickMoveListener(OnAngularJoystickMoveListener angularJoystickMoveListener) {
        this.angularJoystickMoveListener = angularJoystickMoveListener;
    }

    public interface OnAngularJoystickMoveListener {
        void onMove(float angular);
    }
}
