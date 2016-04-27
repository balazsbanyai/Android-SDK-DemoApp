package com.logmein.rescuesdkdemo.core;

import android.content.SharedPreferences;

import com.logmein.rescuesdkdemo.core.config.Config;

public final class Settings {

    public static final String PIN_MODE_KEY = "pinMode";
    public static final String CHANNEL_ID_MODE_KEY = "channelIdMode";
    public static final String CHANNEL_NAME_COMPANY_ID_MODE_KEY = "channelNameCompanyIdMode";

    public static final String API_KEY_KEY = "apiKey";
    public static final String CHANNEL_ID_KEY = "channelId";
    public static final String CHANNEL_NAME_KEY = "channelName";
    public static final String COMPANY_ID_KEY = "companyId";

    public enum SessionConfigMode {
        PIN_MODE, CHANNEL_ID_MODE, CHANNEL_NAME_COMPANY_ID_MODE
    }

    private final SharedPreferences prefs;

    public Settings(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public String getApiKey() {
        return prefs.getString(API_KEY_KEY, Config.API_KEY);
    }

    public SessionConfigMode getSessionConfigMode() {
        if (prefs.getBoolean(CHANNEL_ID_MODE_KEY, false)) {
            return SessionConfigMode.CHANNEL_ID_MODE;
        } else if (prefs.getBoolean(CHANNEL_NAME_COMPANY_ID_MODE_KEY, false)) {
            return SessionConfigMode.CHANNEL_NAME_COMPANY_ID_MODE;
        } else {
            return SessionConfigMode.PIN_MODE;
        }
    }

    public String getChannelId() {
        return prefs.getString(CHANNEL_ID_KEY, Config.CHANNEL_ID);
    }

    public String getChannelName() {
        return prefs.getString(CHANNEL_NAME_KEY, "");
    }

    public String getCompanyId() {
        return prefs.getString(COMPANY_ID_KEY, "");
    }

}
