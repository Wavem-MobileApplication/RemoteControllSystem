package com.example.remotecontrollsystem.ui.fragment.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.databinding.FragmentDrivingStatusBinding;
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.ui.view.status.DrivingProgressView;
import com.example.remotecontrollsystem.viewmodel.StatusViewModel;
import com.example.remotecontrollsystem.viewmodel.manager.AutoDrivingProgression;

public class DrivingStatusFragment extends Fragment {
    private static final String TAG = DrivingStatusFragment.class.getSimpleName();
    private FragmentDrivingStatusBinding binding;
    private RouteViewModel routeViewModel;
    private StatusViewModel statusViewModel;

    private DrivingProgressView drivingProgressView;

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

        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        statusViewModel = new ViewModelProvider(requireActivity()).get(StatusViewModel.class);
        drivingProgressView = new DrivingProgressView(requireContext());

        binding.frameDrivingProgress.addView(drivingProgressView);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        routeViewModel.getCurrentRoute().observe(requireActivity(), currentRouteObserver);
    }

    private final Observer<Route> currentRouteObserver = new Observer<Route>() {
        @Override
        public void onChanged(Route route) {
            drivingProgressView.updateRoute(route);
        }
    };

    private final Observer<AutoDrivingProgression> autoDrivingProgressionObserver = autoDrivingProgression -> {
        drivingProgressView.updateAutoDrivingProgression(autoDrivingProgression);
    };

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        routeViewModel.getCurrentRoute().removeObserver(currentRouteObserver);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}