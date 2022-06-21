package com.crazyidea.alsalah.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.crazyidea.alsalah.data.room.dao.*
import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.Surah
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import com.crazyidea.alsalah.data.room.entity.azkar.AzkarProgress
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr
import com.crazyidea.alsalah.data.room.entity.prayers.Date
import com.crazyidea.alsalah.data.room.entity.prayers.Meta
import com.crazyidea.alsalah.data.room.entity.prayers.Timing

@Database(
    entities = [Surah::class, Ayat::class, Date::class, Meta::class, Timing::class, Fajr::class, Azkar::class, AzkarProgress::class],
    version = 10
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prayersDao(): PrayerDao
    abstract fun azkarProgressDao(): AzkarProgressDao
    abstract fun azkarDao(): AzkarDao
    abstract fun fajrDao(): FajrDao
    abstract fun quranDao(): QuranDao
}