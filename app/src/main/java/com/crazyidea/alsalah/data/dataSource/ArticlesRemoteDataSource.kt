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
                    "${baseURL}articles"
                )
            },
            defaultErrorMessage = "Error fetching articles"
        )
    }

    suspend fun getFawaed(): Resource<ArrayList<Articles>> {
        return getResponse(
            request = { articlesAPI.getFawaed(globalPreferences.getLocale(), "${baseURL}fawaed") },
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

    suspend fun getFawaedComment(articleID: Int): Resource<ArrayList<Comment>> {
        return getResponse(
            request = {
                articlesAPI.getFawaedComments(
                    globalPreferences.getLocale(),
                    "${baseURL}fawaed/${articleID}/comment"
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
                    "${baseURL}article-comment", 1, articleID, comment
                )
            },
            defaultErrorMessage = "Error fetching articles"
        )
    }

    suspend fun postFwaedComment(articleID: Int, comment: String): Resource<Comment> {
        return getResponse(
            request = {
                articlesAPI.postFwaedComment(
                    globalPreferences.getLocale(),
                    "${baseURL}fawaed-comment", 1, articleID, comment
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
                    "${baseURL}article-like", 1, articleID
                )
            },
            defaultErrorMessage = "Error fetching articles"
        )
    }

    suspend fun postFwaedLike(articleID: Int): Resource<String> {
        return getResponse(
            request = {
                articlesAPI.postFwaedLike(
                    globalPreferences.getLocale(),
                    "${baseURL}fawaed-like", 1, articleID
                )
            },
            defaultErrorMessage = "Error fetching articles"
        )
    }
}