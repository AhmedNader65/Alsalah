package com.crazyidea.alsalah.data.room.dao

import androidx.room.*
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.azkar.AzkarProgress
import com.crazyidea.alsalah.data.room.entity.prayers.Date
import com.crazyidea.alsalah.data.room.entity.prayers.DateWithTiming
import com.crazyidea.alsalah.data.room.entity.prayers.Meta
import com.crazyidea.alsalah.data.room.entity.prayers.Timing

@Dao
interface AzkarDao {

    @Transaction
    @Query("SELECT * FROM Azkar where category = :category")
    fun getAzkarByCategory(category: String): List<Azkar>

    @Transaction
    @Query("SELECT * FROM Azkar WHERE category NOT IN (:categories)")
    fun getOtherAzkar( categories:List<String>): List<Azkar>

    @Transaction
    @Query("SELECT * FROM Azkar where category = :category LIMIT 1")
    fun getFirstAzkarByCategory(category: String): Azkar


    @Transaction
    @Query("SELECT * FROM Azkar LIMIT 1")
    fun getFirstAzkarGeneral(): Azkar

    @Insert
    fun insertData(vararg azkar: Azkar)

    @Query("DELETE FROM Azkar")
    fun deleteAzkar()

    @Query("Select COUNT(*) FROM Azkar")
    fun shouldFetchData(): Int

}