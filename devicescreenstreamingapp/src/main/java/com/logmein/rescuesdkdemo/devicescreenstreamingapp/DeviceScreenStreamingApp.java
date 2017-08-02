package com.logmein.rescuesdkdemo.devicescreenstreamingapp;

import android.app.Application;

import com.logmein.rescuesdk.api.RescueSDK;

public class DeviceScreenStreamingApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RescueSDK.initializeLifecycleReporter(this);
    }
}
