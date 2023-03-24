package com.example.remotecontrollsystem.ui.fragment.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentDashboardBinding;
import com.example.remotecontrollsystem.ui.view.CameraView;
import com.example.remotecontrollsystem.ui.view.MapFrameLayout;


public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;

    public static DashboardFragment newInstance(int num) {
        DashboardFragment fragment = new DashboardFragment();
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
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        binding.frameMapMain.addView(new MapFrameLayout(getContext()));
        binding.frameCameraFrontMain.addView(new CameraView(getContext()));
        binding.frameCameraRearMain.addView(new CameraView(getContext()));

        return binding.getRoot();
    }
}