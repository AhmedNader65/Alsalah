package com.crazyidea.alsalah.di

import android.content.Context
import androidx.room.Room
import com.crazyidea.alsalah.data.prayers.PrayersRepository
import com.crazyidea.alsalah.data.repository.*
import com.crazyidea.alsalah.data.room.AppDatabase
import com.crazyidea.alsalah.utils.GlobalPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Provides
    @Singleton
    fun globalPreferences(@ApplicationContext context: Context): GlobalPreferences =
        GlobalPreferences(context)

    @Provides
    @Singleton
    fun provideDB(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java, "SalahDB"
        )
            .fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideArticlesRepository(
        globalPreferences: GlobalPreferences
    ) =
        ArticlesRepository(globalPreferences)

    @Provides
    @Singleton
    fun provideExternalScope() =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    @Singleton
    fun providePrayersRepository(
        coroutineScope: CoroutineScope,
        appDatabase: AppDatabase,
    ) =
        PrayersRepository(appDatabase, coroutineScope)

    @Provides
    @Singleton
    fun provideQuranRepository(
        coroutineScope: CoroutineScope,
        globalPreferences: GlobalPreferences,
        appDatabase: AppDatabase,
    ) =
        QuranRepository(appDatabase,globalPreferences, coroutineScope)

    @Provides
    @Singleton
    fun provideAzkarRepository(
        appDatabase: AppDatabase,
        globalPreferences: GlobalPreferences,
        coroutineScope: CoroutineScope
    ) =
        AzkarRepository(appDatabase, globalPreferences, coroutineScope)

    @Provides
    @Singleton
    fun provideUserRepository(
        coroutineScope: CoroutineScope,
    ) =
        UserRepository(coroutineScope)

    @Provides
    @Singleton
    fun provideCalendarRepository(
        coroutineScope: CoroutineScope,
    ) =
        CalendarRepository(coroutineScope)

    @Provides
    @Singleton
    fun provideFajrListRepository(
        coroutineScope: CoroutineScope,
        appDatabase: AppDatabase,
    ) =
        FajrListRepository(appDatabase, coroutineScope)
}