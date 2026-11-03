package com.example.kit_b5.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MainScreen(viewModel: SavedInformationViewModel, repository: com.example.kit_b5.data.DataStore.OnboardingRepository) {
    var editingReminder by rememberSaveable { mutableStateOf(false) }
    val editor: com.example.kit_b5.ui.onboarding.ReminderViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        key = "homeReminderEditor",
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
                com.example.kit_b5.ui.onboarding.ReminderViewModel(repository) as T
        }
    )
    if (editingReminder) {
        LaunchedEffect(Unit) { editor.loadForEditing() }
        BackHandler { if (!editor.saving) editingReminder = false }
        com.example.kit_b5.ui.onboarding.ReminderScreen(
            editor, editing = true,
            onSaved = { editingReminder = false },
            onCancel = { editingReminder = false }
        )
        return
    }
    var selected by rememberSaveable { mutableStateOf(AppTab.Home) }
    val stateHolder = rememberSaveableStateHolder()
    BackHandler(enabled = selected != AppTab.Home) { selected = AppTab.Home }
    Column(Modifier.fillMaxSize().background(Color(0xFFF7F7FC)).safeDrawingPadding()) {
        Box(Modifier.weight(1f)) {
            stateHolder.SaveableStateProvider(selected.name) {
                when (selected) {
                    AppTab.Home -> HomeScreen(viewModel, onEditReminder = { editingReminder = true }, onNavigate = { selected = it })
                    AppTab.Tips -> TipsScreen()
                    AppTab.Guide -> GuideScreen()
                    AppTab.Quiz -> QuizScreen()
                    AppTab.Profile -> ProfileScreen()
                }
            }
        }
        AppBottomBar(selected) { selected = it }
    }
}
