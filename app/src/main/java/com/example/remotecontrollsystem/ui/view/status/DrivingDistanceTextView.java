package com.example.remotecontrollsystem.ui.view.status;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.R;

import java.util.Locale;

public class DrivingDistanceTextView extends androidx.appcompat.widget.AppCompatTextView {

    private double preX, preY; // 직전 odom x, y 데이터
    private double mileage = 0f; // 총 주행 거리

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
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getWidth() / 2f / 3f);
        setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void updateDrivingDistance(double mileage) {
        String formattedNum = String.format(Locale.KOREA, "%.2fkm", mileage / 1000);
        if (formattedNum != null && !formattedNum.isEmpty()) {
            post(() -> setText(formattedNum));
        }
    }

}
