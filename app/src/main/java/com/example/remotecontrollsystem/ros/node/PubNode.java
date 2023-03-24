package com.example.remotecontrollsystem.ros.node;

import org.ros2.rcljava.node.BaseComposableNode;

public class PubNode extends BaseComposableNode {
    private static PubNode instance;

    public static PubNode getInstance() {
        if (instance == null) {
            instance = new PubNode("rcs_pubnode");
        }
        return instance;
    }

    public PubNode(String s) {
        super(s);
    }
}
