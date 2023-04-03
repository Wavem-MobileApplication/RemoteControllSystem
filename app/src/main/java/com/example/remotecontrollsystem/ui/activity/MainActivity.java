package com.example.remotecontrollsystem.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Observer;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.ActivityMainBinding;
import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private TopicViewModel topicViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        settingWindowFullScreen();
        settingClickEvents();
        settingMqttViewModel();
    }

    private void init() {
        // Initialize ViewPager2
        MainViewPagerAdapter viewPagerAdapter = new MainViewPagerAdapter(this);
        binding.viewPagerMain.setAdapter(viewPagerAdapter);
        binding.viewPagerMain.setUserInputEnabled(false);

        binding.navMenuMain.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.dashboard_menu:
                    binding.viewPagerMain.setCurrentItem(0, false);
                    break;
                case R.id.edit_topic_menu:
                    binding.viewPagerMain.setCurrentItem(1, false);
                    break;
            }
            return false;
        });
    }

    private void settingWindowFullScreen() {
        View decorView = getWindow().getDecorView();
        int uiOption = decorView.getSystemUiVisibility();

        uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOption);
    }

    private void settingClickEvents() {
        binding.btnMenuMain.setOnClickListener(view -> binding.navigationDrawer.open());
    }

    private void settingMqttViewModel() {
        topicViewModel = new TopicViewModel(getApplication());
        topicViewModel.getAllTopics().observe(MainActivity.this, new Observer<List<Topic>>() {
            @Override
            public void onChanged(List<Topic> topics) {
                Mqtt.getInstance().setRosMessageInitData(topics);
                Mqtt.getInstance().connectToMqttServer("tcp://192.168.0.187:1883");
                Mqtt.getInstance().setTopicList(topics);
            }
        });
    }
}