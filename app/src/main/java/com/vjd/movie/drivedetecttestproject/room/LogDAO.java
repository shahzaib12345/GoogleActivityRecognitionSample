package com.vjd.movie.drivedetecttestproject.room;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.vjd.movie.drivedetecttestproject.entity.LogEntity;

@Dao
public interface LogDAO {
    @Insert
    void insert(LogEntity... logEntity);
 
    @Update
    void update(LogEntity... logEntity);
 
    @Delete
    void delete(LogEntity logEntity);

    @Query("SELECT * FROM LogEntity ORDER BY id DESC")
    DataSource.Factory<Integer, LogEntity> getAllLogs();
}