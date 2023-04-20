package com.example.remotecontrollsystem.mqtt.msgs;

public class Transform {
    private Vector3 translation;
    private Quaternion rotation;

    public Transform() {
        translation = new Vector3();
        rotation = new Quaternion();
    }

    public Vector3 getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3 translation) {
        this.translation = translation;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }
}
