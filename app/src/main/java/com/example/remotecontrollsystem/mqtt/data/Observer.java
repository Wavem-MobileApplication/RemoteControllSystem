package com.example.remotecontrollsystem.mqtt.data;

public interface Observer {
    void update(String message);
}
