package com.example.kit_b5.ui.onboarding

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_b5.data.DataStore.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderViewModel(private val repository: OnboardingRepository) : ViewModel() {
    var nickname by mutableStateOf("")
        private set
    var settings by mutableStateOf(ReminderSettings())
        private set
    var saving by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    init { viewModelScope.launch { nickname = repository.profile.first().nickname } }
    fun update(value: ReminderSettings) { settings = value }
    fun finish(saveReminder: Boolean) {
        if (saving) return
        saving = true
        error = null
        viewModelScope.launch {
            try { repository.complete(if (saveReminder) settings else null) }
            catch (e: java.io.IOException) {
                error = "保存できませんでした。もう一度お試しください。"
            } finally { saving = false }
        }
    }
}
