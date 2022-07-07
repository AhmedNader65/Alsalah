package com.crazyidea.alsalah.ui.refactorPrayerNotification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RefactorPrayerTimingNotificationViewModel @Inject constructor(
    private val globalPreferences: GlobalPreferences
) : ViewModel() {

    var fajrMinutes = MutableLiveData("0")
    val shrooqMinutes = MutableLiveData("0")
    val zuhrMinutes = MutableLiveData("0")
    val asrMinutes = MutableLiveData("0")
    val maghribMinutes = MutableLiveData("0")
    val ishaMinutes = MutableLiveData("0")
    val goBack = MutableLiveData(false)


    fun controllFajr(boolean: Boolean) {
        if (boolean)
            fajrMinutes.value = fajrMinutes.value!!.toInt().plus(1).toString()
        else {
            if (fajrMinutes.value!!.toInt() > 0) {
                fajrMinutes.value = fajrMinutes.value!!.toInt().minus(1).toString()
            }
        }
    }

    fun applyModifications() {
        globalPreferences.storeFajrMod(fajrMinutes.value!!.toInt())
        globalPreferences.storeShorookMod(shrooqMinutes.value!!.toInt())
        globalPreferences.storeZuhrMod(zuhrMinutes.value!!.toInt())
        globalPreferences.storeAsrMod(asrMinutes.value!!.toInt())
        globalPreferences.storeMaghribMod(maghribMinutes.value!!.toInt())
        globalPreferences.storeIshaMod(ishaMinutes.value!!.toInt())
        goBack.value = true
    }


    fun controllShrooq(boolean: Boolean) {
        if (boolean)
            shrooqMinutes.value = shrooqMinutes.value!!.toInt().plus(1).toString()
        else {
            if (shrooqMinutes.value!!.toInt() > 0) {
                shrooqMinutes.value = shrooqMinutes.value!!.toInt().minus(1).toString()
            }
        }
    }


    fun controllZuhr(boolean: Boolean) {
        if (boolean)
            zuhrMinutes.value = zuhrMinutes.value!!.toInt().plus(1).toString()
        else {
            if (zuhrMinutes.value!!.toInt() > 0) {
                zuhrMinutes.value = zuhrMinutes.value!!.toInt().minus(1).toString()
            }
        }
    }


    fun controllAsr(boolean: Boolean) {
        if (boolean)
            asrMinutes.value = asrMinutes.value!!.toInt().plus(1).toString()
        else {
            if (asrMinutes.value!!.toInt() > 0) {
                asrMinutes.value = asrMinutes.value!!.toInt().minus(1).toString()
            }
        }
    }


    fun controllMaghrib(boolean: Boolean) {
        if (boolean)
            maghribMinutes.value = maghribMinutes.value!!.toInt().plus(1).toString()
        else {
            if (maghribMinutes.value!!.toInt() > 0) {
                maghribMinutes.value = maghribMinutes.value!!.toInt().minus(1).toString()
            }
        }
    }


    fun controllIsha(boolean: Boolean) {
        if (boolean)
            ishaMinutes.value = ishaMinutes.value!!.toInt().plus(1).toString()
        else {
            if (ishaMinutes.value!!.toInt() > 0) {
                ishaMinutes.value = ishaMinutes.value!!.toInt().minus(1).toString()
            }
        }
    }


}