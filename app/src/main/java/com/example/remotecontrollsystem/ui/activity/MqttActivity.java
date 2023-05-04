package com.example.remotecontrollsystem.ui.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.MessageType;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;
import com.example.remotecontrollsystem.ui.util.ToastMessage;
import com.example.remotecontrollsystem.ui.viewmodel.ConnectionViewModel;
import com.example.remotecontrollsystem.ui.viewmodel.MqttSubViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MqttActivity extends AppCompatActivity {
    private static final String TAG = MqttActivity.class.getSimpleName();
    private static final String CLIENT_NAME = "rcs_mqtt_client";
    private MqttAndroidClient client;

    TopicViewModel topicViewModel;
    MqttSubViewModel mqttSubViewModel;
    ConnectionViewModel connectionViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
        mqttSubViewModel = new ViewModelProvider(this).get(MqttSubViewModel.class);
        connectionViewModel = new ViewModelProvider(this).get(ConnectionViewModel.class);

        connectionViewModel.getMqttUrl().observe(this, this::connectToMqttServer);

        topicViewModel.getAllTopics().observe(this, new Observer<List<Topic>>() {
            @Override
            public void onChanged(List<Topic> topics) {
//                Type type = new TypeToken<List<Topic>>() {}.getType();
                Log.d("Topics", new Gson().toJson(topics));
            }
        });

        settingWindowFullScreen();
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

    public void connectToMqttServer(String address) {
        Log.d(TAG, "Start to connect -> " + address);

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setConnectionTimeout(5000);

        client = new MqttAndroidClient(getApplicationContext(), address, CLIENT_NAME);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
            }
            @Override
            public void connectionLost(Throwable throwable) {
                ToastMessage.showToast(getApplicationContext(), "차량과 연결이 끊겼습니다.");
                throwable.printStackTrace();
            }
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {}
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
        });

        try {
            client.connect(connOpts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.d(TAG, "Success to connect Mqtt broker");
                    ToastMessage.showToast(getApplicationContext(), "차량과 연결되었습니다.");

                    try {
                        sendRosMessageInit();
                        startToSubscribe();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.d(TAG, "Failed to connect Mqtt broker");
                    ToastMessage.showToast(getApplicationContext(), "차량에 연결하지 못했습니다.");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendRosMessageInit() throws MqttException {
        List<RosMessageDefinition> data = new ArrayList<>();

        if (topicViewModel.getAllTopics().getValue() != null) {
            for (Topic topic : topicViewModel.getAllTopics().getValue()) {
                data.add(topic.getMessage());
            }

            byte[] payload = new Gson().toJson(data).getBytes();
            client.publish("ros_message_init", payload, 0, false);
        } else {
            Log.d("GetAllTopics", "NULL");
            ToastMessage.showToast(getApplicationContext(), "차량 연결을 다시 시도해주세요.");
        }
    }

    private void startToSubscribe() {
        int interval = 200;

        List<Topic> topicList = topicViewModel.getAllTopics().getValue();

        ArrayList<String> topicFilters = new ArrayList<>();
        ArrayList<Integer> qosFilter = new ArrayList<>();
        ArrayList<IMqttMessageListener> listeners = new ArrayList<>();

        if (topicList != null) {
            for (Topic topic : topicList) {
                String funcName = topic.getFuncName();
                String type = topic.getMessage().getType();
                String topicName = topic.getMessage().getName();
                int qos = topic.getMessage().getQos();

                switch (type) {
                    case TopicType.SUB:
                        topicFilters.add(topicName);
                        qosFilter.add(qos);
                        listeners.add(createIMqttMessageListener(funcName, interval));
                        break;
                    case TopicType.CALL:
                        topicFilters.add(topicName + MessageType.RESPONSE);
                        qosFilter.add(qos);
                        listeners.add(createIMqttMessageListener(
                                funcName + MessageType.RESPONSE.getType(), interval));
                        break;
                    case TopicType.GOAL:
                        topicFilters.add(topicName + MessageType.FEEDBACK);
                        qosFilter.add(qos);
                        listeners.add(createIMqttMessageListener(
                                funcName + MessageType.FEEDBACK.getType(), interval));

                        topicFilters.add(topicName + MessageType.RESPONSE);
                        qosFilter.add(qos);
                        listeners.add(createIMqttMessageListener(
                                funcName + MessageType.RESPONSE.getType(), interval));
                        break;
                }
            }
        }

        String[] topicFilterArray = topicFilters.toArray(new String[topicFilters.size()]);
        int[] qosFilterArray = qosFilter.stream().mapToInt(Integer::intValue).toArray();
        IMqttMessageListener[] listenerArray = listeners.toArray(new IMqttMessageListener[listeners.size()]);

        try {
            Log.d("Subscribe", "[" + Arrays.toString(topicFilterArray) + "]");
            client.subscribe(topicFilterArray, qosFilterArray, listenerArray);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private IMqttMessageListener createIMqttMessageListener(String funcName, int interval) {
        final long[] preTime = {0};

        IMqttMessageListener listener = (s, mqttMessage) -> {
            long currentTime = System.currentTimeMillis();

            if (currentTime - preTime[0] > interval) {
                RosMessage rosMessage = new RosMessage().fromJson(mqttMessage.toString(), funcName);
                mqttSubViewModel.updateMqttData(funcName, rosMessage);
                preTime[0] = currentTime;
            }
        };

        return listener;
    }

    public MqttAndroidClient getClient() {
        return client;
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
