package com.example.remotecontrollsystem.mqtt.manager;

import com.example.remotecontrollsystem.mqtt.msgs.BatteryState;
import com.example.remotecontrollsystem.mqtt.msgs.ControlHardware;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Request;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.LaserScan;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_FeedBack;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_Request;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_Response;
import com.example.remotecontrollsystem.mqtt.msgs.OccupancyGrid;
import com.example.remotecontrollsystem.mqtt.msgs.Odometry;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.msgs.TFMessage;
import com.example.remotecontrollsystem.mqtt.msgs.Twist;
import com.example.remotecontrollsystem.mqtt.utils.MessageType;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    public static Map<String, Class<? extends RosMessage>> classMap;

    static {
        classMap = new HashMap<>();

        classMap.put(WidgetType.MAP.getType(), OccupancyGrid.class);
        classMap.put(WidgetType.ROBOT_POSE.getType(), Pose.class);
        classMap.put(WidgetType.LASER_SCAN.getType(), LaserScan.class);
        classMap.put(WidgetType.TF.getType(), TFMessage.class);
        classMap.put(WidgetType.TF_STATIC.getType(), TFMessage.class);
        classMap.put(WidgetType.CMD_VEL_PUB.getType(), Twist.class);
        classMap.put(WidgetType.CMD_VEL_SUB.getType(), Twist.class);
        classMap.put(WidgetType.ODOM.getType(), Odometry.class);
//        classMap.put(WidgetType.GLOBAL_PLAN.getType(), Path.class);
//        classMap.put(WidgetType.LOCAL_PLAN.getType(), Path.class);
//        classMap.put(WidgetType.INITIAL_POSE.getType(), PoseWithCovarianceStamped.class);
        classMap.put(WidgetType.CONTROL_HARD_WARE.getType(), ControlHardware.class);
        classMap.put(WidgetType.BATTERY_STATE.getType(), BatteryState.class);
        classMap.put(
                WidgetType.NAVIGATE_TO_POSE.getType() + MessageType.REQUEST.getType(),
                NavigateToPose_Request.class);
        classMap.put(
                WidgetType.NAVIGATE_TO_POSE.getType() + MessageType.FEEDBACK.getType(),
                NavigateToPose_FeedBack.class);
        classMap.put(
                WidgetType.NAVIGATE_TO_POSE.getType() + MessageType.RESPONSE.getType(),
                NavigateToPose_Response.class);
        classMap.put(
                WidgetType.GET_MAP.getType() + MessageType.REQUEST.getType(),
                GetMap_Request.class);
        classMap.put(
                WidgetType.GET_MAP.getType() + MessageType.RESPONSE.getType(),
                GetMap_Response.class);
    }

    public static Class<? extends RosMessage> getMessageClassFromWidgetType(String widgetType) {
        return classMap.get(widgetType);
    }
}
