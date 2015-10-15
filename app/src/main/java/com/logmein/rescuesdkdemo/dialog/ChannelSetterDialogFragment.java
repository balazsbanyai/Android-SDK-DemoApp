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
 * DialogFragment to display the disclaimer dialog when the Config.CHANNEL_ID constant is empty. Also enables to type a channel ID for immediate use.
 */
public class ChannelSetterDialogFragment extends DialogFragment {

    public static final String TAG = "ChannelSetterDialog";

    public interface ChannelSetListener {
        public void onChannelIdSet(final String channelId);
    }

    /**
     * Factory method to produce ChannelSetterDialogFragment instance. Use this method for instantiation.
     * @param listener The ChannelSetListener implementation to receive callback when the channel Id is set.
     * @param previousChannelId The previously entered channel Id.
     * @return ChannelSetterDialogFragment instance.
     */
    public static ChannelSetterDialogFragment newInstance(final ChannelSetListener listener, final String previousChannelId) {
        final ChannelSetterDialogFragment fragment = new ChannelSetterDialogFragment();
        fragment.setChannelSetListener(listener);
        fragment.setPreviousChannelId(previousChannelId);
        fragment.setCancelable(false);
        return fragment;
    }

    private ChannelSetListener channelSetListener;
    private EditText editChannelId;
    private String previousChannelId;

    /**
     * The previously set channel Id. This Id will appear in in the editText the dialog is shown.
     * @param previousChannelId The previous channel Id.
     */
    public void setPreviousChannelId(String previousChannelId) {
        this.previousChannelId = previousChannelId;
    }

    /**
     * Sets the ChannelSetListener to receive callback when the channel Id is set.
     * @param channelSetListener ChannelSetListener implementation.
     */
    private void setChannelSetListener(ChannelSetListener channelSetListener) {
        this.channelSetListener = channelSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View content = getActivity().getLayoutInflater().inflate(R.layout.channel_setter_dialog, null);
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
                if (isValidChannelId(text)) {
                    editChannelId.setError(null);
                } else {
                    editChannelId.setError(getString(R.string.invalid_channel_id));
                }
            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.channel_id_dialog_title)
                .setView(content)
                // Button clicks are handled by the DialogFragment!
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(R.string.exit, null);
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
                    dialog.dismiss();
                    final String channelId = editChannelId.getText().toString();
                    if (isValidChannelId(channelId)) {
                        if (channelSetListener != null) {
                            channelSetListener.onChannelIdSet(channelId);
                        }
                    } else {
                        editChannelId.setError(getString(R.string.invalid_channel_id));
                    }
                }
            });
            Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
    }

    private boolean isValidChannelId(final String channelId) {
        return !TextUtils.isEmpty(channelId.trim());
    }
}
