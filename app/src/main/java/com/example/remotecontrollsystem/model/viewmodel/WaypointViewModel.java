package com.example.remotecontrollsystem.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.remotecontrollsystem.model.DataStorage;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;

import java.util.List;

public class WaypointViewModel extends AndroidViewModel {
    private DataStorage dataStorage;
    private MutableLiveData<Waypoint> editedWaypoint = new MutableLiveData<>();
    private MutableLiveData<Waypoint> newWaypoint = new MutableLiveData<>();

    public WaypointViewModel(@NonNull Application application) {
        super(application);
        dataStorage = DataStorage.getInstance(application);

        newWaypoint = new MutableLiveData<>();
        newWaypoint.postValue(new Waypoint());
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

    public void addPoseToNewWaypoint(Pose pose) {
        Waypoint waypoint;
        if (newWaypoint.getValue() == null) {
            waypoint = new Waypoint();
            waypoint.setName("새 경유지");
        } else {
            waypoint = newWaypoint.getValue();
        }

        List<Pose> poseList = waypoint.getPoseList();
        poseList.add(pose);

        waypoint.setPoseList(poseList);
        newWaypoint.postValue(waypoint);
    }

    public void removePoseToNewWaypoint(Pose pose) {
        if (newWaypoint.getValue() != null) {
            Waypoint waypoint = newWaypoint.getValue();
            List<Pose> poseList = waypoint.getPoseList();

            poseList.remove(pose);
            waypoint.setPoseList(poseList);

            newWaypoint.postValue(waypoint);
        }
    }

    public LiveData<Waypoint> getNewWaypoint() {
        return newWaypoint;
    }

    public boolean saveNewWaypoint() {
        if (newWaypoint.getValue() != null) {
            addWaypoint(newWaypoint.getValue());
            newWaypoint.postValue(new Waypoint()); // Clear newWaypoint after Save
            return true;
        } else {
            return false;
        }
    }
}
