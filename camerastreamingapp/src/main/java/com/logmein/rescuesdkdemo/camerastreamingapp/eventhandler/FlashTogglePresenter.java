package com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler;

import android.view.View;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.ext.RemoteCameraViewExtension;
import com.logmein.rescuesdk.api.remoteview.camera.event.FlashlightAvailable;
import com.logmein.rescuesdk.api.remoteview.camera.event.FlashlightTurnedOff;
import com.logmein.rescuesdk.api.remoteview.camera.event.FlashlightTurnedOn;
import com.logmein.rescuesdk.api.remoteview.camera.event.FlashlightUnavailable;

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
    public void onFlashAvailable(FlashlightAvailable event) {
        showButton();
    }

    @Subscribe
    public void onFlashUnavailable(FlashlightUnavailable event) {
        hideButton();
    }

    @Subscribe
    public void onFlashTurnedOn(FlashlightTurnedOn event) {
        toggleButton.setOnClickListener(flashOffListener);
    }

    @Subscribe
    public void onFlashTurnedOff(FlashlightTurnedOff event) {
        toggleButton.setOnClickListener(flashOnListener);
    }

    private void showButton() {
        toggleButton.setVisibility(View.VISIBLE);
        toggleButton.setOnClickListener(flashOnListener);
    }

    private void hideButton() {
        toggleButton.setVisibility(View.GONE);
        toggleButton.setOnClickListener(null);
    }
}
