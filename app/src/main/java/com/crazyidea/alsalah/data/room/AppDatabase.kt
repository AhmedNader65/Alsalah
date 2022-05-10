package com.crazyidea.alsalah.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.crazyidea.alsalah.data.room.dao.PrayerDao
import com.crazyidea.alsalah.data.room.entity.Date
import com.crazyidea.alsalah.data.room.entity.Meta
import com.crazyidea.alsalah.data.room.entity.Timing

@Database(entities = [Date::class, Meta::class,Timing::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prayersDao(): PrayerDao
}