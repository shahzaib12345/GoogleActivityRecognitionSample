package com.vjd.movie.drivedetecttestproject.repository;

import androidx.paging.DataSource;

import com.vjd.movie.drivedetecttestproject.ApplicatonController;
import com.vjd.movie.drivedetecttestproject.entity.LogEntity;
import com.vjd.movie.drivedetecttestproject.room.AppDatabase;
import com.vjd.movie.drivedetecttestproject.room.LogDAO;

public class LocalRepository {
    private static final LocalRepository ourInstance = new LocalRepository();
    private LogDAO logDAO;

    private LocalRepository() {
        logDAO = AppDatabase.getAppDatabase(ApplicatonController.getContext()).getLogDao();
    }

    public static LocalRepository getInstance() {
        return ourInstance;
    }

    public void insertLog(LogEntity logEntity) {
        logDAO.insert(logEntity);
    }

    public DataSource.Factory<Integer, LogEntity> getAllLogs(){
        return logDAO.getAllLogs();
    }
}
