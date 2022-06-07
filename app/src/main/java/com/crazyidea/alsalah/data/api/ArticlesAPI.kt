package com.crazyidea.alsalah.data.api

import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.model.AzkarResponseApiModel
import com.crazyidea.alsalah.data.model.PrayerResponseApiModel
import com.crazyidea.alsalah.data.model.ServerResponse
import retrofit2.Response
import retrofit2.http.*

interface ArticlesAPI {
    @GET
    suspend fun getArticles(
        @Header("Accept-Language") language: String,
        @Url url:String
    ): Response<ServerResponse<ArrayList<Articles>>>

}