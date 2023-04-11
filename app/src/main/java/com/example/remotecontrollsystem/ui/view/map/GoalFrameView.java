package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

import java.util.List;


public class GoalFrameView extends FrameLayout {
    private static final String TAG = GoalFrameView.class.getSimpleName();
    private AppCompatActivity activity;
    private RouteViewModel routeViewModel;
    private MessagePublisher mapResponse;

    private Handler handler;
    private Route curRoute;
    private float resolution = 0.05f;

    public GoalFrameView(@NonNull Context context, AppCompatActivity activity) {
        super(context);

        this.activity = activity;

        routeViewModel = new ViewModelProvider(activity).get(RouteViewModel.class);
        mapResponse = Mqtt.getInstance().getMessagePublisher(WidgetType.GET_MAP.getType() + Mqtt.RESPONSE);
        handler = new Handler(Looper.getMainLooper());

        setClickable(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        routeViewModel.getCurrentRoute().observe(activity, currentRouteObserver);
        mapResponse.attach(mapResponseObserver);
    }

    private Observer<Route> currentRouteObserver = new Observer<Route>() {
        @Override
        public void onChanged(Route route) {
            curRoute = route;
            updateGoalFlags(route);
        }
    };

    private void updateGoalFlags(Route route) {
        handler.post(() -> {
            removeAllViews();

            List<Waypoint> waypointList = route.getWaypointList();

            for (Waypoint waypoint : waypointList) {
                int lastIndex = waypoint.getPoseList().size() - 1;
                Pose pose = waypoint.getPoseList().get(lastIndex);

                GoalView goalView = new GoalView(getContext());
                goalView.setX((float) (pose.getPosition().getX() / resolution));
                goalView.setY((float) (pose.getPosition().getY() / resolution));
                addView(goalView);
            }
        });
    }

    private final com.example.remotecontrollsystem.mqtt.data.Observer mapResponseObserver = message -> {
        GetMap_Response response = GetMap_Response.fromJson(message);

        resolution = response.getMap().getInfo().getResolution();
        updateGoalFlags(curRoute);
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        routeViewModel.getCurrentRoute().removeObserver(currentRouteObserver);
        mapResponse.detach(mapResponseObserver);
    }
}
