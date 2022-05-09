package com.crazyidea.alsalah.data.api

import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.ServerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface PrayersAPI {
    @GET("timingsByAddress")
    suspend fun getPrayersTimingByAddress(
    ): Response<ServerResponse<PrayerResponseApiModel>>

}