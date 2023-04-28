package com.example.remotecontrollsystem.ui.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.databinding.FragmentMqttConnectBinding;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.viewmodel.ConnectionViewModel;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MqttConnectFragment extends DialogFragment {
    private static final String IP_ARRAY_TAG = "mqttIpArray";
    private static final String IP_TAG = "mqttIp";
    private static final String LAST_USED_IP_TAG = "mqttLastUsedIp";
    private static final String RTSP_ARRAY_TAG = "rtspArray";
    private static final String RTSP_URL_TAG = "rtspUrl";
    private static final String LAST_USED_FRONT_URL_TAG = "rtspLastUsedFrontUrl";
    private static final String LAST_USED_REAR_URL_TAG = "rtspLastUsedRearUrl";

    private FragmentMqttConnectBinding binding;
    private ConnectionViewModel connectionViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMqttConnectBinding.inflate(inflater, container, false);

        connectionViewModel = new ViewModelProvider(requireActivity()).get(ConnectionViewModel.class);

        restoreMqttIpAddress();
        restoreRtspUrls();
        settingClickEvents();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        adjustDialogSize();
        requireDialog().setCancelable(false);
    }

    private void settingClickEvents() {
        binding.btnMqttConnect.setOnClickListener(view -> {
            Mqtt.getInstance().connectToMqttServer(requireContext(), binding.etMqttAddress.getText().toString());
            saveMqttIpAddressPreference();
        });

        binding.btnRtspFrontConnect.setOnClickListener(view -> {
            String url = binding.etRtspFrontUrl.getText().toString();
            connectionViewModel.changeRtspFrontUrl(url);
            saveRtspUrlPreference(url);
        });

        binding.btnRtspRearConnect.setOnClickListener(view -> {
            String url = binding.etRtspRearUrl.getText().toString();
            connectionViewModel.changeRtspRearUrl(url);
            saveRtspUrlPreference(url);
        });

        binding.btnExitMqttDialog.setOnClickListener(view -> dismiss());
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

    protected void saveMqttIpAddressPreference() {
        SharedPreferences pref = requireActivity().getSharedPreferences(IP_ARRAY_TAG, Context.MODE_PRIVATE);

        Set<String> savedIpAddresses = pref.getStringSet(IP_TAG, new LinkedHashSet<>()); // Get saved ip addresses
        Set<String> modifiedIpAddresses = new LinkedHashSet<>(savedIpAddresses); // Copy saved ip addresses to new addresses array
        modifiedIpAddresses.add(binding.etMqttAddress.getText().toString()); // Get ip addresses which will be added to shared preferences

        Log.d("Save IP Address", Arrays.toString(modifiedIpAddresses.toArray(new String[modifiedIpAddresses.size()])));

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

        ArrayAdapter<String> ipAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, savedIpArr);

        Log.d("IP Arrays", Arrays.toString(savedIpArr));

        binding.etMqttAddress.setAdapter(ipAdapter);
        binding.etMqttAddress.setText(lastUsedIp);
    }

    protected void restoreRtspUrls() {
        SharedPreferences pref = requireActivity().getSharedPreferences(RTSP_ARRAY_TAG, Context.MODE_PRIVATE);

        Set<String> savedRtspUrls = pref.getStringSet(RTSP_URL_TAG, new LinkedHashSet<>());
        String[] savedUrlArr = savedRtspUrls.toArray(new String[savedRtspUrls.size()]);

        ArrayAdapter<String> urlAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, savedUrlArr);

        binding.etRtspFrontUrl.setAdapter(urlAdapter);
        binding.etRtspRearUrl.setAdapter(urlAdapter);

        Log.d("URL Arrays", Arrays.toString(savedUrlArr));

        String lastUsedFrontUrl =  pref.getString(LAST_USED_FRONT_URL_TAG, "rtsp://192.168.30.5:1883/rcs");
        String lastUsedRearUrl = pref.getString(LAST_USED_REAR_URL_TAG, "rtsp://192.168.30.5:1883/rcs");

        binding.etRtspFrontUrl.setText(lastUsedFrontUrl);
        binding.etRtspRearUrl.setText(lastUsedRearUrl);
    }

    protected void saveRtspUrlPreference(String rtspUrl) {
        SharedPreferences pref = requireContext().getSharedPreferences(RTSP_ARRAY_TAG, Context.MODE_PRIVATE);

        Set<String> savedUrlAddress = pref.getStringSet(RTSP_URL_TAG, new LinkedHashSet<>());
        Set<String> modifiedUrlAddresses = new LinkedHashSet<>(savedUrlAddress);
        modifiedUrlAddresses.add(rtspUrl);

        Log.d("Save URL", Arrays.toString(modifiedUrlAddresses.toArray(new String[modifiedUrlAddresses.size()])));

        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(RTSP_URL_TAG, modifiedUrlAddresses);
        editor.putString(LAST_USED_FRONT_URL_TAG, binding.etRtspFrontUrl.getText().toString());
        editor.putString(LAST_USED_REAR_URL_TAG, binding.etRtspRearUrl.getText().toString());
        editor.apply();
    }
}