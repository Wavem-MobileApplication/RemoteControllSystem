package com.example.remotecontrollsystem.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.remotecontrollsystem.BuildConfig;
import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.ActivityMainBinding;
import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.ui.dialog.DefaultDialog;
import com.example.remotecontrollsystem.ui.dialog.MqttConnectFragment;
import com.example.remotecontrollsystem.ui.util.ToastMessage;

import org.videolan.libvlc.Dialog;

import java.util.List;

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
        settingUtils();

        Log.d("경로", BuildConfig.APPLICATION_ID);

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
                case R.id.manual_control_menu:
                    binding.viewPagerMain.setCurrentItem(1, false);
                    break;
                case R.id.route_menu:
                    binding.viewPagerMain.setCurrentItem(2, false);
                    break;
                case R.id.edit_topic_menu:
                    binding.viewPagerMain.setCurrentItem(3, false);
                    break;
            }
            return false;
        });

        // Initialize ToastMessage
        ToastMessage.setContext(getApplicationContext());
    }

    private void settingWindowFullScreen() {
        View decorView = getWindow().getDecorView();
        int uiOption = decorView.getSystemUiVisibility();

        uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        uiOption |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        uiOption |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        decorView.setSystemUiVisibility(uiOption);
    }

    private void settingClickEvents() {
        binding.btnMenuMain.setOnClickListener(view -> binding.navigationDrawer.open());

        binding.btnConnection.setOnClickListener(view -> {
            MqttConnectFragment dialog = new MqttConnectFragment();
            dialog.show(getSupportFragmentManager(), "MqttConnectFragment");
        });
    }

    private void settingMqttViewModel() {
        topicViewModel = new TopicViewModel(getApplication());
        topicViewModel.getAllTopics().observe(MainActivity.this, new Observer<List<Topic>>() {
            @Override
            public void onChanged(List<Topic> topics) {
                Mqtt.getInstance().setTopicList(topics);
            }
        });
    }

    private void settingUtils() {
        Mqtt.getInstance().setTopicViewModel(topicViewModel);
    }
}