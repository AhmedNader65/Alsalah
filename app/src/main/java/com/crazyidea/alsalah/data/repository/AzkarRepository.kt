package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.AzkarLocalDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersRemoteDataSource
import com.crazyidea.alsalah.data.room.entity.azkar.AzkarProgress
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class AzkarRepository @Inject constructor(
    private val remoteDataSource: PrayersRemoteDataSource,
    private val localDataSource: AzkarLocalDataSource,
    private val externalScope: CoroutineScope
) {


    suspend fun insertProgress(date: String, category: String) {
        val progress = getProgress(date)
        progress.let {
            alterProgress(it, category)
            localDataSource.insertProgress(alterProgress(it, category))
        }
    }

    suspend fun getTotalProgress(date: String): Int {
        val progress = getProgress(date)
        return calculateProgress(progress)
    }

    private fun calculateProgress(progress: AzkarProgress): Int {
        val value = ((progress.morning + progress.evening + progress.sleeping + progress.more + progress.sebha + progress.prayer)/6.0)*100
        return  value.toInt()
    }

    private fun alterProgress(progress: AzkarProgress, category: String): AzkarProgress {
        when (category) {
            "أذكار الصباح" -> progress.morning = 1
            "أذكار المساء" -> progress.evening = 1
            "أذكار النوم" -> progress.sleeping = 1
            "اخرى" -> progress.more = 1
            "تسابيح" -> progress.sebha = 1
            "أذكار بعد السلام من الصلاة المفروضة" -> progress.prayer = 1
        }
        return progress
    }

    private suspend fun getProgress(date: String): AzkarProgress {
        return localDataSource.getProgress(date) ?: AzkarProgress(date, 0, 0, 0, 0, 0, 0)
    }

}