package com.logmein.rescuesdkdemo.eventhandler;

import android.widget.Button;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.session.event.ConnectingEvent;
import com.logmein.rescuesdk.api.session.event.DisconnectedEvent;
import com.logmein.rescuesdkdemo.R;

/**
 * Created by bbanyai on 15/10/15.
 */
public class ConnectionButtonPresenter {
    private Button connectionButton;

    public ConnectionButtonPresenter(Button connectionButton) {
        this.connectionButton = connectionButton;
        connectionButton.setText(R.string.connect);
    }

    @Subscribe
    public void onConnectingEvent(ConnectingEvent event) {
        connectionButton.setText(R.string.disconnect);
        connectionButton.setEnabled(true);
    }

    @Subscribe
    public void onDisconnectedEvent(DisconnectedEvent event) {
        connectionButton.setText(R.string.connect);
        connectionButton.setEnabled(true);
    }

}
