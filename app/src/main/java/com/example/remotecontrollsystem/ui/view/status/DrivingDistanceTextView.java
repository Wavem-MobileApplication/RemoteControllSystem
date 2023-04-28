package com.example.remotecontrollsystem.ui.view.status;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.Odometry;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

import java.util.Locale;

public class DrivingDistanceTextView extends androidx.appcompat.widget.AppCompatTextView {

    private MessagePublisher<Odometry> odomPublisher;

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
        odomPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.ODOM.getType());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        odomPublisher.attach(odomObserver);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setText("0.0km");
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getWidth() / 2f / 3f);
        setTypeface(Typeface.DEFAULT_BOLD);
    }

    private final Observer<Odometry> odomObserver = new Observer<Odometry>() {
        @Override
        public void update(Odometry odometry) {
            double newX = Math.abs(odometry.getPose().getPose().getPosition().getX()); // 새로 들어온 데이터
            double newY = Math.abs(odometry.getPose().getPose().getPosition().getY());

            if (preX != newX && preY != newY) { // 직전 데이터와 현재 데이터가 다를시에만 거리 합산
                double x = Math.abs(newX - preX); // 직전 데이터와 현재 데이터의 차
                double y = Math.abs(newY - preY);
                double distance = Math.sqrt(x * x + y * y); // 두 점 사이의 거리 공식으로 거리 계산

                mileage += distance; // 계산한 거리 mileage에 누적
                Log.d("Mileage", String.valueOf(mileage));
            }

            preX = newX; // 직전 데이터 현재 데이터로 업데이트
            preY = newY;

            String formattedNum = String.format(Locale.KOREA, "%.2f km", mileage / 1000);
            setText(formattedNum);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        odomPublisher.detach(odomObserver);
    }
}
