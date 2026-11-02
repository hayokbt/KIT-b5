package com.example.kit_b5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kit_b5.data.DataStore.*
import com.example.kit_b5.ui.onboarding.*
import com.example.kit_b5.ui.MainScreen
import com.example.kit_b5.ui.SavedInformationViewModel
import com.example.kit_b5.ui.theme.KITB5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = OnboardingRepository(applicationContext.onboardingDataStore)
        setContent {
            KITB5Theme {
                OnboardingHost(repository)
            }
        }
    }
}

@Composable
private fun OnboardingHost(repository: OnboardingRepository) {
    var completed by remember { mutableStateOf<Boolean?>(null) }
    var readError by remember { mutableStateOf(false) }
    var retry by remember { mutableIntStateOf(0) }
    var step by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(repository, retry) {
        readError = false
        try {
            repository.completed.collect { completed = it }
        } catch (_: java.io.IOException) { readError = true }
    }
    when {
        readError -> Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
            Button(onClick = { retry++ }) { Text("設定を読み込めませんでした。再試行") }
        }
        completed == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        completed == true -> {
            val model: SavedInformationViewModel = viewModel(
                factory = onboardingFactory { SavedInformationViewModel(repository) }
            )
            MainScreen(model, repository)
        }
        else -> {
            BackHandler(enabled = step > 0) { step-- }
            when (step) {
                0 -> WelcomeScreen(viewModel()) { step = it }
                1 -> {
                    val model: ProfileViewModel = viewModel(factory = onboardingFactory { ProfileViewModel(repository) })
                    ProfileScreen(model, onNext = { step = 2 }, onBack = { step = 0 })
                }
                else -> {
                    val model: ReminderViewModel = viewModel(factory = onboardingFactory { ReminderViewModel(repository) })
                    ReminderScreen(model)
                }
            }
        }
    }
}

private fun <T : ViewModel> onboardingFactory(create: () -> T): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <V : ViewModel> create(modelClass: Class<V>): V = create() as V
    }

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
