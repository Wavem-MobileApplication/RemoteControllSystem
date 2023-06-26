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
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Request;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.MessageType;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.ui.util.ToastMessage;
import com.example.remotecontrollsystem.viewmodel.ConnectionViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttPubViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;
import com.example.remotecontrollsystem.viewmodel.StatusViewModel;
import com.example.remotecontrollsystem.viewmodel.manager.AutoDrivingManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class MqttActivity extends AppCompatActivity {
    private static final String TAG = MqttActivity.class.getSimpleName();
    private static final String CLIENT_NAME = "rcs_mqtt_client";
    private MqttAndroidClient client;
    private Gson gson;

    TopicViewModel topicViewModel;
    MqttSubViewModel mqttSubViewModel;
    MqttPubViewModel mqttPubViewModel;
    ConnectionViewModel connectionViewModel;
    RouteViewModel routeViewModel;
    StatusViewModel statusViewModel;
    AutoDrivingManager autoDrivingManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gson = new Gson();

        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
        mqttSubViewModel = new ViewModelProvider(this).get(MqttSubViewModel.class);
        mqttPubViewModel = new ViewModelProvider(this).get(MqttPubViewModel.class);
        connectionViewModel = new ViewModelProvider(this).get(ConnectionViewModel.class);
        routeViewModel = new ViewModelProvider(this).get(RouteViewModel.class);
        statusViewModel = new ViewModelProvider(this).get(StatusViewModel.class);
        autoDrivingManager = new AutoDrivingManager(mqttSubViewModel, mqttPubViewModel, statusViewModel);

        connectionViewModel.getMqttUrl().observe(this, this::connectToMqttServer);
        topicViewModel.getAllTopics().observe(this, new Observer<List<Topic>>() { // Observer를 생성해야 getValue시 업데이트 된 데이터를 반환
            @Override
            public void onChanged(List<Topic> topics) {
                Log.d("AllTopics", gson.toJson(topics));
            }
        });

        mqttPubViewModel.getPublisher().observe(this, pair -> {
            String topicName = pair.first;
            String type = pair.second.first;
            RosMessage data = pair.second.second;

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mode", type);
            jsonObject.add("data", gson.toJsonTree(data));

            try {
                if (client != null) {
                    client.publish(topicName, jsonObject.toString().getBytes(), 0, false);
                    Log.d("publish", jsonObject.toString());
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });

        mqttPubViewModel.getMqttPublisher().observe(this, pair -> {
            try {
                if (client != null) {
                    client.publish(pair.first, pair.second.getBytes(), 0, false);
                    Log.d("MqttPublish", pair.first + " -> " + pair.second);
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });

        settingWindowFullScreen();
        settingAutoDrivingManager();
    }

    private void settingAutoDrivingManager() {
        routeViewModel.getCurrentRoute().observe(this, route -> autoDrivingManager.setRoute(route));
        mqttSubViewModel.getFeedbackLiveData(WidgetType.NAVIGATE_TO_POSE).observe(this, autoDrivingManager.feedbackObserver);
        mqttSubViewModel.getResponseLiveData(WidgetType.NAVIGATE_TO_POSE).observe(this, autoDrivingManager.responseObserver);
        mqttPubViewModel.getAutoDrivingController().observe(this, startDriving -> {
            if (startDriving) {
                autoDrivingManager.startAutoDriving();
            } else {
                autoDrivingManager.stopAutoDriving();
            }
        });
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

        client = new MqttAndroidClient(
                getApplicationContext(),
                address,
                UUID.randomUUID().toString() + CLIENT_NAME);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {}
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
                        mqttPubViewModel.publishCall(WidgetType.GET_MAP.getType(), new GetMap_Request());
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

            Log.d("Ros Message Init", new Gson().toJson(data));
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
                        topicFilters.add(topicName + MessageType.RESPONSE.getType());
                        qosFilter.add(qos);
                        listeners.add(createIMqttMessageListener(
                                funcName + MessageType.RESPONSE.getType(), interval));
                        break;
                    case TopicType.GOAL:
                        topicFilters.add(topicName + MessageType.FEEDBACK.getType());
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
            Log.d("Subscribe", Arrays.toString(topicFilterArray));
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
