package com.example.remotecontrollsystem.ros.service;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.ros.node.PubNode;
import com.example.remotecontrollsystem.ros.node.SubNode;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.executors.Executor;
import org.ros2.rcljava.executors.SingleThreadedExecutor;

import java.util.Timer;
import java.util.TimerTask;

public class RosService extends Service {
    private static final String TAG = RosService.class.getSimpleName();
    private static final long SPINNER_DELAY = 0;
    private static final long SPINNER_PERIOD_MS = 200;

    private IBinder mBinder;
    private Executor rosExecutor;
    private Timer timer;
    private Handler handler;
    private WifiManager.MulticastLock lock;

    public RosService() {
        Log.d(TAG, "Service is created.");
        mBinder = new LocalBinder();
        rosExecutor = new SingleThreadedExecutor();
        timer = new Timer();
        handler = new Handler();
    }

    public class LocalBinder extends Binder {
        public RosService getService() {
            return RosService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!RCLJava.ok()) {
            RCLJava.rclJavaInit();
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(spinTask);
            }
        }, SPINNER_DELAY, SPINNER_PERIOD_MS);

        rosExecutor.addNode(SubNode.getInstance());
        rosExecutor.addNode(PubNode.getInstance());

        settingWifiManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service is started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service is bound.");
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "Application is forced by Task Manager");
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service is destroyed");

        if (lock != null && lock.isHeld()) {
            lock.release();
        }

        if (RCLJava.ok()) {
            RCLJava.shutdown();
        }
    }

    public Runnable spinTask = new Runnable() {
        @Override
        public void run() {
            rosExecutor.spinSome();
        }
    };

    private void settingWifiManager() {
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        lock = wifiManager.createMulticastLock("ssdp");
        lock.acquire();
    }
}
