package com.example.remotecontrollsystem.ui.fragment.setting.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;

import java.util.ArrayList;
import java.util.List;

public class TopicListAdapter extends RecyclerView.Adapter<TopicListAdapter.ViewHolder> {
    private List<RosMessageDefinition> topicList;

    public TopicListAdapter() {
        topicList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public void setTopicList(List<RosMessageDefinition> topicList) {
        this.topicList = topicList;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
