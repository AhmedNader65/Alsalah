package com.crazyidea.alsalah.ui.blogDetail

import android.os.Build
import android.text.Html
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.repository.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BlogDetailViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {

    val views = MutableLiveData("")
    val description = MutableLiveData("")
    val article = MutableLiveData<Articles>()




}