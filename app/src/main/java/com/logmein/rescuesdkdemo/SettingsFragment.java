package com.logmein.rescuesdkdemo;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String PIN_MODE = "pinMode";
    public static final String CHANNEL_ID_MODE = "channelIdMode";
    public static final String CHANNEL_NAME_COMPANY_ID_MODE = "channelNameCompanyIdMode";

    public static final String API_KEY = "apiKey";
    public static final String CHANNEL_ID = "channelId";
    public static final String CHANNEL_NAME = "channelName";
    public static final String COMPANY_ID = "companyId";

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
        sessionModeCheckBoxes.add((CheckBoxPreference) getPreferenceManager().findPreference(PIN_MODE));
        sessionModeCheckBoxes.add((CheckBoxPreference) getPreferenceManager().findPreference(CHANNEL_ID_MODE));
        sessionModeCheckBoxes.add((CheckBoxPreference) getPreferenceManager().findPreference(CHANNEL_NAME_COMPANY_ID_MODE));

        for (CheckBoxPreference checkBox : sessionModeCheckBoxes) {
            checkBox.setOnPreferenceClickListener(this);
        }

        apiKeyPref = (EditTextPreference) getPreferenceManager().findPreference(API_KEY);
        channelIdPref = (EditTextPreference) getPreferenceManager().findPreference(CHANNEL_ID);
        channelNamePref = (EditTextPreference) getPreferenceManager().findPreference(CHANNEL_NAME);
        companyIdPref = (EditTextPreference) getPreferenceManager().findPreference(COMPANY_ID);

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
