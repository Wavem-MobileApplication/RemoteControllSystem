package com.example.remotecontrollsystem.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.remotecontrollsystem.mqtt.msgs.Pose;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "waypoint_table")
public class Waypoint {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "pose")
    private List<Pose> poseList;

    public Waypoint() {
        name = "제목 없음";
        poseList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pose> getPoseList() {
        return poseList;
    }

    public void setPoseList(List<Pose> poseList) {
        this.poseList = poseList;
    }
}
