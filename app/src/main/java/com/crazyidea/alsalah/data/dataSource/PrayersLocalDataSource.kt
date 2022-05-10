package com.crazyidea.alsalah.data.dataSource

import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.Date
import com.crazyidea.alsalah.data.room.entity.Meta
import com.crazyidea.alsalah.data.room.entity.Timing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PrayersLocalDataSource @Inject constructor(
    private val appDatabase: AppDatabase,
    private val externalScope: CoroutineScope
) {
    suspend fun insertData(cityName: String, remoteResponse: List<PrayerResponseApiModel>) {
        appDatabase.prayersDao().deleteDates()
        appDatabase.prayersDao().deleteMeta()
        appDatabase.prayersDao().deleteTimings()
        withContext(externalScope.coroutineContext) {
            remoteResponse.forEach {
                val metaId = appDatabase.prayersDao().insertMeta(
                    Meta(null, it.meta.method.id, cityName, it.date.gregorian.month.number)
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
            }
        }
    }

    suspend fun shouldFetchData(city: String, month: Int): Boolean {
        return withContext(externalScope.coroutineContext) {
            appDatabase.prayersDao().shouldFetchData(city, month) == 0
        }
    }

    suspend fun getDayTimings(day: Int, month: String): Timing {
        return withContext(externalScope.coroutineContext) {
            val dateWithTiming = appDatabase.prayersDao().getTodayTimings(String.format("%02d", day) ,month)
            dateWithTiming.timing
        }
    }
}