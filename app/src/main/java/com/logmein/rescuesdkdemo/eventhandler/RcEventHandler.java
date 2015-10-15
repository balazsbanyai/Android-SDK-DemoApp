package com.logmein.rescuesdkdemo.eventhandler;

import android.view.View;
import android.widget.Button;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.remoteview.RemoteViewClient;
import com.logmein.rescuesdk.api.remoteview.event.RemoteViewStartedEvent;
import com.logmein.rescuesdk.api.remoteview.event.RemoteViewStoppedEvent;

import java.lang.ref.WeakReference;

/**
 * Groups and handles remote control related events.
 */
public final class RcEventHandler {

    private WeakReference<Button> buttonStopRc;
    private RemoteViewClient remoteControlClient;

    /**
     * Constructs an RcEventHandler object with the given parameters.
     * @param stopRc The Button which fires the stop RC action.
     */
    public RcEventHandler(final Button stopRc) {
        buttonStopRc = new WeakReference<Button>(stopRc);
        stopRc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remoteControlClient != null) {
                    remoteControlClient.stop();
                }
            }
        });
    }

    @Subscribe
    public void onRcStartedEvent(final RemoteViewStartedEvent event) {
        if (buttonStopRc.get() != null) {
            buttonStopRc.get().setVisibility(View.VISIBLE);
        }
        remoteControlClient = event.getRemoteViewClient();
    }

    @Subscribe
    public void onRcStoppedEvent(final RemoteViewStoppedEvent event) {
        if (buttonStopRc.get() != null) {
            buttonStopRc.get().setVisibility(View.INVISIBLE);
        }
        remoteControlClient = null;
    }
}
