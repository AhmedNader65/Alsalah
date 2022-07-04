package com.crazyidea.alsalah.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.AyatBookMark
import com.crazyidea.alsalah.data.room.entity.Khatma
import com.crazyidea.alsalah.data.room.entity.KhatmaUpdate
import com.crazyidea.alsalah.data.room.entity.prayers.Date
import com.crazyidea.alsalah.data.room.entity.prayers.DateWithTiming
import com.crazyidea.alsalah.data.room.entity.prayers.Meta
import com.crazyidea.alsalah.data.room.entity.prayers.Timing

@Dao
interface KhatmaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKhatma(khatma: Khatma): Long


    @Query("DELETE FROM Khatma")
    fun deleteAllKhatmas()

    @Query("Select * FROM Khatma")
    fun getAll(): LiveData<List<Khatma>>

    @Query("Select * FROM Khatma ORDER BY RANDOM() LIMIT 1")
    fun getRandom(): LiveData<Khatma>

    @Query("Select * FROM Khatma WHERE status = 0")
    fun getActive(): List<Khatma>

    @Update(entity = Khatma::class)
    fun updateKhatma(obj: KhatmaUpdate)
}