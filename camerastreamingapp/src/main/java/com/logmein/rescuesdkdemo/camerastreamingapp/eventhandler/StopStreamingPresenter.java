package com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler;

import android.view.View;
import android.widget.Button;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.remoteview.RemoteViewClient;
import com.logmein.rescuesdk.api.remoteview.event.RemoteViewStartedEvent;
import com.logmein.rescuesdk.api.remoteview.event.RemoteViewStoppedEvent;

/**
 * Manipulates the display sharing button based on the related events
 */
public class StopStreamingPresenter {
    private Button stopStreaming;
    private RemoteViewClient remoteViewClient;

    public StopStreamingPresenter(Button stopStreaming) {
        this.stopStreaming = stopStreaming;
    }

    @Subscribe
    public void onRemoteViewStartedEvent(final RemoteViewStartedEvent event) {
        stopStreaming.setVisibility(View.VISIBLE);
        remoteViewClient = event.getRemoteViewClient();
        stopStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remoteViewClient.stop();
            }
        });
    }

    @Subscribe
    public void onRemoteViewStoppedEvent(final RemoteViewStoppedEvent event) {
        stopStreaming.setVisibility(View.GONE);
        stopStreaming.setOnClickListener(null);
        remoteViewClient = null;
    }
}
