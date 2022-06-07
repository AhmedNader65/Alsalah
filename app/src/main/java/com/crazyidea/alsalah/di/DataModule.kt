package com.crazyidea.alsalah.di

import android.content.Context
import androidx.room.Room
import com.crazyidea.alsalah.data.api.ArticlesAPI
import com.crazyidea.alsalah.data.api.CalendarAPI
import com.crazyidea.alsalah.data.api.PrayersAPI
import com.crazyidea.alsalah.data.dataSource.*
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
    fun provideArticlesRemoteDataSource(
        articlesAPI: ArticlesAPI,
         globalPreferences: GlobalPreferences, @Named("base_url") BASE_URL: String
    ) =
        ArticlesRemoteDataSource(articlesAPI,globalPreferences,BASE_URL)

    @Provides
    @Singleton
    fun provideArticlesRepository(
        remoteDataSource: ArticlesRemoteDataSource,
    ) =
        ArticlesRepository(remoteDataSource)

    @Provides
    @Singleton
    fun provideEventsRemoteDataSource(
        calendarAPI: CalendarAPI, @Named("wiki_url") BASE_URL: String
    ) =
        EventsRemoteDataSource(calendarAPI, BASE_URL)

    @Provides
    @Singleton
    fun providePrayersLocalDataSource(coroutineScope: CoroutineScope, appDatabase: AppDatabase) =
        PrayersLocalDataSource(appDatabase, coroutineScope)

    @Provides
    @Singleton
    fun provideAzkarLocalDataSource(coroutineScope: CoroutineScope, appDatabase: AppDatabase) =
        AzkarLocalDataSource(appDatabase, coroutineScope)

    @Provides
    @Singleton
    fun provideFajrListDataSource(coroutineScope: CoroutineScope, appDatabase: AppDatabase) =
        FajrListDataSource(appDatabase, coroutineScope)

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
    fun provideAzkarRepository(
        coroutineScope: CoroutineScope,
        remoteDataSource: PrayersRemoteDataSource,
        localDataSource: AzkarLocalDataSource
    ) =
        AzkarRepository(remoteDataSource, localDataSource, coroutineScope)

    @Provides
    @Singleton
    fun provideCalendarRepository(
        coroutineScope: CoroutineScope,
        remoteDataSource: EventsRemoteDataSource,
    ) =
        CalendarRepository(remoteDataSource, coroutineScope)

    @Provides
    @Singleton
    fun provideFajrListRepository(
        coroutineScope: CoroutineScope,
        dataSource: FajrListDataSource,
    ) =
        FajrListRepository(dataSource, coroutineScope)
}