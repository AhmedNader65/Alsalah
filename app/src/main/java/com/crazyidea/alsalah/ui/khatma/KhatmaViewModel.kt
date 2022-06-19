package com.crazyidea.alsalah.ui.khatma

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crazyidea.alsalah.data.prayers.PrayersRepository
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class KhatmaViewModel @Inject constructor(
    private val prayerRepository: PrayersRepository,
    private val globalPreferences: GlobalPreferences
) : ViewModel() {


    var days = MutableLiveData(30)

    fun controllDays(boolean: Boolean) {
        if (boolean)
            days.value = days.value!! + 1
        else {
            if (days.value!! > 0) {
                days.value = days.value!! - 1
            }
        }
    }


    fun twentyFourConverter(hour: Int, minutes: Int): String {
        return try {
            val sdf = SimpleDateFormat("H:mm a", Locale("ar"))
            val dateObj: Date? = sdf.parse("$hour:$minutes")
            dateObj.toString()
        } catch (e: ParseException) {
            e.printStackTrace()
            return "am"
        }
    }
}