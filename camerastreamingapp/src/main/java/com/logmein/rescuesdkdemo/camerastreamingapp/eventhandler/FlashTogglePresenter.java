package com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler;

import android.view.View;
import android.widget.Switch;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.ext.CameraStreamingExtension;
import com.logmein.rescuesdk.api.streaming.camera.event.FlashlightAvailableEvent;
import com.logmein.rescuesdk.api.streaming.camera.event.FlashlightTurnedOffEvent;
import com.logmein.rescuesdk.api.streaming.camera.event.FlashlightTurnedOnEvent;
import com.logmein.rescuesdk.api.streaming.camera.event.FlashlightUnavailableEvent;

/**
 * Manipulates the flashlight toggle button based on the related events.
 */
public class FlashTogglePresenter {

    private final Switch toggleButton;
    private final CameraStreamingExtension extension;

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

    public FlashTogglePresenter(Switch toggleButton, CameraStreamingExtension extension) {
        this.toggleButton = toggleButton;
        this.extension = extension;
    }

    @Subscribe
    public void onFlashAvailable(FlashlightAvailableEvent event) {
        showButton();
    }

    @Subscribe
    public void onFlashUnavailable(FlashlightUnavailableEvent event) {
        hideButton();
    }

    @Subscribe
    public void onFlashTurnedOn(FlashlightTurnedOnEvent event) {
        toggleButton.setOnClickListener(flashOffListener);
        toggleButton.setChecked(true);
    }

    @Subscribe
    public void onFlashTurnedOff(FlashlightTurnedOffEvent event) {
        toggleButton.setOnClickListener(flashOnListener);
        toggleButton.setChecked(false);
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
