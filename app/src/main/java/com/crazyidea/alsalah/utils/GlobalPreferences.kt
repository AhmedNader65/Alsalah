package com.crazyidea.alsalah.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.crazyidea.alsalah.data.model.PoleCalculation
import com.crazyidea.alsalah.data.model.PrimaryColor
import timber.log.Timber
import java.util.*

private const val AZKAR_LANGUAGE = "azkar_language"
private const val PREFS_NAME = "SalahPref"
private const val LATITUDE = "lat"
private const val PRAYER_CHANNEL = "prayer-channel"
private const val AZAN_CHANGED = "azan_changed"
private const val LAST_PAGE_QURAN = "last-quran"
private const val AZAN = "azannny"
private const val CUSTOM_AZAN = "custom-azan"
private const val COLOR = "color"
private const val LONGITUDE = "lng"

private const val AFTER_PRAYER_AZKAR = "AFTER_PRAYER_AZKAR"
private const val MORNING_AZKAR = "MORNING_AZKAR"
private const val EVENING_AZKAR = "EVENING_AZKAR"
private const val SLEEPING_AZKAR = "SLEEPING_AZKAR"
private const val SLEEPING_TIME = "SLEEPING_TIME"


private const val AZKAR_MUTED = "azkar-muted"
private const val AZKAR_VIBRATE = "azkar-vibrate"

// PRAYER

class GlobalPreferences(context: Context) {
    var context: Context? = null
    private var prefs: SharedPreferences
    private var prefsEditor: SharedPreferences.Editor

    init {
        this.context = context
        prefs = context.getSharedPreferences(PREFS_NAME, 0)
        prefsEditor = prefs.edit()
    }





    fun getLatitude(): String {
        return prefs.getString(LATITUDE, "")!!
    }

    fun getLongitude(): String {
        return prefs.getString(LONGITUDE, "")!!
    }

    fun getAzan(): Int {
        return prefs.getInt(AZAN, 1)
    }

    fun isAfterPrayerNotification(): Boolean {
        return prefs.getBoolean(AFTER_PRAYER_AZKAR, false)
    }

    fun isMorningNotification(): Boolean {
        return prefs.getBoolean(MORNING_AZKAR, true)
    }

    fun isEveningNotification(): Boolean {
        return prefs.getBoolean(EVENING_AZKAR, true)
    }

    fun isSleepingNotification(): Boolean {
        return prefs.getBoolean(SLEEPING_AZKAR, true)
    }

    fun getSleepingTime(): Long {
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        val time = prefs.getLong(SLEEPING_TIME, 1657224000991)
        calendar1.timeInMillis = time
        calendar1.set(Calendar.YEAR, calendar2.get(Calendar.YEAR))
        calendar1.set(Calendar.MONTH, calendar2.get(Calendar.MONTH))
        calendar1.set(Calendar.DAY_OF_MONTH, calendar2.get(Calendar.DAY_OF_MONTH))
        return calendar1.timeInMillis
    }




    fun isAzkarMuted(): Boolean {
        return prefs.getBoolean(AZKAR_MUTED, false)
    }

    fun isAzkarVibrating(): Boolean {
        return prefs.getBoolean(AZKAR_VIBRATE, false)
    }

    fun azkarMuted(muted: Boolean) {
        prefsEditor.putBoolean(AZKAR_MUTED, muted)
        prefsEditor.commit()
    }

    fun azkarVibrate(vibrate: Boolean) {
        prefsEditor.putBoolean(AZKAR_MUTED, vibrate)
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

    fun lastReadingPage(): Int {
        return prefs.getInt(LAST_PAGE_QURAN, 0)
    }

    fun storeLatitude(latitude: String?) {
        prefsEditor.putString(LATITUDE, latitude)
        prefsEditor.commit()
    }

    fun storeLongitude(longitude: String?) {
        prefsEditor.putString(LONGITUDE, longitude)
        prefsEditor.commit()
    }

    fun saveLastReadingPage(page: Int) {
        prefsEditor.putInt(LAST_PAGE_QURAN, page)
        prefsEditor.commit()
    }


    fun saveChannelID(id: String) {
        prefsEditor.putString(PRAYER_CHANNEL, id)
        prefsEditor.commit()
    }

    fun storeAfterPrayerNotification(checked: Boolean) {
        prefsEditor.putBoolean(AFTER_PRAYER_AZKAR, checked)
        prefsEditor.commit()

    }

    fun storeMorningNotification(checked: Boolean) {
        prefsEditor.putBoolean(MORNING_AZKAR, checked)
        prefsEditor.commit()
    }

    fun storeEveningNotification(checked: Boolean) {
        prefsEditor.putBoolean(EVENING_AZKAR, checked)
        prefsEditor.commit()
    }

    fun storeSleepingNotification(checked: Boolean) {
        prefsEditor.putBoolean(SLEEPING_AZKAR, checked)
        prefsEditor.commit()
    }

    fun storeSleepingTime(time: Long) {
        prefsEditor.putLong(SLEEPING_TIME, time)
        prefsEditor.commit()
    }

    fun getPrayerChannelId(): String {
        val channelId = prefs.getString(PRAYER_CHANNEL, null)
        channelId?.let {
            return it
        }.run {
            val newChan = "PRAY_AZAN" + Random().nextInt()
            saveChannelID(newChan)
            return newChan
        }

    }




}