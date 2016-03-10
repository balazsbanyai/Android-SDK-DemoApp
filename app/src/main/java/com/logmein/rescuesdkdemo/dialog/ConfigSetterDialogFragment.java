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
 * DialogFragment to display the disclaimer dialog when the Config.CHANNEL_ID_KEY constant is empty. Also enables to type a channel ID for immediate use.
 */
public class ConfigSetterDialogFragment extends DialogFragment {

    public static final String TAG = "ChannelSetterDialog";

    public interface ConfigSetListener {
        public void onConfigSet(final String channelId, String apiKey);
    }

    /**
     * Factory method to produce ConfigSetterDialogFragment instance. Use this method for instantiation.
     * @param listener The ConfigSetListener implementation to receive callback when the channel Id is set.
     * @param previousChannelId The previously entered channel Id.
     * @return ConfigSetterDialogFragment instance.
     */
    public static ConfigSetterDialogFragment newInstance(final ConfigSetListener listener, final String previousChannelId, final String previousApiKey) {
        final ConfigSetterDialogFragment fragment = new ConfigSetterDialogFragment();
        fragment.setConfigSetListener(listener);
        fragment.setPreviousChannelId(previousChannelId);
        fragment.setPreviousApiKey(previousApiKey);
        return fragment;
    }

    private ConfigSetListener configSetListener;
    private EditText editChannelId;
    private EditText editApiKey;
    private String previousChannelId;
    private String previousApiKey;

    /**
     * The previously set channel Id. This Id will appear in an editText inside the shown dialog.
     * @param previousChannelId The previous channel Id.
     */
    public void setPreviousChannelId(String previousChannelId) {
        this.previousChannelId = previousChannelId;
    }

    /**
     * The previously set Api Key. This Key will appear in an editText inside the shown dialog.
     * @param previousApiKey The previous Api Key.
     */
    public void setPreviousApiKey(String previousApiKey) {
        this.previousApiKey = previousApiKey;
    }

    /**
     * Sets the ConfigSetListener to receive callback when the channel Id is set.
     * @param configSetListener ConfigSetListener implementation.
     */
    private void setConfigSetListener(ConfigSetListener configSetListener) {
        this.configSetListener = configSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View content = getActivity().getLayoutInflater().inflate(R.layout.config_setter_dialog, null);
        editChannelId = (EditText) content.findViewById(R.id.editChannelId);
        editChannelId.setText(previousChannelId);
        editChannelId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                final String text = s.toString();
                if (isFieldValid(text)) {
                    editChannelId.setError(null);
                } else {
                    editChannelId.setError(getString(R.string.empty_field));
                }
            }
        });
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
            // We don't want to close the dialog when the user click on OK button, only when the given channelId is valid.
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String channelId = editChannelId.getText().toString();
                    final String apiKey = editApiKey.getText().toString();
                    if (isFieldValid(channelId) && isFieldValid(apiKey)) {
                        if (configSetListener != null) {
                            configSetListener.onConfigSet(channelId, apiKey);
                            dialog.dismiss();
                        }
                    } else {
                        if (!isFieldValid(channelId)) {
                            editChannelId.setError(getString(R.string.empty_field));
                        }
                        if (!isFieldValid(apiKey)) {
                            editApiKey.setError(getString(R.string.empty_field));
                        }
                    }
                }
            });
        }
    }

    private boolean isFieldValid(final String value) {
        return !TextUtils.isEmpty(value.trim());
    }
}
