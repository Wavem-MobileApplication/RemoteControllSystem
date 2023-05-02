package com.example.remotecontrollsystem.mqtt;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.utils.MessageType;
import com.example.remotecontrollsystem.mqtt.utils.RosMessageUtil;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;
import com.example.remotecontrollsystem.ui.util.ToastMessage;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttService;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AndroidMqttService extends MqttService {
    private static final String MQTT_CLIENT_ID = "rcs_android_mqtt_client";
    private MqttAndroidClient mqttClient;
    private Gson gson;

    public AndroidMqttService() {
        gson = new Gson();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MqttBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MqttAndroidClient connectToMqttBroker(String url, List<Topic> topicList, MqttSubViewModel subViewModel) {
        // If mqtt client is connected(try to reconnect) disconnect old client
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                mqttClient.close();
                mqttClient = null;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        mqttClient = new MqttAndroidClient(getApplicationContext(), url, MQTT_CLIENT_ID);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setConnectionTimeout(3000);

        try {
            mqttClient.connect(connOpts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    ToastMessage.showToast(getApplicationContext(), "차량에 연결되었습니다.");
                    subscribeTopics(topicList, subViewModel);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    ToastMessage.showToast(getApplicationContext(), "차량에 연결할 수 없습니다.");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return mqttClient;
    }

    private void subscribeTopics(List<Topic> topicList, MqttSubViewModel subViewModel) {
        ArrayList<String> topicFilters = new ArrayList<>();
        ArrayList<Integer> qosFilters = new ArrayList<>();
        ArrayList<IMqttMessageListener> listeners = new ArrayList<>();

        for (Topic topic : topicList) {
            String funcName = topic.getFuncName();
            String type = topic.getMessage().getType();
            String topicName = topic.getMessage().getName();
            int qos = topic.getMessage().getQos();

            switch (type) {
                case TopicType.SUB:
                    topicFilters.add(topicName);
                    qosFilters.add(qos);
                    listeners.add((topi1, message) -> {
                        RosMessage rosMessage = RosMessageUtil.parseJsonToRosMessage(message.toString(), funcName);
                        subViewModel.updateLiveData(funcName, rosMessage);
                    });
                    break;
                case TopicType.CALL:
                    topicFilters.add(topicName + MessageType.RESPONSE.getType());
                    qosFilters.add(qos);
                    listeners.add((topic1, message) -> {
                        RosMessage rosMessage = RosMessageUtil.parseJsonToRosMessage(
                                message.toString(), funcName + MessageType.RESPONSE.getType());
                        subViewModel.updateLiveData(funcName + MessageType.RESPONSE.getType(), rosMessage);
                    });
                    break;
                case TopicType.GOAL:
                    topicFilters.add(topicName + MessageType.FEEDBACK.getType());
                    qosFilters.add(qos);
                    listeners.add((topic1, message) -> {
                        RosMessage rosMessage = RosMessageUtil.parseJsonToRosMessage(
                                message.toString(), funcName + MessageType.FEEDBACK.getType());
                        subViewModel.updateLiveData(funcName + MessageType.FEEDBACK.getType(), rosMessage);
                    });

                    topicFilters.add(topicName + MessageType.RESPONSE.getType());
                    qosFilters.add(qos);
                    listeners.add((topi1, message) -> {
                        RosMessage rosMessage = RosMessageUtil.parseJsonToRosMessage(
                                message.toString(), funcName + MessageType.RESPONSE.getType());
                        subViewModel.updateLiveData(funcName + MessageType.RESPONSE.getType(), rosMessage);
                    });
                    break;
            }
        }

        // Change array_list to array
        String[] topicFilterArray = topicFilters.toArray(new String[topicFilters.size()]);
        int[] qosFilterArray = qosFilters.stream().mapToInt(Integer::intValue).toArray();
        IMqttMessageListener[] listenersArray = listeners.toArray(new IMqttMessageListener[listeners.size()]);

        try {
            Log.d("Subscribe", "[" + Arrays.toString(topicFilterArray) + "]");
            mqttClient.subscribe(topicFilterArray, qosFilterArray, listenersArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String topicName, RosMessage rosMessage, int qos, boolean retained) {
        if (mqttClient.isConnected()) {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("mode", "pub");
                jsonObject.add("data", gson.toJsonTree(rosMessage));

                byte[] payload = jsonObject.toString().getBytes();

                mqttClient.publish(topicName, payload, qos, retained);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MqttBinder extends Binder {
        public AndroidMqttService getService() {
            return AndroidMqttService.this;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.unregisterResources();
            mqttClient.close();
        }
    }
}