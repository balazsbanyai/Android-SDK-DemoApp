package com.logmein.rescuesdkdemo.core.eventhandler;

import android.view.View;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.session.event.ConnectingEvent;
import com.logmein.rescuesdk.api.session.event.DisconnectedEvent;

/**
 * Manipulates the connection button based on the related events.
 */
public class DisconnectButtonPresenter {

    private final View view;

    public DisconnectButtonPresenter(View view) {
        this.view = view;
    }

    @Subscribe
    public void onConnectingEvent(ConnectingEvent event) {
        view.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onDisconnectedEvent(DisconnectedEvent event) {
        view.setVisibility(View.GONE);
        view.setEnabled(true);
    }

}
