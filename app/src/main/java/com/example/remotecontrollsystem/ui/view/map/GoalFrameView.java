package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.msgs.MapMetaData;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.utils.RosMath;

import java.util.List;


public class GoalFrameView extends FrameLayout {
    private static final String TAG = GoalFrameView.class.getSimpleName();

    private OnPoseViewClickListener poseViewClickListener;
    private float resolution = 0.05f;
    private Route curRoute;
    private Waypoint curWaypoint;

    public GoalFrameView(@NonNull Context context) {
        super(context);

        curRoute = new Route();
        curWaypoint = new Waypoint();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setClickable(false);
    }

    private void updateFlags() {
        post(this::removeAllViews); // Post
        List<Waypoint> waypointList = curRoute.getWaypointList();

        for (Waypoint waypoint : waypointList) {
            int lastIndex = waypoint.getPoseList().size() - 1;

            Pose pose = waypoint.getPoseList().get(lastIndex);

            GoalView goalView = new GoalView(getContext());
            goalView.setX((float) (pose.getPosition().getX() / resolution));
            goalView.setY((float) (pose.getPosition().getY() / resolution));

            post(() -> addView(goalView)); // Post
        }

        List<Pose> poseList = curWaypoint.getPoseList();

        for (Pose pose : poseList) {
            PoseView poseView = new PoseView(getContext());
            poseView.setX((float) (pose.getPosition().getX() / resolution));
            poseView.setY((float) (pose.getPosition().getY() / resolution));
            poseView.setRotation(RosMath.QuaternionToAngular(pose.getOrientation()));

            post(() -> { // Post
                addView(poseView);
                poseView.setOnClickListener(view -> {
                    if (poseViewClickListener != null) {
                        poseViewClickListener.onClick(pose);
                    }
//                    waypointViewModel.removePoseToNewWaypoint(pose);
                });
            });
        }
    }

    public void updateCurrentRoute(Route route) {
        curRoute = route;
        updateFlags();
    }

    public void updateNewWaypoint(Waypoint waypoint) {
        curWaypoint = waypoint;
        updateFlags();
    }

    public void updateMapMetaData(MapMetaData mapMetaData) {
        resolution = mapMetaData.getResolution();
        updateFlags();
    }

    public void setPoseViewClickListener(OnPoseViewClickListener poseViewClickListener) {
        this.poseViewClickListener = poseViewClickListener;
    }

    public interface OnPoseViewClickListener {
        void onClick(Pose pose);
    }
}
