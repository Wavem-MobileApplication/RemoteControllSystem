package com.example.remotecontrollsystem.mqtt.msgs;

public class TransformStamped {
    private Header header;
    private String child_frame_id;
    private Transform transform;

    public TransformStamped() {
        header = new Header();
        child_frame_id = "";
        transform = new Transform();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getChild_frameId() {
        return child_frame_id;
    }

    public void setChild_frameId(String child_frameId) {
        this.child_frame_id = child_frameId;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }
}
