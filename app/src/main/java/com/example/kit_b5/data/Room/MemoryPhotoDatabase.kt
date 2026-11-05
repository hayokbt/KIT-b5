package com.example.kit_b5.data.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MemoryPhoto::class, ValueQuestion::class], version = 2, exportSchema = false)
abstract class MemoryPhotoDatabase : RoomDatabase() {
    abstract fun photoDao(): MemoryPhotoDao
    abstract fun valueQuestionDao(): ValueQuestionDao

    companion object {
        private fun seedQuestions(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            val statement = db.compileStatement(
                "INSERT OR IGNORE INTO value_questions (id, question, optionA, optionB, valueCodeA, valueCodeB, answerCode, answeredAt) VALUES (?, ?, ?, ?, ?, ?, NULL, NULL)"
            )
            try {
                ValueQuestionCatalog.questions.forEach { question ->
                    statement.clearBindings()
                    statement.bindLong(1, question.id.toLong())
                    statement.bindString(2, question.question)
                    statement.bindString(3, question.optionA)
                    statement.bindString(4, question.optionB)
                    statement.bindString(5, question.valueCodeA)
                    statement.bindString(6, question.valueCodeB)
                    statement.executeInsert()
                }
            } finally { statement.close() }
        }

        val CALLBACK = object : Callback() {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) { seedQuestions(db) }
        }

        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS value_questions (id INTEGER NOT NULL PRIMARY KEY, question TEXT NOT NULL, optionA TEXT NOT NULL, optionB TEXT NOT NULL, valueCodeA TEXT NOT NULL, valueCodeB TEXT NOT NULL, answerCode TEXT, answeredAt INTEGER)")
                seedQuestions(db)
            }
        }

        @Volatile private var instance: MemoryPhotoDatabase? = null

        fun getInstance(context: Context): MemoryPhotoDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext, MemoryPhotoDatabase::class.java, "memory_photos.db"
                ).addMigrations(MIGRATION_1_2).addCallback(CALLBACK).build().also { instance = it }
            }
    }
}
