package com.crazyidea.alsalah.data.dataSource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crazyidea.alsalah.data.model.AzkarResponseApiModel
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.prayers.Date
import com.crazyidea.alsalah.data.room.entity.prayers.DateWithTiming
import com.crazyidea.alsalah.data.room.entity.prayers.Meta
import com.crazyidea.alsalah.data.room.entity.prayers.Timing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class PrayersLocalDataSource @Inject constructor(
    private val appDatabase: AppDatabase,
    private val externalScope: CoroutineScope
) {
    fun insertData(cityName: String, remoteResponse: List<PrayerResponseApiModel>) {
        appDatabase.prayersDao().deleteDates()
        appDatabase.prayersDao().deleteMeta()
        appDatabase.prayersDao().deleteTimings()
            remoteResponse.forEach {
               try {
                   val metaId = appDatabase.prayersDao().insertMeta(
                       Meta(null, it.meta.method.id, cityName, it.date.gregorian.month.number,if(it.meta.school=="STANDARD") 0 else 1)
                   )
                   val timingId = appDatabase.prayersDao().insertTiming(
                       Timing(
                           null,
                           it.timings.Fajr.substring(0, 5),
                           it.timings.Sunrise.substring(0, 5),
                           it.timings.Dhuhr.substring(0, 5),
                           it.timings.Asr.substring(0, 5),
                           it.timings.Sunset.substring(0, 5),
                           it.timings.Maghrib.substring(0, 5),
                           it.timings.Isha.substring(0, 5),
                           it.timings.Imsak.substring(0, 5),
                           it.timings.Midnight.substring(0, 5)
                       )
                   )
                   appDatabase.prayersDao().insertDate(
                       Date(
                           null,
                           timingId,
                           metaId,
                           it.date.readable,
                           it.date.timestamp,
                           it.date.gregorian.date,
                           it.date.gregorian.day,
                           it.date.gregorian.month.number,
                           it.date.gregorian.month.en,
                           it.date.gregorian.year,
                           it.date.hijri.date,
                           it.date.hijri.day,
                           it.date.hijri.weekday.en,
                           it.date.hijri.weekday.ar,
                           it.date.hijri.month.number,
                           it.date.hijri.month.en,
                           it.date.hijri.month.ar,
                           it.date.hijri.year
                       )
                   )
               }catch (e:Exception){}
        }
    }

    suspend fun shouldFetchData(city: String, month: Int): Boolean {
        return withContext(externalScope.coroutineContext) {
            appDatabase.prayersDao().shouldFetchData(city, month) == 0
        }
    }

    suspend fun shouldFetchAzkar(): Boolean {
        return withContext(externalScope.coroutineContext) {
            appDatabase.azkarDao().shouldFetchData() == 0
        }
    }

    fun getDayTimings(day: Int, month: String): LiveData<DateWithTiming> {

            val dayy = String.format(Locale.ENGLISH, "%02d", day)
            return MutableLiveData(appDatabase.prayersDao().getTodayTimings(dayy, month))

    }
    fun getMethod(): Int {
        return appDatabase.prayersDao().getMethod()
    }

    fun getSchool(): Int {
        return appDatabase.prayersDao().getSchool()
    }

    suspend fun getFirstAzkarByCategory(category: String): Azkar {
        return withContext(externalScope.coroutineContext) {
            val azkar = appDatabase.azkarDao().getFirstAzkarByCategory(category)
            azkar
        }
    }

    suspend fun getAzkarByCategory(category: String): List<Azkar> {
        return withContext(externalScope.coroutineContext) {
            if (category == "اخرى")
                appDatabase.azkarDao().getOtherAzkar(
                    listOf(
                        "أذكار الصباح",
                        "أذكار النوم",
                        "تسابيح",
                        "أذكار بعد السلام من الصلاة المفروضة",
                        "أذكار المساء"
                    )
                )
            else
                appDatabase.azkarDao().getAzkarByCategory(category)
        }
    }

    suspend fun getFirstAzkar(): Azkar {
        return withContext(externalScope.coroutineContext) {
            val azkar = appDatabase.azkarDao().getFirstAzkarGeneral()
            azkar
        }
    }


    fun insertAzkar(result: AzkarResponseApiModel) {
        appDatabase.azkarDao().deleteAzkar()
        result.morning_azkar.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.evening_azkar.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.afterPrayer_azkar.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.prophets_duaa.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.quran_duaa.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.sleeping_azkar.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.tasabeh.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.wakeup_azkar.forEach {
            appDatabase.azkarDao().insertData(it)
        }
    }
}