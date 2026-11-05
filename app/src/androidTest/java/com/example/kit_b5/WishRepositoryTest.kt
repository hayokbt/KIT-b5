package com.example.kit_b5

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.kit_b5.data.DataStore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class WishRepositoryTest {
    @get:Rule val folder = TemporaryFolder()
    @Test fun allFieldsRoundTripAndCanBeCleared() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val file = java.io.File(folder.root, "wishes.preferences_pb")
        val repository = WishRepository(PreferenceDataStoreFactory.create(scope = scope, produceFile = { file }))
        try {
            assertEquals(Wish(), repository.wish.first())
            val wish = Wish(values = listOf("家族", "趣味"), place = "住み慣れた家",
                people = setOf("友人", "子ども"), treatment = "回復の見込みがあるなら治療したい",
                procedures = mapOf("心肺蘇生" to "わからない", "点滴" to "望む"),
                shared = mapOf("友人" to 1730000000000L), donation = "まだ決めていない")
            repository.save(wish)
            assertEquals(wish, repository.wish.first())
            repository.save(Wish())
            assertEquals(Wish(), repository.wish.first())
        } finally { scope.cancel() }
    }
}
