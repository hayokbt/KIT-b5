package com.example.kit_b5

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.kit_b5.data.DataStore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class OnboardingRepositoryTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    private fun withRepository(test: suspend (OnboardingRepository) -> Unit) = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val file = java.io.File(temporaryFolder.root, "settings.preferences_pb")
        val repository = OnboardingRepository(PreferenceDataStoreFactory.create(scope = scope, produceFile = { file }))
        try { test(repository) } finally { scope.cancel() }
    }

    @Test fun profileSavesWithoutCompletingOnboarding() = withRepository { repository ->
        assertFalse(repository.completed.first())
        repository.saveProfile(Profile("  あおい  ", 1990, "女性"))
        assertEquals(Profile("あおい", 1990, "女性"), repository.profile.first())
        assertFalse(repository.completed.first())
        repository.saveProfile(Profile("あおい"))
        assertEquals(Profile("あおい"), repository.profile.first())
    }

    @Test fun finishWithReminderPersistsCurrentSettings() = withRepository { repository ->
        val settings = ReminderSettings(false, "毎月", 3, 15, 8, 30)
        repository.complete(settings)
        assertTrue(repository.completed.first())
        assertEquals(settings, repository.reminder.first())
    }

    @Test fun skipDoesNotSaveReminderButCompletesOnboarding() = withRepository { repository ->
        repository.saveProfile(Profile("あおい"))
        repository.complete(null)
        assertTrue(repository.completed.first())
        assertNull(repository.reminder.first())
        assertEquals("あおい", repository.profile.first().nickname)
    }
}
