package com.crazyidea.alsalah.data.dataSource

import com.crazyidea.alsalah.data.api.PrayersAPI
import com.crazyidea.alsalah.data.getResponse
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.Resource
import javax.inject.Inject

class PrayersRemoteDataSource @Inject constructor(private val prayersAPI: PrayersAPI) {

    suspend fun getDayPrayers(
        date: String,
        address: String,
        method: Int,
        tune: String?
    ): Resource<PrayerResponseApiModel> {
        return getResponse(
            request = {
                prayersAPI.getPrayersTimingByAddress(date, address, method, tune)
            },
            defaultErrorMessage = "Error getting prayers try again"
        )
    }
}