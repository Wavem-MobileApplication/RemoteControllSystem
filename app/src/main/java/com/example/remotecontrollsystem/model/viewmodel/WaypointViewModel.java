package com.example.remotecontrollsystem.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.remotecontrollsystem.model.DataStorage;
import com.example.remotecontrollsystem.model.entity.Waypoint;

import java.util.List;

public class WaypointViewModel extends AndroidViewModel {
    private DataStorage dataStorage;
    private MutableLiveData<Waypoint> editedWaypoint = new MutableLiveData<>();
    public WaypointViewModel(@NonNull Application application) {
        super(application);
        dataStorage = DataStorage.getInstance(application);
    }

    public void addWaypoint(Waypoint waypoint) {
        dataStorage.addWaypoint(waypoint);
    }

    public void removeWaypoint(Waypoint waypoint) {
        dataStorage.removeWaypoint(waypoint);
    }

    public void updateWaypoint(Waypoint waypoint) {
        dataStorage.updateWaypoint(waypoint);
    }

    public LiveData<Waypoint> getWaypoint(int id) {
        return dataStorage.getWaypoint(id);
    }

    public LiveData<List<Waypoint>> getAllWaypoint() {
        return dataStorage.getAllWaypoint();
    }

    public void selectEditedWaypoint(Waypoint waypoint) {
        editedWaypoint.postValue(waypoint);
    }

    public LiveData<Waypoint> getEditedWaypoint() {
        return editedWaypoint;
    }
}
