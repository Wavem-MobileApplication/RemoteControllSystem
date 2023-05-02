package com.example.remotecontrollsystem.mqtt.data;

import android.os.Handler;
import android.os.HandlerThread;

import com.example.remotecontrollsystem.model.utils.LambdaTask;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessagePublisher<T extends RosMessage> implements Subject<T> {
    private static final String TAG = MessagePublisher.class.getSimpleName();
    private final CopyOnWriteArrayList<Observer<T>> observerList = new CopyOnWriteArrayList<>();
    private T oldData;

    private ThreadPoolExecutor mExecutor;

    public MessagePublisher() {
        mExecutor = new ThreadPoolExecutor(2, 4, 20L,
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(20));
    }

    @Override
    public synchronized void attach(Observer<T> observer) {
        if (oldData != null) {
            mExecutor.execute(() -> observer.update(oldData));
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
        mExecutor.execute(() -> {
            for (Observer<T> observer : observerList) {
                observer.update(message);
            }
        });
    }
}
