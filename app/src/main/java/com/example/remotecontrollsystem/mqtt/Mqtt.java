package com.example.remotecontrollsystem.mqtt;

import android.content.Context;
import android.util.Log;

import com.example.remotecontrollsystem.BuildConfig;
import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;
import com.example.remotecontrollsystem.ui.util.ToastMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.reflect.Type;
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
    public static final String REQUEST = "/request";
    public static final String FEEDBACK = "/feedback";
    public static final String RESPONSE = "/response";
    private static Mqtt instance;
    private MqttClient client;
    private TopicViewModel topicViewModel;
    private HashMap<String, MessagePublisher> messagePublishers;
    private HashMap<String, RosMessageDefinition> topicMap;

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
        topicMap = new HashMap<>();
    }

    public void connectToMqttServer(Context context, String address) {
        Disposable backgroundTask = Observable.fromCallable(() -> {
            try {
                Log.d(TAG, "Try to connect mqtt server -> " + address);

                // If MqttClient is already connected, disconnect client
                if (client != null && client.isConnected()) {
                    client.disconnectForcibly();
                    client = null;
                }

                MqttConnectOptions connOpts = new MqttConnectOptions();

                client = new MqttClient(address, CLIENT_NAME, null);
                client.connect(connOpts);

                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.d("Connection Lost", cause.getMessage());
                        cause.printStackTrace();
                    }
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {}
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {}
                });

                return true;
            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((result) -> {
            if (result) {
                Log.d(TAG, "Success to connect MQTT server...");
                ToastMessage.showToast(context, "차량에 연결하였습니다.");
                sendRosMessageInit();
                startToSubscribe();
            } else {
                Log.d(TAG, "Failed to connect MQTT server...");
                ToastMessage.showToast(context, "차량 연결에 실패하였습니다.");
            }
        });

    }

    private void sendRosMessageInit() throws MqttException {
        List<RosMessageDefinition> msgDefList = new ArrayList<>();
        for (Map.Entry<String, RosMessageDefinition> entry : topicMap.entrySet()) {
            msgDefList.add(entry.getValue());
        }

        byte[] payload = new Gson().toJson(msgDefList).getBytes();
        client.publish("ros_message_init", payload, 0, false);
    }

    private void startToSubscribe() {
        Gson gson = new Gson();
        for (Map.Entry<String, RosMessageDefinition> entry : topicMap.entrySet()) {
            String funcName = entry.getKey();
            String type = entry.getValue().getType();
            String topicName = entry.getValue().getName();
            int qos = entry.getValue().getQos();

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

        // Update MessagePublishers
        for (Topic topic : topicList) {
            String type = topic.getMessage().getType();
            String funcName = topic.getFuncName();

            //Add to topicMap
            topicMap.put(funcName, topic.getMessage());

            // Add Message Publishers
            if (type.equals(TopicType.SUB)) {
                createMessagePublisher(funcName);

                Log.d(TAG, "Add Publisher -> " + funcName);
            } else if (type.equals(TopicType.CALL)) {
                createMessagePublisher(funcName + RESPONSE);

                Log.d(TAG, "Add Publisher -> " + funcName + RESPONSE);
            } else if (type.equals(TopicType.GOAL)) {
                createMessagePublisher(funcName + FEEDBACK);
                createMessagePublisher(funcName + RESPONSE);

                Log.d(TAG, "Add Publisher -> " + funcName + FEEDBACK);
                Log.d(TAG, "Add Publisher -> " + funcName + RESPONSE);
            }
        }

    }

    private void createMessagePublisher(String widgetName) {
        if (messagePublishers.get(widgetName) == null) {
            messagePublishers.put(widgetName, new MessagePublisher());
        }
    }

    public void publishMessage(String funcName, Object data, int qos, boolean retained) {
        try {
            String topicName = topicMap.get(funcName).getName();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mode", "pub");
            jsonObject.add("data", new Gson().toJsonTree(data));
            byte[] payload = jsonObject.toString().getBytes();

            Log.d(funcName, topicName);

            client.publish(topicName, payload, qos, retained);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRequestMessageGoal(String funcName, Object data, int qos, boolean retained) {
        try {
            String requestName = topicMap.get(funcName).getName() + REQUEST;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mode", "goal");
            jsonObject.add("data", new Gson().toJsonTree(data));
            byte[] payload = jsonObject.toString().getBytes();

            Log.d(funcName, requestName);

            client.publish(requestName, payload, qos, retained);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRequestMessageCall(String funcName, Object data, int qos, boolean retained) {
        try {
            String requestName = topicMap.get(funcName).getName() + REQUEST;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mode", "call");
            jsonObject.add("data", new Gson().toJsonTree(data));
            byte[] payload = jsonObject.toString().getBytes();

            Log.d(funcName, requestName);

            client.publish(requestName, payload, qos, retained);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MessagePublisher getMessagePublisher(String widgetName) {
        createMessagePublisher(widgetName);
        return messagePublishers.get(widgetName);
    }

    public void setTopicViewModel(TopicViewModel topicViewModel) {
        this.topicViewModel = topicViewModel;
    }
}
