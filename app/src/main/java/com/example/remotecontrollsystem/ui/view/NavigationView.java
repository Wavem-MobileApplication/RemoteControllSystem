package com.example.remotecontrollsystem.ui.view;


import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NavigationView extends androidx.appcompat.widget.AppCompatImageView {

    private static final int DEFAULT_ICON_SIZE = 20;

    float resolution = 0.05f;
    double originX = 0, originY = 0, originZ = 0, originW = 0;

    public NavigationView(@NonNull Context context) {
        super(context);
    }

    public NavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        init();
    }

/*    private void init() {
        setImageResource(R.drawable.icon_navigation);
        getLayoutParams().width = DEFAULT_ICON_SIZE;
        getLayoutParams().height = DEFAULT_ICON_SIZE;

        observeMap();
        subscribeTopic("/robot_pose");
    }

    private void observeMap() {
        MapInfo.getInstance().observeMap(new MapInfo.OnMapChangeListener() {
            @Override
            public void onChange(OccupancyGrid occupancyGrid) {
                resolution = occupancyGrid.getInfo().getResolution();
                originX = occupancyGrid.getInfo().getOrigin().getPosition().getX();
                originY = occupancyGrid.getInfo().getOrigin().getPosition().getY();
                originZ = occupancyGrid.getInfo().getOrigin().getOrientation().getZ();
                originW = occupancyGrid.getInfo().getOrigin().getOrientation().getW();
            }
        });
    }

    private void subscribeTopic(String topicName) {
        if (subscription != null) {
            SubNode.getInstance().getNode().removeSubscription(subscription);
        }

        subscription = SubNode.getInstance().getNode().createSubscription(Pose.class, topicName, pose -> {
            double poseX = pose.getPosition().getX();
            double poseY = pose.getPosition().getY();
            double poseZ = pose.getOrientation().getZ();
            double poseW = pose.getOrientation().getW();

            updateNavigationPosition(poseX, poseY, poseZ, poseW);
        });
    }

    private void updateNavigationPosition(double poseX, double poseY, double poseZ, double poseW) {
        float navX = (float) (((poseX + originX) / resolution) - getWidth() / 2);
        float navY = (float) (((poseY + originY) / resolution) - getHeight() / 2);
        float navZ = (float) (poseZ + originZ);
        float navW = (float) (poseW + originW);
        float degree = RosMath.QuaternionToAngular(navZ, navW);

        setX(navX);
        setY(navY);
        setRotation(degree);
    }*/
}
