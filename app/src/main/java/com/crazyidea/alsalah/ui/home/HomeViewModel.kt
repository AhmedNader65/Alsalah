package com.crazyidea.alsalah.ui.home

import android.content.Context
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.*
import com.crazyidea.alsalah.R
import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.model.PrayerTimingApiModel
import com.crazyidea.alsalah.data.repository.ArticlesRepository
import com.crazyidea.alsalah.data.repository.PrayersRepository
import com.crazyidea.alsalah.data.room.entity.prayers.Timing
import com.crazyidea.alsalah.utils.GlobalPreferences
import com.crazyidea.alsalah.utils.themeColor
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prayerRepository: PrayersRepository,
    private val articlesRepository: ArticlesRepository,
    private val globalPreferences: GlobalPreferences
) : ViewModel() {
    val prayers = prayerRepository.prayers

    val fajrTimeFormatter = Transformations.map(prayers) { time ->
        if (time != null)
            Pair(twentyFourConverter(time.timing.Fajr), twentyFourConverter(time.timing.Fajr, true))
        else
            Pair("", "")
    }
    val shorokTimeFormatter = Transformations.map(prayers) { time ->
        if (time != null)
            Pair(
                twentyFourConverter(time.timing.Sunrise),
                twentyFourConverter(time.timing.Sunrise, true)
            )
        else
            Pair("", "")
    }
    val zuhrTimeTimeFormatter = Transformations.map(prayers) { time ->
        if (time != null)
            Pair(
                twentyFourConverter(time.timing.Dhuhr),
                twentyFourConverter(time.timing.Dhuhr, true)
            )
        else
            Pair("", "")
    }
    val asrTimeFormatter = Transformations.map(prayers) { time ->
        if (time != null)
            Pair(twentyFourConverter(time.timing.Asr), twentyFourConverter(time.timing.Asr, true))
        else
            Pair("", "")
    }
    val maghribTimeFormatter = Transformations.map(prayers) { time ->
        if (time != null)
            Pair(
                twentyFourConverter(time.timing.Maghrib),
                twentyFourConverter(time.timing.Maghrib, true)
            )
        else
            Pair("", "")
    }
    val ishaTimeFormatter = Transformations.map(prayers) { time ->
        if (time != null)
            Pair(twentyFourConverter(time.timing.Isha), twentyFourConverter(time.timing.Isha, true))
        else
            Pair("", "")
    }
    val midnightTimeFormatter = Transformations.map(prayers) { time ->
        if (time != null)
            Pair(
                twentyFourConverter(time.timing.Midnight),
                twentyFourConverter(time.timing.Midnight, true)
            )
        else
            Pair("", "")
    }
    val lastQuarterTimeFormatter = Transformations.map(prayers) { time ->
        if (time != null)
            Pair(
                twentyFourConverter(time.timing.Imsak),
                twentyFourConverter(time.timing.Imsak, true)
            )
        else
            Pair("", "")
    }

    val nextPrayer = MutableLiveData(" صلاة الظهر بعد")
    val nextPrayerId = MutableLiveData(1)
    val remainingTime = MutableLiveData("00:00")
    val azkarAfterPrayer = MutableLiveData("")
    val azkar = MutableLiveData("")
    val otherTimings = mutableMapOf<String, PrayerTimingApiModel>()

    var hijri = UmmalquraCalendar()
    var gor = Calendar.getInstance()


    var day = MutableLiveData("Saturday")
    var previousDayName = MutableLiveData("Saturday")
    var nextDayName = MutableLiveData("Saturday")
    var dateMonthYear = MutableLiveData("13 ramadan")
    var dateDay = MutableLiveData("15")


    private var prayerDataJob: Job? = null
    private var articleDataJob: Job? = null

//    private val _prayerData = MutableLiveData<Timing>()
//    val prayerData: LiveData<Timing> = _prayerData


    private val _articleData = MutableLiveData<ArrayList<Articles>>()
    val articleData: LiveData<ArrayList<Articles>> = _articleData

    init {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)+1
        viewModelScope.launch {
            prayerRepository.getPrayers(day, month.toString())
        }
        setupDate()
        getArticles()
    }

    fun fetchPrayerData(
        cityName: String,
        calendar: Calendar,
        lat: String,
        lng: String,
        method: Int,
        tune: String?,
        save: Boolean = true
    ) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = (calendar.get(Calendar.MONTH) + 1).toString()
        val year = calendar.get(Calendar.YEAR).toString()
        val hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        val minute = calendar.get(Calendar.MINUTE).toString()
        prayerDataJob?.cancel()
        prayerDataJob = viewModelScope.launch {

            prayerRepository.refreshPrayers(
                cityName,
                day,
                month,
                year,
                lat,
                lng,
                method,
                tune,
                save
            )
            prayerRepository.getPrayers(day, month)
            prayerRepository.getAzkar()
            getFirstAzkar()
            val sdf = SimpleDateFormat("H:mm", Locale("ar"))
            val currentDate = sdf.parse("$hour:$minute")
//            getNextPrayer(currentDate, timings)
//            _prayerData.value = timings
        }
    }


    fun getArticles() {
        articleDataJob?.cancel()
        articleDataJob = viewModelScope.launch {
            articlesRepository.fetcharticle()
                .collect {
                    if (it?.data != null)
                        _articleData.value = it.data!!
                }
        }

    }

