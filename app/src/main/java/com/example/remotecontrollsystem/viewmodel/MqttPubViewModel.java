package com.example.remotecontrollsystem.viewmodel;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.utils.MessageType;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

public class MqttPubViewModel extends AndroidViewModel {
    private TopicViewModel topicViewModel;
    private MutableLiveData<Pair<String, Pair<String, RosMessage>>> publisher;
    private MutableLiveData<Boolean> controlAutoDriving;
    private MutableLiveData<Pair<String, String>> mqttPublisher;

    private HashMap<String, String> pubTopicNameMap;

    public MqttPubViewModel(@NonNull Application application) {
        super(application);
        topicViewModel = new ViewModelProvider.AndroidViewModelFactory(application).create(TopicViewModel.class);
        publisher = new MutableLiveData<>();
        controlAutoDriving = new MutableLiveData<>();
        pubTopicNameMap = new HashMap<>();

        topicViewModel.getAllTopics().observeForever(topics -> {
            for (Topic topic : topics) {
                if (topic.getMessage().getType().equals(TopicType.PUB)) {
                    pubTopicNameMap.put(topic.getFuncName(), topic.getMessage().getName());
                } else if (topic.getMessage().getType().equals(TopicType.GOAL) || topic.getMessage().getType().equals(TopicType.CALL)) {
                    pubTopicNameMap.put(topic.getFuncName(), topic.getMessage().getName());
                }
            }
        });

        mqttPublisher = new MutableLiveData<>();
    }

    public void publishTopic(String widgetName, RosMessage rosMessage) {
        publisher.postValue(new Pair<>(pubTopicNameMap.get(widgetName), new Pair<>(TopicType.PUB, rosMessage)));
    }

    public void publishCall(String widgetName, RosMessage rosMessage) {
        publisher.postValue(new Pair<>(pubTopicNameMap.get(widgetName) + MessageType.REQUEST.getType(),
                new Pair<>(TopicType.CALL, rosMessage)));
    }

    public void publishGoal(String widgetName, RosMessage rosMessage) {
        publisher.postValue(new Pair<>(pubTopicNameMap.get(widgetName) + MessageType.REQUEST.getType(),
                new Pair<>(TopicType.GOAL, rosMessage)));
    }

    public void publishDefaultMqttTopic(String topicName, String data) {
        mqttPublisher.postValue(new Pair<>(topicName, data));
    }

    public LiveData<Pair<String, Pair<String, RosMessage>>> getPublisher() {
        return publisher;
    }

    public MutableLiveData<Pair<String, String>> getMqttPublisher() {
        return mqttPublisher;
    }

    public void startAutoDriving() {
        controlAutoDriving.postValue(true);
    }

    public void stopAutoDriving() {
        controlAutoDriving.postValue(false);
    }

    public LiveData<Boolean> getAutoDrivingController() {
        return controlAutoDriving;
    }
}
