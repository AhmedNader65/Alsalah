package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.PrayersLocalDataSource
import com.crazyidea.alsalah.data.dataSource.PrayersRemoteDataSource
import javax.inject.Inject

class PrayersRepository @Inject constructor(
    remoteDataSource: PrayersRemoteDataSource,
    localDataSource: PrayersLocalDataSource
) {
}