package com.logmein.rescuesdkdemo.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.logmein.rescuesdkdemo.R;

/**
 * DialogFragment to display the disclaimer dialog when the Config.API_KEY
 * constant is empty. Also enables to type a channel ID for immediate use.
 */
public class ApiKeySetterDialogFragment extends DialogFragment {

    public static final String TAG = "ApiKeySetterDialog";

    public interface ConfigSetListener {
        public void onConfigSet(String apiKey);
    }

    /**
     * Factory method to produce ApiKeySetterDialogFragment instance. Use this method for instantiation.
     * @param listener The ConfigSetListener implementation to receive callback when the API key is set.
     */
    public static ApiKeySetterDialogFragment newInstance(final ConfigSetListener listener, final String previousApiKey) {
        final ApiKeySetterDialogFragment fragment = new ApiKeySetterDialogFragment();
        fragment.setConfigSetListener(listener);
        fragment.setPreviousApiKey(previousApiKey);
        return fragment;
    }

    private ConfigSetListener configSetListener;
    private EditText editApiKey;
    private String previousApiKey;

    /**
     * The previously set API Key. This key will appear in an editText inside the shown dialog.
     */
    public void setPreviousApiKey(String previousApiKey) {
        this.previousApiKey = previousApiKey;
    }

    /**
     * Sets the ConfigSetListener to receive callback when the channel Id is set.
     */
    private void setConfigSetListener(ConfigSetListener configSetListener) {
        this.configSetListener = configSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View content = getActivity().getLayoutInflater().inflate(R.layout.api_key_setter_dialog, null);

        editApiKey = (EditText) content.findViewById(R.id.editApiKey);
        editApiKey.setText(previousApiKey);
        editApiKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFieldValid(s.toString())) {
                    editApiKey.setError(null);
                } else {
                    editApiKey.setError(getString(R.string.empty_field));
                }
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sessionconfig_dialog_title)
                .setView(content)
                // Button clicks are handled by the DialogFragment!
                .setPositiveButton(android.R.string.ok, null);

        setCancelable(true);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            // We don't want to close the dialog when the user click on OK button, only when the given API key is valid.
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String apiKey = editApiKey.getText().toString();
                    if (isFieldValid(apiKey)) {
                        if (configSetListener != null) {
                            configSetListener.onConfigSet(apiKey);
                            dialog.dismiss();
                        }
                    } else {
                        editApiKey.setError(getString(R.string.empty_field));
                    }
                }
            });
        }
    }

    private boolean isFieldValid(final String value) {
        return !TextUtils.isEmpty(value.trim());
    }
}
