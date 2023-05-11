package com.example.remotecontrollsystem.ui.view.map;


import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.mqtt.msgs.MapMetaData;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.utils.RosMath;

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

        setImageResource(R.drawable.icon_navigation);
        getLayoutParams().width = DEFAULT_ICON_SIZE;
        getLayoutParams().height = DEFAULT_ICON_SIZE;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        updateNavigationPosition(-100, -100, 1, 0);
    }

    public void updateMapMetaData(MapMetaData mapMetaData) {
        resolution = mapMetaData.getResolution();
        originX = mapMetaData.getOrigin().getPosition().getX();
        originY = mapMetaData.getOrigin().getPosition().getY();
        originZ = mapMetaData.getOrigin().getOrientation().getZ();
        originW = mapMetaData.getOrigin().getOrientation().getW();
    }

    public void updateRobotPose(Pose pose) {
        updateNavigationPosition(
                pose.getPosition().getX(), pose.getPosition().getY(), // position x, y
                pose.getOrientation().getZ(), pose.getOrientation().getW() // orientation z, w
        );
    }

    private void updateNavigationPosition(double poseX, double poseY, double poseZ, double poseW) {
        float navX = (float) (((poseX + originX) / resolution) - getWidth() / 2);
        float navY = (float) (((poseY + originY) / resolution) - getHeight() / 2);
        float navZ = (float) poseZ;
        float navW = (float) poseW;

        float degree = RosMath.QuaternionToAngular(navZ, navW);
        float originDegree = RosMath.QuaternionToAngular(originZ, originW);

        setX(navX);
        setY(navY);
        setRotation(degree + originDegree + 90);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
