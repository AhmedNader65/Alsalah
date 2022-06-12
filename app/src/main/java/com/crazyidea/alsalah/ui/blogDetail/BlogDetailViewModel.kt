package com.crazyidea.alsalah.ui.blogDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.model.Articles
import com.crazyidea.alsalah.data.model.Comment
import com.crazyidea.alsalah.data.repository.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val comments: LiveData<Comment> = _comments
    val likedComment: LiveData<String> = _likedComments
    val share: LiveData<String> = _share
    private var commentDataJob: Job? = null
    private var fawaedcommentDataJob: Job? = null
    private var postArticleCommentJob: Job? = null
    private var postFwaedCommentJob: Job? = null
    private var postFwaedLikeJob: Job? = null
    private var postArticleShare: Job? = null
    private var postArticleLikeJob: Job? = null


     fun getComments(id: Int) {
        commentDataJob?.cancel()
        commentDataJob = viewModelScope.launch {
            articlesRepository.fetchComments(id)
                .collect {
                    if (it?.data != null)
                        _commentData.value = it.data!!
                }
        }

    }


     fun getFwaedComments(id: Int) {
         fawaedcommentDataJob?.cancel()
         fawaedcommentDataJob = viewModelScope.launch {
            articlesRepository.fetchFwaedComments(id)
                .collect {
                    if (it?.data != null)
                        _commentData.value = it.data!!
                }
        }

    }


     fun postArticleComment(id: Int,comment:String) {
         postArticleCommentJob?.cancel()
         postArticleCommentJob = viewModelScope.launch {
            articlesRepository.posArticleComments(id,comment)
                .collect {
                    if (it?.data != null)
                        _comments.value = it.data!!
                }
        }

    }


     fun postFwaedComment(id: Int,comment:String) {
         postFwaedCommentJob?.cancel()
         postFwaedCommentJob = viewModelScope.launch {
            articlesRepository.postFwaedComments(id,comment)
                .collect {
                    if (it?.data != null)
                        _comments.value = it.data!!
                }
        }

    }


     fun postArticleLike(id: Int) {
         postArticleLikeJob?.cancel()
         postArticleLikeJob = viewModelScope.launch {
            articlesRepository.postArticleLike(id)
                .collect {
                    if (it?.data != null)
                        _likedComments.value = it.data!!
                }
        }

    }


     fun postFwaedLike(id: Int) {
         postFwaedLikeJob?.cancel()
         postFwaedLikeJob = viewModelScope.launch {
            articlesRepository.postFwaedLike(id)
                .collect {
                    if (it?.data != null)
                        _likedComments.value = it.data!!
                }
        }

    }


     fun postShareArticle(id: Int) {
         postArticleShare?.cancel()
         postArticleShare = viewModelScope.launch {
            articlesRepository.postShareArticle(id)
                .collect {
                    if (it?.data != null)
                        _share.value = it.data!!
                }
        }

    }

    override fun onCleared() {
        commentDataJob?.cancel()
        fawaedcommentDataJob?.cancel()
        postArticleCommentJob?.cancel()
        postFwaedCommentJob?.cancel()
        postFwaedLikeJob?.cancel()
        postArticleLikeJob?.cancel()
        postArticleShare?.cancel()
        super.onCleared()
    }
}