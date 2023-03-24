package com.example.remotecontrollsystem.ros.node;

import com.example.remotecontrollsystem.ros.data.MapInfo;

import org.ros2.rcljava.node.BaseComposableNode;

public class SubNode extends BaseComposableNode {
    private static SubNode instance;

    public static SubNode getInstance() {
        if (instance == null) {
            instance = new SubNode("rcs_subnode");
        }
        return instance;
    }

    private SubNode(String s) {
        super(s);

        MapInfo.getInstance(this);
    }
}
