package com.example.remotecontrollsystem.ui.fragment.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentRobotStatusBinding;

public class RobotStatusFragment extends Fragment {
    private FragmentRobotStatusBinding binding;

    public static RobotStatusFragment newInstance(int num) {
        RobotStatusFragment fragment = new RobotStatusFragment();
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
        binding = FragmentRobotStatusBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }
}