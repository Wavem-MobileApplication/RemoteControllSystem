package com.example.remotecontrollsystem.ui.fragment.manualcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.databinding.FragmentManualControlBinding;
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.model.viewmodel.WaypointViewModel;
import com.example.remotecontrollsystem.mqtt.data.RosObserver;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.LaserScan;
import com.example.remotecontrollsystem.mqtt.msgs.Odometry;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.TFMessage;
import com.example.remotecontrollsystem.mqtt.msgs.Twist;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.ui.view.map.MapFrameLayout;
import com.example.remotecontrollsystem.ui.view.status.CameraView;
import com.example.remotecontrollsystem.viewmodel.ConnectionViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttPubViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;

import java.util.Locale;


public class ManualControlFragment extends Fragment {
    private static final String LINEAR_MAX_VEL_TAG = "linearMaxVel";
    private static final String ANGULAR_MAX_VEL_TAG = "angularMaxVel";
    private FragmentManualControlBinding binding;

    // ViewModels
    private ConnectionViewModel connectionViewModel;
    private MqttSubViewModel mqttSubViewModel;
    private MqttPubViewModel mqttPubViewModel;
    private RouteViewModel routeViewModel;
    private WaypointViewModel waypointViewModel;

    private MutableLiveData<Float> linearMaxVel;
    private MutableLiveData<Float> angularMaxVel;

    private MapFrameLayout mapFrameLayout;

    private Twist twist;


