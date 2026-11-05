package com.example.kit_b5.ui.guide

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class GuideViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    val draft = savedState.getStateFlow("guide_draft", "")
    val questions = savedState.getStateFlow("guide_questions", arrayListOf<String>())

    fun updateDraft(value: String) { savedState["guide_draft"] = value }

    fun sendDraft() {
        if (send(draft.value)) updateDraft("")
    }

    fun send(text: String): Boolean {
        val question = text.trim()
        if (question.isEmpty()) return false
        savedState["guide_questions"] = ArrayList(questions.value + question)
        return true
    }

    companion object {
        const val PLACEHOLDER_REPLY = "現在は実装されていません。AIへの相談機能は準備中です。"
    }
}
