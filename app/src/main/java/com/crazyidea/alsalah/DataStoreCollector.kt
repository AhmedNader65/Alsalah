package com.crazyidea.alsalah

import com.crazyidea.alsalah.data.DataStoreManager
import com.crazyidea.alsalah.ui.setting.AppSettings
import com.crazyidea.alsalah.ui.setting.AzanSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

object DataStoreCollector {
    var userId: Int = 0
    var loggedIn: Boolean = false
    var articlesLanguage: String = "all"
    var showArticlesNotifications: Boolean = true
    var fridayNotifications: Boolean = false
    var fastingNotifications: Boolean = false
    var silentPhone: Boolean = false


    // AZAN SETTINGS
    object AzanPrefs {
        var backgroundMosqueStyle: Boolean = true
        var notifyBeforePrayer: Boolean = true
        var notifyBeforePrayerPeriod: Int = 10
        var notifyIqama: Boolean = true
        var notifyAzan: Boolean = true
    }

    fun startCollecting(dataStoreManager: DataStoreManager) {

        GlobalScope.launch(Dispatchers.IO)
        {

            dataStoreManager.settingsDataStore.data.collect { preferences ->
                userId = preferences[AppSettings.USER_ID] ?: 0
                loggedIn = preferences[AppSettings.LOGGED] ?: false
                articlesLanguage = preferences[AppSettings.ARTICLES_LANGUAGE] ?: "all"
                showArticlesNotifications = preferences[AppSettings.ARTICLES_NOTIFICATIONS] ?: true
                fridayNotifications = preferences[AppSettings.FRIDAY_NOTIFICATIONS] ?: false
                fastingNotifications = preferences[AppSettings.FASTING_NOTIFICATIONS] ?: false
                silentPhone = preferences[AppSettings.SILENT_PHONE] ?: false
            }
            dataStoreManager.settingsAzan.data.collect { preferences ->
                Timber.e("collecting azan settings")
                AzanPrefs.backgroundMosqueStyle = preferences[AzanSettings.AZAN_MOSQUE_BG] ?: true
                AzanPrefs.notifyBeforePrayer = preferences[AzanSettings.BEFORE_PRAYER_REMINDER] ?: true
                AzanPrefs.notifyBeforePrayerPeriod =
                    preferences[AzanSettings.BEFORE_PRAYER_REMINDER_PERIOD] ?: 10
                AzanPrefs.notifyIqama = preferences[AzanSettings.IQAMA_NOTIFICATION] ?: true
                AzanPrefs.notifyAzan = preferences[AzanSettings.AZAN_NOTIFICATION] ?: true
            }
        }
    }

}