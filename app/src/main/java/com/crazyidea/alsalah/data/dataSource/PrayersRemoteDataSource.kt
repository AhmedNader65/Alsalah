package com.crazyidea.alsalah.data.dataSource

import com.crazyidea.alsalah.data.api.PrayersAPI
import javax.inject.Inject

class PrayersRemoteDataSource @Inject constructor(private val prayersAPI: PrayersAPI) {
}