package com.crazyidea.alsalah.ui.quran

import android.widget.Toast
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
import java.lang.Exception
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

}