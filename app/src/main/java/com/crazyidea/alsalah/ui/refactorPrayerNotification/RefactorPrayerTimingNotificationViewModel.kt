package com.crazyidea.alsalah.ui.refactorPrayerNotification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

class RefactorPrayerTimingNotificationViewModel : ViewModel() {

    var fajrMinutes = MutableLiveData(0)
    val shrooqMinutes = MutableLiveData(0)
    val zuhrMinutes = MutableLiveData(0)
    val asrMinutes = MutableLiveData(0)
    val maghribMinutes = MutableLiveData(0)
    val ishaMinutes = MutableLiveData(0)


    fun controllFajr(boolean: Boolean) {
        if (boolean)
            fajrMinutes.value = fajrMinutes.value!! + 1
        else{
            if (fajrMinutes.value!! >0){
                fajrMinutes.value = fajrMinutes.value!! - 1
            }
        }
    }



    fun controllShrooq(boolean: Boolean) {
        if (boolean)
            shrooqMinutes.value = shrooqMinutes.value!! + 1
        else{
            if (shrooqMinutes.value!! >0){
                shrooqMinutes.value = shrooqMinutes.value!! - 1
            }
        }
    }


    fun controllZuhr(boolean: Boolean) {
        if (boolean)
            zuhrMinutes.value = zuhrMinutes.value!! + 1
        else{
            if (zuhrMinutes.value!! >0){
                zuhrMinutes.value = zuhrMinutes.value!! - 1
            }
        }
    }


    fun controllAsr(boolean: Boolean) {
        if (boolean)
            asrMinutes.value = asrMinutes.value!! + 1
        else{
            if (asrMinutes.value!! >0){
                asrMinutes.value = asrMinutes.value!! - 1
            }
        }
    }


    fun controllMaghrib(boolean: Boolean) {
        if (boolean)
            maghribMinutes.value = maghribMinutes.value!! + 1
        else{
            if (maghribMinutes.value!! >0){
                maghribMinutes.value = maghribMinutes.value!! - 1
            }
        }
    }


    fun controllIsha(boolean: Boolean) {
        if (boolean)
            ishaMinutes.value = ishaMinutes.value!! + 1
        else{
            if (ishaMinutes.value!! >0){
                ishaMinutes.value = ishaMinutes.value!! - 1
            }
        }
    }



}