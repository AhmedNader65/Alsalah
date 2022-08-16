package com.crazyidea.alsalah.ui.prayerTiming

import androidx.lifecycle.ViewModel
import com.crazyidea.alsalah.data.repository.PrayerSettingsRepository
import com.crazyidea.alsalah.ui.setting.BaseSettingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PrayerTimingViewModel @Inject constructor(
    private val settingsRepository: PrayerSettingsRepository
) : BaseSettingViewModel(settingsRepository)