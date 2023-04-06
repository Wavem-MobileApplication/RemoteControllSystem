package com.example.remotecontrollsystem.model.utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LambdaTask implements Disposable {

    private TaskRunnable taskRunnable;

    public LambdaTask(TaskRunnable runnable) {
        this.taskRunnable = runnable;
    }

    @Override
    public void dispose() {
        Observable.fromCallable(() -> {
            taskRunnable.run();
            return false;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    public interface TaskRunnable {
        void run();
    }
}
