package com.example.remotecontrollsystem.mqtt.data;

public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void postValue(String message);
}
