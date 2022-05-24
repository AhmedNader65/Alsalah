package com.crazyidea.alsalah.di

import android.content.Context
import androidx.room.Room
import com.crazyidea.alsalah.data.api.CalendarAPI
import com.crazyidea.alsalah.data.api.PrayersAPI
import com.crazyidea.alsalah.data.dataSource.EventsRemoteDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersLocalDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersRemoteDataSource
import com.crazyidea.alsalah.data.repository.CalendarRepository
import com.crazyidea.alsalah.data.repository.PrayersRepository
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
import javax.inject.Named
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
    fun providePrayersRemoteDataSource(
        prayersAPI: PrayersAPI
    ) =
        PrayersRemoteDataSource(prayersAPI)
  @Provides
    @Singleton
    fun provideEventsRemoteDataSource(
        calendarAPI: CalendarAPI,@Named("wiki_url") BASE_URL: String
    ) =
      EventsRemoteDataSource(calendarAPI,BASE_URL)

    @Provides
    @Singleton
    fun providePrayersLocalDataSource(coroutineScope: CoroutineScope,appDatabase: AppDatabase) =
        PrayersLocalDataSource(appDatabase,coroutineScope)

    @Provides
    @Singleton
    fun provideExternalScope() =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    @Singleton
    fun providePrayersRepository(
        coroutineScope: CoroutineScope,
        remoteDataSource: PrayersRemoteDataSource,
        localDataSource: PrayersLocalDataSource
    ) =
        PrayersRepository(remoteDataSource, localDataSource, coroutineScope)

    @Provides
    @Singleton
    fun provideCalendarRepository(
        coroutineScope: CoroutineScope,
        remoteDataSource: EventsRemoteDataSource,
    ) =
        CalendarRepository(remoteDataSource, coroutineScope)
}