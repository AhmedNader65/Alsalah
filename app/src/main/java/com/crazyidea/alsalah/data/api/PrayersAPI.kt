package com.crazyidea.alsalah.data.api

import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.ServerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayersAPI {
    @GET("calendar")
    suspend fun getPrayersTimingByAddress(
        @Query("latitude") latitude:String,
        @Query("longitude") longitude:String,
        @Query("month") month:String,
        @Query("year") year:String,
        @Query("method") method:Int,
        @Query("tune") tune:String?
    ): Response<ServerResponse<PrayerResponseApiModel>>

}