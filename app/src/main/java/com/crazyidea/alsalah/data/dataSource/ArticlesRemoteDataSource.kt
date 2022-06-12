package com.crazyidea.alsalah.data.dataSource

import com.crazyidea.alsalah.data.api.ArticlesAPI
import com.crazyidea.alsalah.data.getResponse
import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.model.Comment
import com.crazyidea.alsalah.data.model.Resource
import com.crazyidea.alsalah.utils.GlobalPreferences
import javax.inject.Inject

class ArticlesRemoteDataSource @Inject constructor(
    private val articlesAPI: ArticlesAPI,
    private val globalPreferences: GlobalPreferences, private val baseURL: String

) {

    suspend fun getArticles(): Resource<ArrayList<Articles>> {
        return getResponse(
            request = {
                articlesAPI.getArticles(
                    globalPreferences.getLocale(),
                    "${baseURL}articles", globalPreferences.getUserId()
                )
            },
            defaultErrorMessage = "Error fetching articles"
        )
    }
    suspend fun getRecentArticles(): Resource<ArrayList<Articles>> {
        return getResponse(
            request = {
                articlesAPI.getArticles(
                    globalPreferences.getLocale(),
                    "${baseURL}recent-articles", globalPreferences.getUserId()
                )
            },
            defaultErrorMessage = "Error fetching articles"
        )
    }

    suspend fun getArticleComment(articleID: Int): Resource<ArrayList<Comment>> {
        return getResponse(
            request = {
                articlesAPI.getArticleComments(
                    globalPreferences.getLocale(),
                    "${baseURL}article/${articleID}/comment"
                )
            },
            defaultErrorMessage = "Error fetching articles"
        )
    }


    suspend fun postArticleComment(articleID: Int, comment: String): Resource<Comment> {
        return getResponse(
            request = {
                articlesAPI.postArticleComment(
                    globalPreferences.getLocale(),
                    "${baseURL}article-comment",globalPreferences.getUserId(), articleID, comment
                )
            },
            defaultErrorMessage = "Error fetching articles"
        )
    }

    suspend fun postArticleLike(articleID: Int): Resource<String> {
        return getResponse(
            request = {
                articlesAPI.postArticleLike(
                    globalPreferences.getLocale(),
                    "${baseURL}article-like",globalPreferences.getUserId(), articleID
                )
            },
            defaultErrorMessage = "Error fetching articles"
        )
    }

}