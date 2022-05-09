package com.crazyidea.alsalah.di

import com.crazyidea.alsalah.data.api.PrayersAPI
import com.crazyidea.alsalah.data.dataSource.PrayersLocalDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersRemoteDataSource
import com.crazyidea.alsalah.data.repository.PrayersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DataModule {

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
    fun providePrayersRepository(
        remoteDataSource: PrayersRemoteDataSource,
        localDataSource: PrayersLocalDataSource
    ) =
        PrayersRepository(remoteDataSource, localDataSource)
}