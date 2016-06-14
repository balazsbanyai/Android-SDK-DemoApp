package com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler;

import android.view.View;

import com.logmein.rescuesdk.api.ext.CameraStreamView;
import com.logmein.rescuesdk.api.ext.CameraStreamingExtension;

/**
 * Manipulates the rendering toggle button based on the related events.
 */
public class RenderTooglePresenter {

    private final View toggleButton;
    private final CameraStreamingExtension extension;
    private final CameraStreamView cameraStreamView;
    private final View.OnClickListener renderOnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            extension.startRendering(cameraStreamView);
            toggleButton.setOnClickListener(renderOffListener);
        }
    };

    private final View.OnClickListener renderOffListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            extension.stopRendering();
            toggleButton.setOnClickListener(renderOnListener);
        }
    };

    public RenderTooglePresenter(View toggleButton, CameraStreamingExtension extension, CameraStreamView cameraStreamView) {
        this.toggleButton = toggleButton;
        this.extension = extension;
        this.cameraStreamView = cameraStreamView;

        toggleButton.setOnClickListener(renderOffListener);
    }

}
