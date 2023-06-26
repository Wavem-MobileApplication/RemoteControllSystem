package com.example.remotecontrollsystem.ui.view.list.waypoint;

import android.view.View;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewHolder;
import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.ListItemWaypointTreeBinding;
import com.example.remotecontrollsystem.model.entity.Waypoint;

public class WaypointTreeViewHolder extends TreeViewHolder {
    private ListItemWaypointTreeBinding b;
    public WaypointTreeViewHolder(@NonNull View itemView) {
        super(itemView);
        b = ListItemWaypointTreeBinding.bind(itemView);
    }

    @Override
    public void bindTreeNode(TreeNode node) {
        super.bindTreeNode(node);

        WaypointTreeNode waypointNode = (WaypointTreeNode) node;
        Waypoint waypoint = (Waypoint) waypointNode.getValue();
        String waypointName = waypoint.getName();

        b.tvWaypointTreeName.setText(waypointName);

        if (node.getChildren().isEmpty()) {
            b.waypointStateIcon.setVisibility(View.INVISIBLE);
        } else {
            b.waypointStateIcon.setVisibility(View.VISIBLE);
            int stateIcon = node.isExpanded() ? R.drawable.icon_arrow_down : R.drawable.icon_arrow_right;
            b.waypointStateIcon.setImageResource(stateIcon);
        }
    }
}
