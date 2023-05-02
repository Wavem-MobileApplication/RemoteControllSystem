package com.example.remotecontrollsystem.ui.fragment.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.databinding.FragmentDashboardBinding;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.ui.view.map.MapFrameLayout;
import com.example.remotecontrollsystem.ui.view.status.CameraView;
import com.example.remotecontrollsystem.viewmodel.ConnectionViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;

import org.videolan.libvlc.Dialog;


public class DashboardFragment extends Fragment {
    private static final String FRAGMENT_TAG = "대시보드";
    private FragmentDashboardBinding binding;

    private MqttSubViewModel mqttSubViewModel;
    private ConnectionViewModel connectionViewModel;

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

        connectionViewModel = new ViewModelProvider(requireActivity()).get(ConnectionViewModel.class);
        mqttSubViewModel = new ViewModelProvider(requireActivity()).get(MqttSubViewModel.class);

        init();
        settingCameras();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
//        settingCameras();
        Log.d("대시보드", "Resume");
    }

    private void init() {
        MapFrameLayout mapFrameLayout = new MapFrameLayout(requireContext(), (AppCompatActivity) requireActivity());
        binding.frameMapMain.addView(mapFrameLayout);

        mqttSubViewModel.getRosMessagePublisher(WidgetType.ROBOT_POSE.getType()).observe(requireActivity(), robotPoseObserver);
    }

    private final Observer<RosMessage> robotPoseObserver = new Observer<RosMessage>() {
        @Override
        public void onChanged(RosMessage rosMessage) {
            if (rosMessage instanceof Pose) {
                Pose pose = (Pose) rosMessage;
            }
        }
    };

    private void settingCameras() {
        connectionViewModel.getRtspFrontUrl().observe(requireActivity(), frontUrlObserver);
        connectionViewModel.getRtspRearUrl().observe(requireActivity(), rearUrlObserver);
    }

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
    }
}