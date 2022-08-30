package com.crazyidea.alsalah.ui.azkarSetting

import androidx.lifecycle.ViewModel
import com.crazyidea.alsalah.data.repository.AzanSettingsRepository
import com.crazyidea.alsalah.data.repository.SettingsRepository
import com.crazyidea.alsalah.ui.setting.BaseSettingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class AzkarSettingViewModel @Inject constructor(
    private val settingsRepository: AzanSettingsRepository
) : BaseSettingViewModel(settingsRepository)