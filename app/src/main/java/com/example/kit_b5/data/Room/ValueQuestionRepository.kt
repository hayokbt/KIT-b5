package com.example.kit_b5.data.Room

class ValueQuestionRepository(private val dao: ValueQuestionDao) {
    val questions = dao.observeQuestions()
    suspend fun answer(id: Int, code: String) {
        check(dao.answer(id, code, System.currentTimeMillis()) == 1) { "保存する質問が見つかりません" }
    }
}
