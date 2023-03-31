package com.example.remotecontrollsystem.model.utils;

import com.example.remotecontrollsystem.model.dao.TopicDao;
import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;

public class InitializeDatabase {

    public void settingDefaultTopics(TopicDao topicDao) {
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
    }

    private Topic getDefaultMap() {
        Topic topic = new Topic("지도");
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/map", "nav_msgs/msg/OccupancyGrid", 1, true);
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultRobotPose() {
        Topic topic = new Topic("차량 위치");
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/robot_pose", "geometry_msgs/msg/Pose");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultScan() {
        Topic topic = new Topic("라이다 센서");
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/scan", "sensor_msgs/msg/LaserScan");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultTFStatic() {
        Topic topic = new Topic("정적 기준 좌표");
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/tf_static", "tf2_msgs/msg/TFMessage");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultTF() {
        Topic topic = new Topic("동적 기준 좌표");
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/tf", "tf2_msgs/msg/TFMessage");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultCmdVelSub() {
        Topic topic = new Topic("차량 속도");
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/cmd_vel", "geometry_msgs/msg/Twist");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultCmdVelPub() {
        Topic topic = new Topic("수동 제어");
        RosMessageDefinition msg =
                RosMessageDefinition.PUB("/cmd_vel", "geometry_msgs/msg/Twist");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultOdom() {
        Topic topic = new Topic("위치/이동 정보");
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/odom", "nav_msgs/msg/Odometry");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultGlobalPlan() {
        Topic topic = new Topic("예상 주행 경로");
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/transformed_global_plan", "nav_msgs/msg/Path");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultLocalPlan() {
        Topic topic = new Topic("목표 주행 경로");
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/local_plan", "nav_msgs/msg/Path");
        topic.setMessage(msg);

        return topic;
    }

    private Topic getDefaultInitialPose() {
        Topic topic = new Topic("차량 위치 갱신");
        RosMessageDefinition msg =
                RosMessageDefinition.SUB("/initialpose", "geometry_msgs/msg/PoseWithCovarianceStamped");
        topic.setMessage(msg);

        return topic;
    }
}
