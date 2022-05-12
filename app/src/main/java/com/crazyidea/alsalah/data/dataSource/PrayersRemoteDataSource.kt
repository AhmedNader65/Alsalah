package com.crazyidea.alsalah.data.dataSource

import com.crazyidea.alsalah.data.api.PrayersAPI
import com.crazyidea.alsalah.data.getResponse
import com.crazyidea.alsalah.data.getResponseNoServerResource
import com.crazyidea.alsalah.data.model.AzkarResponseApiModel
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.Resource
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class PrayersRemoteDataSource @Inject constructor(
    private val prayersAPI: PrayersAPI,
) {

    suspend fun getDayPrayers(
        month: String,
        year: String,
        lat: String,
        lng: String,
        method: Int,
        tune: String?
    ): Resource<List<PrayerResponseApiModel>> {
        return getResponse(
            request = {
                prayersAPI.getPrayersTimingByAddress(lat, lng, month, year, method, tune)
            },
            defaultErrorMessage = "Error getting prayers try again"
        )
    }

    suspend fun getAzkar(
    ): Resource<AzkarResponseApiModel> {
        return getResponseNoServerResource(
            request = {
                prayersAPI.getAzkar("https://raw.githubusercontent.com/nawafalqari/azkar-api/main/api/azkar.json")
            },
            defaultErrorMessage = "Error getting azkar try again"
        )
    }
}