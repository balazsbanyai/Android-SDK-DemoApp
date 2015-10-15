package com.logmein.rescuesdkdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.session.Session;
import com.logmein.rescuesdk.api.session.SessionFactory;
import com.logmein.rescuesdk.api.session.config.SessionConfig;
import com.logmein.rescuesdk.api.session.event.ConnectedEvent;
import com.logmein.rescuesdk.api.session.event.ConnectingEvent;
import com.logmein.rescuesdk.api.session.event.DisconnectedEvent;
import com.logmein.rescuesdk.api.session.event.NoSuchChannelEvent;
import com.logmein.rescuesdkdemo.adapter.LogsAdapter;
import com.logmein.rescuesdkdemo.config.Config;
import com.logmein.rescuesdkdemo.dialog.ChannelSetterDialogFragment;
import com.logmein.rescuesdkdemo.dialog.DialogFragmentUtils;
import com.logmein.rescuesdkdemo.eventhandler.ChatEventHandler;
import com.logmein.rescuesdkdemo.eventhandler.ConnectionEventHandler;
import com.logmein.rescuesdkdemo.eventhandler.ErrorEventHandler;
import com.logmein.rescuesdkdemo.eventhandler.RcEventHandler;
import com.logmein.rescuesdkresources.StringResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
            if (TextUtils.isEmpty(Config.CHANNEL_ID)) {
                showChannelIdSetter();
            } else {
                startSession(Config.CHANNEL_ID);
            }
        }
    }

    /**
     * OnClickListener implementation which initiates Session disconnection.
     */
    private class DisconnectSessionStrategy implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            buttonConnection.setEnabled(false);
            if (rescueSession != null) {
                rescueSession.disconnect();
            }
        }
    }

    private Button buttonConnection;

    private Session rescueSession;
    private List<Object> eventHandlers;
    private LogsAdapter logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue_sdk_demo);

        buttonConnection = (Button) findViewById(R.id.buttonConnection);
        buttonConnection.setOnClickListener(new CreateSessionStrategy());

        final ListView logsView = (ListView) findViewById(R.id.listLogs);
        logAdapter = new LogsAdapter(this);

        logsView.setAdapter(logAdapter);
    }

    /**
     * Shows the channel id setter dialog.
     */
    private void showChannelIdSetter() {
        DialogFragmentUtils.showFragmentAndDismissPrevious(getSupportFragmentManager(), ChannelSetterDialogFragment.newInstance(new ChannelSetterDialogFragment.ChannelSetListener() {
            @Override
            public void onChannelIdSet(String channelId) {
                PreferenceManager.getDefaultSharedPreferences(RescueSdkDemoActivity.this).edit().putString(Config.PREFERENCE_CHANNEL_ID, channelId).commit();
                startSession(channelId);
            }
        }, PreferenceManager.getDefaultSharedPreferences(this).getString(Config.PREFERENCE_CHANNEL_ID, null)), ChannelSetterDialogFragment.TAG);
    }

    /**
     * (Re)starts the session and connects to the given channel.
     *
     * @param channelId The id of the channel to join to.
     */
    private void startSession(final String channelId) {
        buttonConnection.setEnabled(false);

        cleanup();

        // Creating a Session object may take a considerable amount of time so we create a task
        // that will invoke it from a worker thread.
        AsyncTask<Void, Void, Session> sessionStarterTask = new AsyncTask<Void, Void, Session>() {
            @Override
            protected Session doInBackground(Void... params) {
                return SessionFactory.newInstance().create(RescueSdkDemoActivity.this);
            }

            @Override
            protected void onPostExecute(Session session) {
                // This is executed on the UI thread after the Session object has been created on the
                // worker thread.

                super.onPostExecute(session);

                rescueSession = session;

                // Now we set up our event handlers and add them to the session's event bus.
                // We store them in a list so that we can remove them from the bus later in the
                // cleanup() method.
                logAdapter.onSessionCreated(rescueSession);
                eventHandlers = new ArrayList<Object>();
                eventHandlers.add(logAdapter);
                eventHandlers.add(new ConnectionEventHandler((TextView) findViewById(R.id.textConnectionStatus), (Button) findViewById(R.id.buttonConnection)));
                eventHandlers.add(new ChatEventHandler((EditText) findViewById(R.id.editChatMessage), (Button) findViewById(R.id.buttonChatSend), (TextView) findViewById(R.id.textTypingNotification)));
                eventHandlers.add(new RcEventHandler((Button) findViewById(R.id.buttonStopRc)));
                eventHandlers.add(new ErrorEventHandler(buttonConnection, getSupportFragmentManager(), new StringResolver(RescueSdkDemoActivity.this, rescueSession)));
                eventHandlers.add(RescueSdkDemoActivity.this);
                for (final Object eventHandler : eventHandlers) {
                    rescueSession.getEventBus().add(eventHandler);
                }

                logAdapter.clear();

                // After everything is set up, we connect the session to the channel.
                rescueSession.connect(SessionConfig.createWithChannelId(channelId));
            }
        };

        // Start the task
        sessionStarterTask.execute();
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
        buttonConnection.setOnClickListener(new DisconnectSessionStrategy());
    }

    /**
     * Local subscription to the DisconnectedEvent, chooses the proper OnClickListener.
     *
     * @param event DisconnectedEvent object.
     */
    @Subscribe
    public void onDisconnectedEvent(DisconnectedEvent event) {
        buttonConnection.setOnClickListener(new CreateSessionStrategy());
    }

}
