package com.crazyidea.alsalah.data.room.dao

import androidx.room.*
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.prayers.Date
import com.crazyidea.alsalah.data.room.entity.prayers.DateWithTiming
import com.crazyidea.alsalah.data.room.entity.prayers.Meta
import com.crazyidea.alsalah.data.room.entity.prayers.Timing

@Dao
interface AzkarDao {
    @Transaction
    @Query("SELECT * FROM Azkar where category = :category")
    fun getAzkarByCategory(category: String): Azkar

    @Insert
    fun insertData(azkar: Azkar): Long

    @Query("DELETE FROM Azkar")
    fun deleteAzkar()

    @Query("Select COUNT(*) FROM Azkar")
    fun shouldFetchData(): Int
}