package com.example.remotecontrollsystem.mqtt.msgs;

import java.util.HashMap;
import java.util.Map;

public class NavSatFix extends RosMessage {
    private Header header;
    private NavSatStatus status;
    private double latitude;
    private double longitude;
    private double altitude;
    private Map<String, Double> position_covariance;
    private int position_covariance_type;

    public NavSatFix() {
        header = new Header();
        status = new NavSatStatus();
        position_covariance = new HashMap<>();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public NavSatStatus getStatus() {
        return status;
    }

    public void setStatus(NavSatStatus status) {
        this.status = status;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public Map<String, Double> getPosition_covariance() {
        return position_covariance;
    }

    public void setPosition_covariance(Map<String, Double> position_covariance) {
        this.position_covariance = position_covariance;
    }

    public int getPosition_covariance_type() {
        return position_covariance_type;
    }

    public void setPosition_covariance_type(int position_covariance_type) {
        this.position_covariance_type = position_covariance_type;
    }
}
