package com.example.remotecontrollsystem.mqtt.msgs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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

    public static GetMap_Response fromJson(String jsonData) {
        GetMap_Response response = new GetMap_Response();
        Gson gson = new Gson();

        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
        OccupancyGrid map = OccupancyGrid.fromJson(jsonObject.get("map").toString());

        response.setMap(map);

        return response;
    }
}
