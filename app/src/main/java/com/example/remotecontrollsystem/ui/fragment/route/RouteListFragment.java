package com.example.remotecontrollsystem.ui.fragment.route;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.remotecontrollsystem.databinding.FragmentRouteListBinding;
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.ui.fragment.route.adapter.RouteListAdapter;

import java.util.List;


public class RouteListFragment extends Fragment {
    private FragmentRouteListBinding binding;
    private RouteViewModel routeViewModel;
    private RouteListAdapter adapter;

    public static RouteListFragment newInstance(int num) {
        RouteListFragment fragment = new RouteListFragment();
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
        binding = FragmentRouteListBinding.inflate(inflater, container, false);

        init();

        return binding.getRoot();
    }

    private void init() {
        adapter = new RouteListAdapter();
        binding.rvRoute.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRoute.setHasFixedSize(true);
        binding.rvRoute.setAdapter(adapter);

        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        routeViewModel.getAllRoute().observe(requireActivity(), allRoutesObserver);
    }

    private final Observer<List<Route>> allRoutesObserver = new Observer<List<Route>>() {
        @Override
        public void onChanged(List<Route> routes) {
            adapter.setRouteList(routes);
            Log.d("루트", String.valueOf(routes.size()));
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        routeViewModel.getAllRoute().removeObserver(allRoutesObserver);
    }
}