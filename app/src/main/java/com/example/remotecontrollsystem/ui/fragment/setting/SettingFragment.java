package com.example.remotecontrollsystem.ui.fragment.setting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentSettingBinding;
import com.example.remotecontrollsystem.ui.fragment.setting.adapter.SettingViewPagerAdapter;


public class SettingFragment extends Fragment {
    private static final String FRAGMENT_TAG = "환경설정";
    private FragmentSettingBinding binding;

    public static SettingFragment newInstance(int num) {
        SettingFragment fragment = new SettingFragment();
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
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        binding.getRoot().setTag(FRAGMENT_TAG);

        init();

        return binding.getRoot();
    }

    private void init() {
        SettingViewPagerAdapter viewPagerAdapter = new SettingViewPagerAdapter(this);
        binding.viewPagerSetting.setAdapter(viewPagerAdapter);
        binding.viewPagerSetting.setUserInputEnabled(false);

        binding.btnSettingTopic.setOnClickListener(v ->
                binding.viewPagerSetting.setCurrentItem(0, false));

        binding.btnControlCar.setOnClickListener(v ->
                binding.viewPagerSetting.setCurrentItem(1, false));
    }
}