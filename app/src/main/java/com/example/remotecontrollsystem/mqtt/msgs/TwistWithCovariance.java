package com.example.remotecontrollsystem.mqtt.msgs;

public class TwistWithCovariance {
    Twist twist;
    double[] covariance;

    public TwistWithCovariance() {
        this.twist = new Twist();
    }

    public Twist getTwist() {
        return twist;
    }

    public void setTwist(Twist twist) {
        this.twist = twist;
    }

    public double[] getCovariance() {
        return covariance;
    }

    public void setCovariance(double[] covariance) {
        this.covariance = covariance;
    }
}
