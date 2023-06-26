package com.example.remotecontrollsystem.ui.view.move;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.ui.util.ArrowPath;

import java.util.Timer;
import java.util.TimerTask;

public class LinearJoystickView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = LinearJoystickView.class.getSimpleName();

    private Paint outerPaint;
    private Paint joystickPaint;
    private Paint arrowPaint;
    private float joystickRadius;
    private float posY;
    private float polarY;
    private float maxVel = 0.45f;

    private RectF arcRect;
    private Path topArrow;
    private Path bottomArrow;

    private OnLinearJoystickMoveListener linearJoystickMoveListener;
    private Timer timer;
    private TimerTask timerTask;

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

        outerPaint = new Paint();
        outerPaint.setColor(Color.parseColor("#9BA4B5"));
        outerPaint.setStyle(Paint.Style.STROKE);
        outerPaint.setStrokeCap(Paint.Cap.BUTT);

        arrowPaint = new Paint();
        arrowPaint.setColor(Color.RED);
        arrowPaint.setStyle(Paint.Style.FILL);

        arcRect = new RectF();
/*        topArrow = new Path();
        bottomArrow = new Path();*/
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        joystickRadius = (float) (getWidth() * 0.25 / 2);
        moveTo(getHeight() / 2f);

        int width = getWidth();
        int height = getHeight();
        float stroke = Math.min(width / 4f, height / 4f);

        outerPaint.setStrokeWidth(stroke);
        arcRect.set(stroke / 2f, stroke / 2f, width - stroke / 2f, height - stroke / 2f);

        float padding = stroke * 0.1f;
        Log.d("padding", String.valueOf(padding));

        // Draw top arrow
        float arrowPadding = joystickRadius * 0.5f; // 화살표의 여백
        float arrowLength = joystickRadius * 2.5f; // 화살표의 길이

        topArrow = new Path();
        topArrow.moveTo(getWidth() / 2f, joystickRadius + arrowPadding); // 시작 지점

        // 화살표의 끝 지점 (위쪽으로 화살표가 향하도록 설정)
        topArrow.lineTo(getWidth() / 2f - arrowLength / 2f, joystickRadius + arrowPadding + arrowLength);
        topArrow.lineTo(getWidth() / 2f + arrowLength / 2f, joystickRadius + arrowPadding + arrowLength);
        topArrow.close();

        // Draw bottom arrow
        bottomArrow = new Path();
        bottomArrow.moveTo(getWidth() / 2f, getHeight() - joystickRadius - arrowPadding); // 시작 지점

        // 화살표의 끝 지점 (아래쪽으로 화살표가 향하도록 설정)
        bottomArrow.lineTo(getWidth() / 2f - arrowLength / 2f, getHeight() - joystickRadius - arrowPadding - arrowLength);
        bottomArrow.lineTo(getWidth() / 2f + arrowLength / 2f, getHeight() - joystickRadius - arrowPadding - arrowLength);
        bottomArrow.close();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventY = event.getY();
        polarY = convertPxToPolar(eventY);
        posY = convertPolarToPx(polarY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTimerTask();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTo(posY);
                break;
            case MotionEvent.ACTION_UP:
                stopTimerTask();
                moveTo(getHeight() / 2f);
                if (linearJoystickMoveListener != null) {
                    Log.d("Action Up", "Not Null");
                    linearJoystickMoveListener.onMove(0);
                }
                Log.d(TAG, "ACTION_UP");
                break;
        }

        return true;
    }

    private void initializeTimerTask() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (linearJoystickMoveListener != null) {
                    linearJoystickMoveListener.onMove(-polarY * maxVel);
                    Log.d(TAG, String.valueOf(-polarY * maxVel));
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

        canvas.drawArc(arcRect, -135, 90, false, outerPaint);
        canvas.drawArc(arcRect, 45, 90, false, outerPaint);

/*        canvas.drawPath(topArrow, arrowPaint);
        canvas.drawPath(bottomArrow, arrowPaint);*/

//        canvas.drawPath(topArrow, arrowPaint);

        canvas.drawCircle(getWidth() / 2f, posY, joystickRadius, joystickPaint);
    }

    public void setLinearJoystickMoveListener(OnLinearJoystickMoveListener linearJoystickMoveListener) {
        this.linearJoystickMoveListener = linearJoystickMoveListener;
    }

    public interface OnLinearJoystickMoveListener {
        void onMove(float linear);
    }
}
