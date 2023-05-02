package com.example.remotecontrollsystem.mqtt.msgs;

import com.google.gson.Gson;

public class GetMap_Response extends RosMessage {
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

    @Override
    public RosMessage fromJson(String json) {
        return new Gson().fromJson(json, GetMap_Response.class);
    }
}
