package com.crazyidea.alsalah.data.api

import com.crazyidea.alsalah.BuildConfig
import com.crazyidea.alsalah.data.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface PrayersApi {
    @GET("calendar")
    suspend fun getPrayersTimingByAddress(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("method") method: Int,
        @Query("school") school: Int,
        @Query("tune") tune: String?,
        @Query("latitudeAdjustmentMethod") adjustment: Int
    ): PrayersNetworkContainer

}

interface CalendarAPI {
    @GET
    suspend fun getEvents(
        @Url url: String
    ): WikiResponseApiModel
}

interface AzkarAPI {
    @GET
    suspend fun getAzkar(
        @Header("Accept-Language") language: String,
        @Url url: String = BuildConfig.BASE_URL + "azkar"
    ): AzkarResponseApiModel

}
interface QuranAPI {
    @GET
    suspend fun getQuran(
        @Url url: String = BuildConfig.QURAN_BASE_URL + "quran/quran-uthmani"
    ): QuranNetworkContainer

    @GET
    suspend fun getAudio(
        @Url url: String
    ): QuranNetworkContainer

}

interface AuthAPI {
    @FormUrlEncoded
    @POST
    suspend fun login(
        @Field("uid") uid: String,
        @Field("name") name: String,
        @Url url: String = BuildConfig.BASE_URL + "login",
    ): ServerResponse<User>
}


interface ArticlesAPI {
    @GET
    suspend fun getArticles(
        @Header("Accept-Language") language: String,
        @Url url: String = BuildConfig.BASE_URL + "articles",
        @Query("uid") id: Int = 1,
    ): ServerResponse<ArrayList<Articles>>


    @GET
    suspend fun getRecentArticles(
        @Header("Accept-Language") language: String,
        @Url url: String = BuildConfig.BASE_URL + "recent-articles",
        @Query("uid") id: Int = 1,
    ): ServerResponse<ArrayList<Articles>>


    @GET
    suspend fun getArticleComments(
        @Header("Accept-Language") language: String,
        @Url url: String
    ): ServerResponse<ArrayList<Comment>>

    @POST
    @FormUrlEncoded
    suspend fun postArticleComment(
        @Header("Accept-Language") language: String,
        @Url url: String = BuildConfig.BASE_URL + "article-comment",
        @Field("uid") userID: Int,
        @Field("article_id") article_id: Int,
        @Field("comment") comment: String,
    ): ServerResponse<Comment>


    @GET
    suspend fun postShare(
        @Header("Accept-Language") language: String,
        @Url url: String,
    ): ServerResponse<String>

    @POST
    @FormUrlEncoded
    suspend fun postArticleLike(
        @Header("Accept-Language") language: String,
        @Url url: String = BuildConfig.BASE_URL + "article-like",
        @Field("uid") userID: Int,
        @Field("article_id") article_id: Int,
    ): ServerResponse<String>

}

object Network {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.PRAYERS_BASE_URL)
        .client(getClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun getClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
        }
        return httpClient.build()
    }

    val prayers = retrofit.create(PrayersApi::class.java)
    val auth = retrofit.create(AuthAPI::class.java)
    val articles = retrofit.create(ArticlesAPI::class.java)
    val azkar = retrofit.create(AzkarAPI::class.java)
    val calendar = retrofit.create(CalendarAPI::class.java)
    val quran = retrofit.create(QuranAPI::class.java)
}
