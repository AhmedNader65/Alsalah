package com.crazyidea.alsalah.ui.fawaed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.repository.ArticlesRepository
import com.crazyidea.alsalah.data.repository.AppSettingsRepository
import com.crazyidea.alsalah.ui.setting.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FawaedSettingViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    private val settingsRepository: AppSettingsRepository
) : ViewModel() {

    private var fawaedDataJob: Job? = null


    private val _fawaedData = MutableLiveData<ArrayList<Articles>>()
    val fawaedData: LiveData<ArrayList<Articles>> = _fawaedData


    init {
        getFawaed()
    }


    fun getFawaed() {
        fawaedDataJob?.cancel()
        fawaedDataJob = viewModelScope.launch {
            articlesRepository.fetchArticle()
                .collect {
                    if (it?.data != null)
                        _fawaedData.value = it.data!!
                }
        }

    }


    override fun onCleared() {
        fawaedDataJob?.cancel()
        super.onCleared()
    }

    fun saveLanguage(shortcut: String) {
        viewModelScope.launch {
            settingsRepository.updateAppSettings(AppSettings.ARTICLES_LANGUAGE, shortcut)
        }
    }
    fun saveNotificationPref(notificationPref: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAppSettings(AppSettings.ARTICLES_NOTIFICATIONS, notificationPref)
        }
    }
}