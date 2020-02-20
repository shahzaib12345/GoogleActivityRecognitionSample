package com.vjd.movie.drivedetecttestproject;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

public class ApplicatonController extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        context = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


}
