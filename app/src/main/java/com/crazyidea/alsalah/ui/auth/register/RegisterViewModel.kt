package com.crazyidea.alsalah.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.model.User
import com.crazyidea.alsalah.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(val userRepository: UserRepository) : ViewModel() {
    var loginJob: Job? = null
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    fun login(uid: String, name: String) {
        // check login job if not null cancel it
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            val user = userRepository.login(uid, name)
            user.data.let {
                _user.value = it
            }
        }
    }


}