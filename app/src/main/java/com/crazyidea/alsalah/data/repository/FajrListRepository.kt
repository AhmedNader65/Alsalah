package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface FajrListRepository : BaseRepository {
    suspend fun getFajrList(): List<Fajr>
    suspend fun insert(
        list: List<Fajr>
    )
}