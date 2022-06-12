package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.ArticlesRemoteDataSource
import com.crazyidea.alsalah.data.dataSource.UserRemoteDataSource
import com.crazyidea.alsalah.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val dataSource: UserRemoteDataSource,
    private val externalScope: CoroutineScope
) {


    suspend fun login(uid:String, name:String): Resource<User> {
        return withContext(externalScope.coroutineContext) {
            return@withContext dataSource.login(uid, name)
        }
    }

}