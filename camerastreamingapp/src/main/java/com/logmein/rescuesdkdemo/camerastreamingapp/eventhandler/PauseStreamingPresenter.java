package com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler;

import android.view.View;
import android.widget.Button;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.remoteview.StreamingClient;
import com.logmein.rescuesdk.api.remoteview.camera.event.CameraStreamingPausedEvent;
import com.logmein.rescuesdk.api.remoteview.camera.event.CameraStreamingResumedEvent;
import com.logmein.rescuesdk.api.remoteview.camera.event.CameraStreamingStartedEvent;
import com.logmein.rescuesdk.api.remoteview.camera.event.CameraStreamingStoppedEvent;
import com.logmein.rescuesdkdemo.camerastreamingapp.R;

/**
 * Manipulates the pause/resume streaming button based on the related events.
 */
public class PauseStreamingPresenter {

    private Button pauseStream;
    private StreamingClient streamingClient;

    private final View.OnClickListener pauser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            streamingClient.pause();
        }
    };

    private final View.OnClickListener resumer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            streamingClient.resume();
        }
    };

    public PauseStreamingPresenter(Button pauseStream) {
        this.pauseStream = pauseStream;
    }

    @Subscribe
    public void onRemoteViewStartedEvent(final CameraStreamingStartedEvent event) {
        pauseStream.setVisibility(View.VISIBLE);
        streamingClient = event.getStreamingClient();
        pauseStream.setOnClickListener(pauser);
    }

    @Subscribe
    public void onRemoteViewStoppedEvent(final CameraStreamingStoppedEvent event) {
        pauseStream.setVisibility(View.GONE);
        pauseStream.setOnClickListener(null);
        streamingClient = null;
    }

    @Subscribe
    public void onRemoteViewPausedEvent(CameraStreamingPausedEvent event) {
        pauseStream.setOnClickListener(resumer);
        pauseStream.setText(R.string.resume);
    }

    @Subscribe
    public void onRemoteViewResumedEvent(CameraStreamingResumedEvent event) {
        pauseStream.setOnClickListener(pauser);
        pauseStream.setText(R.string.pause);
    }
}