//    fun getAnotherDayPrayerData(
//        cityName: String,
//        calendar: Calendar,
//        lat: String,
//        lng: String,
//        method: Int,
//        tune: String?,
//        save: Boolean = true
//    ) {
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//        val month = (calendar.get(Calendar.MONTH) + 1).toString()
//        val year = calendar.get(Calendar.YEAR).toString()
//        val date = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(calendar.time)
//
//        prayerDataJob?.cancel()
//        prayerDataJob = viewModelScope.launch {
//            var timings = otherTimings[date]
//            if (timings == null) {
//                val prayersList =
//                    prayerRepository.refreshPrayers(
//                        cityName,
//                        day,
//                        month,
//                        year,
//                        lat,
//                        lng,
//                        method,
//                        tune,
//                        save
//                    )
////                prayersList?.forEach {
////                    otherTimings[it.date.gregorian.date] = it.timings
////                }
////                timings = otherTimings[date]
//            }
//
//        }
//    }

//    private fun getNextPrayer(
//        currentDate: Date,
//        timings: Timing,
//    ) {
//        if (remainingTime.value == "00:00") {
//            val sdf = SimpleDateFormat("H:mm", Locale("ar"))
//            val fajrDate = sdf.parse(timings.Fajr)
//            val shorokDate = sdf.parse(timings.Sunrise)
//            val zuhrDate = sdf.parse(timings.Dhuhr)
//            val asrDate = sdf.parse(timings.Asr)
//            val maghribDate = sdf.parse(timings.Maghrib)
//            val ishaDate = sdf.parse(timings.Isha)
//            nextPrayer.value = "صلاة الفجر بعد"
//            nextPrayerId.value = 1
//            var diff = 30000L
//            var basePrayerForNext = fajrDate
//            if (currentDate.after(fajrDate)) {
//                nextPrayerId.value = 2
//                nextPrayer.value = "صلاة الشروق بعد"
//                diff = (shorokDate.time - currentDate.time)
//                basePrayerForNext = shorokDate
//            }
//            if (currentDate.after(shorokDate)) {
//                nextPrayerId.value = 3
//                nextPrayer.value = "صلاة الظهر بعد"
//                diff = (zuhrDate.time - currentDate.time)
//                basePrayerForNext = zuhrDate
//            }
//            if (currentDate.after(zuhrDate)) {
//                nextPrayerId.value = 4
//                nextPrayer.value = "صلاة العصر بعد"
//                diff = (asrDate.time - currentDate.time)
//                basePrayerForNext = asrDate
//            }
//            if (currentDate.after(asrDate)) {
//                nextPrayerId.value = 5
//                nextPrayer.value = "صلاة المغرب بعد"
//                diff = (maghribDate.time - currentDate.time)
//                basePrayerForNext = maghribDate
//            }
//            if (currentDate.after(maghribDate)) {
//                nextPrayer.value = "صلاة العشاء بعد"
//                nextPrayerId.value = 6
//                diff = (ishaDate.time - currentDate.time)
//                basePrayerForNext = ishaDate
//            }
//            if (currentDate.after(ishaDate)) {
//                nextPrayerId.value = 1
//                nextPrayer.value = " صلاة الفجر بعد"
//                diff = (zuhrDate.time - fajrDate.time)
//                basePrayerForNext = fajrDate
//            }
//            object : CountDownTimer(diff, 1000) {
//                override fun onTick(millisUntilFinished: Long) {
//                    val d = Date(millisUntilFinished)
//                    val df = SimpleDateFormat("HH:mm:ss", Locale.getDefault()) // HH for 0-23
//                    df.timeZone = TimeZone.getTimeZone("GMT");
//                    val time = df.format(d)
//                    remainingTime.value = time
//                    //here you can have your logic to set text to edittext
//                }
//
//                override fun onFinish() {
//                    val timeInSecs: Long = basePrayerForNext.time
//                    val afterAdding1Min = Date(timeInSecs + 1 * 60 * 1000)
//                    getNextPrayer(afterAdding1Min, timings)
//                }
//            }.start()
//        }
//    }

    private fun twentyFourConverter(time: String, am: Boolean = false): String {
        return try {
            val sdf = SimpleDateFormat("H:mm", Locale("ar"))
            val dateObj: Date? = sdf.parse(time)
            if (!am) SimpleDateFormat("K:mm").format(dateObj)
            else {
                SimpleDateFormat("a").format(dateObj)
            }

        } catch (e: ParseException) {
            e.printStackTrace()
            return "am"
        }
    }

    private suspend fun getFirstAzkar() {
        viewModelScope.launch {
            azkarAfterPrayer.value =
                prayerRepository.getFirstAzkarByCategory("أذكار بعد السلام من الصلاة المفروضة").content
            azkar.value = prayerRepository.getFirstAzkar().content
        }
    }


    fun nextDay() {
        gor.add(Calendar.DAY_OF_MONTH, 1)
        hijri.time = gor.time
        setupDate()
    }

    fun prevDay() {
        gor.add(Calendar.DAY_OF_MONTH, -1)
        hijri.time = gor.time
        setupDate()

    }


    private fun setupDate() {
        day.value =
            gor.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val hijriDate = "${
            hijri.get(Calendar.DAY_OF_MONTH)
        } "

        val hijriMonthYear = "${
            hijri.getDisplayName(
                UmmalquraCalendar.MONTH,
                UmmalquraCalendar.SHORT,
                Locale.getDefault()
            )
        }  ${
            hijri.get(
                UmmalquraCalendar.YEAR,
            )
        }"


        dateDay.value = "$hijriDate"
        dateMonthYear.value = "$hijriMonthYear"
        gor.add(Calendar.DAY_OF_MONTH, 1)
        nextDayName.value =
            gor.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        gor.add(Calendar.DAY_OF_MONTH, -2)
        previousDayName.value =
            gor.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        gor.add(Calendar.DAY_OF_MONTH, 1)

    }

    fun setTextColor(prayer: Int, context: Context): Int {
        var color = context.resources.getColor(R.color.white)
        if (prayer == 4 || prayer == 3)
            color = context.resources.getColor(R.color.header_color)
        else
            color = context.resources.getColor(R.color.white)

        return color

    }

    fun getStatusBarColor(number: Int, context: Context): Int {
        var color = context.resources.getColor(R.color.header_color)
        when (number) {
            1 -> color = context.resources.getColor(R.color.fajr_header)
            2 -> color = context.resources.getColor(R.color.shrooq_header)
            3 -> color = context.resources.getColor(R.color.zuhr_header)
            4 -> color = context.resources.getColor(R.color.zuhr_header)
            5 -> color = context.resources.getColor(R.color.maghrib_header)
            6 -> color = context.resources.getColor(R.color.isha_header)
        }
        return color
    }

}