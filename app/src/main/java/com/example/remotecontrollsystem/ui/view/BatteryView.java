package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class BatteryView extends View {
    private static final String TAG = BatteryView.class.getSimpleName();
    private static final int STROKE_WIDTH = 30;
    private Paint archPaint;
    private Paint circlePaint;
    private Paint textPaint;
    private float battery = 0f;
    private int maxOperationTime = 60 * 4;
    private boolean isPercentMode = true;

    public BatteryView(Context context) {
        super(context);
        init();
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setContentDescription("View for cars battery status");

        archPaint = new Paint();
        archPaint.setStyle(Paint.Style.STROKE);
        archPaint.setStrokeWidth(STROKE_WIDTH);

        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(STROKE_WIDTH);
        circlePaint.setColor(Color.WHITE);

        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.parseColor("#14dafe"));
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        setOnClickListener(v -> {
            isPercentMode = !isPercentMode;// Change isPercentMode state.
            invalidate();
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateUI(88.4f);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        textPaint.setTextSize(getWidth() / 2 / 3);
    }

    private void updateUI(float battery) {
        this.battery = battery;

        // Set arch color of remaining battery capacity.
        if (battery >= 80) {
            archPaint.setColor(Color.parseColor("#47ccb7"));
        } else if (battery >= 60) {
            archPaint.setColor(Color.parseColor("#68cc76"));
        } else if (battery >= 40) {
            archPaint.setColor(Color.parseColor("#ffcf48"));
        } else if (battery >= 20) {
            archPaint.setColor(Color.parseColor("#ff864e"));
        } else {
            archPaint.setColor(Color.parseColor("#fc4a49"));
        }

        invalidate();
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

        float centerX = getPivotX();
        float centerY = getPivotY();
        float textSize = textPaint.getTextSize();

        float radius = (getWidth() - STROKE_WIDTH * 2) / 2;
        canvas.drawCircle(centerX, centerY, radius, circlePaint); // Draw background white ring.

        RectF rect = new RectF();
        rect.set(STROKE_WIDTH, STROKE_WIDTH, getWidth() - STROKE_WIDTH, getHeight() - STROKE_WIDTH);
        float startAngle = 270;
        float angle = battery / 100 * 360; // 360(Max Angle), 100(Max Battery), battery(Remain Battery)
        canvas.drawArc(rect, startAngle, -angle, false, archPaint); // Draw percent of battery to arch.

        if (isPercentMode) {
            canvas.drawText(battery + "%", centerX, centerY + getFontMetrics(), textPaint);
        } else {
            int remainTime = (int) (maxOperationTime * (battery / 100));
            String hour = remainTime / 60 + "시간";
            String min = remainTime % 60 + "분";
            canvas.drawText(hour, centerX, centerY + getFontMetrics() - textSize / 2, textPaint);
            canvas.drawText(min, centerX, centerY + getFontMetrics() + textSize / 2, textPaint);
        }

        Log.d("뷰 크기", String.valueOf(getWidth()));
        Log.d("텍스트 사이즈", String.valueOf(textPaint.getTextSize()));
    }
}
