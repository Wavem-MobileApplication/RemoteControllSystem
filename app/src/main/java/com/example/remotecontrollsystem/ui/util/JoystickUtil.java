package com.example.remotecontrollsystem.ui.util;

import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.msgs.Twist;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

public class JoystickUtil {
    private static JoystickUtil instance;

    private JoystickUtil() {
    }

    public static JoystickUtil getInstance() {
        if (instance == null) {
            instance = new JoystickUtil();
        }
        return instance;
    }

    private Twist twist = new Twist();

    private void publishCmdVel() {
        Mqtt.getInstance().publishMessage(WidgetType.CMD_VEL_PUB.getType(), twist, 0, false);
    }

    public void publishLinearVel(float y) {
        twist.getLinear().setX(y);
        publishCmdVel();
    }

    public void publishAngularVel(float x) {
        twist.getAngular().setZ(-x);
        publishCmdVel();
    }
}
