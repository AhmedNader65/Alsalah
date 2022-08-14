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

abstract class BaseSettingViewModel constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {


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

    fun fetchStringData(key: Preferences.Key<String>,default:String = ""): Flow<String> {
        return settingsRepository.fetchStringAppSettings(key,default)
    }

    fun fetchBooleanData(key: Preferences.Key<Boolean>,default : Boolean = false): Flow<Boolean> {
        return settingsRepository.fetchBooleanAppSettings(key,default)
    }
    fun fetchIntegerData(key: Preferences.Key<Int>,default :Int= 0): Flow<Int> {
        return settingsRepository.fetchIntAppSettings(key,default)
    }

    fun fetchData (): Flow<Preferences> {
        return settingsRepository.fetchData()
    }
}