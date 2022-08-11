package com.crazyidea.alsalah.data.repository

import androidx.datastore.preferences.core.Preferences

interface SettingsRepository : BaseRepository {
    suspend fun updateAppSettings(key: Preferences.Key<String>, value:String)
    suspend fun updateAppSettings(key: Preferences.Key<Boolean>, value:Boolean)
    suspend fun updateAppSettings(key: Preferences.Key<Int>, value:Int)
}