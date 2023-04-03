package com.example.remotecontrollsystem.ui.fragment.setting.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.ItemListTopicBinding;
import com.example.remotecontrollsystem.model.entity.Topic;

import java.util.ArrayList;
import java.util.List;

public class TopicListAdapter extends RecyclerView.Adapter<TopicListAdapter.ViewHolder> {
    private List<Topic> topicList;

    public TopicListAdapter() {
        topicList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        holder.binding.tvDataName.setText(topic.getFuncName());
        holder.binding.etTopicName.setText(topic.getMessage().getName());
        holder.binding.etMessageType.setText(topic.getMessage().getMessage_type());
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public void setTopicList(List<Topic> topicList) {
        this.topicList.clear();
        this.topicList.addAll(topicList);
        notifyDataSetChanged();
    }

    public List<Topic> getTopicList() {
        return topicList;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ItemListTopicBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemListTopicBinding.bind(itemView);
        }
    }
}
