package com.logmein.rescuesdkdemo.core.eventhandler;

import android.widget.TextView;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.session.event.ConnectedEvent;
import com.logmein.rescuesdk.api.session.event.DisconnectedEvent;
import com.logmein.rescuesdkresources.StringResolver;

/**
 * Manipulates the connection status view based on the related events
 */
public class ConnectionStatusPresenter {

    private TextView connectionStatus;
    private final StringResolver resolver;

    public ConnectionStatusPresenter(TextView textConnectionStatus, StringResolver resolver) {
        this.connectionStatus = textConnectionStatus;
        this.resolver = resolver;
    }

    @Subscribe
    public void onConnectedEvent(ConnectedEvent event) {
        connectionStatus.setText(resolver.resolve(event));
    }

    @Subscribe
    public void onDisconnectedEvent(DisconnectedEvent event) {
        connectionStatus.setText(resolver.resolve(event));
    }


}
