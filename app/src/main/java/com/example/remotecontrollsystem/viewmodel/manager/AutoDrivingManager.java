package com.example.remotecontrollsystem.viewmodel.manager;

import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.mqtt.data.RosObserver;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_FeedBack;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_Request;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_Response;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.PoseStamped;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.viewmodel.MqttPubViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;
import com.example.remotecontrollsystem.viewmodel.StatusViewModel;

public class AutoDrivingManager {
    private Route route;
    private double startX, startY;

    private double totalDistance;
    private double poseProgression;
    private double waypointProgression;

    private int curWaypointSize;
    private int poseCount = 0;
    private int waypointCount = 0;

    private AutoDrivingProgression autoDrivingProgression;

    private MqttPubViewModel mqttPubViewModel;
    private MqttSubViewModel mqttSubViewModel;
    private StatusViewModel statusViewModel;

    public AutoDrivingManager(MqttSubViewModel mqttSubViewModel, MqttPubViewModel mqttPubViewModel, StatusViewModel statusViewModel) {
        this.mqttPubViewModel = mqttPubViewModel;
        this.mqttSubViewModel = mqttSubViewModel;
        this.statusViewModel = statusViewModel;
        autoDrivingProgression = new AutoDrivingProgression();
    }

    public void setRoute(Route route) {
        this.route = route;
        clearData();
    }

    public void startAutoDriving() {
/*        PoseStamped poseStamped = new PoseStamped();
        poseStamped.getHeader().setFrame_id("map");
        poseStamped.setPose(route.getWaypointList().get(waypointCount).getPoseList().get(poseCount));

        NavigateToPose_Request goal = new NavigateToPose_Request();
        goal.setPose(poseStamped);

        sendGoal(goal);*/
    }

    public void stopAutoDriving() {

    }

    private void sendGoal(NavigateToPose_Request goal) {
        double desX = goal.getPose().getPose().getPosition().getX();
        double desY = goal.getPose().getPose().getPosition().getY();
        startX = ((Pose) mqttSubViewModel.getTopicLiveData(WidgetType.ROBOT_POSE).getValue()).getPosition().getX();
        startY = ((Pose) mqttSubViewModel.getTopicLiveData(WidgetType.ROBOT_POSE).getValue()).getPosition().getY();

        curWaypointSize = route.getWaypointList().get(waypointCount).getPoseList().size();

        totalDistance = Math.sqrt(Math.pow(desX - startX, 2) + Math.pow(desY - startY, 2));

        mqttPubViewModel.publishGoal(WidgetType.NAVIGATE_TO_POSE.getType(), goal);
    }

    public final RosObserver<NavigateToPose_FeedBack> feedbackObserver = new RosObserver<>(NavigateToPose_FeedBack.class) {
        @Override
        public void onChange(NavigateToPose_FeedBack feedBack) {
            poseProgression = feedBack.getDistance_remaining() / totalDistance;
            waypointProgression = (poseCount + poseProgression) / curWaypointSize;

            autoDrivingProgression.setPercent(waypointProgression);
        }
    };

    public final RosObserver<NavigateToPose_Response> responseObserver = new RosObserver<>(NavigateToPose_Response.class) {
        @Override
        public void onChange(NavigateToPose_Response response) {
            poseCount++;
            if (poseCount >= route.getWaypointList().get(waypointCount).getPoseList().size()) {
                poseCount = 0;
                waypointCount++;
                autoDrivingProgression.setClearedNum(waypointCount);
            }
            if (waypointCount >= route.getWaypointList().size()) {
                waypointCount = 0;
                poseCount = 0;
            }

            if (waypointCount != 0 && poseCount != 0) {
                PoseStamped poseStamped = new PoseStamped();
                poseStamped.getHeader().setFrame_id("map");
                poseStamped.setPose(route.getWaypointList().get(waypointCount).getPoseList().get(poseCount));

                NavigateToPose_Request goal = new NavigateToPose_Request();
                goal.setPose(poseStamped);

                sendGoal(goal);
            }
        }
    };

    private void clearData() {
        poseCount = 0;
        waypointCount = 0;
        poseProgression = 0;
        waypointProgression = 0;
        totalDistance = 0;
    }
}
