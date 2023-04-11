package com.example.remotecontrollsystem.data;


public interface ISubject<T> {
    void attach(IObserver<T> observer);
    void detach(IObserver<T> observer);
    void postValue(T data);
}
