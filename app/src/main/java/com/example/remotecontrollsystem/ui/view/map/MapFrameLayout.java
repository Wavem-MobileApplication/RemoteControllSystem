package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.msgs.LaserScan;
import com.example.remotecontrollsystem.mqtt.msgs.OccupancyGrid;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.TFMessage;
import com.example.remotecontrollsystem.ui.util.GestureUtil;


public class MapFrameLayout extends FrameLayout {
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;

    private GridMapView gridMapView;
    private NavigationView navigationView;
    private LaserScanView laserScanView;
    private GoalFrameView goalFrameView;
    private PoseJoystickView poseJoystickView;

    public MapFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        settingGestures();
        setRotationX(180);

        gridMapView = new GridMapView(getContext());
        navigationView = new NavigationView(getContext());
        laserScanView = new LaserScanView(getContext());
        goalFrameView = new GoalFrameView(getContext());
        poseJoystickView = new PoseJoystickView(getContext());

        addView(gridMapView);
        addView(navigationView);
        addView(laserScanView);
        addView(goalFrameView);
        addView(poseJoystickView);

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

    public void updateMap(OccupancyGrid occupancyGrid) {
        gridMapView.updateMap(occupancyGrid);
        navigationView.updateMapMetaData(occupancyGrid.getInfo());
        laserScanView.updateMapMetaData(occupancyGrid.getInfo());
        goalFrameView.updateMapMetaData(occupancyGrid.getInfo());
        poseJoystickView.updateMapMetaData(occupancyGrid.getInfo());
    }

    public void updateRobotPose(Pose pose) {
        navigationView.updateRobotPose(pose);
        laserScanView.updateRobotPose(pose);
    }

    public void updateLaserScan(LaserScan laserScan) {
        laserScanView.updateLaserScan(laserScan);
    }

    public void updateTF(TFMessage tfMessage) {
        laserScanView.updateTF(tfMessage);
    }

    public void updateCurrentRoute(Route route) {
        goalFrameView.updateCurrentRoute(route);
    }

    public void updateNewWaypoint(Waypoint waypoint) {
        goalFrameView.updateNewWaypoint(waypoint);
    }

    public void setPoseViewClickListener(GoalFrameView.OnPoseViewClickListener listener) {
        goalFrameView.setPoseViewClickListener(listener);
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
