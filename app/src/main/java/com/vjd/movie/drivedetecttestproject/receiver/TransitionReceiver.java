package com.vjd.movie.drivedetecttestproject.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.vjd.movie.drivedetecttestproject.R;
import com.vjd.movie.drivedetecttestproject.entity.LogEntity;
import com.vjd.movie.drivedetecttestproject.repository.LocalRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TransitionReceiver extends BroadcastReceiver {
    final String KEY_LAST_ACTIVITY_TYPE = "lastActivityType";
    private int lastActivityType = -1;

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
                return resources.getString(R.string.unknown_activity);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                Toast.makeText(context, getActivityString(context, event.getActivityType()), Toast.LENGTH_SHORT).show();
            }
        }
        if (ActivityRecognitionResult.hasResult(intent)) {

            //If data is available, then extract the ActivityRecognitionResult from the Intent//
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            SharedPreferences sharedpreferences = context.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
            lastActivityType = sharedpreferences.getInt(KEY_LAST_ACTIVITY_TYPE, -1);

            //Get an array of DetectedActivity objects//
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

            for (DetectedActivity resultReceived : detectedActivities) {
                if (resultReceived.getConfidence() > 75) {

                    if (lastActivityType != resultReceived.getType()) {
                        showNotification(getActivityString(context, resultReceived.getType()), context);
                        lastActivityType = resultReceived.getType();
                        sharedpreferences.edit().putInt(KEY_LAST_ACTIVITY_TYPE, lastActivityType).apply();
                    }
                    LocalRepository.getInstance().insertLog(new LogEntity(getActivityString(context, resultReceived.getType()),
                            String.valueOf(resultReceived.getConfidence()), new Date()));
                }
            }
        }
    }

    void showNotification(String message, Context context) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "DRIVE_DETECT")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Drive Detetcion")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = message;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("DRIVE_DETECT", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(100, builder.build());

    }
}
