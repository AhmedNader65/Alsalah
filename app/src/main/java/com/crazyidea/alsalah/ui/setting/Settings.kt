package com.crazyidea.alsalah.ui.setting

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppSettings{
    val APP_LANGUAGE = stringPreferencesKey("app-language")
    val aAPP_LANGUAGE = booleanPreferencesKey("app-language")
}