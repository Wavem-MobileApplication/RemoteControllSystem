package com.example.remotecontrollsystem.ui.view.list.pose;

import com.amrdeveloper.treeview.TreeNode;
import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;

public class PoseTreeNode extends TreeNode {

    public PoseTreeNode(Pose pose) {
        super(pose, R.layout.list_item_pose_tree);
    }

    public void setPose(Pose pose) {
        setValue(pose);
    }

    public Pose getPose() {
        return (Pose) getValue();
    }
}
