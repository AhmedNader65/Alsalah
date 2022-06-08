package com.crazyidea.alsalah.data.api

import com.crazyidea.alsalah.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ArticlesAPI {
    @GET
    suspend fun getArticles(
        @Header("Accept-Language") language: String,
        @Url url: String
    ): Response<ServerResponse<ArrayList<Articles>>>

    @GET
    suspend fun getFawaed(
        @Header("Accept-Language") language: String,
        @Url url: String
    ): Response<ServerResponse<ArrayList<Articles>>>

    @GET
    suspend fun getArticleComments(
        @Header("Accept-Language") language: String,
        @Url url: String
    ): Response<ServerResponse<ArrayList<Comment>>>

    @GET
    suspend fun getFawaedComments(
        @Header("Accept-Language") language: String,
        @Url url: String
    ): Response<ServerResponse<ArrayList<Comment>>>

    @POST
    @FormUrlEncoded
    suspend fun postArticleComment(
        @Header("Accept-Language") language: String,
        @Url url: String,
        @Field("customer_id") userID: Int,
        @Field("article_id") article_id: Int,
        @Field("comment") comment: String,
    ): Response<ServerResponse<Comment>>

    @POST
    @FormUrlEncoded
    suspend fun postFwaedComment(
        @Header("Accept-Language") language: String,
        @Url url: String,
        @Field("customer_id") userID: Int,
        @Field("fawaed_id") article_id: Int,
        @Field("comment") comment: String,
    ): Response<ServerResponse<Comment>>

    @POST
    @FormUrlEncoded
    suspend fun postArticleLike(
        @Header("Accept-Language") language: String,
        @Url url: String,
        @Field("customer_id") userID: Int,
        @Field("article_id") article_id: Int,
    ): Response<ServerResponse<Comment>>

    @POST
    @FormUrlEncoded
    suspend fun postFwaedLike(
        @Header("Accept-Language") language: String,
        @Url url: String,
        @Field("customer_id") userID: Int,
        @Field("fawaed_id") article_id: Int,
    ): Response<ServerResponse<Comment>>

}