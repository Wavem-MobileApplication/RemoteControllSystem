package com.example.remotecontrollsystem.mqtt.msgs;

public class Header {
    Time stamp;
    String frame_id;

    public Header() {
        this.stamp = new Time();
        this.frame_id = "";
    }

    public Header(Time stamp, String frame_id) {
        this.stamp = stamp;
        this.frame_id = frame_id;
    }

    public Time getStamp() {
        return stamp;
    }

    public void setStamp(Time stamp) {
        this.stamp = stamp;
    }

    public String getFrame_id() {
        return frame_id;
    }

    public void setFrame_id(String frame_id) {
        this.frame_id = frame_id;
    }
}
