package com.crazyidea.alsalah.ui.quran

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.AppSettingsRepository
import com.crazyidea.alsalah.data.repository.KhatmaRepository
import com.crazyidea.alsalah.data.repository.QuranRepository
import com.crazyidea.alsalah.data.room.entity.Khatma
import com.crazyidea.alsalah.data.room.entity.Surah
import com.crazyidea.alsalah.ui.setting.BaseSettingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedQuranViewModel @Inject constructor(
    private val quranRepository: QuranRepository,
    private val khatmaRepository: KhatmaRepository,
    private val appSettingsRepository: AppSettingsRepository
) : BaseSettingViewModel(appSettingsRepository) {
    private val _allSurahs = MutableLiveData<List<Surah>>()
    private val _downloaded = MutableLiveData(false)
    private val _currentPage = MutableLiveData(1)
    private val _sidePage = MutableLiveData(1)
    val khatma = MutableLiveData<Khatma>()
    val openDrawer = MutableLiveData(false)
    val allSurahs: LiveData<List<Surah>> = _allSurahs
    val downloaded: LiveData<Boolean> = _downloaded
    val currentPage: LiveData<Int> = _currentPage
    val sidePage: LiveData<Int> = _sidePage
    val bookmarks = quranRepository.bookmarks

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

    fun setSideDrawerPage(page: Int) {
        _sidePage.value = page
    }

    fun getJuzPage(toInt: Int) {
        viewModelScope.launch {
            _currentPage.postValue(quranRepository.getJuzPage(toInt))
        }
    }

    fun updateKhatma(id: Long, read: Int) {
        viewModelScope.launch {
            khatmaRepository.updateKhatma(id, read)
        }
    }


}