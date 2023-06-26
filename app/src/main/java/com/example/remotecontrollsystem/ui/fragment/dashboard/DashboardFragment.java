package com.example.remotecontrollsystem.ui.fragment.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.databinding.FragmentDashboardBinding;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Request;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.ui.view.mapsforge.GpsMapView;
import com.example.remotecontrollsystem.ui.view.utils.CameraViewLifecycleManager;
import com.example.remotecontrollsystem.ui.view.utils.GpsMapViewLifecycleManager;
import com.example.remotecontrollsystem.ui.view.status.CameraView;
import com.example.remotecontrollsystem.viewmodel.ConnectionViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttPubViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;


public class DashboardFragment extends Fragment {
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private static final String FRAGMENT_TAG = "대시보드";
    private FragmentDashboardBinding binding;

    // ViewModels
    private MqttSubViewModel mqttSubViewModel;
    private MqttPubViewModel mqttPubViewModel;


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

        // Initialize ViewModels
        mqttSubViewModel = new ViewModelProvider(requireActivity()).get(MqttSubViewModel.class);
        mqttPubViewModel = new ViewModelProvider(requireActivity()).get(MqttPubViewModel.class);

        // Initialize Lifecycle Managers
        GpsMapViewLifecycleManager gpsMapViewLifecycleManager = new GpsMapViewLifecycleManager(this, binding.frameMapMain);
        CameraViewLifecycleManager cameraViewLifecycleManager = new CameraViewLifecycleManager(this, binding.frontCameraMainView, binding.rearCameraMainView);

        binding.EStartButton.setOnClickListener(view ->
                mqttPubViewModel.publishCall(WidgetType.GET_MAP.getType(), new GetMap_Request()));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mqttSubViewModel = null;
    }
}