package com.crazyidea.alsalah.ui.azkar.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.AzkarRepository
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AzkarViewModel @Inject constructor(
    private val azkarRepository: AzkarRepository,
) : ViewModel() {
    var progress = MutableLiveData(0)
    var day = MutableLiveData("Saturday")
    var previousDayName = MutableLiveData("Saturday")
    var nextDayName = MutableLiveData("Saturday")
    var date = MutableLiveData("15 May / 13 ramadan")
    private var hijri = UmmalquraCalendar()
    private var gor = Calendar.getInstance()

    init {
        hijri = UmmalquraCalendar()
        gor = Calendar.getInstance()
        progress = MutableLiveData(0)
        getTotalProgress()
        setupDate()
    }


    fun getTotalProgress() {

        viewModelScope.launch {
            val date =
                java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(gor.time)
            withContext(viewModelScope.coroutineContext) {
                val prog = azkarRepository.getTotalProgress(date)
                progress.value = prog
            }
        }
    }

    private fun setupDate() {
        day.value =
            gor.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val hijriDate = "${
            hijri.get(Calendar.DAY_OF_MONTH)
        } ${
            hijri.getDisplayName(
                UmmalquraCalendar.MONTH,
                UmmalquraCalendar.SHORT,
                Locale.getDefault()
            )
        }"
        val gorDate = "${
            gor.get(Calendar.DAY_OF_MONTH)
        } ${
            gor.getDisplayName(
                Calendar.MONTH,
                Calendar.SHORT,
                Locale.getDefault()
            )
        }"
        date.value = "$hijriDate / $gorDate"
        gor.add(Calendar.DAY_OF_MONTH, 1)
        nextDayName.value =
            gor.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        gor.add(Calendar.DAY_OF_MONTH, -2)
        previousDayName.value =
            gor.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        gor.add(Calendar.DAY_OF_MONTH, 1)

    }

    fun nextDay() {
        gor.add(Calendar.DAY_OF_MONTH, 1)
        hijri.time = gor.time
        setupDate()
        viewModelScope.launch {
            getTotalProgress()
        }
    }

    fun prevDay() {
        gor.add(Calendar.DAY_OF_MONTH, -1)
        hijri.time = gor.time
        setupDate()
        viewModelScope.launch {
            getTotalProgress()
        }
    }


}