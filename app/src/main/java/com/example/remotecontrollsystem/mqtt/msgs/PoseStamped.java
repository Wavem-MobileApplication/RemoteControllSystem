package com.example.remotecontrollsystem.mqtt.msgs;

public class PoseStamped extends RosMessage {
    private Header header;
    private Pose pose;

    public PoseStamped() {
        header = new Header();
        pose = new Pose();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Pose getPose() {
        return pose;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }
}
