package com.example.remotecontrollsystem.mqtt.msgs;

import java.util.HashMap;
import java.util.Map;

public class TFMessage {
    private TransformStamped[] transforms;

    public TFMessage() {
    }

    public TransformStamped[] getTransforms() {
        return transforms;
    }

    public void setTransforms(TransformStamped[] transforms) {
        this.transforms = transforms;
    }
}
