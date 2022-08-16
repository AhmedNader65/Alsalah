package com.crazyidea.alsalah.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crazyidea.alsalah.data.repository.AppSettingsRepository
import com.crazyidea.alsalah.ui.setting.BaseSettingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
) : BaseSettingViewModel(appSettingsRepository) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}