package com.logmein.rescuesdkdemo.eventhandler;

import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.Button;

import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.session.event.ConnectionErrorEvent;
import com.logmein.rescuesdkdemo.dialog.ConnectionErrorDialogFragment;
import com.logmein.rescuesdkdemo.dialog.DialogFragmentUtils;
import com.logmein.rescuesdkresources.StringResolver;

import java.lang.ref.WeakReference;

/**
 * Shows an error dialog when an ErrorEvent is received.
 */
public class ErrorEventHandler {
    private final StringResolver stringResolver;
    private WeakReference<Button> buttonConnect;
    private WeakReference<FragmentManager> fragmentManager;

    /**
     * Constructs an ErrorEventHandler instance with the given parameters.
     * @param fragmentManager The FragmentManager instance which provides the fragment APIs.
     */
    public ErrorEventHandler(final FragmentManager fragmentManager, StringResolver stringResolver) {
        this.fragmentManager = new WeakReference<FragmentManager>(fragmentManager);
        this.stringResolver = stringResolver;
    }

    @Subscribe
    public void onErrorEvent(ConnectionErrorEvent event) {

        final FragmentManager fragmentManager = this.fragmentManager.get();
        if (fragmentManager != null) {
            final String message = stringResolver.resolve(event);
            if (!TextUtils.isEmpty(message)) {
                DialogFragmentUtils.showFragmentAndDismissPrevious(fragmentManager, ConnectionErrorDialogFragment.newInstance(message), ConnectionErrorDialogFragment.TAG);
            }
        }
    }
}
