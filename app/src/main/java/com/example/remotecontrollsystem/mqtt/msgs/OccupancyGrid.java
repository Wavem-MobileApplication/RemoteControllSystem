package com.example.remotecontrollsystem.mqtt.msgs;

import com.google.gson.Gson;

import java.util.Map;

public class OccupancyGrid extends RosMessage {
    Header header;
    MapMetaData info;
    int[] data;

    public OccupancyGrid() {
        header = new Header();
        info = new MapMetaData();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public MapMetaData getInfo() {
        return info;
    }

    public void setInfo(MapMetaData info) {
        this.info = info;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }

    @Override
    public RosMessage fromJson(String json) {
        return new Gson().fromJson(json, OccupancyGrid.class);
    }
}
