package com.crazyidea.alsalah.ui.fajrlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.FajrListRepository
import com.crazyidea.alsalah.data.room.entity.fajr.Fajr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FajrListViewModel @Inject constructor(
    private val repository: FajrListRepository
) : ViewModel() {
    private val _fajrList = MutableLiveData<List<Fajr>>()
    val fajrList: LiveData<List<Fajr>> = _fajrList
    fun saveList(fajrList: MutableList<Fajr>) {
        viewModelScope.launch {
            repository.insert(fajrList)
        }
    }

    fun getList() {
        viewModelScope.launch {
            _fajrList.value = repository.getFajrList()
        }
    }

}