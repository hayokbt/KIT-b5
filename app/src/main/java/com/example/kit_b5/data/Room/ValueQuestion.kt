package com.example.kit_b5.data.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "value_questions")
data class ValueQuestion(
    @PrimaryKey val id: Int,
    val question: String,
    val optionA: String,
    val optionB: String,
    val valueCodeA: String,
    val valueCodeB: String,
    val answerCode: String? = null,
    val answeredAt: Long? = null
)
