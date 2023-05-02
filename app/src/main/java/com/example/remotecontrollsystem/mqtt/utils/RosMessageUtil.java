package com.example.remotecontrollsystem.mqtt.utils;

import com.example.remotecontrollsystem.mqtt.manager.MessageManager;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.google.gson.Gson;

public class RosMessageUtil {
    public static RosMessage parseJsonToRosMessage(String json, String widgetName) {
        return new Gson().fromJson(json, MessageManager.getMessageClassFromWidgetType(widgetName));
    }
}
