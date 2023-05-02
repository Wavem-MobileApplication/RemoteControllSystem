package com.example.remotecontrollsystem.mqtt.msgs;

import com.google.gson.Gson;

public class TFMessage extends RosMessage {
    private TransformStamped[] transforms;

    public TFMessage() {
    }

    public TransformStamped[] getTransforms() {
        return transforms;
    }

    public void setTransforms(TransformStamped[] transforms) {
        this.transforms = transforms;
    }

    @Override
    public RosMessage fromJson(String json) {
        return new Gson().fromJson(json, TFMessage.class);
    }
}
