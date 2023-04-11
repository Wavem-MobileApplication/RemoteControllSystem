package com.example.remotecontrollsystem.ui.fragment.dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.databinding.FragmentDashboardBinding;
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.ui.view.status.CameraView;
import com.example.remotecontrollsystem.ui.view.map.MapFrameLayout;


public class DashboardFragment extends Fragment {
    private static final String FRAGMENT_TAG = "대시보드";
    private FragmentDashboardBinding binding;
    private RouteViewModel routeViewModel;

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
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        MapFrameLayout mapFrameLayout = new MapFrameLayout(getContext(), (AppCompatActivity) requireActivity());

        binding.frameMapMain.addView(mapFrameLayout);
        binding.frameCameraFrontMain.addView(new CameraView(getContext()));
        binding.frameCameraRearMain.addView(new CameraView(getContext()));
    }
}