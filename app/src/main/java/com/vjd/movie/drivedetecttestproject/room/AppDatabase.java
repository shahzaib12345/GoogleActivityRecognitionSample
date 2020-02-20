package com.vjd.movie.drivedetecttestproject.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.vjd.movie.drivedetecttestproject.entity.LogEntity;

import java.util.Date;


@Database(entities = {LogEntity.class}, version = 3, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LogDAO getLogDao();

    private static AppDatabase appDatabase;

    public static AppDatabase getAppDatabase(Context context) {

        if(appDatabase==null){
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, "db-contacts")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()//Allows room to do operation on main thread
                    .build();
        }
        return appDatabase;

    }


}