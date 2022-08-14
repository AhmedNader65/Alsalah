package com.crazyidea.alsalah.ui.azanSetting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.AppSettingsRepository
import com.crazyidea.alsalah.data.repository.AzanSettingsRepository
import com.crazyidea.alsalah.ui.setting.AzanSettings
import com.crazyidea.alsalah.ui.setting.BaseSettingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AzanSettingViewModel @Inject constructor(
    private val settingsRepository: AzanSettingsRepository
) : BaseSettingViewModel(settingsRepository) {


    var prayerMinutes = MutableLiveData(10)

    init {
        viewModelScope.launch {
            fetchIntegerData(AzanSettings.BEFORE_PRAYER_REMINDER_PERIOD, 10).collect {
                prayerMinutes.postValue(it)
            }
        }
    }

    fun controlPrayer(boolean: Boolean) {
        if (boolean)
            prayerMinutes.value = prayerMinutes.value!! + 1
        else {
            if (prayerMinutes.value!! > 0) {
                prayerMinutes.value = prayerMinutes.value!! - 1
            }
        }
        update(AzanSettings.BEFORE_PRAYER_REMINDER_PERIOD, prayerMinutes.value!!)
    }

}