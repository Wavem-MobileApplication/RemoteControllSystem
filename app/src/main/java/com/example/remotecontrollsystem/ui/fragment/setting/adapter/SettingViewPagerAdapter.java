package com.example.remotecontrollsystem.ui.fragment.setting.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.remotecontrollsystem.ui.fragment.setting.frag.ControlFragment;
import com.example.remotecontrollsystem.ui.fragment.setting.frag.TopicEditFragment;

import java.util.ArrayList;
import java.util.List;

public class SettingViewPagerAdapter extends FragmentStateAdapter {
    private List<Fragment> fragmentList;

    public SettingViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
        fragmentList = new ArrayList<>();
        fragmentList.add(TopicEditFragment.newInstance(0));
        fragmentList.add(ControlFragment.newInstance(1));
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
