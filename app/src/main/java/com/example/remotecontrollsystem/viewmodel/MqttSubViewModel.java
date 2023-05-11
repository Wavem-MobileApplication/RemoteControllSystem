package com.example.remotecontrollsystem.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.utils.MessageType;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.viewmodel.manager.ViewModelManager;

import java.util.HashMap;

public class MqttSubViewModel extends AndroidViewModel {
    private HashMap<String, MutableLiveData<RosMessage>> rosMessagesMap; // Key = WidgetName

    public MqttSubViewModel(@NonNull Application application) {
        super(application);
        rosMessagesMap = new ViewModelManager().getSubRosMessageMap();
    }

    public void updateMqttData(String funcName, RosMessage rosMessage) {
        rosMessagesMap.get(funcName).postValue(rosMessage);
    }

    public LiveData<RosMessage> getTopicLiveData(WidgetType widgetType) {
        Log.d("WidgetType", widgetType.getType());
        return rosMessagesMap.get(widgetType.getType());
    }

    public LiveData<RosMessage> getFeedbackLiveData(WidgetType widgetType) {
        return rosMessagesMap.get(widgetType.getType() + MessageType.FEEDBACK.getType());
    }

    public LiveData<RosMessage> getResponseLiveData(WidgetType widgetType) {
        return rosMessagesMap.get(widgetType.getType() + MessageType.RESPONSE.getType());
    }
}
