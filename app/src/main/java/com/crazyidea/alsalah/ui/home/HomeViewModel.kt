package com.crazyidea.alsalah.ui.home

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class HomeViewModel @Inject constructor(
    private val prayerRepository: PrayersRepository,
    private val globalPreferences: GlobalPreferences
) : ViewModel() {
    val fajrTime = MutableLiveData("00:00")
    val shorokTime = MutableLiveData("00:00")
    val zuhrTime = MutableLiveData("00:00")
    val asrTime = MutableLiveData("00:00")
    val nextPrayer = MutableLiveData(" صلاة الظهر بعد")
    val nextPrayerId = MutableLiveData(1)
    val remainingTime = MutableLiveData("00:00")
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
        calendar: Calendar,
        lat: String,
        lng: String,
        method: Int,
        tune: String?
    ) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = (calendar.get(Calendar.MONTH) + 1).toString()
        val year = calendar.get(Calendar.YEAR).toString()
        val hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        val minute = calendar.get(Calendar.MINUTE).toString()
        prayerDataJob?.cancel()
        prayerDataJob = viewModelScope.launch {
            var pair =
                prayerRepository.getPrayersData(cityName, day, month, year, lat, lng, method, tune)
            prayerRepository.getAzkar()
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
            getNextPrayer(minute, hour, timings)
            if (pair.second)
                _prayerData.value = timings
        }
    }

    private fun getNextPrayer(
        minute: String,
        hour: String,
        timings: Timing,
    ) {
        val sdf = SimpleDateFormat("H:mm", Locale("ar"))

        val currentDate = sdf.parse("$hour:$minute")
        val fajrDate = sdf.parse(timings.Fajr)
        val shorokDate = sdf.parse(timings.Sunrise)
        val zuhrDate = sdf.parse(timings.Dhuhr)
        val asrDate = sdf.parse(timings.Asr)
        val maghribDate = sdf.parse(timings.Maghrib)
        val ishaDate = sdf.parse(timings.Isha)
        nextPrayer.value = "صلاة الفجر بعد"
        nextPrayerId.value = 1
        var diff = 30000L
        if (currentDate.after(fajrDate)) {
            nextPrayerId.value = 2
            nextPrayer.value = "صلاة الشروق بعد"
            diff = (shorokDate.time - currentDate.time)
        }
        if (currentDate.after(shorokDate)) {
            nextPrayerId.value = 3
            nextPrayer.value = "صلاة الظهر بعد"
            diff = (zuhrDate.time - currentDate.time)
        }
        if (currentDate.after(zuhrDate)) {
            nextPrayerId.value = 4
            nextPrayer.value = "صلاة العصر بعد"
            diff = (asrDate.time - currentDate.time)
        }
        if (currentDate.after(asrDate)) {
            nextPrayerId.value = 5
            nextPrayer.value = "صلاة المغرب بعد"
            diff = (maghribDate.time - currentDate.time)
        }
        if (currentDate.after(maghribDate)) {
            nextPrayer.value = "صلاة العشاء بعد"
            nextPrayerId.value = 6
            diff = (ishaDate.time - currentDate.time)
        }
        if (currentDate.after(ishaDate)) {
            nextPrayerId.value = 1
            nextPrayer.value = " صلاة الفجر بعد"
            diff = (zuhrDate.time - fajrDate.time)
        }
        object : CountDownTimer(diff, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val d = Date(millisUntilFinished)
                val df = SimpleDateFormat("HH:mm:ss", Locale.getDefault()) // HH for 0-23
                df.timeZone = TimeZone.getTimeZone("GMT");
                val time = df.format(d)
                remainingTime.value = time
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                remainingTime.value = "done!"
            }
        }.start()
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