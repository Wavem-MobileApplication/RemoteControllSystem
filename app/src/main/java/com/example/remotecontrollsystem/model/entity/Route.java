package com.example.remotecontrollsystem.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "route_table")
public class Route {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "waypoint_list")
    private List<Waypoint> waypointList;

    public Route() {
        name = "제목 없음";
        waypointList = new ArrayList<>();
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

    public List<Waypoint> getWaypointList() {
        return waypointList;
    }

    public void setWaypointList(List<Waypoint> waypointList) {
        this.waypointList = waypointList;
    }
}
