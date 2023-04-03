package com.example.remotecontrollsystem.mqtt.data;

import java.util.ArrayList;
import java.util.List;

public class MessagePublisher implements Subject{
    private final List<Observer> observerList = new ArrayList<>();

    @Override
    public void attach(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void postValue(String message) {
        for (Observer observer : observerList) {
            observer.update(message);
        }
    }
}
