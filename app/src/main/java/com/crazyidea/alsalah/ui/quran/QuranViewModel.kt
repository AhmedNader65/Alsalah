package com.crazyidea.alsalah.ui.quran

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.QuranRepository
import com.crazyidea.alsalah.data.room.entity.Ayat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val quranRepository: QuranRepository
) : ViewModel() {
    private val _pageContent = MutableLiveData<List<Ayat>>()
    val pageContent: LiveData<List<Ayat>> = _pageContent
    fun getQuran() {
        viewModelScope.launch {
            Timber.e("Getting quran")
            _pageContent.postValue(quranRepository.getPage(1))
        }
    }

    init {
//        Timber.e("Getting quran")
//        viewModelScope.launch {
//            Timber.e("Getting quran")
//            quranRepository.downloadQuran()
//        }
    }

}