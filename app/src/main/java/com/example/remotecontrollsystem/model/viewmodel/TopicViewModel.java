package com.example.remotecontrollsystem.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.remotecontrollsystem.model.DataStorage;
import com.example.remotecontrollsystem.model.entity.Topic;

import java.util.List;

public class TopicViewModel extends AndroidViewModel {
    private DataStorage dataStorage;
    private MediatorLiveData<List<Topic>> allTopics;

    public TopicViewModel(@NonNull Application application) {
        super(application);
        dataStorage = DataStorage.getInstance(application);

        allTopics = new MediatorLiveData<>();
        allTopics.addSource(dataStorage.getAllTopics(), new Observer<List<Topic>>() {
            @Override
            public void onChanged(List<Topic> topics) {
                allTopics.postValue(topics);
            }
        });
    }

    public void updateTopic(Topic topic) {
        dataStorage.updateTopic(topic);
    }

    public LiveData<List<Topic>> getAllTopics() {
        return allTopics;
    }
}
