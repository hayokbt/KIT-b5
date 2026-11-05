package com.example.kit_b5.ui

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_b5.data.Room.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PhotoGalleryState(
    val loading: Boolean = true,
    val photos: List<MemoryPhotoInfo> = emptyList(),
    val error: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MemoryPhotoRepository(application)
    private val refresh = MutableStateFlow(0)
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val gallery = refresh.flatMapLatest {
        repository.photos.map { PhotoGalleryState(loading = false, photos = it) }
            .catch { error ->
                if (error is CancellationException) throw error
                emit(PhotoGalleryState(loading = false, error = true))
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PhotoGalleryState())

    var saving by mutableStateOf(false)
        private set
    var deleting by mutableStateOf<Set<Long>>(emptySet())
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun retry() { refresh.value++ }

    fun add(uri: Uri) {
        if (saving) return
        saving = true
        error = null
        viewModelScope.launch {
            try { repository.add(uri) }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { error = "写真を保存できませんでした。別の写真を選ぶか、もう一度お試しください。" }
            finally { saving = false }
        }
    }

    fun delete(id: Long) {
        if (id in deleting) return
        deleting = deleting + id
        error = null
        viewModelScope.launch {
            try { repository.delete(id) }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { error = "写真を削除できませんでした。もう一度お試しください。" }
            finally { deleting = deleting - id }
        }
    }

    suspend fun thumbnail(id: Long) = repository.thumbnail(id)
}
