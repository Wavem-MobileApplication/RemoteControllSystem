package com.example.remotecontrollsystem.ui.view.utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.GravityInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.model.viewmodel.RouteViewModel;
import com.example.remotecontrollsystem.model.viewmodel.WaypointViewModel;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.ui.view.mapsforge.GpsMapView;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;

public class GpsMapViewLifecycleManager {
    private static final String TAG = GpsMapViewLifecycleManager.class.getSimpleName();
    private Context mContext;

    // Views
    private GpsMapView gpsMapView;
    private FrameLayout parentLayout;

    // ViewModels
    private MqttSubViewModel mqttSubViewModel;
    private RouteViewModel routeViewModel;
    private WaypointViewModel waypointViewModel;


    // Constructors
    public GpsMapViewLifecycleManager(AppCompatActivity activity, FrameLayout parentLayout) {
        this(activity, activity.getLifecycle(), activity, parentLayout);
    }

    public GpsMapViewLifecycleManager(Fragment fragment, FrameLayout parentLayout) {
        this(fragment.requireContext(), fragment.getLifecycle(), fragment.requireActivity(), parentLayout);
    }

    private GpsMapViewLifecycleManager(Context context, Lifecycle lifecycle, ViewModelStoreOwner storeOwner, FrameLayout parentLayout) {
        // Initialize instance of parentLayout
        this.parentLayout = parentLayout;
        this.mContext = context;

        manageLifeCycle(lifecycle, storeOwner);
    }

    // Methods
    private void manageLifeCycle(Lifecycle lifecycle, ViewModelStoreOwner storeOwner) {
        lifecycle.addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onCreate(owner);

                mqttSubViewModel = new ViewModelProvider(storeOwner).get(MqttSubViewModel.class);
                routeViewModel = new ViewModelProvider(storeOwner).get(RouteViewModel.class);
                waypointViewModel = new ViewModelProvider(storeOwner).get(WaypointViewModel.class);

                addGpsMapView();
            }

            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onResume(owner);

                addGpsMapView();
                attachObservers(owner);
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onPause(owner);

                removeObservers();
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onDestroy(owner);
            }
        });
    }

    private void addGpsMapView() {
        // Initialize GpsMapView
        gpsMapView = new GpsMapView(mContext);
        gpsMapView.setOnLongClickListener(geoPoint -> {
            Pose pose = new Pose();
            pose.getPosition().setX(geoPoint.getLatitude());
            pose.getPosition().setY(geoPoint.getLongitude());
            waypointViewModel.addPoseToNewWaypoint(pose);
        });

        gpsMapView.setOnPoseClickListener(pose -> {
            waypointViewModel.removePoseToNewWaypoint(pose);
        });

        // Add GpsMapView
        parentLayout.removeAllViews();
        parentLayout.addView(gpsMapView);
    }

    private void attachObservers(LifecycleOwner owner) {
        mqttSubViewModel.getTopicLiveData(WidgetType.NAV_SAT_FIX).observe(owner, gpsMapView.navSatFixObserver);
        mqttSubViewModel.getTopicLiveData(WidgetType.RTT_ODOM).observe(owner, gpsMapView.rttOdomObserver);
        routeViewModel.getCurrentRoute().observe(owner, gpsMapView.currentRouteObserver);
        waypointViewModel.getNewWaypoint().observe(owner, gpsMapView.currentWaypointObserver);
    }

    private void removeObservers() {
        routeViewModel.getCurrentRoute().removeObserver(gpsMapView.currentRouteObserver);
        waypointViewModel.getNewWaypoint().removeObserver(gpsMapView.currentWaypointObserver);
    }
}
