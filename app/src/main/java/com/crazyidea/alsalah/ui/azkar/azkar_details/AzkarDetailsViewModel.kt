package com.crazyidea.alsalah.ui.azkar.azkar_details

import android.animation.ObjectAnimator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.AzkarRepository
import com.crazyidea.alsalah.data.repository.PrayersRepository
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


@HiltViewModel
class AzkarDetailsViewModel @Inject constructor(
    private val prayerRepository: PrayersRepository,
    private val azkarRepository: AzkarRepository,
) : ViewModel() {
    private lateinit var category: String
    val azkar = MutableLiveData<Azkar>()
    var currentIndex = MutableLiveData(0)
    val azkarCounter = MutableLiveData(0)
    val fontSize = MutableLiveData(12)
    val allAzkar = MutableLiveData<List<Azkar>>()
    suspend fun getAzkar(category: String) {
        this.category = category
        withContext(viewModelScope.coroutineContext) {
            allAzkar.value = prayerRepository.getAzkarByCategory(category)
            allAzkar.value?.let {
                if (it.isNotEmpty())
                    azkar.value = it[0]
            }
        }
    }

    private fun increaseProgress(date: String, category: String) {
        viewModelScope.launch {
            azkarRepository.insertProgress(date, category)
        }
    }

    fun increaseCounter() {
        var currentValue = azkarCounter.value
        if (currentValue != null) {
            // if there's room to increase
            if (currentValue < azkar.value!!.count)
                currentValue = currentValue.plus(1)
            else {
                // done this azkar and update total progress
                val date =
                    java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                increaseProgress(date, category = category)
            }
            // update the counter value with current value
            azkarCounter.value = currentValue
        }
    }
    fun getNextAzkar(){

        // if counter is at max
        if (azkarCounter.value == azkar.value!!.count) {

            var currentIndexVal = currentIndex.value!!
            // get next azkar
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
