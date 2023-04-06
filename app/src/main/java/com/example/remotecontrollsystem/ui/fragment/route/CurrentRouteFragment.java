package com.example.remotecontrollsystem.ui.fragment.route;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentCurrentRouteBinding;
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;

public class CurrentRouteFragment extends Fragment {
    private FragmentCurrentRouteBinding binding;
    private RouteViewModel routeViewModel;
    private CurrentRouteListAdapter rvAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCurrentRouteBinding.inflate(inflater, container, false);

        init();
        settingClickEvents();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        routeViewModel.getCurrentRoute().observe(requireActivity(), currentRouteObserver);
    }

    private void init() {
        // Initialize RecyclerView
        rvAdapter = new CurrentRouteListAdapter();
        binding.rvCurrentRoute.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
        binding.rvCurrentRoute.setHasFixedSize(true);
        binding.rvCurrentRoute.setAdapter(rvAdapter);

        // Initialize ViewModel
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
    }

    private void settingClickEvents() {
        binding.btnResetCurrentRoute.setOnClickListener(view -> routeViewModel.clearCurrentRoute());
    }

    private Observer<Route> currentRouteObserver = new Observer<Route>() {
        @Override
        public void onChanged(Route route) {
            rvAdapter.setWaypointList(route.getWaypointList());
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        routeViewModel.getCurrentRoute().removeObserver(currentRouteObserver);
    }
}