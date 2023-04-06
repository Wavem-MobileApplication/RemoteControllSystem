package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class JoystickView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = JoystickView.class.getSimpleName();
    private static final int STROKE_WIDTH = 10;

    public JoystickView(Context context) {
        super(context);
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
