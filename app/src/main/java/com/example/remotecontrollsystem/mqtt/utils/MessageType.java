package com.example.remotecontrollsystem.mqtt.utils;

public enum MessageType {
    REQUEST("/request"),
    RESPONSE("/response"),
    FEEDBACK("/feedback");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
