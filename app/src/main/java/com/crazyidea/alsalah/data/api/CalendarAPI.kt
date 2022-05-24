package com.crazyidea.alsalah.data.api

import com.crazyidea.alsalah.data.model.AzkarResponseApiModel
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.ServerResponse
import com.crazyidea.alsalah.data.model.WikiResponseApiModel
import retrofit2.Response
import retrofit2.http.*

interface CalendarAPI {
    @GET
    suspend fun getEvents(
        @Url url:String
    ): Response<WikiResponseApiModel>
}