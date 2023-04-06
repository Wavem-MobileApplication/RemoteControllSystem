package com.example.remotecontrollsystem.mqtt.msgs;

public class OccupancyGrid {
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
}
