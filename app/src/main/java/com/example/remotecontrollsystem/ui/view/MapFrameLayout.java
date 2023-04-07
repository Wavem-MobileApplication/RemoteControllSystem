package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.OccupancyGrid;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.ui.util.GestureUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class MapFrameLayout extends FrameLayout {
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;

    public MapFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public MapFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void init() {
        settingGestures();
        setRotationX(180);
        addView(new GridMapView(getContext()));
        addView(new NavigationView(getContext()));

        MessagePublisher publisher = Mqtt.getInstance().getMqttMessageListener(WidgetType.MAP.getType());
        publisher.attach(new Observer() {
            @Override
            public void update(String message) {
                Type type = new TypeToken<OccupancyGrid>() {}.getType();
                OccupancyGrid occupancyGrid = new Gson().fromJson(message, type);
                getLayoutParams().width = occupancyGrid.getInfo().getWidth();
                getLayoutParams().height = occupancyGrid.getInfo().getHeight();
                requestLayout();
            }
        });
    }

    private void settingGestures() {
        GestureUtil util = new GestureUtil();
        gestureDetector = new GestureDetector(getContext(), util.getGestureListener(this));
        scaleDetector = new ScaleGestureDetector(getContext(), util.getScaleListener(this));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        scaleDetector.onTouchEvent(event);
        return true;
    }
}
