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
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.model.viewmodel.WaypointViewModel;
import com.example.remotecontrollsystem.mqtt.msgs.ControlHardware;
import com.example.remotecontrollsystem.mqtt.msgs.NavSatFix;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.Twist;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.ui.view.mapsforge.GpsMapView;
import com.example.remotecontrollsystem.ui.view.status.CameraView;
import com.example.remotecontrollsystem.ui.view.utils.CameraViewLifecycleManager;
import com.example.remotecontrollsystem.ui.view.utils.GpsMapViewLifecycleManager;
import com.example.remotecontrollsystem.viewmodel.ConnectionViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttPubViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;

import java.util.Locale;


public class ManualControlFragment extends Fragment {
    private static final String TAG = ManualControlFragment.class.getSimpleName();
    private static final String LINEAR_MAX_VEL_TAG = "linearMaxVel";
    private static final String ANGULAR_MAX_VEL_TAG = "angularMaxVel";
    private FragmentManualControlBinding binding;

    // ViewModels
    private MqttSubViewModel mqttSubViewModel;
    private MqttPubViewModel mqttPubViewModel;
    private RouteViewModel routeViewModel;
    private WaypointViewModel waypointViewModel;

    // Data of max velocity
    private MutableLiveData<Float> linearMaxVel;
    private MutableLiveData<Float> angularMaxVel;

    // Publish messages
    private Twist twist;
    private ControlHardware controlHardware;

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

        mqttSubViewModel = new ViewModelProvider(requireActivity()).get(MqttSubViewModel.class);
        mqttPubViewModel = new ViewModelProvider(requireActivity()).get(MqttPubViewModel.class);
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        waypointViewModel = new ViewModelProvider(requireActivity()).get(WaypointViewModel.class);

        // Attach Lifecycle Managers
        GpsMapViewLifecycleManager gpsMapViewLifecycleManager = new GpsMapViewLifecycleManager(
                this,
                binding.frameMapManual
        );
        CameraViewLifecycleManager cameraViewLifecycleManager = new CameraViewLifecycleManager(
                this,
                binding.frameFrontCameraManual,
                binding.frameRearCameraManual
        );

        linearMaxVel = new MutableLiveData<>();
        angularMaxVel = new MutableLiveData<>();

        // Initialize Messages
        twist = new Twist();
        controlHardware = new ControlHardware();

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
            Log.d("Linear", String.valueOf(linear));
            twist.getLinear().setX(linear);
            mqttPubViewModel.publishTopic(WidgetType.CMD_VEL_PUB.getType(), twist);
        });

        binding.angularJoystickView.setAngularJoystickMoveListener(angular -> {
            twist.getAngular().setZ(angular);
            mqttPubViewModel.publishTopic(WidgetType.CMD_VEL_PUB.getType(), twist);
        });

        // Initialize ControlHardWare Buttons
        binding.btnClarkson.setOnClickListener(v -> {
            controlHardware.setHorn(!controlHardware.isHorn());
            mqttPubViewModel.publishTopic(WidgetType.CONTROL_HARD_WARE.getType(), controlHardware);
        });

        binding.btnHeadRight.setOnClickListener(v -> {
            controlHardware.setHead_light(!controlHardware.isHead_light());
            mqttPubViewModel.publishTopic(WidgetType.CONTROL_HARD_WARE.getType(), controlHardware);
        });

        binding.btnLeftLight.setOnClickListener(v -> {
            controlHardware.setLeft_light(!controlHardware.isLeft_light());
            mqttPubViewModel.publishTopic(WidgetType.CONTROL_HARD_WARE.getType(), controlHardware);
        });

        binding.btnRightLight.setOnClickListener(v -> {
            controlHardware.setRight_light(!controlHardware.isRight_light());
            mqttPubViewModel.publishTopic(WidgetType.CONTROL_HARD_WARE.getType(), controlHardware);
        });

        binding.button.setOnClickListener(v -> {
            NavSatFix fix = (NavSatFix) mqttSubViewModel.getTopicLiveData(WidgetType.NAV_SAT_FIX).getValue();
            if (fix != null) {
                Pose pose = new Pose();
                pose.getPosition().setX(fix.getLatitude());
                pose.getPosition().setY(fix.getLongitude());
                waypointViewModel.addPoseToNewWaypoint(pose);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("수동조작", "Resume");

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
        mqttSubViewModel = null;
        mqttPubViewModel = null;
    }
}