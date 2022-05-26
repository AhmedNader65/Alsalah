package com.crazyidea.alsalah.data.dataSource

import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.azkar.AzkarProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AzkarLocalDataSource @Inject constructor(
    private val appDatabase: AppDatabase,
    private val externalScope: CoroutineScope
) {
    suspend fun insertProgress(progress: AzkarProgress) {
        withContext(externalScope.coroutineContext) {
            appDatabase.azkarProgressDao().insertOrUpdateProgress(progress)
        }
    }

    suspend fun getProgress(date: String): AzkarProgress? {
        var progress: AzkarProgress? = null
        withContext(externalScope.coroutineContext) {
            progress = appDatabase.azkarProgressDao().getAzkarProgressByDay(date)
        }
        return progress
    }
}