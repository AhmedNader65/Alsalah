package com.crazyidea.alsalah.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private val Context.dataSource by preferencesDataStore("app_settings")

class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context){
    val settingsDataStore = appContext.dataSource

}