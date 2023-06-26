package com.example.remotecontrollsystem.model.entity;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ugv_attr_table")
public class UgvAttribute {
    @PrimaryKey
    private int id;

    private Bitmap image;
    private String name;
    private String mqttAddress;

    private String frontRtspAddress;

    private String rearRtspAddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMqttAddress() {
        return mqttAddress;
    }

    public void setMqttAddress(String mqttAddress) {
        this.mqttAddress = mqttAddress;
    }

    public String getFrontRtspAddress() {
        return frontRtspAddress;
    }

    public void setFrontRtspAddress(String frontRtspAddress) {
        this.frontRtspAddress = frontRtspAddress;
    }

    public String getRearRtspAddress() {
        return rearRtspAddress;
    }

    public void setRearRtspAddress(String rearRtspAddress) {
        this.rearRtspAddress = rearRtspAddress;
    }
}
