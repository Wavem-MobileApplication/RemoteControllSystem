package com.example.remotecontrollsystem.ui.fragment.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.databinding.FragmentRobotStatusBinding;
import com.example.remotecontrollsystem.viewmodel.StatusViewModel;

public class RobotStatusFragment extends Fragment {
    private FragmentRobotStatusBinding binding;

    private StatusViewModel statusViewModel;

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

        statusViewModel = new ViewModelProvider(requireActivity()).get(StatusViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        statusViewModel.getDrivingDistance().observe(requireActivity(), drivingDistanceObserver);
    }

    private final Observer<Double> drivingDistanceObserver = new Observer<Double>() {
        @Override
        public void onChanged(Double mileage) {
            binding.tvDrivingDistance.updateDrivingDistance(mileage);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        statusViewModel.getDrivingDistance().removeObserver(drivingDistanceObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}