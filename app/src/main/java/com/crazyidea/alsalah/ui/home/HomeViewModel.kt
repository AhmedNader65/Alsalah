package com.crazyidea.alsalah.ui.home

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.text.format.DateUtils
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
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
        Pair(twentyFourConverter(time.timing.Fajr), twentyFourConverter(time.timing.Fajr, true))

    }
    val shorokTimeFormatter = Transformations.map(prayers) { time ->
        Pair(
            twentyFourConverter(time.timing.Sunrise),
            twentyFourConverter(time.timing.Sunrise, true)
        )
    }
    val zuhrTimeTimeFormatter = Transformations.map(prayers) { time ->
        Pair(
            twentyFourConverter(time.timing.Dhuhr),
            twentyFourConverter(time.timing.Dhuhr, true)
        )
    }
    val asrTimeFormatter = Transformations.map(prayers) { time ->
        Pair(twentyFourConverter(time.timing.Asr), twentyFourConverter(time.timing.Asr, true))

    }
    val maghribTimeFormatter = Transformations.map(prayers) { time ->
        Pair(
            twentyFourConverter(time.timing.Maghrib),
            twentyFourConverter(time.timing.Maghrib, true)
        )
    }
    val ishaTimeFormatter = Transformations.map(prayers) { time ->
        Pair(twentyFourConverter(time.timing.Isha), twentyFourConverter(time.timing.Isha, true))

    }
    val midnightTimeFormatter = Transformations.map(prayers) { time ->
        Pair(
            twentyFourConverter(time.timing.Midnight),
            twentyFourConverter(time.timing.Midnight, true)
        )
    }
    val lastQuarterTimeFormatter = Transformations.map(prayers) { time ->
        Pair(
            twentyFourConverter(time.timing.Imsak),
            twentyFourConverter(time.timing.Imsak, true)
        )
    }

    val nextPrayer = MutableLiveData(" صلاة الظهر بعد")
    val clickedPrayer = MutableLiveData(" صلاة الظهر بعد")
    val nextPrayerId = MutableLiveData(1)
    val clickedPrayerId = MutableLiveData(0)
    val remainingTime = MutableLiveData("00:00")
    val clickedremainingTime = MutableLiveData("00:00")
    val azkarAfterPrayer = MutableLiveData("")
    val azkar = MutableLiveData("")
    lateinit var timer: CountDownTimer
    lateinit var timer2: CountDownTimer

    private var hijri = UmmalquraCalendar()
    var gor: Calendar = Calendar.getInstance()


    var day = MutableLiveData("Saturday")
    var previousDayName = MutableLiveData("Saturday")
    var nextDayName = MutableLiveData("Saturday")
    var dateMonthYear = MutableLiveData("13 ramadan")
    var dateDay = MutableLiveData("15")


    private var prayerDataJob: Job? = null
    private var articleDataJob: Job? = null


    private val _articleData = MutableLiveData<ArrayList<Articles>>()
    val articleData: LiveData<ArrayList<Articles>> = _articleData

    init {
        val day = gor.get(Calendar.DAY_OF_MONTH)
        val month = gor.get(Calendar.MONTH) + 1
        viewModelScope.launch {
            prayerRepository.getPrayers(day, month.toString())
        }
        setupDate()
        getArticles()
    }

    fun fetchPrayerData(
        cityName: String,
        lat: String,
        lng: String,
        method: Int,
        tune: String?,
        save: Boolean = true
    ) {
        val day = gor.get(Calendar.DAY_OF_MONTH)
        val month = (gor.get(Calendar.MONTH) + 1).toString()
        val year = gor.get(Calendar.YEAR).toString()
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
            getNextPrayer()
        }
    }


    private fun getArticles() {
        articleDataJob?.cancel()
        articleDataJob = viewModelScope.launch {
            articlesRepository.fetcharticle()
                .collect {
                    if (it?.data != null)
                        _articleData.value = it.data!!
                }
        }

    }

    private fun twentyFourConverter(time: String, am: Boolean = false): String {
        return try {
            val sdf = SimpleDateFormat("H:mm", Locale("ar"))
            val dateObj: Date = sdf.parse(time) as Date
            if (!am) SimpleDateFormat("K:mm", Locale("ar")).format(dateObj)
            else {
                SimpleDateFormat("a", Locale("ar")).format(dateObj)
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

    fun clickPrayer(int: Int) {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        if (this::timer2.isInitialized) {
            timer2.cancel()
            delayTime()
        } else {
            delayTime()
        }

        val hour = gor.get(Calendar.HOUR_OF_DAY).toString()
        val minute = gor.get(Calendar.MINUTE).toString()
        val seconds = gor.get(Calendar.SECOND).toString()
        val sdf1 = SimpleDateFormat("H:mm:ss", Locale("ar"))
        val sdf = SimpleDateFormat("H:mm", Locale("ar"))
        val currentDate = sdf1.parse("$hour:$minute:$seconds") as Date
        val timings = prayers.value?.timing

        timings?.let {
            val fajrDate = sdf.parse(timings.Fajr) as Date
            val shorokDate = sdf.parse(timings.Sunrise) as Date
            val zuhrDate = sdf.parse(timings.Dhuhr) as Date
            val asrDate = sdf.parse(timings.Asr) as Date
            val maghribDate = sdf.parse(timings.Maghrib) as Date
            val ishaDate = sdf.parse(timings.Isha) as Date
            var basePrayerForNext = fajrDate
            var diff = 30000L
            clickedPrayerId.value = int
            when (int) {
                1 -> {
                    if (currentDate.after(fajrDate)) {
                        clickedPrayer.value = "صلاة الفجر منذ"
                        diff = (currentDate.time - fajrDate.time)
                    } else {
                        clickedPrayer.value = "صلاة الفجر بعد"
                        diff = (fajrDate.time - currentDate.time)

                    }
                }
                2 -> {
                    if (currentDate.after(shorokDate)) {
                        clickedPrayer.value = "صلاة الشروق منذ"
                        diff = (currentDate.time - shorokDate.time)
                    } else {
                        clickedPrayer.value = "صلاة الشروق بعد"
                        diff = (shorokDate.time - currentDate.time)

                    }
                }
                3 -> {
                    if (currentDate.after(zuhrDate)) {
                        clickedPrayer.value = "صلاة الظهر منذ"
                        diff = (currentDate.time - zuhrDate.time)
                    } else {
                        clickedPrayer.value = "صلاة الظهر بعد"
                        diff = (zuhrDate.time - currentDate.time)

                    }
                }
                4 -> {
                    if (currentDate.after(asrDate)) {
                        clickedPrayer.value = "صلاة العصر منذ"
                        diff = (currentDate.time - asrDate.time)
                    } else {
                        clickedPrayer.value = "صلاة العصر بعد"
                        diff = (asrDate.time - currentDate.time)

                    }
                }
                5 -> {
                    if (currentDate.after(maghribDate)) {
                        clickedPrayer.value = "صلاة المغرب منذ"
                        diff = (currentDate.time - maghribDate.time)
                    } else {
                        clickedPrayer.value = "صلاة المغرب بعد"
                        diff = (maghribDate.time - currentDate.time)

                    }
                }
                6 -> {
                    if (currentDate.after(ishaDate)) {
                        clickedPrayer.value = "صلاة العشاء منذ"
                        diff = (currentDate.time - ishaDate.time)
                    } else {
                        clickedPrayer.value = "صلاة العشاء بعد"
                        diff = (ishaDate.time - currentDate.time)

                    }
                }


            }


            timer = object : CountDownTimer(diff, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val d = Date(millisUntilFinished)
                    val df = SimpleDateFormat("HH:mm:ss", Locale.getDefault()) // HH for 0-23
                    df.timeZone = TimeZone.getTimeZone("GMT");
                    val time = df.format(d)
                    clickedremainingTime.value = time
                    //here you can have your logic to set text to edittext
                }

                override fun onFinish() {

                }
            }
            timer.start()
        }
        Log.e("TAG", "clickPrayer: " + clickedPrayer.value)
        Log.e("TAG", "clickPrayer: " + clickedremainingTime.value)


    }


    private fun getNextPrayer(newDate: Date? = null) {

        val hour = gor.get(Calendar.HOUR_OF_DAY).toString()
        val minute = gor.get(Calendar.MINUTE).toString()
        val seconds = gor.get(Calendar.SECOND).toString()
        val sdf1 = SimpleDateFormat("H:mm:ss", Locale("ar"))
        val sdf = SimpleDateFormat("H:mm", Locale("ar"))
        val currentDate = newDate ?: sdf1.parse("$hour:$minute:$seconds") as Date
        val timings = prayers.value?.timing
        timings?.let {
            if (remainingTime.value == "00:00") {
                val fajrDate = sdf.parse(timings.Fajr) as Date
                val shorokDate = sdf.parse(timings.Sunrise) as Date
                val zuhrDate = sdf.parse(timings.Dhuhr) as Date
                val asrDate = sdf.parse(timings.Asr) as Date
                val maghribDate = sdf.parse(timings.Maghrib) as Date
                val ishaDate = sdf.parse(timings.Isha) as Date
                nextPrayer.value = "صلاة الفجر بعد"
                nextPrayerId.value = 1
                var diff = 30000L
                var basePrayerForNext = fajrDate
                if (currentDate.after(fajrDate)) {
                    nextPrayerId.value = 2
                    nextPrayer.value = "صلاة الشروق بعد"
                    diff = (shorokDate.time - currentDate.time)
                    basePrayerForNext = shorokDate
                }
                if (currentDate.after(shorokDate)) {
                    nextPrayerId.value = 3
                    nextPrayer.value = "صلاة الظهر بعد"
                    diff = (zuhrDate.time - currentDate.time)
                    basePrayerForNext = zuhrDate
                }
                if (currentDate.after(zuhrDate)) {
                    nextPrayerId.value = 4
                    nextPrayer.value = "صلاة العصر بعد"
                    diff = (asrDate.time - currentDate.time)
                    basePrayerForNext = asrDate
                }
                if (currentDate.after(asrDate)) {
                    nextPrayerId.value = 5
                    nextPrayer.value = "صلاة المغرب بعد"
                    diff = (maghribDate.time - currentDate.time)
                    basePrayerForNext = maghribDate
                }
                if (currentDate.after(maghribDate)) {
                    nextPrayer.value = "صلاة العشاء بعد"
                    nextPrayerId.value = 6
                    diff = (ishaDate.time - currentDate.time)
                    basePrayerForNext = ishaDate
                }
                if (currentDate.after(ishaDate)) {
                    nextPrayerId.value = 1
                    nextPrayer.value = " صلاة الفجر بعد"
                    diff = (zuhrDate.time - fajrDate.time)
                    basePrayerForNext = fajrDate
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
                        val timeInSecs: Long = basePrayerForNext.time
                        val afterAdding1Min = Date(timeInSecs + 1 * 60 * 1000)
                        remainingTime.value = "00:00"

                        getNextPrayer(newDate = afterAdding1Min)
                    }
                }.start()
            }
        }
    }


    fun nextDay() {
        gor.add(Calendar.DAY_OF_MONTH, 1)
        hijri.time = gor.time

        val day = gor.get(Calendar.DAY_OF_MONTH)
        val month = gor.get(Calendar.MONTH) + 1
        viewModelScope.launch {
            prayerRepository.getPrayers(day, month.toString())
        }
        setupDate()
    }

    fun prevDay() {
        gor.add(Calendar.DAY_OF_MONTH, -1)
        hijri.time = gor.time
        setupDate()
        val day = gor.get(Calendar.DAY_OF_MONTH)
        val month = gor.get(Calendar.MONTH) + 1
        viewModelScope.launch {
            prayerRepository.getPrayers(day, month.toString())
        }
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


        dateDay.value = hijriDate
        dateMonthYear.value = hijriMonthYear
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

    fun delayTime() {

        timer2 = object : CountDownTimer(6000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //here you can have your logic to set text to edittext

            }

            override fun onFinish() {
                clickedPrayerId.value = 0
                clickedremainingTime.value = ""
            }
        }
        timer2.start()

    }

}


@BindingAdapter("setupImage", "clickedID", requireAll = false)
fun bindPrayerHeaderImage(imageView: ImageView, prayerId: Int, clickedID: Int) {
    var myMethod = prayerId
    if (clickedID == 0)
        myMethod = prayerId
    else
        myMethod = clickedID
    when (myMethod) {
        1 -> imageView.setImageResource(R.drawable.fajr_pic)
        2 -> imageView.setImageResource(R.drawable.shorok_pic)
        3 -> imageView.setImageResource(R.drawable.zuhr_pic)
        4 -> imageView.setImageResource(R.drawable.asr_pic)
        5 -> imageView.setImageResource(R.drawable.maghrib_pic)
        6 -> imageView.setImageResource(R.drawable.isha_pic)
    }
}