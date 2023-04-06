package com.example.remotecontrollsystem.ui.util;

import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.listener.FeedbackListener;
import com.example.remotecontrollsystem.mqtt.listener.ResponseListener;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;

public class SendGoal {
    private static SendGoal instance;
    private Route route;
    private Waypoint waypoint;
    private int waypointNum = 0;
    private int poseNum = 0;

    public static SendGoal getInstance() {
        if (instance == null) {
            instance = new SendGoal();
        }
        return instance;
    }

    public void startRouteDriving(RosMessageDefinition messageDefinition, Route route) {
        this.route = route;
        this.waypoint = route.getWaypointList().get(waypointNum);
        Pose pose = waypoint.getPoseList().get(poseNum);
        sendGoal();
    }

    private void sendGoal(RosMessageDefinition messageDefinition, String pose) {
        Mqtt.getInstance().sendActionRequest(messageDefinition, "", feedbackListener, responseListener);
    }

    private FeedbackListener feedbackListener = new FeedbackListener() {
        @Override
        public void onReceive(String message) {

        }
    };

    private ResponseListener responseListener = new ResponseListener() {
        @Override
        public void onReceive(String message) {
            poseNum++;
        }
    };
}
