package com.vjd.movie.drivedetecttestproject.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vjd.movie.drivedetecttestproject.MainActivity;
import com.vjd.movie.drivedetecttestproject.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

//Extend IntentService//
public class ActivityIntentService extends IntentService {

    public static final int[] POSSIBLE_ACTIVITIES = {

            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };
    protected static final String TAG = "Activity";

    //Call the super IntentService constructor with the name for the worker thread//
    public ActivityIntentService() {
        super(TAG);
    }

    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch (detectedActivityType) {
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.vehicle);
            default:
                return resources.getString(R.string.unknown_activity, detectedActivityType);
        }
    }
    //Convert the code for the detected activity type, into the corresponding string//

    public static String detectedActivitiesToJson(ArrayList<DetectedActivity> detectedActivitiesList) {
        Type type = new TypeToken<ArrayList<DetectedActivity>>() {
        }.getType();
        return new Gson().toJson(detectedActivitiesList, type);
    }

    public static ArrayList<DetectedActivity> detectedActivitiesFromJson(String jsonArray) {
        Type listType = new TypeToken<ArrayList<DetectedActivity>>() {
        }.getType();
        ArrayList<DetectedActivity> detectedActivities = new Gson().fromJson(jsonArray, listType);
        if (detectedActivities == null) {
            detectedActivities = new ArrayList<>();
        }
        return detectedActivities;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                Toast.makeText(this, getActivityString(this, event.getActivityType()), Toast.LENGTH_SHORT).show();
            }
        }
        if (ActivityRecognitionResult.hasResult(intent)) {

            //If data is available, then extract the ActivityRecognitionResult from the Intent//
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            //Get an array of DetectedActivity objects//
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
            for (DetectedActivity resultReceived : detectedActivities) {
                if (resultReceived.getType() == DetectedActivity.STILL && resultReceived.getConfidence() > 80) {
                    Toast.makeText(this, "STILL SITTING", Toast.LENGTH_SHORT).show();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "DRIVE_DETECT")
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("Drive Detetcion")
                            .setContentText("STILL")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = getString(R.string.channel_name);
                        String description = "STILL";
                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                        NotificationChannel channel = new NotificationChannel("DRIVE_DETECT", name, importance);
                        channel.setDescription(description);
                        // Register the channel with the system; you can't change the importance
                        // or other notification behaviors after this
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
                    notificationManager.notify(100, builder.build());

                }
                if (resultReceived.getType() == DetectedActivity.WALKING && resultReceived.getConfidence() > 80) {
                    Toast.makeText(this, "WALKING", Toast.LENGTH_SHORT).show();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "DRIVE_DETECT")
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("Drive Detetcion")
                            .setContentText("WALKING")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = getString(R.string.channel_name);
                        String description = "WALING";
                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                        NotificationChannel channel = new NotificationChannel("DRIVE_DETECT", name, importance);
                        channel.setDescription(description);
                        // Register the channel with the system; you can't change the importance
                        // or other notification behaviors after this
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
                    notificationManager.notify(100, builder.build());
                }
            }
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(MainActivity.DETECTED_ACTIVITY,
                            detectedActivitiesToJson(detectedActivities))
                    .apply();


        }
    }
}