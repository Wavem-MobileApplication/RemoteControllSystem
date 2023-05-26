package com.example.remotecontrollsystem.ui.fragment.setting.frag;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentControlBinding;
import com.example.remotecontrollsystem.viewmodel.MqttPubViewModel;

public class ControlFragment extends Fragment {
    private FragmentControlBinding binding;

    private MqttPubViewModel mqttPubViewModel;

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

        binding.btnWakeOffLan.setOnClickListener(v ->
                mqttPubViewModel.publishDefaultMqttTopic("nuc_shutdown", "nuc_shutdown"));

        return binding.getRoot();
    }
}