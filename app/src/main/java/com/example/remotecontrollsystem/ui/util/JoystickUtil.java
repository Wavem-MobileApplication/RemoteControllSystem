package com.example.remotecontrollsystem.ui.util;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.msgs.Twist;
import com.example.remotecontrollsystem.mqtt.utils.Constants;
import com.example.remotecontrollsystem.mqtt.utils.TopicType;

import java.util.List;

public class JoystickUtil {
    private static JoystickUtil instance;
    private AppCompatActivity activity;
    private TopicViewModel topicViewModel;
    private String topicName;

    private JoystickUtil() {}

    public static JoystickUtil getInstance() {
        if (instance == null) {
            instance = new JoystickUtil();
        }
        return instance;
    }

    private Twist twist = new Twist();

    private void publishCmdVel() {
        if (!topicName.isEmpty()) {
            Mqtt.getInstance().publishMqttMessage(topicName, twist, 0, false);
        }
    }

    public void publishLinearVel(float y) {
        twist.getLinear().setX(y);
        publishCmdVel();
    }

    public void publishAngularVel(float x) {
        twist.getAngular().setZ(x);
        publishCmdVel();
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
        topicViewModel = new TopicViewModel(activity.getApplication());
        topicViewModel.getTopic(Constants.CMD_VEL_PUB).observe(activity, new Observer<Topic>() {
            @Override
            public void onChanged(Topic topic) {
                topicName = topic.getMessage().getName();
            }
        });
    }
}
