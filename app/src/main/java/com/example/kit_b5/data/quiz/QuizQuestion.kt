package com.example.kit_b5.data.quiz

enum class QuizLevel(val label: String) {
    Beginner("初級"), Intermediate("中級"), Advanced("上級")
}

data class QuizQuestion(
    val id: Int,
    val level: QuizLevel,
    val prompt: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String
)
