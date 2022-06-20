package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FajrListRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val externalScope: CoroutineScope
) {

    suspend fun getFajrList(): List<Fajr> {
        return withContext(externalScope.coroutineContext) {
            return@withContext appDatabase.fajrDao().getList()
        }
    }

    suspend fun insert(
        list: List<Fajr>
    ) {
        withContext(externalScope.coroutineContext) {
            appDatabase.fajrDao().empty()
            appDatabase.fajrDao().insertAll(*list.toTypedArray())
        }
    }
}