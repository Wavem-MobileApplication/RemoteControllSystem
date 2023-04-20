package com.example.remotecontrollsystem.ui.fragment.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.remotecontrollsystem.databinding.FragmentDashboardBinding;
import com.example.remotecontrollsystem.ui.view.map.MapFrameLayout;
import com.example.remotecontrollsystem.ui.view.status.CameraView;


public class DashboardFragment extends Fragment {
    private static final String FRAGMENT_TAG = "대시보드";
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
        binding.getRoot().setTag(FRAGMENT_TAG);

        init();

        return binding.getRoot();
    }

    private void init() {
        MapFrameLayout mapFrameLayout = new MapFrameLayout(getContext(), (AppCompatActivity) requireActivity());

        binding.frameMapMain.addView(mapFrameLayout);
        binding.frameCameraFrontMain.addView(new CameraView(getContext()));
        binding.frameCameraRearMain.addView(new CameraView(getContext()));
    }
}