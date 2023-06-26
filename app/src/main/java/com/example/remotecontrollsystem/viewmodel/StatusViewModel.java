package com.example.remotecontrollsystem.viewmodel;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.remotecontrollsystem.model.utils.InitializeDatabase;
import com.example.remotecontrollsystem.mqtt.data.RosObserver;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_FeedBack;
import com.example.remotecontrollsystem.mqtt.msgs.NavigateToPose_Response;
import com.example.remotecontrollsystem.mqtt.msgs.Odometry;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessage;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.viewmodel.manager.AutoDrivingProgression;

public class StatusViewModel extends AndroidViewModel {
    private MqttSubViewModel mqttSubViewModel;

    private MediatorLiveData<Double> drivingDistance;
    private MutableLiveData<Integer> manualMoveProgress;

    private double preX = 0f, preY = 0f, mileage = 0f; // About driving distance mileage

    public StatusViewModel(@NonNull Application application) {
        super(application);
        mqttSubViewModel = new ViewModelProvider.AndroidViewModelFactory(application).create(MqttSubViewModel.class);

        drivingDistance = new MediatorLiveData<>();
        drivingDistance.addSource(mqttSubViewModel.getTopicLiveData(WidgetType.ODOM), new RosObserver<Odometry>(Odometry.class) {
            @Override
            public void onChange(Odometry odometry) {
                double newX = Math.abs(odometry.getPose().getPose().getPosition().getX()); // 새로 들어온 데이터
                double newY = Math.abs(odometry.getPose().getPose().getPosition().getY());

                if (preX != newX && preY != newY) { // 직전 데이터와 현재 데이터가 다를시에만 거리 합산
                    double x = Math.abs(newX - preX); // 직전 데이터와 현재 데이터의 차
                    double y = Math.abs(newY - preY);
                    double distance = Math.sqrt(x * x + y * y); // 두 점 사이의 거리 공식으로 거리 계산

                    mileage += distance; // 계산한 거리 mileage에 누적
                    Log.d("Mileage", String.valueOf(mileage));

                    preX = newX; // 직전 데이터 현재 데이터로 업데이트
                    preY = newY;

                    drivingDistance.postValue(mileage);
                }
            }
        });

        manualMoveProgress = new MutableLiveData<>();
    }

    public LiveData<Double> getDrivingDistance() {
        return drivingDistance;
    }

    public void updateManualMoveProgress(int progress) {
        manualMoveProgress.postValue(progress);
    }
    public LiveData<Integer> getManualMoveProgress() {
        return manualMoveProgress;
    }
}
