package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.dataSource.ArticlesRemoteDataSource
import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.model.Comment
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
    private var articleComments: Resource<ArrayList<Comment>>? = null
    private var commentData: Resource<Comment>? = null

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

    suspend fun fetchComments(id: Int): Flow<Resource<ArrayList<Comment>>?> {
        return flow {
            emit(Resource.loading())
            val result = dataSource.getArticleComment(id)
            if (result.status == Status.SUCCESS) {
                result.let {
                    infoDataMutex.withLock {
                        articleComments = it
                    }
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchFwaedComments(id: Int): Flow<Resource<ArrayList<Comment>>?> {
        return flow {
            emit(Resource.loading())
            val result = dataSource.getFawaedComment(id)
            if (result.status == Status.SUCCESS) {
                result.let {
                    infoDataMutex.withLock {
                        articleComments = it
                    }
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postFwaedComments(id: Int, comment: String): Flow<Resource<Comment>?> {
        return flow {
            emit(Resource.loading())
            val result = dataSource.postFwaedComment(id, comment)
            if (result.status == Status.SUCCESS) {
                result.let {
                    infoDataMutex.withLock {
                        commentData = it
                    }
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun posArticleComments(id: Int, comment: String): Flow<Resource<Comment>?> {
        return flow {
            emit(Resource.loading())
            val result = dataSource.postArticleComment(id, comment)
            if (result.status == Status.SUCCESS) {
                result.let {
                    infoDataMutex.withLock {
                        commentData = it
                    }
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun postFwaedLike(id: Int): Flow<Resource<Comment>?> {
        return flow {
            emit(Resource.loading())
            val result = dataSource.postFwaedLike(id)
            if (result.status == Status.SUCCESS) {
                result.let {
                    infoDataMutex.withLock {
                        commentData = it
                    }
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postArticleLike(id: Int): Flow<Resource<Comment>?> {
        return flow {
            emit(Resource.loading())
            val result = dataSource.postArticleLike(id)
            if (result.status == Status.SUCCESS) {
                result.let {
                    infoDataMutex.withLock {
                        commentData = it
                    }
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


}