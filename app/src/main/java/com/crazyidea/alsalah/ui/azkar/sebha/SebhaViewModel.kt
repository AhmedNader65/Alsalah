package com.crazyidea.alsalah.ui.azkar.sebha

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.PrayersRepository
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SebhaViewModel @Inject constructor(
    private val prayerRepository: PrayersRepository,
) : ViewModel() {

    val azkar = MutableLiveData<Azkar>()
    var currentIndex = MutableLiveData(0)
    val azkarCounter = MutableLiveData(0)
    val allAzkar = MutableLiveData<List<Azkar>>(listOf(Azkar(1, "", 2, "", "", "")))
    suspend fun getSebha() {
        withContext(viewModelScope.coroutineContext) {
            allAzkar.value = prayerRepository.getAzkarByCategory("تسابيح")
            azkar.value = allAzkar.value!![0]
        }
    }

    fun increaseCounter() {
        var currentValue = azkarCounter.value
        if (currentValue != null) {
            if (currentValue < azkar.value!!.count)
                currentValue = currentValue.plus(1)
            azkarCounter.value = currentValue
            if (currentValue == azkar.value!!.count) {
                var currentIndexVal = currentIndex.value!!
                if (currentIndexVal < allAzkar.value!!.size - 1) {
                    currentIndexVal = currentIndexVal.plus(1)
                    currentIndex.value = currentIndexVal
                    azkar.value = allAzkar.value!![currentIndexVal]
                    azkarCounter.value = 0
                } else if (currentIndexVal == allAzkar.value!!.size - 1) {
                    currentIndex.value = allAzkar.value!!.size
                }
            }
        }
    }
}