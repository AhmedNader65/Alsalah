package com.crazyidea.alsalah.ui.azkar.azkar_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.PrayersRepository
import com.crazyidea.alsalah.data.room.entity.azkar.Azkar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class AzkarMenuModel @Inject constructor(
    private val prayerRepository: PrayersRepository,
) : ViewModel() {
    private val _allAzkar = MutableLiveData<List<Azkar>>()
    val allAzkar: LiveData<List<Azkar>> = _allAzkar
    suspend fun getAzkar(category: String) {
        withContext(viewModelScope.coroutineContext) {
            _allAzkar.value = prayerRepository.getAzkarByCategory(category)
        }
    }

}
