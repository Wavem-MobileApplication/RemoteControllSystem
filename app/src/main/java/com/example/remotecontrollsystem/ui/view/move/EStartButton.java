package com.example.remotecontrollsystem.ui.view.move;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class EStartButton extends View {
    private static final String TAG = EStopButton.class.getSimpleName();

    private static int MARGIN = 10;
    private static int RING_MARGIN = 20;
    private static int RING_STROKE = 10;
    private Paint circlePaint;
    private Paint ringPaint;
    private Paint textPaint;

    public EStartButton(Context context) {
        super(context);
        init();
    }

    public EStartButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        textPaint.setTextSize(getWidth() / 6);
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.parseColor("#00b050"));

        ringPaint = new Paint();
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setColor(Color.WHITE);
        ringPaint.setStrokeWidth(RING_STROKE);

        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.WHITE);

        setClickable(true);
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

        float radius = getWidth() / 2 - MARGIN;
        float ringRadius = radius - RING_MARGIN;
        float centerX = getPivotX();
        float centerY = getPivotY();

        canvas.drawCircle(centerX, centerY, radius, circlePaint);
        canvas.drawCircle(centerX, centerY , ringRadius, ringPaint);
        canvas.drawText("START", centerX, centerY + getFontMetrics(), textPaint);
    }
}
