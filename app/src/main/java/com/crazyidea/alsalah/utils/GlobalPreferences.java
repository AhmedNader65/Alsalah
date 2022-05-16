package com.crazyidea.alsalah.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class GlobalPreferences {

    private static final String APP_LANGUAGE = "language";
    private static final String PREFS_NAME = "SalahPref";
    private static final String LATITUIDE = "lat";
    private static final String LONGITUIDE = "lng";
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
    public String getLatituide() {
        return prefs.getString(LATITUIDE, "");
    }
    public String getLongituide() {
        return prefs.getString(LONGITUIDE, "");
    }

    public void storeLatituide(String  latitude) {
        prefsEditor.putString(LATITUIDE, latitude);
        prefsEditor.commit();

    }
    public void storeLongituide(String  longituide) {
        prefsEditor.putString(LONGITUIDE, longituide);
        prefsEditor.commit();

    }
}
