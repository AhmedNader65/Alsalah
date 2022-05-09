package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.PrayersLocalDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersRemoteDataSource
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.Resource
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
        date: String,
        address: String,
        method: Int,
        tune: String?
    ): Flow<Resource<PrayerResponseApiModel>?> {
        return withContext(externalScope.coroutineContext) {
            flow {
                emit(Resource.loading())
                val result = remoteDataSource.getDayPrayers(date, address, method, tune)
                emit(result)
            }.flowOn(Dispatchers.IO)
        }

    }

}