package com.example.remotecontrollsystem.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.remotecontrollsystem.databinding.ActivityMainBinding;
import com.example.remotecontrollsystem.ros.node.SubNode;
import com.example.remotecontrollsystem.ros.service.RosService;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.consumers.Consumer;

import geometry_msgs.msg.Pose;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private RosService rosService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        settingWindowFullScreen();
        settingRosService();
    }

    private void init() {
        if (!RCLJava.ok()) RCLJava.rclJavaInit();

        MainViewPagerAdapter adapter = new MainViewPagerAdapter(this);
        binding.viewPagerMain.setAdapter(adapter);
        binding.viewPagerMain.setUserInputEnabled(false);
    }

    private void settingWindowFullScreen() {
        View decorView = getWindow().getDecorView();
        int uiOption = decorView.getSystemUiVisibility();

        uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOption);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RosService.LocalBinder lb = (RosService.LocalBinder) service;
            rosService = lb.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    private void settingRosService() {
        Intent intent = new Intent(getApplicationContext(), RosService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }
}