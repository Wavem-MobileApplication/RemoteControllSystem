package com.example.remotecontrollsystem.mqtt.msgs;

import com.google.gson.Gson;

public class Duration {
    int sec;
    int nanosec;

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public int getNanosec() {
        return nanosec;
    }

    public void setNanosec(int nanosec) {
        this.nanosec = nanosec;
    }

    public static Duration fromJson(String jsonData) {
        return new Gson().fromJson(jsonData, Duration.class);
    }

    public static String toJson(Duration duration) {
        return new Gson().toJson(duration);
    }
}
