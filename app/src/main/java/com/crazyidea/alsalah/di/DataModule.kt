package com.crazyidea.alsalah.di

import android.content.Context
import androidx.room.Room
import com.crazyidea.alsalah.data.DataStoreManager
import com.crazyidea.alsalah.data.repository.DefaultPrayersRepository
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
    ): ArticlesRepository =
        DefaultArticlesRepository(globalPreferences)

    @Provides
    @Singleton
    fun provideExternalScope() =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun providePrayersRepository(
        coroutineScope: CoroutineScope,
        appDatabase: AppDatabase,
    ): PrayersRepository =
        DefaultPrayersRepository(appDatabase, coroutineScope)

    @Provides
    @Singleton
    fun provideQuranRepository(
        coroutineScope: CoroutineScope,
        globalPreferences: GlobalPreferences,
        appDatabase: AppDatabase,
    ): QuranRepository =
        DefaultQuranRepository(appDatabase, globalPreferences, coroutineScope)

    @Provides
    @Singleton
    fun provideKhatmaRepository(
        coroutineScope: CoroutineScope,
        globalPreferences: GlobalPreferences,
        appDatabase: AppDatabase,
    ): KhatmaRepository =
        DefaultKhatmaRepository(appDatabase, globalPreferences, coroutineScope)

    @Provides
    @Singleton
    fun provideAzkarRepository(
        appDatabase: AppDatabase,
        globalPreferences: GlobalPreferences,
        coroutineScope: CoroutineScope
    ): AzkarRepository =
        DefaultAzkarRepository(appDatabase, globalPreferences, coroutineScope)

    @Provides
    @Singleton
    fun provideUserRepository(
        coroutineScope: CoroutineScope,
    ): UserRepository =
        DefaultUserRepository(coroutineScope)

    @Provides
    @Singleton
    fun provideCalendarRepository(
        coroutineScope: CoroutineScope,
    ): CalendarRepository =
        DefaultCalendarRepository(coroutineScope)

    @Provides
    @Singleton
    fun provideAppSettingsRepository(
        coroutineScope: CoroutineScope,
        dataStoreManager: DataStoreManager,
    ): SettingsRepository =
        AppSettingsRepository(coroutineScope,dataStoreManager)

    @Provides
    @Singleton
    fun provideAzanSettingsRepository(
        coroutineScope: CoroutineScope,
        dataStoreManager: DataStoreManager,
    ): AzanSettingsRepository =
        AzanSettingsRepository(coroutineScope,dataStoreManager)

    @Provides
    @Singleton
    fun providePrayerSettingsRepository(
        coroutineScope: CoroutineScope,
        dataStoreManager: DataStoreManager,
    ): PrayerSettingsRepository =
        PrayerSettingsRepository(coroutineScope,dataStoreManager)

    @Provides
    @Singleton
    fun provideFajrListRepository(
        coroutineScope: CoroutineScope,
        appDatabase: AppDatabase,
    ): FajrListRepository =
        DefaultFajrListRepository(appDatabase, coroutineScope)

    @Provides
    @Singleton
    fun provideDataStoreManager(
        @ApplicationContext context: Context
    ): DataStoreManager =
        DataStoreManager(context)

}