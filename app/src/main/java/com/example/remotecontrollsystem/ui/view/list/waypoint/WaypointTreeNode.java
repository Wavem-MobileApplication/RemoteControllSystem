package com.example.remotecontrollsystem.ui.view.list.waypoint;

import android.util.Log;

import com.amrdeveloper.treeview.TreeNode;
import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.ui.view.list.pose.PoseTreeNode;

public class WaypointTreeNode extends TreeNode {

    public WaypointTreeNode(Waypoint waypoint) {
        super(waypoint, R.layout.list_item_waypoint_tree);

        findChildPoses(waypoint);
    }

    private void findChildPoses(Waypoint waypoint) {
        for (Pose pose : waypoint.getPoseList()) {
            PoseTreeNode poseTreeNode = new PoseTreeNode(pose);
            addChild(poseTreeNode);
        }
    }

    public void changeName(String name) {
        Waypoint waypoint = getWaypoint();
        waypoint.setName(name);

        setWaypoint(waypoint);
    }

    public void setWaypoint(Waypoint waypoint) {
        setValue(waypoint);
    }

    public Waypoint getWaypoint() {
        return (Waypoint) getValue();
    }
}
