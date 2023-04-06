package com.example.remotecontrollsystem.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.remotecontrollsystem.model.DataStorage;
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;

import java.util.List;

public class RouteViewModel extends AndroidViewModel {
    private DataStorage dataStorage;
    private MutableLiveData<Route> currentRoute;

    public RouteViewModel(@NonNull Application application) {
        super(application);
        dataStorage = DataStorage.getInstance(application);
        currentRoute = new MutableLiveData<>();
        currentRoute.postValue(new Route());
    }

    public void addRoute(Route route) {
        dataStorage.addRoute(route);
    }

    public void updateRoute(Route route) {
        dataStorage.updateRoute(route);
    }

    public void removeRoute(Route route) {
        dataStorage.removeRoute(route);
    }

    public LiveData<Route> getRoute(int id) {
        return dataStorage.getRoute(id);
    }

    public LiveData<List<Route>> getAllRoute() {
        return dataStorage.getAllRoutes();
    }

    public void selectRoute(int id) {
        currentRoute.postValue(getRoute(id).getValue());
    }

    public void clearCurrentRoute() {
        currentRoute.postValue(new Route());
    }

    public void addWaypointToCurrentRoute(Waypoint waypoint) {
        Route route;
        try {
            route = currentRoute.getValue();
        } catch (Exception e) {
            route = new Route();
            route.setName("새 주행 목록");
        }
        route.getWaypointList().add(waypoint);
        currentRoute.postValue(route);
    }

    public LiveData<Route> getCurrentRoute() {
        return currentRoute;
    }
}
