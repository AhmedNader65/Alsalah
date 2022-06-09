package com.crazyidea.alsalah.data.dataSource

import com.crazyidea.alsalah.data.api.AuthAPI
import com.crazyidea.alsalah.data.api.PrayersAPI
import com.crazyidea.alsalah.data.getResponse
import com.crazyidea.alsalah.data.model.Resource
import com.crazyidea.alsalah.data.model.User
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val authAPI: AuthAPI, private val baseURL: String
) {

    suspend fun login(
        uid: String,
        name: String,
    ): Resource<User> {
        return getResponse(
            request = {
                authAPI.login(
                    uid, name,
                    "${baseURL}login"
                )
            },
            defaultErrorMessage = "Error getting prayers try again"
        )
    }

}