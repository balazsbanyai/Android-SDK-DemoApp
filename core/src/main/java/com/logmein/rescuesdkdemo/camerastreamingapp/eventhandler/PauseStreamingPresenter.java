package com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler;

import android.view.View;
import android.widget.Button;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.streaming.StreamingClient;
import com.logmein.rescuesdk.api.streaming.event.StreamingPausedEvent;
import com.logmein.rescuesdk.api.streaming.event.StreamingResumedEvent;
import com.logmein.rescuesdk.api.streaming.event.StreamingStartedEvent;
import com.logmein.rescuesdk.api.streaming.event.StreamingStoppedEvent;
import com.logmein.rescuesdkdemo.displaystreamingapp.rescuesdkdemo.core.R;

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
    public void onRemoteViewStartedEvent(final StreamingStartedEvent event) {
        pauseStream.setVisibility(View.VISIBLE);
        streamingClient = event.getStreamingClient();
        pauseStream.setOnClickListener(pauser);
    }

    @Subscribe
    public void onRemoteViewStoppedEvent(final StreamingStoppedEvent event) {
        pauseStream.setVisibility(View.GONE);
        pauseStream.setOnClickListener(null);
        streamingClient = null;
    }

    @Subscribe
    public void onRemoteViewPausedEvent(StreamingPausedEvent event) {
        pauseStream.setOnClickListener(resumer);
        pauseStream.setText(R.string.resume);
    }

    @Subscribe
    public void onRemoteViewResumedEvent(StreamingResumedEvent event) {
        pauseStream.setOnClickListener(pauser);
        pauseStream.setText(R.string.pause);
    }
}
