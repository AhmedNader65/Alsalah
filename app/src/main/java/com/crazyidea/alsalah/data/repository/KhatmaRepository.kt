package com.crazyidea.alsalah.data.repository

import androidx.lifecycle.LiveData
import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.Khatma

interface KhatmaRepository : BaseRepository {
    val khatmas: LiveData<List<Khatma>>
    val randomKhatma: LiveData<Khatma>

    suspend fun saveKhatma(khatma: Khatma)
    suspend fun getKhatmaAya(page:Int): Ayat
    suspend fun updateKhatma(khatmaId:Long,read:Int)

}