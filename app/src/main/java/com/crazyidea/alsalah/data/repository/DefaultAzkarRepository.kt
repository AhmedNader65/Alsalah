package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.App
import com.crazyidea.alsalah.data.api.Network
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.azkar.AzkarProgress

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultAzkarRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val externalScope: CoroutineScope
) : AzkarRepository {

    override suspend fun insertProgress(date: String, category: String) {
        var progress = getProgress(date)
        if (progress == null)
            progress = AzkarProgress(date, 0, 0, 0, 0, 0, 0)
        progress = alterProgress(progress, category)
        withContext(externalScope.coroutineContext) {
            appDatabase.azkarProgressDao().insertOrUpdateProgress(progress)
        }
    }

    override suspend fun getTotalProgress(date: String): Int {
        return withContext(externalScope.coroutineContext) {
            val progress = getProgress(date) ?: return@withContext 0
            val prog = calculateProgress(progress)
            return@withContext prog

        }
    }

    fun calculateProgress(progress: AzkarProgress): Int {
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

    override suspend fun getAzkar() {

        return withContext(externalScope.coroutineContext) {
            val azkar = Network.azkar.getAzkar(language = App.instance.getAppLocale().language)
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

    override suspend fun getFirstAzkarByCategory(category: String?): Azkar {
        return withContext(externalScope.coroutineContext) {
            category?.let {
                return@withContext appDatabase.azkarDao().getFirstAzkarByCategory(it)
            }.run {
                return@withContext appDatabase.azkarDao().getFirstAzkarGeneral()
            }
        }
    }

    override suspend fun getAzkarByCategory(category: String): List<Azkar> {
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


    override suspend fun getRandomAzkar(category: String): Azkar {
        return withContext(externalScope.coroutineContext) {
            val azkar = appDatabase.azkarDao().getRandomAzkar(category)
            azkar
        }
    }
}