package com.example.remotecontrollsystem.mqtt.msgs;

public class MapMetaData {
    Time map_load_time;
    float resolution;
    int width;
    int height;
    Pose origin;

    public MapMetaData() {
        map_load_time = new Time();
        origin = new Pose();
    }

    public Time getMap_load_time() {
        return map_load_time;
    }

    public void setMap_load_time(Time map_load_time) {
        this.map_load_time = map_load_time;
    }

    public float getResolution() {
        return resolution;
    }

    public void setResolution(float resolution) {
        this.resolution = resolution;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Pose getOrigin() {
        return origin;
    }

    public void setOrigin(Pose origin) {
        this.origin = origin;
    }
}
