package com.crazyidea.alsalah.data.repository

import androidx.lifecycle.LiveData
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.BookmarkWithAya
import com.crazyidea.alsalah.data.room.entity.Khatma
import com.crazyidea.alsalah.data.room.entity.KhatmaUpdate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultKhatmaRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val externalScope: CoroutineScope
) : KhatmaRepository {
    override val khatmas: LiveData<List<Khatma>> = appDatabase.khatmaDao().getAll()
    override val randomKhatma: LiveData<Khatma> = appDatabase.khatmaDao().getRandom()

    override suspend fun saveKhatma(khatma: Khatma) {
        return withContext(externalScope.coroutineContext) {
           appDatabase.khatmaDao().insertKhatma(khatma)
        }
    }
    override suspend fun getKhatmaAya(page:Int): Ayat {
        return withContext(externalScope.coroutineContext) {
           return@withContext appDatabase.quranDao().getFirstAyaOfPage(page)
        }
    }
    override suspend fun updateKhatma(khatmaId:Long,read:Int) {
        return withContext(externalScope.coroutineContext) {
           appDatabase.khatmaDao().updateKhatma(KhatmaUpdate(khatmaId,read,if(read==604) 1 else 0))
        }
    }

}