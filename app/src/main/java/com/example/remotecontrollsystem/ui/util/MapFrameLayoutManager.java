package com.example.remotecontrollsystem.ui.util;

import android.util.Log;

import androidx.lifecycle.Observer;

import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.data.RosObserver;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.LaserScan;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.TFMessage;
import com.example.remotecontrollsystem.ui.view.map.MapFrameLayout;
import com.google.gson.Gson;

public class MapFrameLayoutManager {
    private final MapFrameLayout mapFrameLayout;

    public MapFrameLayoutManager(MapFrameLayout mapFrameLayout) {
        this.mapFrameLayout = mapFrameLayout;
    }

    // ROS-Mqtt Observers
    public final RosObserver<GetMap_Response> mapObserver = new RosObserver<GetMap_Response>(GetMap_Response.class) {
        @Override
        public void onChange(GetMap_Response getMap_response) {
            mapFrameLayout.updateMap(getMap_response.getMap());
        }
    };

    public final RosObserver<Pose> robotPoseObserver = new RosObserver<Pose>(Pose.class) {
        @Override
        public void onChange(Pose pose) {
            mapFrameLayout.updateRobotPose(pose);
        }
    };

    public final RosObserver<LaserScan> scanObserver = new RosObserver<LaserScan>(LaserScan.class) {
        @Override
        public void onChange(LaserScan laserScan) {
            mapFrameLayout.updateLaserScan(laserScan);
        }
    };

    public final RosObserver<TFMessage> tfObserver = new RosObserver<TFMessage>(TFMessage.class) {
        @Override
        public void onChange(TFMessage tfMessage) {
            mapFrameLayout.updateTF(tfMessage);
        }
    };

    // Route Observers
    public final Observer<Route> currentRouteObserver = new Observer<Route>() {
        @Override
        public void onChanged(Route route) {
            mapFrameLayout.updateCurrentRoute(route);
        }
    };

    public final Observer<Waypoint> newWaypointObserver = new Observer<Waypoint>() {
        @Override
        public void onChanged(Waypoint waypoint) {
            mapFrameLayout.updateNewWaypoint(waypoint);
        }
    };
}
