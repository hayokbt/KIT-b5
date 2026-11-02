package com.example.kit_b5.ui.onboarding

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_b5.data.DataStore.*
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: OnboardingRepository) : ViewModel() {
    var profile by mutableStateOf(Profile())
        private set
    var saving by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    fun update(value: Profile) { profile = value }
    fun next(onSaved: () -> Unit) {
        if (profile.nickname.isBlank() || saving) return
        saving = true
        error = null
        viewModelScope.launch {
            try {
                repository.saveProfile(profile)
                onSaved()
            } catch (e: java.io.IOException) {
                error = "保存できませんでした。もう一度お試しください。"
            } finally { saving = false }
        }
    }
}
