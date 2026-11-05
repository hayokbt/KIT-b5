package com.example.kit_b5.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_b5.data.DataStore.*
import kotlinx.coroutines.flow.*

data class SavedInformationState(
    val loading: Boolean = true,
    val profile: Profile = Profile(),
    val reminder: ReminderSettings? = null,
    val error: Boolean = false
)

class SavedInformationViewModel(repository: OnboardingRepository) : ViewModel() {
    val state: StateFlow<SavedInformationState> = combine(repository.profile, repository.reminder) { profile, reminder ->
        SavedInformationState(loading = false, profile = profile, reminder = reminder)
    }.catch { error ->
        if (error is java.io.IOException) emit(SavedInformationState(loading = false, error = true))
        else throw error
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SavedInformationState())
}
