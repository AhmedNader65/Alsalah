package com.crazyidea.alsalah.ui.quran

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.QuranRepository
import com.crazyidea.alsalah.data.room.entity.Ayat
import com.crazyidea.alsalah.data.room.entity.Surah
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val quranRepository: QuranRepository
) : ViewModel() {
    private val _pageContent = MutableLiveData<List<Ayat>>()
    private val _allSurahs = MutableLiveData<List<Surah>>()
    private val _downloaded = MutableLiveData(false)
    private val _currentPage = MutableLiveData(1)
    val openDrawer = MutableLiveData(false)
    val pageContent: LiveData<List<Ayat>> = _pageContent
    val allSurahs: LiveData<List<Surah>> = _allSurahs
    val downloaded: LiveData<Boolean> = _downloaded
    val currentPage: LiveData<Int> = _currentPage
    fun getQuran(page: Int) {
        viewModelScope.launch {
            _pageContent.postValue(quranRepository.getPage(page))
        }
    }

    fun getAllSurah() {
        viewModelScope.launch {
            _allSurahs.postValue(quranRepository.getAllSurah())
        }
    }

    fun downloadQuran() {
        viewModelScope.launch {
            _downloaded.postValue(quranRepository.downloadQuran())
        }
    }

    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }

}