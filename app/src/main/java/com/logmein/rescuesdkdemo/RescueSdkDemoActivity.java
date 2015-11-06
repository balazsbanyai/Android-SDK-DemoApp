package com.logmein.rescuesdkdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.session.Session;
import com.logmein.rescuesdk.api.session.SessionFactory;
import com.logmein.rescuesdk.api.session.config.SessionConfig;
import com.logmein.rescuesdk.api.session.event.ConnectingEvent;
import com.logmein.rescuesdk.api.session.event.DisconnectedEvent;
import com.logmein.rescuesdkdemo.adapter.ChatLogAdapter;
import com.logmein.rescuesdkdemo.config.Config;
import com.logmein.rescuesdkdemo.dialog.ConfigSetterDialogFragment;
import com.logmein.rescuesdkdemo.dialog.DialogFragmentUtils;
import com.logmein.rescuesdkdemo.eventhandler.ChatMessagePresenter;
import com.logmein.rescuesdkdemo.eventhandler.ChatSendPresenter;
import com.logmein.rescuesdkdemo.eventhandler.ConnectionButtonPresenter;
import com.logmein.rescuesdkdemo.eventhandler.ConnectionStatusPresenter;
import com.logmein.rescuesdkdemo.eventhandler.ErrorEventHandler;
import com.logmein.rescuesdkdemo.eventhandler.StopDisplaySharingPresenter;
import com.logmein.rescuesdkdemo.eventhandler.TypingPresenter;
import com.logmein.rescuesdkresources.StringResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to demonstrate RescueSDK features.
 */
public class RescueSdkDemoActivity extends AppCompatActivity {

    /**
     * OnClickListener implementation which initiates Session connection to the given channel..
     */
    private class CreateSessionStrategy implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(Config.CHANNEL_ID) || TextUtils.isEmpty(Config.API_KEY)) {
                showChannelIdSetter();
            } else {
                startSession(Config.CHANNEL_ID, Config.API_KEY);
            }
        }
    }

    /**
     * OnClickListener implementation which initiates Session disconnection.
     */
    private class DisconnectSessionStrategy implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            connectionButton.setEnabled(false);
            if (rescueSession != null) {
                rescueSession.disconnect();
            }
        }
    }

    private Button connectionButton;
    private Session rescueSession;
    private List<Object> eventHandlers;
    private ChatLogAdapter logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue_sdk_demo);

        connectionButton = (Button) findViewById(R.id.buttonConnection);
        connectionButton.setOnClickListener(new CreateSessionStrategy());

        final ListView logsView = (ListView) findViewById(R.id.listLogs);
        logAdapter = new ChatLogAdapter(this);

        logsView.setAdapter(logAdapter);
    }

    /**
     * Shows the channel id setter dialog.
     */
    private void showChannelIdSetter() {

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        DialogFragment dialogFragment = ConfigSetterDialogFragment.newInstance(new ConfigSetterDialogFragment.ConfigSetListener() {
            @Override
            public void onConfigSet(String channelId, String apiKey) {
                sharedPreferences.edit()
                        .putString(Config.PREFERENCE_CHANNEL_ID, channelId)
                        .putString(Config.PREFERENCE_API_KEY, apiKey)
                        .commit();

                startSession(channelId, apiKey);
            }
        }, sharedPreferences.getString(Config.PREFERENCE_CHANNEL_ID, null), sharedPreferences.getString(Config.PREFERENCE_API_KEY, null));

        DialogFragmentUtils.showFragmentAndDismissPrevious(getSupportFragmentManager(), dialogFragment, ConfigSetterDialogFragment.TAG);
    }

    /**
     * (Re)starts the session and connects to the given channel.
     *
     * @param channelId The id of the channel to join to.
     */
    private void startSession(final String channelId, final String apiKey) {
        connectionButton.setEnabled(false);

        cleanup();

        SessionFactory.newInstance().create(getApplicationContext(), apiKey, new SessionFactory.SessionCreationCallback() {
            @Override
            public void onSessionCreated(Session session) {

                rescueSession = session;

                // Now we set up our event handlers and add them to the session's event bus.
                // We store them in a list so that we can remove them from the bus later in the
                // cleanup() method.
                eventHandlers = new ArrayList<Object>();

                logAdapter.onSessionCreated(session);
                eventHandlers.add(logAdapter);

                TextView textConnectionStatus = (TextView) findViewById(R.id.textConnectionStatus);
                eventHandlers.add(new ConnectionStatusPresenter(textConnectionStatus));

                Button connectionButton = (Button) findViewById(R.id.buttonConnection);
                eventHandlers.add(new ConnectionButtonPresenter(connectionButton));

                EditText chatMessage = (EditText) findViewById(R.id.editChatMessage);
                eventHandlers.add(new ChatMessagePresenter(chatMessage));

                Button chatSend = (Button) findViewById(R.id.buttonChatSend);
                eventHandlers.add(new ChatSendPresenter(chatSend, chatMessage));

                TextView typing = (TextView) findViewById(R.id.textTypingNotification);
                eventHandlers.add(new TypingPresenter(typing));

                Button stopRcButton = (Button) findViewById(R.id.buttonStopRc);
                eventHandlers.add(new StopDisplaySharingPresenter(stopRcButton));

                StringResolver resolver = new StringResolver(RescueSdkDemoActivity.this, rescueSession);

                eventHandlers.add(new ErrorEventHandler(RescueSdkDemoActivity.this.connectionButton, getSupportFragmentManager(), resolver));
                eventHandlers.add(RescueSdkDemoActivity.this);
                for (final Object eventHandler : eventHandlers) {
                    rescueSession.getEventBus().add(eventHandler);
                }

                // After everything is set up, we connect the session to the channel.
                rescueSession.connect(SessionConfig.createWithChannelId(channelId));
            }
        });
    }

    /**
     * Cleans up the previous Session and aggregated objects.
     */
    private void cleanup() {
        if (rescueSession != null) {
            for (final Object eventHandler : eventHandlers) {
                rescueSession.getEventBus().remove(eventHandler);
            }
            eventHandlers.clear();
        }

        if (rescueSession != null) {
            rescueSession.disconnect();
            rescueSession = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Currently we shut down the session when the app is destroyed. This behavior should change in the future.
        cleanup();
    }

    /**
     * Local subscription to the ConnectingEvent, chooses the proper OnClickListener.
     *
     * @param event ConnectingEvent object.
     */
    @Subscribe
    public void onConnectingEvent(ConnectingEvent event) {
        connectionButton.setOnClickListener(new DisconnectSessionStrategy());
    }

    /**
     * Local subscription to the DisconnectedEvent, chooses the proper OnClickListener.
     *
     * @param event DisconnectedEvent object.
     */
    @Subscribe
    public void onDisconnectedEvent(DisconnectedEvent event) {
        connectionButton.setOnClickListener(new CreateSessionStrategy());
    }
}
