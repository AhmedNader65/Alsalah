package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.api.Network
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.azkar.AzkarProgress
import com.crazyidea.alsalah.utils.GlobalPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AzkarRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val globalPreferences: GlobalPreferences,
    private val externalScope: CoroutineScope
) : BaseRepository {

    suspend fun insertProgress(date: String, category: String) {
        var progress = getProgress(date)
        if (progress == null)
            progress = AzkarProgress(date, 0, 0, 0, 0, 0, 0)
        progress = alterProgress(progress, category)
        withContext(externalScope.coroutineContext) {
            appDatabase.azkarProgressDao().insertOrUpdateProgress(progress)
        }
    }

    suspend fun getTotalProgress(date: String): Int {
        return withContext(externalScope.coroutineContext) {
            val progress = getProgress(date) ?: return@withContext 0
            val prog = calculateProgress(progress)
            return@withContext prog

        }
    }

    private fun calculateProgress(progress: AzkarProgress): Int {
        val prog =
            (progress.morning + progress.evening + progress.sleeping + progress.more + progress.sebha + progress.prayer)
        val value =
            (prog / 6.0) * 100
        return value.toInt()
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

    private suspend fun getProgress(date: String): AzkarProgress? {
        return withContext(externalScope.coroutineContext) {

            appDatabase.azkarProgressDao().getAzkarProgressByDay(date)
        }
    }

    suspend fun getAzkar() {

        return withContext(externalScope.coroutineContext) {
            val azkar = Network.azkar.getAzkar(language = globalPreferences.getLocale())
            appDatabase.azkarDao().insertData(*azkar.evening_azkar.toTypedArray())
            appDatabase.azkarDao().insertData(*azkar.afterPrayer_azkar.toTypedArray())
            appDatabase.azkarDao().insertData(*azkar.morning_azkar.toTypedArray())
            appDatabase.azkarDao().insertData(*azkar.sleeping_azkar.toTypedArray())
            appDatabase.azkarDao().insertData(*azkar.wakeup_azkar.toTypedArray())
            appDatabase.azkarDao().insertData(*azkar.prophets_duaa.toTypedArray())
            appDatabase.azkarDao().insertData(*azkar.quran_duaa.toTypedArray())
            appDatabase.azkarDao().insertData(*azkar.tasabeh.toTypedArray())
        }
    }

    suspend fun getFirstAzkarByCategory(category: String): Azkar {
        return withContext(externalScope.coroutineContext) {
            val azkar = appDatabase.azkarDao().getFirstAzkarByCategory(category)
            azkar
        }
    }

    suspend fun getAzkarByCategory(category: String): List<Azkar> {
        return withContext(externalScope.coroutineContext) {
            if (category == "اخرى")
                appDatabase.azkarDao().getOtherAzkar(
                    listOf(
                        "أذكار الصباح",
                        "أذكار النوم",
                        "تسابيح",
                        "أذكار بعد السلام من الصلاة المفروضة",
                        "أذكار المساء"
                    )
                )
            else
                appDatabase.azkarDao().getAzkarByCategory(category)
        }
    }

    suspend fun getFirstAzkar(): Azkar {
        return withContext(externalScope.coroutineContext) {
            val azkar = appDatabase.azkarDao().getFirstAzkarGeneral()
            azkar
        }
    }

    suspend fun getRandomAzkar(category: String): Azkar {

        return withContext(externalScope.coroutineContext) {
            val azkar = appDatabase.azkarDao().getRandomAzkar(category)
            azkar
        }
    }
}