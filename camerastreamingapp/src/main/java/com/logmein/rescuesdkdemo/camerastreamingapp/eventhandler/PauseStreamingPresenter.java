package com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler;

import android.view.View;
import android.widget.Button;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.remoteview.RemoteViewClient;
import com.logmein.rescuesdk.api.remoteview.event.RemoteCameraViewPausedEvent;
import com.logmein.rescuesdk.api.remoteview.event.RemoteCameraViewResumedEvent;
import com.logmein.rescuesdk.api.remoteview.event.RemoteViewStartedEvent;
import com.logmein.rescuesdk.api.remoteview.event.RemoteViewStoppedEvent;
import com.logmein.rescuesdkdemo.camerastreamingapp.R;

/**
 * Manipulates the pause/resume streaming button based on the related events.
 */
public class PauseStreamingPresenter {

    private Button pauseStream;
    private RemoteViewClient remoteViewClient;

    private final View.OnClickListener pauser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            remoteViewClient.pause();
        }
    };

    private final View.OnClickListener resumer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            remoteViewClient.resume();
        }
    };

    public PauseStreamingPresenter(Button pauseStream) {
        this.pauseStream = pauseStream;
    }

    @Subscribe
    public void onRemoteViewStartedEvent(final RemoteViewStartedEvent event) {
        pauseStream.setVisibility(View.VISIBLE);
        remoteViewClient = event.getRemoteViewClient();
        pauseStream.setOnClickListener(pauser);
    }

    @Subscribe
    public void onRemoteViewStoppedEvent(final RemoteViewStoppedEvent event) {
        pauseStream.setVisibility(View.GONE);
        pauseStream.setOnClickListener(null);
        remoteViewClient = null;
    }

    @Subscribe
    public void onRemoteViewPausedEvent(RemoteCameraViewPausedEvent event) {
        pauseStream.setOnClickListener(resumer);
        pauseStream.setText(R.string.resume);
    }

    @Subscribe
    public void onRemoteViewResumedEvent(RemoteCameraViewResumedEvent event) {
        pauseStream.setOnClickListener(pauser);
        pauseStream.setText(R.string.pause);
    }
}
