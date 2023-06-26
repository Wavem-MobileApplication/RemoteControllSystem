package com.example.remotecontrollsystem.ui.fragment.route;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.databinding.FragmentRouteDialogBinding;
import com.example.remotecontrollsystem.databinding.FragmentRouteListBinding;
import com.example.remotecontrollsystem.ui.dialog.CustomDialogFragment;
import com.example.remotecontrollsystem.ui.util.DialogUtil;


public class RouteDialogFragment extends CustomDialogFragment {
    private FragmentRouteDialogBinding binding;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRouteDialogBinding.inflate(inflater, container, false);

        DialogUtil.settingNoNavigationBarScreen(requireDialog());

        setCancelable(false);
        binding.btnExitRouteDialog.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        navController = Navigation.findNavController(binding.navHostFragmentRoute);
        adjustDialogSize();
    }

    private void adjustDialogSize() {
        // Get device's size
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        // Adjust dialog size
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = (int) (displaySize.x * 0.7);
        params.height = (int) (displaySize.y * 0.7);
        getDialog().getWindow().setAttributes(params);
    }


}