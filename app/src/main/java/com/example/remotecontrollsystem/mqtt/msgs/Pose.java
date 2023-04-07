package com.example.remotecontrollsystem.mqtt.msgs;

public class Pose {
    Point position;
    Quaternion orientation;

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
}