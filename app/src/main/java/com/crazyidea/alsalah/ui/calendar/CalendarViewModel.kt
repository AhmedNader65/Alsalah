package com.crazyidea.alsalah.ui.calendar

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.model.Resource
import com.crazyidea.alsalah.data.repository.CalendarRepository
import com.crazyidea.alsalah.data.repository.PrayersRepository
import com.crazyidea.alsalah.data.room.entity.prayers.Timing
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val globalPreferences: GlobalPreferences
) : ViewModel() {


    private var eventsDataJob: Job? = null

    private val _eventsData = MutableLiveData<Resource<String>>()
    val eventsData: LiveData<Resource<String>> = _eventsData

    fun fetchEventsData(
        date: String
    ) {
        eventsDataJob?.cancel()
        eventsDataJob = viewModelScope.launch {
            val list =
                calendarRepository.getEventsList(date)
            _eventsData.value = list
        }
    }

    override fun onCleared() {
        eventsDataJob?.cancel()
        super.onCleared()
    }

}