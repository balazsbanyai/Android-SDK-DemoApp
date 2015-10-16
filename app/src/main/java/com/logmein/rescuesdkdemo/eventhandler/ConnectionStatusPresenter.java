package com.logmein.rescuesdkdemo.eventhandler;

import android.widget.TextView;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.session.event.ConnectedEvent;
import com.logmein.rescuesdk.api.session.event.DisconnectedEvent;
import com.logmein.rescuesdkdemo.R;

/**
 * Manipulates the connection status view based on the related events
 */
public class ConnectionStatusPresenter {

    TextView connectionStatus;

    public ConnectionStatusPresenter(TextView connectionStatus) {
        this.connectionStatus = connectionStatus;
        connectionStatus.setText(R.string.disconnected);
    }

    @Subscribe
    public void onConnectedEvent(ConnectedEvent event) {
        connectionStatus.setText(R.string.connected);
    }

    @Subscribe
    public void onDisconnectedEvent(DisconnectedEvent event) {
        connectionStatus.setText(R.string.disconnected);
    }


}
