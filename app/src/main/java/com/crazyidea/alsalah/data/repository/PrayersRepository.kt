package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.PrayersLocalDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersRemoteDataSource
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.prayers.Timing
import kotlinx.coroutines.CoroutineScope
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
    ): Pair<Timing, Boolean> {
        val shouldFetch = localDataSource.shouldFetchData(cityName, month.toInt())
        if (shouldFetch)
            withContext(externalScope.coroutineContext) {
                val result = remoteDataSource.getDayPrayers(month, year, lat, lng, method, tune)
                localDataSource.insertData(cityName, result.data!!)
            }
        return Pair(localDataSource.getDayTimings(day, month), shouldFetch)
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