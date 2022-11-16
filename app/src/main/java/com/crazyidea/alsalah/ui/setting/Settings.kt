package com.crazyidea.alsalah.ui.setting

import androidx.datastore.preferences.core.*

object AppSettings{
    val APP_LANGUAGE = stringPreferencesKey("app-language")
    val USER_ID = intPreferencesKey("user-id")
    val LOGGED = booleanPreferencesKey("logged")
    val ARTICLES_LANGUAGE = stringPreferencesKey("articles_language")
    val ARTICLES_NOTIFICATIONS = booleanPreferencesKey("articles_notifications")
    val FRIDAY_NOTIFICATIONS = booleanPreferencesKey("friday_notifications")
    val FASTING_NOTIFICATIONS = booleanPreferencesKey("fasting_notifications")
    val SILENT_PHONE = booleanPreferencesKey("silent_phone")
    val LATITUDE = doublePreferencesKey("latitude")
    val LONGITUDE = doublePreferencesKey("longitude")
    val ACCENT_COLOR = intPreferencesKey("accent-color")
    val LAST_READING = intPreferencesKey("accent-color")
}

object AzanSettings{
    val AZAN_SOUND = intPreferencesKey("azan-sound")
    val AZAN_CHANNEL = stringPreferencesKey("azan-channel")
    val AZAN_MOSQUE_BG = booleanPreferencesKey("azan-mosque_bg")
    val BEFORE_PRAYER_REMINDER = booleanPreferencesKey("before-prayer-reminder")
    val BEFORE_PRAYER_REMINDER_PERIOD = intPreferencesKey("before-prayer-period")
    val IQAMA_NOTIFICATION = booleanPreferencesKey("iqama_notification")
    val WAS_SILENT = booleanPreferencesKey("was_silent")
    val AZAN_NOTIFICATION = booleanPreferencesKey("azan")

}
object SalahSettings{
    val CALCULATION_METHOD = intPreferencesKey("calc-method")
    val SCHOOL = intPreferencesKey("school")
    val TIME_ZONE = intPreferencesKey("time-zone")
    val POLE_CALCULATION = intPreferencesKey("pole-calc")
    val FAJR_MARGIN = intPreferencesKey("fajr-margin")
    val SHOROK_MARGIN = intPreferencesKey("shorok-margin")
    val DHUHR_MARGIN = intPreferencesKey("dhuhr-margin")
    val ASR_MARGIN = intPreferencesKey("asr-margin")
    val MAGHRIB_MARGIN = intPreferencesKey("maghrib-margin")
    val ISHA_MARGIN = intPreferencesKey("isha-margin")

}
object AzkarSettings{
    val LANGUAGE = stringPreferencesKey("azkar-language")
    val VIBRATE = booleanPreferencesKey("vibrate")
    val BEEP = booleanPreferencesKey("Beep")
    val AUTO_NEXT = booleanPreferencesKey("auto-next")
    val MORNING_AZKAR = booleanPreferencesKey("morning-azkar")
    val EVENING_AZKAR = booleanPreferencesKey("evening-azkar")
    val SLEEPING_AZKAR = booleanPreferencesKey("sleeping-azkar")
    val SLEEPING_AZKAR_TIME = longPreferencesKey("sleeping-azkar")
    val AFTER_PRAYER_AZKAR = booleanPreferencesKey("after_prayer_azkar")

}