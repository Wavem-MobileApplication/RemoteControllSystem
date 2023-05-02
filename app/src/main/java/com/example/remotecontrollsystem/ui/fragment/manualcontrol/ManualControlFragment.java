package com.example.remotecontrollsystem.ui.fragment.manualcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.databinding.FragmentManualControlBinding;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.Odometry;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.ui.view.map.MapFrameLayout;
import com.example.remotecontrollsystem.ui.view.status.CameraView;
import com.example.remotecontrollsystem.ui.viewmodel.ConnectionViewModel;

import java.util.Locale;


public class ManualControlFragment extends Fragment {
    private static final String LINEAR_MAX_VEL_TAG = "linearMaxVel";
    private static final String ANGULAR_MAX_VEL_TAG = "angularMaxVel";
    private FragmentManualControlBinding binding;
    private MessagePublisher<Odometry> odomPublisher;
    private ConnectionViewModel connectionViewModel;

    private MutableLiveData<Float> linearMaxVel;
    private MutableLiveData<Float> angularMaxVel;


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
        linearMaxVel = new MutableLiveData<>();
        angularMaxVel = new MutableLiveData<>();

        binding.frameMapManual.addView(new MapFrameLayout(requireContext(), (AppCompatActivity) requireActivity()));

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

        binding.etAngularMaxVel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                try {
                    String data = binding.etAngularMaxVel.getText().toString();
                    data = data.replace("m/s", "");
                    angularMaxVel.postValue(Float.parseFloat(data));
                } catch (Exception e) {
                    e.printStackTrace();
                    angularMaxVel.postValue(angularMaxVel.getValue());
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        odomPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.ODOM.getType());
        odomPublisher.attach(odomObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("수동조작", "Resume");
        connectionViewModel.getRtspFrontUrl().observe(requireActivity(), frontUrlObserver);
        connectionViewModel.getRtspRearUrl().observe(requireActivity(), rearUrlObserver);

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

    private final androidx.lifecycle.Observer<String> frontUrlObserver = url -> {
        binding.frameFrontCameraManual.removeAllViews();

        CameraView cameraView = new CameraView(requireContext());
        binding.frameFrontCameraManual.addView(cameraView);

        cameraView.post(() -> cameraView.settingRtspConnection(url));
    };

    private final androidx.lifecycle.Observer<String> rearUrlObserver = url -> {
        binding.frameRearCameraManual.removeAllViews();

        CameraView cameraView = new CameraView(requireContext());
        binding.frameRearCameraManual.addView(cameraView);

        cameraView.post(() -> cameraView.settingRtspConnection(url));
    };

    private final Observer<Odometry> odomObserver = new Observer<Odometry>() {
        @Override
        public void update(Odometry message) {
            double linearVel = message.getTwist().getTwist().getLinear().getX();
            double angularVel = message.getTwist().getTwist().getAngular().getZ();

            String linear = String.format(Locale.KOREA, "%.2f m/s", linearVel);
            String angular = String.format(Locale.KOREA, "%.2f m/s", angularVel);

            binding.tvCurrentLinearVel.post(() -> binding.tvCurrentLinearVel.setText(linear));
            binding.tvCurrentAngularVel.post(() -> binding.tvCurrentAngularVel.setText(angular));
        }
    };



    @Override
    public void onPause() {
        super.onPause();
        Log.d("수동조작", "Pause");
        odomPublisher.detach(odomObserver);
        connectionViewModel.getRtspFrontUrl().removeObserver(frontUrlObserver);
        connectionViewModel.getRtspRearUrl().removeObserver(rearUrlObserver);

        try {
            float linear = linearMaxVel.getValue();
            float angular = angularMaxVel.getValue();

            saveMaxVelPreference(LINEAR_MAX_VEL_TAG, linear);
            saveMaxVelPreference(ANGULAR_MAX_VEL_TAG, angular);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}