package com.example.remotecontrollsystem.mqtt.msgs;

public class TFMessage extends RosMessage{
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
