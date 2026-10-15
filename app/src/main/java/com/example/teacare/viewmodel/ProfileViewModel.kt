package com.example.teacare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacare.data.local.preferences.UserPreferences
import com.example.teacare.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Người làm vườn",
    val gardenName: String = "Đồi chè Tân Cương",
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val apiBaseUrl: String = RetrofitClient.DEFAULT_EMULATOR_BASE_URL,
    val isEditProfileDialogOpen: Boolean = false,
    val isEditUrlDialogOpen: Boolean = false
)

class ProfileViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.userName.collect { name ->
                _uiState.value = _uiState.value.copy(userName = name)
            }
        }
        viewModelScope.launch {
            userPreferences.gardenName.collect { garden ->
                _uiState.value = _uiState.value.copy(gardenName = garden)
            }
        }
        viewModelScope.launch {
            userPreferences.isDarkMode.collect { dark ->
                _uiState.value = _uiState.value.copy(isDarkMode = dark)
            }
        }
        viewModelScope.launch {
            userPreferences.notificationsEnabled.collect { notif ->
                _uiState.value = _uiState.value.copy(notificationsEnabled = notif)
            }
        }
        viewModelScope.launch {
            userPreferences.apiBaseUrl.collect { url ->
                _uiState.value = _uiState.value.copy(apiBaseUrl = url)
            }
        }
    }

    fun openEditProfileDialog() {
        _uiState.value = _uiState.value.copy(isEditProfileDialogOpen = true)
    }

    fun dismissEditProfileDialog() {
        _uiState.value = _uiState.value.copy(isEditProfileDialogOpen = false)
    }

    fun openEditUrlDialog() {
        _uiState.value = _uiState.value.copy(isEditUrlDialogOpen = true)
    }

    fun dismissEditUrlDialog() {
        _uiState.value = _uiState.value.copy(isEditUrlDialogOpen = false)
    }

    fun updateProfile(name: String, garden: String) {
        viewModelScope.launch {
            userPreferences.setUserName(name.trim().ifBlank { "Người làm vườn" })
            userPreferences.setGardenName(garden.trim().ifBlank { "Đồi chè Tân Cương" })
            dismissEditProfileDialog()
        }
    }

    fun updateApiBaseUrl(url: String) {
        viewModelScope.launch {
            val clean = url.trim()
            if (clean.isNotBlank()) {
                userPreferences.setApiBaseUrl(clean)
                dismissEditUrlDialog()
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(enabled)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)
        }
    }
}
