package com.example.remotecontrollsystem.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.remotecontrollsystem.model.entity.Waypoint;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface WaypointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Waypoint waypoint);

    @Update
    void update(Waypoint waypoint);

    @Delete
    void delete(Waypoint waypoint);

    @Query("SELECT * FROM waypoint_table WHERE id = :id")
    LiveData<Waypoint> getWaypoint(int id);

    @Query("SELECT * FROM waypoint_table")
    LiveData<List<Waypoint>> getAllWaypoints();
}
