package com.example.kit_b5

import androidx.lifecycle.SavedStateHandle
import com.example.kit_b5.data.quiz.*
import com.example.kit_b5.ui.QuizViewModel
import org.junit.Assert.*
import org.junit.Test

class QuizViewModelTest {
    @Test fun bundledCatalogContainsAllThreeLevelsAndValidAnswers() {
        val questions = QuizCatalog.questions
        assertEquals(200, questions.size)
        assertEquals((1..200).toList(), questions.map { it.id })
        assertEquals(70, questions.count { it.level == QuizLevel.Beginner })
        assertEquals(80, questions.count { it.level == QuizLevel.Intermediate })
        assertEquals(50, questions.count { it.level == QuizLevel.Advanced })
        questions.forEach {
            assertTrue(it.prompt.isNotBlank())
            assertTrue(it.explanation.isNotBlank())
            assertEquals(2, it.choices.size)
            assertTrue(it.choices.all(String::isNotBlank))
            assertTrue(it.correctIndex in it.choices.indices)
        }
        assertEquals("参加できる", questions[70].choices[0])
        assertEquals(1, questions.last().correctIndex)
    }

    @Test fun unansweredCannotAdvanceAndAnswerCannotBeChanged() {
        val model = QuizViewModel(SavedStateHandle())
        assertNull(model.accuracy)
        model.next()
        assertEquals(0, model.progress.index)
        model.answer(-1)
        assertNull(model.selectedAnswer)
        model.answer(1) // First question's correct answer is A.
        model.answer(0)
        assertEquals(1, model.selectedAnswer)
        assertEquals(0, model.correctCount)
        assertEquals(0, model.accuracy)
        model.next()
        assertEquals(1, model.progress.index)
        assertNull(model.selectedAnswer)
        model.answer(1) // Second question's correct answer is B.
        assertEquals(1, model.correctCount)
        assertEquals(50, model.accuracy)
    }

    @Test fun difficultyBoundariesCompletionAndRestart() {
        val model = QuizViewModel(SavedStateHandle())
        repeat(200) { index ->
            val expected = when (index) {
                in 0..69 -> QuizLevel.Beginner
                in 70..149 -> QuizLevel.Intermediate
                else -> QuizLevel.Advanced
            }
            assertEquals(expected, model.question.level)
            model.answer(model.question.correctIndex)
            model.next()
        }
        assertTrue(model.progress.finished)
        assertEquals(200, model.correctCount)
        assertEquals(100, model.accuracy)
        model.next()
        model.answer(0)
        assertEquals(200, model.progress.answers.size)
        model.restart()
        assertFalse(model.progress.finished)
        assertEquals(0, model.progress.index)
        assertTrue(model.progress.answers.isEmpty())
        assertNull(model.accuracy)
    }

    @Test fun restoredStateRetainsQuestionAnswerAndScore() {
        val handle = SavedStateHandle()
        val original = QuizViewModel(handle)
        original.answer(0)
        original.next()
        original.answer(0)
        val restored = QuizViewModel(SavedStateHandle(mapOf(
            "quiz_index" to handle.get<Int>("quiz_index"),
            "quiz_answers" to handle.get<IntArray>("quiz_answers"),
            "quiz_finished" to handle.get<Boolean>("quiz_finished")
        )))
        assertEquals(original.progress, restored.progress)
        assertEquals(50, restored.accuracy)
        assertEquals(0, restored.selectedAnswer)
    }
}
