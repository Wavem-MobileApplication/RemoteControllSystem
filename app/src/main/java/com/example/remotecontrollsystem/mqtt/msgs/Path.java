package com.example.remotecontrollsystem.mqtt.msgs;

public class Path extends RosMessage{
    private Header header;
    private PoseStamped[] poses;

    public Path() {
        this.header = new Header();
        poses = new PoseStamped[0];
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public PoseStamped[] getPoses() {
        return poses;
    }

    public void setPoses(PoseStamped[] poses) {
        this.poses = poses;
    }
}
