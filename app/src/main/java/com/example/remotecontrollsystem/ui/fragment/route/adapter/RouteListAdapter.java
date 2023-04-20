package com.example.remotecontrollsystem.ui.fragment.route.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.ItemListRouteBinding;
import com.example.remotecontrollsystem.model.entity.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.ViewHolder> {
    private List<Route> routeList;

    public RouteListAdapter() {
        routeList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_route, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Route route = routeList.get(position);

        holder.binding.tvRouteNum.setText(String.valueOf(position + 1));
        holder.binding.tvRouteName.setText(route.getName());
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public void setRouteList(List<Route> routeList) {
        this.routeList.clear();
        this.routeList.addAll(routeList);

        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ItemListRouteBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemListRouteBinding.bind(itemView);
        }
    }
}
