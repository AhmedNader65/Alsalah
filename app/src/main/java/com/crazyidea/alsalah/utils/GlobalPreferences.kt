package com.crazyidea.alsalah.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.crazyidea.alsalah.data.model.PoleCalculation
import com.crazyidea.alsalah.data.model.PrimaryColor
import timber.log.Timber
import java.util.*

private const val APP_LANGUAGE = "language"
private const val AZKAR_LANGUAGE = "azkar_language"
private const val PREFS_NAME = "SalahPref"
private const val LATITUDE = "lat"
private const val CALCULATION_METHOD = "calc_method"
private const val SCHOOL_METHOD = "school_method"
private const val USER_ID = "user_id"
private const val POLE = "pole"
private const val PRAYER_CHANNEL = "prayer-channel"
private const val AZAN_CHANGED = "azan_changed"
private const val LAST_PAGE_QURAN = "last-quran"
private const val AZAN = "azannny"
private const val CUSTOM_AZAN = "custom-azan"
private const val COLOR = "color"
private const val LONGITUDE = "lng"
private const val LOGGED = "logged"

private const val FAJR_MODIFICATION = "fajr-modification"
private const val SHOROOK_MODIFICATION = "shorok-modification"
private const val ZUHR_MODIFICATION = "zuhr-modification"
private const val ASR_MODIFICATION = "asr-modification"
private const val MAGHRIB_MODIFICATION = "maghrib-modification"
private const val ISHA_MODIFICATION = "isha-modification"
private const val AFTER_PRAYER_AZKAR = "AFTER_PRAYER_AZKAR"
private const val MORNING_AZKAR = "MORNING_AZKAR"
private const val EVENING_AZKAR = "EVENING_AZKAR"
private const val SLEEPING_AZKAR = "SLEEPING_AZKAR"
private const val SLEEPING_TIME = "SLEEPING_TIME"


private const val AZKAR_MUTED = "azkar-muted"
private const val AZKAR_VIBRATE = "azkar-vibrate"

// PRAYER

private const val BEFORE_PRAYER_NOTIFICATION = "BEFORE_PRAYER_NOTIFICATION"
private const val IQAMA_NOTIFICATION = "IQAMA_NOTIFICATION"
private const val BEFORE_PRAYER_NOTIFICATION_TIME = "BEFORE_PRAYER_NOTIFICATION_TIME"
private const val AZAN_BACKGROUND_MOSQUE = "AZAN_BACKGROUND_MOSQUE"

class GlobalPreferences(context: Context) {
    var context: Context? = null
    private var prefs: SharedPreferences
    private var prefsEditor: SharedPreferences.Editor

    init {
        this.context = context
        prefs = context.getSharedPreferences(PREFS_NAME, 0)
        prefsEditor = prefs.edit()
    }

    fun storeUserId(userId: Int) {
        prefsEditor.putInt(USER_ID, userId)
        prefsEditor.commit()
    }

    fun storeLocale(locale: String) {
        prefsEditor.putString(APP_LANGUAGE, locale)
        prefsEditor.commit()
    }

    fun storeCalculationMethod(method: Int) {
        prefsEditor.putInt(CALCULATION_METHOD, method)
        prefsEditor.commit()
    }

    fun storeSchoolMethod(school: Int) {
        prefsEditor.putInt(SCHOOL_METHOD, school)
        prefsEditor.commit()
    }

    fun storeFajrMod(mod: Int) {
        prefsEditor.putInt(FAJR_MODIFICATION, mod)
        prefsEditor.commit()
    }

    fun storeShorookMod(mod: Int) {
        prefsEditor.putInt(SHOROOK_MODIFICATION, mod)
        prefsEditor.commit()
    }

    fun storeZuhrMod(mod: Int) {
        prefsEditor.putInt(ZUHR_MODIFICATION, mod)
        prefsEditor.commit()
    }

    fun storeAsrMod(mod: Int) {
        prefsEditor.putInt(ASR_MODIFICATION, mod)
        prefsEditor.commit()
    }

