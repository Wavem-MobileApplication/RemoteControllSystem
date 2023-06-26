package com.example.remotecontrollsystem.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.remotecontrollsystem.model.entity.UgvAttribute;

import java.util.List;

@Dao
public interface UgvAttributeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UgvAttribute attr);

    @Update
    void update(UgvAttribute attr);

    @Delete
    void delete(UgvAttribute attr);

    @Query("SELECT * FROM ugv_attr_table WHERE id = :id")
    LiveData<UgvAttribute> getUgvAttribute(int id);

    @Query("SELECT * FROM ugv_attr_table")
    LiveData<List<UgvAttribute>> getAllUgvAttribute();
}
