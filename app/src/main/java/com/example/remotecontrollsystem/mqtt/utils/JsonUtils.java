package com.example.remotecontrollsystem.mqtt.utils;


import com.example.remotecontrollsystem.mqtt.msgs.OccupancyGrid;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {
    public static OccupancyGrid convertJsonToOccupancyGrid(String json) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        JsonElement mapElement = jsonObject.get("map");

        OccupancyGrid occupancyGrid = new OccupancyGrid();

        return occupancyGrid;
    }
}
