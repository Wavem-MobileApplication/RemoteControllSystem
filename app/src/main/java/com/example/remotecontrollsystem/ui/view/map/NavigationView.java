package com.example.remotecontrollsystem.ui.view.map;


import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.utils.RosMath;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

public class NavigationView extends androidx.appcompat.widget.AppCompatImageView {

    private static final int DEFAULT_ICON_SIZE = 20;

    float resolution = 0.05f;
    double originX = 0, originY = 0, originZ = 0, originW = 0;

    private MessagePublisher<GetMap_Response> mapPublisher;
    private MessagePublisher<Pose> robotPosePublisher;

    public NavigationView(@NonNull Context context) {
        super(context);
        init();
    }

    public NavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mapPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.GET_MAP.getType() + Mqtt.RESPONSE);
        robotPosePublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.ROBOT_POSE.getType());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setImageResource(R.drawable.icon_navigation);
        getLayoutParams().width = DEFAULT_ICON_SIZE;
        getLayoutParams().height = DEFAULT_ICON_SIZE;


        mapPublisher.attach(mapObserver);
        robotPosePublisher.attach(robotPoseObserver);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        updateNavigationPosition(-100, -100, 1, 0);
    }

    private final Observer<GetMap_Response> mapObserver = new Observer<GetMap_Response>() {
        @Override
        public void update(GetMap_Response response) {
            if (response != null) {
                resolution = response.getMap().getInfo().getResolution();
                originX = response.getMap().getInfo().getOrigin().getPosition().getX();
                originY = response.getMap().getInfo().getOrigin().getPosition().getY();
                originZ = response.getMap().getInfo().getOrigin().getOrientation().getZ();
                originW = response.getMap().getInfo().getOrigin().getOrientation().getW();
            }
        }
    };

    private final Observer<Pose> robotPoseObserver = new Observer<Pose>() {
        @Override
        public void update(Pose pose) {
            double poseX = pose.getPosition().getX();
            double poseY = pose.getPosition().getY();
            double poseZ = pose.getOrientation().getZ();
            double poseW = pose.getOrientation().getW();

            updateNavigationPosition(poseX, poseY, poseZ, poseW);
        }
    };

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

        mapPublisher.detach(mapObserver);
        robotPosePublisher.detach(robotPoseObserver);
    }
}
