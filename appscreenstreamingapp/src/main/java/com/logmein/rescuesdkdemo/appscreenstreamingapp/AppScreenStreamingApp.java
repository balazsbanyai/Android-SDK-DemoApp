package com.logmein.rescuesdkdemo.appscreenstreamingapp;

import android.app.Application;

import com.logmein.rescuesdk.api.RescueSDK;

public class AppScreenStreamingApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RescueSDK.initializeLifecycleReporter(this);
    }
}
