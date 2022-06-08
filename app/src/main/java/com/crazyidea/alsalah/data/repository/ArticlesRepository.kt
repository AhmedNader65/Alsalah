package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.ArticlesRemoteDataSource
import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.model.Resource
import com.crazyidea.alsalah.data.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class ArticlesRepository @Inject constructor(
    private val dataSource: ArticlesRemoteDataSource,
) {


    private val infoDataMutex = Mutex()

    private var articleData: Resource<ArrayList<Articles>>? = null

    suspend fun fetcharticle(): Flow<Resource<ArrayList<Articles>>?> {
        return flow {
            emit(Resource.loading())
            val result = dataSource.getArticles()
            if (result.status == Status.SUCCESS) {
                result.let {
                    infoDataMutex.withLock {
                        articleData = it
                    }
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun fetchFwaed(): Flow<Resource<ArrayList<Articles>>?> {
        return flow {
            emit(Resource.loading())
            val result = dataSource.getFawaed()
            if (result.status == Status.SUCCESS) {
                result.let {
                    infoDataMutex.withLock {
                        articleData = it
                    }
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


}