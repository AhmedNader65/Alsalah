package com.crazyidea.alsalah.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.crazyidea.alsalah.data.DataStoreManager
import com.crazyidea.alsalah.ui.setting.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultSettingsRepository @Inject constructor(
    private val externalScope: CoroutineScope,
    private val dataStoreManager: DataStoreManager
) : SettingsRepository {
    override suspend fun updateAppSettings(key: Preferences.Key<String>, value: String) {
        externalScope.launch {
            dataStoreManager.settingsDataStore.edit { settings ->
                settings[key] = value
            }
        }
    }

    override suspend fun updateAppSettings(key: Preferences.Key<Int>, value: Int) {
        externalScope.launch {
            dataStoreManager.settingsDataStore.edit { settings ->
                settings[key] = value
            }
        }
    }

    override fun fetchStringAppSettings(key: Preferences.Key<String>): Flow<String> {
       return dataStoreManager.settingsDataStore.data.map { preferences ->
            return@map preferences[AppSettings.APP_LANGUAGE]?: "ar"
        }
    }

    override suspend fun updateAppSettings(key: Preferences.Key<Boolean>, value: Boolean) {
        externalScope.launch {
            dataStoreManager.settingsDataStore.edit { settings ->
                settings[key] = value
            }
        }
    }


}