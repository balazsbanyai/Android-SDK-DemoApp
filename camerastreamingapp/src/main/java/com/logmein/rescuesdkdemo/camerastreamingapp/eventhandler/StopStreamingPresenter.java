package com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler;

import android.view.View;
import android.widget.Button;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.streaming.StreamingClient;
import com.logmein.rescuesdk.api.streaming.camera.event.CameraStreamingStartedEvent;
import com.logmein.rescuesdk.api.streaming.camera.event.CameraStreamingStoppedEvent;

/**
 * Manipulates the display sharing button based on the related events
 */
public class StopStreamingPresenter {
    private Button stopStreaming;
    private StreamingClient streamingClient;

    public StopStreamingPresenter(Button stopStreaming) {
        this.stopStreaming = stopStreaming;
    }

    @Subscribe
    public void onCameraStreamingStartedEvent(final CameraStreamingStartedEvent event) {
        stopStreaming.setVisibility(View.VISIBLE);
        streamingClient = event.getStreamingClient();
        stopStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                streamingClient.stop();
            }
        });
    }

    @Subscribe
    public void onCameraStreamingStoppedEvent(final CameraStreamingStoppedEvent event) {
        stopStreaming.setVisibility(View.GONE);
        stopStreaming.setOnClickListener(null);
        streamingClient = null;
    }
}
