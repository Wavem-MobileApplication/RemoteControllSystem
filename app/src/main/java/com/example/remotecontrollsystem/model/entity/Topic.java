package com.example.remotecontrollsystem.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;

@Entity(tableName = "topic_table")
public class Topic {
    @PrimaryKey
    @NonNull
    private String funcName;
    @ColumnInfo(name = "message")
    private RosMessageDefinition message;

    public Topic() {
    }

    @Ignore
    public Topic(String funcName) {
        this.funcName = funcName;
    }

    @Ignore
    public Topic(String funcName, RosMessageDefinition message) {
        this.funcName = funcName;
        this.message = message;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public RosMessageDefinition getMessage() {
        return message;
    }

    public void setMessage(RosMessageDefinition message) {
        this.message = message;
    }
}
