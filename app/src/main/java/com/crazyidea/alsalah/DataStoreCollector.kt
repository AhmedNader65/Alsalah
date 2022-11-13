package com.crazyidea.alsalah

import com.crazyidea.alsalah.data.DataStoreManager
import com.crazyidea.alsalah.ui.setting.AppSettings
import com.crazyidea.alsalah.ui.setting.AzanSettings
import com.crazyidea.alsalah.ui.setting.AzkarSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

object DataStoreCollector {
    var userId: Int = 0
    var loggedIn: Boolean = false
    var articlesLanguage: String = "all"
    var showArticlesNotifications: Boolean = true
    var fridayNotifications: Boolean = false
    var fastingNotifications: Boolean = false
    var silentPhone: Boolean = false
    var lat: Double = 0.0
    var lng: Double = 0.0
    var accentColor: Int = 0
    var lastReading: Int = 0


    // AZAN SETTINGS
    object AzanPrefs {
        var azanSound: Int = 1
        var backgroundMosqueStyle: Boolean = true
        var notifyBeforePrayer: Boolean = true
        var notifyBeforePrayerPeriod: Int = 10
        var notifyIqama: Boolean = true
        var notifyAzan: Boolean = true
        var channel: String = "PRAYER" + Random().nextInt()
    }

    // AZKAR SETTINGS
    object AzkarPrefs {
        var azkarBeep: Boolean = true
        var azkarVibrate: Boolean = true
    }

    fun startCollecting(dataStoreManager: DataStoreManager) {

        GlobalScope.launch(Dispatchers.IO)
        {

            dataStoreManager.settingsDataStore.data.collect { preferences ->
                Timber.e("collecting settings")
                userId = preferences[AppSettings.USER_ID] ?: 0
                loggedIn = preferences[AppSettings.LOGGED] ?: false
                articlesLanguage = preferences[AppSettings.ARTICLES_LANGUAGE] ?: "all"
                showArticlesNotifications = preferences[AppSettings.ARTICLES_NOTIFICATIONS] ?: true
                fridayNotifications = preferences[AppSettings.FRIDAY_NOTIFICATIONS] ?: false
                fastingNotifications = preferences[AppSettings.FASTING_NOTIFICATIONS] ?: false
                silentPhone = preferences[AppSettings.SILENT_PHONE] ?: false
                lat = preferences[AppSettings.LATITUDE] ?: 0.0
                lng = preferences[AppSettings.LONGITUDE] ?: 0.0
                accentColor = preferences[AppSettings.ACCENT_COLOR] ?: 0
                lastReading = preferences[AppSettings.LAST_READING] ?: 1
            }
        }
        GlobalScope.launch(Dispatchers.IO)
        {
            dataStoreManager.settingsAzan.data.collect { preferences ->
                Timber.e("collecting azan settings")
                AzanPrefs.backgroundMosqueStyle = preferences[AzanSettings.AZAN_MOSQUE_BG] ?: true
                AzanPrefs.azanSound = preferences[AzanSettings.AZAN_SOUND] ?: 1
                AzanPrefs.backgroundMosqueStyle = preferences[AzanSettings.AZAN_MOSQUE_BG] ?: true
                AzanPrefs.notifyBeforePrayer =
                    preferences[AzanSettings.BEFORE_PRAYER_REMINDER] ?: true
                AzanPrefs.notifyBeforePrayerPeriod =
                    preferences[AzanSettings.BEFORE_PRAYER_REMINDER_PERIOD] ?: 10
                AzanPrefs.notifyIqama = preferences[AzanSettings.IQAMA_NOTIFICATION] ?: true
                AzanPrefs.notifyAzan = preferences[AzanSettings.AZAN_NOTIFICATION] ?: true
                AzanPrefs.channel =
                    preferences[AzanSettings.AZAN_CHANNEL] ?: ("PRAYER" + Random().nextInt())
            }
        }
        GlobalScope.launch(Dispatchers.IO)
        {
            dataStoreManager.azkarSettings.data.collect { preferences ->
                Timber.e("collecting azkar settings")
                AzkarPrefs.azkarBeep = preferences[AzkarSettings.BEEP] ?: true
                AzkarPrefs.azkarVibrate = preferences[AzkarSettings.VIBRATE] ?: true
            }
        }

    }

}