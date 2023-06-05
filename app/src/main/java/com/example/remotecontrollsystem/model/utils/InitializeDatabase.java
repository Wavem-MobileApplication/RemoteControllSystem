package com.example.remotecontrollsystem.model.utils;

import android.util.Log;

import com.example.remotecontrollsystem.model.dao.TopicDao;
import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InitializeDatabase {

    public void settingDefaultTopics(TopicDao topicDao) {
        Disposable backgroundTask = Observable.fromCallable(() -> {
                    topicDao.insert(getDefaultMap());
                    topicDao.insert(getDefaultRobotPose());
                    topicDao.insert(getDefaultScan());
                    topicDao.insert(getDefaultTF());
                    topicDao.insert(getDefaultTFStatic());
                    topicDao.insert(getDefaultCmdVelSub());
                    topicDao.insert(getDefaultOdom());
                    topicDao.insert(getDefaultGlobalPlan());
                    topicDao.insert(getDefaultLocalPlan());
                    topicDao.insert(getDefaultBatteryState());

                    topicDao.insert(getDefaultCmdVelPub());
                    topicDao.insert(getDefaultInitialPose());
                    topicDao.insert(getDefaultControlHardWare());

                    topicDao.insert(getDefaultNavigateToPose());

                    topicDao.insert(getDefaultGetMap());
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    Log.d("Finish", "Insert Main Topics");
                });
    }

    private Topic getDefaultMap() {
        Topic topic = new Topic(WidgetType.MAP.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/map", "nav_msgs/msg/OccupancyGrid", 0, false);
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultRobotPose() {
        Topic topic = new Topic(WidgetType.ROBOT_POSE.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/robot_pose", "geometry_msgs/msg/Pose");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultScan() {
        Topic topic = new Topic(WidgetType.LASER_SCAN.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/scan", "sensor_msgs/msg/LaserScan");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultTFStatic() {
        Topic topic = new Topic(WidgetType.TF_STATIC.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/tf_static", "tf2_msgs/msg/TFMessage");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultTF() {
        Topic topic = new Topic(WidgetType.TF.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/tf", "tf2_msgs/msg/TFMessage");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultCmdVelSub() {
        Topic topic = new Topic(WidgetType.CMD_VEL_SUB.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/cmd_vel", "geometry_msgs/msg/Twist");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultCmdVelPub() {
        Topic topic = new Topic(WidgetType.CMD_VEL_PUB.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.PUB("/cmd_vel", "geometry_msgs/msg/Twist");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultOdom() {
        Topic topic = new Topic(WidgetType.ODOM.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/odom", "nav_msgs/msg/Odometry");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultGlobalPlan() {
        Topic topic = new Topic(WidgetType.GLOBAL_PLAN.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/transformed_global_plan", "nav_msgs/msg/Path");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultLocalPlan() {
        Topic topic = new Topic(WidgetType.LOCAL_PLAN.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/local_plan", "nav_msgs/msg/Path");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultBatteryState() {
        Topic topic = new Topic(WidgetType.BATTERY_STATE.getType());
        RosMessageDefinition msg = RosMessageDefinition.SUB("/battery/state", "sensor_msgs/msg/BatteryState");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultInitialPose() {
        Topic topic = new Topic(WidgetType.INITIAL_POSE.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.PUB("/initialpose", "geometry_msgs/msg/PoseWithCovarianceStamped");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultControlHardWare() {
        Topic topic = new Topic(WidgetType.CONTROL_HARD_WARE.getType());
        RosMessageDefinition msg =
                RosMessageDefinition.PUB("/can/control_hardware", "can_msgs/msg/ControlHardware");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultNavigateToPose() {
        Topic topic = new Topic(WidgetType.NAVIGATE_TO_POSE.getType());
        RosMessageDefinition msg = RosMessageDefinition.GOAL(
                "/navigate_to_pose", "nav2_msgs/action/NavigateToPose",
                "nav2_msgs/action/NavigateToPose_Goal", "nav2_msgs/action/NavigateToPose_Feedback",
                "nav2_msgs/action/NavigateToPose_Response", 0, false);
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultGetMap() {
        Topic topic = new Topic(WidgetType.GET_MAP.getType());
        RosMessageDefinition msg = RosMessageDefinition.CALL("/map_server/map", "nav_msgs/srv/GetMap",
                "nav_msgs/srv/GetMap_Request", "nav_msgs/srv/GetMap_Response", 0, false);
        topic.setMessage(msg);

        return topic;
    }
}
