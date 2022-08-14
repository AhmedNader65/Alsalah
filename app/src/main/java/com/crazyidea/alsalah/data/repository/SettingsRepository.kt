package com.crazyidea.alsalah.data.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository : BaseRepository {
    suspend fun updateAppSettings(key: Preferences.Key<String>, value:String)
    suspend fun updateAppSettings(key: Preferences.Key<Boolean>, value:Boolean)
    suspend fun updateAppSettings(key: Preferences.Key<Int>, value:Int)
    fun fetchStringAppSettings(key:Preferences.Key<String>,default:String): Flow<String>
    fun fetchBooleanAppSettings(key:Preferences.Key<Boolean>,default:Boolean): Flow<Boolean>
    fun fetchIntAppSettings(key:Preferences.Key<Int>,default:Int): Flow<Int>
    fun fetchData(): Flow<Preferences>
}