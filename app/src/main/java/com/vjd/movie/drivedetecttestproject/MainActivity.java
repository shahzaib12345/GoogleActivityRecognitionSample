package com.vjd.movie.drivedetecttestproject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.vjd.movie.drivedetecttestproject.adapter.ActivitiesAdapter;
import com.vjd.movie.drivedetecttestproject.service.ActivityIntentService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String DETECTED_ACTIVITY = ".DETECTED_ACTIVITY";
    private Context mContext;
    //Define an ActivityRecognitionClient//
    private ActivityRecognitionClient mActivityRecognitionClient;
    private ActivitiesAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        ListView detectedActivitiesListView = findViewById(R.id.activities_listview);

        ArrayList<DetectedActivity> detectedActivities = ActivityIntentService.detectedActivitiesFromJson(
                PreferenceManager.getDefaultSharedPreferences(this).getString(
                        DETECTED_ACTIVITY, ""));

        mAdapter = new ActivitiesAdapter(this, detectedActivities);
        detectedActivitiesListView.setAdapter(mAdapter);
        mActivityRecognitionClient = ActivityRecognition.getClient(this);

        requestUpdatesHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        updateDetectedActivitiesList();
    }

    @Override
    protected void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    public void requestUpdatesHandler() {
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityTransitionRequest request = buildTransitionRequest();
                Task task = mActivityRecognitionClient.requestActivityTransitionUpdates(request, getActivityDetectionPendingIntent());
                task.addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                updateDetectedActivitiesList();
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
        }, 1000);*/
        ActivityTransitionRequest request = buildTransitionRequest();
        Task task = mActivityRecognitionClient.requestActivityUpdates(10, getActivityDetectionPendingIntent());
        task.addOnSuccessListener(
                new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        updateDetectedActivitiesList();
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

    ActivityTransitionRequest buildTransitionRequest() {
        List transitions = new ArrayList<>();
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        return new ActivityTransitionRequest(transitions);
    }

    //Get a PendingIntent//
    private PendingIntent getActivityDetectionPendingIntent() {
//Send the activity data to our DetectedActivitiesIntentService class//
        Intent intent = new Intent(this, ActivityIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    //Process the list of activities//
    protected void updateDetectedActivitiesList() {
        ArrayList<DetectedActivity> detectedActivities = ActivityIntentService.detectedActivitiesFromJson(
                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getString(DETECTED_ACTIVITY, ""));

        mAdapter.updateActivities(detectedActivities);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(DETECTED_ACTIVITY)) {
            updateDetectedActivitiesList();
        }
    }
}
