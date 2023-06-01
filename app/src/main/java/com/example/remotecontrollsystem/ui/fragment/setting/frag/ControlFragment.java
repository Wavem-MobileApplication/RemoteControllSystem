package com.example.remotecontrollsystem.ui.fragment.setting.frag;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentControlBinding;
import com.example.remotecontrollsystem.ui.util.IpAddressInputManager;
import com.example.remotecontrollsystem.ui.util.MacAddressInputManager;
import com.example.remotecontrollsystem.ui.util.ToastMessage;
import com.example.remotecontrollsystem.viewmodel.MqttPubViewModel;
import com.example.remotecontrollsystem.wol.WakeOnLan;

public class ControlFragment extends Fragment {
    private FragmentControlBinding binding;

    private MqttPubViewModel mqttPubViewModel;

    private WakeOnLan wakeOnLan;
    private Thread wakeOnLanThread;
    public ControlFragment() {
        // Required empty public constructor
    }

    public static ControlFragment newInstance(int num) {
        ControlFragment fragment = new ControlFragment();
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
        binding = FragmentControlBinding.inflate(inflater, container, false);

        mqttPubViewModel = new ViewModelProvider(requireActivity()).get(MqttPubViewModel.class);

        initializePowerController();

        return binding.getRoot();
    }

    private void initializePowerController() {
        IpAddressInputManager ipAddressInputManager = new IpAddressInputManager(binding.etWakeOnLanIpAddress);
        binding.etWakeOnLanIpAddress.addTextChangedListener(ipAddressInputManager.getIpAddressTextWatcher());

        MacAddressInputManager macAddressInputManager = new MacAddressInputManager(binding.etWakeOnLanMacAddress);
        binding.etWakeOnLanMacAddress.addTextChangedListener(macAddressInputManager.getMacAddressTextWatcher());

        wakeOnLan = new WakeOnLan();
        wakeOnLanThread = new Thread(() -> {
            try {
                wakeOnLan.sendWakeOnLan(
                        binding.etWakeOnLanMacAddress.getText().toString(),
                        binding.etWakeOnLanIpAddress.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                ToastMessage.showToast(requireContext(), "전송실패: 네트워크를 확인해주세요.");
            }
        });

        binding.btnWakeOnLan.setOnClickListener(v -> wakeOnLanThread.start());

        binding.btnWakeOffLan.setOnClickListener(v ->
                mqttPubViewModel.publishDefaultMqttTopic("nuc_shutdown", "nuc_shutdown"));
    }

    private void saveWakeOnLanData() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}