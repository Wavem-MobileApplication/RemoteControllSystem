package com.example.remotecontrollsystem.ui.util;

import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.listener.FeedbackListener;
import com.example.remotecontrollsystem.mqtt.listener.ResponseListener;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_FeedBack;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_Response;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SendGoal {
    private static SendGoal instance;
    private RosMessageDefinition messageDefinition;
    private Route route;
    private Waypoint waypoint;
    private int waypointNum = 0;
    private int poseNum = 0;
    private boolean isDrivable;

    public static SendGoal getInstance() {
        if (instance == null) {
            instance = new SendGoal();
        }
        return instance;
    }

    public void startRouteDriving(RosMessageDefinition messageDefinition, Route route) {
        this.messageDefinition = messageDefinition;
        this.route = route;
        this.waypoint = route.getWaypointList().get(waypointNum);
        this.isDrivable = true;

        Pose pose = waypoint.getPoseList().get(poseNum);
    }

    private void sendGoal(RosMessageDefinition messageDefinition, String pose) {
//        Mqtt.getInstance().sendActionRequest(messageDefinition, pose, feedbackListener, responseListener);
    }

    private FeedbackListener feedbackListener = new FeedbackListener() {
        @Override
        public void onReceive(String message) {
            Type type = new TypeToken<NavigateToPose_FeedBack>() {}.getType();
            NavigateToPose_FeedBack feedBack = new Gson().fromJson(message, type);

            // TODO -> waypoint 데이터 업데이트 전 완료된 waypoint 반환 하여 UI 업데이트 하게끔 추가.
        }
    };

    private ResponseListener responseListener = new ResponseListener() {
        @Override
        public void onReceive(String message) {
            Type type = new TypeToken<NavigateToPose_Response>() {}.getType();
            NavigateToPose_Response response = new Gson().fromJson(message, type);

            poseNum++;
            if ((poseNum >= waypoint.getPoseList().size())) {
                poseNum = 0;
                waypointNum++;

                // TODO -> waypoint 데이터 업데이트 전 완료된 waypoint 반환 하여 UI 업데이트 하게끔 추가.
            }

            if (waypointNum >= route.getWaypointList().size()) {
                waypointNum = 0;
                isDrivable = false;
                // TODO -> 모든 주행 경로 완료 알림
            } else {
                waypoint = route.getWaypointList().get(waypointNum);

                Pose pose = waypoint.getPoseList().get(poseNum);
                String poseMsg = new Gson().toJson(pose);
                sendGoal(messageDefinition, poseMsg);
            }
        }
    };
}
