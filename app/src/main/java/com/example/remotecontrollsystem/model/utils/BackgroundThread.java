package com.example.remotecontrollsystem.model.utils;

import android.os.HandlerThread;

public class BackgroundThread extends HandlerThread {
    public BackgroundThread(String name) {
        super(name);
    }
}
