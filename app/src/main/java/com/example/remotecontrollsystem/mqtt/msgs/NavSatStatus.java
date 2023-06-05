package com.example.remotecontrollsystem.mqtt.msgs;

public class NavSatStatus extends RosMessage {
    int status;
    int service;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }
}
