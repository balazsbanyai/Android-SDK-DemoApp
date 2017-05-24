package com.logmein.rescuesdkdemo.camerastreamingapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.ext.CameraStreamView;
import com.logmein.rescuesdk.api.ext.CameraStreamingExtension;
import com.logmein.rescuesdk.api.session.Session;
import com.logmein.rescuesdk.api.session.SessionFactory;
import com.logmein.rescuesdk.api.session.config.SessionConfig;
import com.logmein.rescuesdk.api.streaming.camera.event.CameraUnableToStartEvent;
import com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler.FlashTogglePresenter;
import com.logmein.rescuesdkdemo.core.eventhandler.PauseStreamingPresenter;
import com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler.RenderTooglePresenter;
import com.logmein.rescuesdkdemo.camerastreamingapp.eventhandler.StopStreamingPresenter;
import com.logmein.rescuesdkdemo.core.Settings;
import com.logmein.rescuesdkdemo.core.SettingsActivity;
import com.logmein.rescuesdkdemo.core.dialog.PinCodeEntryDialogFragment;
import com.logmein.rescuesdkdemo.core.eventhandler.ConnectButtonPresenter;
import com.logmein.rescuesdkdemo.core.eventhandler.ConnectionStatusPresenter;
import com.logmein.rescuesdkdemo.core.eventhandler.DisconnectButtonPresenter;
import com.logmein.rescuesdkdemo.core.eventhandler.ErrorEventHandler;
import com.logmein.rescuesdkresources.StringResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to demonstrate camera streaming features of RescueSDK.
 */
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_FOR_CAMERA = 1;

    private Button connectButton;
    private Button disconnectButton;
    private CameraStreamView cameraStreamView;
    private RenderTooglePresenter renderTogglePresenter;

    /**
     * OnClickListener implementation which initiates Session connection based on session configuration.
     */
    private class OnConnectListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            Settings settings = new Settings(prefs);

            switch (settings.getSessionConfigMode()) {
                case PIN_MODE:
                    DialogFragment pinEntryFragment = PinCodeEntryDialogFragment.newInstance(new PinCodeEntryDialogFragment.OnResultListener() {
                        @Override
                        public void onResult(String pinCode) {
                            startSession(SessionConfig.createWithPinCode(pinCode));
                        }
                    });
                    pinEntryFragment.show(getSupportFragmentManager(), PinCodeEntryDialogFragment.TAG);

                    break;

                case CHANNEL_ID_MODE:
                    startSession(SessionConfig.createWithChannelId(settings.getChannelId()));
                    break;

                case CHANNEL_NAME_COMPANY_ID_MODE:
                    startSession(SessionConfig.createWithChannelNameAndCompanyId(settings.getChannelName(), settings.getCompanyId()));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = (Button) findViewById(R.id.buttonConnect);
        connectButton.setOnClickListener(new OnConnectListener());

        disconnectButton = (Button) findViewById(R.id.buttonDisconnect);
        disconnectButton.setOnClickListener(new OnDisconnectListener());

        cameraStreamView = (CameraStreamView) findViewById(R.id.camera_stream_view);

        int cameraPermissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (cameraPermissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_FOR_CAMERA);
        } else {
            createNewSession();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FOR_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    createNewSession();

                } else {
                    finish();
                }
            }
            break;

        }
    }


    private void createNewSession() {
        createNewSession(null);
    }

    private void createNewSession(final Runnable whenSessionCreated) {
        if (rescueSession!= null ) {
            for (final Object eventHandler : eventHandlers) {
                rescueSession.getEventBus().remove(eventHandler);
            }
            eventHandlers.clear();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Settings settings = new Settings(prefs);
        final String apiKey = settings.getApiKey();
        SessionFactory factory = SessionFactory.newInstance();
        factory.useExtension(CameraStreamingExtension.class);
        factory.useExtension(AudioStreamingExtension.class);
        factory.create(getApplicationContext(), apiKey, new SessionFactory.SessionCreationCallback() {
            @Override
            public void onSessionCreated(Session session) {
                rescueSession = session;
                addHandlers();
                CameraStreamingExtension extension = session.getExtension(CameraStreamingExtension.class);
                extension.startRendering(cameraStreamView);
                renderTogglePresenter.setButtonOn();
                // After everything is set up, the session is ready to be connected

                if (whenSessionCreated != null) {
                    whenSessionCreated.run();
                }
            }
        });
    }

    private void addHandlers() {
        // Now we set up our event handlers and add them to the session's event bus.
        // We store them in a list so that we can remove them from the bus later in the
        // cleanup() method.
        eventHandlers = new ArrayList<Object>();

        StringResolver resolver = new StringResolver(MainActivity.this, rescueSession);

        TextView textConnectionStatus = (TextView) findViewById(R.id.textConnectionStatus);
        eventHandlers.add(new ConnectionStatusPresenter(textConnectionStatus, resolver));

        Button connectButton = (Button) findViewById(R.id.buttonConnect);
        eventHandlers.add(new ConnectButtonPresenter(connectButton));

        Button disconnectButton = (Button) findViewById(R.id.buttonDisconnect);
        eventHandlers.add(new DisconnectButtonPresenter(disconnectButton));

        Button stopStreamingButton = (Button) findViewById(R.id.buttonStopStreaming);
        eventHandlers.add(new StopStreamingPresenter(stopStreamingButton));

        Button pauseStreamingButton = (Button) findViewById(R.id.buttonPauseStreaming);
        eventHandlers.add(new PauseStreamingPresenter(pauseStreamingButton));

        eventHandlers.add(new ErrorEventHandler(getSupportFragmentManager(), resolver));
        eventHandlers.add(MainActivity.this);

        CameraStreamingExtension extension = rescueSession.getExtension(CameraStreamingExtension.class);
        Switch flashToggleButton = (Switch) findViewById(R.id.buttonFlashToggle);
        eventHandlers.add(new FlashTogglePresenter(flashToggleButton, extension));

        Switch toogleRenderButton = (Switch) findViewById(R.id.buttonToogleRendering);
        toogleRenderButton.setVisibility(View.VISIBLE);
        renderTogglePresenter = new RenderTooglePresenter(toogleRenderButton, extension, cameraStreamView);
        eventHandlers.add(renderTogglePresenter);

        for (final Object eventHandler : eventHandlers) {
            rescueSession.getEventBus().add(eventHandler);
        }
    }

    /**
     * (Re)starts the session and connects using the given configuration.
     */
    private void startSession(final SessionConfig sessionConfig) {

        connectButton.setEnabled(false);

        Runnable connectSessionTask = new Runnable() {
            @Override
            public void run() {
                rescueSession.connect(sessionConfig);
            }
        };

        cleanup();
        createNewSession(connectSessionTask);
    }

    /**
     * Cleans up the previous Session and aggregated objects.
     */
    private void cleanup() {
        if (rescueSession != null) {

            CameraStreamingExtension e = rescueSession.getExtension(CameraStreamingExtension.class);
            e.stopRendering();
            renderTogglePresenter.setButtonOff();


            rescueSession.disconnect();
            rescueSession = null;
        }
    }

    @Subscribe
    public void onUnableToStartCamera(CameraUnableToStartEvent e) {
        CharSequence warningText = "Unable to access camera! It may be used by another app!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, warningText, duration);
        toast.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Shut down the session when the demo app is destroyed.
        cleanup();
    }
}
