package com.crazyidea.alsalah.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.crazyidea.alsalah.data.room.dao.AzkarDao
import com.crazyidea.alsalah.data.room.dao.PrayerDao
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.prayers.Date
import com.crazyidea.alsalah.data.room.entity.prayers.Meta
import com.crazyidea.alsalah.data.room.entity.prayers.Timing

@Database(entities = [Date::class, Meta::class, Timing::class, Azkar::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prayersDao(): PrayerDao
    abstract fun azkarDao(): AzkarDao
}