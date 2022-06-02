package com.crazyidea.alsalah.data.room.dao

import androidx.room.*
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr
import com.crazyidea.alsalah.data.room.entity.prayers.Date
import com.crazyidea.alsalah.data.room.entity.prayers.DateWithTiming
import com.crazyidea.alsalah.data.room.entity.prayers.Meta
import com.crazyidea.alsalah.data.room.entity.prayers.Timing

@Dao
interface FajrDao {
    @Transaction
    @Query("SELECT * FROM Fajr")
    fun getList(): List<Fajr>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fajr: List<Fajr>): Long
}