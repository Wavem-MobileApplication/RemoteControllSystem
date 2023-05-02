package com.example.remotecontrollsystem.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.BuildConfig;
import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.ActivityMainBinding;
import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.AndroidMqttService;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.MqttService;
import com.example.remotecontrollsystem.ui.dialog.MqttConnectFragment;
import com.example.remotecontrollsystem.ui.util.ToastMessage;
import com.example.remotecontrollsystem.viewmodel.ConnectionViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;
import com.example.remotecontrollsystem.viewmodel.RobotStatusViewModel;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AndroidMqttService mqttService;
    private TopicViewModel topicViewModel;
    private ConnectionViewModel connectionViewModel;
    private MqttSubViewModel mqttSubViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize default setting
        init();
        settingWindowFullScreen();
        settingClickEvents();
        settingViewModel();

        // Service binding
        Intent intent = new Intent(getApplicationContext(), MqttService.class);
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);

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

    private void settingViewModel() {
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
        mqttSubViewModel = new ViewModelProvider(this).get(MqttSubViewModel.class);

        connectionViewModel = new ViewModelProvider(this).get(ConnectionViewModel.class);
        connectionViewModel.getMqttUrl().observe(this, url ->  {
            mqttService.connectToMqttBroker(url, topicViewModel.getAllTopics().getValue(), mqttSubViewModel);
        });
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AndroidMqttService.MqttBinder mb = (AndroidMqttService.MqttBinder) iBinder;
            mqttService = mb.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}