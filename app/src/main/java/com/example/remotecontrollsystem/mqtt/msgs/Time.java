package com.example.remotecontrollsystem.mqtt.msgs;

public class Time {
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
}
