package com.crazyidea.alsalah.ui.poleCalculateWay

import androidx.lifecycle.ViewModel
import com.crazyidea.alsalah.data.repository.PrayerSettingsRepository
import com.crazyidea.alsalah.ui.setting.BaseSettingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PoleCalculateWayViewModel @Inject constructor(
    prayerSettingsRepository: PrayerSettingsRepository
) : BaseSettingViewModel(prayerSettingsRepository)