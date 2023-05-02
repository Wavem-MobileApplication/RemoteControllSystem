package com.example.remotecontrollsystem.mqtt.msgs;

import com.example.remotecontrollsystem.mqtt.manager.MessageManager;
import com.google.gson.Gson;

public class RosMessage {

    public String toJson(Object message) {
        return new Gson().toJson(message);
    }

    public RosMessage fromJson(String json, String widgetType) {
/*        if (widgetType.equals(WidgetType.LASER_SCAN.getType())) {
            Log.d("LaserScan", json);
        }*/
        return new Gson().fromJson(json, MessageManager.getMessageClassFromWidgetType(widgetType));
    }
}
