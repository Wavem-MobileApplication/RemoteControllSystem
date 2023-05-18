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
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.model.viewmodel.WaypointViewModel;
import com.example.remotecontrollsystem.mqtt.data.RosObserver;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Request;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.LaserScan;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.TFMessage;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.ui.util.MapFrameLayoutManager;
import com.example.remotecontrollsystem.ui.view.map.MapFrameLayout;
import com.example.remotecontrollsystem.ui.view.status.CameraView;
import com.example.remotecontrollsystem.viewmodel.ConnectionViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttPubViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;


public class DashboardFragment extends Fragment {
    private static final String FRAGMENT_TAG = "대시보드";
    private FragmentDashboardBinding binding;

    // ViewModels
    private ConnectionViewModel connectionViewModel;
    private MqttSubViewModel mqttSubViewModel;
    private MqttPubViewModel mqttPubViewModel;
    private RouteViewModel routeViewModel;
    private WaypointViewModel waypointViewModel;

    // Views
    private MapFrameLayout mapFrameLayout;

    // Managers
    private MapFrameLayoutManager manager;

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
        connectionViewModel = new ViewModelProvider(requireActivity()).get(ConnectionViewModel.class);
        mqttSubViewModel = new ViewModelProvider(requireActivity()).get(MqttSubViewModel.class);
        mqttPubViewModel = new ViewModelProvider(requireActivity()).get(MqttPubViewModel.class);
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        waypointViewModel = new ViewModelProvider(requireActivity()).get(WaypointViewModel.class);

        // Initialize Views
        mapFrameLayout = new MapFrameLayout(requireContext());
        binding.frameMapMain.addView(mapFrameLayout);
        manager = new MapFrameLayoutManager(mapFrameLayout);

        mapFrameLayout.setPoseViewClickListener(pose ->
                waypointViewModel.removePoseToNewWaypoint(pose));

        binding.EStartButton.setOnClickListener(view ->
                mqttPubViewModel.publishCall(WidgetType.GET_MAP.getType(), new GetMap_Request()));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        attachObservers();

        Log.d("대시보드", "Resume");
    }

    private void attachObservers() {
        connectionViewModel.getRtspFrontUrl().observe(requireActivity(), frontUrlObserver);
        connectionViewModel.getRtspRearUrl().observe(requireActivity(), rearUrlObserver);

        mqttSubViewModel.getResponseLiveData(WidgetType.GET_MAP).observe(requireActivity(), manager.mapObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.ROBOT_POSE).observe(requireActivity(), manager.robotPoseObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.LASER_SCAN).observe(requireActivity(), manager.scanObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.TF).observe(requireActivity(), manager.tfObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.GLOBAL_PLAN).observe(requireActivity(), manager.globalPlanObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.LOCAL_PLAN).observe(requireActivity(), manager.localPlanObserver);

        routeViewModel.getCurrentRoute().observe(requireActivity(), manager.currentRouteObserver);
        waypointViewModel.getNewWaypoint().observe(requireActivity(), manager.newWaypointObserver);
    }

    // RTSP Camera Observers
    private final Observer<String> frontUrlObserver = new Observer<String>() {
        @Override
        public void onChanged(String url) {
            binding.frontCameraMainView.removeAllViews();

            CameraView cameraView = new CameraView(requireContext());
            binding.frontCameraMainView.addView(cameraView);

            cameraView.post(() -> cameraView.settingRtspConnection(url));
            Log.d("전면카메라", "세팅");
        }
    };

    private final Observer<String> rearUrlObserver = new Observer<String>() {
        @Override
        public void onChanged(String url) {
            binding.rearCameraMainView.removeAllViews();

            CameraView cameraView = new CameraView(requireContext());
            binding.rearCameraMainView.addView(cameraView);

            cameraView.post(() -> cameraView.settingRtspConnection(url));
            Log.d("후면카메라", "세팅");
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        Log.d("대시보드", "Pause");
        connectionViewModel.getRtspFrontUrl().removeObserver(frontUrlObserver);
        connectionViewModel.getRtspRearUrl().removeObserver(rearUrlObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.ROBOT_POSE).removeObserver(manager.robotPoseObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.MAP).removeObserver(manager.mapObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.LASER_SCAN).removeObserver(manager.scanObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.TF).removeObserver(manager.scanObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.GLOBAL_PLAN).removeObserver(manager.globalPlanObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.LOCAL_PLAN).removeObserver(manager.localPlanObserver);

        routeViewModel.getCurrentRoute().removeObserver(manager.currentRouteObserver);
        waypointViewModel.getNewWaypoint().removeObserver(manager.newWaypointObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        connectionViewModel = null;
        mqttSubViewModel = null;
        routeViewModel = null;
        waypointViewModel = null;
        manager = null;
    }
}