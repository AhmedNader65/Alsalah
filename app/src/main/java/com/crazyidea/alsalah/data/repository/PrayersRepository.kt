package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.PrayersLocalDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersRemoteDataSource
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.Resource
import com.crazyidea.alsalah.data.room.entity.Timing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
    ): Pair<Timing,Boolean> {
        val shouldFetch = localDataSource.shouldFetchData(cityName, month.toInt())
        if (shouldFetch)
            withContext(externalScope.coroutineContext) {
                val result = remoteDataSource.getDayPrayers(month, year, lat, lng, method, tune)
                localDataSource.insertData(cityName, result.data!!)
            }
        return Pair(localDataSource.getDayTimings(day, month),shouldFetch)
    }


}