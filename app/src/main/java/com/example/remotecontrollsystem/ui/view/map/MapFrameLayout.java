package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.OccupancyGrid;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.ui.util.GestureUtil;
import com.google.gson.Gson;


public class MapFrameLayout extends FrameLayout {
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;

    private PoseJoystickView poseJoystickView;

    public MapFrameLayout(@NonNull Context context, AppCompatActivity activity) {
        super(context);
        init(activity);
    }

    private void init(AppCompatActivity activity) {
        settingGestures();
        setRotationX(180);

        GridMapView gridMapView = new GridMapView(getContext());
        poseJoystickView = new PoseJoystickView(getContext(), activity);

        addView(gridMapView);
        addView(poseJoystickView);
        addView(new NavigationView(getContext()));
        addView(new GoalFrameView(getContext(), activity));
        addView(new LaserScanView(getContext()));

        gridMapView.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> {
            View parent = (View) getParent();
            int width = view.getWidth();
            int height = view.getHeight();
            int parentWidth = parent.getWidth();
            int parentHeight = parent.getHeight();
            float x = (parentWidth - width) / 2f;
            float y = (parentHeight - height) / 2f;
            float scale;

            if (width > height) {
                scale = (float) parentWidth / (float) width;
            } else {
                scale = (float) parentHeight / (float) height;
            }

            post(() -> {
                getLayoutParams().width = width;
                getLayoutParams().height = height;

                requestLayout();

                setScaleX(scale);
                setScaleY(scale);

                setX(x);
                setY(y);
            });
        });
    }

    private void settingGestures() {
        GestureUtil util = new GestureUtil();
        gestureDetector = new GestureDetector(getContext(), util.getGestureListener(this));
        scaleDetector = new ScaleGestureDetector(getContext(), util.getScaleListener(this));
    }

    public void startPoseJoystick(MotionEvent e) {
        poseJoystickView.setX(e.getX());
        poseJoystickView.setY(e.getY());
        poseJoystickView.setVisibility(VISIBLE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (poseJoystickView.getVisibility() == VISIBLE) {
            poseJoystickView.dispatchTouchEvent(event);
        }
        gestureDetector.onTouchEvent(event);
        scaleDetector.onTouchEvent(event);

        return true;
    }
}
