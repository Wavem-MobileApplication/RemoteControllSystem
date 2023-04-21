package com.example.remotecontrollsystem.mqtt.msgs;

import com.example.remotecontrollsystem.mqtt.manager.MessageManager;
import com.google.gson.Gson;

public class RosMessage {
    private static Gson gson;

    public RosMessage() {
        gson = new Gson();
    }

    public String toJson(Object message) {
        return gson.toJson(message);
    }

    public RosMessage fromJson(String json, String widgetType) {
        return gson.fromJson(json, MessageManager.getMessageClassFromWidgetType(widgetType));
    }
}
