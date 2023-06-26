package com.example.remotecontrollsystem.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.remotecontrollsystem.model.dao.RouteDao;
import com.example.remotecontrollsystem.model.dao.TopicDao;
import com.example.remotecontrollsystem.model.dao.UgvAttributeDao;
import com.example.remotecontrollsystem.model.dao.WaypointDao;
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.entity.UgvAttribute;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.model.utils.InitializeDatabase;
import com.example.remotecontrollsystem.model.utils.InitializerUgvDatabase;
import com.example.remotecontrollsystem.model.utils.LambdaTask;
import com.example.remotecontrollsystem.model.utils.RoomConverter;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Database(entities = {Topic.class, Waypoint.class, Route.class, UgvAttribute.class}, version = 1, exportSchema = false)
@TypeConverters(RoomConverter.class)
public abstract class DataStorage extends RoomDatabase {
    private static final String TAG = DataStorage.class.getSimpleName();

    private static DataStorage instance;


    public static synchronized DataStorage getInstance(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DataStorage.class, "RCSDatabase")
                    .addCallback(roomCallback)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            new InitializerUgvDatabase(instance.ugvDao()).initializeDatabase();
            new InitializeDatabase().settingDefaultTopics(instance.topicDao());

        }
    };

    // Dao Methods
    public abstract TopicDao topicDao();
    public abstract WaypointDao waypointDao();
    public abstract RouteDao routeDao();
    public abstract UgvAttributeDao ugvDao();

    // Topic Methods
    public void addTopic(Topic topic) {
        topicDao().insert(topic);
    }

    public void removeTopic(Topic topic) {
        topicDao().delete(topic);
    }

    public void updateTopic(Topic topic) {
        topicDao().update(topic);
    }

    public LiveData<Topic> getTopic(String funcName) {
        return topicDao().getTopic(funcName);
    }

    public LiveData<List<Topic>> getAllTopics() {
        return topicDao().getAllTopics();
    }

    // Waypoint Methods
    public void addWaypoint(Waypoint waypoint) {
        new LambdaTask(() -> waypointDao().insert(waypoint)).dispose();
    }

    public void removeWaypoint(Waypoint waypoint) {
        new LambdaTask(() -> waypointDao().delete(waypoint)).dispose();
    }

    public void updateWaypoint(Waypoint waypoint) {
        new LambdaTask(() -> waypointDao().update(waypoint)).dispose();
    }

    public LiveData<Waypoint> getWaypoint(int id) {
        return waypointDao().getWaypoint(id);
    }

    public LiveData<List<Waypoint>> getAllWaypoint() {
        return waypointDao().getAllWaypoints();
    }

    // Route Methods
    public void addRoute(Route route) {
        new LambdaTask(() -> routeDao().insert(route)).dispose();
    }

    public void removeRoute(Route route) {
        new LambdaTask(() -> routeDao().delete(route.getId())).dispose();
    }

    public void updateRoute(Route route) {
        new LambdaTask(() -> routeDao().update(route)).dispose();
    }

    public LiveData<Route> getRoute(int id) {
        return routeDao().getRoute(id);
    }

    public LiveData<List<Route>> getAllRoutes() {
        return routeDao().getAllRoutes();
    }

    // Ugv Methods
    public void addUgvAttr(UgvAttribute attr) {
        new LambdaTask(() -> ugvDao().insert(attr)).dispose();
    }

    public void removeUgvAttr(UgvAttribute attr) {
        new LambdaTask(() -> ugvDao().delete(attr)).dispose();
    }

    public void updateUgvAttr(UgvAttribute attr) {
        new LambdaTask(() -> ugvDao().update(attr)).dispose();
    }

    public LiveData<UgvAttribute> getUgvAttr(int id) {
        return ugvDao().getUgvAttribute(id);
    }

    public LiveData<List<UgvAttribute>> getAllUgvAttr() {
        return ugvDao().getAllUgvAttribute();
    }
}
