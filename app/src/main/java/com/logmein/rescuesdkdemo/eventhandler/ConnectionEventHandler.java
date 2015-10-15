package com.logmein.rescuesdkdemo.eventhandler;

import android.widget.Button;
import android.widget.TextView;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.session.event.ConnectedEvent;
import com.logmein.rescuesdk.api.session.event.ConnectingEvent;
import com.logmein.rescuesdk.api.session.event.DisconnectedEvent;
import com.logmein.rescuesdkdemo.R;

import java.lang.ref.WeakReference;

/**
 * Groups and handles connection-related events.
 */
public final class ConnectionEventHandler {

    private WeakReference<TextView> textConnectionStatus;
    private WeakReference<Button> buttonConnection;

    /**
     * Constructs a ConnectionEventHandler instance with the given parameters.
     * @param connectionStatus The TextView which displays the current connection status.
     * @param buttonConnection The Button which fires out connect/disconnect action.
     */
    public ConnectionEventHandler(final TextView connectionStatus, final Button buttonConnection) {
        this.textConnectionStatus = new WeakReference<TextView>(connectionStatus);
        this.buttonConnection = new WeakReference<Button>(buttonConnection);

        initializeUI();
    }

    private void initializeUI() {
        buttonConnection.get().setText(R.string.connect);
        textConnectionStatus.get().setText(R.string.disconnected);
    }

    @Subscribe
    public void onConnectingEvent(ConnectingEvent event) {
        buttonConnection.get().setText(R.string.disconnect);
        buttonConnection.get().setEnabled(true);
    }

    @Subscribe
    public void onConnectedEvent(ConnectedEvent event) {
        textConnectionStatus.get().setText(R.string.connected);
    }

    @Subscribe
    public void onDisconnectedEvent(DisconnectedEvent event) {
        buttonConnection.get().setText(R.string.connect);
        textConnectionStatus.get().setText(R.string.disconnected);
        buttonConnection.get().setEnabled(true);
    }

}
