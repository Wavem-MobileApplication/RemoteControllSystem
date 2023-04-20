package com.example.remotecontrollsystem.ui.view.status;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.Odometry;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.google.gson.Gson;

import java.util.Locale;

public class DrivingDistanceTextView extends androidx.appcompat.widget.AppCompatTextView {

    private MessagePublisher odomPublisher;
    private Gson gson;

    private double preX, preY;
    private double mileage = 0f;

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
        odomPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.ROBOT_POSE.getType());
        gson = new Gson();
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
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getWidth() / 2 / 3);
        setTypeface(Typeface.DEFAULT_BOLD);
    }

    Observer odomObserver = new Observer() {
        @Override
        public void update(String message) {
            Odometry odometry = gson.fromJson(message, Odometry.class);
            double newX = Math.abs(odometry.getPose().getPose().getPosition().getX());
            double newY = Math.abs(odometry.getPose().getPose().getPosition().getY());

            if (preX != newX && preY != newY) {
                double x = Math.abs(newX - preX);
                double y = Math.abs(newY - preY);
                double distance = Math.sqrt(x * x + y * y);

                mileage += distance;
            }

            preX = newX;
            preY = newY;

            String formattedNum = String.format(Locale.KOREA, "%.2f Km", mileage / 1000);
            setText(formattedNum);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        odomPublisher.detach(odomObserver);
    }
}
