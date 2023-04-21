package com.example.remotecontrollsystem.mqtt.msgs;

public class Quaternion {
    double x;
    double y;
    double z;
    double w;

    public double getX() {
        return Math.round(x * 10000.0) / 10000.0;
    }

    public void setX(double x) {
        this.x = Math.round(x * 10000.0) / 10000.0;
    }

    public double getY() {
        return Math.round(y * 10000.0) / 10000.0;
    }

    public void setY(double y) {
        this.y = Math.round(y * 10000.0) / 10000.0;
    }

    public double getZ() {
        return Math.round(z * 10000.0) / 10000.0;
    }

    public void setZ(double z) {
        this.z = Math.round(z * 10000.0) / 10000.0;
    }

    public double getW() {
        return Math.round(w * 10000.0) / 10000.0;
    }

    public void setW(double w) {
        this.w = Math.round(w * 10000.0) / 10000.0;
    }
}
