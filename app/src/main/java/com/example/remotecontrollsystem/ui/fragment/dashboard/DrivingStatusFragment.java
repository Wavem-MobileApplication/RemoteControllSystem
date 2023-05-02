package com.example.remotecontrollsystem.ui.fragment.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.databinding.FragmentDrivingStatusBinding;
import com.example.remotecontrollsystem.ui.view.status.DrivingProgressView;

public class DrivingStatusFragment extends Fragment {
    private FragmentDrivingStatusBinding binding;

    public static DrivingStatusFragment newInstance(int num) {
        DrivingStatusFragment fragment = new DrivingStatusFragment();
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
        binding = FragmentDrivingStatusBinding.inflate(inflater, container, false);

        binding.frameDrivingProgress.addView(new DrivingProgressView(getContext()));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}