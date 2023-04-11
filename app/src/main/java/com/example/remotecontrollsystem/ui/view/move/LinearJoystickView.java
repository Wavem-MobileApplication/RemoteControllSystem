package com.example.remotecontrollsystem.ui.view.move;

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

public class LinearJoystickView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = LinearJoystickView.class.getSimpleName();

    private Paint joystickPaint;
    private float joystickRadius;
    private float posY;
    private float maxVel = 0.45f;

    public LinearJoystickView(Context context) {
        super(context);
        init();
    }

    public LinearJoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        joystickPaint = new Paint();
        joystickPaint.setColor(Color.parseColor("#ed1c24"));
        joystickPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        setBackgroundResource(R.drawable.icon_joystick_linear);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        joystickRadius = (float) (getWidth() * 0.25 / 2);
        moveTo(getHeight() / 2f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventY = event.getY();
        float polarY = convertPxToPolar(eventY);
        posY = convertPolarToPx(polarY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveTo(posY);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("폴라Y", String.valueOf(polarY));
                moveTo(posY);
                JoystickUtil.getInstance().publishLinearVel(-polarY * maxVel);
                break;
            case MotionEvent.ACTION_UP:
                moveTo(getHeight() / 2f);
                JoystickUtil.getInstance().publishLinearVel(0);
                break;
        }

        return true;
    }

    private float convertPxToPolar(float y) {
        float centerY = getHeight() / 2f;
        float polarY = Math.min(1, (y - centerY) / centerY);

        if (polarY < -1) {
            polarY = -1;
        }
        
        return polarY;
    }

    private float convertPolarToPx(float y) {
        float centerY = getHeight() / 2f;
        float py = y * centerY + centerY;

        return py;
    }

    private void moveTo(float y) {
        posY = y;
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

        canvas.drawCircle(getWidth() / 2f, posY, joystickRadius, joystickPaint);
    }
}
