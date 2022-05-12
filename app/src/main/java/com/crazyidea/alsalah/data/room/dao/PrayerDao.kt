package com.crazyidea.alsalah.data.room.dao

import androidx.room.*
import com.crazyidea.alsalah.data.room.entity.prayers.Date
import com.crazyidea.alsalah.data.room.entity.prayers.DateWithTiming
import com.crazyidea.alsalah.data.room.entity.prayers.Meta
import com.crazyidea.alsalah.data.room.entity.prayers.Timing

@Dao
interface PrayerDao {
    @Transaction
    @Query("SELECT * FROM Date where g_day = :day AND g_month = :month")
    fun getTodayTimings(day: String,month: String): DateWithTiming


    @Insert
    fun insertDate(date: Date): Long

    @Insert
    fun insertMeta(meta: Meta): Long

    @Insert
    fun insertTiming(timing: Timing): Long

    @Query("DELETE FROM Date")
    fun deleteDates()

    @Query("DELETE FROM Timing")
    fun deleteTimings()

    @Query("DELETE FROM Meta")
    fun deleteMeta()

    @Query("Select COUNT(*) FROM META WHERE city = :city AND month = :month")
    fun shouldFetchData(city: String, month: Int): Int
}