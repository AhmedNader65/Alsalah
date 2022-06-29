package com.crazyidea.alsalah.ui.khatma

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.crazyidea.alsalah.data.prayers.PrayersRepository
import com.crazyidea.alsalah.data.repository.KhatmaRepository
import com.crazyidea.alsalah.data.room.entity.Khatma
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class KhatmaViewModel @Inject constructor(
    private val repository: KhatmaRepository,
    private val globalPreferences: GlobalPreferences
) : ViewModel() {


    var khatma = MutableLiveData(Khatma(null, null, null))
    var days = MutableLiveData(30)
    var result = MutableLiveData(20)
    var type = MutableLiveData(0)

    fun controllDays(boolean: Boolean) {
        if (boolean) {
            days.value = days.value!! + 1
        } else {
            if (days.value!! > 0) {
                days.value = days.value!! - 1
            }
        }
        result.value = 604 / days.value!!
        khatma.value?.pages_num = if (type.value == 0) {
            days.value!!
        } else {
            604 / days.value!!
        }
        khatma.value?.days = if (type.value == 0) {
            604 / days.value!!
        } else {
            days.value!!
        }
        khatma.value = khatma.value
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

    fun saveKhatma() {
        khatma.value?.let { viewModelScope.launch { repository.saveKhatma(it) } }
    }
}
