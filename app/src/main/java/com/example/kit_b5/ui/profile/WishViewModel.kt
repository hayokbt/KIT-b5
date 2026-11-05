package com.example.kit_b5.ui.profile

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_b5.data.DataStore.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WishViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WishRepository(application)
    var wish by mutableStateOf(Wish())
        private set
    var loaded by mutableStateOf(false)
        private set
    var saving by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set
    init { load() }
    fun load() {
        viewModelScope.launch {
            try { wish = repository.wish.first(); loaded = true; message = null }
            catch (_: java.io.IOException) { message = "読み込めませんでした。再試行してください。" }
        }
    }
    fun update(value: Wish) { wish = value; message = null }
    fun save(onSaved: () -> Unit) {
        if (!loaded || saving) return
        saving = true
        viewModelScope.launch {
            try { repository.save(wish); onSaved() }
            catch (_: java.io.IOException) { message = "保存できませんでした。もう一度お試しください。" }
            finally { saving = false }
        }
    }
}
