package com.example.remotecontrollsystem.data;

public interface IObserver<T> {
    void update(T data);
}
