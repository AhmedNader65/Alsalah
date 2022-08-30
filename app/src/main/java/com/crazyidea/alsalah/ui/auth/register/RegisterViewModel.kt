package com.crazyidea.alsalah.ui.auth.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazyidea.alsalah.data.model.User
import com.crazyidea.alsalah.data.repository.AppSettingsRepository
import com.crazyidea.alsalah.data.repository.UserRepository
import com.crazyidea.alsalah.ui.setting.AppSettings
import com.crazyidea.alsalah.ui.setting.BaseSettingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: AppSettingsRepository
) : BaseSettingViewModel(settingsRepository) {
    var loginJob: Job? = null
    private val _user = MutableLiveData<User>()
    fun login(uid: String, name: String) {
        // check login job if not null cancel it
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            val user = userRepository.login(uid, name)
            user.data.let {
                settingsRepository.updateAppSettings(AppSettings.USER_ID, user.data.id)
                settingsRepository.updateAppSettings(AppSettings.LOGGED, true)
            }
        }
    }


}