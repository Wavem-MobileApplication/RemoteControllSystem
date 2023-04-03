package com.example.remotecontrollsystem.ui.fragment.setting.frag;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentTopicEditBinding;
import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.ui.fragment.setting.adapter.TopicListAdapter;

import java.util.List;


public class TopicEditFragment extends Fragment {
    private static final String TAG = TopicEditFragment.class.getSimpleName();
    private FragmentTopicEditBinding binding;

    private TopicViewModel topicViewModel;
    private TopicListAdapter rvAdapter;

    public static TopicEditFragment newInstance(int num) {
        TopicEditFragment fragment = new TopicEditFragment();
        Bundle args = new Bundle();
        args.putInt("number", num);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int num = getArguments().getInt("number");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTopicEditBinding.inflate(inflater, container, false);

        init();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        topicViewModel.getAllTopics().observe(requireActivity(), allTopicObserver); // Start to observe all topics
    }

    private void init() {
        topicViewModel = new TopicViewModel(getActivity().getApplication()); // Initialize View Model

        // Initialize Recycler View
        rvAdapter = new TopicListAdapter();
        binding.rvTopic.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTopic.setHasFixedSize(true);
        binding.rvTopic.setAdapter(rvAdapter);
    }

    private final Observer<List<Topic>> allTopicObserver = new Observer<List<Topic>>() {
        @Override
        public void onChanged(List<Topic> topics) {
            for (Topic topic : topics) {
                Log.d(TAG, topic.getFuncName());
            }
           rvAdapter.setTopicList(topics);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        topicViewModel.getAllTopics().removeObserver(allTopicObserver); // Release all topic observer
    }
}