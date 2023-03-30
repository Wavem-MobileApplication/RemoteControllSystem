package com.example.remotecontrollsystem.mqtt.msgs;

public class LaserScan {
    Header header;
    float angle_min;
    float angle_max;
    float angle_increment;
    float time_increment;
    float scan_time;
    float range_min;
    float range_max;
    float[] ranges;
    float[] intensities;

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

    public float[] getRanges() {
        return ranges;
    }

    public void setRanges(float[] ranges) {
        this.ranges = ranges;
    }

    public float[] getIntensities() {
        return intensities;
    }

    public void setIntensities(float[] intensities) {
        this.intensities = intensities;
    }
}
