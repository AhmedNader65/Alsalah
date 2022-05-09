package com.crazyidea.alsalah.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.Resource
import com.crazyidea.alsalah.data.model.Status
import com.crazyidea.alsalah.data.repository.PrayersRepository
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

    init {
        fetchPrayerData("09-05-2022", "Mansoura,EG", 5, null)
    }

    private var prayerDataJob: Job? = null

    private val _prayerData =
        MutableStateFlow(Resource<PrayerResponseApiModel>(Status.INIT, null, null))
    val prayerData: StateFlow<Resource<PrayerResponseApiModel>> = _prayerData.asStateFlow()

    private fun fetchPrayerData(
        date: String,
        address: String,
        method: Int,
        tune: String?
    ) {
        prayerDataJob?.cancel()
        prayerDataJob = viewModelScope.launch {
            prayerRepository.getPrayersData(date, address, method, tune).collect { resource ->
                if (resource != null) {
                    _prayerData.emit(resource)
                    resource.data?.timings.let { timings ->
                        if (timings != null) {

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
                        }
                    }
                }
            }
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