package com.example.remotecontrollsystem.mqtt;

import android.content.Context;
import android.util.Log;

import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;
import com.example.remotecontrollsystem.ui.util.ToastMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Arrays;
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

    private HashMap<String, MessagePublisher<RosMessage>> messagePublishers;
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
                    client.disconnect();
                    client = null;
                }

                MqttConnectOptions connOpts = new MqttConnectOptions();

                client = new MqttClient(address, CLIENT_NAME, null);
                client.connect(connOpts);

                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        ToastMessage.showToast(context, "차량과의 연결이 끊겼습니다.");
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

        Log.d("Publish ROS Message Init", new Gson().toJson(msgDefList));
        client.publish("ros_message_init", payload, 0, false);
    }

    private void startToSubscribe() {
        ArrayList<String> topicFilters = new ArrayList<>();
        ArrayList<Integer> qosFilters = new ArrayList<>();
        ArrayList<IMqttMessageListener> listeners = new ArrayList<>();

        for (Map.Entry<String, RosMessageDefinition> entry : topicMap.entrySet()) {
            String funcName = entry.getKey();
            String type = entry.getValue().getType();
            String topicName = entry.getValue().getName();
            int qos = entry.getValue().getQos();

            switch (type) {
                case TopicType.SUB:
                    topicFilters.add(topicName);
                    qosFilters.add(qos);
                    listeners.add((topic, message) -> {
  /*                      RosMessage rosMessage = new RosMessage().fromJson(message.toString(), funcName);
                        messagePublishers.get(funcName).postValue(rosMessage);*/
                    });
                    break;
                case TopicType.CALL:
                    topicFilters.add(topicName + RESPONSE);
                    qosFilters.add(qos);
                    listeners.add((topic, message) -> {
                        /*RosMessage rosMessage = new RosMessage().fromJson(message.toString(), funcName + RESPONSE);
                        messagePublishers.get(funcName + RESPONSE).postValue(rosMessage);*/
                    });
                    break;
                case TopicType.GOAL:
                    topicFilters.add(topicName + FEEDBACK);
                    qosFilters.add(qos);
                    listeners.add((topic, message) -> {
                        /*RosMessage rosMessage = new RosMessage().fromJson(message.toString(), funcName + FEEDBACK);
                        messagePublishers.get(funcName + FEEDBACK).postValue(rosMessage);*/
                    });

                    topicFilters.add(topicName + RESPONSE);
                    qosFilters.add(qos);
                    listeners.add((topic, message) -> {
/*                        RosMessage rosMessage = new RosMessage().fromJson(message.toString(), funcName + RESPONSE);
                        messagePublishers.get(funcName + RESPONSE).postValue(rosMessage);*/
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
            client.subscribe(topicFilterArray, qosFilterArray, listenersArray);
        } catch (Exception e) {
            e.printStackTrace();
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
            switch (type) {
                case TopicType.SUB:
                    createMessagePublisher(funcName);

                    Log.d(TAG, "Add Publisher -> " + funcName);
                    break;
                case TopicType.CALL:
                    createMessagePublisher(funcName + RESPONSE);

                    Log.d(TAG, "Add Publisher -> " + funcName + RESPONSE);
                    break;
                case TopicType.GOAL:
                    createMessagePublisher(funcName + FEEDBACK);
                    createMessagePublisher(funcName + RESPONSE);

                    Log.d(TAG, "Add Publisher -> " + funcName + FEEDBACK);
                    Log.d(TAG, "Add Publisher -> " + funcName + RESPONSE);
                    break;
            }
        }
    }

    private void createMessagePublisher(String widgetName) {
        if (messagePublishers.get(widgetName) == null) {
            messagePublishers.put(widgetName, new MessagePublisher());
        }
    }

    public void publishMessage(String funcName, Object data, int qos, boolean retained) {
        if (client.isConnected()) {
            try {
                String topicName = topicMap.get(funcName).getName();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("mode", "pub");
                jsonObject.add("data", new Gson().toJsonTree(data));
                byte[] payload = jsonObject.toString().getBytes();

                client.publish(topicName, payload, qos, retained);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendRequestMessageGoal(String funcName, Object data, int qos, boolean retained) {
        if (client.isConnected()) {
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
    }

    public void sendRequestMessageCall(String funcName, Object data, int qos, boolean retained) {
        if (client.isConnected()) {
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
    }

    public MessagePublisher getMessagePublisher(String widgetName) {
        createMessagePublisher(widgetName);
        return messagePublishers.get(widgetName);
    }

    public void releaseMqtt() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
