package com.example.remotecontrollsystem.viewmodel;

import android.app.Application;
import android.util.Log;

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

import java.util.HashMap;
import java.util.List;

public class MqttSubViewModel extends AndroidViewModel {
    private static final String TAG = MqttSubViewModel.class.getSimpleName();
    private final HashMap<String, MutableLiveData<RosMessage>> rosMessagePublisherMap;


    public MqttSubViewModel(@NonNull Application application) {
        super(application);
        TopicViewModel topicViewModel = new ViewModelProvider.AndroidViewModelFactory(application).create(TopicViewModel.class);
        rosMessagePublisherMap = new HashMap<>();

        topicViewModel.getAllTopics().observeForever(this::initRosMessagePublisherMap);
    }


    // Initialize rosMessagePublisherMap
    private void initRosMessagePublisherMap(List<Topic> topicList) {
        // Update MessagePublishers
        for (Topic topic : topicList) {
            String type = topic.getMessage().getType();
            String funcName = topic.getFuncName();

            // Add Message Publishers
            switch (type) {
                case TopicType.SUB:
                    createRosMessagePublisher(funcName);
                    break;
                case TopicType.CALL:
                    createRosMessagePublisher(funcName + MessageType.RESPONSE.getType());
                    break;
                case TopicType.GOAL:
                    createRosMessagePublisher(funcName + MessageType.FEEDBACK.getType());
                    createRosMessagePublisher(funcName + MessageType.RESPONSE.getType());
                    break;
            }
        }
    }

    private void createRosMessagePublisher(String widgetName) {
        if (rosMessagePublisherMap.get(widgetName) == null) {
            Log.d(TAG, "Add Publisher -> " + widgetName);
            rosMessagePublisherMap.put(widgetName, new MutableLiveData<>());
        }
    }

    // About LiveData
    public void updateLiveData(String widgetName, RosMessage data) {
        rosMessagePublisherMap.get(widgetName).postValue(data);
    }

    public LiveData<RosMessage> getRosMessagePublisher(String widgetName) {
        return rosMessagePublisherMap.get(widgetName);
    }
}
