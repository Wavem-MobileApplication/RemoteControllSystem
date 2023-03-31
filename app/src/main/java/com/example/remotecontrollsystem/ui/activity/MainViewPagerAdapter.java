package com.example.remotecontrollsystem.ui.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.remotecontrollsystem.ui.fragment.dashboard.DashboardFragment;
import com.example.remotecontrollsystem.ui.fragment.setting.SettingFragment;

import java.util.ArrayList;
import java.util.List;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    private List<Fragment> fragmentList;

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        init();
    }

    public MainViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
        init();
    }

    public MainViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        init();
    }

    private void init() {
        fragmentList = new ArrayList<>();
        fragmentList.add(DashboardFragment.newInstance(0));
        fragmentList.add(SettingFragment.newInstance(1));
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (fragmentList != null && !fragmentList.isEmpty()) {
            return fragmentList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public List<String> getFragmentsNameList() {
        List<String> fragNameList = new ArrayList<>();

        for (int i = 0; i < fragNameList.size(); i++) {
            fragNameList.add(fragmentList.get(i).getTag());
        }

        return fragNameList;
    }
}
