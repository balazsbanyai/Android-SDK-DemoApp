package com.logmein.rescuesdkdemo.displaystreamingapp;

import android.app.Application;

import com.logmein.rescuesdk.api.RescueSDK;

public class DisplayStreamingApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RescueSDK.initializeLifecycleReporter(this);
    }
}
