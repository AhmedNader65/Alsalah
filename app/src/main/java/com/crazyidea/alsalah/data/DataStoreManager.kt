package com.crazyidea.alsalah.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private val Context.dataSource by preferencesDataStore("app_settings")
private val Context.azanSettings by preferencesDataStore("azan_settings")
private val Context.prayerSettings by preferencesDataStore("prayer_settings")
private val Context.azkarSettings by preferencesDataStore("azkar_settings")

class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {
    val settingsDataStore = appContext.dataSource
    val settingsAzan = appContext.azanSettings
    val prayerSettings = appContext.prayerSettings
    val azkarSettings = appContext.azkarSettings

}