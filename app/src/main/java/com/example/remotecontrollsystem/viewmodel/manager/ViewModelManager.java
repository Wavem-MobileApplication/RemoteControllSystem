package com.example.remotecontrollsystem.viewmodel.manager;

import androidx.lifecycle.MutableLiveData;

import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.utils.MessageType;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

import java.util.HashMap;

public class ViewModelManager {

    public HashMap<String, MutableLiveData<RosMessage>> getSubRosMessageMap() {
        HashMap<String, MutableLiveData<RosMessage>> rosMessageMap = new HashMap<>();

        rosMessageMap.put(WidgetType.MAP.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.ROBOT_POSE.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.LASER_SCAN.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.TF.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.TF_STATIC.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.CMD_VEL_SUB.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.ODOM.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.GLOBAL_PLAN.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.LOCAL_PLAN.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.BATTERY_STATE.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.NAVIGATE_TO_POSE.getType() + MessageType.FEEDBACK.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.NAVIGATE_TO_POSE.getType() + MessageType.RESPONSE.getType(), new MutableLiveData<>());
        rosMessageMap.put(WidgetType.GET_MAP.getType() + MessageType.RESPONSE.getType(), new MutableLiveData<>());

        return rosMessageMap;
    }
}
