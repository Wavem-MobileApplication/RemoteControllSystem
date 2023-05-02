package com.example.remotecontrollsystem.mqtt.msgs;

import com.google.gson.Gson;

public class NavigateToPose_Request extends RosMessage {
    private PoseStamped pose;
    private String behavior_tree;

    public NavigateToPose_Request() {
        pose = new PoseStamped();
        behavior_tree = "";
    }

    public PoseStamped getPose() {
        return pose;
    }

    public void setPose(PoseStamped pose) {
        this.pose = pose;
    }

    public String getBehavior_tree() {
        return behavior_tree;
    }

    public void setBehavior_tree(String behavior_tree) {
        this.behavior_tree = behavior_tree;
    }

    @Override
    public RosMessage fromJson(String json) {
        return new Gson().fromJson(json, NavigateToPose_Request.class);
    }
}
