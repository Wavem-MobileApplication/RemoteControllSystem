package com.example.remotecontrollsystem.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;

import java.util.HashMap;

public class MqttSubViewModel extends ViewModel {
    private HashMap<String, MutableLiveData<RosMessage>> rosMessagesMap;

    public MqttSubViewModel(HashMap<String, MutableLiveData<RosMessage>> rosMessagesMap) {
        this.rosMessagesMap = rosMessagesMap;
    }
}
