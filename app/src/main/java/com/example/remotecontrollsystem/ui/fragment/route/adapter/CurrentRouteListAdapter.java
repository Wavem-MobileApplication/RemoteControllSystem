package com.example.remotecontrollsystem.ui.fragment.route.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.ItemListRouteHorizontalBinding;
import com.example.remotecontrollsystem.model.entity.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class CurrentRouteListAdapter extends RecyclerView.Adapter<CurrentRouteListAdapter.ViewHolder>{
    private List<Waypoint> waypointList;

    public CurrentRouteListAdapter() {
        waypointList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_route_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Waypoint waypoint = waypointList.get(position);

        holder.binding.tvRouteWaypointName.setText(waypoint.getName());
        if (position == getItemCount() - 1) {
            holder.binding.ivCurrentRouteArrow.setVisibility(View.INVISIBLE);
        } else {
            holder.binding.ivCurrentRouteArrow.setVisibility(View.VISIBLE);
        }
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

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ItemListRouteHorizontalBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemListRouteHorizontalBinding.bind(itemView);
        }
    }
}
