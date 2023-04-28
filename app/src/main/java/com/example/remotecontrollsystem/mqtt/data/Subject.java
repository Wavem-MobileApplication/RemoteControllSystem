package com.example.remotecontrollsystem.mqtt.data;

import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;

public interface Subject<T extends RosMessage> {
    void attach(Observer<T> observer);
    void detach(Observer<T> observer);
    void postValue(T message);
}