    public static ManualControlFragment newInstance(int num) {
        ManualControlFragment fragment = new ManualControlFragment();
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
        binding = FragmentManualControlBinding.inflate(inflater, container, false);

        connectionViewModel = new ViewModelProvider(requireActivity()).get(ConnectionViewModel.class);
        mqttSubViewModel = new ViewModelProvider(requireActivity()).get(MqttSubViewModel.class);
        mqttPubViewModel = new ViewModelProvider(requireActivity()).get(MqttPubViewModel.class);
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        waypointViewModel = new ViewModelProvider(requireActivity()).get(WaypointViewModel.class);

        linearMaxVel = new MutableLiveData<>();
        angularMaxVel = new MutableLiveData<>();
        twist = new Twist();

        mapFrameLayout = new MapFrameLayout(requireContext());
        binding.frameMapManual.addView(mapFrameLayout);

        // Initialize max velocity EditTexts
        binding.etLinearMaxVel.setOnFocusChangeListener((view, isFocused) -> {
            if (!isFocused) {
                try {
                    String data = binding.etLinearMaxVel.getText().toString();
                    data = data.replace("m/s", "");
                    linearMaxVel.postValue(Float.parseFloat(data));
                } catch (Exception e) {
                    e.printStackTrace();
                    linearMaxVel.postValue(linearMaxVel.getValue());
                }
            }
        });
        binding.etAngularMaxVel.setOnFocusChangeListener((view, isFocused) -> {
            try {
                String data = binding.etAngularMaxVel.getText().toString();
                data = data.replace("m/s", "");
                angularMaxVel.postValue(Float.parseFloat(data));
            } catch (Exception e) {
                e.printStackTrace();
                angularMaxVel.postValue(angularMaxVel.getValue());
            }
        });

        // Initialize joystickViews
        binding.linearJoystickView.setLinearJoystickMoveListener(linear -> {
            twist.getLinear().setX(linear);
            mqttPubViewModel.publishTopic(WidgetType.CMD_VEL_PUB.getType(), twist);
        });
        binding.angularJoystickView.setAngularJoystickMoveListener(angular -> {
            twist.getAngular().setZ(-angular);
            mqttPubViewModel.publishTopic(WidgetType.CMD_VEL_PUB.getType(), twist);
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("수동조작", "Resume");
        connectionViewModel.getRtspFrontUrl().observe(requireActivity(), frontUrlObserver);
        connectionViewModel.getRtspRearUrl().observe(requireActivity(), rearUrlObserver);

        mqttSubViewModel.getResponseLiveData(WidgetType.GET_MAP).observe(requireActivity(), mapObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.ROBOT_POSE).observe(requireActivity(), robotPoseObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.LASER_SCAN).observe(requireActivity(), scanObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.TF).observe(requireActivity(), scanObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.ODOM).observe(requireActivity(), odomObserver);

        routeViewModel.getCurrentRoute().observe(requireActivity(), currentRouteObserver);
        waypointViewModel.getNewWaypoint().observe(requireActivity(), newWaypointObserver);

        linearMaxVel.observe(getViewLifecycleOwner(), velocity -> {
            String text = String.format(Locale.KOREA, "%.2fm/s", velocity);
            binding.etLinearMaxVel.setText(text);
            binding.linearJoystickView.setMaxVel(velocity);
        });

        angularMaxVel.observe(getViewLifecycleOwner(), velocity -> {
            String text = String.format(Locale.KOREA, "%.2fm/s", velocity);
            binding.etAngularMaxVel.setText(text);
            binding.angularJoystickView.setMaxVel(velocity);
        });

        restorePreference();
    }

    // RTSP Camera Observers
    private final Observer<String> frontUrlObserver = url -> {
        binding.frameFrontCameraManual.removeAllViews();

        CameraView cameraView = new CameraView(requireContext());
        binding.frameFrontCameraManual.addView(cameraView);

        cameraView.post(() -> cameraView.settingRtspConnection(url));
    };

    private final Observer<String> rearUrlObserver = url -> {
        binding.frameRearCameraManual.removeAllViews();

        CameraView cameraView = new CameraView(requireContext());
        binding.frameRearCameraManual.addView(cameraView);

        cameraView.post(() -> cameraView.settingRtspConnection(url));
    };

    // ROS-MQTT Observers
    private final RosObserver<Odometry> odomObserver = new RosObserver<Odometry>(Odometry.class) {
        @Override
        public void onChange(Odometry odometry) {
            double linearVel = odometry.getTwist().getTwist().getLinear().getX();
            double angularVel = odometry.getTwist().getTwist().getAngular().getZ();

            String linear = String.format(Locale.KOREA, "%.2f m/s", linearVel);
            String angular = String.format(Locale.KOREA, "%.2f m/s", angularVel);

            binding.tvCurrentLinearVel.post(() -> binding.tvCurrentLinearVel.setText(linear));
            binding.tvCurrentAngularVel.post(() -> binding.tvCurrentAngularVel.setText(angular));
        }
    };

    private final RosObserver<GetMap_Response> mapObserver = new RosObserver<GetMap_Response>(GetMap_Response.class) {
        @Override
        public void onChange(GetMap_Response getMap_response) {
            mapFrameLayout.updateMap(getMap_response.getMap());
        }
    };

    private final RosObserver<Pose> robotPoseObserver = new RosObserver<Pose>(Pose.class) {
        @Override
        public void onChange(Pose pose) {
            mapFrameLayout.updateRobotPose(pose);
        }
    };

    private final RosObserver<LaserScan> scanObserver = new RosObserver<LaserScan>(LaserScan.class) {
        @Override
        public void onChange(LaserScan laserScan) {
            mapFrameLayout.updateLaserScan(laserScan);
        }
    };

    private final RosObserver<TFMessage> tfObserver = new RosObserver<TFMessage>(TFMessage.class) {
        @Override
        public void onChange(TFMessage tfMessage) {
            mapFrameLayout.updateTF(tfMessage);
        }
    };

    // Route Observers
    private final Observer<Route> currentRouteObserver = new Observer<Route>() {
        @Override
        public void onChanged(Route route) {
            mapFrameLayout.updateCurrentRoute(route);
        }
    };

    private final Observer<Waypoint> newWaypointObserver = new Observer<Waypoint>() {
        @Override
        public void onChanged(Waypoint waypoint) {
            mapFrameLayout.updateNewWaypoint(waypoint);
        }
    };

    // SharedPreferences
    private void saveMaxVelPreference(String tag, float maxVel) {
        SharedPreferences pref = requireContext().getSharedPreferences(tag, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(tag, maxVel);
        editor.apply();
    }

    private void restorePreference() {
        SharedPreferences linearPref = requireContext().getSharedPreferences(LINEAR_MAX_VEL_TAG, Context.MODE_PRIVATE);
        SharedPreferences angularPref = requireContext().getSharedPreferences(ANGULAR_MAX_VEL_TAG, Context.MODE_PRIVATE);

        float linear = linearPref.getFloat(LINEAR_MAX_VEL_TAG, 1f);
        float angular = angularPref.getFloat(ANGULAR_MAX_VEL_TAG, 1f);

        linearMaxVel.postValue(linear);
        angularMaxVel.postValue(angular);
    }

    @Override
    public void onPause() {
        super.onPause();

        connectionViewModel.getRtspFrontUrl().removeObserver(frontUrlObserver);
        connectionViewModel.getRtspRearUrl().removeObserver(rearUrlObserver);
        mqttSubViewModel.getResponseLiveData(WidgetType.GET_MAP).removeObserver(mapObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.ROBOT_POSE).removeObserver(robotPoseObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.LASER_SCAN).removeObserver(scanObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.TF).removeObserver(tfObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.ODOM).removeObserver(odomObserver);
        routeViewModel.getCurrentRoute().removeObserver(currentRouteObserver);
        waypointViewModel.getNewWaypoint().removeObserver(newWaypointObserver);

        try {
            float linear = linearMaxVel.getValue();
            float angular = angularMaxVel.getValue();

            saveMaxVelPreference(LINEAR_MAX_VEL_TAG, linear);
            saveMaxVelPreference(ANGULAR_MAX_VEL_TAG, angular);
        } catch (Exception e) {
            e.printStackTrace();
        }

        linearMaxVel.removeObservers(getViewLifecycleOwner());
        angularMaxVel.removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        connectionViewModel = null;
        mqttSubViewModel = null;
        mqttPubViewModel = null;
        routeViewModel = null;
        waypointViewModel = null;
    }
}