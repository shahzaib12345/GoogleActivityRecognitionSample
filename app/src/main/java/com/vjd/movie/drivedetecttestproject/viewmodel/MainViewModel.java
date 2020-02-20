package com.vjd.movie.drivedetecttestproject.viewmodel;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.vjd.movie.drivedetecttestproject.entity.LogEntity;
import com.vjd.movie.drivedetecttestproject.receiver.TransitionReceiver;
import com.vjd.movie.drivedetecttestproject.repository.LocalRepository;

public class MainViewModel extends ViewModel {

    public LiveData<PagedList<LogEntity>> getAllLogs() {
        PagedList.Config config = (new PagedList.Config.Builder())
                .setPageSize(10)
                .setEnablePlaceholders(true)
                .setPrefetchDistance(20)
                .build();
        return new LivePagedListBuilder<>(
                LocalRepository.getInstance().getAllLogs(),config)
                .build();
    }

    public PendingIntent getActivityDetectionPendingIntent(Context context) {
        Intent intent = new Intent(context, TransitionReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
