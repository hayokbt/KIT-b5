package com.example.kit_b5.data.Room

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ValueQuestionDao {
    @Query("SELECT * FROM value_questions ORDER BY id")
    fun observeQuestions(): Flow<List<ValueQuestion>>

    @Query("UPDATE value_questions SET answerCode = :code, answeredAt = :timestamp WHERE id = :id AND :code IN (valueCodeA, valueCodeB)")
    suspend fun answer(id: Int, code: String, timestamp: Long): Int
}
