package com.example.remotecontrollsystem.ui.view.status;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class ClockView extends View {
    private static final String TAG = ClockView.class.getSimpleName();

    private static final int BIG_STROKE = 5;
    private Paint bigGraduationPaint;
    private Paint minTimerPaint;
    private Paint textPaint;
    private Timer timer;
    private int sec = 0;

    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        textPaint.setTextSize(getWidth() / 2 / 3);
    }

    private void init() {
        bigGraduationPaint = new Paint();
        bigGraduationPaint.setColor(Color.RED);
        bigGraduationPaint.setStrokeWidth(BIG_STROKE);

        minTimerPaint = new Paint();
        minTimerPaint.setColor(Color.parseColor("#14dafe"));
        minTimerPaint.setStrokeWidth(BIG_STROKE);
        minTimerPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#14dafe"));
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);

        startTimer();
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sec++;
                invalidate();
            }
        }, 1000, 1000);
    }

    private float getFontMetrics() {
        float ascent = Math.abs(textPaint.getFontMetrics().ascent);
        float descent = Math.abs(textPaint.getFontMetrics().descent);

        if (ascent > descent) {
            return (ascent - descent) / 2;
        } else {
            return -(ascent - descent) / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = getWidth() / 2f - BIG_STROKE;

        for (int i = 0; i < 12; i++) {
            int angle = i * 30;
            float startX = (float) (centerX + radius * Math.cos(Math.toRadians(angle)));
            float startY = (float) (centerY + radius * Math.sin(Math.toRadians(angle)));
            float endX = (float) (centerX + (radius * 0.9f) * Math.cos(Math.toRadians(angle)));
            float endY = (float) (centerY + (radius * 0.9f) * Math.sin(Math.toRadians(angle)));
            canvas.drawLine(startX, startY, endX, endY, bigGraduationPaint);
        }

        int angleSec = sec * 6 - 90;
        float length = radius * 0.1f;
        float xSeconds = (float) (centerX + radius * Math.cos(Math.toRadians(angleSec)));
        float ySeconds = (float) (centerY + radius * Math.sin(Math.toRadians(angleSec)));

        float x1 = (float) (xSeconds - length * Math.cos(Math.toRadians(angleSec)));
        float y1 = (float) (ySeconds - length * Math.sin(Math.toRadians(angleSec)));
        float x2 = (float) (xSeconds - length * Math.cos(Math.toRadians(angleSec - 120)) / 2);
        float y2 = (float) (ySeconds - length * Math.sin(Math.toRadians(angleSec - 120)) / 2);
        float x3 = (float) (xSeconds - length * Math.cos(Math.toRadians(angleSec + 120)) / 2);
        float y3 = (float) (ySeconds - length * Math.sin(Math.toRadians(angleSec + 120)) / 2);

        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.close();

        canvas.drawPath(path, minTimerPaint);

        int hour = sec / 3600;
        int min = (sec % 3600) / 60;
        String hourText = hour + "시간";
        String minText = min + "분";
        canvas.drawText(hourText, centerX, centerY + getFontMetrics() - textPaint.getTextSize() / 2, textPaint);
        canvas.drawText(minText, centerX, centerY + getFontMetrics() + textPaint.getTextSize() / 2, textPaint);
    }
}
