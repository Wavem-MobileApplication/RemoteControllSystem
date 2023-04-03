package com.example.remotecontrollsystem.mqtt;

import android.util.Log;

import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Mqtt {
    private static final String TAG = Mqtt.class.getSimpleName();
    private static final String CLIENT_NAME = "rcs_mqtt_client";

    private static Mqtt instance;
    private MqttClient client;
    private HashMap<String, RosMessageDefinition> topicMap; // Key: FuncName, Value: topicName
    private HashMap<String, MessagePublisher> topicDataPublisherMap;
    private List<RosMessageDefinition> rosMessageInitData;

    public static Mqtt getInstance() {
        if (instance == null) {
            instance = new Mqtt();
        }
        return instance;
    }

    private Mqtt() {
        topicMap = new HashMap<>();
        init();
    }

    private void init() {
        topicDataPublisherMap = new HashMap<>();
        for (WidgetType type : WidgetType.values()) {
            topicDataPublisherMap.put(type.getType(), new MessagePublisher());
        }
    }

    public void connectToMqttServer(String address) {
        Disposable backgroundTask = Observable.fromCallable(() -> {
                    try {
                        Log.d(TAG, "Try to connect mqtt server");
                        // If MqttClient is already connected, disconnect client
                        if (client != null && client.isConnected()) {
                            client.disconnectForcibly();
                            client = null;
                        }

                        MqttConnectOptions connOpts = new MqttConnectOptions();
                        connOpts.setConnectionTimeout(5000);

                        client = new MqttClient(address, CLIENT_NAME, null);
                        client.connect(connOpts);

                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();

                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (result) {
                        publishMqttMessage("ros_message_init", rosMessageInitData, 1, true);
                        startSubscribeMqttTopics();
                    }
                });
    }

    private void startSubscribeMqttTopics() throws MqttException {
        for (Map.Entry<String, RosMessageDefinition> entry : topicMap.entrySet()) {
            String topicName = entry.getValue().getName();
            int qos = entry.getValue().getQos();

            Log.d(TAG, "Subscribe -> " + topicName);
            client.subscribe(topicName, qos, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    topicDataPublisherMap.get(entry.getKey()).postValue(message.toString());
                }
            });
        }

    }

    public void setRosMessageInitData(List<Topic> topics) {
        List<RosMessageDefinition> defList = new ArrayList<>();
        for (Topic topic : topics) {
            defList.add(topic.getMessage());
        }

        rosMessageInitData = defList;
    }

    public void publishMqttMessage(String topicName, Object message, int qos, boolean retained) {
        byte[] payload = new Gson().toJson(message).getBytes();
        try {
            client.publish(topicName, payload, qos, retained);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MessagePublisher getMqttMessageListener(String funcName) {
        return topicDataPublisherMap.get(funcName);
    }

    public void setTopicList(List<Topic> topicList) {
        for (Topic topic : topicList) {
            String funcName = topic.getFuncName();
            String topicName = topic.getMessage().getName();

            if (topic.getMessage().getType().equals(TopicType.SUB)) {
                Log.d(TAG, "Add topic to HashMap -> " + topicName);
                topicMap.put(funcName, topic.getMessage());
            }
        }
    }
}
