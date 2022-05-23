package com.crazyidea.alsalah.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.crazyidea.alsalah.R;
import com.crazyidea.alsalah.data.model.PoleCalculation;

import org.jetbrains.annotations.NotNull;


public class GlobalPreferences {

    private static final String APP_LANGUAGE = "language";
    private static final String AZKAR_LANGUAGE = "azkar_language";
    private static final String PREFS_NAME = "SalahPref";
    private static final String LATITUIDE = "lat";
    private static final String MAZHAB = "mazhab";
    private static final String POLE = "pole";
    private static final String AZAN = "azan";
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


    public void storeAzkarLanguage(String locale) {
        prefsEditor.putString(AZKAR_LANGUAGE, locale);
        prefsEditor.commit();
    }

    public String getLocale() {
        return prefs.getString(APP_LANGUAGE, "ar");
    }

    public String getAzkarLanguage() {
        return prefs.getString(AZKAR_LANGUAGE, "ar");
    }

    public String getLatituide() {
        return prefs.getString(LATITUIDE, "");
    }

    public String getLongituide() {
        return prefs.getString(LONGITUIDE, "");
    }

    public String getMazhab() {
        return prefs.getString(MAZHAB, "others");
    }

    public String getPole() {
        return prefs.getString(POLE, "NOTHING");
    }

    public String getAzan() {
        return prefs.getString(AZAN, "الاذان المكي");
    }

    public void storeLatituide(String latitude) {
        prefsEditor.putString(LATITUIDE, latitude);
        prefsEditor.commit();

    }

    public void storeLongituide(String longituide) {
        prefsEditor.putString(LONGITUIDE, longituide);
        prefsEditor.commit();

    }

    public void saveMazhab(@NotNull String mazhab) {
        prefsEditor.putString(MAZHAB, mazhab);
        prefsEditor.commit();

    }

    public void saveAzan(String azan) {
        prefsEditor.putString(AZAN, azan);
        prefsEditor.commit();

    }

    public void savePole(PoleCalculation pole) {
        prefsEditor.putString(POLE, pole.toString());
        prefsEditor.commit();

    }
}
