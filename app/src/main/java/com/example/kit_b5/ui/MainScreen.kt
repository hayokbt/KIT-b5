package com.example.kit_b5.ui

import com.example.kit_b5.ui.bottombar.AppBottomBar
import com.example.kit_b5.ui.bottombar.AppTab
import com.example.kit_b5.ui.home.HomeScreen
import com.example.kit_b5.ui.home.SavedInformationViewModel
import com.example.kit_b5.ui.guide.GuideScreen
import com.example.kit_b5.ui.profile.ProfileScreen
import com.example.kit_b5.ui.quiz.QuizScreen
import com.example.kit_b5.ui.tips.TipsScreen

import com.example.kit_b5.ui.settings.SettingsViewModel
import com.example.kit_b5.ui.settings.SettingsScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SheetValue
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: SavedInformationViewModel, repository: com.example.kit_b5.data.DataStore.OnboardingRepository) {
    var showSettings by rememberSaveable { mutableStateOf(false) }
    val settingsModel: SettingsViewModel = viewModel(factory = viewModelFactory {
        initializer { SettingsViewModel(repository) }
    })
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
    if (showSettings) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.Hidden || !settingsModel.saving }
        )
        LaunchedEffect(Unit) { settingsModel.load() }
        ModalBottomSheet(
            onDismissRequest = { if (!settingsModel.saving) showSettings = false },
            sheetState = sheetState,
            containerColor = Color(0xFFF7F6FC)
        ) {
            SettingsScreen(settingsModel, onClose = { showSettings = false })
        }
    }
    BackHandler(enabled = selected != AppTab.Home) { selected = AppTab.Home }
    Column(Modifier.fillMaxSize().background(Color(0xFFF7F7FC)).safeDrawingPadding()) {
        Box(Modifier.weight(1f)) {
            stateHolder.SaveableStateProvider(selected.name) {
                when (selected) {
                    AppTab.Home -> HomeScreen(viewModel, onEditReminder = { editingReminder = true }, onNavigate = { selected = it }, onOpenSettings = { showSettings = true })
                    AppTab.Tips -> TipsScreen()
                    AppTab.Guide -> GuideScreen(nickname = viewModel.state.collectAsState().value.profile.nickname, onOpenSettings = { showSettings = true })
                    AppTab.Quiz -> QuizScreen()
                    AppTab.Profile -> ProfileScreen(nickname = viewModel.state.collectAsState().value.profile.nickname, onOpenSettings = { showSettings = true })
                }
            }
        }
        AppBottomBar(selected) { selected = it }
    }
}
