package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.ui.util.GestureUtil;


public class MapFrameLayout extends FrameLayout {
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;

    private PoseJoystickView poseJoystickView;
    private GoalFrameView goalFrameView;

    private GridMapView gridMapView;
    private NavigationView navigationView;
    private LaserScanView laserScanView;

    public MapFrameLayout(@NonNull Context context, AppCompatActivity activity) {
        super(context);
        init(activity);
    }

    private void init(AppCompatActivity activity) {
        settingGestures();
        setRotationX(180);

        poseJoystickView = new PoseJoystickView(getContext(), activity);
        goalFrameView = new GoalFrameView(getContext(), activity);

        gridMapView = new GridMapView(getContext());
        navigationView = new NavigationView(getContext());
        laserScanView = new LaserScanView(getContext());

        addView(gridMapView);
        addView(poseJoystickView);
        addView(navigationView);
        addView(goalFrameView);
        addView(laserScanView);

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

    public void updateRobotPose(Pose pose) {

    }
}
