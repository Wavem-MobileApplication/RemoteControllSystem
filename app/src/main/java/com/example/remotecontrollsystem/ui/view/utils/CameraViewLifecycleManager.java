package com.example.remotecontrollsystem.ui.view.utils;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.remotecontrollsystem.ui.view.status.CameraView;
import com.example.remotecontrollsystem.viewmodel.ConnectionViewModel;

import java.util.logging.Handler;

public class CameraViewLifecycleManager {
    private Context mContext;
    // ViewModels
    private ConnectionViewModel connectionViewModel;

    // Views
    private CameraView frontCameraView;
    private CameraView rearCameraView;
    private FrameLayout frontLayout;
    private FrameLayout rearLayout;

    // Constructors
    public CameraViewLifecycleManager(AppCompatActivity activity, FrameLayout frontLayout, FrameLayout rearLayout) {
        this(activity, activity.getLifecycle(), activity, frontLayout, rearLayout);
    }

    public CameraViewLifecycleManager(Fragment fragment, FrameLayout frontLayout, FrameLayout rearLayout) {
        this(fragment.requireContext(), fragment.getLifecycle(), fragment.requireActivity(), frontLayout, rearLayout);
    }

    private CameraViewLifecycleManager(Context context, Lifecycle lifecycle, ViewModelStoreOwner storeOwner, FrameLayout frontLayout, FrameLayout rearLayout) {
        this.mContext = context;
        this.frontLayout = frontLayout;
        this.rearLayout = rearLayout;

        connectionViewModel = new ViewModelProvider(storeOwner).get(ConnectionViewModel.class);

        manageLifeCycle(lifecycle, storeOwner);
    }

    // Methods
    private void manageLifeCycle(Lifecycle lifecycle, ViewModelStoreOwner storeOwner) {
        lifecycle.addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onCreate(owner);
            }


            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onResume(owner);

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

    private void attachObservers(LifecycleOwner owner) {
        connectionViewModel.getRtspFrontUrl().observe(owner, frontUrlObserver);
        connectionViewModel.getRtspRearUrl().observe(owner, rearUrlObserver);
    }

    private void removeObservers() {
        connectionViewModel.getRtspFrontUrl().removeObserver(frontUrlObserver);
        connectionViewModel.getRtspRearUrl().removeObserver(rearUrlObserver);
    }

    private final Observer<String> frontUrlObserver = new Observer<>() {
        @Override
        public void onChanged(String url) {
            frontLayout.removeAllViews();

            frontCameraView = new CameraView(mContext);
            frontLayout.addView(frontCameraView);

            frontCameraView.post(() -> frontCameraView.settingRtspConnection(url));
        }
    };

    private final Observer<String> rearUrlObserver = new Observer<>() {
        @Override
        public void onChanged(String url) {
            rearLayout.removeAllViews();

            rearCameraView = new CameraView(mContext);
            rearLayout.addView(rearCameraView);

            rearCameraView.post(() -> rearCameraView.settingRtspConnection(url));
        }
    };
}
