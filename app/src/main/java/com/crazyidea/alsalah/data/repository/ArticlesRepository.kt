package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.BuildConfig
import com.crazyidea.alsalah.data.api.Network
import com.crazyidea.alsalah.data.model.*
import com.crazyidea.alsalah.utils.GlobalPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class ArticlesRepository @Inject constructor(
    private val globalPreferences: GlobalPreferences
) {


    private val infoDataMutex = Mutex()

    private var articleData: ServerResponse<ArrayList<Articles>>? = null
    private var articleComments: ServerResponse<ArrayList<Comment>>? = null
    private var commentData: ServerResponse<Comment>? = null
    private var likeData: ServerResponse<String>? = null
    private var shareData: ServerResponse<String>? = null

    suspend fun fetcharticle(): Flow<ServerResponse<ArrayList<Articles>>?> {
        return flow {
            val result = Network.articles.getArticles(
                language = globalPreferences.getLocale(),
                id = globalPreferences.getUserId()
            )
            result.let {
                infoDataMutex.withLock {
                    articleData = it
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchRecentArticle(): Flow<ServerResponse<ArrayList<Articles>>?> {
        return flow {
            val result = Network.articles.getRecentArticles(
                language = globalPreferences.getLocale(),
                id = globalPreferences.getUserId(),
            )
            result.let {
                infoDataMutex.withLock {
                    articleData = it
                }
            }

            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun fetchComments(id: Int): Flow<ServerResponse<ArrayList<Comment>>?> {
        return flow {
            val result = Network.articles.getArticleComments(
                language = globalPreferences.getLocale(),
                url = BuildConfig.BASE_URL + "article/${id}/comment"
            )
            result.let {
                infoDataMutex.withLock {
                    articleComments = it
                }
            }

            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postArticleComment(id: Int, comment: String): Flow<ServerResponse<Comment>?> {
        return flow {
            val result = Network.articles.postArticleComment(
                globalPreferences.getLocale(),
                userID = globalPreferences.getUserId(),
                article_id = id,
                comment = comment
            )
            result.let {
                infoDataMutex.withLock {
                    commentData = it
                }
            }

            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun postShareArticle(id: Int): Flow<ServerResponse<String>?> {
        return flow {
            val result = Network.articles.postShare(
                globalPreferences.getLocale(),
                url = BuildConfig.BASE_URL + "share/${id}/article"
            )
            result.let {
                infoDataMutex.withLock {
                    shareData = it
                }
            }

            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postArticleLike(id: Int): Flow<ServerResponse<String>?> {
        return flow {
            val result = Network.articles.postArticleLike(

                language = globalPreferences.getLocale(),
                article_id = id,
                userID = globalPreferences.getUserId(),
            )
            result.let {
                infoDataMutex.withLock {
                    likeData = it
                }
            }

            emit(result)
        }.flowOn(Dispatchers.IO)
    }


}