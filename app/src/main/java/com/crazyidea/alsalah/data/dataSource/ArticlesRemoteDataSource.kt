package com.crazyidea.alsalah.data.dataSource

import com.crazyidea.alsalah.data.api.ArticlesAPI
import com.crazyidea.alsalah.data.getResponse
import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.model.Resource
import com.crazyidea.alsalah.utils.GlobalPreferences
import javax.inject.Inject

class ArticlesRemoteDataSource @Inject constructor(
    private val articlesAPI: ArticlesAPI,
    private val globalPreferences: GlobalPreferences
    ,private val baseURL: String

) {

    suspend fun getArticles(): Resource<ArrayList<Articles>> {
        return getResponse(
            request = { articlesAPI.getArticles(globalPreferences.getLocale(),"${baseURL}articles") },
            defaultErrorMessage = "Error fetching articles"
        )
    }
}