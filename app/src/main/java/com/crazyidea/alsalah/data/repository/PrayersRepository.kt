package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.PrayersLocalDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersRemoteDataSource
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.prayers.Timing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PrayersRepository @Inject constructor(
    private val remoteDataSource: PrayersRemoteDataSource,
    private val localDataSource: PrayersLocalDataSource,
    private val externalScope: CoroutineScope
) {

    suspend fun getPrayersData(
        cityName: String,
        day: Int,
        month: String,
        year: String,
        lat: String,
        lng: String,
        method: Int,
        school: Int,
        tune: String?,
        save: Boolean = true
    ): Pair<Timing, Boolean>? {
        val job = withContext(externalScope.coroutineContext) {
            var data: Pair<Timing, Boolean>? = null
            if (method == getLocaleMethod() && school == getSchool())
                data = getFromLocale(day, month, false)
            if (data != null) {
                return@withContext data
            }
            fetchPrayers(cityName, day, month, year, lat, lng, method, school,tune, save)
            data = getFromLocale(day, month, true)
            if (data != null) {
                return@withContext data
            } else {
                null
            }

        }
        return job
    }

    suspend fun getPrayersDataNoSaving(
        cityName: String,
        day: Int,
        month: String,
        year: String,
        lat: String,
        lng: String,
        method: Int,
        school: Int,
        tune: String?,
        save: Boolean = false
    ): List<PrayerResponseApiModel>? {
        val job = withContext(externalScope.coroutineContext) {
            return@withContext fetchAndReturnPrayers(
                cityName,
                day,
                month,
                year,
                lat,
                lng,
                method,
                school,
                tune,
                save
            )

        }
        return job
    }

    private fun getFromLocale(
        day: Int,
        month: String,
        shouldFetch: Boolean
    ): Pair<Timing, Boolean>? {
        localDataSource.getDayTimings(day, month)?.let {
            return Pair(it, shouldFetch)
        }
        return null
    }

    private fun getLocaleMethod(): Int {
        return localDataSource.getMethod()
    }

    private fun getSchool(): Int {
        return localDataSource.getSchool()
    }

    private suspend fun fetchPrayers(
        cityName: String,
        day: Int,
        month: String,
        year: String,
        lat: String,
        lng: String,
        method: Int,
        school: Int,
        tune: String?,
        save: Boolean = true
    ) {
        val result = remoteDataSource.getDayPrayers(month, year, lat, lng, method, school, tune)
        if (save)
            localDataSource.insertData(cityName, result.data!!)

    }

    private suspend fun fetchAndReturnPrayers(
        cityName: String,
        day: Int,
        month: String,
        year: String,
        lat: String,
        lng: String,
        method: Int,
        school: Int,
        tune: String?,
        save: Boolean = false
    ): List<PrayerResponseApiModel>? {
        val result = remoteDataSource.getDayPrayers(month, year, lat, lng, method, school, tune)
        return result.data

    }

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