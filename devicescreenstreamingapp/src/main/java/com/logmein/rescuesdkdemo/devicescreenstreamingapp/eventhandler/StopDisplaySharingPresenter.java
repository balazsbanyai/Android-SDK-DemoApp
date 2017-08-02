package com.logmein.rescuesdkdemo.devicescreenstreamingapp.eventhandler;

import android.view.View;
import android.widget.Button;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.streaming.StreamingClient;
import com.logmein.rescuesdk.api.streaming.display.event.DisplayStreamingStartedEvent;
import com.logmein.rescuesdk.api.streaming.display.event.DisplayStreamingStoppedEvent;

/**
 * Manipulates the display sharing button based on the related events
 */
public class StopDisplaySharingPresenter {
    private Button stopDisplaySharing;
    private StreamingClient remoteViewClient;

    public StopDisplaySharingPresenter(Button stopDisplaySharing) {
        this.stopDisplaySharing = stopDisplaySharing;
    }

    @Subscribe
    public void onDisplayStreamingStartedEvent(final DisplayStreamingStartedEvent event) {
        stopDisplaySharing.setVisibility(View.VISIBLE);
        remoteViewClient = event.getStreamingClient();
        stopDisplaySharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remoteViewClient.stop();
            }
        });
    }

    @Subscribe
    public void onDisplayStreamingStoppedEvent(final DisplayStreamingStoppedEvent event) {
        stopDisplaySharing.setVisibility(View.GONE);
        stopDisplaySharing.setOnClickListener(null);
        remoteViewClient = null;
    }

}
