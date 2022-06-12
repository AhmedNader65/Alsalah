package com.crazyidea.alsalah.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(val userRepository: UserRepository) : ViewModel() {
    var loginJob : Job? = null

    fun login(uid : String, name:String){
        // check login job if not null cancel it
        loginJob?.cancel()
        loginJob =  viewModelScope.launch {
            userRepository.login(uid,name)
        }
    }


}