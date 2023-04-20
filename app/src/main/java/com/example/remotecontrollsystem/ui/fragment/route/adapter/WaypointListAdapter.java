package com.example.remotecontrollsystem.ui.fragment.route.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.ItemListWaypointBinding;
import com.example.remotecontrollsystem.model.entity.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class WaypointListAdapter extends RecyclerView.Adapter<WaypointListAdapter.ViewHolder> {
    private List<Waypoint> waypointList;

    private OnSignupClickListener signupClickListener;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;

    public WaypointListAdapter() {
        waypointList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_waypoint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Waypoint waypoint = waypointList.get(position);

        holder.binding.tvWaypointNum.setText(String.valueOf(position + 1));
        holder.binding.tvWaypointNameItem.setText(waypoint.getName());

        holder.binding.btnSignUpWaypoint.setOnClickListener(view -> {
            if (signupClickListener != null) {
                signupClickListener.onClick(holder.getAdapterPosition(), waypoint);
            }
        });

        holder.binding.btnEditWaypoint.setOnClickListener(view -> {
            if (editClickListener != null) {
                editClickListener.onClick(holder.getAdapterPosition(), waypoint);
            }
        });

        holder.binding.btnDeleteRoute.setOnClickListener(view -> {
            if (deleteClickListener != null) {
                deleteClickListener.onClick(holder.getAdapterPosition(), waypoint);
            }
        });
    }

    @Override
    public int getItemCount() {
        return waypointList.size();
    }

    public void setWaypointList(List<Waypoint> waypointList) {
        this.waypointList.clear();
        this.waypointList.addAll(waypointList);
        notifyDataSetChanged();
    }

    public void setSignupClickListener(OnSignupClickListener signupClickListener) {
        this.signupClickListener = signupClickListener;
    }

    public void setEditClickListener(OnEditClickListener editClickListener) {
        this.editClickListener = editClickListener;
    }

    public void setDeleteClickListener(OnDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public interface OnSignupClickListener {
        void onClick(int position, Waypoint waypoint);
    }

    public interface OnEditClickListener {
        void onClick(int position, Waypoint waypoint);
    }

    public interface OnDeleteClickListener {
        void onClick(int position, Waypoint waypoint);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ItemListWaypointBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemListWaypointBinding.bind(itemView);
        }
    }
}
