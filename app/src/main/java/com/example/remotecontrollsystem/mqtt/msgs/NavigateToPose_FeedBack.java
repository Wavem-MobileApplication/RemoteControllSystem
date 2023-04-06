package com.example.remotecontrollsystem.mqtt.msgs;

public class NavigateToPose_FeedBack {
    PoseStamped current_pose;
    Duration navigation_time;
    int number_of_recoveries;
    float distance_remaining;

    public NavigateToPose_FeedBack() {
        current_pose = new PoseStamped();
        navigation_time = new Duration();
    }

    public PoseStamped getCurrent_pose() {
        return current_pose;
    }

    public void setCurrent_pose(PoseStamped current_pose) {
        this.current_pose = current_pose;
    }

    public Duration getNavigation_time() {
        return navigation_time;
    }

    public void setNavigation_time(Duration navigation_time) {
        this.navigation_time = navigation_time;
    }

    public int getNumber_of_recoveries() {
        return number_of_recoveries;
    }

    public void setNumber_of_recoveries(int number_of_recoveries) {
        this.number_of_recoveries = number_of_recoveries;
    }

    public float getDistance_remaining() {
        return distance_remaining;
    }

    public void setDistance_remaining(float distance_remaining) {
        this.distance_remaining = distance_remaining;
    }
}
