package com.example.kit_b5.ui

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_b5.data.Room.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ValuesState(
    val loading: Boolean = true,
    val questions: List<ValueQuestion> = emptyList(),
    val error: Boolean = false
)

class ValuesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ValueQuestionRepository(MemoryPhotoDatabase.getInstance(application).valueQuestionDao())
    private val reload = MutableStateFlow(0)
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val state = reload.flatMapLatest {
        repository.questions.map { ValuesState(loading = false, questions = it) }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(ValuesState(loading = false, error = true))
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ValuesState())

    var savingIds by mutableStateOf<Set<Int>>(emptySet())
        private set
    var saveError by mutableStateOf<String?>(null)
        private set
    fun retry() { reload.value++ }
    fun answer(question: ValueQuestion, code: String) {
        if (question.id in savingIds || question.answerCode == code) return
        savingIds = savingIds + question.id
        saveError = null
        viewModelScope.launch {
            try { repository.answer(question.id, code) }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { saveError = "回答を保存できませんでした。もう一度選択してください。" }
            finally { savingIds = savingIds - question.id }
        }
    }
}
