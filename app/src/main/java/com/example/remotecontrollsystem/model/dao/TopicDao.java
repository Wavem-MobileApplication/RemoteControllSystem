package com.example.remotecontrollsystem.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.remotecontrollsystem.model.entity.Topic;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface TopicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Topic topic);

    @Update
    Completable update(Topic topic);

    @Delete
    Completable delete(Topic topic);

    @Query("SELECT * FROM topic_table WHERE funcName = :funcName")
    LiveData<Topic> getTopic(String funcName);

    @Query("SELECT * FROM topic_table")
    LiveData<List<Topic>> getAllTopics();
}
