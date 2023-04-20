package com.example.remotecontrollsystem.ui.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentMqttConnectBinding;
import com.example.remotecontrollsystem.mqtt.Mqtt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MqttConnectFragment extends DialogFragment {
    private static final String IP_ARRAY_TAG = "mqttIpArray";
    private static final String IP_TAG = "mqttIp";
    private static final String LAST_USED_IP_TAG = "mqttLastUsedIp";
    private FragmentMqttConnectBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMqttConnectBinding.inflate(inflater, container, false);

        restoreMqttIpAddress();
        settingClickEvents();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        adjustDialogSize();
    }

    private void settingClickEvents() {
        binding.btnMqttConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mqtt.getInstance().connectToMqttServer(requireContext(), binding.btnMqttConnect.getText().toString());
                saveMqttIpAddress();
            }
        });
        binding.btnExitMqttDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void adjustDialogSize() {
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        // Adjust dialog size
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = (int) (displaySize.x * 0.5);
        params.height = (int) (displaySize.y * 0.5);
        getDialog().getWindow().setAttributes(params);
    }

    protected void saveMqttIpAddress() {
        SharedPreferences pref = requireActivity().getSharedPreferences(IP_ARRAY_TAG, Context.MODE_PRIVATE);

        Set<String> savedIpAddresses = pref.getStringSet(IP_TAG, new LinkedHashSet<>()); // Get saved ip addresses
        Set<String> modifiedIpAddresses = new LinkedHashSet<>(savedIpAddresses); // Copy saved ip addresses to new addresses array
        modifiedIpAddresses.add(binding.etMqttAddress.getText().toString()); // Get ip addresses which will be added to shared preferences

        Log.d("Save Instance", Arrays.toString(modifiedIpAddresses.toArray(new String[modifiedIpAddresses.size()])));

        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(IP_TAG, modifiedIpAddresses);
        editor.putString(LAST_USED_IP_TAG, binding.etMqttAddress.getText().toString());
        editor.apply();
    }

    protected void restoreMqttIpAddress() {
        SharedPreferences pref = requireActivity().getSharedPreferences(IP_ARRAY_TAG, Context.MODE_PRIVATE);

        Set<String> savedIpAddresses = pref.getStringSet(IP_TAG, new LinkedHashSet<>());
        String lastUsedIp = pref.getString(LAST_USED_IP_TAG, "tcp://10.223.188.12:1883");
        String[] savedIpArr = savedIpAddresses.toArray(new String[savedIpAddresses.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, savedIpArr);

        Log.d("Arrays", Arrays.toString(savedIpArr));

        binding.etMqttAddress.setAdapter(adapter);
        binding.etMqttAddress.setText(lastUsedIp);
    }
}