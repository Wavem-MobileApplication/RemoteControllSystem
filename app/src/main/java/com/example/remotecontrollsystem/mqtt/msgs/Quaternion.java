package com.example.remotecontrollsystem.mqtt.msgs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Quaternion implements Serializable {
    double x;
    double y;
    double z;
    double w;

    public double getX() {
        return Math.round(x * 10000.0) / 10000.0;
    }

    public void setX(double x) {
        this.x = Math.round(x * 10000.0) / 10000.0;
    }

    public double getY() {
        return Math.round(y * 10000.0) / 10000.0;
    }

    public void setY(double y) {
        this.y = Math.round(y * 10000.0) / 10000.0;
    }

    public double getZ() {
        return Math.round(z * 10000.0) / 10000.0;
    }

    public void setZ(double z) {
        this.z = Math.round(z * 10000.0) / 10000.0;
    }

    public double getW() {
        return Math.round(w * 10000.0) / 10000.0;
    }

    public void setW(double w) {
        this.w = Math.round(w * 10000.0) / 10000.0;
    }

    public static byte[] serialize(Quaternion quaternion) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(quaternion);

        return out.toByteArray();
    }

    public static Quaternion deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream is = new ObjectInputStream(in);
        return (Quaternion) is.readObject();
    }
}
