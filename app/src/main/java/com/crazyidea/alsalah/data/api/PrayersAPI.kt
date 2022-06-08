package com.crazyidea.alsalah.data.api

import com.crazyidea.alsalah.data.model.AzkarResponseApiModel
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.ServerResponse
import retrofit2.Response
import retrofit2.http.*

interface PrayersAPI {
    @GET("calendar")
    suspend fun getPrayersTimingByAddress(
        @Query("latitude") latitude:String,
        @Query("longitude") longitude:String,
        @Query("month") month:String,
        @Query("year") year:String,
        @Query("method") method:Int,
        @Query("school") school:Int,
        @Query("tune") tune:String?
    ): Response<ServerResponse<List<PrayerResponseApiModel>>>

    @GET
    suspend fun getAzkar(
        @Url url:String
    ): Response<AzkarResponseApiModel>

}