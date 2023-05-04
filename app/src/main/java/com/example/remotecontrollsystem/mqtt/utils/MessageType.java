package com.example.remotecontrollsystem.mqtt.utils;

public enum MessageType {
    REQUEST("/request"),
    FEEDBACK("/feedback"),
    RESPONSE("/response");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
