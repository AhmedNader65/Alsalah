package com.crazyidea.alsalah.data.repository

import androidx.lifecycle.LiveData
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.BookmarkWithAya
import com.crazyidea.alsalah.data.room.entity.Khatma
import com.crazyidea.alsalah.utils.GlobalPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class KhatmaRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val globalPreferences: GlobalPreferences,
    private val externalScope: CoroutineScope
) {
    val khatmas: LiveData<List<Khatma>> = appDatabase.khatmaDao().getAll()

    suspend fun saveKhatma(khatma: Khatma) {
        return withContext(externalScope.coroutineContext) {
           appDatabase.khatmaDao().insertKhatma(khatma)
        }
    }

}