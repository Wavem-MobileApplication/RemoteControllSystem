package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class JoystickView extends View {
    private static final String TAG = JoystickView.class.getSimpleName();
    private static final int STROKE_WIDTH = 10;
    private Paint outerPaint;
    private Paint innerPaint;

    public JoystickView(Context context) {
        super(context);
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        outerPaint = new Paint();
        outerPaint.setColor(Color.BLACK);
        outerPaint.setStrokeWidth(STROKE_WIDTH);
        outerPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
