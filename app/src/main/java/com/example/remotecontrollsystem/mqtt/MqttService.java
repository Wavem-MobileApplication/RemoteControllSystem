package com.example.remotecontrollsystem.mqtt;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.remotecontrollsystem.BuildConfig;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class MqttService extends Service {
    private static final String TAG = MqttService.class.getSimpleName();

    // Mqtt
    private static final String CLIENT_NAME = "rcs_mqtt_client";

    // Service
    private IBinder mBinder;
    public MqttService() {
        mBinder = new LocalBinder();
    }

    public class LocalBinder extends Binder {
        public MqttService getService() {
            return MqttService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, BuildConfig.APPLICATION_ID + " is forced by task manager");
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Mqtt.getInstance().releaseMqtt();
    }

    public void connectToMqttServer(String uri) {
        Mqtt.getInstance().connectToMqttServer(getApplicationContext(), uri);
    }
}