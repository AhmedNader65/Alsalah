package com.crazyidea.alsalah.ui.azanSetting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AzanSettingViewModel @Inject constructor(private val globalPreferences: GlobalPreferences) :
    ViewModel() {


    var prayerMinutes = MutableLiveData(globalPreferences.beforeAzanNotificationPeriod())


    fun controllPrayer(boolean: Boolean) {
        if (boolean)
            prayerMinutes.value = prayerMinutes.value!! + 1
        else {
            if (prayerMinutes.value!! > 0) {
                prayerMinutes.value = prayerMinutes.value!! - 1
            }
        }
        globalPreferences.storeBeforePrayerNotificationPeriod(prayerMinutes.value!! )
    }

}