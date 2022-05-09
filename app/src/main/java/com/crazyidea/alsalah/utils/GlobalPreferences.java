package com.crazyidea.alsalah.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class GlobalPreferences {

    private static final String APP_LANGUAGE = "language";
    private static final String PREFS_NAME = "SalahPref";
    Context context;
    private SharedPreferences prefs;
    private final SharedPreferences.Editor prefsEditor;


    public GlobalPreferences(Context context) {
        this.context = context;
        prefs = null;
        prefs = context.getSharedPreferences(PREFS_NAME, 0);
        prefsEditor = prefs.edit();
    }


    public void storeLocale(String locale) {
        prefsEditor.putString(APP_LANGUAGE, locale);
        prefsEditor.commit();
    }
    public String getLocale() {
        return prefs.getString(APP_LANGUAGE, "ar");
    }
}
