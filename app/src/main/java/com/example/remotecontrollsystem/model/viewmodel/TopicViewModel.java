package com.example.remotecontrollsystem.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.remotecontrollsystem.model.DataStorage;
import com.example.remotecontrollsystem.model.entity.Topic;

import java.util.List;

public class TopicViewModel extends AndroidViewModel {
    private DataStorage dataStorage;

    public TopicViewModel(@NonNull Application application) {
        super(application);
        dataStorage = DataStorage.getInstance(application);
    }

    public void updateTopic(Topic topic) {
        dataStorage.updateTopic(topic);
    }

    public LiveData<Topic> getTopic(String funcName) {
        return dataStorage.getTopic(funcName);
    }

    public LiveData<List<Topic>> getAllTopics() {
        return dataStorage.getAllTopics();
    }
}
