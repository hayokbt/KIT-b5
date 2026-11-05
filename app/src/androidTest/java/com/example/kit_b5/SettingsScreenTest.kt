package com.example.kit_b5

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.kit_b5.data.DataStore.*
import com.example.kit_b5.ui.MainScreen
import com.example.kit_b5.ui.home.SavedInformationViewModel
import com.example.kit_b5.ui.settings.SettingsScreen
import com.example.kit_b5.ui.settings.SettingsViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TemporaryFolder
import java.util.concurrent.atomic.AtomicBoolean

class SettingsScreenTest {
    @get:Rule val compose = createComposeRule()
    @get:Rule val folder = TemporaryFolder()
    private lateinit var scope: CoroutineScope
    private lateinit var repository: OnboardingRepository

    @Before fun setup() = runBlocking {
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        repository = OnboardingRepository(PreferenceDataStoreFactory.create(scope = scope,
            produceFile = { java.io.File(folder.root, "settings.preferences_pb") }))
        repository.saveProfile(Profile("初期の名前", 1990, "女性"))
        repository.complete(ReminderSettings(true, "毎週", 2, 1, 20, 30))
    }

    @After fun cleanup() { scope.cancel() }

    @Test fun saveRemainsVisibleWhileScrollingAndPersistsChanges() {
        val model = SettingsViewModel(repository)
        val closed = AtomicBoolean(false)
        model.load()
        compose.setContent { SettingsScreen(model, onClose = { closed.set(true) }) }
        compose.waitUntil(10000) { compose.onAllNodesWithTag("settings_nickname").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("settings_nickname").assertTextContains("初期の名前")
        compose.onNodeWithTag("settings_nickname").performTextReplacement("更新した名前")
        compose.onNodeWithTag("settings_scroll").performTouchInput { swipeUp() }
        compose.onNodeWithTag("settings_save").assertIsDisplayed().assertIsEnabled().performClick()
        compose.waitUntil(10000) { closed.get() }
        runBlocking {
            assertEquals(Profile("更新した名前", 1990, "女性"), repository.profile.first())
            assertEquals(ReminderSettings(true, "毎週", 2, 1, 20, 30), repository.reminder.first())
            assertTrue(repository.completed.first())
        }
    }

    @Test fun allThreeAvatarsOpenSameSettingsAndClosingDiscardsDraft() {
        val savedModel = SavedInformationViewModel(repository)
        compose.setContent { MaterialTheme { MainScreen(savedModel, repository) } }
        listOf<String?>(null, "ガイド", "わたし").forEach { tab ->
            if (tab != null) compose.onNodeWithText(tab, useUnmergedTree = true).performClick()
            compose.onNodeWithContentDescription("設定を開く").performClick()
            compose.waitUntil(10000) { compose.onAllNodesWithTag("settings_nickname").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithTag("settings_nickname").assertTextContains("初期の名前")
            compose.onNodeWithTag("settings_nickname").performTextReplacement("保存しない名前")
            compose.onNodeWithContentDescription("設定を閉じる").performClick()
            compose.waitUntil(10000) { compose.onAllNodesWithTag("settings_nickname").fetchSemanticsNodes().isEmpty() }
            runBlocking { assertEquals("初期の名前", repository.profile.first().nickname) }
        }
    }
}
