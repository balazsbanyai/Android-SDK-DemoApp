package com.logmein.rescuesdkdemo.core.eventhandler;

import android.view.View;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.session.event.ConnectingEvent;
import com.logmein.rescuesdk.api.session.event.DisconnectedEvent;

/**
 * Manipulates the connection button based on the related events.
 */
public class ConnectButtonPresenter {

    private final View connectButton;

    public ConnectButtonPresenter(View connectButton) {
        this.connectButton = connectButton;
    }

    @Subscribe
    public void onConnectingEvent(ConnectingEvent event) {
        connectButton.setVisibility(View.GONE);
    }

    @Subscribe
    public void onDisconnectedEvent(DisconnectedEvent event) {
        connectButton.setVisibility(View.VISIBLE);
        connectButton.setEnabled(true);
    }

}
