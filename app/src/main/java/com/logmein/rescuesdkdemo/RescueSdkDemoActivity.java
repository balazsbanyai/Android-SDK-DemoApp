package com.logmein.rescuesdkdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.logmein.rescuesdk.api.session.Session;
import com.logmein.rescuesdk.api.session.SessionFactory;
import com.logmein.rescuesdk.api.session.config.SessionConfig;
import com.logmein.rescuesdkdemo.adapter.ChatLogAdapter;
import com.logmein.rescuesdkdemo.config.Config;
import com.logmein.rescuesdkdemo.dialog.ApiKeySetterDialogFragment;
import com.logmein.rescuesdkdemo.dialog.ConfigSetterDialogFragment;
import com.logmein.rescuesdkdemo.dialog.DialogFragmentUtils;
import com.logmein.rescuesdkdemo.dialog.PinCodeEntryDialogFragment;
import com.logmein.rescuesdkdemo.eventhandler.ChatMessagePresenter;
import com.logmein.rescuesdkdemo.eventhandler.ChatSendPresenter;
import com.logmein.rescuesdkdemo.eventhandler.ConnectionButtonsPresenter;
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

    private Button connectChannelButton;
    private Button connectPinButton;
    private Button disconnectButton;

    /**
     * OnClickListener implementation which initiates Session connection to the given channel.
     */
    private class OnConnectChannelListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(Config.CHANNEL_ID) || TextUtils.isEmpty(Config.API_KEY)) {
                showChannelIdSetter();
            } else {
                startSession(SessionConfig.createWithChannelId(Config.CHANNEL_ID), Config.API_KEY);
            }
        }
    }

    /**
     * OnClickListener implementation which initiates Session with a PIN code.
     */
    private class OnConnectPinListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(Config.API_KEY)) {
                showApiKeySetter(new ApiKeySetterDialogFragment.ConfigSetListener() {
                    @Override
                    public void onConfigSet(String apiKey) {
                        showPinCodeEntry(apiKey);
                    }
                });
            } else {
                showPinCodeEntry(Config.API_KEY);
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
        setContentView(R.layout.activity_rescue_sdk_demo);

        connectChannelButton = (Button) findViewById(R.id.buttonConnectChannel);
        connectChannelButton.setOnClickListener(new OnConnectChannelListener());

        connectPinButton = (Button) findViewById(R.id.buttonConnectPin);
        connectPinButton.setOnClickListener(new OnConnectPinListener());

        disconnectButton = (Button) findViewById(R.id.buttonDisconnect);
        disconnectButton.setOnClickListener(new OnDisconnectListener());

        final ListView logsView = (ListView) findViewById(R.id.listLogs);
        logAdapter = new ChatLogAdapter(this);

        logsView.setAdapter(logAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add("Settings")
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

                SessionConfig sessionConfig = SessionConfig.createWithChannelId(channelId);
                startSession(sessionConfig, apiKey);
            }
        }, sharedPreferences.getString(Config.PREFERENCE_CHANNEL_ID, null), sharedPreferences.getString(Config.PREFERENCE_API_KEY, null));

        DialogFragmentUtils.showFragmentAndDismissPrevious(getSupportFragmentManager(), dialogFragment, ConfigSetterDialogFragment.TAG);
    }

    /**
     * Shows the API key setter dialog.
     */
    private void showApiKeySetter(final ApiKeySetterDialogFragment.ConfigSetListener resultListener) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String previousApiKey = sharedPreferences.getString(Config.PREFERENCE_API_KEY, null);

        DialogFragment dialogFragment = ApiKeySetterDialogFragment.newInstance(new ApiKeySetterDialogFragment.ConfigSetListener() {
            @Override
            public void onConfigSet(String apiKey) {
                sharedPreferences.edit()
                        .putString(Config.PREFERENCE_API_KEY, apiKey)
                        .commit();

                resultListener.onConfigSet(apiKey);
            }
        }, previousApiKey);

        DialogFragmentUtils.showFragmentAndDismissPrevious(getSupportFragmentManager(), dialogFragment, ConfigSetterDialogFragment.TAG);

    }

    private void showPinCodeEntry(final String apiKey) {

        DialogFragment dialogFragment = PinCodeEntryDialogFragment.newInstance(new PinCodeEntryDialogFragment.OnResultListener() {
            @Override
            public void onResult(String pinCode) {
                startSession(SessionConfig.createWithPinCode(pinCode), apiKey);
            }
        });

        DialogFragmentUtils.showFragmentAndDismissPrevious(getSupportFragmentManager(), dialogFragment, ConfigSetterDialogFragment.TAG);


    }

    /**
     * (Re)starts the session and connects to the given channel.
     *
     */
    private void startSession(final SessionConfig sessionConfig, final String apiKey) {
//        connectChannelButton.setEnabled(false);
//        connectPinButton.setEnabled(false);

        View connectionContainer = findViewById(R.id.connectionContainer);
        connectionContainer.setEnabled(true);

        cleanup();

        SessionFactory.newInstance().create(getApplicationContext(), apiKey, new SessionFactory.SessionCreationCallback() {
            @Override
            public void onSessionCreated(Session session) {
                rescueSession = session;

                // Now we set up our event handlers and add them to the session's event bus.
                // We store them in a list so that we can remove them from the bus later in the
                // cleanup() method.
                eventHandlers = new ArrayList<Object>();

                StringResolver resolver = new StringResolver(RescueSdkDemoActivity.this, session);
                logAdapter.setStringResolver(resolver);
                eventHandlers.add(logAdapter);

                TextView textConnectionStatus = (TextView) findViewById(R.id.textConnectionStatus);
                eventHandlers.add(new ConnectionStatusPresenter(textConnectionStatus));

                View connectionContainer = findViewById(R.id.connectionContainer);
                View sessionStatusContainer = findViewById(R.id.sessionStatusContainer);
                eventHandlers.add(new ConnectionButtonsPresenter(connectionContainer, sessionStatusContainer));

                EditText chatMessage = (EditText) findViewById(R.id.editChatMessage);
                eventHandlers.add(new ChatMessagePresenter(chatMessage));

                Button chatSend = (Button) findViewById(R.id.buttonChatSend);
                eventHandlers.add(new ChatSendPresenter(chatSend, chatMessage));

                TextView typing = (TextView) findViewById(R.id.textTypingNotification);
                eventHandlers.add(new TypingPresenter(typing, resolver));

                Button stopRcButton = (Button) findViewById(R.id.buttonStopRc);
                eventHandlers.add(new StopDisplaySharingPresenter(stopRcButton));

                eventHandlers.add(new ErrorEventHandler(getSupportFragmentManager(), resolver));
                eventHandlers.add(RescueSdkDemoActivity.this);
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

        // Currently we shut down the session when the app is destroyed. This behavior should change in the future.
        cleanup();
    }
}
