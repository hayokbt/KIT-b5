package com.example.kit_b5.ui.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.kit_b5.data.quiz.QuizCatalog

data class QuizProgress(
    val index: Int = 0,
    val answers: List<Int> = emptyList(),
    val finished: Boolean = false
)

class QuizViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    val questions = QuizCatalog.questions
    var progress by mutableStateOf(QuizProgress(
        index = savedState["quiz_index"] ?: 0,
        answers = savedState.get<IntArray>("quiz_answers")?.toList() ?: emptyList(),
        finished = savedState["quiz_finished"] ?: false
    ))
        private set

    val question get() = questions[progress.index]
    val selectedAnswer get() = progress.answers.getOrNull(progress.index)
    val correctCount get() = progress.answers.withIndex().count {
        it.value == questions[it.index].correctIndex
    }
    val accuracy get() = if (progress.answers.isEmpty()) null
        else correctCount * 100 / progress.answers.size

    fun answer(choice: Int) {
        if (progress.finished || selectedAnswer != null || choice !in question.choices.indices) return
        update(progress.copy(answers = progress.answers + choice))
    }

    fun next() {
        if (progress.finished || selectedAnswer == null) return
        update(if (progress.index == questions.lastIndex) progress.copy(finished = true)
            else progress.copy(index = progress.index + 1))
    }

    fun restart() { update(QuizProgress()) }

    private fun update(value: QuizProgress) {
        progress = value
        savedState["quiz_index"] = value.index
        savedState["quiz_answers"] = value.answers.toIntArray()
        savedState["quiz_finished"] = value.finished
    }
}
