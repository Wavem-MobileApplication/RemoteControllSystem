package com.example.remotecontrollsystem.ui.util;

import androidx.lifecycle.Observer;

import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.data.RosObserver;
import com.example.remotecontrollsystem.mqtt.msgs.LaserScan;
import com.example.remotecontrollsystem.mqtt.msgs.OccupancyGrid;
import com.example.remotecontrollsystem.mqtt.msgs.Path;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.TFMessage;
import com.example.remotecontrollsystem.ui.view.map.MapFrameLayout;

public class MapFrameLayoutManager {
    private final MapFrameLayout mapFrameLayout;

    public MapFrameLayoutManager(MapFrameLayout mapFrameLayout) {
        this.mapFrameLayout = mapFrameLayout;
    }

    // ROS-Mqtt Observers
    public final RosObserver<OccupancyGrid> mapObserver = new RosObserver<>(OccupancyGrid.class) {
        @Override
        public void onChange(OccupancyGrid occupancyGrid) {
            mapFrameLayout.updateMap(occupancyGrid);
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

    public final RosObserver<Path> globalPlanObserver = new RosObserver<Path>(Path.class) {
        @Override
        public void onChange(Path path) {
            mapFrameLayout.updateGlobalPath(path);
        }
    };

    public final RosObserver<Path> localPlanObserver = new RosObserver<Path>(Path.class) {
        @Override
        public void onChange(Path path) {
            mapFrameLayout.updateLocalPath(path);
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
