package com.crazyidea.alsalah.data.room.dao

import androidx.room.*
import com.crazyidea.alsalah.data.model.PrayerTimingApiModel
import com.crazyidea.alsalah.data.room.entity.Date
import com.crazyidea.alsalah.data.room.entity.DateWithTiming
import com.crazyidea.alsalah.data.room.entity.Meta
import com.crazyidea.alsalah.data.room.entity.Timing

@Dao
interface PrayerDao {
    @Transaction
    @Query("SELECT * FROM Date where readable = :date")
    fun getTodayTimings(date: String): DateWithTiming


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