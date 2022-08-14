package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.App
import com.crazyidea.alsalah.BuildConfig
import com.crazyidea.alsalah.DataStoreCollector
import com.crazyidea.alsalah.data.api.Network
import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.model.Comment
import com.crazyidea.alsalah.data.model.ServerResponse
import com.crazyidea.alsalah.utils.GlobalPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class DefaultArticlesRepository @Inject constructor(
    private val globalPreferences: GlobalPreferences
) : ArticlesRepository {


    private val infoDataMutex = Mutex()

    private var articleData: ServerResponse<ArrayList<Articles>>? = null
    private var articleComments: ServerResponse<ArrayList<Comment>>? = null
    private var commentData: ServerResponse<Comment>? = null
    private var likeData: ServerResponse<String>? = null
    private var shareData: ServerResponse<String>? = null

    override suspend fun fetchArticle(): Flow<ServerResponse<ArrayList<Articles>>?> {
        return flow {
            val result = Network.articles.getArticles(
                language = DataStoreCollector.articlesLanguage,
                id = DataStoreCollector.userId
            )
            result.let {
                infoDataMutex.withLock {
                    articleData = it
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun fetchRecentArticle(): Flow<ServerResponse<ArrayList<Articles>>?> {
        return flow {
            val result = Network.articles.getRecentArticles(
                language = DataStoreCollector.articlesLanguage,
                id =  DataStoreCollector.userId,
            )
            result.let {
                infoDataMutex.withLock {
                    articleData = it
                }
            }

            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    override suspend fun fetchComments(id: Int): Flow<ServerResponse<ArrayList<Comment>>?> {
        return flow {
            val result = Network.articles.getArticleComments(
                language = DataStoreCollector.articlesLanguage,
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

    override suspend fun postArticleComment(
        id: Int,
        comment: String
    ): Flow<ServerResponse<Comment>?> {
        return flow {
            val result = Network.articles.postArticleComment(
                language = DataStoreCollector.articlesLanguage,
                userID =  DataStoreCollector.userId,
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


    override suspend fun postShareArticle(id: Int): Flow<ServerResponse<String>?> {
        return flow {
            val result = Network.articles.postShare(
                App.instance.getAppLocale().language,
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

    override suspend fun postArticleLike(id: Int): Flow<ServerResponse<String>?> {
        return flow {
            val result = Network.articles.postArticleLike(

                App.instance.getAppLocale().language,
                article_id = id,
                userID =  DataStoreCollector.userId,
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