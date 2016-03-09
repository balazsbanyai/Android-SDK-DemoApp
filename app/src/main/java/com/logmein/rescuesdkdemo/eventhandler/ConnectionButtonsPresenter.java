package com.logmein.rescuesdkdemo.eventhandler;

import android.view.View;
import android.widget.Button;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.session.event.ConnectingEvent;
import com.logmein.rescuesdk.api.session.event.DisconnectedEvent;
import com.logmein.rescuesdkdemo.R;

/**
 * Manipulates the connection button based on the related events.
 */
public class ConnectionButtonsPresenter {


    private final View connectionContainer;
    private final View sessionStatusContainer;

    public ConnectionButtonsPresenter(View connectionContainer, View sessionStatusContainer) {
        this.connectionContainer = connectionContainer;
        this.sessionStatusContainer = sessionStatusContainer;
    }

    @Subscribe
    public void onConnectingEvent(ConnectingEvent event) {
        connectionContainer.setVisibility(View.GONE);
        sessionStatusContainer.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onDisconnectedEvent(DisconnectedEvent event) {
        connectionContainer.setVisibility(View.VISIBLE);
        sessionStatusContainer.setVisibility(View.GONE);
        connectionContainer.setEnabled(true);
    }

}
