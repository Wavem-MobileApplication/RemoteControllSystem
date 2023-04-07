package com.example.remotecontrollsystem.model.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;

public class DataManager {
    private static DataManager instance;
    private AppCompatActivity activity;
    private TopicViewModel topicViewModel;

    private DataManager(AppCompatActivity activity) {
        this.activity = activity;
        topicViewModel = new ViewModelProvider(activity).get(TopicViewModel.class);
    }

    public static DataManager setInstance(AppCompatActivity activity) {
        if (instance == null) {
            instance = new DataManager(activity);
        }
        return instance;
    }

    public static DataManager getInstance() {
        return instance;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public TopicViewModel getTopicViewModel() {
        return topicViewModel;
    }
}
