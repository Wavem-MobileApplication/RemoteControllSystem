package com.example.remotecontrollsystem.mqtt.msgs;

import com.google.gson.Gson;

public class GetMap_Request extends RosMessage {

    @Override
    public RosMessage fromJson(String json) {
        return new Gson().fromJson(json, GetMap_Request.class);
    }
}
