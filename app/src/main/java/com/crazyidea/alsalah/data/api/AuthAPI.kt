package com.crazyidea.alsalah.data.api

import com.crazyidea.alsalah.data.model.ServerResponse
import com.crazyidea.alsalah.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface AuthAPI {
    @FormUrlEncoded
    @POST
    suspend fun login(
        @Field ("uid") uid: String,
        @Field ("name") name: String,
        @Url url: String,
    ): Response<ServerResponse<User>>
}