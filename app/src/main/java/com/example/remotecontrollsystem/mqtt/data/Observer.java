package com.example.remotecontrollsystem.mqtt.data;

import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;

public interface Observer<T extends RosMessage> {
    void update(T message);
}
