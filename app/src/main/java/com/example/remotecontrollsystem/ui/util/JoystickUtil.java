package com.example.remotecontrollsystem.ui.util;

import com.example.remotecontrollsystem.model.utils.DataManager;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.msgs.Twist;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

public class JoystickUtil {
    private static JoystickUtil instance;
    private TopicViewModel topicViewModel;
    private String topicName;

    private JoystickUtil() {
        topicViewModel = DataManager.getInstance().getTopicViewModel();
        topicViewModel.getTopic(WidgetType.CMD_VEL_PUB.getType()).observe(DataManager.getInstance().getActivity(),
                topic -> topicName = topic.getMessage().getName());
    }

    public static JoystickUtil getInstance() {
        if (instance == null) {
            instance = new JoystickUtil();
        }
        return instance;
    }

    private Twist twist = new Twist();

    private void publishCmdVel() {
        if (topicName != null && !topicName.isEmpty()) {
//            Mqtt.getInstance().publishMqttMessage(topicName, twist, 0, false);
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
}
