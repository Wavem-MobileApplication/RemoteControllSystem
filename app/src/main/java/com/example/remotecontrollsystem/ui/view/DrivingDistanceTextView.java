package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.R;

public class DrivingDistanceTextView extends androidx.appcompat.widget.AppCompatTextView {
    public DrivingDistanceTextView(@NonNull Context context) {
        super(context);
        init();
    }

    public DrivingDistanceTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
        setTextColor(getResources().getColor(R.color.color_text_sky_blue));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setText("0.0km");
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getWidth() / 2 / 3);
        setTypeface(Typeface.DEFAULT_BOLD);
    }
}
