package com.example.kit_b5

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kit_b5.data.Room.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ValueQuestionStorageTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private fun open(name: String) = Room.databaseBuilder(context, MemoryPhotoDatabase::class.java, name)
        .addCallback(MemoryPhotoDatabase.CALLBACK)
        .addMigrations(MemoryPhotoDatabase.MIGRATION_1_2).build()

    @Test fun initialRowsContainAll200QuestionsAndFiveCodePairs() = runBlocking {
        val name = "values-${UUID.randomUUID()}.db"
        val db = open(name)
        try {
            val rows = db.valueQuestionDao().observeQuestions().first()
            assertEquals((1..200).toList(), rows.map { it.id })
            val pairs = listOf("F" to "S", "K" to "M", "W" to "D", "Y" to "T", "L" to "P")
            rows.forEachIndexed { index, row ->
                assertEquals(pairs[index / 40], row.valueCodeA to row.valueCodeB)
                assertNull(row.answerCode)
                assertNull(row.answeredAt)
                assertTrue(row.question.isNotBlank())
                assertTrue(row.optionA.isNotBlank() && row.optionB.isNotBlank())
            }
        } finally { db.close(); context.deleteDatabase(name) }
    }

    @Test fun answersUpdateExistingRowsAndSurviveDatabaseReopen() = runBlocking {
        val name = "answers-${UUID.randomUUID()}.db"
        var db = open(name)
        try {
            val dao = db.valueQuestionDao()
            val rows = dao.observeQuestions().first()
            rows.forEach { assertEquals(1, dao.answer(it.id, it.valueCodeA, 100)) }
            assertEquals(1, dao.answer(1, "S", 200))
            assertEquals(0, dao.answer(1, "K", 300))
            assertEquals(0, dao.answer(201, "F", 300))
            db.close()
            db = open(name)
            val restored = db.valueQuestionDao().observeQuestions().first()
            assertEquals(200, restored.size)
            assertEquals("S", restored.first().answerCode)
            assertEquals(200L, restored.first().answeredAt)
            assertEquals(199, restored.drop(1).count { it.answerCode == it.valueCodeA })
        } finally { db.close(); context.deleteDatabase(name) }
    }

    @Test fun migrationFromPhotoDatabasePreservesPhotosAndSeedsUnansweredQuestions() = runBlocking {
        val name = "migration-${UUID.randomUUID()}.db"
        val file = context.getDatabasePath(name)
        file.parentFile!!.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { legacy ->
            legacy.execSQL("CREATE TABLE memory_photos (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, image BLOB NOT NULL, timestamp INTEGER NOT NULL)")
            legacy.execSQL("INSERT INTO memory_photos (id, image, timestamp) VALUES (1, X'010203', 123)")
            legacy.version = 1
        }
        val db = open(name)
        try {
            val rows = db.valueQuestionDao().observeQuestions().first()
            assertEquals(200, rows.size)
            assertTrue(rows.all { it.answerCode == null })
            val photo = requireNotNull(db.photoDao().find(1))
            assertArrayEquals(byteArrayOf(1, 2, 3), photo.image)
            assertEquals(123L, photo.timestamp)
        } finally { db.close(); context.deleteDatabase(name) }
    }
}
