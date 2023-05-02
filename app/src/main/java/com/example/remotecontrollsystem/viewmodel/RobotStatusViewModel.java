package com.example.remotecontrollsystem.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RobotStatusViewModel extends ViewModel {
    private MutableLiveData<Double> driveMileage;

    public RobotStatusViewModel() {
        driveMileage = new MutableLiveData<>();
    }

    private void updateDriveMileage(double mileage) {

    }
}
