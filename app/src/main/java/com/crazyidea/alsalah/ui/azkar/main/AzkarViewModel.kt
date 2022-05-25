package com.crazyidea.alsalah.ui.azkar.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.AzkarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AzkarViewModel @Inject constructor(
    private val azkarRepository: AzkarRepository,
) : ViewModel() {
    var progress = MutableLiveData(0)

    init {
        progress = MutableLiveData(0)
        viewModelScope.launch {
            getProgress()
        }
    }

    private suspend fun getProgress() {
        val date = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        withContext(viewModelScope.coroutineContext) {
            progress.value = azkarRepository.getTotalProgress(date)
            val x = 534
        }
    }


}