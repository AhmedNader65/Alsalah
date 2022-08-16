package com.crazyidea.alsalah.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.CalendarRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
) : ViewModel() {


    private var eventsDataJob: Job? = null

    private val _eventsData = MutableLiveData<String?>()
    val eventsData: LiveData<String?> = _eventsData

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