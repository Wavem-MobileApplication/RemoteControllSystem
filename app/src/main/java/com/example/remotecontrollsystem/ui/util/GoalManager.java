package com.example.remotecontrollsystem.ui.util;

import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_FeedBack;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_Request;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_Response;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.google.gson.Gson;

public class GoalManager {
    private static GoalManager instance;
    private RosMessageDefinition messageDefinition;
    private Route route;
    private Waypoint waypoint;
    private int waypointNum = 0;
    private int poseNum = 0;
    private String goalId = "";
    private boolean isDrivable;

    private MessagePublisher feedbackPublisher;
    private MessagePublisher responsePublisher;

    public static GoalManager getInstance() {
        if (instance == null) {
            instance = new GoalManager();
        }
        return instance;
    }

    public GoalManager() {
        feedbackPublisher = Mqtt.getInstance().getMessagePublisher(
                WidgetType.NAVIGATE_TO_POSE.getType() + Mqtt.FEEDBACK);
        feedbackPublisher.attach(feedbackObserver);

        responsePublisher = Mqtt.getInstance().getMessagePublisher(
                WidgetType.NAVIGATE_TO_POSE.getType() + Mqtt.RESPONSE);
        responsePublisher.attach(responseObserver);
    }

    public void startRouteDriving(Route route) {
        if (!route.getWaypointList().isEmpty()) {
            this.route = route;
            this.waypoint = route.getWaypointList().get(waypointNum);
            this.isDrivable = true;

            NavigateToPose_Request request = new NavigateToPose_Request();
            request.getPose().setPose(waypoint.getPoseList().get(poseNum));
            sendGoal(request);
        }
    }

    private void sendGoal(NavigateToPose_Request request) {
        Mqtt.getInstance().sendRequestMessageGoal(
                WidgetType.NAVIGATE_TO_POSE.getType(), request, 1, false);
    }

    private final Observer feedbackObserver = new Observer() {
        @Override
        public void update(String message) {
            NavigateToPose_FeedBack feedBack = new Gson().fromJson(message, NavigateToPose_FeedBack.class);

            // TODO -> waypoint 데이터 업데이트 전 완료된 waypoint 반환 하여 UI 업데이트 하게끔 추가.
        }
    };

    private final Observer responseObserver = new Observer() {
        @Override
        public void update(String message) {
            NavigateToPose_Response response = new Gson().fromJson(message, NavigateToPose_Response.class);
            
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

                NavigateToPose_Request request = new NavigateToPose_Request();
                request.getPose().setPose(waypoint.getPoseList().get(poseNum));
                sendGoal(request);
            }
        }
    };

}
