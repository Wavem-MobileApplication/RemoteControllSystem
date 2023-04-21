package com.example.remotecontrollsystem.mqtt.msgs;

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

}
