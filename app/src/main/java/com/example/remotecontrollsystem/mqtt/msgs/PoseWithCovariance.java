package com.example.remotecontrollsystem.mqtt.msgs;

import java.util.Map;

public class PoseWithCovariance {
    Pose pose;
    Map<String, Double> covariance;

    public PoseWithCovariance() {
        this.pose = new Pose();
    }

    public Pose getPose() {
        return pose;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }

    public Map<String, Double> getCovariance() {
        return covariance;
    }

    public void setCovariance(Map<String, Double> covariance) {
        this.covariance = covariance;
    }
}
