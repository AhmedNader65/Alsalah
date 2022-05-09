package com.crazyidea.alsalah.di

import android.content.Context
import com.crazyidea.alsalah.data.api.PrayersAPI
import com.crazyidea.alsalah.data.dataSource.PrayersLocalDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersRemoteDataSource
import com.crazyidea.alsalah.data.repository.PrayersRepository
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
    fun providePrayersRemoteDataSource(
        prayersAPI: PrayersAPI
    ) =
        PrayersRemoteDataSource(prayersAPI)

    @Provides
    @Singleton
    fun providePrayersLocalDataSource() =
        PrayersLocalDataSource()

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
        PrayersRepository(remoteDataSource, localDataSource,coroutineScope)
}