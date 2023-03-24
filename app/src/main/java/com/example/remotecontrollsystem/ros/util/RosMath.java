package com.example.remotecontrollsystem.ros.util;

import geometry_msgs.msg.Quaternion;

public class RosMath {
    public static float QuaternionToAngular(Quaternion quaternion) {
        float zPoint = (float) quaternion.getZ();
        float wPoint = (float) quaternion.getW();
        double siny_cosp = 2 * (wPoint * zPoint);
        double cosy_cosp = 1 - 2 * (wPoint * wPoint);
        float yaw = (float) Math.atan2(siny_cosp, cosy_cosp);
        float yawInt = (float) Math.toDegrees(yaw);
        return -yawInt;
    }

    public static float QuaternionToAngular(double z, double w) {
        double siny_cosp = 2 * (w * z);
        double cosy_cosp = 1 - 2 * (w * z);
        float yaw = (float) Math.atan2(siny_cosp, cosy_cosp);
        float yawInt = (float) Math.toDegrees(yaw);
        return -yawInt;
    }

    public static float QuaternionToRadian(Quaternion quaternion) {
        float zPoint = (float) quaternion.getZ();
        float wPoint = (float) quaternion.getW();
        double siny_cosp = 2 * (wPoint * zPoint);
        double cosy_cosp = 1 - 2 * (wPoint * wPoint);

        return (float) Math.atan2(siny_cosp, cosy_cosp);
    }

    public static float[] AngularToQuaternion(double angle) {
        double radian = Math.PI / 180 * angle;
        float z = (float) Math.sin(radian / 2);
        float w = (float) Math.cos(radian / 2);

        return new float[]{z, w};
    }

    public static double[] RadianToQuaternion(double radian) {
        double z = Math.sin(radian / 2);
        double w = Math.cos(radian / 2);

        return new double[]{z, w};
    }
}
