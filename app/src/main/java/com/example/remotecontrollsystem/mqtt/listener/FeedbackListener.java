package com.example.remotecontrollsystem.mqtt.listener;

public interface FeedbackListener {
    void onReceive(String message);
}
