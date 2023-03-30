package com.example.remotecontrollsystem.ui.fragment.setting.frag;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentTopicEditBinding;


public class TopicEditFragment extends Fragment {
    private FragmentTopicEditBinding binding;

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



        return binding.getRoot();
    }
}