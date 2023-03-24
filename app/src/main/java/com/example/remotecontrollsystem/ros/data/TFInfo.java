package com.example.remotecontrollsystem.ros.data;

import com.example.remotecontrollsystem.ros.node.SubNode;

import org.ros2.rcljava.subscription.Subscription;

import java.util.HashMap;

import geometry_msgs.msg.TransformStamped;
import tf2_msgs.msg.TFMessage;

public class TFInfo {
    private static TFInfo instance;
    private SubNode subNode;
    private Subscription<TFMessage> subscription;
    private HashMap<String, TransformStamped> tfMap;

    public static TFInfo getInstance() {
        return instance;
    }

    public static TFInfo getInstance(SubNode subNode) {
        if (instance == null) {
            instance = new TFInfo(subNode);
        }
        return instance;
    }

    private TFInfo(SubNode subNode) {
        this.subNode = subNode;
        tfMap = new HashMap<>();
    }

    public void subscribeTF(String topicName) {
        if (subscription != null) {
            subNode.getNode().removeSubscription(subscription);
        }

        subscription = subNode.getNode().createSubscription(TFMessage.class, topicName, tfMessage -> {
           tfMap.clear();
           for (TransformStamped tf : tfMessage.getTransforms()) {
               tfMap.put(tf.getChildFrameId(), tf);
           }
        });
    }

    public HashMap<String, TransformStamped> getAllTF() {
        return tfMap;
    }

    public TransformStamped getTF(String frameId) {
        return tfMap.get(frameId);
    }
}
