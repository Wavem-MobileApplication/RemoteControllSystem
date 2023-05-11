package com.example.remotecontrollsystem.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.example.remotecontrollsystem.BuildConfig;
import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.ActivityMainBinding;
import com.example.remotecontrollsystem.ui.dialog.MqttConnectFragment;

public class MainActivity extends MqttActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        settingClickEvents();

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
    }

    private void settingClickEvents() {
        binding.btnMenuMain.setOnClickListener(view -> binding.navigationDrawer.open());

        binding.btnConnection.setOnClickListener(view -> {
            MqttConnectFragment dialog = new MqttConnectFragment();
            dialog.show(getSupportFragmentManager(), "MqttConnectFragment");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}