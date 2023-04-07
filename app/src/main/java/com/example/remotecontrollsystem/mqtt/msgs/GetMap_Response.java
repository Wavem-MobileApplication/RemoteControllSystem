package com.example.remotecontrollsystem.mqtt.msgs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GetMap_Response {
    private OccupancyGrid map;

    public GetMap_Response() {
        map = new OccupancyGrid();
    }

    public OccupancyGrid getMap() {
        return map;
    }

    public void setMap(OccupancyGrid map) {
        this.map = map;
    }

    public void fromJson(String json) {
        Gson gson = new Gson();
        JsonObject jsonMap = gson.fromJson(json, JsonObject.class);
        map.fromJson(jsonMap.getAsJsonObject("map"));
    }
}
