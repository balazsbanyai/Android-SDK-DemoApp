package com.logmein.rescuesdkdemo;

import android.app.Application;

import com.logmein.rescuesdk.api.RescueSDK;

public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RescueSDK.initializeLifecycleReporter(this);
    }
}
