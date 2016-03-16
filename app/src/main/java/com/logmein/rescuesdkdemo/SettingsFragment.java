package com.logmein.rescuesdkdemo;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.InputType;
import android.text.TextUtils;

import java.util.ArrayList;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public SettingsFragment() {
        // Required empty public constructor
    }

    ArrayList<CheckBoxPreference> sessionModeCheckBoxes;

    EditTextPreference apiKeyPref;
    EditTextPreference channelIdPref;
    EditTextPreference channelNamePref;
    EditTextPreference companyIdPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        sessionModeCheckBoxes = new ArrayList();
        sessionModeCheckBoxes.add((CheckBoxPreference) getPreferenceManager().findPreference(Settings.PIN_MODE_KEY));
        sessionModeCheckBoxes.add((CheckBoxPreference) getPreferenceManager().findPreference(Settings.CHANNEL_ID_MODE_KEY));
        sessionModeCheckBoxes.add((CheckBoxPreference) getPreferenceManager().findPreference(Settings.CHANNEL_NAME_COMPANY_ID_MODE_KEY));

        for (CheckBoxPreference checkBox : sessionModeCheckBoxes) {
            checkBox.setOnPreferenceClickListener(this);
        }

        apiKeyPref = (EditTextPreference) getPreferenceManager().findPreference(Settings.API_KEY_KEY);
        channelIdPref = (EditTextPreference) getPreferenceManager().findPreference(Settings.CHANNEL_ID_KEY);
        channelNamePref = (EditTextPreference) getPreferenceManager().findPreference(Settings.CHANNEL_NAME_KEY);
        companyIdPref = (EditTextPreference) getPreferenceManager().findPreference(Settings.COMPANY_ID_KEY);

        apiKeyPref.getEditText().setSingleLine();
        apiKeyPref.getEditText().setLines(1);
        channelNamePref.getEditText().setSingleLine();
        channelNamePref.getEditText().setLines(1);

        channelIdPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        companyIdPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        updateSummaries();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        for (CheckBoxPreference cbp: sessionModeCheckBoxes) {
            if (!cbp.getKey().equals(preference.getKey())) {
                cbp.setChecked(false);
            }
        }

        if (preference instanceof CheckBoxPreference) {
            boolean anyMethodSelected = false;
            for (CheckBoxPreference cbp : sessionModeCheckBoxes) {
                if (cbp.isChecked()) {
                    anyMethodSelected = true;
                    break;
                }
            }

            if (!anyMethodSelected) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                checkBoxPreference.setChecked(true);
            }
        }

        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummaries();
    }

    private void updateSummaries() {
        updateSummary(apiKeyPref);
        updateSummary(channelIdPref);
        updateSummary(channelNamePref);
        updateSummary(companyIdPref);
    }

    private void updateSummary(EditTextPreference pref) {
        pref.setSummary(TextUtils.isEmpty(pref.getText()) ? getResources().getString(R.string.not_set) : pref.getText());
    }


}
