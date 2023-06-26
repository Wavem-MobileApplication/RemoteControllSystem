package com.example.remotecontrollsystem.ui.view.list.route;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewHolder;
import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.ListItemRouteTreeBinding;
import com.example.remotecontrollsystem.model.entity.Route;

import org.w3c.dom.Text;

public class RouteTreeViewHolder extends TreeViewHolder {
    private ListItemRouteTreeBinding b;

    private OnSaveButtonClickListener saveButtonClickListener;
    private OnDeleteButtonClickListener deleteButtonClickListener;

    public RouteTreeViewHolder(@NonNull View itemView) {
        super(itemView);
        b = ListItemRouteTreeBinding.bind(itemView);
    }

    @Override
    public void bindTreeNode(TreeNode node) {
        super.bindTreeNode(node);

        RouteTreeNode routeNode = (RouteTreeNode) node;
        b.tvRouteTreeName.setText(routeNode.getRoute().getName());

        if (node.getChildren().isEmpty()) {
            b.routeStateIcon.setVisibility(View.INVISIBLE);
        } else {
            b.routeStateIcon.setVisibility(View.VISIBLE);
            int stateIcon = node.isExpanded() ? R.drawable.icon_arrow_down : R.drawable.icon_arrow_right;
            b.routeStateIcon.setImageResource(stateIcon);
        }

        b.tvRouteTreeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                routeNode.changeName(s.toString());
            }
        });

        b.btnSaveRouteTree.setOnClickListener(v -> {
            if (saveButtonClickListener != null) {
                saveButtonClickListener.onClick(routeNode.getRoute());
            }
        });

        b.btnDeleteRouteTree.setOnClickListener(v -> {
            if (deleteButtonClickListener != null) {
                deleteButtonClickListener.onClick(routeNode.getRoute());
            }
        });
    }

    public void setSaveButtonClickListener(OnSaveButtonClickListener saveButtonClickListener) {
        this.saveButtonClickListener = saveButtonClickListener;
    }

    public void setDeleteButtonClickListener(OnDeleteButtonClickListener deleteButtonClickListener) {
        this.deleteButtonClickListener = deleteButtonClickListener;
    }

    public interface OnSaveButtonClickListener {
        void onClick(Route route);
    }

    public interface OnDeleteButtonClickListener {
        void onClick(Route route);
    }
}
