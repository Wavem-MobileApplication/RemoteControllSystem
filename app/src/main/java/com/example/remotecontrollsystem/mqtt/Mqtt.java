package com.example.remotecontrollsystem.mqtt;

import android.util.Log;

import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.listener.FeedbackListener;
import com.example.remotecontrollsystem.mqtt.listener.ResponseListener;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
    private static final String REQUEST = "/request";
    private static final String FEEDBACK = "/feedback";
    private static final String RESPONSE = "/response";
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
                        publishRosMessageInit(rosMessageInitData);
                        startSubscribeMqttTopics();
                    }
                });
    }

    private void startSubscribeMqttTopics() throws MqttException {
        for (Map.Entry<String, RosMessageDefinition> entry : topicMap.entrySet()) {
            String topicName = entry.getValue().getName();
            String type = entry.getValue().getType();
            int qos = entry.getValue().getQos();

            if (type.equals(TopicType.SUB)) {
                Log.d(TAG, "Subscribe -> " + topicName);
                client.subscribe(topicName, qos, new IMqttMessageListener() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        topicDataPublisherMap.get(entry.getKey()).postValue(message.toString());
                    }
                });
            } else if (type.equals(TopicType.GOAL)) {
                Log.d(TAG, "Subscribe -> " + topicName + FEEDBACK);
                client.subscribe(topicName + FEEDBACK, qos, new IMqttMessageListener() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        topicDataPublisherMap.get(entry.getKey()).postValue(message.toString());
                    }
                });

                Log.d(TAG, "Subscribe -> " + topicName + RESPONSE);
                client.subscribe(topicName + RESPONSE, qos, new IMqttMessageListener() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        topicDataPublisherMap.get(entry.getKey()).postValue(message.toString());
                    }
                });
            } else if (type.equals(TopicType.CALL)) {
                Log.d(TAG, "Subscribe -> " + topicName + RESPONSE);
                client.subscribe(topicName + RESPONSE, qos, new IMqttMessageListener() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        topicDataPublisherMap.get(entry.getKey()).postValue(message.toString());
                    }
                });
            }
        }

    }

    public void setRosMessageInitData(List<Topic> topics) {
        List<RosMessageDefinition> defList = new ArrayList<>();
        for (Topic topic : topics) {
            defList.add(topic.getMessage());
        }

        rosMessageInitData = defList;
    }

    public void publishRosMessageInit(List<RosMessageDefinition> rosMessageInitData) {
        if (client.isConnected()) {
            String message = new Gson().toJson(rosMessageInitData);
            byte[] payload = message.getBytes();
            try {
                client.publish("ros_message_init", payload, 1, true);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void publishMqttMessage(String topicName, Object message, int qos, boolean retained) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mode", "pub");
            jsonObject.add("data", new Gson().toJsonTree(message));
            String data = jsonObject.toString();
            byte[] payload = data.getBytes();

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
            } else if (topic.getMessage().getType().equals(TopicType.GOAL)) {
                topicMap.put(funcName, topic.getMessage());
/*                String feedbackName = funcName + FEEDBACK;
                String responseName = funcName + RESPONSE;
                topicMap.put(feedbackName, topic.getMessage());
                topicMap.put(responseName, topic.getMessage());*/
            }
        }
    }

/*    public void sendActionRequest(RosMessageDefinition messageDefinition, String data, FeedbackListener feedbackListener, ResponseListener responseListener) {
        try {
            int qos = messageDefinition.getQos();
            boolean retained = messageDefinition.isRetained();

            String feedbackName = messageDefinition.getName() + FEEDBACK;
            client.subscribe(feedbackName, 0, (topic, message) -> {
                feedbackListener.onReceive(message.toString());
            });

            String responseName = messageDefinition.getName() + RESPONSE;
            client.subscribe(responseName, qos, (topic, message) -> {
                responseListener.onReceive(message.toString());
                client.unsubscribe(feedbackName);
                client.unsubscribe(responseName);
            });

            String requestName = messageDefinition.getName() + REQUEST;
            byte[] payload = data.getBytes();
            client.publish(requestName, payload, messageDefinition.getQos(), messageDefinition.isRetained());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendServiceRequest(RosMessageDefinition messageDefinition, String data, ResponseListener responseListener) {
        try {
            int qos = messageDefinition.getQos();
            boolean retained = messageDefinition.isRetained();

            String responseName = messageDefinition.getName() + RESPONSE;
            Log.d("Mqtt_Subscribe", messageDefinition.getName());
            client.subscribe(responseName, qos, (topic, message) -> {
                responseListener.onReceive(message.toString());
                client.unsubscribe(responseName);
            });

            String requestName = messageDefinition.getName() + REQUEST;

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mode", "call");
            jsonObject.addProperty("data", data);
            String call = jsonObject.toString();
            byte[] payload = call.getBytes();

            Log.d("Mqtt_Publish", messageDefinition.getName());
            client.publish(requestName, payload, qos, retained);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