    fun storeMaghribMod(mod: Int) {
        prefsEditor.putInt(MAGHRIB_MODIFICATION, mod)
        prefsEditor.commit()
    }

    fun storeIshaMod(mod: Int) {
        prefsEditor.putInt(ISHA_MODIFICATION, mod)
        prefsEditor.commit()
    }


    fun storeAzkarLanguage(locale: String) {
        prefsEditor.putString(AZKAR_LANGUAGE, locale)
        prefsEditor.commit()
    }

    fun storeLogged(logged: Boolean) {
        prefsEditor.putBoolean(LOGGED, logged)
        prefsEditor.commit()
    }

    fun getLogged(): Boolean {
        return prefs.getBoolean(LOGGED, false)
    }

    fun getLocale(): String {
        return prefs.getString(APP_LANGUAGE, "ar")!!
    }

    fun getUserId(): Int {
        return prefs.getInt(USER_ID, 0)
    }

    fun getCalculationMethod(): Int {
        return prefs.getInt(CALCULATION_METHOD, 5)
    }

    fun getSchool(): Int {
        return prefs.getInt(SCHOOL_METHOD, 0)
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

    fun getPole(): String {
        return prefs.getString(POLE, "NOTHING")!!
    }

    fun getAzan(): Int {
        return prefs.getInt(AZAN, 1)
    }

    fun getFajrModification(): Int {
        return prefs.getInt(FAJR_MODIFICATION, 0)
    }

    fun getShorookModification(): Int {
        return prefs.getInt(SHOROOK_MODIFICATION, 0)
    }

    fun getZuhrModification(): Int {
        return prefs.getInt(ZUHR_MODIFICATION, 0)
    }

    fun getAsrModification(): Int {
        return prefs.getInt(ASR_MODIFICATION, 0)
    }

    fun getMaghribModification(): Int {
        return prefs.getInt(MAGHRIB_MODIFICATION, 0)
    }

    fun getIshaModification(): Int {
        return prefs.getInt(ISHA_MODIFICATION, 0)
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


    fun getCustomAzanUri(): Uri {
        return Uri.parse(prefs.getString(CUSTOM_AZAN, ""))
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

    fun saveAzan(azan: Int) {
        prefsEditor.putInt(AZAN, azan)
        prefsEditor.commit()
        saveChannelID("PRAYER" + Random().nextInt())
    }

    fun saveCustomAzanUri(uri: Uri) {
        prefsEditor.putBoolean(AZAN_CHANGED, true)
        prefsEditor.putString(CUSTOM_AZAN, uri.toString())
        prefsEditor.commit()
    }

    fun saveLastReadingPage(page: Int) {
        prefsEditor.putInt(LAST_PAGE_QURAN, page)
        prefsEditor.commit()
    }

    fun savePole(pole: PoleCalculation) {
        prefsEditor.putString(POLE, pole.toString())
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


    // PRAYER SETTINGS
    fun storeBeforePrayerNotification(checked: Boolean) {
        prefsEditor.putBoolean(BEFORE_PRAYER_NOTIFICATION, checked)
        prefsEditor.commit()
    }
    fun notifyBeforePrayer() :Boolean = prefs.getBoolean(BEFORE_PRAYER_NOTIFICATION,true)

    fun storeBeforePrayerNotificationPeriod(minutes: Int) {
        prefsEditor.putInt(BEFORE_PRAYER_NOTIFICATION_TIME, minutes)
        prefsEditor.commit()
    }
    fun beforeAzanNotificationPeriod() :Int = prefs.getInt(BEFORE_PRAYER_NOTIFICATION_TIME,10)

    fun notifyIqama() :Boolean = prefs.getBoolean(IQAMA_NOTIFICATION,true)

    fun setAzanBackgroundMosque(mosque:Boolean) {
        prefsEditor.putBoolean(AZAN_BACKGROUND_MOSQUE, mosque)
        prefsEditor.commit()
    }
    fun getAzanBackground()= prefs.getBoolean(AZAN_BACKGROUND_MOSQUE,true)


}