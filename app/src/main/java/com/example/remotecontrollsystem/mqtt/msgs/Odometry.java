package com.example.remotecontrollsystem.mqtt.msgs;

public class Odometry {
    Header header;
    String child_frame_id;
    PoseWithCovariance pose;
    TwistWithCovariance twist;

    public Odometry() {
        this.header = new Header();
        this.child_frame_id = "";
        this.pose = new PoseWithCovariance();
        this.twist = new TwistWithCovariance();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getChild_frame_id() {
        return child_frame_id;
    }

    public void setChild_frame_id(String child_frame_id) {
        this.child_frame_id = child_frame_id;
    }

    public PoseWithCovariance getPose() {
        return pose;
    }

    public void setPose(PoseWithCovariance pose) {
        this.pose = pose;
    }

    public TwistWithCovariance getTwist() {
        return twist;
    }

    public void setTwist(TwistWithCovariance twist) {
        this.twist = twist;
    }
}
