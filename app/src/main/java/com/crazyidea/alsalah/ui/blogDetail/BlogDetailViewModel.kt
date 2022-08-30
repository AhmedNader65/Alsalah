package com.crazyidea.alsalah.ui.blogDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.model.Comment
import com.crazyidea.alsalah.data.repository.ArticlesRepository
import com.crazyidea.alsalah.utils.showError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlogDetailViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {

    val views = MutableLiveData("")
    val description = MutableLiveData("")
    val article = MutableLiveData<Articles>()

    private val _commentData = MutableLiveData<ArrayList<Comment>>()
    val commentData: LiveData<ArrayList<Comment>> = _commentData
    private val _comments = MutableLiveData<Comment>()
    private val _likedComments = MutableLiveData<String>()
    private val _share = MutableLiveData<String>()
    val toastLiveData = MutableLiveData<String?>()
    val comments: LiveData<Comment> = _comments
    val likedComment: LiveData<String> = _likedComments
    val share: LiveData<String> = _share
    private var commentDataJob: Job? = null
    private var postArticleCommentJob: Job? = null
    private var postArticleShare: Job? = null
    private var postArticleLikeJob: Job? = null
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
        toastLiveData.value = throwable.showError(throwable)
    }


    fun getComments(id: Int) {
        commentDataJob?.cancel()
        commentDataJob = viewModelScope.launch(coroutineExceptionHandler) {
            articlesRepository.fetchComments(id)
                .collect {
                    if (it?.data != null)
                        _commentData.value = it.data
                }
        }

    }


     fun postArticleComment(id: Int,comment:String) {
         postArticleCommentJob?.cancel()
         postArticleCommentJob = viewModelScope.launch(coroutineExceptionHandler) {
            articlesRepository.postArticleComment(id,comment)
                .collect {
                    if (it?.data != null)
                        _comments.value = it.data
                }
        }

    }




     fun postArticleLike(id: Int) {
         postArticleLikeJob?.cancel()
         postArticleLikeJob = viewModelScope.launch(coroutineExceptionHandler) {
                 articlesRepository.postArticleLike(id)
                     .collect {
                         if (it?.data != null)
                             _likedComments.value = it.data
                     }
        }

    }


     fun postShareArticle(id: Int) {
         postArticleShare?.cancel()
         postArticleShare = viewModelScope.launch(coroutineExceptionHandler) {
            articlesRepository.postShareArticle(id)
                .collect {
                    if (it?.data != null)
                        _share.value = it.data
                }
        }

    }

    override fun onCleared() {
        commentDataJob?.cancel()
        postArticleCommentJob?.cancel()
        postArticleLikeJob?.cancel()
        postArticleShare?.cancel()
        super.onCleared()
    }
}