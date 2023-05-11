package com.example.remotecontrollsystem.ui.fragment.route;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentRouteBinding;
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.model.viewmodel.WaypointViewModel;
import com.example.remotecontrollsystem.mqtt.data.RosObserver;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Request;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.ui.fragment.route.adapter.WaypointListAdapter;
import com.example.remotecontrollsystem.ui.util.GoalManager;
import com.example.remotecontrollsystem.ui.util.MapFrameLayoutManager;
import com.example.remotecontrollsystem.ui.util.ToastMessage;
import com.example.remotecontrollsystem.ui.view.map.MapFrameLayout;
import com.example.remotecontrollsystem.viewmodel.MqttPubViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;

import java.util.List;


public class RouteFragment extends Fragment {
    private FragmentRouteBinding binding;


    private WaypointViewModel waypointViewModel;
    private RouteViewModel routeViewModel;
    private MqttSubViewModel mqttSubViewModel;
    private MqttPubViewModel mqttPubViewModel;
    private WaypointListAdapter rvAdapter;

    private MapFrameLayout mapFrameLayout;
    private MapFrameLayoutManager manager;


    public static RouteFragment newInstance(int num) {
        RouteFragment fragment = new RouteFragment();
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
        binding = FragmentRouteBinding.inflate(inflater, container, false);

        init();
        settingStaticRoutes();
        settingClickEvents();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        mqttSubViewModel.getResponseLiveData(WidgetType.GET_MAP).observe(requireActivity(), manager.mapObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.ROBOT_POSE).observe(requireActivity(), manager.robotPoseObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.LASER_SCAN).observe(requireActivity(), manager.scanObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.TF).observe(requireActivity(), manager.tfObserver);

        routeViewModel.getCurrentRoute().observe(requireActivity(), manager.currentRouteObserver);

        waypointViewModel.getNewWaypoint().observe(requireActivity(), manager.newWaypointObserver);
        waypointViewModel.getAllWaypoint().observe(requireActivity(), waypointListObserver);
    }

    private void init() {
        // Initialize RecyclerView
        rvAdapter = new WaypointListAdapter();
        binding.rvWaypoint.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvWaypoint.setHasFixedSize(true);
        binding.rvWaypoint.setAdapter(rvAdapter);

        settingRecyclerItemClickEvents();

        // Initialize ViewModel
        waypointViewModel = new ViewModelProvider(requireActivity()).get(WaypointViewModel.class);
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        mqttSubViewModel = new ViewModelProvider(requireActivity()).get(MqttSubViewModel.class);
        mqttPubViewModel = new ViewModelProvider(requireActivity()).get(MqttPubViewModel.class);

        // Add Views
        mapFrameLayout = new MapFrameLayout(requireContext());
        manager = new MapFrameLayoutManager(mapFrameLayout);
        binding.frameMapRoute.addView(mapFrameLayout);
    }

    private void settingRecyclerItemClickEvents() {
        rvAdapter.setSignupClickListener((position, waypoint) -> {
            if (waypoint.getPoseList() != null && !waypoint.getPoseList().isEmpty()) {
                routeViewModel.addWaypointToCurrentRoute(waypoint);
            } else {
                ToastMessage.showToast(getContext(), "빈 경유지 목록입니다.");
            }
        });

        rvAdapter.setEditClickListener((position, waypoint) -> {
            waypointViewModel.selectEditedWaypoint(waypoint);

            WaypointDialogFragment dialog = new WaypointDialogFragment();
            dialog.show(getParentFragmentManager(), "waypoint_add_dialog");
        });

        rvAdapter.setDeleteClickListener((position, waypoint) -> waypointViewModel.removeWaypoint(waypoint));
    }

    private void settingStaticRoutes() {
        // setting home
        binding.layoutHomeRoute.ivWaypointIcon.setImageResource(R.drawable.icon_home);
        binding.layoutHomeRoute.tvWaypointNameStatic.setText("HOME");

        // setting charging station
        binding.layoutChargingRoute.ivWaypointIcon.setImageResource(R.drawable.icon_charging_station);
        binding.layoutChargingRoute.tvWaypointNameStatic.setText("충전소");
    }

    private void settingClickEvents() {
        binding.btnAddWaypoint.setOnClickListener(view -> {
            Waypoint waypoint = waypointViewModel.getNewWaypoint().getValue();
            if (waypoint == null) {
                waypoint = new Waypoint();
            }
            waypointViewModel.selectEditedWaypoint(waypoint);

            WaypointDialogFragment dialog = new WaypointDialogFragment();
            dialog.show(getParentFragmentManager(), "waypoint_add_dialog");
        });

        binding.btnStartDrive.setOnClickListener(view -> {
            Route route = routeViewModel.getCurrentRoute().getValue();
            GoalManager.getInstance().startRouteDriving(route);
        });
    }

    private final Observer<List<Waypoint>> waypointListObserver = new Observer<List<Waypoint>>() {
        @Override
        public void onChanged(List<Waypoint> waypoints) {
            rvAdapter.setWaypointList(waypoints);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        mqttSubViewModel.getResponseLiveData(WidgetType.GET_MAP).removeObserver(manager.mapObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.ROBOT_POSE).removeObserver(manager.robotPoseObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.LASER_SCAN).removeObserver(manager.scanObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.TF).removeObserver(manager.tfObserver);

        routeViewModel.getCurrentRoute().removeObserver(manager.currentRouteObserver);

        waypointViewModel.getNewWaypoint().removeObserver(manager.newWaypointObserver);
        waypointViewModel.getAllWaypoint().removeObserver(waypointListObserver);
    }
}