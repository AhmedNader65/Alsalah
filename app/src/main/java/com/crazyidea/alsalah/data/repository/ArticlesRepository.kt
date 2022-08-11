package com.crazyidea.alsalah.data.repository

import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.model.Comment
import com.crazyidea.alsalah.data.model.ServerResponse
import kotlinx.coroutines.flow.Flow

interface ArticlesRepository : BaseRepository {
    suspend fun fetchArticle(): Flow<ServerResponse<ArrayList<Articles>>?>
    suspend fun fetchRecentArticle(): Flow<ServerResponse<ArrayList<Articles>>?>


    suspend fun fetchComments(id: Int): Flow<ServerResponse<ArrayList<Comment>>?>

    suspend fun postArticleComment(id: Int, comment: String): Flow<ServerResponse<Comment>?>

    suspend fun postShareArticle(id: Int): Flow<ServerResponse<String>?>

    suspend fun postArticleLike(id: Int): Flow<ServerResponse<String>?>


}