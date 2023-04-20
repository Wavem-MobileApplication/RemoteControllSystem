package com.example.remotecontrollsystem.mqtt.msgs;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LaserScan {
    private Header header;
    private float angle_min;
    private float angle_max;
    private float angle_increment;
    private float time_increment;
    private float scan_time;
    private float range_min;
    private float range_max;
    private Map<String, Float> ranges;
    private Map<String, Float> intensities;

    public LaserScan() {
        this.header = new Header();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public float getAngle_min() {
        return angle_min;
    }

    public void setAngle_min(float angle_min) {
        this.angle_min = angle_min;
    }

    public float getAngle_max() {
        return angle_max;
    }

    public void setAngle_max(float angle_max) {
        this.angle_max = angle_max;
    }

    public float getAngle_increment() {
        return angle_increment;
    }

    public void setAngle_increment(float angle_increment) {
        this.angle_increment = angle_increment;
    }

    public float getTime_increment() {
        return time_increment;
    }

    public void setTime_increment(float time_increment) {
        this.time_increment = time_increment;
    }

    public float getScan_time() {
        return scan_time;
    }

    public void setScan_time(float scan_time) {
        this.scan_time = scan_time;
    }

    public float getRange_min() {
        return range_min;
    }

    public void setRange_min(float range_min) {
        this.range_min = range_min;
    }

    public float getRange_max() {
        return range_max;
    }

    public void setRange_max(float range_max) {
        this.range_max = range_max;
    }

    public Map<String, Float> getRanges() {
        return ranges;
    }

    public void setRanges(Map<String, Float> ranges) {
        this.ranges = ranges;
    }

    public Map<String, Float> getIntensities() {
        return intensities;
    }

    public void setIntensities(Map<String, Float> intensities) {
        this.intensities = intensities;
    }
}
