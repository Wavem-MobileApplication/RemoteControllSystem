package com.example.remotecontrollsystem.mqtt.data;

import java.util.ArrayList;
import java.util.List;

public class MessagePublisher implements Subject{
    private static final String TAG = MessagePublisher.class.getSimpleName();
    private static final String DEFAULT_DATA = "Default";
    private final List<Observer> observerList = new ArrayList<>();
    private String oldData = DEFAULT_DATA;

    @Override
    public void attach(Observer observer) {
        if (!oldData.equals(DEFAULT_DATA)) {
            observer.update(oldData);
        }
        observerList.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void postValue(String message) {
        oldData = message;

/*        for (int i = 0; i < observerList.size(); i++) {
            observerList.get(i).update(message);
        }*/
        for (Observer observer : observerList) {
            observer.update(message);
        }
    }
}
