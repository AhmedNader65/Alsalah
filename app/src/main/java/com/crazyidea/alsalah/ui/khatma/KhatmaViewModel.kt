package com.crazyidea.alsalah.ui.khatma

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crazyidea.alsalah.data.repository.PrayersRepository
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
        var amPm = ""
        var hours = hour
        var minutess = minutes
        if (hour > 12) {
            hours = hour - 12
            if (globalPreferences.locale.equals("ar"))
                amPm = "ص"
            else
                amPm = "AM"
        } else {

            if (globalPreferences.locale.equals("ar"))
                amPm = "م"
            else
                amPm = "PM"
        }
        var myString = ""
        if (globalPreferences.locale.equals("ar"))
            myString = " $minutess : $hours  $amPm "
        else
            myString = " $hours : $minutess  $amPm "
        return myString
    }
}