package com.crazyidea.alsalah.utils

import android.content.Context
import android.content.SharedPreferences
import com.crazyidea.alsalah.data.model.PoleCalculation
import com.crazyidea.alsalah.data.model.PrimaryColor

private const val APP_LANGUAGE = "language"
private const val AZKAR_LANGUAGE = "azkar_language"
private const val PREFS_NAME = "SalahPref"
private const val LATITUDE = "lat"
private const val MAZHAB = "mazhab"
private const val POLE = "pole"
private const val AZAN = "azan"
private const val COLOR = "color"
private const val LONGITUDE = "lng"


private const val AZKAR_MUTED = "azkar-muted"

class GlobalPreferences(context: Context) {
    var context: Context? = null
    private var prefs: SharedPreferences
    private var prefsEditor: SharedPreferences.Editor

    init {
        this.context = context
        prefs = context.getSharedPreferences(PREFS_NAME, 0)
        prefsEditor = prefs.edit()
    }

    fun storeLocale(locale: String) {
        prefsEditor.putString(APP_LANGUAGE, locale)
        prefsEditor.commit()
    }


    fun storeAzkarLanguage(locale: String) {
        prefsEditor.putString(AZKAR_LANGUAGE, locale)
        prefsEditor.commit()
    }

    fun getLocale(): String {
        return prefs.getString(APP_LANGUAGE, "ar")!!
    }

    fun getAzkarLanguage(): String {
        return prefs.getString(AZKAR_LANGUAGE, "ar")!!
    }

    fun getLatitude(): String {
        return prefs.getString(LATITUDE, "")!!
    }

    fun getLongitude(): String {
        return prefs.getString(LONGITUDE, "")!!
    }

    fun getMazhab(): String {
        return prefs.getString(MAZHAB, "others")!!
    }

    fun getPole(): String {
        return prefs.getString(POLE, "NOTHING")!!
    }

    fun getAzan(): String {
        return prefs.getString(AZAN, "الاذان المكي")!!
    }


    fun isAzkarMuted(): Boolean {
        return prefs.getBoolean(AZKAR_MUTED, false)
    }

    fun azkarMuted(muted: Boolean) {
        prefsEditor.putBoolean(AZKAR_MUTED, muted)
        prefsEditor.commit()
    }

    fun getPrimaryColor(): PrimaryColor {
        return when (prefs.getInt(COLOR, 0)) {
            0 -> PrimaryColor.ORANGE
            1 -> PrimaryColor.PINK
            else -> PrimaryColor.BLUE
        }
    }

    fun storePrimaryColor(primColor: PrimaryColor?) {
        var value = 0
        value = when (primColor) {
            PrimaryColor.ORANGE -> 0
            PrimaryColor.PINK -> 1
            else -> 2
        }
        prefsEditor.putInt(COLOR, value)
        prefsEditor.commit()
    }

    fun storeLatitude(latitude: String?) {
        prefsEditor.putString(LATITUDE, latitude)
        prefsEditor.commit()
    }

    fun storeLongitude(longitude: String?) {
        prefsEditor.putString(LONGITUDE, longitude)
        prefsEditor.commit()
    }

    fun saveMazhab(mazhab: String) {
        prefsEditor.putString(MAZHAB, mazhab)
        prefsEditor.commit()
    }

    fun saveAzan(azan: String?) {
        prefsEditor.putString(AZAN, azan)
        prefsEditor.commit()
    }

    fun savePole(pole: PoleCalculation) {
        prefsEditor.putString(POLE, pole.toString())
        prefsEditor.commit()
    }
}