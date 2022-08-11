package com.crazyidea.alsalah.ui.setting

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    suspend fun getLanguage(): Flow<String> {
       return withContext(viewModelScope.coroutineContext) {
            settingsRepository.fetchStringAppSettings(AppSettings.APP_LANGUAGE)
        }
    }

    fun update(key: Preferences.Key<String>, value: String) {
        viewModelScope.launch {
            settingsRepository.updateAppSettings(key, value)
        }
    }

    fun update(key: Preferences.Key<Int>, value: Int) {
        viewModelScope.launch {
            settingsRepository.updateAppSettings(key, value)
        }
    }

    fun update(key: Preferences.Key<Boolean>, value: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAppSettings(key, value)
        }
    }
}