package com.example.remotecontrollsystem.mqtt.msgs;

import java.util.Map;

public class TwistWithCovariance {
    Twist twist;
    Map<String, Double> covariance;

    public TwistWithCovariance() {
        this.twist = new Twist();
    }

    public Twist getTwist() {
        return twist;
    }

    public void setTwist(Twist twist) {
        this.twist = twist;
    }

    public Map<String, Double> getCovariance() {
        return covariance;
    }

    public void setCovariance(Map<String, Double> covariance) {
        this.covariance = covariance;
    }
}
