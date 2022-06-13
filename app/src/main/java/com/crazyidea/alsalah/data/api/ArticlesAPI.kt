package com.crazyidea.alsalah.data.api

import com.crazyidea.alsalah.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ArticlesAPI {

    @GET
    suspend fun getArticles(
        @Header("Accept-Language") language: String,
        @Url url: String,
        @Query("uid") id: Int =1,
    ): Response<ServerResponse<ArrayList<Articles>>>


    @GET
    suspend fun getRecentArticles(
        @Header("Accept-Language") language: String,
        @Url url: String,
        @Query("uid") id: Int =1,
    ): Response<ServerResponse<ArrayList<Articles>>>


    @GET
    suspend fun getArticleComments(
        @Header("Accept-Language") language: String,
        @Url url: String
    ): Response<ServerResponse<ArrayList<Comment>>>

    @POST
    @FormUrlEncoded
    suspend fun postArticleComment(
        @Header("Accept-Language") language: String,
        @Url url: String,
        @Field("uid") userID: Int,
        @Field("article_id") article_id: Int,
        @Field("comment") comment: String,
    ): Response<ServerResponse<Comment>>


    @GET
    suspend fun postShare(
        @Header("Accept-Language") language: String,
        @Url url: String,
    ): Response<ServerResponse<String>>

    @POST
    @FormUrlEncoded
    suspend fun postArticleLike(
        @Header("Accept-Language") language: String,
        @Url url: String,
        @Field("uid") userID: Int,
        @Field("article_id") article_id: Int,
    ): Response<ServerResponse<String>>

}