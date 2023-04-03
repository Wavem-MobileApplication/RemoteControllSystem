package com.example.remotecontrollsystem.model.utils;

import android.util.Log;

import com.example.remotecontrollsystem.model.dao.TopicDao;
import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.Constants;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InitializeDatabase {

    public void settingDefaultTopics(TopicDao topicDao) {
        Disposable backgroundTask = Observable.fromCallable(() -> {
//        topicDao.insert(getDefaultMap());
//        topicDao.insert(getDefaultRobotPose());
                    topicDao.insert(getDefaultScan());
                    topicDao.insert(getDefaultTF());
                    topicDao.insert(getDefaultTFStatic());
                    topicDao.insert(getDefaultCmdVelSub());
                    topicDao.insert(getDefaultOdom());
                    topicDao.insert(getDefaultGlobalPlan());
                    topicDao.insert(getDefaultLocalPlan());


                    topicDao.insert(getDefaultCmdVelPub());
                    topicDao.insert(getDefaultInitialPose());
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    Log.d("Finish", "Insert Main Topics");
                });
    }

    private Topic getDefaultMap() {
        Topic topic = new Topic(Constants.MAP);
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/map", "nav_msgs/msg/OccupancyGrid", 1, true);
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultRobotPose() {
        Topic topic = new Topic(Constants.ROBOT_POSE);
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/robot_pose", "geometry_msgs/msg/Pose");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultScan() {
        Topic topic = new Topic(Constants.LASER_SCAN);
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/scan", "sensor_msgs/msg/LaserScan");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultTFStatic() {
        Topic topic = new Topic(Constants.TF_STATIC);
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/tf_static", "tf2_msgs/msg/TFMessage");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultTF() {
        Topic topic = new Topic(Constants.TF);
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/tf", "tf2_msgs/msg/TFMessage");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultCmdVelSub() {
        Topic topic = new Topic(Constants.CMD_VEL_SUB);
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/cmd_vel", "geometry_msgs/msg/Twist");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultCmdVelPub() {
        Topic topic = new Topic(Constants.CMD_VEL_PUB);
        RosMessageDefinition msg =
                RosMessageDefinition.PUB("/cmd_vel", "geometry_msgs/msg/Twist");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultOdom() {
        Topic topic = new Topic(Constants.ODOM);
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/odom", "nav_msgs/msg/Odometry");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultGlobalPlan() {
        Topic topic = new Topic(Constants.GLOBAL_PLAN);
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/transformed_global_plan", "nav_msgs/msg/Path");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultLocalPlan() {
        Topic topic = new Topic(Constants.LOCAL_PLAN);
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/local_plan", "nav_msgs/msg/Path");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultInitialPose() {
        Topic topic = new Topic(Constants.INITIAL_POSE);
        RosMessageDefinition msg =
                RosMessageDefinition.PUB("/initialpose", "geometry_msgs/msg/PoseWithCovarianceStamped");
        topic.setMessage(msg);

        return topic;
    }
}
