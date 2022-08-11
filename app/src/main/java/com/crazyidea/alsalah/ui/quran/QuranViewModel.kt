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

    private val _ayahContent = MutableLiveData<String>()
    val ayahContent: LiveData<String> = _ayahContent
    fun getQuran(page: Int) {
        Timber.e("getting page $page")
        viewModelScope.launch {
            _pageContent.postValue(quranRepository.getPage(page))
        }
    }

    fun getAudio(ayah:String,ayahId: Int,page: Int) {
        Timber.e("getting ayah $ayah")
        try {
            viewModelScope.launch {
                _ayahContent.postValue(quranRepository.getAudio(ayah, ayahId, page))
            }
        }catch (e:Exception){

        }
    }
    fun bookmarkAya(ayah:String,ayahId: Int,page: Int) {
        Timber.e("bookmarking ayah $ayah")
        try {
            viewModelScope.launch {
               quranRepository.bookmarkAya(ayah,ayahId,page)
            }
        }catch (e:Exception){

        }
    }
    fun bookmarkPage(page:Long) {
        Timber.e("bookmarking page $page")
        try {
            viewModelScope.launch {
               quranRepository.bookmarkPage(page)
            }
        }catch (e:Exception){

        }
    }

}