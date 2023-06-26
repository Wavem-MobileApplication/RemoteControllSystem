package com.example.remotecontrollsystem.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.remotecontrollsystem.model.entity.Route;

import java.util.List;

@Dao
public interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Route route);

    @Update
    void update(Route route);

    @Query("DELETE FROM route_table WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM route_table WHERE id = :id")
    LiveData<Route> getRoute(int id);

    @Query("SELECT * FROM route_table")
    LiveData<List<Route>> getAllRoutes();
}
