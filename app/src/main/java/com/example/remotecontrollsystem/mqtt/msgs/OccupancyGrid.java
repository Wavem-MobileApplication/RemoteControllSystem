package com.example.remotecontrollsystem.mqtt.msgs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

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

    public static OccupancyGrid fromJson(String jsonData) {
        OccupancyGrid occupancyGrid = new OccupancyGrid();
        Gson gson = new Gson();

        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
        Header header = gson.fromJson(jsonObject.get("header"), Header.class);
        MapMetaData info = gson.fromJson(jsonObject.get("info"), MapMetaData.class);

        JsonObject dataJson = jsonObject.getAsJsonObject("data");
        int[] data = new int[dataJson.size()];

        for (int i = 0; i < data.length; i++) {
            JsonElement element = dataJson.get(Integer.toString(i));
            if (element == null) {
                // Handle missing value
            } else {
                data[i] = element.getAsInt();
            }
        }

        occupancyGrid.setHeader(header);
        occupancyGrid.setInfo(info);
        occupancyGrid.setData(data);

        return occupancyGrid;
    }

    public static String toJson(OccupancyGrid occupancyGrid) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        int size = occupancyGrid.data.length;
        Map<Integer, Integer> dataMap = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            dataMap.put(i, occupancyGrid.data[i]);
        }

        jsonObject.add("header", gson.toJsonTree(occupancyGrid.header));
        jsonObject.add("info", gson.toJsonTree(occupancyGrid.info));
        jsonObject.add("data", gson.toJsonTree(dataMap));

        return jsonObject.toString();
    }
}
