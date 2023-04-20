package com.example.remotecontrollsystem.mqtt.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessagePublisher implements Subject{
    private static final String TAG = MessagePublisher.class.getSimpleName();
    private static final String DEFAULT_DATA = "Default";
    private final CopyOnWriteArrayList<Observer> observerList = new CopyOnWriteArrayList<>();
    private String oldData = DEFAULT_DATA;

    @Override
    public synchronized void attach(Observer observer) {
        if (!oldData.equals(DEFAULT_DATA)) {
            observer.update(oldData);
        }
        observerList.add(observer);
    }

    @Override
    public synchronized void detach(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void postValue(String message) {
        oldData = message;

        for (Observer observer : observerList) {
            observer.update(message);
        }
    }
}
