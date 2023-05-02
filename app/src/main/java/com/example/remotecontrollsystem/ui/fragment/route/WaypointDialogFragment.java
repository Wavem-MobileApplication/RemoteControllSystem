package com.example.remotecontrollsystem.ui.fragment.route;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.remotecontrollsystem.databinding.FragmentWaypointDialogBinding;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.model.viewmodel.WaypointViewModel;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.ui.fragment.route.adapter.DestinationListAdapter;
import com.example.remotecontrollsystem.ui.util.DialogUtil;
import com.example.remotecontrollsystem.ui.util.TextWatcherImpl;
import com.example.remotecontrollsystem.ui.util.ToastMessage;

import java.util.List;


public class WaypointDialogFragment extends DialogFragment {
    private static final String TAG = WaypointDialogFragment.class.getSimpleName();
    public static final String WAYPOINT = "waypoint";
    private FragmentWaypointDialogBinding binding;
    private DestinationListAdapter rvAdapter;
    private WaypointViewModel waypointViewModel;
    private Waypoint waypoint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWaypointDialogBinding.inflate(inflater, container, false);

        init();
        settingClickEvents();
        settingFocusEvents();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustDialogSize();
        waypointViewModel.getEditedWaypoint().observe(requireActivity(), editedWaypointObserver);
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
    }

    private void init() {
        // Initialize RecyclerView
        rvAdapter = new DestinationListAdapter();
        binding.rvDestination.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDestination.setHasFixedSize(true);
        binding.rvDestination.setAdapter(rvAdapter);

        // Initialize ViewModel
        waypointViewModel = new ViewModelProvider(requireActivity()).get(WaypointViewModel.class);

        // Initialize no navigation bar setting
        DialogUtil.settingNoNavigationBarScreen(requireDialog());
    }

    private void adjustDialogSize() {
        // Get device's size
        WindowManager windowManager = (WindowManager) requireActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        // Adjust dialog size
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = (int) (displaySize.x * 0.7);
        params.height = (int) (displaySize.y * 0.7);
        getDialog().getWindow().setAttributes(params);
    }

    private void settingClickEvents() {
        binding.btnEditWaypointTitle.setOnClickListener(view -> {
            if (binding.etWaypointName.getVisibility() != View.VISIBLE) {
                binding.tvWaypointName.setVisibility(View.INVISIBLE);
                binding.btnEditWaypointTitle.setVisibility(View.INVISIBLE);

                binding.etWaypointName.setVisibility(View.VISIBLE);
                binding.etWaypointName.requestFocus();
                binding.etWaypointName.setSelection(binding.etWaypointName.getText().length());
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.etWaypointName, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        binding.layoutWaypointDialog.setOnClickListener(view -> {
            binding.etWaypointName.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.etWaypointName.getWindowToken(), 0);
        });

        binding.btnAddDestination.setOnClickListener(view -> rvAdapter.addPose(new Pose()));

        binding.btnSaveWaypoint.setOnClickListener(view -> saveDestinations());

        binding.btnExitWaypointDialog.setOnClickListener(view -> dismiss());
    }

    private void settingFocusEvents() {
        binding.etWaypointName.setOnFocusChangeListener((view, b) -> {
            if (!b) { // If EditText is not focused
                binding.tvWaypointName.setVisibility(View.VISIBLE);
                binding.etWaypointName.setVisibility(View.INVISIBLE);
                binding.btnEditWaypointTitle.setVisibility(View.VISIBLE);

                String name = binding.etWaypointName.getText().toString();
                if (name.isEmpty()) {
                    binding.etWaypointName.setText("제목 없음");
                }
            }
        });

        binding.etWaypointName.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                binding.tvWaypointName.setText(editable.toString());
            }
        });
    }

    private void saveDestinations() {
        List<Pose> poseList = rvAdapter.getPoseList();
        waypoint.setName(binding.tvWaypointName.getText().toString());
        waypoint.setPoseList(poseList);
        waypointViewModel.addWaypoint(waypoint);
        ToastMessage.showToast(getContext(), "'" + waypoint.getName() + "'" + " 경유지를 저장하였습니다.");
        dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        waypointViewModel.getEditedWaypoint().removeObserver(editedWaypointObserver);
    }

    private final Observer<Waypoint> editedWaypointObserver = new Observer<Waypoint>() {
        @Override
        public void onChanged(Waypoint waypoint) {
            WaypointDialogFragment.this.waypoint = waypoint;
            binding.tvWaypointName.setText(waypoint.getName());
            rvAdapter.setPoseList(waypoint.getPoseList());
            Log.d(waypoint.getName(), "이름");
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        waypointViewModel.selectEditedWaypoint(null);
    }
}