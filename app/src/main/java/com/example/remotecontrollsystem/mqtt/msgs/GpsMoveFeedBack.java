package com.example.remotecontrollsystem.mqtt.msgs;

public class GpsMoveFeedBack extends RosMessage {
    private Header header;
    private int waypoint_count;
    private int count;
    private float start_distance;
    private float distance_remaining;
    private float driving_process;

    public GpsMoveFeedBack() {
        header = new Header();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public int getWaypoint_count() {
        return waypoint_count;
    }

    public void setWaypoint_count(int waypoint_count) {
        this.waypoint_count = waypoint_count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getStart_distance() {
        return start_distance;
    }

    public void setStart_distance(float start_distance) {
        this.start_distance = start_distance;
    }

    public float getDistance_remaining() {
        return distance_remaining;
    }

    public void setDistance_remaining(float distance_remaining) {
        this.distance_remaining = distance_remaining;
    }

    public float getDriving_process() {
        return driving_process;
    }

    public void setDriving_process(float driving_process) {
        this.driving_process = driving_process;
    }
}
