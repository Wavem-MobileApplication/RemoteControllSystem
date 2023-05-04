package com.example.remotecontrollsystem.mqtt.data;

import android.util.Log;

import androidx.lifecycle.Observer;

import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;

public abstract class RosObserver<T extends RosMessage> implements Observer<RosMessage> {
    private final Class<T> messageType;

    public RosObserver(Class<T> messageType) {
        this.messageType = messageType;
    }

    @Override
    public void onChanged(RosMessage rosMessage) {
        if (messageType.isInstance(rosMessage)) {
            onChange(messageType.cast(rosMessage));
        } else {
            Log.e("CastError", rosMessage.toJson(rosMessage) + " -> " + messageType.getSimpleName());
        }
    }

    public abstract void onChange(T message);
}