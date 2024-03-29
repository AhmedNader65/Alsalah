package com.crazyidea.alsalah.ui.khatma

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.KhatmaRepository
import com.crazyidea.alsalah.data.repository.QuranRepository
import com.crazyidea.alsalah.data.room.entity.Khatma
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KhatmaViewModel @Inject constructor(
    private val repository: KhatmaRepository,
    private val quranRepository: QuranRepository
) : ViewModel() {

    val khatmas = repository.khatmas

    var khatma = MutableLiveData(Khatma(null, null, null, time = null, days = 30))
    var days = MutableLiveData(30)
    var updateFields = MutableLiveData(false)
    var result = MutableLiveData(20)
    var type = MutableLiveData(0)

    fun controlDays(boolean: Boolean) {
        if (boolean) {
            days.value = days.value!! + 1
            updateFields.value = true
        } else {
            if (days.value!! > 1) {
                updateFields.value = true
                days.value = days.value!! - 1
            }
        }
    }


    fun saveKhatma() {
        khatma.value?.let { viewModelScope.launch { repository.saveKhatma(it) } }
    }

    fun getJuzPage(plus: Int) {

        viewModelScope.launch {
            khatma.value?.read = quranRepository.getJuzPage(plus)
            khatma.value = khatma.value
            updateFields.value = true
        }
    }
}
