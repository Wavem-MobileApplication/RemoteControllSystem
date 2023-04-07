package com.example.remotecontrollsystem.mqtt;

import android.util.Log;

import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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
    private static final String REQUEST = "/request";
    private static final String FEEDBACK = "/feedback";
    private static final String RESPONSE = "/response";
    private static Mqtt instance;
    private MqttClient client;
    private HashMap<String, MessagePublisher> messagePublishers;
    private List<Topic> topicList;

    public static Mqtt getInstance() {
        if (instance == null) {
            instance = new Mqtt();
        }
        return instance;
    }

    private Mqtt() {
        init();
    }

    private void init() {
        messagePublishers = new HashMap<>();
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
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((result) -> {
            if (result) {
                startToSubscribe();
            } else {
                Log.d(TAG, "Failed to connect MQTT server...");
            }
        });
    }

    private void startToSubscribe() {
        for (Topic topic : topicList) {
            String funcName = topic.getFuncName();
            String type = topic.getMessage().getType();
            String topicName = topic.getMessage().getName();
            int qos = topic.getMessage().getQos();
            boolean retained = topic.getMessage().isRetained();

            try {
                if (type.equals(TopicType.SUB)) {
                    client.subscribe(topicName, qos, (topic1, message) -> {
                        messagePublishers.get(funcName).postValue(message.toString());
                    });
                    Log.d(TAG, "Subscribe -> " + topicName);
                } else if (type.equals(TopicType.CALL)) {
                    client.subscribe(topicName + RESPONSE, qos, (topic12, message) -> {
                        messagePublishers.get(funcName + RESPONSE).postValue(message.toString());
                    });
                    Log.d(TAG, "Subscribe -> " + topicName + RESPONSE);
                } else if (type.equals(TopicType.GOAL)) {
                    client.subscribe(topicName + FEEDBACK, qos, (topic13, message) -> {
                        messagePublishers.get(funcName + FEEDBACK).postValue(message.toString());
                    });
                    Log.d(TAG, "Subscribe -> " + topicName + FEEDBACK);
                    client.subscribe(topicName + RESPONSE, qos, (topic14, message) -> {
                        messagePublishers.get(funcName + RESPONSE).postValue(message.toString());
                    });
                    Log.d(TAG, "Subscribe -> " + topicName + RESPONSE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setTopicList(List<Topic> topicList) {
        this.topicList = topicList;

        // Update MessagePublishers
        for (Topic topic : topicList) {
            String topicName = topic.getMessage().getName();
            String type = topic.getMessage().getType();
            String funcName = topic.getFuncName();

            // Add Message Publishers
            if (type.equals(TopicType.SUB)) {
                messagePublishers.put(funcName, new MessagePublisher());
                Log.d(TAG, "Add Publisher -> " + funcName);
            } else if (type.equals(TopicType.CALL)) {
                messagePublishers.put(funcName + RESPONSE, new MessagePublisher());
                Log.d(TAG, "Add Publisher -> " + funcName + RESPONSE);
            } else if (type.equals(TopicType.GOAL)) {
                messagePublishers.put(funcName + FEEDBACK, new MessagePublisher());
                messagePublishers.put(funcName + RESPONSE, new MessagePublisher());
                Log.d(TAG, "Add Publisher -> " + funcName + FEEDBACK);
                Log.d(TAG, "Add Publisher -> " + funcName + RESPONSE);
            }
        }
    }
}
