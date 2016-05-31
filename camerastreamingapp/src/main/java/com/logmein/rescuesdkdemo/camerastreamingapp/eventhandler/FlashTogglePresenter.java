package com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler;

import android.view.View;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.ext.RemoteCameraViewExtension;
import com.logmein.rescuesdk.api.remoteview.event.FlashlightTurnedOff;
import com.logmein.rescuesdk.api.remoteview.event.FlashlightTurnedOn;
import com.logmein.rescuesdk.api.remoteview.event.RemoteCameraViewPausedEvent;
import com.logmein.rescuesdk.api.remoteview.event.RemoteCameraViewResumedEvent;
import com.logmein.rescuesdk.api.remoteview.event.RemoteViewStartedEvent;
import com.logmein.rescuesdk.api.remoteview.event.RemoteViewStoppedEvent;

/**
 * Manipulates the flashlight toggle button based on the related events.
 */
public class FlashTogglePresenter {

    private final View toggleButton;
    private final RemoteCameraViewExtension extension;

    private final View.OnClickListener flashOnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            extension.flashOn();
        }
    };

    private final View.OnClickListener flashOffListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            extension.flashOff();
        }
    };

    public FlashTogglePresenter(View toggleButton, RemoteCameraViewExtension extension) {
        this.toggleButton = toggleButton;
        this.extension = extension;
    }

    @Subscribe
    public void onRemoteViewStarted(RemoteViewStartedEvent event) {
        showButton();
    }

    @Subscribe
    public void onRemoteViewResumed(RemoteCameraViewResumedEvent event) {
        showButton();
    }

    @Subscribe
    public void onRemoteViewPausedEvent(RemoteCameraViewPausedEvent event) {
        hideButtonAndTurnFlashOff();
    }

    @Subscribe
    public void onRemoteViewStopped(RemoteViewStoppedEvent event) {
        hideButtonAndTurnFlashOff();
    }

    @Subscribe
    public void onFlashTurnedOn(FlashlightTurnedOn event) {
        toggleButton.setOnClickListener(flashOffListener);
    }

    @Subscribe
    public void onFlashTurnedOff(FlashlightTurnedOff event) {
        toggleButton.setOnClickListener(flashOnListener);
    }

    //TODO: handle Freeze/Unfreeze here

    private void showButton() {
        toggleButton.setVisibility(View.VISIBLE);
        toggleButton.setOnClickListener(flashOnListener);
    }

    private void hideButtonAndTurnFlashOff() {
        toggleButton.setVisibility(View.GONE);
        toggleButton.setOnClickListener(null);
        extension.flashOff();
    }
}
