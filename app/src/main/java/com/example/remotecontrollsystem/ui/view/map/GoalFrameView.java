package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.model.viewmodel.WaypointViewModel;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.utils.RosMath;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;


public class GoalFrameView extends FrameLayout {
    private static final String TAG = GoalFrameView.class.getSimpleName();
    private AppCompatActivity activity;
    private RouteViewModel routeViewModel;
    private WaypointViewModel waypointViewModel;
    private MessagePublisher mapResponse;

    private float resolution = 0.05f;
    private boolean isLongClicked = false;

    private Route curRoute;
    private Waypoint curWaypoint;

    private Map<Waypoint, GoalView> goalViewMap;
    private Map<Pose, PoseView> poseViewMap;

    public GoalFrameView(@NonNull Context context, AppCompatActivity activity) {
        super(context);

        this.activity = activity;

        routeViewModel = new ViewModelProvider(activity).get(RouteViewModel.class);
        waypointViewModel = new ViewModelProvider(activity).get(WaypointViewModel.class);
        mapResponse = Mqtt.getInstance().getMessagePublisher(WidgetType.GET_MAP.getType() + Mqtt.RESPONSE);

        curRoute = new Route();
        curWaypoint = new Waypoint();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setClickable(false);

        routeViewModel.getCurrentRoute().observe(activity, currentRouteObserver);
        waypointViewModel.getNewWaypoint().observe(activity, newWaypointObserver);
        mapResponse.attach(mapObserver);
    }

    private Observer<Route> currentRouteObserver = new Observer<Route>() {
        @Override
        public void onChanged(Route route) {
            curRoute = route;
            updateFlags();
        }
    };

    private Observer<Waypoint> newWaypointObserver = new Observer<Waypoint>() {
        @Override
        public void onChanged(Waypoint waypoint) {
            curWaypoint = waypoint;
            updateFlags();
        }
    };

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
                    waypointViewModel.removePoseToNewWaypoint(pose);
                });
            });
        }
    }

    private final com.example.remotecontrollsystem.mqtt.data.Observer mapObserver = message -> {
        GetMap_Response response = new Gson().fromJson(message, GetMap_Response.class);

        resolution = response.getMap().getInfo().getResolution();
        updateFlags();
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        routeViewModel.getCurrentRoute().removeObserver(currentRouteObserver);
        mapResponse.detach(mapObserver);
    }
}
