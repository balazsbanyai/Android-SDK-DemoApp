package com.logmein.rescuesdkdemo.core.eventhandler;

import android.view.View;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.session.event.ConnectingEvent;
import com.logmein.rescuesdk.api.session.event.DisconnectedEvent;

/**
 * Manipulates the connection button based on the related events.
 */
public class ConnectionButtonsPresenter {

    private final View connectButton;
    private final View sessionStatusContainer;

    public ConnectionButtonsPresenter(View connectButton, View sessionStatusContainer) {
        this.connectButton = connectButton;
        this.sessionStatusContainer = sessionStatusContainer;
    }

    @Subscribe
    public void onConnectingEvent(ConnectingEvent event) {
        connectButton.setVisibility(View.GONE);
        sessionStatusContainer.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onDisconnectedEvent(DisconnectedEvent event) {
        connectButton.setVisibility(View.VISIBLE);
        sessionStatusContainer.setVisibility(View.GONE);
        connectButton.setEnabled(true);
    }

}
