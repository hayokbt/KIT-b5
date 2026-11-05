package com.example.kit_b5.ui.settings

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_b5.data.DataStore.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: OnboardingRepository) : ViewModel() {
    var profile by mutableStateOf(Profile())
        private set
    var reminder by mutableStateOf(ReminderSettings(enabled = false))
        private set
    var loaded by mutableStateOf(false)
        private set
    var saving by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    private var loadJob: Job? = null

    fun load() {
        if (saving) return
        loadJob?.cancel()
        loaded = false
        error = null
        loadJob = viewModelScope.launch {
            try {
                val saved = repository.settings.first()
                profile = saved.profile
                reminder = saved.reminder ?: ReminderSettings(enabled = false)
                loaded = true
            } catch (_: java.io.IOException) {
                error = "設定を読み込めませんでした。再試行してください。"
            }
        }
    }

    fun updateProfile(value: Profile) { if (!saving) profile = value }
    fun updateReminder(value: ReminderSettings) { if (!saving) reminder = value }

    fun save(onSaved: () -> Unit) {
        if (!loaded || saving || profile.nickname.isBlank()) return
        saving = true
        error = null
        viewModelScope.launch {
            try {
                repository.saveSettings(profile, reminder)
                onSaved()
            } catch (_: java.io.IOException) {
                error = "保存できませんでした。もう一度お試しください。"
            } finally { saving = false }
        }
    }
}
