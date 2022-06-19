package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.FajrListDataSource
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FajrListRepository @Inject constructor(
    private val dataSource: FajrListDataSource,
    private val externalScope: CoroutineScope
) {

    suspend fun getFajrList(): List<Fajr>? {
        return withContext(externalScope.coroutineContext) {
            return@withContext dataSource.getList()
        }
    }
    suspend fun insert(
        list:List<Fajr>
    ) {
        dataSource.insertContacts(list)
    }
}