package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.ui.util.JoystickUtil;

public class AngularJoystickView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = AngularJoystickView.class.getSimpleName();
    private Paint joystickPaint;
    private float joystickRadius;
    private float posX;
    private float maxVel = 0.45f;


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
        setBackgroundResource(R.drawable.icon_joystick_angular);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        joystickRadius = (float) (getWidth() * 0.25 / 2);
        moveTo(getWidth() / 2f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float polarX = convertPxToPolar(eventX);
        posX = convertPolarToPx(polarX);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveTo(posX);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("폴라", String.valueOf(convertPxToPolar(posX)));
                moveTo(posX);
                JoystickUtil.getInstance().publishAngularVel(polarX * maxVel);
                break;
            case MotionEvent.ACTION_UP:
                moveTo(getWidth() / 2f);
                JoystickUtil.getInstance().publishAngularVel(0);
                break;
        }
        return true;
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

        canvas.drawCircle(posX, getHeight() / 2f, joystickRadius, joystickPaint);
    }
}
