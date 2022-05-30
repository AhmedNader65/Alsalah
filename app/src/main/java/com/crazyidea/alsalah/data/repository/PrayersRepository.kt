package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.PrayersLocalDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersRemoteDataSource
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
        tune: String?
    ): Pair<Timing, Boolean>? {
        val job = withContext(externalScope.coroutineContext) {
            var data = getFromLocale(day, month, false)
            if (data != null) {
                return@withContext data
            }
            fetchPrayers(cityName, day, month, year, lat, lng, method, tune)
            data = getFromLocale(day, month, true)
            if (data != null) {
                return@withContext data
            } else {
                null
            }

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

    private suspend fun fetchPrayers(
        cityName: String,
        day: Int,
        month: String,
        year: String,
        lat: String,
        lng: String,
        method: Int,
        tune: String?
    ) {
        withContext(externalScope.coroutineContext) {
            val result = remoteDataSource.getDayPrayers(month, year, lat, lng, method, tune)
            localDataSource.insertData(cityName, result.data!!)
        }
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