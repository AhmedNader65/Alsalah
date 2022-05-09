package com.crazyidea.alsalah.data

import com.crazyidea.alsalah.data.model.Resource
import com.crazyidea.alsalah.data.model.ServerResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response



suspend fun <T> getResponse(
    request: suspend () -> Response<ServerResponse<T>>,
    defaultErrorMessage: String
): Resource<T> {
    return try {
        val result = request.invoke()
        if (result.isSuccessful) {
            val response: ServerResponse<T>? = result.body()
            return Resource.success(response?.data,response?.status)
        } else {
            val gson = Gson()
            val type = object : TypeToken<ServerResponse<String>?>() {}.type
            val errorResponse: ServerResponse<String> =
                gson.fromJson(result.errorBody()!!.charStream(), type)
            Resource.error(errorResponse.status, null)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        Resource.error(defaultErrorMessage, null)
    }
}
