package com.crazyidea.alsalah.data.api

import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.ServerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayersAPI {
    @GET("timingsByAddress/{date}")
    suspend fun getPrayersTimingByAddress(
        @Path("date") date:String,
        @Query("address") address:String,
        @Query("method") method:Int,
        @Query("tune") tune:String?
    ): Response<ServerResponse<PrayerResponseApiModel>>

}