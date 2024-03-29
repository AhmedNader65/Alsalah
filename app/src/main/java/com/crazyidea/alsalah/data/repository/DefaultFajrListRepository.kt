package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultFajrListRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val externalScope: CoroutineScope
) : FajrListRepository {

    override suspend fun getFajrList(): List<Fajr> {
        return withContext(externalScope.coroutineContext) {
            return@withContext appDatabase.fajrDao().getList()
        }
    }

    override suspend fun insert(
        list: List<Fajr>
    ) {
        withContext(externalScope.coroutineContext) {
            appDatabase.fajrDao().empty()
            appDatabase.fajrDao().insertAll(*list.toTypedArray())
        }
    }
}