package com.vjd.movie.drivedetecttestproject;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vjd.movie.drivedetecttestproject.adapter.LogsAdapter;
import com.vjd.movie.drivedetecttestproject.entity.LogEntity;
import com.vjd.movie.drivedetecttestproject.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    public static final String DETECTED_ACTIVITY = ".DETECTED_ACTIVITY";
    private MainViewModel mainViewModel;
    private Context mContext;
    //Define an ActivityRecognitionClient//
    private ActivityRecognitionClient mActivityRecognitionClient;
    private LogsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mainViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(MainViewModel.class)) {
                    return (T) new MainViewModel();
                } else {
                    throw new IllegalArgumentException("ViewModel Not Found");
                }
            }
        }).get(MainViewModel.class);
        RecyclerView detectedActivitiesListView = findViewById(R.id.rv_activities);
        mAdapter = new LogsAdapter();
        detectedActivitiesListView.setAdapter(mAdapter);
        mainViewModel.getAllLogs().observe(this, new Observer<PagedList<LogEntity>>() {
            @Override
            public void onChanged(PagedList<LogEntity> logEntities) {
                mAdapter.submitList(logEntities);
            }
        });

        mActivityRecognitionClient = ActivityRecognition.getClient(this);

        requestUpdatesHandler();
    }

    public void requestUpdatesHandler() {
        //ActivityTransitionRequest request = buildTransitionRequest();
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(30000, mainViewModel.getActivityDetectionPendingIntent(this));
        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(mContext, "Recognition Client Initialized Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(mContext, "Failed to initialize Recognition Client", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
