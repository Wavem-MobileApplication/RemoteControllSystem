package com.example.remotecontrollsystem.ui.fragment.route;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolder;
import com.amrdeveloper.treeview.TreeViewHolderFactory;
import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentRouteListBinding;
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.ui.fragment.route.adapter.RouteListAdapter;
import com.example.remotecontrollsystem.ui.view.list.pose.PoseTreeViewHolder;
import com.example.remotecontrollsystem.ui.view.list.route.RouteTreeNode;
import com.example.remotecontrollsystem.ui.view.list.route.RouteTreeViewHolder;
import com.example.remotecontrollsystem.ui.view.list.waypoint.WaypointTreeViewHolder;

import java.util.ArrayList;
import java.util.List;


public class RouteListFragment extends Fragment {
    private static final String TAG = RouteListFragment.class.getSimpleName();
    private FragmentRouteListBinding binding;
    private RouteViewModel routeViewModel;

    private TreeViewAdapter treeViewAdapter;

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

        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        routeViewModel.getAllRoute().observe(requireActivity(), allRoutesObserver);

        initializeTreeView();

        return binding.getRoot();
    }

    private void initializeTreeView() {
        TreeViewHolderFactory factory = (view, layout) -> {
            if (layout == R.layout.list_item_route_tree) {
                return createRouteTreeViewHolder(view);
            } else if (layout == R.layout.list_item_waypoint_tree) {
                return new WaypointTreeViewHolder(view);
            } else {
                return new PoseTreeViewHolder(view);
            }
        };


        treeViewAdapter = new TreeViewAdapter(factory);
        binding.rvRoute.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRoute.setHasFixedSize(true);
        binding.rvRoute.setAdapter(treeViewAdapter);
    }

    private final Observer<List<Route>> allRoutesObserver = new Observer<>() {
        @Override
        public void onChanged(List<Route> routes) {
            List<TreeNode> routeNodeList = new ArrayList<>();

            for (Route route : routes) {
                RouteTreeNode routeTreeNode = new RouteTreeNode(route);
                routeNodeList.add(routeTreeNode);
            }

            treeViewAdapter.updateTreeNodes(routeNodeList);
        }
    };

    private RouteTreeViewHolder createRouteTreeViewHolder (View view) {
        RouteTreeViewHolder viewHolder = new RouteTreeViewHolder(view);

        viewHolder.setDeleteButtonClickListener(route -> {
            routeViewModel.removeRoute(route);
        });

        viewHolder.setSaveButtonClickListener(route -> {
            routeViewModel.updateRoute(route);
        });

        return viewHolder;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        routeViewModel.getAllRoute().removeObserver(allRoutesObserver);
    }
}