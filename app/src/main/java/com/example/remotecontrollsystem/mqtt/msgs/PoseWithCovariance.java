package com.example.remotecontrollsystem.mqtt.msgs;

public class PoseWithCovariance {
    Pose pose;
    double[] covariance;

    public PoseWithCovariance() {
        this.pose = new Pose();
    }

    public Pose getPose() {
        return pose;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }

    public double[] getCovariance() {
        return covariance;
    }

    public void setCovariance(double[] covariance) {
        this.covariance = covariance;
    }
}
