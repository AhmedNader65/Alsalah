package com.crazyidea.alsalah.data.room.dao

import androidx.room.*
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.azkar.AzkarProgress
import com.crazyidea.alsalah.data.room.entity.prayers.Date
import com.crazyidea.alsalah.data.room.entity.prayers.DateWithTiming
import com.crazyidea.alsalah.data.room.entity.prayers.Meta
import com.crazyidea.alsalah.data.room.entity.prayers.Timing

@Dao
interface AzkarProgressDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(progress: AzkarProgress): Long

    @Update
    fun update(progress: AzkarProgress)

    @Transaction
    @Query("SELECT * FROM AzkarProgress WHERE day = :date")
    fun getAzkarProgressByDay( date:String): AzkarProgress


    @Transaction
    fun insertOrUpdateProgress(progress: AzkarProgress) {
        if (insertIgnore(progress) == -1L) {
            update(progress)
        }
    }
}