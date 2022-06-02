package com.crazyidea.alsalah.data.dataSource

import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.azkar.AzkarProgress
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FajrListDataSource @Inject constructor(
    private val appDatabase: AppDatabase,
    private val externalScope: CoroutineScope
) {
    suspend fun insertContacts(fajrList: List<Fajr>) {
        withContext(externalScope.coroutineContext) {
            appDatabase.fajrDao().insert(fajrList)
        }
    }

    suspend fun getList(): List<Fajr>? {
        var list: List<Fajr>?
        withContext(externalScope.coroutineContext) {
            list = appDatabase.fajrDao().getList()
        }
        return list
    }
}