package com.example.remotecontrollsystem.mqtt.msgs;

import android.icu.text.IDNA;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    public void fromJson(JsonObject json) {
        header = new Gson().fromJson(json.getAsJsonObject("header").toString(), Header.class);
        info = new Gson().fromJson(json.getAsJsonObject("info").toString(), MapMetaData.class);

        JsonObject dataJson = json.getAsJsonObject("data");
        data = new int[dataJson.size()];

        for (int i = 0; i < data.length; i++) {
            JsonElement element = dataJson.get(Integer.toString(i));
            if (element == null) {
                // Handle missing value
            } else {
                data[i] = element.getAsInt();
            }
        }
    }
}
