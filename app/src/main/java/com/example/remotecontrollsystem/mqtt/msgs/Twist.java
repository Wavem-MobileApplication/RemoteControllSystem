package com.example.remotecontrollsystem.mqtt.msgs;

import com.google.gson.Gson;

public class Twist extends RosMessage {
    Vector3 linear;
    Vector3 angular;

    public Twist() {
        this.linear = new Vector3();
        this.angular = new Vector3();
    }

    public Vector3 getLinear() {
        return linear;
    }

    public void setLinear(Vector3 linear) {
        this.linear = linear;
    }

    public Vector3 getAngular() {
        return angular;
    }

    public void setAngular(Vector3 angular) {
        this.angular = angular;
    }

    @Override
    public RosMessage fromJson(String json) {
        return new Gson().fromJson(json, Twist.class);
    }
}
