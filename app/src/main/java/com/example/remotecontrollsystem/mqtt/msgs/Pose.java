package com.example.remotecontrollsystem.mqtt.msgs;

import com.google.gson.Gson;

public class Pose extends RosMessage {
    private Point position;
    private Quaternion orientation;

    public Pose() {
        this.position = new Point();
        this.orientation = new Quaternion();
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Quaternion getOrientation() {
        return orientation;
    }

    public void setOrientation(Quaternion orientation) {
        this.orientation = orientation;
    }

    @Override
    public RosMessage fromJson(String json) {
        return new Gson().fromJson(json, Pose.class);
    }
}
