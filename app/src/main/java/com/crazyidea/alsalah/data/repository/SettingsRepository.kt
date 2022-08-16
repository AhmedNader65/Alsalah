package com.crazyidea.alsalah.data.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository : BaseRepository {
    suspend fun<T> updateAppSettings(key: Preferences.Key<T>, value:T)
    fun fetchStringAppSettings(key:Preferences.Key<String>,default:String): Flow<String>
    fun fetchBooleanAppSettings(key:Preferences.Key<Boolean>,default:Boolean): Flow<Boolean>
    fun fetchIntAppSettings(key:Preferences.Key<Int>,default:Int): Flow<Int>
    fun fetchData(): Flow<Preferences>
}