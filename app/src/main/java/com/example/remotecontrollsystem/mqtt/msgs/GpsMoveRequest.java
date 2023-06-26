package com.example.remotecontrollsystem.mqtt.msgs;

public class GpsMoveRequest extends RosMessage {
    private PoseStamped[] waypoints_list;

    public GpsMoveRequest(PoseStamped[] waypoints_list) {
        this.waypoints_list = waypoints_list;
    }

    public PoseStamped[] getWaypoints_list() {
        return waypoints_list;
    }

    public void setWaypoints_list(PoseStamped[] waypoints_list) {
        this.waypoints_list = waypoints_list;
    }
}

