package com.example.remotecontrollsystem.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConnectionViewModel extends ViewModel {
    private MutableLiveData<String> mqttUrl;
    private MutableLiveData<String> rtspFrontUrl;
    private MutableLiveData<String> rtspRearUrl;

    public ConnectionViewModel() {
        mqttUrl = new MutableLiveData<>();
        rtspFrontUrl = new MutableLiveData<>();
        rtspRearUrl = new MutableLiveData<>();
    }

    public void changeMqttUrl(String url) {
        mqttUrl.postValue(url);
    }

    public MutableLiveData<String> getMqttUrl() {
        return mqttUrl;
    }

    public void changeRtspFrontUrl(String url) {
        rtspFrontUrl.postValue(url);
    }

    public MutableLiveData<String> getRtspFrontUrl() {
        return rtspFrontUrl;
    }

    public void changeRtspRearUrl(String url) {
        rtspRearUrl.postValue(url);
    }

    public MutableLiveData<String> getRtspRearUrl() {
        return rtspRearUrl;
    }
}
