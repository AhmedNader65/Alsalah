package com.crazyidea.alsalah.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.Resource
import com.crazyidea.alsalah.data.model.Status
import com.crazyidea.alsalah.data.repository.PrayersRepository
import com.crazyidea.alsalah.data.room.entity.Timing
import com.crazyidea.alsalah.utils.CommonUtils
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prayerRepository: PrayersRepository,
    private val globalPreferences: GlobalPreferences
) : ViewModel() {
    val fajrTime = MutableLiveData("00:00")
    val shorokTime = MutableLiveData("00:00")
    val zuhrTime = MutableLiveData("00:00")
    val asrTime = MutableLiveData("00:00")
    val maghribTime = MutableLiveData("00:00")
    val eshaTime = MutableLiveData("00:00")
    val midnightTime = MutableLiveData("00:00")
    val lastQuarterTime = MutableLiveData("00:00")
    val fajrTimeAPM = MutableLiveData("AM")
    val shorokTimeAPM = MutableLiveData("AM")
    val zuhrTimeAPM = MutableLiveData("AM")
    val asrTimeAPM = MutableLiveData("AM")
    val maghribTimeAPM = MutableLiveData("AM")
    val eshaTimeAPM = MutableLiveData("AM")


    private var prayerDataJob: Job? = null

    private val _prayerData = MutableLiveData<Timing>()
    val prayerData: LiveData<Timing> = _prayerData

    fun fetchPrayerData(
        cityName: String,
        day: Int,
        month: String,
        year: String,
        lat: String,
        lng: String,
        method: Int,
        tune: String?
    ) {
        prayerDataJob?.cancel()
        prayerDataJob = viewModelScope.launch {
            var pair =
                prayerRepository.getPrayersData(cityName, day, month, year, lat, lng, method, tune)
            val timings = pair.first
            fajrTime.value = twentyFourConverter(timings.Fajr)
            zuhrTime.value = twentyFourConverter(timings.Dhuhr)
            shorokTime.value = twentyFourConverter(timings.Sunrise)
            asrTime.value = twentyFourConverter(timings.Asr)
            maghribTime.value = twentyFourConverter(timings.Maghrib)
            eshaTime.value = twentyFourConverter(timings.Isha)
            fajrTimeAPM.value = twentyFourConverter(timings.Fajr, true)
            zuhrTimeAPM.value = twentyFourConverter(timings.Dhuhr, true)
            shorokTimeAPM.value = twentyFourConverter(timings.Sunrise, true)
            asrTimeAPM.value = twentyFourConverter(timings.Asr, true)
            maghribTimeAPM.value = twentyFourConverter(timings.Maghrib, true)
            eshaTimeAPM.value = twentyFourConverter(timings.Isha, true)
            midnightTime.value = twentyFourConverter(timings.Midnight)
            lastQuarterTime.value = timings.Imsak
            if (pair.second)
                _prayerData.value = timings
        }
    }

    private fun twentyFourConverter(time: String, am: Boolean = false): String {
        return try {
            val sdf = SimpleDateFormat("H:mm", Locale("ar"))
            val dateObj: Date? = sdf.parse(time)
            if (!am) SimpleDateFormat("K:mm").format(dateObj)
            else {
                val amPm = SimpleDateFormat("a").format(dateObj)
                if (amPm == "am")
                    if (globalPreferences.locale.equals("ar"))
                        "ص"
                    else
                        "AM"
                else
                    if (globalPreferences.locale.equals("ar"))
                        "م"
                    else
                        "PM"
            }

        } catch (e: ParseException) {
            e.printStackTrace()
            return "am"
        }
    }

}