package com.example.remotecontrollsystem.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.utils.MessageType;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

import java.util.HashMap;

public class MqttSubViewModel extends AndroidViewModel {
    private TopicViewModel topicViewModel;
    private HashMap<String, MutableLiveData<RosMessage>> rosMessagesMap; // Key = WidgetName

    public MqttSubViewModel(@NonNull Application application) {
        super(application);
        topicViewModel = new ViewModelProvider.AndroidViewModelFactory(application).create(TopicViewModel.class);
        rosMessagesMap = new HashMap<>();

        topicViewModel.getAllTopics().observeForever(topics -> {

            for (Topic topic : topics) {

                String key = topic.getFuncName();
                String type = topic.getMessage().getType();

                switch (type) {
                    case TopicType.SUB:
                        addNewMutableLiveDataToRosMessageMap(key);
                        break;
                    case TopicType.CALL:
                        addNewMutableLiveDataToRosMessageMap(key + MessageType.RESPONSE.getType());
                        break;
                    case TopicType.GOAL:
                        addNewMutableLiveDataToRosMessageMap(key + MessageType.FEEDBACK.getType());
                        addNewMutableLiveDataToRosMessageMap(key + MessageType.RESPONSE.getType());
                        break;
                }

            }

        });
    }

    private void addNewMutableLiveDataToRosMessageMap(String key) {
        if (rosMessagesMap.get(key) == null) {
            rosMessagesMap.put(key, new MutableLiveData<>());
        }
    }

    public void updateMqttData(String funcName, RosMessage rosMessage) {
        rosMessagesMap.get(funcName).postValue(rosMessage);
    }

    public LiveData<RosMessage> getTopicLiveData(WidgetType widgetType) {
        return rosMessagesMap.get(widgetType.getType());
    }

    public LiveData<RosMessage> getFeedbackLiveData(WidgetType widgetType) {
        return rosMessagesMap.get(widgetType.getType() + MessageType.FEEDBACK.getType());
    }

    public LiveData<RosMessage> getResponseLiveData(WidgetType widgetType) {
        return rosMessagesMap.get(widgetType.getType() + MessageType.RESPONSE.getType());
    }
}
