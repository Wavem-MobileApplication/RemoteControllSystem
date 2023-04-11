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
    private double[] ranges;
    private double[] intensities;

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

    public double[] getRanges() {
        return ranges;
    }

    public void setRanges(double[] ranges) {
        this.ranges = ranges;
    }

    public double[] getIntensities() {
        return intensities;
    }

    public void setIntensities(double[] intensities) {
        this.intensities = intensities;
    }

    public static LaserScan fromJson(String jsonData) {
        LaserScan laserScan = new LaserScan();
        Gson gson = new Gson();

        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
        Header header = gson.fromJson(jsonObject.get("header"), Header.class);

        Map<String, Double> rangesMap = gson.fromJson(jsonObject.get("ranges"), Map.class);
        double[] rangesArr = new double[rangesMap.size()]; // 배열의 크기를 지정
        int i = 0;
        for (Double value : rangesMap.values()) {
            rangesArr[i++] = (value != null) ? value : Double.NaN; // null값이면 Double.NaN으로 대체
        }

        laserScan.setHeader(header);
        laserScan.setAngle_min(jsonObject.get("angle_min").getAsFloat());
        laserScan.setAngle_max(jsonObject.get("angle_max").getAsFloat());
        laserScan.setAngle_increment(jsonObject.get("angle_increment").getAsFloat());
        laserScan.setTime_increment(jsonObject.get("time_increment").getAsFloat());
        laserScan.setScan_time(jsonObject.get("scan_time").getAsFloat());
        laserScan.setRange_min(jsonObject.get("range_min").getAsFloat());
        laserScan.setRange_max(jsonObject.get("range_max").getAsFloat());
        laserScan.setRanges(rangesArr);

        return laserScan;
    }
}
