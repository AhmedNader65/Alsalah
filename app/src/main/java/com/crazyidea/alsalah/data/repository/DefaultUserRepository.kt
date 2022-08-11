package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.api.Network
import com.crazyidea.alsalah.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(
    private val externalScope: CoroutineScope
) : UserRepository{

    override suspend fun login(uid:String, name:String):ServerResponse<User> {
        return withContext(externalScope.coroutineContext) {
            return@withContext Network.auth.login(uid,name)
        }
    }

}