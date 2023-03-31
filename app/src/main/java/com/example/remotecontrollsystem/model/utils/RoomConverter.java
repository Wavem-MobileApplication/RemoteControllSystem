package com.example.remotecontrollsystem.model.utils;

import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

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
}
