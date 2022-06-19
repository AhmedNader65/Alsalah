package com.crazyidea.alsalah.data.dataSource

import com.crazyidea.alsalah.data.model.AzkarResponseApiModel
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.azkar.AzkarProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AzkarLocalDataSource @Inject constructor(
    private val appDatabase: AppDatabase,
    private val externalScope: CoroutineScope
) {

    suspend fun shouldFetchAzkar(): Boolean {
        return withContext(externalScope.coroutineContext) {
            appDatabase.azkarDao().shouldFetchData() == 0
        }
    }

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


    fun insertAzkar(result: AzkarResponseApiModel) {
        appDatabase.azkarDao().deleteAzkar()
        result.morning_azkar.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.evening_azkar.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.afterPrayer_azkar.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.prophets_duaa.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.quran_duaa.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.sleeping_azkar.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.tasabeh.forEach {
            appDatabase.azkarDao().insertData(it)
        }
        result.wakeup_azkar.forEach {
            appDatabase.azkarDao().insertData(it)
        }
    }
}