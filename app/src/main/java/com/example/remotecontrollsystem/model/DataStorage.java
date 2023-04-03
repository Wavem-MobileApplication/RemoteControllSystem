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

import com.example.remotecontrollsystem.model.dao.TopicDao;
import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.utils.InitializeDatabase;
import com.example.remotecontrollsystem.model.utils.RoomConverter;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Database(entities = {Topic.class}, version = 1, exportSchema = false)
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
            new InitializeDatabase().settingDefaultTopics(instance.topicDao());
        }
    };

    // Dao Methods
    public abstract TopicDao topicDao();

    // Topic Method
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
}
