package com.logmein.rescuesdkdemo.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.logmein.rescuesdkdemo.R;

/**
 * DialogFragment to present error message to the user.
 */
public class ConnectionErrorDialogFragment extends DialogFragment {

    public static final String TAG = "ConnectionErrorDialogFragment";

    /**
     * Factory method to produce ConnectionErrorDialogFragment instance. Use this method for instantiation.
     * @param message The string message to be displayed.
     * @return ConnectionErrorDialogFragment instance.
     */
    public static ConnectionErrorDialogFragment newInstance(final String message) {
        final ConnectionErrorDialogFragment fragment = new ConnectionErrorDialogFragment();
        fragment.setCancelable(false);
        fragment.setErrorMessage(message);
        return fragment;
    }

    private String errorToPresent;

    /**
     * Sets the message to be displayed in the dialog.
     * @param message The message to be displayed.
     */
    private void setErrorMessage(final String message) {
        errorToPresent = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.error_title)
                .setMessage(errorToPresent)
                .setNeutralButton(android.R.string.ok, null);
        return builder.create();
    }
}
