package com.crazyidea.alsalah.ui.khatma

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

class KhatmaViewModel : ViewModel() {


    var days = MutableLiveData(30)

    fun controllDays(boolean: Boolean) {
        if (boolean)
            days.value = days.value!! + 1
        else{
            if (days.value!! >0){
                days.value = days.value!! - 1
            }
        }
    }
}