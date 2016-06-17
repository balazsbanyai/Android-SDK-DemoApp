package com.logmein.rescuesdkdemo.displaystreamingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.logmein.rescuesdk.api.ext.DisplayStreamingExtension;
import com.logmein.rescuesdk.api.session.Session;
import com.logmein.rescuesdk.api.session.SessionFactory;
import com.logmein.rescuesdk.api.session.config.SessionConfig;
import com.logmein.rescuesdkdemo.core.eventhandler.PauseStreamingPresenter;
import com.logmein.rescuesdkdemo.displaystreamingapp.adapter.ChatLogAdapter;
import com.logmein.rescuesdkdemo.core.Settings;
import com.logmein.rescuesdkdemo.core.SettingsActivity;
import com.logmein.rescuesdkdemo.core.dialog.PinCodeEntryDialogFragment;
import com.logmein.rescuesdkdemo.displaystreamingapp.eventhandler.ChatMessagePresenter;
import com.logmein.rescuesdkdemo.displaystreamingapp.eventhandler.ChatSendPresenter;
import com.logmein.rescuesdkdemo.core.eventhandler.DisconnectButtonPresenter;
import com.logmein.rescuesdkdemo.core.eventhandler.ConnectionStatusPresenter;
import com.logmein.rescuesdkdemo.core.eventhandler.ErrorEventHandler;
import com.logmein.rescuesdkdemo.displaystreamingapp.eventhandler.StopDisplaySharingPresenter;
import com.logmein.rescuesdkdemo.displaystreamingapp.eventhandler.TypingPresenter;
import com.logmein.rescuesdkresources.StringResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to demonstrate RescueSDK features.
 */
public class MainActivity extends AppCompatActivity {

    private Button connectButton;
    private Button disconnectButton;

    /**
     * OnClickListener implementation which initiates Session connection to the given channel.
     */
    private class OnConnectListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            Settings settings = new Settings(prefs);

            switch (settings.getSessionConfigMode()) {
                case PIN_MODE:
                    final String apiKey = settings.getApiKey();
                    DialogFragment pinEntryFragment = PinCodeEntryDialogFragment.newInstance(new PinCodeEntryDialogFragment.OnResultListener() {
                        @Override
                        public void onResult(String pinCode) {
                            startSession(SessionConfig.createWithPinCode(pinCode), apiKey);
                        }
                    });
                    pinEntryFragment.show(getSupportFragmentManager(), PinCodeEntryDialogFragment.TAG);

                    break;

                case CHANNEL_ID_MODE:
                    startSession(SessionConfig.createWithChannelId(settings.getChannelId()), settings.getApiKey());
                    break;

                case CHANNEL_NAME_COMPANY_ID_MODE:
                    startSession(SessionConfig.createWithChannelNameAndCompanyId(settings.getChannelName(), settings.getCompanyId()), settings.getApiKey());
                    break;
            }
        }
    }

    /**
     * OnClickListener implementation which initiates Session disconnection.
     */
    private class OnDisconnectListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (rescueSession != null) {
                rescueSession.disconnect();
            }
        }
    }

    private Session rescueSession;
    private List<Object> eventHandlers;
    private ChatLogAdapter logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = (Button) findViewById(R.id.buttonConnect);
        connectButton.setOnClickListener(new OnConnectListener());

        disconnectButton = (Button) findViewById(R.id.buttonDisconnect);
        disconnectButton.setOnClickListener(new OnDisconnectListener());

        final ListView logsView = (ListView) findViewById(R.id.listLogs);
        logAdapter = new ChatLogAdapter(this);

        logsView.setAdapter(logAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(R.string.settings_menu_label)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                        startActivity(intent);
                        return true;
                    }
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    /**
     * (Re)starts the session and connects using the given configuration.
     *
     */
    private void startSession(final SessionConfig sessionConfig, final String apiKey) {

        connectButton.setEnabled(false);

        cleanup();

        SessionFactory sessionFactory = SessionFactory.newInstance();
        sessionFactory.useExtension(DisplayStreamingExtension.class);
        sessionFactory.create(getApplicationContext(), apiKey, new SessionFactory.SessionCreationCallback() {
            @Override
            public void onSessionCreated(Session session) {
                rescueSession = session;

                // Now we set up our event handlers and add them to the session's event bus.
                // We store them in a list so that we can remove them from the bus later in the
                // cleanup() method.
                eventHandlers = new ArrayList<Object>();

                StringResolver resolver = new StringResolver(MainActivity.this, session);
                logAdapter.setStringResolver(resolver);
                eventHandlers.add(logAdapter);

                TextView textConnectionStatus = (TextView) findViewById(R.id.textConnectionStatus);
                eventHandlers.add(new ConnectionStatusPresenter(textConnectionStatus, resolver));

                Button connectButton = (Button) findViewById(R.id.buttonConnect);
                eventHandlers.add(new DisconnectButtonPresenter(connectButton));

                Button disconnectButton = (Button) findViewById(R.id.buttonDisconnect);
                eventHandlers.add(new DisconnectButtonPresenter(disconnectButton));

                EditText chatMessage = (EditText) findViewById(R.id.editChatMessage);
                eventHandlers.add(new ChatMessagePresenter(chatMessage));

                Button chatSend = (Button) findViewById(R.id.buttonChatSend);
                eventHandlers.add(new ChatSendPresenter(chatSend, chatMessage));

                TextView typing = (TextView) findViewById(R.id.textTypingNotification);
                eventHandlers.add(new TypingPresenter(typing, resolver));

                Button stopRcButton = (Button) findViewById(R.id.buttonStopStreaming);
                eventHandlers.add(new StopDisplaySharingPresenter(stopRcButton));

                Button pauseStreamingButton = (Button) findViewById(R.id.buttonPauseStreaming);
                eventHandlers.add(new PauseStreamingPresenter(pauseStreamingButton));

                eventHandlers.add(new ErrorEventHandler(getSupportFragmentManager(), resolver));
                eventHandlers.add(MainActivity.this);
                for (final Object eventHandler : eventHandlers) {
                    rescueSession.getEventBus().add(eventHandler);
                }

                // After everything is set up, we connect the session with the given configuration.
                rescueSession.connect(sessionConfig);
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

        // Shut down the session when the demo app is destroyed.
        cleanup();
    }
}
