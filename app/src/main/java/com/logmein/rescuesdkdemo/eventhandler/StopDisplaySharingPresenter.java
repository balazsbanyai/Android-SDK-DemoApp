package com.logmein.rescuesdkdemo.eventhandler;

import android.view.View;
import android.widget.Button;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.remoteview.RemoteViewClient;
import com.logmein.rescuesdk.api.remoteview.event.RemoteViewStartedEvent;
import com.logmein.rescuesdk.api.remoteview.event.RemoteViewStoppedEvent;

/**
 * Created by bbanyai on 15/10/15.
 */
public class StopDisplaySharingPresenter {
    private Button stopDisplaySharing;
    private RemoteViewClient remoteViewClient;

    public StopDisplaySharingPresenter(Button stopDisplaySharing) {
        this.stopDisplaySharing = stopDisplaySharing;
    }

    @Subscribe
    public void onRemoteViewStartedEvent(final RemoteViewStartedEvent event) {
        stopDisplaySharing.setVisibility(View.VISIBLE);
        remoteViewClient = event.getRemoteViewClient();
        stopDisplaySharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remoteViewClient.stop();
            }
        });
    }

    @Subscribe
    public void onRemoteViewStoppedEvent(final RemoteViewStoppedEvent event) {
        stopDisplaySharing .setVisibility(View.INVISIBLE);
        stopDisplaySharing.setOnClickListener(null);
        remoteViewClient = null;
    }

}
