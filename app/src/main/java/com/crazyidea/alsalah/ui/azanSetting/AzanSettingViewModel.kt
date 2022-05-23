package com.crazyidea.alsalah.ui.azanSetting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AzanSettingViewModel : ViewModel() {


    var prayerMinutes = MutableLiveData(0)


    fun controllPrayer(boolean: Boolean) {
        if (boolean)
            prayerMinutes.value = prayerMinutes.value!! + 1
        else{
            if (prayerMinutes.value!! >0){
                prayerMinutes.value = prayerMinutes.value!! - 1
            }
        }
    }

}