package com.example.remotecontrollsystem.mqtt.data;

import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessagePublisher<T extends RosMessage> implements Subject<T> {
    private static final String TAG = MessagePublisher.class.getSimpleName();
    private static final String DEFAULT_DATA = "Default";
    private final CopyOnWriteArrayList<Observer<T>> observerList = new CopyOnWriteArrayList<>();
    private T oldData;

    @Override
    public synchronized void attach(Observer<T> observer) {
        if (oldData != null) {
            observer.update(oldData);
        }
        observerList.add(observer);
    }

    @Override
    public synchronized void detach(Observer<T> observer) {
        observerList.remove(observer);
    }

    @Override
    public void postValue(T message) {
        oldData = message;

        for (Observer<T> observer : observerList) {
            observer.update(message);
        }
    }
}
