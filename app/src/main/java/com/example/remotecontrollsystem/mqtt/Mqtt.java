package com.example.remotecontrollsystem.mqtt;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;
import com.example.remotecontrollsystem.ui.util.ToastMessage;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Mqtt {
    private static final String TAG = Mqtt.class.getSimpleName();
    private static final String CLIENT_NAME = "rcs_mqtt_client";
    public static final String REQUEST = "/request";
    public static final String FEEDBACK = "/feedback";
    public static final String RESPONSE = "/response";
    private static Mqtt instance;
    private MqttAndroidClient client;

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
        Log.d(TAG, "Try to connect mqtt server -> " + address);

        client = new MqttAndroidClient(context, address, CLIENT_NAME);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}
            @Override
            public void connectionLost(Throwable cause) {
                ToastMessage.showToast(context, "차량과 연결이 끊겼습니다.");
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {}
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setConnectionTimeout(5000);

        try {
            client.connect(connOpts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    ToastMessage.showToast(context, "차량과 연결되었습니다.");

                    try {
                        sendRosMessageInit();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    startToSubscribe();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    ToastMessage.showToast(context, "차량 연결에 실패했습니다.");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
        float interval = 200;

        ArrayList<String> topicFilters = new ArrayList<>();
        ArrayList<Integer> qosFilters = new ArrayList<>();
        ArrayList<IMqttMessageListener> listeners = new ArrayList<>();

        for (Map.Entry<String, RosMessageDefinition> entry : topicMap.entrySet()) {
            final long[] preTime = {0};
            String funcName = entry.getKey();
            String type = entry.getValue().getType();
            String topicName = entry.getValue().getName();
            int qos = entry.getValue().getQos();

            switch (type) {
                case TopicType.SUB:
                    topicFilters.add(topicName);
                    qosFilters.add(qos);
                    listeners.add((topic, message) -> {
                        long currentTime = System.currentTimeMillis();

                        if (currentTime - preTime[0] > interval) {
//                            Log.d(topic, message.toString());
                            RosMessage rosMessage = new RosMessage().fromJson(message.toString(), funcName);
                            messagePublishers.get(funcName).postValue(rosMessage);
                            preTime[0] = currentTime;
                        }
                    });
                    break;
                case TopicType.CALL:
                    topicFilters.add(topicName + RESPONSE);
                    qosFilters.add(qos);
                    listeners.add((topic, message) -> {
                        RosMessage rosMessage = new RosMessage().fromJson(message.toString(), funcName + RESPONSE);
                        messagePublishers.get(funcName + RESPONSE).postValue(rosMessage);
                    });
                    break;
                case TopicType.GOAL:
                    topicFilters.add(topicName + FEEDBACK);
                    qosFilters.add(qos);
                    listeners.add((topic, message) -> {
                        long currentTime = System.currentTimeMillis();

                        if (currentTime - preTime[0] > interval) {
                            RosMessage rosMessage = new RosMessage().fromJson(message.toString(), funcName + FEEDBACK);
                            messagePublishers.get(funcName + FEEDBACK).postValue(rosMessage);
                            preTime[0] = currentTime;
                        }
                    });

                    topicFilters.add(topicName + RESPONSE);
                    qosFilters.add(qos);
                    listeners.add((topic, message) -> {
                        RosMessage rosMessage = new RosMessage().fromJson(message.toString(), funcName + RESPONSE);
                        messagePublishers.get(funcName + RESPONSE).postValue(rosMessage);
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
}
