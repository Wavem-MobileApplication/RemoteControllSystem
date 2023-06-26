package com.example.remotecontrollsystem.ui.view.list.pose;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewHolder;
import com.example.remotecontrollsystem.databinding.ListItemPoseTreeBinding;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;

public class PoseTreeViewHolder extends TreeViewHolder {
    private ListItemPoseTreeBinding b;

    public PoseTreeViewHolder(@NonNull View itemView) {
        super(itemView);
        b = ListItemPoseTreeBinding.bind(itemView);
    }

    @Override
    public void bindTreeNode(TreeNode node) {
        super.bindTreeNode(node);

        PoseTreeNode poseNode = (PoseTreeNode) node;

        b.etPoseTreeX.setText(String.valueOf(poseNode.getPose().getPosition().getX()));
        b.etPoseTreeY.setText(String.valueOf(poseNode.getPose().getPosition().getY()));

        b.etPoseTreeX.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Pose pose = poseNode.getPose();
                    pose.getPosition().setX(Double.parseDouble(s.toString()));
                    poseNode.setPose(pose);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        b.etPoseTreeY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Pose pose = poseNode.getPose();
                    pose.getPosition().setY(Double.parseDouble(s.toString()));
                    poseNode.setPose(pose);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
