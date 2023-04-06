package com.example.remotecontrollsystem.model.utils;

import androidx.room.ProvidedTypeConverter;
import androidx.room.RawQuery;
import androidx.room.TypeConverter;

import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

@ProvidedTypeConverter
public class RoomConverter {

    @TypeConverter
    public static RosMessageDefinition fromJsonToRosMessageDefinition(String json) {
        RosMessageDefinition result;
        Type type = new TypeToken<RosMessageDefinition>() {}.getType();
        result = new Gson().fromJson(json, type);
        return result;
    }

    @TypeConverter
    public static String fromRosMessageDefinitionToJson(RosMessageDefinition rosMessageDefinition) {
        return new Gson().toJson(rosMessageDefinition);
    }

    @TypeConverter
    public static List<Pose> fromJsonToPose(String json) {
        List<Pose> result;
        Type type = new TypeToken<List<Pose>>() {}.getType();
        result = new Gson().fromJson(json, type);
        return result;
    }

    @TypeConverter
    public static String fromPoseToJson(List<Pose> poseList) {
        return new Gson().toJson(poseList);
    }

    @TypeConverter
    public static List<Waypoint> fromJsonToWaypointList(String json) {
        List<Waypoint> result;
        Type type = new TypeToken<List<Waypoint>>() {}.getType();
        result = new Gson().fromJson(json, type);
        return result;
    }

    @TypeConverter
    public static String fromWaypointListToJson(List<Waypoint> waypointList) {
        return new Gson().toJson(waypointList);
    }
}
