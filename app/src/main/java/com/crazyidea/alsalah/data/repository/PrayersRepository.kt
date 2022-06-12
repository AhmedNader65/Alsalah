package com.crazyidea.alsalah.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crazyidea.alsalah.data.dataSource.PrayersLocalDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersRemoteDataSource
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.prayers.DateWithTiming
import com.crazyidea.alsalah.data.room.entity.prayers.Timing
import kotlinx.coroutines.*
import javax.inject.Inject

class PrayersRepository @Inject constructor(
    private val remoteDataSource: PrayersRemoteDataSource,
    private val localDataSource: PrayersLocalDataSource,
    private val externalScope: CoroutineScope
) {


    /**
     * A list of asteroids that can be shown on the screen.
     */
    private var _prayers: MutableLiveData<DateWithTiming> = MutableLiveData()
    var prayers: LiveData<DateWithTiming> = _prayers
    suspend fun getPrayers(day: Int, month: String) {

        return withContext(externalScope.coroutineContext) {
            val data  = localDataSource.getDayTimings(day, month)
            _prayers.postValue(data.value)
            val x = 5
        }
    }

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     */
    suspend fun refreshPrayers(
        cityName: String,
        day: Int,
        month: String,
        year: String,
        lat: String,
        lng: String,
        method: Int,
        tune: String?,
        save: Boolean = true
    ) {
        withContext(Dispatchers.IO) {
            val prayers =
                remoteDataSource.getDayPrayers(month, year, lat, lng, method, tune)
            prayers.data?.let { localDataSource.insertData(cityName, it) }
        }
    }

//    suspend fun getPrayersData(
//        cityName: String,
//        day: Int,
//        month: String,
//        year: String,
//        lat: String,
//        lng: String,
//        method: Int,
//        tune: String?,
//        save: Boolean = true
//    ): Pair<Timing, Boolean>? {
//        val job = withContext(externalScope.coroutineContext) {
//            var data = getFromLocale(day, month, false)
//            if (data != null) {
//                return@withContext data
//            }
//            fetchPrayers(cityName, day, month, year, lat, lng, method, tune, save)
//            data = getFromLocale(day, month, true)
//            if (data != null) {
//                return@withContext data
//            } else {
//                null
//            }
//
//        }
//        return job
//    }
//    suspend fun getPrayersDataNoSaving(
//        cityName: String,
//        day: Int,
//        month: String,
//        year: String,
//        lat: String,
//        lng: String,
//        method: Int,
//        tune: String?,
//        save: Boolean = false
//    ): List<PrayerResponseApiModel>? {
//        val job = withContext(externalScope.coroutineContext) {
//            return@withContext fetchAndReturnPrayers(cityName, day, month, year, lat, lng, method, tune, save)
//
//        }
//        return job
//    }
//
//    private fun getFromLocale(
//        day: Int,
//        month: String,
//        shouldFetch: Boolean
//    ): Pair<Timing, Boolean>? {
//        localDataSource.getDayTimings(day, month)?.let {
//            return Pair(it, shouldFetch)
//        }
//        return null
//    }
//
//    private suspend fun fetchPrayers(
//        cityName: String,
//        day: Int,
//        month: String,
//        year: String,
//        lat: String,
//        lng: String,
//        method: Int,
//        tune: String?,
//        save: Boolean = true
//    ) {
//        val result = remoteDataSource.getDayPrayers(month, year, lat, lng, method, tune)
//        if (save)
//            localDataSource.insertData(cityName, result.data!!)
//
//    }
//    private suspend fun fetchAndReturnPrayers(
//        cityName: String,
//        day: Int,
//        month: String,
//        year: String,
//        lat: String,
//        lng: String,
//        method: Int,
//        tune: String?,
//        save: Boolean = false
//    ): List<PrayerResponseApiModel>? {
//        val result = remoteDataSource.getDayPrayers(month, year, lat, lng, method, tune)
//        return result.data
//
//    }

    suspend fun getAzkar() {
        val shouldFetch = localDataSource.shouldFetchAzkar()
        if (shouldFetch)
            withContext(externalScope.coroutineContext) {
                val result = remoteDataSource.getAzkar()
                result.data?.let { localDataSource.insertAzkar(it) }
            }
    }

    suspend fun getFirstAzkarByCategory(category: String): Azkar {
        return localDataSource.getFirstAzkarByCategory(category)
    }

    suspend fun getAzkarByCategory(category: String): List<Azkar> {
        return localDataSource.getAzkarByCategory(category)
    }

    suspend fun getFirstAzkar(): Azkar {
        return localDataSource.getFirstAzkar()
    }


}