package com.logmein.rescuesdkdemo.core.dialog;

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

import com.logmein.rescuesdkdemo.core.R;

/**
 * DialogFragment to display a PIN-code entry dialog.
 */
public class PinCodeEntryDialogFragment extends DialogFragment {

    public static final String TAG = "PinCodeEntryDialog";

    public interface OnResultListener {
        void onResult(String pinCode);
    }

    /**
     * Factory method to produce PinCodeEntryDialogFragment instance. Use this method for instantiation.
     */
    public static PinCodeEntryDialogFragment newInstance(final OnResultListener listener) {
        final PinCodeEntryDialogFragment fragment = new PinCodeEntryDialogFragment();
        fragment.setOnResultListener(listener);
        return fragment;
    }

    private OnResultListener onResultListener;
    private EditText editPinCode;

    /**
     * Sets the ConfigSetListener to receive callback when the PIN code is set.
     */
    private void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View content = getActivity().getLayoutInflater().inflate(R.layout.pin_code_entry_dialog, null);

        editPinCode = (EditText) content.findViewById(R.id.editPinCode);
        editPinCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFieldValid(s.toString())) {
                    editPinCode.setError(null);
                } else {
                    editPinCode.setError(getString(R.string.empty_field));
                }
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pin_code_entry_dialog_title)
                .setView(content)
                // Button clicks are handled by the DialogFragment!
                .setPositiveButton(R.string.connect, null);

        setCancelable(true);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String pinCode = editPinCode.getText().toString();
                    if (isFieldValid(pinCode)) {
                        if (onResultListener != null) {
                            onResultListener.onResult(pinCode);
                            dialog.dismiss();
                        }
                    } else {
                        editPinCode.setError(getString(R.string.empty_field));
                    }
                }
            });
        }
    }

    private boolean isFieldValid(final String value) {
        return !TextUtils.isEmpty(value.trim());
    }
}
